import ReleaseTransformations._

lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yinline-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture"
)

lazy val hashSettings = Seq(
  ivyConfigurations += config("compileonly").hide,
  unmanagedClasspath in Compile ++= update.value.select(configurationFilter("compileonly")),
  scalacOptions ++= commonScalacOptions,
  organization := "com.rklaehn",
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.10.5", "2.11.7"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full),
  libraryDependencies ++= Seq(
    "com.github.mpilquist" %% "simulacrum" % "0.5.0" % "compileonly",
    "org.spire-math" %% "cats" % "0.3.0",
    "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",

    // thyme
    "ichi.bench" % "thyme" % "0.1.1" % "test" from "https://github.com/Ichoran/thyme/raw/9ff531411e10c698855ade2e5bde77791dd0869a/Thyme.jar"
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature"
  ),
  licenses += ("Apache License, Version 2.0", url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage := Some(url("http://github.com/rklaehn/hash")),

  // release stuff
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := Function.const(false),
  publishTo <<= version { v =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("Snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("Releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra :=
    <scm>
      <url>git@github.com:rklaehn/hash.git</url>
      <connection>scm:git:git@github.com:rklaehn/hash.git</connection>
    </scm>
    <developers>
      <developer>
        <id>r_k</id>
        <name>R&#xFC;diger Klaehn</name>
        <url>http://github.com/rklaehn/</url>
      </developer>
    </developers>
  ,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    ReleaseStep(action = Command.process("package", _)),
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    ReleaseStep(action = Command.process("publishSigned", _)),
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges))

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false)

lazy val root = project.in(file("."))
  .aggregate(coreJVM, coreJS)
  .settings(name := "root")
  .settings(hashSettings: _*)
  .settings(noPublish: _*)

lazy val core = crossProject.crossType(CrossType.Pure).in(file("."))
  .settings(name := "hash")
  .settings(hashSettings: _*)

lazy val coreJVM = core.jvm
lazy val coreJS = core.js
