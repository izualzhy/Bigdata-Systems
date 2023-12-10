#!/usr/bin/env python
# coding=utf-8

class YarnApplication(object):
    def __init__(self, app):
        self._app = app

    def get_application_id(self):
        return self._app['id']

    def get_allocatedMB(self):
        return self._app['allocatedMB']

    def get_memory_seconds(self):
        return self._app['memorySeconds']

    def get_state(self):
        return self._app['state']
