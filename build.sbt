lazy val root = (project in file(".")).
  settings(
    name := "jsonsurfer",
    version := "0.2",
    libraryDependencies += "com.googlecode.json-simple" % "json-simple" % "1.1.1"
  )
