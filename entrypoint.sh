#!/bin/bash

flyway -community migrate

java -jar /opt/data/starter-all.jar platform
