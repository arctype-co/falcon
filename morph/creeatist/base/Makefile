all: loggly.conf

.PHONY: loggly.conf

loggly.conf: loggly.conf.m4
	m4 -DLOGGLY_TOKEN=$(LOGGLY_TOKEN) $^ > $@
