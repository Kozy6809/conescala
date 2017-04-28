lazy val root = (project in file(".")).
  settings(
	javacOptions := List("-encoding", "UTF-8"),
	name := "conescala",
	assemblyJarName in assembly := "haake1sec.jar",
	mainClass in assembly := Some("conescala.Startup")
  )
