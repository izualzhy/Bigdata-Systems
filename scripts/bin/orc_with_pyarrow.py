#!/usr/bin/env python
# coding=utf-8


import numpy as np
import pyarrow as pa
from pyarrow import orc

if __name__ == '__main__':
    table = pa.table(
        {
            'one': [-1, np.nan, 2.5],
            'two': ['foo', 'bar', 'baz'],
            'three': [True, False, True]
        }
    )
    orc.write_table(table, 'example.orc')
    print(orc.read_table('example.orc', columns=['one', 'three']))

