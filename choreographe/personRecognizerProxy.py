import struct
import socket
import logging

class PersonRecognizerProxy(object):
    def __init__(self, address):
        self.address = address
        
    def recognize_person(self, image):
        logging.debug("address: " + str(self.address))
        person = "unknown"
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            ip, port = self.address.split(":")
            logging.debug("connecting to server at: " + str(ip) + " " + str(port))
            s.connect((ip, int(port)))
            msg = struct.pack('>I', len(image)) + image
            s.sendall(msg)
            logging.info("image sent")
            person = s.recv(1024)
            logging.debug("server response: " + str(person))
        finally:
            s.close()
        return person
        
"""
import personRecognizerProxy
personRec = personRecognizerProxy.PersonRecognizerProxy("")
"""