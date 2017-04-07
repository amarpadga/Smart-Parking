# Source: https://pymotw.com/2/socket/udp.html

import socket, sys, time, serial

ser = serial.Serial('/dev/ttyUSB0',9600)

textport = sys.argv[1]

rpi_ip_address = sys.argv[2]

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
port = int(textport)
server_address = (rpi_ip_address, port)
s.bind(server_address)

while True:

    print ("Waiting to receive on port %d : press Ctrl-C or Ctrl-Break to stop " % port)

    buf, address = s.recvfrom(port)
    if not len(buf):
        break
    print ("Received %s bytes from %s %s: " % (len(buf), address, buf ))
    ser.write(b'o')
s.shutdown(1)
