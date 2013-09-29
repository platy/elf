
trait Event

trait EventAppender {
	def append(event: Event): Unit
}
