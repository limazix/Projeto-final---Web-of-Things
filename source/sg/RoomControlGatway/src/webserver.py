# To change this template, choose Tools | Templates
# and open the template in the editor.

__author__="bruno"
__date__ ="$24/07/2011 15:59:26$"

#from wifimodule import get_response
#from wifimodule import wifi_server
#from wifimodule import *
import cgi
from threading import Thread
from os import curdir, sep
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
from bluetoothmodule import BluetoothMenager

devices = []

class MyHandler(BaseHTTPRequestHandler):

    def do_GET(self):
        try:
            if self.path.endswith(".apk"):
                f = open(curdir + sep + self.path,'rb')
                self.send_response(200)
                self.send_header('Content-type','application/zip')
                self.end_headers()
                self.wfile.write(f.read())
                f.close()
                return
            if self.path.endswith(".html"):
                f = open(curdir + sep + self.path)
                self.send_response(200)
                self.send_header('Content-type','text/html')
                self.end_headers()
                self.wfile.write(f.read())
                f.close()
                return
            if self.path == '/OnOff':   #our dynamic content
                self.send_response(200)
                self.send_header('Content-type','text/html')
                self.end_headers()
                if devices:
                    devices[0].send_msg('onoff')
                    self.wfile.write(devices[0].get_response())
                else:
                    self.wfile.write("Recurso indsponivel.")
                return
            if self.path == '/ChUp':   #our dynamic content
                self.send_response(200)
                self.send_header('Content-type','text/html')
                self.end_headers()
                if devices:
                    devices[0].send_msg('chup')
                    self.wfile.write(devices[0].get_response())
                else:
                    self.wfile.write("Recurso indsponivel.")
                return
            if self.path == '/ChDown':   #our dynamic content
                self.send_response(200)
                self.send_header('Content-type','text/html')
                self.end_headers()
                if devices:
                    devices[0].send_msg('chdown')
                    self.wfile.write(devices[0].get_response())
                else:
                    self.wfile.write("Recurso indsponivel.")
                return
            if self.path == '/VolUp':   #our dynamic content
                self.send_response(200)
                self.send_header('Content-type','text/html')
                self.end_headers()
                if devices:
                    devices[0].send_msg('volup')
                    self.wfile.write(devices[0].get_response())
                else:
                    self.wfile.write("Recurso indsponivel.")
                return
            if self.path == '/VolDown':   #our dynamic content
                self.send_response(200)
                self.send_header('Content-type','text/html')
                self.end_headers()
                if devices:
                    devices[0].send_msg('voldown')
                    self.wfile.write(devices[0].get_response())
                else:
                    self.wfile.write("Recurso indsponivel.")
                return

            return
                
        except IOError:
            self.send_error(404,'File Not Found: %s' % self.path)
     

    def do_POST(self):
        global rootnode
        try:
            ctype, pdict = cgi.parse_header(self.headers.getheader('content-type'))
            if ctype == 'multipart/form-data':
                query=cgi.parse_multipart(self.rfile, pdict)
            self.send_response(301)
            
            self.end_headers()
            upfilecontent = query.get('upfile')
            print "filecontent", upfilecontent[0]
            self.wfile.write("<HTML>POST OK.<BR><BR>");
            self.wfile.write(upfilecontent[0]);
            
        except :
            pass
            

class MyServer(Thread):
    def __init__(self):
        Thread.__init__(self)

    # sobrescrevendo o metodo run()
    def run(self):
        try:
            server = HTTPServer(('', 8081), MyHandler)
            print 'started httpserver...'
            server.serve_forever()
        except KeyboardInterrupt:
            print '^C received, shutting down server'
            server.socket.close()


def main():

    my_server = MyServer()
    my_server.start()
#    wifiServer = wifi_server()
#    wifiServer.start()
#    devices = wifiServer.get_device_list()
 #   bluetoothMenager = BluetoothMenager()
#    devices = bluetoothMenager.get_devices()

if __name__ == '__main__':
    main()