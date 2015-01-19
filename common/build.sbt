name := "key-service-common"

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"           %% "spray-testkit"        % sprayV    % Test,
    "com.typesafe.akka"  %% "akka-slf4j"           % akkaV,
    "com.typesafe.akka"  %% "akka-testkit"         % akkaV     % Test,
    "com.blinkbox.books" %% "common-scala-test"    % "0.3.0"   % Test,
    "com.blinkbox.books" %% "common-spray"         % "0.17.5",
    "com.blinkbox.books" %% "common-spray-auth"    % "0.7.4",
    "com.blinkbox.books" %% "common-config"        % "1.4.1",
    "com.typesafe.slick" %% "slick"                % "2.1.0",
    "com.h2database"     %  "h2"                   % "1.4.182" % Test,
    "mysql"              %  "mysql-connector-java" % "5.1.34"
  )
}