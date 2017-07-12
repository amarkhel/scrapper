name := "viewer"
 
version := ""

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

excludedJars in assembly <<= (fullClasspath in assembly) map { cp => 
  cp filter { 
		i => i.data.getName == "slf4j-api-1.7.12.jar" 
	    }
}

scalaVersion := "2.11.8"
 
resolvers += "jitpack" at "https://jitpack.io"
resolvers += "jboss-releases" at "https://repository.jboss.org/nexus/content/repositories/releases"
 
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-streaming" % "2.1.0" % "provided",
  "org.scalaz" %% "scalaz-core" % "7.1.0",
  "org.scalaz" %% "scalaz-effect" % "7.1.0",
  "org.scalaz" %% "scalaz-typelevel" % "7.1.0",
  "org.scalaz" %% "scalaz-scalacheck-binding" % "7.1.0" % "test",
  "org.apache.spark" %% "spark-mllib" % "2.1.0" % "provided",
  "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided",
// comment above line and uncomment the following to run in sbt
// "org.apache.spark" %% "spark-streaming" % "1.6.1",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "org.mongodb.scala" %% "mongo-scala-driver" % "1.2.1",
"org.jsoup" % "jsoup" % "1.8.3",
"org.kie" % "kie-api" % "6.4.0.Final",
"org.drools" % "drools-core" % "6.4.0.Final",
"org.drools" % "drools-compiler" % "6.4.0.Final",
"org.drools" % "drools-decisiontables" % "6.4.0.Final",
"org.drools" % "drools-templates" % "6.4.0.Final",
"org.kie" % "kie-internal" % "6.4.0.Final",
"ch.qos.logback" % "logback-classic" % "1.1.3",
"org.slf4j" % "slf4j-api" % "1.7.7",
"org.apache.poi" % "poi" % "3.14",
"org.springframework" % "spring-context" % "4.3.1.RELEASE",
"org.springframework" % "spring-context-support" % "4.3.1.RELEASE",
"org.springframework" % "spring-aop" % "4.3.1.RELEASE",
"org.springframework" % "spring-webmvc" % "4.3.1.RELEASE",
"org.springframework" % "spring-web" % "4.3.1.RELEASE",
"javax.servlet" % "javax.servlet-api" % "3.1.0",
"javax.servlet.jsp" % "javax.servlet.jsp-api" % "2.3.1",
"org.freemarker" % "freemarker" % "2.3.23",
"junit" % "junit" % "4.8.1",
"org.scalactic" %% "scalactic" % "3.0.0",
"org.scalatest" %% "scalatest" % "3.0.0",
"c3p0" % "c3p0" % "0.9.1.2",
"com.typesafe.slick" %% "slick" % "3.1.1",
"mysql" % "mysql-connector-java" % "5.1.21",
"org.slf4j" % "slf4j-nop" % "1.6.4",
"com.typesafe.akka" %% "akka-stream" % "2.4.17",
"org.eclipse.jetty" % "jetty-plus" % "7.4.5.v20110725" % "container",
"org.jfarcand" % "wcs" % "1.5" 
)

enablePlugins(JettyPlugin)

javaOptions in Jetty ++= Seq(
  "-Xdebug",
  "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"
)

artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "." + artifact.extension
}

scalacOptions += "-feature"

initialCommands in console := "import scalaz._, Scalaz._"
