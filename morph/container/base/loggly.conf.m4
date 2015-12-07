### Syslog-ng Logging Directives for Loggly.com ###
template LogglyFormat { template("<${`PRI'}>1 ${`ISODATE'} ${`HOST'} ${`PROGRAM'} ${`PID'} ${`MSGID'} [LOGGLY_TOKEN@41058 tag=\"syslog\" ] $MSG\n");
	template_escape(no);
};

destination d_loggly {
	tcp("logs-01.loggly.com" port(514) template(LogglyFormat));
};

log { 
	source(s_src); 
	destination(d_loggly); 
};
### END Syslog-ng Logging Directives for Loggly.com ###
