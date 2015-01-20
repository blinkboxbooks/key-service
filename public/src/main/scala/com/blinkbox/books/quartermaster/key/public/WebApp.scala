package com.blinkbox.books.quartermaster.key.public

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.blinkbox.books.config.Configuration
import com.blinkbox.books.logging.{DiagnosticExecutionContext, Loggers}
import com.blinkbox.books.spray._
import spray.can.Http
import spray.http.Uri.Path
import spray.routing.HttpServiceActor

class WebService(config: AppConfig) extends HttpServiceActor {

  val that = this
  implicit val executionContext = DiagnosticExecutionContext(actorRefFactory.dispatcher)
  //  val apiService = new DefaultAdminService[DefaultDatabaseTypes](db, featureRepository, userRepository)
  val api = new PublicApi(config)

  val healthService = new HealthCheckHttpService {
    override implicit def actorRefFactory = that.actorRefFactory
    override val basePath = Path("/")
  }

  def receive = runRoute(api.routes ~ healthService.routes)

  //  override def dbSettings = config.db
}

object WebApp extends App with Configuration with Loggers {
  val appConfig = AppConfig(config)
  implicit val system = ActorSystem("akka-spray", config)
  implicit val executionContext = DiagnosticExecutionContext(system.dispatcher)
  implicit val timeout = Timeout(appConfig.api.timeout)
  sys.addShutdownHook(system.shutdown())
  val service = system.actorOf(Props(classOf[WebService], appConfig))
  val localUrl = appConfig.api.localUrl
  HttpServer(Http.Bind(service, localUrl.getHost, port = localUrl.effectivePort))
}
