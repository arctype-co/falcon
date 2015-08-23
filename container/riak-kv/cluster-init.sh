#!/bin/bash

# Create backend bucket types
# riak-admin bucket-type create bitcask_backend '{"props":{"backend":"bitcask"}}'
riak-admin bucket-type create leveldb_backend '{"props":{"backend":"leveldb"}}'
riak-admin bucket-type create memory_backend '{"props":{"backend":"memory"}}'
# riak-admin bucket-type activate bitcask_backend
riak-admin bucket-type activate leveldb_backend
riak-admin bucket-type activate memory_backend
