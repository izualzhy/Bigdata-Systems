package cn.izualzhy;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class WatchSample {

    public static void main(String[] args) {
        WatchSample watchSample = new WatchSample();

        if (args.length == 1) {
            Arrays.stream(args[0].split(","))
                    .map(pair -> pair.split("="))
                    .forEach(pair -> {
                        watchSample.addLabel(pair[0], pair[1]);
                    });
        }

        watchSample.watchSample();
    }

    private static final Logger logger = LoggerFactory.getLogger(WatchSample.class);
    private final Map<String, String> commonLabels = new HashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> resourceCounter = new ConcurrentHashMap<>();
    File resourceDir;


    WatchSample() {
        commonLabels.put("type", "flink-native-kubernetes");

        resourceDir = new File("./data/watch_resources");
        resourceDir.mkdirs();
    }

    void addLabel(String key, String value) {
        commonLabels.put(key, value);
    }

    void watchSample() {
        try {
            KubernetesClient kubernetesClient = KubernetesUtils.initKubernetesClient();
            try (Watch podWatch = kubernetesClient.pods()
                    .withLabels(commonLabels)
                    .watch(new PodWatcher());
                 Watch deploymentWatch = kubernetesClient.apps().deployments()
                         .withLabels(commonLabels)
                         .watch(new DeploymentWatcher())) {

                Thread.sleep(30 * 24 * 60 * 60 * 1000L);
                logger.info("watch created, commonLabels:{} podWatch:{} deploymentWatch:{}.",
                        commonLabels, podWatch, deploymentWatch);
            }

            kubernetesClient.close();
        } catch (Exception e) {
            logger.info("quit watch.", e);
        }
    }

    String saveResourceToFile(String resourceName, String content) {
        AtomicLong counter = resourceCounter.computeIfAbsent(resourceName, k -> new AtomicLong(0));
        long index = counter.incrementAndGet();
        String fileName = String.format("%s.v%d.txt", resourceName, index);

        File file = new File(resourceDir, fileName);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(content);
            logger.info("resource:{} write fileName:{}.", resourceName, fileName);
        } catch (Exception e) {
            logger.warn("resource:{} write fileName:{}.", resourceName, fileName);
        }

        return file.getName();
    }

    class PodWatcher implements Watcher<Pod> {
        @Override
        public boolean reconnecting() {
            logger.info("reconnecting");
            return Watcher.super.reconnecting();
        }

        @Override
        public void eventReceived(Action action, Pod resource) {
            String yamlFile = saveResourceToFile(
                    "pod." + resource.getMetadata().getName(),
                    io.fabric8.kubernetes.client.utils.Serialization.asYaml(resource));
            logger.info("eventReceived: action:{}, kind:{} name:{} detail:{}",
                    action, resource.getKind(), resource.getMetadata().getName(), yamlFile);
        }

        @Override
        public void onClose() {
            logger.info("onClose");
            Watcher.super.onClose();
        }

        @Override
        public void onClose(WatcherException cause) {
            logger.info("onClose cause=", cause);
        }
    }

    class DeploymentWatcher implements Watcher<Deployment> {

        @Override
        public boolean reconnecting() {
            logger.info("reconnecting");
            return Watcher.super.reconnecting();
        }

        @Override
        public void eventReceived(Action action, Deployment resource) {
            String yamlFile = saveResourceToFile(
                    "deployment." + resource.getMetadata().getName(),
                    io.fabric8.kubernetes.client.utils.Serialization.asYaml(resource));
            logger.info("eventReceived: action:{}, kind:{} name:{} detail:{}",
                    action, resource.getKind(), resource.getMetadata().getName(), yamlFile);
        }

        @Override
        public void onClose() {
            logger.info("onClose");
            Watcher.super.onClose();
        }

        @Override
        public void onClose(WatcherException cause) {
            logger.info("onClose cause=", cause);
        }
    }
}
