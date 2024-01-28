package cn.izualzhy;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

public class WatchSample {
    private static final Logger logger = LoggerFactory.getLogger(WatchSample.class);
    private KubernetesClient kubernetesClient = null;
    private final Map<String, String> commonLabels = new HashMap<>();


    public WatchSample() {
        commonLabels.put("type", "flink-native-kubernetes");
    }

    void watchSample() {
        Config config = Config.autoConfigure(null);

        kubernetesClient = new KubernetesClientBuilder().withConfig(config).build();
        try (Watch watch = kubernetesClient.pods()
                .withLabels(commonLabels)
                .watch(new KubernetesWatcherSample())) {
            logger.info("watch:{} created.", watch);

            Thread.sleep(24 * 60 * 60 * 1000L);
        } catch (InterruptedException e) {
            logger.info("exception:", e);
        }
    }

    class KubernetesWatcherSample implements Watcher<Pod> {

        @Override
        public boolean reconnecting() {
            return Watcher.super.reconnecting();
        }

        @Override
        public void eventReceived(Action action, Pod pod) {
            logger.info("receive {} events for pod:{}, status:{}\n\tpod:{}",
                    action,
                    pod.getStatus(),
                    pod.getMetadata().getName(),
                    pod);

            switch (action) {
                case ERROR:
                case MODIFIED:
                    // 仅当 Pod 状态为非 Running 或存在重启时获取日志
                    if (!"Running".equals(pod.getStatus().getPhase()) ||
                            pod.getStatus().getContainerStatuses().stream().anyMatch(cs -> cs.getRestartCount() > 0)) {

                        String log = kubernetesClient.pods()
                                .inNamespace(pod.getMetadata().getNamespace())
                                .withName(pod.getMetadata().getName())
                                .tailingLines(1000) // 仅获取最后 100 行日志
                                .getLog();

                        MDC.put("podName", pod.getMetadata().getName());
                        logger.info("Pod {} \nlog:\n{}", pod.getMetadata().getName(), log);
                        MDC.clear();
                    }
                default:
                    break;
            }

        }

        @Override
        public void onClose() {
            logger.info("watcher:{} is close.", this);
            Watcher.super.onClose();
        }

        @Override
        public void onClose(WatcherException cause) {
            logger.info("watcher:{} is close. cause:", this, cause);
        }
    }

    public static void main(String[] args) {
        WatchSample watchSample = new WatchSample();
        watchSample.watchSample();
    }
}
