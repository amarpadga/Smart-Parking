'''
This program uses a webcam to continuously scan QR codes and determines if
the QR code is in the list of reservations. The program uses ZBar to interpret
QR codes
'''

#!/usr/bin/python

from sys import argv
import socket
import time
import zbar
import json
import urllib2

host = "10.0.0.41"
port = 1235

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
server_address = (host, port)

proc = zbar.Processor()
proc.parse_config('enable')
device = '/dev/video0'
proc.init(device,True)

reservation_id = "$oid"
expired_key = "expired"

proc.visible = True
prev_time = 0
prev_value = ''
while(1):
        proc.process_one()
        for symbol in proc.results:
		current_time = time.time()
                if(prev_value!=symbol.data or (current_time-prev_time)>10):
			prev_time = current_time
			try:
				url = "http://smart-parking-bruck.c9users.io:8081/reservations/" + symbol.data
	                        contents = json.loads(urllib2.urlopen(url).read())

				if(contents[expired_key] == False):
					print "Gate opens"
					s.sendto(("O").encode('utf-8'), server_address)
				else:
					print "Gate stays closed"

			except urllib2.HTTPError:
			        print "There was a communication error with the server"
			except urllib2.URLError:
				print "The reservation does not exist"

                        prev_value = symbol.data
s.shutdown(1)
