package cn.izualzhy;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

public class WatchNativeFlinkSample {
    private static final Logger logger = LoggerFactory.getLogger(WatchNativeFlinkSample.class);
    private KubernetesClient kubernetesClient = null;
    private final Map<String, String> commonLabels = new HashMap<>();


    public WatchNativeFlinkSample() {
        logger.info("hello world.");
        commonLabels.put("type", "flink-native-kubernetes");
    }

    void watchSample() {
        kubernetesClient = KubernetesUtils.initKubernetesClient();

        try (Watch podWatch = kubernetesClient.pods()
                .withLabels(commonLabels)
                .watch(new FlinkPodWatcher());
             Watch deploymentWatch = kubernetesClient.apps().deployments()
                .withLabels(commonLabels)
                .watch(new FlinkDeploymentWatcher());
             Watch configMapWatch = kubernetesClient.configMaps()
                .withLabels(commonLabels)
                .watch(new FlinkConfigMapWatcher());) {
            logger.info("watch created.");

            Thread.sleep(30 * 24 * 60 * 60 * 1000L);
        } catch (InterruptedException e) {
            logger.info("exception:", e);
        }

        logger.info("quit watch.");
        kubernetesClient.close();
    }

    class FlinkPodWatcher implements Watcher<Pod> {

        @Override
        public boolean reconnecting() {
            logger.info("watcher:{} is reconnecting.", this);
            return Watcher.super.reconnecting();
        }

        @Override
        public void eventReceived(Action action, Pod pod) {
            MDC.put("resourceName", "pod." + pod.getMetadata().getName());
            logger.info("\n\n\n--- begin event ---\n\n\n");
            logger.info("receive {} events for pod name:{}, phase:{} resourceVersion:{} status:{}",
                    action,
                    pod.getMetadata().getName(),
                    pod.getStatus().getPhase(),
                    pod.getMetadata().getResourceVersion(),
                    pod.getStatus());

            switch (action) {
                case ERROR:
                case MODIFIED:
                    // 仅当 Pod 状态为非 Running 或存在重启时获取日志
                    if (!"Running".equals(pod.getStatus().getPhase()) ||
                            pod.getStatus().getContainerStatuses().stream().anyMatch(cs -> cs.getRestartCount() > 0)) {
                        logger.info("\n\n\n--- begin log ---\n\n\n");

                        try {
                            String log = kubernetesClient.pods()
                                    .inNamespace(pod.getMetadata().getNamespace())
                                    .withName(pod.getMetadata().getName())
                                    .tailingLines(20000) // 仅获取最后 N 行日志
                                    .getLog();

                            logger.info("Pod {} log.length:{}\nlog:\n{}", pod.getMetadata().getName(), log.length(), log);
                        } catch (Exception e) {
                            logger.warn("exception:", e);
                        } finally {
                            logger.info("\n\n\n--- end log ---\n\n\n");
                        }
                    } else {
                        logger.info("do nothing with event:{}", action);
                    }
                default:
                    logger.info("do nothing with event:{}", action);
                    break;
            }

            logger.info("\n\n\n--- end event ---\n\n\n");
            MDC.clear();
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

    class FlinkDeploymentWatcher implements Watcher<Deployment> {

        @Override
        public boolean reconnecting() {
            logger.info("watcher:{} is reconnecting.", this);
            return Watcher.super.reconnecting();
        }

        @Override
        public void eventReceived(Action action, Deployment deployment) {
            MDC.put("resourceName", "deployment." + deployment.getMetadata().getName());
            logger.info("receive {} events for deployment:{}, status:{}",
                    action,
                    deployment.getMetadata().getName(),
                    deployment.getStatus());
            logger.info("deployment:{}", deployment);
            MDC.clear();
        }

        @Override
        public void onClose() {
            logger.info("watcher:{} is close.", this);
            Watcher.super.onClose();
        }

        @Override
        public void onClose(WatcherException cause) {
            logger.info("watcher:{} is close. ", this, cause);
        }
    }

    class FlinkConfigMapWatcher implements Watcher<ConfigMap> {

        @Override
        public boolean reconnecting() {
            logger.info("watcher:{} is reconnecting.", this);
            return Watcher.super.reconnecting();
        }

        @Override
        public void eventReceived(Action action, ConfigMap configMap) {
            MDC.put("resourceName", "configmap." + configMap.getMetadata().getName());
            logger.info("receive {} events for configMap:{}, keys'len:{}",
                    action,
                    configMap.getMetadata().getName(),
                    configMap.getData().size());
            Map<String, String> data = configMap.getData();
            int count = 0;
            for (Map.Entry<String, String> entry : data.entrySet()) {
                logger.info("\tKey: " + entry.getKey() + ", Value: " + entry.getValue());
                if (++count == 5) {
                    break;
                }
            }
//            logger.info("configMap:{}", configMap);
            MDC.clear();
        }

        @Override
        public void onClose() {
            logger.info("watcher:{} is close.", this);
            Watcher.super.onClose();
        }

        @Override
        public void onClose(WatcherException cause) {
            logger.info("watcher:{} is close. ", this, cause);
        }

    }

    public static void main(String[] args) {
        WatchNativeFlinkSample watchSample = new WatchNativeFlinkSample();
        watchSample.watchSample();
    }
}
