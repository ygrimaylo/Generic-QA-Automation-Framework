#!/bin/sh
ps -ef | grep $1 | awk '{print $2}' | xargs kill
