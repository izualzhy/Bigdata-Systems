package cn.izualzhy;
//package io.fabric8.kubernetes.examples;
/*
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderCallbacks;
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderElectionConfigBuilder;
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderElector;
import io.fabric8.kubernetes.client.extended.leaderelection.resourcelock.ConfigMapLock;
import io.fabric8.kubernetes.client.extended.leaderelection.resourcelock.LeaseLock;
import io.fabric8.kubernetes.client.extended.leaderelection.resourcelock.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("java:S106")
public class LeaderElectionExamples {

    private static final Logger logger = LoggerFactory.getLogger(LeaderElectionExamples.class);
    private static final String NAMESPACE = "default";
    private static final String NAME = "leaders-of-the-future";

    public static final class SingleThreadExample {
        public static void main(String[] args) throws InterruptedException {
            final String lockIdentity = UUID.randomUUID().toString();
            try (KubernetesClient kc = new KubernetesClientBuilder().build()) {
                LeaderElector leader = kc.leaderElector()
                        .withConfig(
                                new LeaderElectionConfigBuilder()
                                        .withReleaseOnCancel()
                                        .withName("Sample Leader Election configuration")
                                        .withLeaseDuration(Duration.ofSeconds(15L))
                                        .withLock(new LeaseLock(NAMESPACE, NAME, lockIdentity))
                                        .withRenewDeadline(Duration.ofSeconds(10L))
                                        .withRetryPeriod(Duration.ofSeconds(2L))
                                        .withLeaderCallbacks(new LeaderCallbacks(
                                                () -> System.out.println("STARTED LEADERSHIP"),
                                                () -> System.out.println("STOPPED LEADERSHIP"),
                                                newLeader -> System.out.printf("New leader elected %s%n", newLeader)))
                                        .build())
                        .build();
                CompletableFuture<?> f = leader.start();
                Thread.sleep(10000);
                f.cancel(true);
                Thread.sleep(5000);
            }
        }
    }

    public static final class ConcurrentExample {

        private static final int COUNT = 10;
        private static final int THREADS_TO_KILL = 10;
        private static final long WAIT_TO_KILL_TIME = 2500L;
        private static final long TASK_SLEEP = 50000L;
        private static final int TASK_THREADS = 10;

        private final KubernetesClient kubernetesClient;
        private final Function<String, Lock> lockSupplier;
        private final Class<? extends Lock> lockClass;
        private final AtomicReference<String> leaderReference;
        private final Map<String, Future<?>> leaderCandidates;
        private final ScheduledThreadPoolExecutor executorService;

        public ConcurrentExample(KubernetesClient kubernetesClient, Function<String, Lock> lockSupplier) {
            this.kubernetesClient = kubernetesClient;
            this.lockSupplier = lockSupplier;
            this.lockClass = lockSupplier.apply("GET LOCK INFO ONLY").getClass();
            leaderReference = new AtomicReference<>(null);
            leaderCandidates = new ConcurrentHashMap<>();
            executorService = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(TASK_THREADS);
        }

        public static void main(String[] args) throws Exception {
            try (KubernetesClient kc = KubernetesUtils.initKubernetesClient()) {
                final Function<String, Lock> lockSupplier;
                final String lockArgument = args.length > 0 ? args[0] : "";
                switch (lockArgument) {
                    case "lease":
                        lockSupplier = id -> new LeaseLock(NAMESPACE, NAME, id);
                        break;
                    case "configmap":
                    default:
                        lockSupplier = id -> new ConfigMapLock(NAMESPACE, NAME, id);
                }
                new LeaderElectionExamples.ConcurrentExample(kc, lockSupplier).run();
            }
        }

        private void run() throws Exception {
            final Future<?> monitor = monitor();
            spawn();
            final CountDownLatch countDownLatch = new CountDownLatch(THREADS_TO_KILL);
            final Future<?> killLeaders = killLeaders(countDownLatch);
            if (!countDownLatch.await(123456789, TimeUnit.SECONDS)) {
                logger.info("Timeout reached, demo did not complete successfully");
            }
            tearDown(monitor, killLeaders);
        }

        private Future<?> monitor() {
            return executorService.scheduleWithFixedDelay(() -> {
                final String currentLeader = leaderReference.get();
                logger.info("Active:{} \tCurrent leader:{}.",
                        leaderCandidates.size(), Optional.ofNullable(currentLeader).orElse(""));
            }, 0, TASK_SLEEP, TimeUnit.MILLISECONDS);
        }

        private void spawn() {
            logger.info("Spawning {} identical peers with the same leader election algorithm and lock ({}).",
                    COUNT, lockClass.getSimpleName());
            for (int it = 0; it < COUNT; it++) {
                final String id = String.format("Concurrent-%s", it);
                leaderCandidates.put(id, leader(id, lockSupplier).start());
            }
        }

        private Future<?> killLeaders(CountDownLatch leadersToKillCountDown) {
            logger.info("Spawning thread to kill {} leader candidates {} millis after they become leaders.",
                    THREADS_TO_KILL, WAIT_TO_KILL_TIME);
            return executorService.scheduleWithFixedDelay(() -> {
                final String currentLeader = leaderReference.get();
                Optional.ofNullable(currentLeader)
                        .map(leaderCandidates::get)
                        .ifPresent(leader -> {
                            try {
                                Thread.sleep(WAIT_TO_KILL_TIME);
                                leader.cancel(true);
                                leaderCandidates.remove(currentLeader);
                                leadersToKillCountDown.countDown();
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                        });
            }, 0L, TASK_SLEEP, TimeUnit.MILLISECONDS);
        }

        private void tearDown(Future<?>... futures) throws Exception {
            Thread.sleep(TASK_SLEEP + 1);
            logger.info("Demo completed!");
            logger.info("Tearing down rest of the leader candidates ({}) and task threads ({}).",
                    leaderCandidates.size(), futures.length);
            Stream.concat(Stream.of(futures), leaderCandidates.values().stream())
                    .forEach(f -> f.cancel(true));
            leaderCandidates.clear();
            executorService.shutdownNow();
            logger.info("All threads scheduled to cancel, shutting down.");
            kubernetesClient.close();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        }

        private LeaderElector leader(String id, Function<String, Lock> lockSupplier) {
            return kubernetesClient.leaderElector()
                    .withConfig(
                            new LeaderElectionConfigBuilder()
                                    .withName("Concurrent Leader Election configuration")
                                    .withLeaseDuration(Duration.ofMillis(2000L))
                                    .withLock(lockSupplier.apply(id))
                                    .withRenewDeadline(Duration.ofMillis(1500L))
                                    .withRetryPeriod(Duration.ofMillis(300L))
                                    .withLeaderCallbacks(new LeaderCallbacks(
                                            () -> logger.info("{}: I just became leader!!!", id),
                                            () -> {
                                                leaderReference.updateAndGet(s -> id.equals(s) ? null : s);
                                                logger.info("{}: I just lost my leadership :(", id);
                                            },
                                            leaderReference::set))
                                    .build())
                    .build();
        }
    }
}