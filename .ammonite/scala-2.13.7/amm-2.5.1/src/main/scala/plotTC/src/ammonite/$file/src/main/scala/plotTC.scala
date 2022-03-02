
package ammonite
package $file.src.main.scala
import _root_.ammonite.interp.api.InterpBridge.{
  value => interp
}
import _root_.ammonite.interp.api.InterpBridge.value.{
  exit,
  scalaVersion
}
import _root_.ammonite.interp.api.IvyConstructor.{
  ArtifactIdExt,
  GroupIdExt
}
import _root_.ammonite.compiler.CompilerExtensions.{
  CompilerInterpAPIExtensions,
  CompilerReplAPIExtensions
}
import _root_.ammonite.runtime.tools.{
  browse,
  grep,
  time,
  tail
}
import _root_.ammonite.compiler.tools.{
  desugar,
  source
}
import _root_.mainargs.{
  arg,
  main
}
import _root_.ammonite.repl.tools.Util.{
  PathRead
}


object plotTC{
/*<script>*/import $ivy.$                                   

import $file.$       

val tcDb = hurdat2.finalTCDb

import viz.PlotTargets.postHttp

def fixProjection(): ujson.Value => Unit = new((ujson.Value => Unit)) {
      override def toString = s"fix projection"
      def apply(spec: ujson.Value) = {
          spec("signals").arr.foreach{aSignal => if(aSignal.obj.contains("bind")) then {aSignal.obj.remove("bind")} }
  
          spec("signals")(1)("value") = 500
          spec("signals")(2)("value") = 61
          spec("signals")(3)("value") = -22
          spec("signals")(5)("value") = 7
          spec("signals")(6)("value") = 10
  
      }
   }

 /*<amm>*/val res_5 = /*</amm>*/viz.vega.plots.WorldMap(List(viz.Utils.fixDefaultDataUrl, fixProjection(), viz.Utils.fillDiv)) /*</script>*/ /*<generated>*/
def $main() = { scala.Iterator[String]() }
  override def toString = "plotTC"
  /*</generated>*/
}
