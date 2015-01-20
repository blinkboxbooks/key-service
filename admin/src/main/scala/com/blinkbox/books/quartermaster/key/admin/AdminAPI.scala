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

class AdminApi(config: AppConfig)
              (implicit val actorRefFactory: ActorRefFactory) extends HttpService with CommonDirectives  {

  implicit val executionContext = DiagnosticExecutionContext(actorRefFactory.dispatcher)
  implicit val timeout = config.api.timeout
  val log = LoggerFactory.getLogger(classOf[AdminApi])

  def createKey = pathEndOrSingleSlash {
    post {
      uncacheable("Hello World")
    }
  }

  val routes = rootPath(config.api.localUrl.path / "keys") {
    monitor(log) {
      respondWithHeader(RawHeader("Vary", "Accept, Accept-Encoding")) {
        createKey
      }
    }
  }
}
