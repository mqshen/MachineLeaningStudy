import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import sbtassembly.AssemblyPlugin.autoImport._

object Build extends sbt.Build {

  lazy val root = Project("dataStudy", file("."))
    .aggregate(base, firstWeek, web)
    .settings(basicSettings: _*)
    .settings(noPublishing: _*)
    .settings(XitrumPackage.skip: _*)

  lazy val base = Project("dataStudy-base", file("base"))
    .settings(basicSettings: _*)
    .settings(formatSettings: _*)
    .settings(releaseSettings: _*)
    .settings(SbtMultiJvm.multiJvmSettings ++ multiJvmSettings: _*)
    .settings(libraryDependencies ++= Dependencies.all)
    .settings(unmanagedSourceDirectories in Test += baseDirectory.value / "multi-jvm/scala")
    .settings(XitrumPackage.skip: _*)
    .configs(MultiJvm)

 lazy val firstWeek = Project("dataStudy-first", file("first"))
    .settings(basicSettings: _*)
    .settings(formatSettings: _*)
    .settings(releaseSettings: _*)
    .settings(crawlSettings: _*)
    .settings(SbtMultiJvm.multiJvmSettings ++ multiJvmSettings: _*)
    .settings(libraryDependencies ++= Dependencies.all)
    .settings(libraryDependencies <++= scalaVersion(Dependencies.jacksonLibs(_)))
    .settings(unmanagedSourceDirectories in Test += baseDirectory.value / "multi-jvm/scala")
    .settings(XitrumPackage.skip: _*)
    .configs(MultiJvm)
    .dependsOn(base)

 lazy val web = Project("dataStudy-web", file("web"))
    .settings(basicSettings: _*)
    .settings(formatSettings: _*)
    .settings(releaseSettings: _*)
    .settings(SbtMultiJvm.multiJvmSettings ++ multiJvmSettings: _*)
    .settings(libraryDependencies ++= Dependencies.all)
    .settings(unmanagedSourceDirectories in Test += baseDirectory.value / "multi-jvm/scala")
    .settings(XitrumPackage.skip: _*)
    .configs(MultiJvm)
   .dependsOn(base)

  lazy val benchmark = Project("dataStudy-benchmark", file("benchmark"))
    .settings(basicSettings: _*)
    .settings(formatSettings: _*)
    .settings(releaseSettings: _*)
    .settings(SbtMultiJvm.multiJvmSettings ++ multiJvmSettings: _*)
    .settings(libraryDependencies ++= Dependencies.all)
    .settings(unmanagedSourceDirectories in Test += baseDirectory.value / "multi-jvm/scala")
    .settings(XitrumPackage.skip: _*)
    .configs(MultiJvm)
    .dependsOn(base )

  lazy val basicSettings = Seq(
      organization := "org.goldratio",
      version := "0.1.0-SNAPSHOT",
      scalaVersion := "2.11.4",
      crossScalaVersions := Seq("2.10.4", "2.11.4"),
      scalacOptions ++= Seq("-unchecked", "-deprecation"),
      resolvers ++= Seq(
        "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
        "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
        "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
        "spray" at "http://repo.spray.io",
        "spray nightly" at "http://nightlies.spray.io/",
        "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven")
      )

  lazy val exampleSettings = basicSettings ++ noPublishing

  lazy val releaseSettings = Seq(
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (version.value.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      },
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := { (repo: MavenRepository) => false },
      pomExtra := (
        <url>https://github.com/mqshen/wetalk</url>
          <licenses>
            <license>
              <name>The Apache Software License, Version 2.0</name>
              <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
              <distribution>repo</distribution>
            </license>
          </licenses>
          <scm>
            <url>git@github.com:mqshen/wetalk.git</url>
            <connection>scm:git:git@github.com:mqshen/wetalk.git</connection>
          </scm>
          <developers>
            <developer>
              <id>mqshen</id>
              <name>miaoqi shen</name>
              <email>goldratio87@gmail.com</email>
            </developer>
          </developers>
        )
    )

  def multiJvmSettings = Seq(
    // make sure that MultiJvm test are compiled by the default test compilation
    compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
    // disable parallel tests
    parallelExecution in Test := false,
    // make sure that MultiJvm tests are executed by the default test target,
    // and combine the results from ordinary test and multi-jvm tests
    executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
      case (testResults, multiNodeResults) =>
        val overall =
          if (testResults.overall.id < multiNodeResults.overall.id)
            multiNodeResults.overall
          else
            testResults.overall
        Tests.Output(overall,
          testResults.events ++ multiNodeResults.events,
          testResults.summaries ++ multiNodeResults.summaries)
    })

  lazy val noPublishing = Seq(
    publish := (),
    publishLocal := (),
    // required until these tickets are closed https://github.com/sbt/sbt-pgp/issues/42,
    // https://github.com/sbt/sbt-pgp/issues/36
    publishTo := None
  )

  lazy val formatSettings = SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test := formattingPreferences)

  lazy val crawlSettings = Seq(
    mainClass in assembly := Some("org.goldratio.Starter")
  )

  import scalariform.formatter.preferences._
  def formattingPreferences =
    FormattingPreferences()
      .setPreference(RewriteArrowSymbols, false)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(IndentSpaces, 2)

}

object Dependencies {
  val AKKA_VERSION = "2.3.7"


  def jacksonVersion(scalaVersion: String) =
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, 11)) => "2.4.4"
      case _ => "2.3.1"
    }

  def jacksonLibs(scalaVersion: String) = Seq(
    "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion(scalaVersion),
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion(scalaVersion),
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion(scalaVersion) exclude("com.google.guava", "guava"),
    "com.google.guava" % "guava" % "16.0.1"
  )

  val akka_actor = "com.typesafe.akka" %% "akka-actor" % AKKA_VERSION
  val akka_contrib = "com.typesafe.akka" %% "akka-contrib" % AKKA_VERSION
  val akka_stream = "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-M2"
  val akka_http = "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0-M2"
  val scala_async = "org.scala-lang.modules" %% "scala-async" % "0.9.2"
  val jsoup = "org.jsoup" % "jsoup" % "1.8.1"
  val breeze = "org.scalanlp" %% "breeze" % "0.9"
  val breeze_viz = "org.scalanlp" %% "breeze-viz" % "0.9" exclude("org.scalanlp", "breeze")
  val spark = "org.apache.spark" %% "spark-core" % "1.2.0"

  val hikariCP = "com.zaxxer" % "HikariCP-java6" % "2.2.5" % "compile"
  val mysql = "mysql" % "mysql-connector-java" % "5.1.29"

  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.1" % "test"

  val all = Seq(akka_actor, akka_contrib, akka_stream, akka_http, scala_async, jsoup, hikariCP, mysql,
    scalaTest, breeze, breeze_viz, spark)
}
