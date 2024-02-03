package cn.izualzhy;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

public class KubernetesUtils {
    static KubernetesClient initKubernetesClient() {
        Config config = Config.autoConfigure(null);

        return new KubernetesClientBuilder().withConfig(config).build();
    }
}
