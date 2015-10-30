#!/bin/bash

## Create backend bucket types
riak-admin bucket-type create leveldb_backend '{"props":{"backend":"leveldb"}}'
riak-admin bucket-type create leveldb_consistent '{"props":{"backend": "leveldb", "consistent":true}}'
riak-admin bucket-type create memory_backend '{"props":{"backend":"memory"}}'
riak-admin bucket-type create memory_consistent '{"props":{"backend":"memory", "consistent": true}}'

## Activate bucket types
riak-admin bucket-type activate leveldb_backend
riak-admin bucket-type activate leveldb_consistent
riak-admin bucket-type activate memory_backend
riak-admin bucket-type activate memory_consistent
