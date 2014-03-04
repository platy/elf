elf
===

Still at the conceptual stage - trying to think about logging in a different way.

Concept
-------

Traditional logging is you pass a string and log level to a method, some filters and formats
are applied, and this is written out to a file or stdout. A lot of the time this is all I want,
but not always - but this is what the myriad of logging frameworks cater for.

Problems I've seen with logging (some solutions) - my experience is from the java world:

* locking while writing to files in log intensive applications (newer async loggers like log4j2)
* Large log files slow to grep (logstash/elastic search)
* log files large on disk (log rolling / archiving)
* archives slow to search
* String creation overhead of trace logs which are not appended (late formatting after filtering ie slf4j)

What I've been wanting is, all in one box:

* Fast searchable audit logs since the beginning of time
* On-the-fly compression 
* Automatic caching
* Logs can be grouped, filtered and sorted by fields
* Logs are data structures with string representation
* Fields can be pulled out for creating stats
* Incremental map reduce to efficiently update stats and metrics which can then be queried by time windows in the past
* A natural API which can be read in source as documentation but can add as much context as possible to log data
* Scalable from hack projects to infinity

There is no attempt at integrating with other logging methodologies except that there should be a possibility to write /
obtain text logs.

I think this is achievable with a system whereby:

* Log objects are asynchronously sent to the log server via MQ
* Log server stores events as documents (some couch based database would probably be used)
* Incremental map/reduce then munches these into realtime stats and indexes and search indexes
  which can be queried incredibly quickly
* A monitoring thread then just makes sliding window queries on the reduced data to fire alerts
* Graphing APIs use the reduced data on demand (and cached so even the reduction range query need
  not be reperformed when multiple clients are following the same graphs)
* Search can be used from clients / curl to generate text/plain log output that you can then use
  and standard log parsing tools / techniches on


API
---

Currently the oly thing started in this project is a trial at a scala api for the system - I want to see whether
something like this would be readable and practical.
