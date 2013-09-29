import org.scalatest._
import org.scalamock.scalatest.MockFactory

class LoggerSpec extends FlatSpec with MockFactory with ShouldMatchers {
	
	"that with a String" should "log a basic instantaneous event" in {
		val appender = mock[EventAppender]
		val log = new Logger(appender)

		(appender.append _).expects(BasicEvent("Some event happened"))

		log that "Some event happened"
	}

	case class Noun(noun: String)

	"thatThe with a noun object, verb and predicate" should "log a simple instantaneous event" in {
		val appender = mock[EventAppender]
		val log = new Logger(appender)
		val subject = Noun("Thing")

		(appender.append _).expects(SimpleEvent(subject, "has done something"))

		log thatThe subject has "done something"
	}

	"the with a verb of subject" should "log an outcome event of the block" in {
		val appender = mock[EventAppender]
		val log = new Logger(appender)
		val subject = Noun("Thing")

		(appender.append _).expects(ProcessEvent("Process", subject, Left("Response")))

		log the "Process" of subject in { "Response" }
	}

	it should "log an exception outcome event of the block" in {
		val appender = mock[EventAppender]
		val log = new Logger(appender)
		val exception = new Exception
		val subject = Noun("Thing")

		(appender.append _).expects(ProcessEvent("Process", subject, Right(exception)))

		intercept[Exception] {
			log the "Process" of subject in { throw exception }
		}
	}
}
