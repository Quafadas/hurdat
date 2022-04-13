import mill._
import mill.define.Target
import mill.scalajslib._
import mill.scalalib._
import mill.util.Ctx
import coursier.maven.MavenRepository
import $ivy.`com.lihaoyi::mill-contrib-bloop:$MILL_VERSION`
import $ivy.`de.wayofquality.blended::mill-mdoc-testsupport_mill0.10.0:0.0.3`
import $ivy.`de.wayofquality.blended::de.wayofquality.blended.mill.docusaurus2_mill0.10:0.0.3`
import de.wayofquality.mill.mdoc.MDocModule
import de.wayofquality.mill.docusaurus2.Docusaurus2Module
import mill.define.Sources
import os.Path
import de.wayofquality.mill.mdoc._

object Config {
  def scalaVersion = "3.1.1"
  def scalaJSVersion = "1.10.0"
  
  def sharedDependencies = Agg(

  )

  def jvmDependencies = Agg(
  )

  def jsDependencies = Agg(
    
  )
}

trait Common extends ScalaModule {
  def scalaVersion = Config.scalaVersion

  //waiting covenant release
  def repositories = super.repositories ++ Seq(
    
  )

  def ivyDeps = super.ivyDeps() ++ Config.sharedDependencies

  def sources = T.sources(
    millSourcePath / "src",
    millSourcePath / os.up / "shared" / "src"
  )
}
object shared extends Common //needed for intellij

object jvm extends Common {
  def ivyDeps = super.ivyDeps() ++ Config.jvmDependencies
}

object js extends Common with ScalaJSModule {
  def scalaJSVersion = Config.scalaJSVersion

  def ivyDeps = super.ivyDeps() ++ Config.jsDependencies
}

object site extends Docusaurus2Module with MDocModule {
       // Set the Scala version (required to invoke the proper compiler)
    override def scalaVersion = T(Config.scalaVersion)

    override def scalaMdocVersion = "2.3.2"
    // The md inputs live in the "docs" folder of the project 
    override def mdocSources = T.sources{ T.workspace / "docs" }
    override def docusaurusSources = T.sources(
      T.workspace / "website"
    )

    // If we are running docusaurus in watch mode we want to replace compiled 
    // mdoc files on the fly - this will NOT build md files for the site
    // Hence we must use `mdoc` once we finished editing.
    override def watchedMDocsDestination: T[Option[Path]] = T(Some(docusaurusBuild().path / "docs"))

    // This is where docusaurus will find the compiled mdocs to BUILD the site
    override def compiledMdocs: Sources = T.sources(mdoc().path) 
}