package cn.izualzhy;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.utils.Serialization;

import java.util.Date;
import java.util.concurrent.TimeUnit;

// https://github.com/fabric8io/kubernetes-client/blob/main/kubernetes-examples/src/main/java/io/fabric8/kubernetes/examples/ConfigMapExample.java
public class ConfigMapSample {
    public static void main(String[] args) {
        String configMapName = args.length > 0 ? args[0] : "test-configmap";
        try (final KubernetesClient client = KubernetesUtils.initKubernetesClient()) {
            Resource<ConfigMap> configMapResource = client.configMaps().inNamespace("default")
                    .resource(
                            new ConfigMapBuilder().withNewMetadata()
                                    .withName(configMapName)
                                    .endMetadata()
                                    .addToData("foo", "" + new Date())
                                    .addToData("bar", "beer")
                                    .addToData("k1", "v1")
                                    .addToData("k2", "v2")
                                    .addToData("k3", "v3")
                                    .addToData("k4", "v4")
                                    .addToData("k5", "v5")
                                    .addToData("k6", "v6")
                                    .build()
                    );

            ConfigMap configMap = configMapResource.create();

            System.out.println("\ninit    configMap:\n" + Serialization.asYaml(configMap));

            TimeUnit.MINUTES.sleep(1);

            String originalValue = configMap.getData().put("fooV2", "" + new Date());
            System.out.println("add fooV2, originalValue = " + originalValue);
            originalValue = configMap.getData().remove("foo");
            System.out.println("del foo, originalValue = " + originalValue);
            System.out.println("\nlocal   configMap:\n" + Serialization.asYaml(configMap));

            ConfigMap updatedConfigMap = client.configMaps().inNamespace("default")
                    .withName(configMapName)
                    .patch(configMap);
            System.out.println("\nlocal   updatedConfigMap:\n" + Serialization.asYaml(updatedConfigMap));
            configMap = client.configMaps().inNamespace("default")
                    .withName(configMapName)
                    .get();
            System.out.println("\ncluster updatedConfigMap:\n" + Serialization.asYaml(configMap));

            TimeUnit.MINUTES.sleep(1);

            originalValue = configMap.getData().put("fooV3", "" + new Date());
            System.out.println("add fooV3, originalValue = " + originalValue);
            originalValue = configMap.getData().remove("fooV2");
            System.out.println("del fooV2, originalValue = " + originalValue);
            System.out.println("\nlocal   configMap:\n" + Serialization.asYaml(configMap));

           updatedConfigMap = client.configMaps().inNamespace("default")
                    .withName(configMapName)
                    .replace(configMap);
            System.out.println("\nlocal   updatedConfigMap:\n" + Serialization.asYaml(updatedConfigMap));
            configMap = client.configMaps().inNamespace("default")
                    .withName(configMapName)
                    .get();
            System.out.println("\ncluster updatedConfigMap:\n" + Serialization.asYaml(configMap));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
