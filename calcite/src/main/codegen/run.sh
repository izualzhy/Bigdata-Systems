#!/bin/bash

generated_code_dir='./cn/izualzhy'
rm -rf "${generated_code_dir}"

javacc -JDK_VERSION:1.8 -OUTPUT_DIRECTORY="${generated_code_dir}" parser.jj

/usr/bin/javac "${generated_code_dir}"/*.java
echo 'EXPLAIN javacc;' > ./args
/usr/bin/java cn.izualzhy.Explain < ./args
