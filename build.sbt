import laika.helium.Helium
import laika.helium.config.HeliumIcon
import laika.helium.config.IconLink
import laika.ast.Path.Root
import laika.theme.config.Color
import java.time.Instant
Global / semanticdbEnabled := true
Global / onChangedBuildSource := ReloadOnSourceChanges
import java.io.File

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

lazy val root = project
  .in(file("."))
  .settings(
    name := "hurdat",
    description := "Playing with hurricane data",
    scalaVersion := "3.0.2",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "requests" % "0.6.9",
      "com.lihaoyi" %% "upickle" % "1.4.3",
      "com.lihaoyi" %% "os-lib" % "0.8.0",
      "io.github.quafadas" %% "dedav4s" % "0.0.9",
      "org.scalanlp" %% "breeze" % "2.0"
    )
  )
  .enablePlugins(NoPublishPlugin)

val scalafixRules = Seq(
  "OrganizeImports",
  "DisableSyntax",
  "LeakingImplicitClassVal",
  "ProcedureSyntax",
  "NoValInForComprehension"
).mkString(" ")

lazy val jsdocs = project
  .in(file("jsdocs"))
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.1.0"
  )
  .enablePlugins(ScalaJSPlugin)

lazy val docs = project
  .in(file("myproject-docs")) // important: it must not be docs/
  .settings(
    mdocJS := Some(jsdocs),
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
    laikaConfig ~= { _.withRawContent },
    tlSiteHeliumConfig ~= {
      // Actually, this *disables* auto-linking, to avoid duplicates with mdoc
      _.site.autoLinkJS()
    },
    laikaTheme := tlSiteHeliumConfig.value.all
      .metadata(
        title = Some("Hurdat"),
        language = Some("en"),
        description = Some("Declarative data visualisation for scala"),
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
      .build
  )
  //.dependsOn(root)
  .enablePlugins(TypelevelSitePlugin)
