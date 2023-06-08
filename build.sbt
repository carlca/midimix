ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.0"

lazy val root = (project in file("."))
  .settings(
    name                := "MidiMix",
    // idePackagePrefix    := Some("com.carlca"),
    resolvers           += "Bitwig Maven Repository" at "https://maven.bitwig.com",
    libraryDependencies += "com.bitwig" % "extension-api" % "18" % "provided",
    libraryDependencies += "org.json"   % "json"          % "20230227",
    logLevel            := Level.Error,
    // showSuccess         := false,
    // showTiming          := false,
    scalacOptions       ++= Seq("-explain", "-deprecation", "-Wunused:imports,privates,locals,explicits")
  )
