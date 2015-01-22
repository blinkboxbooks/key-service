package com.blinkbox.books.quartermaster.key.admin

import akka.actor.ActorRefFactory
import com.blinkbox.books.logging.DiagnosticExecutionContext
import com.blinkbox.books.quartermaster.key.common._
import com.blinkbox.books.slick.{UnknownDatabaseException, _}
import com.blinkbox.books.spray.v2.Implicits.throwableMarshaller
import com.blinkbox.books.spray.{v2, Directives => CommonDirectives}
import com.typesafe.scalalogging.StrictLogging
import spray.http.HttpHeaders.RawHeader
import spray.http.{RequestProcessingException, StatusCodes}
import spray.http.Uri.Path
import spray.routing.{HttpService, _}

import scala.concurrent.ExecutionContext

class AdminApi(config: AppConfig, keyStore: DbKeyStore[_ <: DatabaseSupport])(implicit val actorRefFactory: ActorRefFactory) extends v2.JsonSupport with HttpService with CommonDirectives with StrictLogging  {
  implicit val ec = DiagnosticExecutionContext(actorRefFactory.dispatcher)
  implicit val timeout = config.api.timeout

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: NoSuchKeyException => complete(StatusCodes.NotFound, v2.Error(e.getMessage, None))
    case e: KeyHasV1PathException => complete(StatusCodes.Conflict, v2.Error(e.getMessage, None))
    case e: UnknownDatabaseException => {
      logger.error(s"Accessing the database failed: ${e.getMessage}",e)
      failWith(new RequestProcessingException(StatusCodes.ServiceUnavailable))
    }
  }

  val getOrCreateKey = pathEndOrSingleSlash {
    post {
      entity(as[CreateKeyRequest]) { request =>
        onSuccess(keyStore.getOrCreateKey(request.isbn, request.fileHash))(complete(StatusCodes.OK, _))
      }
    }
  }

  val addV1PathToKey = pathPrefix(Segment.map(KeyId)) { keyId: KeyId =>
    pathEnd {
      patch {
        entity(as[AddPathRequest]) { request =>
          onSuccess(keyStore.addV1Path(keyId, request.v1Path))(complete(StatusCodes.OK, _))
        }
      }
    }
  }

  val routes = monitor(logger, throwableMarshaller) {
    handleExceptions(exceptionHandler) {
      rootPath(Path(config.api.localUrl.getPath)) {
        pathPrefix("keys") {
          respondWithHeader(RawHeader("Vary", "Accept, Accept-Encoding")) {
            getOrCreateKey ~ addV1PathToKey
          }
        }
      }
    }
  }
}
