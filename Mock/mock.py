import urllib2
import json

def get_parking_spots():
	contents = json.loads(urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/parking_spots?name=P1").read())
	if(contents[0]["name"] != "P1"):
		print "Test failed: get_parking_spots()"
	else:
		print "Test passed: get_parking_spots()"		

def get_parking_lot():
        contents = json.loads(urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/parking_lots").read())
        if(len(contents) != 0):
                print "Test failed: get_parking_lots()"
        else:
                print "Test passed: get_parking_lots()"

def user_sign_in():
        contents = json.loads(urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/users?email=bruckwendu80@gmail.com").read())
        if(contents[0]["username"] != "bruck"):
                print "Test failed: get_parking_spots()"
        else:
                print "Test passed: get_parking_spots()"

def update_parking_space():
	contents = json.loads(urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/parking_spots?name=P1").read())
	contents[0]['occupied'] = True
	parking_space = contents[0]
        open_connection = urllib2.build_opener(urllib2.HTTPHandler)
	request = urllib2.Request('http://smart-parking-bruck.c9users.io:8081/parking_spots/'+contents[0]["id"]["$oid"], data=str(json.dumps(parking_space)))
        request.add_header('Content-Type', 'application/json')
        request.get_method = lambda: 'PUT'
        url = open_connection.open(request)     

        contents = json.loads(urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/parking_spots?name=P1").read())
        if(contents[0]['occupied'] != True):
                print "Test failed: update_parking_space()"
        else:
		parking_space = {'occupied': False}
		url = open_connection.open(request)
      		contents = json.loads(urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/parking_spots?name=P1").read())
	        if(contents[0]["occupied"] != False):
			print "Test failed: update_parking_spots()"
       		else:
               		 print "Test passed: update_parking_spots()"


def update_reserved_parking_info():
        contents = json.loads(urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/parking_spots?name=R1").read())
        contents[0]['occupied'] = True
        parking_space = contents[0]
        open_connection = urllib2.build_opener(urllib2.HTTPHandler)
        request = urllib2.Request('http://smart-parking-bruck.c9users.io:8081/parking_spots/'+contents[0]["id"]["$oid"], data=str(json.dumps(parking_space)))

        request.add_header('Content-Type', 'application/json')
        request.get_method = lambda: 'PUT'
        url = open_connection.open(request)     


        contents = json.loads(urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/parking_spots?name=P1").read())
        if(contents[0]['occupied'] != True):
                print "Test failed: update_reserved_parking_info()"
        else:
                parking_space = {'occupied': False}
                url = open_connection.open(request)
                contents = json.loads(urllib2.urlopen("http://smart-parking-bruck.c9users.io:8081/parking_spots?name=P1").read())
                if(contents[0]["occupied"] != False):
                        print "Test failed: update_reserved_parking_info()"
                else:
                         print "Test passed: update_reserved_parking_info()"



get_parking_spots()
get_parking_lot()
update_parking_space()
update_reserved_parking_info()

