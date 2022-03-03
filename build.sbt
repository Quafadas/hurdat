import org.scalablytyped.converter.internal.ScalaJsBundlerHack
import laika.helium.Helium
import laika.helium.config.HeliumIcon
import laika.helium.config.IconLink
import laika.helium.*
import laika.rewrite.link.LinkConfig
import laika.rewrite.link.SourceLinks
import laika.markdown.github.GitHubFlavor
import laika.parse.code.SyntaxHighlighting
import laika.ast.Path.Root
import laika.theme.config.Color
import java.time.Instant
import java.io.File

Global / semanticdbEnabled := true
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17"))
ThisBuild / tlSitePublishBranch := Some("main")
ThisBuild / tlBaseVersion := "0.0"
ThisBuild / organization := "io.github.quafadas"
ThisBuild / organizationName := "quafadas"
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("quafadas", "Simon Parten")
)
ThisBuild / scalaVersion := "3.1.0"

lazy val root = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
    name := "hurdat",
    description := "Playing with hurricane data",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "1.5.0",

    )
  ).jvmSettings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "requests" % "0.7.0",
      "com.lihaoyi" %% "os-lib" % "0.8.0",
      "io.github.quafadas" %% "dedav4s" % "0.5.1"
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(      
      "org.scala-js" %%% "scalajs-dom" % "2.1.0",
    )
  )
  .enablePlugins(NoPublishPlugin)

lazy val leaflet = project
  .in(file("leaflet"))
  .enablePlugins(ScalablyTypedConverterGenSourcePlugin, ScalaJSBundlerPlugin)
  .dependsOn(root.js)
  .settings(
    organization := "com.leaflet",
    moduleName := "leaflet",
    Compile / npmDependencies ++= Seq(      
      "leaflet" -> "1.7.1",
      "@types/leaflet" -> "1.7.9"
    ),
    stOutputPackage := "com.leaflet",
    //stMinimize := Selection.AllExcept("@types/leaflet"),
    scalaJSUseMainModuleInitializer := true
  )
  
lazy val docs = project
  .in(file("myproject-docs")) // important: it must not be docs/
  .dependsOn(root.jvm)
  .settings(
    mdocJS := Some(leaflet),
    mdocVariables ++= Map(
      "js-batch-mode" -> "true",
      "js-html-header" ->
        """
<script crossorigin type="text/javascript" src="https://cdn.jsdelivr.net/npm/vega@5"></script>
<script crossorigin type="text/javascript" src="https://cdn.jsdelivr.net/npm/vega-lite@5"></script>
<script crossorigin type="text/javascript" src="https://cdn.jsdelivr.net/npm/vega-embed@6"></script>
"""
    ),
    libraryDependencies ++= Seq(
      //("org.scalanlp" %% "breeze" % "2.0").exclude("org.scala-lang.modules", "scala-collection-compat_2.13")
    ),
    laikaConfig := LaikaConfig.defaults.withConfigValue(
      LinkConfig(sourceLinks =
        Seq(
          SourceLinks(baseUri = "https://github.com/Quafadas/hurdat/blob/main/src/main/scala/", suffix = "scala")
        )
      )
    ),
    laikaExtensions := Seq(GitHubFlavor, SyntaxHighlighting),
    laikaTheme := Helium.defaults.all
      .metadata(
        title = Some("Hurdat"),
        language = Some("de")
      )
      .build
    /*     laikaTheme := Helium.defaults.all
      .metadata(
        title = Some("Hurdat"),
        language = Some("en"),
        description = Some("Playing with hurdat"),
        authors = Seq("Simon Parten"),
        date = Some(Instant.now)
      )
      .site
      .darkMode
      .themeColors(
        primary = Color.hex("1c44b2"),
        secondary = Color.hex("b26046"),
        primaryMedium = Color.hex("2962ff"),
        primaryLight = Color.hex("e6f4f3"),
        text = Color.hex("000000"),
        background = Color.hex("ffffff"),
        bgGradient = (Color.hex("3788ac"), Color.hex("fff5e6"))
      )
      .site
      .themeColors(
        primary = Color.hex("3788ac"),
        secondary = Color.hex("b26046"),
        primaryMedium = Color.hex("2962ff"),
        primaryLight = Color.hex("fff5e6"),
        text = Color.hex("000000"),
        background = Color.hex("ffffff"),
        bgGradient = (Color.hex("3788ac"), Color.hex("fff5e6"))
      )
      .site
      .topNavigationBar(
        homeLink = IconLink.internal(Root / "README.md", HeliumIcon.home),
        navLinks = Seq(IconLink.external("https://github.com/Quafadas/hurdat", HeliumIcon.github)),
        highContrast = false
      )
      .build */
  )
  .enablePlugins(TypelevelSitePlugin)
