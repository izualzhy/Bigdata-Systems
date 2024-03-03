package cn.izualzhy;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static io.fabric8.kubernetes.client.Config.KUBERNETES_KUBECONFIG_FILE;

public class CreatePodSample {
    private static final Logger logger = LoggerFactory.getLogger(CreatePodSample.class);

    void createPodSample(String fileName) {
        System.setProperty(KUBERNETES_KUBECONFIG_FILE, "/data/homework/.kube/config");
        try (final KubernetesClient client = KubernetesUtils.initKubernetesClient()) {
            logger.info("namespace:{}", client.getNamespace());
            client.pods().list().getItems().forEach(pod -> {
                logger.info("pod name:{}", pod.getMetadata().getName());
            });

            logger.info("config:{} {} {}", client.getConfiguration().getFile()
                    , client.getConfiguration().getClientCertFile()
                    , client.getMasterUrl());
            // created by yml
            List<HasMetadata> resources = client.load(Files.newInputStream(Paths.get(fileName)))
                    .items();
            logger.info("resources'len:{}", resources.size());
            logger.info("resources:{}", resources);

            HasMetadata resource = resources.get(0);
            if (resource instanceof Pod) {
                Pod pod = (Pod) resource;

                Pod createdPod = client.pods()
                        .inNamespace(client.getNamespace())
                        .resource(pod)
                        .create();

                logger.info("created pod name:{}", createdPod.getMetadata().getName());
                logger.info("created pod:{}", createdPod);
            }

            // created by builder
            Pod pod = new PodBuilder()
                    .withNewMetadata()
                    .withGenerateName("example-pod-")
                    .addToLabels("app", "example-pod")
                    .addToLabels("version", "v1")
                    .addToLabels("role", "backend")
                    .endMetadata()
                    .withNewSpec()
                    .addNewContainer()
                    .withName("nginx")
                    .withImage("nginx:1.7.9")
                    .withPorts(Collections.singletonList(new ContainerPort(80, null, null, "http", null)))
                    .endContainer()
                    .endSpec()
                    .build();

            Pod createdPod = client.pods().inNamespace("default").create(pod);
            logger.info("created pod name:{}", createdPod.getMetadata().getName());
        } catch (Exception e) {
            logger.info("exception", e);
        }
    }

    public static void main(String[] args) {
        CreatePodSample createPodSample = new CreatePodSample();
        createPodSample.createPodSample(args[0]);
    }
}
