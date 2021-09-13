#!/usr/bin/env bash

#PBS -N program3
#PBS -q cs735c1
#PBS -l walltime=20:00

sbt -Dsbt.ci=true clean 'test:runMain grading.Grade' < /dev/null
