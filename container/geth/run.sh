#!/bin/bash

# TODO
# - Respawn on exit
# - Copy from readonly mount for n replicas > 1
# - Single r/w master?

OPTS="--verbosity 3 --datadir data  --rpc --rpccorsdomain \"*\" --rpcaddr 0.0.0.0 --unlock primary --password secrets/password"

ADDRESS=$(cat secrets/address)
ACCOUNT_FILE="secrets/$ADDRESS"
if [ -z "$ADDRESS" ]; then
  echo "Failed to read address secret"
  exit 1
fi
if [ ! -f $ACCOUNT_FILE ]; then
  echo "Account file secret does not exist: $ACCOUNT_FILE"
  exit 1
fi

echo "Importing address $ADDRESS"
mkdir -p data/keystore/$ADDRESS
cp -f $ACCOUNT_FILE data/keystore/$ADDRESS/$ADDRESS

geth $OPTS
