#!/bin/bash

name="$(basename $0)"
pgdata="$(find /pgdata -type d -name $name)"
logFile="$pgdata/pg_log/startup.log"

if [ ! -f $logFile ]
then
        "/bin/touch" "$logFile"
fi

"/usr/lib64/pgsql95/bin/pg_ctl" $1 "-D" $pgdata "-l" $logFile
