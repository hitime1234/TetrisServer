import os
def Store():
    global urlADD
    os.system("java -jar TetrisServer.jar " + urlADD.get())

class windowmanage:
    def Close():
        global urlADD,Error
        if urlADD.get().isspace() != True and urlADD.get() != "" and urlADD.get().isnumeric():
            root.destroy()
            Store()
        else:
            Error.config(text = "error: Port number is not vaild")
            
    
    def createwindow(header,title1,size):
        global result,heading
        root = Tk()
        try:
            window_size = str(size)
        except:
            window_size = "500x500"
        root.title(str(title1))
        root.geometry(window_size)
        heading = Label(root,
                        text=str(header),
                        font=("arial",12,"bold"),fg = "black")
        heading.pack()
        return root
    
    def textBoxHandler(top):
        Urlenter = Label(top, text="enter port number: ",font = ("arial",11,"bold"),fg="black")
        Urlenter.place(x=10,y=100)
        hold = Label(top, text="",font = ("arial",14,"bold"),fg="red")
        hold.place(x=10,y=150)
        global urlADD
        urlADD = StringVar()
        entry_box = Entry(top, textvariable=urlADD,width=25,bg="light blue").place(x=50,y=120)
        return hold
        
        
         

    

    def createbuttons(window):

        #enter = Button(window,text="Start",width=20,height=3,bg="lightblue",
         #            command=lambda:
          #           Store(window)).place(x=174,y=425)        
                        
        enter = Button(window,text="Start",width=20,height=3,bg="lightblue", command=windowmanage.Close).place(x=124,y=225)



    
from tkinter import *
import time    
from requests import get
try:
    ip = get('https://api.ipify.org',verify=True).text
except:
    try:
        ip = get('https://api.ipify.org',verify=False).text
    except:
        ip = '0.0.0.0'
print('My public IP address is: {}'.format(ip))
global Error
import socket
hostname = socket. gethostname()
root = windowmanage.createwindow("Tetris Networking server,\n\nLocal Server ip: " + socket. gethostbyname(hostname) + ",\n" + 'Server public IP address is: {}'.format(ip),"Networking Tetris Server","400x300")
windowmanage.createbuttons(root)
Error = windowmanage.textBoxHandler(root)
root.mainloop()
