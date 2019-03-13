#!/bin/bash
screen -S eye
screen -r eye -p 0 -X stuff $'java -jar ./eyekp.jar 45 5 2>&1 \n'

