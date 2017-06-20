#!/bin/bash

if [ "$#" -ne 2 ]
then
        echo "Illegal number of parameters. 2 params: date e.g. '2017-06-05 14:00:00 EEST' true/false if you want to start the db also"
        exit 1
fi

name="$(basename $0)"
pgdata="$(find /pgdata -type d -name $name)"
if [ ${#pgdata} == 0 ]
then
        echo "tenant with name $name has no pgdata folder"
        exit 2
fi

backup="$(find /backup -type d -name $name)"
if [ ${#backup} == 0 ]
then
        echo "tenant with name $name has no backup folder"
        exit 3
fi

basebackup="$backup/basebackup"
if [ ${#basebackup} == 0 ]
then
        echo "tenant with name $name has no basebackup folder"
        exit 4
fi

wal="$backup/wal"
if [ ${#wal} == 0 ]
then
        echo "tenant with name $name has no WAL files folder"
        exit 5
fi

# TO DO: make sure that the server instance is stopped
"/home/ec2-user/disertatie/server/$name" "stop"

# TO DO: backup logs and other things of interes before empty pgdata dir
rm -rf $pgdata/*
rm -rf $pgdata/.[a-z]*

"cp" "-a" "-r" "$basebackup/." $pgdata

result="restore_command = 'cp $wal/%f %p'\n
recovery_target_time = '$1'"

echo -e $result > $pgdata/recovery.conf

if [ $2 == true ]
then
        # Start the db server which will trigger recovery
        "/home/ec2-user/disertatie/server/$name" "start"
fi
