#!/bin/bash

# TODO
# - Respawn on exit
# - Copy from readonly mount for n replicas > 1
# - Single r/w master?

PASSWORD=""
OPTS="--verbosity 3 --datadir /home/ethd/eth-data  --rpc --rpccorsdomain \"*\" --rpcaddr 0.0.0.0"
mkfifo js
catpid=$!
tail -f js | geth $OPTS console &
gethpid=$!
printf "if(eth.accounts.length == 0) {console.log(\"Creating new account\"); admin.newAccount(\"$PASSWORD\");}\nadmin.unlock(eth.accounts[0], \"$PASSWORD\");\neth.accounts;\n" > js
wait $gethpid
kill $gethpid
