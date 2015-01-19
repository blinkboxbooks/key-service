package com.blinkbox.books.quartermaster.key.public

import akka.actor.ActorRefFactory
import com.blinkbox.books.logging.DiagnosticExecutionContext
import com.blinkbox.books.spray.{Directives => CommonDirectives, _}
import org.slf4j.LoggerFactory
import spray.http.HttpHeaders._
import spray.http.StatusCodes._
import spray.routing.{ExceptionHandler, HttpService, Route}
import spray.util.LoggingContext

import scala.util.control.NonFatal

trait PublicApiRoutes extends HttpService {
  def retrieve: Route
}

class PublicApi(config: AppConfig)
              (implicit val actorRefFactory: ActorRefFactory) extends PublicApiRoutes with CommonDirectives  {

  implicit val executionContext = DiagnosticExecutionContext(actorRefFactory.dispatcher)
  implicit val timeout = config.api.timeout
  implicit val log = LoggerFactory.getLogger(classOf[PublicApi])

  val retrieve = pathEndOrSingleSlash {
    post {
      uncacheable("Hello World")
    }
  }

  val routes = rootPath(config.api.localUrl.path / "keys") {
    monitor() {
      respondWithHeader(RawHeader("Vary", "Accept, Accept-Encoding")) {
        handleExceptions(exceptionHandler) {
          retrieve
        }
      }
    }
  }

  private def exceptionHandler(implicit log: LoggingContext) = ExceptionHandler {
    case NonFatal(e) =>
      log.error(e, "Unhandled error")
      uncacheable(InternalServerError)
  }
}
