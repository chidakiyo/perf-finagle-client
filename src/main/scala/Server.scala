import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Http, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import java.net.InetSocketAddress
import org.jboss.netty.handler.codec.http._
import scala.util.Random

object Server {

  val filter = new SimpleFilter[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]) = {
      // Check if a request header exists and add it if it doesn't.
      val id = request.getHeader("X-Request-ID")
      if (id == null) {
        request.setHeader("X-Request-ID", Random.alphanumeric.take(5).mkString)
      }
      service(request)
    }
  }
  
  val responseMessage = """親譲おやゆずりの無鉄砲むてっぽうで小供の時から損ばかりしている。小学校に居る時分学校の二階から飛び降りて一週間ほど腰こしを抜ぬかした事がある。なぜそんな無闇むやみをしたと聞く人があるかも知れぬ。別段深い理由でもない。新築の二階から首を出していたら、同級生の一人が冗談じょうだんに、いくら威張いばっても、そこから飛び降りる事は出来まい。弱虫やーい。と囃はやしたからである。小使こづかいに負ぶさって帰って来た時、おやじが大きな眼めをして二階ぐらいから飛び降りて腰を抜かす奴やつがあるかと云いったから、この次は抜かさずに飛んで見せますと答えた。"""

  val response = new Service[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest) = {
      val id = request.getHeader("X-Request-ID")
      val response = Response()
      response.setContentString(responseMessage)
      Future.value(response)
    }
  }

  def main(args: Array[String]) {
    println("Start HTTP server on port 80")

    val service = filter andThen response

    val server = ServerBuilder()
      .codec(Http())
      .bindTo(new InetSocketAddress(80))
      .name("httpserver")
      .build(service)
  }

}