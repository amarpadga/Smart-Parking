'''
This program is used to signal the RPi connected to the Arduino to open the gate once the button
is pressed by the user
'''
import pifacedigitalio as pfio
import socket
from time import sleep
from sys import argv

# get the port and host address of the RPi connected to the servo motor
port = sys.argv[1]
host = sys.argv[2]

button = 0

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
server_address = (host, port)

pfio.init()
while(1):
		# If the button is pressed, open the gate
        if pfio.digital_read(button):
            print("here")
            sleep(1)
            s.sendto(("O").encode('utf-8'), server_address)
          
s.shutdown(1)
