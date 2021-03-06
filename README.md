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

    val log: Logger
    log that "some event happened"            // Basic form of logging
    val subject                               // Extra context can be taken from this object, you might use a
                                              // conversion function to specify what data from it should be logged
    log thatThe subject has "done something"  // Simple event around subject
    log the "Process" of subject in {         // Logs metrics and result / exception of running block of code
      doSomething()
    }

The intention is to deter undescriptive logging and to encourage that a log entry represents an event and not a state - and also to allow collection of context and extra information in a consistent way accross the application.

There's a basic form, `log that <a past tense statement>`. In logs this allows us to say `At 11:30 some event happened`

A simple subjective form, `log thatThe <subject object> has <past tense predicate>` or `log that <a specified subject object> has <past tense predicate>`. In the first, the source subject has the definite article as the symbol will be defined before, both are treated the same by the system as the value name will not be used. The logging statement is a 3rd person singular present perfect statement (plural may be added), and due to the form it would be possible to group statements in output - `Between 4 & 5am 35 customers logged in` or make conditional perfect statements `After they logged in, 60% of customers bought something`.

A process logging form, `log the <name for what block does to object> of <object> in <code block / function>` which can automatically have access to the return value of the block, thrown exception and possibly the execution time of the block. This allows logging to talk about the process as the subject `Logging in of customer 1 failed with NullPointerException` or `Average time taken for logging in customers last week was 50ms`.

We could additionally get the variable names by using compiler macros making them part of the system grammar programmatically as well as in source.

The intention was also to provide a context (such as request / session) through threadlocals but this wouldn't work with futures anyway so I think either we could have implicit contexts or just maybe stick to the clearer explicit subjective contexts.
