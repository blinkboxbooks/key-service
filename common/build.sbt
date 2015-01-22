name := "key-service-common"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"           %% "spray-testkit"        % sprayV    % Test,
    "com.typesafe.akka"  %% "akka-slf4j"           % akkaV,
    "com.typesafe.akka"  %% "akka-testkit"         % akkaV     % Test,
    "com.blinkbox.books" %% "common-scala-test"    % "0.3.0"   % Test,
    "com.blinkbox.books" %% "common-spray"         % "0.24.0",
    "com.blinkbox.books" %% "common-config"        % "2.3.1",
    "com.blinkbox.books" %% "common-slick"         % "0.3.4",
    "com.typesafe.slick" %% "slick"                % "2.1.0",
    "com.h2database"     %  "h2"                   % "1.4.185" % Test,
    "mysql"              %  "mysql-connector-java" % "5.1.34",
    "org.apache.commons" % "commons-dbcp2"         % "2.0.1"
  )
}

parallelExecution in Test := false
