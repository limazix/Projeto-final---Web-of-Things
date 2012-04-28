#! /usr/bin/python

# To change this template, choose Tools | Templates
# and open the template in the editor.

__author__="bruno"
__date__ ="$22/11/2011 21:33:34$"

devices = []

class MyDevice():
    def __init__(self,sock,addr):
        self.addr = addr
        self.sock = sock
        self.listofservices = list()
        self.response = ""

    def send_msg(self,msg):
        self.sock.send(msg)
        wait(0.5)
        self.response = self.sock.recv(1024)
        
    def get_response(self):
        return self.response
    
    def disconect(self):
        self.sock.close()
        for d in devices:
            if d.addr == self.addr:
                devices.remove(d)
                break

    def append_service(self,service_name):
        self.listofservices.append(service_name)

    def has_service(self,service_name):
        if service_name in self.listofservices:
            return 'true'
        return 'false'

class wifi_server(Thread):
    def __init__(self):
        Thread.__init__(self)

    def get_device_list(self):
        return devices
    
    def run(self):
        serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        serversocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

        try:
            serversocket.bind(('localhost', 8082))
        except ValueError, e:
            print e
        serversocket.listen(5)
        print "waiting connection"
        while True:
            (clientSocket, address) = serversocket.accept()
            d = MyDevice(clientSocket,addr)
            devices.append(d)
