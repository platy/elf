
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
	def of(subject: Any): BlockProcessLogger = new BlockProcessLogger(eventAppender, verb, subject)
}

class BlockProcessLogger(eventAppender: EventAppender, verb: String, subject: Any) {
	def in[T](block: => T): T  = try {
			val result: T = block
			eventAppender.append(ProcessEvent(verb, subject, Left(result)))
			block
		} catch {
			case ex: Throwable => {
				eventAppender.append(ProcessEvent(verb, subject, Right(ex)))
				throw ex
			}
		}
}

class Logger(eventAppender: EventAppender) {

	def that(event: String) = eventAppender.append(new BasicEvent(event))

	def thatThe(subject: Any) = new SubjectLogger(eventAppender, subject)

	def the(verb: String) = new VerbLogger(eventAppender, verb)
}
