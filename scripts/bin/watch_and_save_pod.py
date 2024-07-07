#!/usr/bin/env python
# coding=utf-8
import logging

import yaml
from kubernetes import client, config, watch
import os
import sys
import datetime
import threading

from configs import script_config
from utils import log


class PodEventWatcher(object):
    def __init__(self, config_path, namespace):
        logging.info("PodEventWatcher pwd: " + os.getcwd())
        print("PodEventWatcher pwd: " + os.getcwd())
        # 配置 Kubernetes 客户端
        config.load_kube_config(config_file=config_path)

        # Kubernetes API 客户端
        self.v1 = client.CoreV1Api()
        self.namespace = namespace  # 设置你要监控的命名空间

    def handle_pod_event(self, pod_data):
        pod_name = pod_data['object'].metadata.name
        pod_uid = pod_data['object'].metadata.uid
        event_type = pod_data['type']
        timestamp = datetime.datetime.now().strftime("%Y%m%d%H%M%S")

        yaml_dir = f'yaml/{pod_name}.{pod_uid}'
        yaml_file = f'{yaml_dir}/{timestamp}.yaml'

        if not os.path.exists(yaml_dir):
            os.makedirs(yaml_dir)

        pod_yaml = self.v1.read_namespaced_pod(name=pod_name, namespace=self.namespace)
        with open(yaml_file, 'w') as file:
            yaml_content = yaml.dump(client.ApiClient().sanitize_for_serialization(pod_yaml), default_flow_style=False)
            file.write(yaml_content)

        logging.info(f"Event: {event_type}, Pod: {pod_name}, {pod_uid} yaml exported to: {yaml_file}")

    def monitor_pods(self):
        w = watch.Watch()
        for event in w.stream(self.v1.list_namespaced_pod, self.namespace):
            threading.Thread(target=self.handle_pod_event, args=(event,)).start()


if __name__ == '__main__':
    try:
        log.enable_pretty_logging(script_config.POD_EVENT_WATCHER_PARAMS)
        pod_event_watcher = PodEventWatcher(sys.argv[1], sys.argv[2])
        logging.info("begin loop.")
        while True:
            logging.info("begin monitor.")
            pod_event_watcher.monitor_pods()
    except Exception as e:
        print("e:")
        print(e)

