'''
This program is connected serially to an Arduino and reads values from the IR sensors connected
to the Arduino. Once an IR value has been read, the program updates the parking spot information
on the server regarding the avaialability of the parking spot. The RPi running this program
will be connected to the Arduino responsible for monitoring the IR sensors of the reservable
parking spots.
'''
import urllib2
import json
import serial
import time

#Alternate serial ports depending on which Arduino the RPi is connected to
#ser = serial.Serial('/dev/ttyUSB0',9600)
ser1 = serial.Serial('/dev/ttyACM0',9600)

#Initializing global variables that will be used for debouncing
previous_value = ''
previous_time = 0
previous_value1 = ''
previous_time1 = 0
init_time = 0
contents = json.loads(urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/parking_spots/").read())
#contents = urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/parking_spots").read()

parking_lot = {}

# This method gets the parking space availability status of the passed parking space parameter
def get_space_info(space_name):
	return json.loads(urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/parking_spots/" + parking_lot[space_name]).read())

# This method returns the not of the passed in boolean 
def invert_status(status):
	if(status):
		return False
	else:
		return True

# This method updates the parking spot with the passed in JSON parking spot parameter
def update_parking_space(parking_space_json):
        parking_space = {'occupied': parking_space_json['occupied']}
        print parking_space
        open_connection = urllib2.build_opener(urllib2.HTTPHandler)
		

# Making a request to get all the parking spaces and store their IDs in a dictionary with the parking
# space name as the key-identifier
request = urllib2.Request('http://smart-parking-bruck.c9users.io:8081/parking_spots/' + parking_space_json["id"]["$oid"], data= str(json.dumps(parking_space)))
request.add_header('Content-Type', 'application/json')
request.get_method = lambda: 'PUT'
url = open_connection.open(request)	

for space in contents:
	parking_lot[space["name"]] = space["id"]["$oid"]

count = 0

while True:      
	value1 = ser1.readline()[:-2]
        if value1 is 'o':
            continue
        value1 = int(value1) + 1 
        print value1
        count = count + 1
        current_time1=time.time()
		
		# if statement used as a debounce to prevent multiple readings to come in from the same sensor
        if(count > 8 and not(value1 == previous_value1 and (current_time1 - previous_time1) < 0.5)):
                changed_parking_space = get_space_info("R"+str(value1))
                changed_parking_space["occupied"] = invert_status(changed_parking_space["occupied"])
                print ('R' + str(value1))

                update_parking_space(changed_parking_space)
                previous_time1 = current_time1
                previous_value1 = value1
