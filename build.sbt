lazy val root = (project in file(".")).
  settings(
    name := "jsonsurfer",
    version := "0.1",
    libraryDependencies += "org.scalastuff" %% "json-parser" % "2.0.2"
  )
