val UNH_CS_Repository = "UNH/CS repository" at "https://cs.unh.edu/~charpov/lib"
val jcip = "net.jcip" % "jcip-annotations" % "1.0"
val apacheLang = "org.apache.commons" % "commons-lang3" % "3.9"
val UNH_CS = "edu.unh.cs" % "classutils" % "1.4.2"

lazy val root = (project in file(".")).
  settings(
    name := "network-on-chip",
    version := "1.1.1",
    scalaVersion := "2.13.1",

    resolvers += UNH_CS_Repository,

    libraryDependencies ++= Seq(
      UNH_CS % Test,
      jcip,
      apacheLang
    ),

    crossPaths := false,

    Test / logBuffered := false,
    Test / fork := true,
    Test / parallelExecution := false,
    Test / javaOptions += "-Xmx8G",

    Compile / compile / javacOptions ++= Seq("-deprecation", "-Xlint"),

    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-encoding", "utf-8", // Specify character encoding used by source files.
      "-explaintypes", // Explain type errors in more detail.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-unchecked", // Enable detailed unchecked (erasure) warnings.
      "-Xlint" // Enable recommended additional warnings.
    ),
    
    autoAPIMappings := true,
    Compile / doc / scalacOptions ++= Seq(
      "-author",
      "-doctitle", "CS735/835: Introduction to Parallel and Distributed Computing",
      "-doc-footer", "Copyright UNH/Michel Charpentier, 2020"
    ),
  )
