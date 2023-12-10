#!/usr/bin/env python
# coding=utf-8
import json
import logging
import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import time
from datetime import datetime

from bin import log, config

from collections import defaultdict

from bin.yan_application import YarnApplication
from bin.yarn_util import YarnUtil

# https://stackoverflow.com/questions/33866888/aggregate-resource-allocation-for-a-job-in-yarn/33868015#33868015
# https://stackoverflow.com/questions/34794753/get-the-memory-cpu-and-disk-usage-for-yarn-application

class ResourcesFetcher(object):
    def __init__(self, rm_url):
        self.yarn_util = YarnUtil(
            url=rm_url,
            auth=('', '')
        )
        self._yarn_applications = defaultdict(list)
        self._start_time = datetime.now()

    def run(self):
        while True:
            self._fetch_resources()
            self._dump()
            time.sleep(60)

    def _fetch_resources(self):
        params = {
            'startedTimeBegin': int(self._start_time.timestamp() * 1000)
            # 'queue': 'bigdata-core',
            # 'states': 'RUNNING,ACCEPTED,FINISHED,KILLED,FAILED'
        }
        resp = json.loads(self.yarn_util.fetchAllApplications(params))
        if resp['apps']:
            for app in resp['apps']['app']:
                yarn_application = YarnApplication(app)
                self._update_applications(yarn_application)

    def _update_applications(self, yarn_application):
        application_id = yarn_application.get_application_id()
        state = yarn_application.get_state()
        if application_id in self._yarn_applications or state == 'ACCEPTED':
            if state == 'ACCEPTED':
                pass
                # print('new application : %s' % application_id)

            self._yarn_applications[application_id].append((
                datetime.now(),
                yarn_application.get_state(),
                yarn_application.get_allocatedMB(),
                yarn_application.get_memory_seconds()))

        if application_id in self._yarn_applications and state == 'FINISHED':
            app_resources = self._yarn_applications.pop(application_id, [])
            logging.info('application:%s', application_id)
            for resource in app_resources:
                logging.info(resource)

            cal_memory_seconds = 0
            for i in range(len(app_resources) - 1):
                resource = app_resources[i]
                next_resource = app_resources[i + 1]
                elapsed_seconds = (next_resource[0] - resource[0]).total_seconds()
                cal_memory_seconds += (elapsed_seconds * resource[2])

            resource = app_resources[-1]
            diff_percent = -1.0
            if cal_memory_seconds > 0:
                diff_percent = (resource[-1] - cal_memory_seconds) / float(cal_memory_seconds)
            logging.info('len:%d calc_memory_seconds:%d last-resource:%s diff:%.2f',
                         len(app_resources),
                         cal_memory_seconds,
                         resource,
                         diff_percent)

        if state in ('KILLED', 'FAILED'):
            self._yarn_applications.pop(application_id, None)

    def _dump(self):
        with open('./data/application_used_resource.out', 'w') as f:
            for application_id in self._yarn_applications:
                f.write(application_id + '\n')
                for resource in self._yarn_applications[application_id]:
                    f.write(str(resource) + '\n')
                f.write('\n' * 2)


if __name__ == '__main__':
    log.enable_pretty_logging(config.COMMON_LOG_PARAMS)
    resources_fetcher = ResourcesFetcher(sys.argv[1])
    resources_fetcher.run()
