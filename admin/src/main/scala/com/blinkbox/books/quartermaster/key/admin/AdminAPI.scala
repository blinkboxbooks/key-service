package com.blinkbox.books.quartermaster.key.admin

import akka.actor.ActorRefFactory
import com.blinkbox.books.logging.DiagnosticExecutionContext
import com.blinkbox.books.spray.{Directives => CommonDirectives, _}
import org.slf4j.LoggerFactory
import spray.http.HttpHeaders.RawHeader
import spray.http.StatusCodes._
import spray.routing.{ExceptionHandler, Route, HttpService}
import spray.util.LoggingContext

import scala.util.control.NonFatal

trait AdminApiRoutes extends HttpService {
  def create: Route
}

class AdminApi(config: AppConfig)
              (implicit val actorRefFactory: ActorRefFactory) extends AdminApiRoutes with CommonDirectives  {

  implicit val executionContext = DiagnosticExecutionContext(actorRefFactory.dispatcher)
  implicit val timeout = config.api.timeout
  implicit val log = LoggerFactory.getLogger(classOf[AdminApi])

  val create = pathEndOrSingleSlash {
    post {
      uncacheable("Hello World")
    }
  }

  val routes = rootPath(config.api.localUrl.path / "keys") {
    monitor() {
      respondWithHeader(RawHeader("Vary", "Accept, Accept-Encoding")) {
        handleExceptions(exceptionHandler) {
          create
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
