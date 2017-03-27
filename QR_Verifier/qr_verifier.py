'''
This program uses a webcam to continuously scan QR codes and determines if
the QR code is in the list of reservations. The program uses ZBar to interpret
QR codes
'''

#!/usr/bin/python

from sys import argv
import zbarcam

reservation_id = "$oid"

proc = zbar.Processor()
proc.parse_config('enable')
device = '/dev/video0'
proc.init(device, True)

proc.visible = True

prev_value = ''
while(1):
        proc.process_one()
        for symbol in proc.results:
                if(prev_value!=symbol.data):
                        contents = json.loads(urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/reservations/").read())

                        for reservation in contents:
                                if(reservation[reservation_id] == symbol.data):
                                        # TODO: open gate
                                        break;

                        prev_value = symbol.data



