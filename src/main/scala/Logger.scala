
case class BasicEvent(event: String) extends Event {
	override def toString = event
}

case class SimpleEvent(subject: Any, predicate: String) extends Event {
	override def toString = subject.toString + predicate
}

case class ProcessEvent(verb: String, subject: Any, result: Either[Any, Throwable]) extends Event {
	
}


class SubjectLogger(eventAppender: EventAppender, subject: Any) {
	// need to import verbs from somewhere else
	def has(predicate: String) = eventAppender.append(SimpleEvent(subject, "has " + predicate))
}

class VerbLogger(eventAppender: EventAppender, verb: String) {
	def of(subject: Any) = new BlockProcessLogger(eventAppender, verb, subject)
}

class BlockProcessLogger(eventAppender: EventAppender, verb: String, subject: Any) {
	def in(block: () => Any) = eventAppender.append(try {
		ProcessEvent(verb, subject, Left(block()))
	} catch {
		case ex: Throwable => ProcessEvent(verb, subject, Right(ex))
	})
}

class Logger(eventAppender: EventAppender) {

	def that(event: String) = eventAppender.append(new BasicEvent(event))

	def thatThe(subject: Any) = new SubjectLogger(eventAppender, subject)

	def the(verb: String) = new VerbLogger(eventAppender, verb)
}
