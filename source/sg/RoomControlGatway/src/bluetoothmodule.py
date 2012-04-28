# To change this template, choose Tools | Templates
# and open the template in the editor.

__author__ = "bruno"
__date__ = "$27/07/2011 16:45:44$"

import bluetooth
import select
#from threading import Thread

devices = []

#model
class MyDivice():
    def __init__(self,name,sock):
        self.name = name
        self.sock = sock
        self.listofservices = list()

    def send_msg(self,msg):
        self.sock.send(msg)
        data = self.sock.recv(1024)
        print data

    def disconect(self):
        self.sock.close()
        for d in devices:
            if d.name == self.name:
                devices.remove(d)
                break

    def append_service(self,service_name):
        self.listofservices.append(service_name)
        
    def has_service(self,service_name):
        if service_name in self.listofservices:
            return 'true'
        return 'false'

#class MyDiscoverer(Thread):
#    def __init__(self):
#        Thread.__init__(self)
#
#    def run(self):
#        discovered_devices = discover_devices()
#        for address in discovered_devices:
#            sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
#            sock.connect((address, 8082))
#            device = Device(lookup_name(address),sock)
#            devices.append(device)
#            print lookup_name(address) + " => " + address
#        if devices.isEmpty():
#            print "No Device Found"

class MyDiscoverer(bluetooth.DeviceDiscoverer):

    def pre_inquiry(self):
        self.done = False

    def device_discovered(self, address, device_class, name):
#            sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
#            sock.connect((address, 2))
#            device = Device(address,sock)
#            devices.append(device)
            print "%s - %s" % (address, name)

    def inquiry_complete(self):
        self.done = True

#server
#class BluetoothServer(Thread):
#    def __init__(self):
#        Thread.__init__(self)
#
#    def run(self):
#
#        server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
#
#        port = 1
#        server_sock.bind(("", port))
#        server_sock.listen(1)
#
#        client_sock, address = server_sock.accept()
#        print "Accepted connection from ", address
#
#        data = client_sock.recv(1024)
#        print "received [%s]" % data
#
#        client_sock.close()
#        server_sock.close()


class BluetoothMenager():
    def __init__(self):
        d = MyDiscoverer()
        d.find_devices(lookup_names = True)

        readfiles = [ d, ]

        while True:
            rfds = select.select( readfiles, [], [] )[0]

            if d in rfds:
                d.process_event()

            if d.done: break

#        if devices.isEmpty():
#            print "No Device Found"

#        print "Star discovery"
#        d = MyDiscoverer()
#        d.start()

    def get_devices(self):
        print "getting device list"
        return devices