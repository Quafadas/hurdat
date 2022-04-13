Try and get some scala js going

How about now?

```scala
val x = 1
```

```scala
import org.scalajs.dom.html
import org.scalajs.dom
import scala.util.Random


val child = dom.document.createElement("div")
val anId = Random.alphanumeric.take(8).mkString("")        
child.id = anId
child.setAttribute("style",s"width:75vmin;height:50vmin")
node.appendChild(child)
hurdat.map.setupMap("../assets/hurdat.json", Some(child.asInstanceOf[html.Div]))
```