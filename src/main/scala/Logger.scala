
case class BasicEvent(event: String) extends Event {
	override def toString = event
}

case class SimpleEvent(subject: Any, predicate: String) extends Event {

}

class SubjectLogger(eventAppender: EventAppender, subject: Any) {
	// need to import verbs from somewhere else
	def has(predicate: String) = eventAppender.append(SimpleEvent(subject, "has " + predicate))
}

class Logger(eventAppender: EventAppender) {

	def that(event: String) = eventAppender.append(new BasicEvent(event))

	def thatThe(subject: Any) = new SubjectLogger(eventAppender, subject)
}
