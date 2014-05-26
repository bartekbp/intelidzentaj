import httplib
import random
import string
import sys
import mimetypes
import urllib2
import httplib
import time
import re

def random_string(length):
    return ''.join(random.choice(string.letters) for ii in range(length + 1))


def encode_multipart_data(data, files, binary):
    boundary = random_string(30)

    def get_content_type(filename):
        return mimetypes.guess_type(filename)[0] or 'application/octet-stream'

    def encode_field(field_name):
        return ('--' + boundary,
                'Content-Disposition: form-data; name="%s"' % field_name,
                '', str(data[field_name]))

    def encode_file(field_name):
        filename = files[field_name]
        return ('--' + boundary,
                'Content-Disposition: form-data; name="%s"; filename="%s"' % (field_name, filename),
                'Content-Type: %s' % get_content_type(filename),
                '', open(filename, 'rb').read())
                
    def encode_binary(field_name):
        return ('--' + boundary,
                'Content-Disposition: form-data; name="%s"; filename="%s"' % (field_name, field_name),
                'Content-Type: image/jpeg',
                '', binary[field_name])        

    lines = []
    for name in data:
        lines.extend(encode_field(name))
    for name in files:
        lines.extend(encode_file(name))
    for name in binary:
        lines.extend(encode_binary(name))
        
    lines.extend(('--%s--' % boundary, ''))
    body = '\r\n'.join(lines)

    headers = {'content-type': 'multipart/form-data; boundary=' + boundary,
               'content-length': str(len(body))}

    return body, headers


def send_post(url, data, files, binary):
    req = urllib2.Request(url)
    connection = httplib.HTTPConnection(req.get_host())
    connection.request('POST', req.get_selector(),
                       *encode_multipart_data(data, files, binary))
    response = connection.getresponse()
    if response.status != 200:
        return "bad response code"
    return response.read()
    
    

class PersonRecognizerProxy(object):
    def __init__(self, address):
        self.address = address
        
    def recognize_person(self, image):
        return send_post(self.address, {}, {}, {"img":image})
        
        
"""
import personRecognizerProxy
personRec = personRecognizerProxy.PersonRecognizerProxy("")
"""
