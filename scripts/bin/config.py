#!/usr/bin/env python
# coding=utf-8
import datetime
import time

today = datetime.date.today().strftime("%Y%m%d")

COMMON_LOG_PARAMS = {
    "cut_type": "Timing",
    "logging": "info",
    "log_to_stderr": False,
    "log_file_prefix": "./logs/common.{date}.log".format(date=today),
    "startat": int(time.mktime(time.localtime((int(time.time()) / 60 / 60 / 24 + 1) * 60 * 60 * 24))),
    "suffix": "%Y%m%d%H",
    "extMatch": r"^\d{4}\d{2}\d{2}\d{2}$",
    "when": "D",
    "log_file_max_size": 10 * 1000 * 1000 * 1000,
    "log_file_num_backups": 10
}