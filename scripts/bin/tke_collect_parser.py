#!/usr/bin/env python
# coding=utf-8
import json
import logging
import os
import re
import sys
import time

from kafka import KafkaConsumer

from configs import script_config
from utils import log


class TKECollectParser(object):
    def __init__(self, broker_svr, topic, group_id):
        self._broker_svr = broker_svr
        self._topic = topic
        self._group_id = group_id
        self._consumer = KafkaConsumer(self._topic,
                                       bootstrap_servers=self._broker_svr,
                                       auto_offset_reset='earliest',
                                       group_id=self._group_id,
                                       enable_auto_commit=True)
        self._log_base_dir = '../data/tke_collect_parser_logs'
        self._log_files = {}
        self._last_flush_time = time.time()
        log.enable_pretty_logging(script_config.TKE_COLLECT_PARSER_PARAMS)

    def run(self):
        for (key, value) in self._fetch_log():
            (app_name, file_name, processed_log) = self._parse_log(key, value)
            self._save_log(app_name, file_name, processed_log)

    def _save_log(self, app_name, file_name, processed_log):
        # Create directory if it doesn't exist
        dir_path = os.path.join(self._log_base_dir, app_name)
        if not os.path.exists(dir_path):
            os.makedirs(dir_path)

        # Write processed log to file
        file_path = os.path.join(dir_path, file_name)
        if file_path in self._log_files:
            f = self._log_files[file_path]
        else:
            f = open(file_path, 'w')
            logging.info("new pod with app_name : %s , log will write to file_name : %s ", app_name, file_name)
            self._log_files[file_path] = f

        self._log_files[file_path].write(processed_log + '\n')

        # flush all log file handle
        if (self._last_flush_time - time.time()) >= 60.0:
            for log_file in self._log_files.values():
                log_file.flush()

    def _fetch_log(self):
        for message in self._consumer:
            # 从消息中获取键和值
            key = message.key.decode('utf-8') if message.key else None
            value = message.value.decode('utf-8') if message.value else None

            yield key, value

    @staticmethod
    def _parse_log(key, value):
        # currently we ignore key.
        json_data = json.loads(value)

        # Extract information for directory and filename
        app_name = json_data['kubernetes']['labels']['app']
        pod_name = json_data['kubernetes']['pod_name']
        pod_id = json_data['kubernetes']['pod_id']
        file_name = f"{pod_name}-{pod_id}.log"

        # Extract and process log content
        log_content = json_data['log']
        processed_log = re.sub(r'^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d+\+\d{2}:\d{2} (stdout|stderr) [FP] ', '', log_content)
        processed_log = processed_log.replace('\\t', '\t')

        return app_name, file_name, processed_log


if __name__ == '__main__':
    try:
        tke_collect_parser = TKECollectParser(sys.argv[1], sys.argv[2], sys.argv[3])
        tke_collect_parser.run()
    except Exception as e:
        logging.error("meet e:%s", e)

