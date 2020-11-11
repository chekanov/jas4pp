# Example from DMelt http://jwork.org/dmelt/
# S.Chekanov (ANL)

from jhplot  import *
from jhplot.io import *
import os.path

# make event as a list of strind, P0D and histogram
def makeEvent(entry):
    event=[]
    label="Event="+str(i)
    p=P0D(label)
    p.randomUniform(10,0,1)
    h=H1D(label,10,-1,1)
    h.fill(i)
    event.append(label) 
    event.append(p)
    event.append(h)
    return event

# write events to serialized files
file="output.jser"
f=HFile(file,"w")
Events=100
for i in range(Events):
       event=makeEvent(i)
       if (i%100 == 0):  
           print "write :" +event[0]+" size=",os.path.getsize(file)     
       f.write(event)
f.close()

# read all the entries 
f=HFile(file)
while(1):
       event=f.read()
       if event == None: 
                  print "End of events"
                  break
       print "read=",event[0]
       p=event[1]
       h=event[2]
       print p.toString()
       # print h.toString()
print "No of processed events=",f.getEntries()
f.close()
