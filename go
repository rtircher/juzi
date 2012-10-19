#!/bin/bash

ERLANG_VERSION="erl -eval 'erlang:display(erlang:system_info(otp_release)), halt().'  -noshell"
# Find a way to check version is greater or equal to R15B01

RIAK_SRC="http://downloads.basho.com.s3-website-us-east-1.amazonaws.com/riak/1.2/1.2.1/riak-1.2.1.tar.gz"
RIAK_TARBALL="tools/riak-1.2.1.tar.gz"
RIAK_HOME="`pwd`/tools/riak-1.2.1"
RIAK_DEVREL="$RIAK_HOME/dev"
LAST_RET_CODE=0
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

if [ ! `which make` ]; then
  echo "Install Xcode with the Command Line Tools"
  exit 1
fi

if [ ! `which erl` ]; then
  echo "Install erlang first:"
  echo "brew install erlang"
  exit 1
fi

if [ $LAST_RET_CODE == 0 ] && [ ! -f $RIAK_TARBALL ]; then
  echo "Downloading Riak"
  curl -# $RIAK_SRC -o $RIAK_TARBALL --create-dirs
  LAST_RET_CODE=$?
fi

if [ $LAST_RET_CODE == 0 ] && [ ! -d $RIAK_HOME ]; then
  echo "Installing Riak"
  tar zxf $RIAK_TARBALL -C tools
  cd $RIAK_HOME
  make rel
  LAST_RET_CODE=$?

  if [ $LAST_RET_CODE == 0 ]; then
    echo "Buidling Riak"
    make all
    LAST_RET_CODE=$?
  fi
fi

if [ $LAST_RET_CODE == 0 ] && [ ! -d $RIAK_DEVREL ]; then
  echo "Creating Riak nodes"
  cd $RIAK_HOME
  make devrel
  LAST_RET_CODE=$?
fi

# For ulimit warning check
# http://docs.basho.com/riak/latest/cookbooks/Open-Files-Limit/

if [ $LAST_RET_CODE == 0 ] && [ -d $RIAK_DEVREL ]; then
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
