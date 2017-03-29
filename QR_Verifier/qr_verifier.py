'''
This program uses a webcam to continuously scan QR codes and determines if
the QR code is in the list of reservations. The program uses ZBar to interpret
QR codes
'''

#!/usr/bin/python

from sys import argv
import zbar
import json
import urllib2

proc = zbar.Processor()
proc.parse_config('enable')
device = '/dev/video0'
proc.init(device,True)

expired_key = "expired"

proc.visible = True

prev_value = ''
while(1):
        proc.process_one()
        for symbol in proc.results:
                if(prev_value!=symbol.data):
                        try:
                                url = "http://smart-parking-bruck.c9users.io:8081/reservations/" + symbol.data
								contents = json.loads(urllib2.urlopen(url).read())

       	                        if(contents[expired_key] == False):
                                        print "Gate opens"
                                else:
                                        print "Gate stays closed"

                        except urllib2.HTTPError:
				# If the server is down or if the QR code is not valid, the gate would remain closed
				# and no action is expected to occur
                                print "The server is down or the reservation does not exist"

                        prev_value = symbol.data






