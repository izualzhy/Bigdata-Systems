#!/usr/bin/env python
# coding=utf-8
import json
import sys
from datetime import datetime

import requests

from bin.yarn_application import YarnApplication


class YarnUtil(object):
    def __init__(self, url, auth):
        self._url = url
        self._auth = auth
        self._headers = {
            'Content-Type': 'application/json'
        }

    def fetchAllApplications(self, params):
        url = '%s/ws/v1/cluster/apps' % self._url
        print(url)
        print(params)
        response = requests.get(url,
                                headers=self._headers,
                                params=params,
                                auth=self._auth,
                                verify=False)
        return response.text

    def sortApplication(self, params, sort_key):
        applications = []
        resp = json.loads(self.fetchAllApplications(params))
        if resp['apps']:
            for app in resp['apps']['app']:
                applications.append(YarnApplication(app))
        applications.sort(key=sort_key)
        for application in applications:
            dt_object = datetime.fromtimestamp(application.get_started_time() / 1000.)
            time_string = dt_object.strftime('%Y-%m-%d %H:%M:%S')
            print('%s : %s' % (application.get_application_name(), time_string))


if __name__ == '__main__':
    yarn_url = sys.argv[1]
    user, passwd = sys.argv[2:4] if len(sys.argv) >= 4 else ('', '')

    yarn_util = YarnUtil(
        url=yarn_url,
        auth=(user, passwd)
    )
    yarn_params = {
        'states': 'RUNNING',
    }
    if len(sys.argv) >= 5:
        yarn_params['user.name'] = sys.argv[4]

    yarn_util.sortApplication(yarn_params, lambda x: x.get_started_time())


