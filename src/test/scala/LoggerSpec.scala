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
		val subject = Noun("Thing")
		log.thatThe(subject).has("done something")
	}

	"the with a verb of subject" should "log an outcome event of the block" in {
		val appender = mock[EventAppender]
		val log = new Logger(appender)
		(appender.append _).expects(ProcessEvent("Process", Noun("Thing"), Left("Response")))
		val subject = Noun("Thing")
		log.the("Process").of(subject).in(() => "Response")
	}

	it should "log an exception outcome event of the block" in {
		val appender = mock[EventAppender]
		val log = new Logger(appender)
		val exception = new Exception
		(appender.append _).expects(ProcessEvent("Process", Noun("Thing"), Right(exception)))
		val subject = Noun("Thing")
		log.the("Process").of(subject).in(() => throw exception)
	}
}
