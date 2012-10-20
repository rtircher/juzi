#!/bin/bash

source riak_setup.sh

RIAK_DEV="$RIAK_HOME/dev"
STOP_RIAK=0

function extract_node_name {
  local first_node=$1
  local node_prefix="$RIAK_DEV/"
  echo ${first_node#${node_prefix}}
}

function create_cluster {
  local all_nodes=$@
  local first_node=$1
  local join_node="`extract_node_name $first_node`@127.0.0.1"
  shift
  local rest_nodes=$@

  for node in $all_nodes; do
    local riak_node="$node/bin/riak"

    if ! "$riak_node" ping &> /dev/null; then
      echo "Starting node $node"
      "$riak_node" start
    fi
  done


  for node in $rest_nodes; do
    local admin_node="$node/bin/riak-admin"

    $admin_node cluster join $join_node
  done

  $first_node/bin/riak-admin cluster plan
  $first_node/bin/riak-admin cluster commit
}

function shutdown_cluster {
  local all_nodes=$@
  local first_node=$1
  local join_node="`extract_node_name $first_node`@127.0.0.1"
  shift
  local rest_nodes=$@

  # This line might be enought to remove other nodes from cluster and
  # stop them... to be verified
  $first_node/bin/riak-admin cluster clear

  for node in $rest_nodes; do
    local riak_node="$node/bin/riak"

    if "$riak_node" ping &> /dev/null; then
      local admin_node="$node/bin/riak-admin"
      echo "Node $node leaving cluster"
      $admin_node cluster leave
    fi
  done

  for node in $all_nodes; do
    local riak_node="$node/bin/riak"

    if "$riak_node" ping &> /dev/null; then
      echo "Stoping node $node"
      "$riak_node" stop &> /dev/null;
    fi
  done
}

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

  DEV_DIRS=`ls -d $RIAK_DEV/*`

  if [ $STOP_RIAK == 1 ]; then
    shutdown_cluster $DEV_DIRS
  else
    create_cluster $DEV_DIRS
  fi
fi

if [ $LAST_RET_CODE == 0 ] && [ $STOP_RIAK == 0 ]; then
  lein ring server-headless
fi

if [ $LAST_RET_CODE != 0 ]; then
  exit $LAST_RET_CODE
fi
