#!/usr/bin/env python
# coding=utf-8


import sqlglot


if __name__ == '__main__':
    sql = 'SELECT size(ARRAY (1, 2))'
    print(sqlglot.transpile(sql, 'hive', 'trino'))
