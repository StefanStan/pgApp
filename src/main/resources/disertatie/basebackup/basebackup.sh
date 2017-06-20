#!/bin/bash

if [ "$#" -ne 2 ]
then
        echo "Illegal number of parameters. 2 params: ip port"
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
if [ -d $basebackup ]
then
        rm -rf $basebackup/*
        rm -rf $basebackup/.[a-z]*
else
        mkdir $basebackup
        chmod 700 $basebackup
fi

"pg_basebackup" "-h" $1 "-p" $2 "-D" $basebackup
