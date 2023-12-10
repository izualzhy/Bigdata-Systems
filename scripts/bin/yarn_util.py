#!/usr/bin/env python
# coding=utf-8
import requests


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

