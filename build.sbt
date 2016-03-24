lazy val root = (project in file(".")).
  settings(
    name := "my-project",
    version := "1.0",
    scalaVersion := "2.11.7",
    mainClass in Compile := Some("com.lasclocker.java.SparkGopProcess")        
  )

autoScalaLibrary := false

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.4.1" % "provided"

unmanagedBase := baseDirectory.value / "custom_spark_lib"

// META-INF discarding
mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
   {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
   }
}
