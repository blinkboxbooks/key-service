package com.blinkbox.books.quartermaster.key.public

import akka.actor.ActorRefFactory
import com.blinkbox.books.logging.DiagnosticExecutionContext
import com.blinkbox.books.spray.{Directives => CommonDirectives, _}
import org.slf4j.LoggerFactory
import spray.http.HttpHeaders._
import spray.http.StatusCodes._
import spray.routing.{ExceptionHandler, HttpService, Route}
import spray.util.LoggingContext

class PublicApi(config: AppConfig)
              (implicit val actorRefFactory: ActorRefFactory) extends HttpService with CommonDirectives  {

  implicit val executionContext = DiagnosticExecutionContext(actorRefFactory.dispatcher)
  implicit val timeout = config.api.timeout
  val log = LoggerFactory.getLogger(classOf[PublicApi])

  def retrieveKey = pathEndOrSingleSlash {
    post {
      uncacheable("Hello World")
    }
  }

  val routes = rootPath(config.api.localUrl.path / "keys") {
    monitor(log) {
      respondWithHeader(RawHeader("Vary", "Accept, Accept-Encoding")) {
        retrieveKey
      }
    }
  }
}
