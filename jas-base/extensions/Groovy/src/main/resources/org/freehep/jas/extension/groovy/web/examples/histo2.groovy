import java.awt.*
import java.util.*
import jhplot.*

c1 = new HPlot("Canvas",600,400)
c1.getAntiAlias()
c1.setGTitle("Title")
// make the frame visible
c1.visible()
c1.setAutoRange()

h1 = new H1D("e^{+}e^{-} &rarr; W^{+}W^{-} &rarr; 4 jets",20, -2.0, 2.0)
rand = new Random()
for (int i=0; i<1000; i++)
      h1.fill(rand.nextGaussian())

h1.setFill(true)
h1.setFillColor(Color.green)
h1.setErrX(false)
h1.setErrY(true)
h1.setPenWidthErr(2)

h2 = new H1D("e^{+}e^{-} &rarr; Z/&gamma; &rarr;  hadrons ",15, -2.0, 2.0)
h2.setFill(true)
h2.setErrX(false)
h2.setErrY(true)
h2.setFillColorTransparency(0.7)
h2.setFillColor(Color.red)
h2.setColor(Color.red)
h2.setErrColorY(Color.blue)
h2.setNameX("X of H2")
h2.setNameY("Y of H2")

for (int i=0; i<1000; i++)
      h2.fill(2+rand.nextGaussian())



c1.setLegendFont( new Font("Lucida Sans", Font.BOLD, 18)  )
c1.setNameX("Text Examples:&minus; &theta; &pi; &omega; &int; &sum;")
c1.setNameY("Yaxis")
c1.setName("Canvas title: &radic;(1&minus; e)")

c1.draw(h1)
c1.draw(h2)

c1.export("histo2d.eps")
