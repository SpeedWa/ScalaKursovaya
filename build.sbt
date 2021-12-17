lazy val akkaHttpVersion = "10.2.7"
lazy val akkaVersion = "2.6.17"
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.kr",
      scalaVersion := "2.13.7"
    )),
    name := "akka-http-quickstart-scala",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.7",
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion %
        Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion %
        Test,
      "org.scalatest" %% "scalatest" % "3.2.9" %
        Test
    )
  )
