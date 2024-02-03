package cn.izualzhy;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;

public class UsageSample {
    void usageSample() {
        try (KubernetesClient client = KubernetesUtils.initKubernetesClient()) {
            // list namespaces
            client.namespaces().list().getItems().forEach(namespace -> {
                System.out.println("namespace name: " + namespace.getMetadata().getName());
                System.out.println("namespace: " + namespace);
            });

            // list secret
            client.secrets().list().getItems().forEach(secret -> {
                System.out.println("secret name: " + secret.getMetadata().getName());
                System.out.println("secret: " + secret);
            });

            /*
            // create namespace
            Namespace myNamespace = client.namespaces().create(
                    new NamespaceBuilder().withNewMetadata()
                            .withName("my-ns-from-fabric8io-client")
                            .addToLabels("a", "label").endMetadata().build());
            System.out.println("myNamespace : " + myNamespace);
             */

            /*
            // create service
            Service myService = client.services().inNamespace("my-ns-from-fabric8io-client").create(
                    new ServiceBuilder().withNewMetadata()
                            .withName("my-service-from-fabric8io-client")
                            .addToLabels("another", "label").endMetadata().build());
            System.out.println("myService : " + myService);
             */

            ListOptions options = new ListOptions();
            options.setLabelSelector("type=flink-native-kubernetes");
            client.pods().inNamespace("default")
                    .list(options)
                    .getItems().forEach(pod -> {
                        System.out.println("pod name: " + pod.getMetadata().getName());
                        System.out.println("pod: " + pod);
                    });

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
    public static void main(String[] args) {
        UsageSample usageSample = new UsageSample();
        usageSample.usageSample();
    }
}
