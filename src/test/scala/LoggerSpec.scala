import org.scalatest._
import org.scalamock.scalatest.MockFactory

class LoggerSpec extends FlatSpec with MockFactory with ShouldMatchers {
	
	"that with a String" should "log a basic instantaneous event" in {
		val appender = mock[EventAppender]
		val log = new Logger(appender)
		(appender.append _).expects(BasicEvent("Some event happened"))
		log.that("Some event happened").toString
	}

	case class Noun(noun: String)

	"thatThe with a noun object, verb and predicate" should "log a simple instantaneous event" in {
		val appender = mock[EventAppender]
		val log = new Logger(appender)
		(appender.append _).expects(SimpleEvent(Noun("Thing"), "has done something"))
		log.thatThe(Noun("Thing")).has("done something")
	}
}