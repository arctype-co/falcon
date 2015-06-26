#!/bin/bash

# Docker env
# ETHD_DATA, ETHD_SECRETS
OPTS="--verbosity 3 --datadir $ETHD_DATA  --rpc --rpccorsdomain '*' --rpcaddr 0.0.0.0 --unlock primary --password $ETHD_SECRETS/password"

ADDRESS=$(cat $ETHD_SECRETS/address)
ACCOUNT_FILE="$ETHD_SECRETS/$ADDRESS"
KEY_PATH=$ETHD_DATA/keystore/$ADDRESS/$ADDRESS
if [ -z "$ADDRESS" ]; then
  echo "Failed to read address secret"
  exit 1
fi
if [ ! -f $ACCOUNT_FILE ]; then
  echo "Account file secret does not exist: $ACCOUNT_FILE"
  exit 1
fi

echo "Importing address $ADDRESS"
mkdir -p $ETHD_DATA/keystore/$ADDRESS
rm -f $KEY_PATH
ln -s $ACCOUNT_FILE $KEY_PATH

geth $OPTS
