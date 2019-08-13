#!/bin/bash

if [[ -z $1 ]]; then
    echo "path argument is needed"
    exit 1
fi

WINDOWS_USER=$(whoami.exe | awk -F "\\" '{print $2}')
MOUNT_C=$(cat /proc/mounts | grep C: | awk '{print $2}')
HOME_FOLDER=$MOUNT_C/Users/${WINDOWS_USER/$'\r'/}
ex -sc "28d|x" $HOME_FOLDER/.air-adb/air-adb.sh && ex -sc "28i|                echo $1" -cx $HOME_FOLDER/.air-adb/air-adb.sh