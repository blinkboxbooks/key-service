package com.blinkbox.books.quartermaster.key.admin

import akka.actor.{ActorRefFactory, ActorSystem, Props}
import akka.util.Timeout
import com.blinkbox.books.config.Configuration
import com.blinkbox.books.logging.Loggers
import com.blinkbox.books.quartermaster.key.common._
import com.blinkbox.books.slick.{DatabaseSupport, MySQLDatabaseSupport}
import com.blinkbox.books.spray._
import com.typesafe.scalalogging.StrictLogging
import spray.can.Http
import spray.http.Uri.Path
import spray.routing.HttpServiceActor

import scala.concurrent.duration._

object Main extends App with Configuration with Loggers with StrictLogging {
  val SERVICE_NAME = "key-service-admin"
  logger.info(s"Starting ${SERVICE_NAME}")
  val appConfig = AppConfig(config)
  implicit val system = ActorSystem(SERVICE_NAME)
  implicit val ec = system.dispatcher

  val dbComponent = new DefaultDatabaseComponent(appConfig.db)
  val keyStore = new DbKeyStore[MySQLDatabaseSupport](dbComponent.db, dbComponent.tables, dbComponent.exceptionFilter, ec)
  val adminApi = new AdminApi(appConfig, keyStore)
  val service = system.actorOf(Props(classOf[AdminApiActor], adminApi))

  val localUrl = appConfig.api.localUrl
  HttpServer(Http.Bind(service, localUrl.getHost, port = localUrl.effectivePort))(system, system.dispatcher, Timeout(10.seconds))
}

class AdminApiActor(adminApi: AdminApi) extends HttpServiceActor {

  val healthService = new HealthCheckHttpService {
    override val basePath: Path = Path("/")
    override implicit def actorRefFactory: ActorRefFactory = AdminApiActor.this.actorRefFactory
  }

  override def receive = runRoute(healthService.routes ~ adminApi.routes)
}