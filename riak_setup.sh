#!/bin/bash

ERLANG_VERSION="erl -eval 'erlang:display(erlang:system_info(otp_release)), halt().'  -noshell"
# Find a way to check version is greater or equal to R15B01

RIAK_SRC="http://downloads.basho.com.s3-website-us-east-1.amazonaws.com/riak/1.2/1.2.1/riak-1.2.1.tar.gz"
RIAK_TARBALL="tools/riak-1.2.1.tar.gz"
RIAK_HOME="`pwd`/tools/riak-1.2.1"
RIAK_DEVREL="$RIAK_HOME/dev"
LAST_RET_CODE=0

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
  echo "  --> Extracting sources"
  tar zxf $RIAK_TARBALL -C tools
  echo "  --> Copying configuration"
  cp resources/config/riak/app.config $RIAK_HOME/rel/files/
  # cf comment above (this might be what's needed for prod deploy)
  # echo "  --> Building riak"
  # cd $RIAK_HOME
  # make all rel
  LAST_RET_CODE=$?
fi

# Seems like we should not use this in "prod"
# cf http://docs.basho.com/riak/1.2.0/cookbooks/Basic-Cluster-Setup/
# for more info
if [ $LAST_RET_CODE == 0 ] && [ ! -d $RIAK_DEVREL ]; then
  echo "Creating Riak nodes"
  cd $RIAK_HOME
  make devrel
  LAST_RET_CODE=$?
fi

if [ $LAST_RET_CODE != 0 ]; then
  exit $LAST_RET_CODE
fi
