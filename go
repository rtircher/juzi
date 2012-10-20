#!/bin/bash

source riak_setup.sh

STOP_RIAK=0

if [ $# -gt 1 ]; then
  echo "Usage ./go [stop-riak]"
  exit 1
elif [ $# == 1 ]; then
  if [ $1 = "--stop-riak" ]; then
    STOP_RIAK=1
  else
    echo "Invalid parameter $1"
    exit 1
  fi
fi

# For ulimit warning check
# http://docs.basho.com/riak/latest/cookbooks/Open-Files-Limit/
# On Mac edit /etc/launchd.conf
# and add following
# line limit maxfiles 16384 unlimited

if [ $LAST_RET_CODE == 0 ] && [ -d $RIAK_DEVREL ]; then
  ulimit -n 2048

  DEV_DIRS=`ls -d $RIAK_HOME/dev/*`

  for DEV_DIR in $DEV_DIRS; do
    RIAK_NODE="$DEV_DIR/bin/riak"
    if ! "$RIAK_NODE" ping &> /dev/null && [ $STOP_RIAK == 0 ]; then
      echo "Starting node $RIAK_NODE"
      "$RIAK_NODE" start
    fi

    if [ $STOP_RIAK == 1 ]; then
      echo "Stoping node $RIAK_NODE"
      "$RIAK_NODE" stop &> /dev/null;
    fi
  done
fi

if [ $LAST_RET_CODE == 0 ] && [ $STOP_RIAK == 0 ]; then
  lein ring server-headless
fi

if [ $LAST_RET_CODE != 0 ]; then
  exit $LAST_RET_CODE
fi
