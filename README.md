# HttpTest

ip and port you can fand in the application

## GET / - returns metadata

fields in response:
"services" - array of available services
"start" - when the server is launched

example:
{
  "services": [
    {
      "name": "status",
      "uri": "http://192.168.1.189:10000/status"
    },
    {
      "name": "log",
      "uri": "http://192.168.1.189:10000/log"
    }
  ],
  "start": "2023-06-08T01:48:44+02:00"
}

## GET /status - returns information about ongoing call

fields in response:
"name" - name from contacts
"number" - target phone number
"ongoing" - equals true if the call is active

example:
{
  "name": "",
  "number": "",
  "ongoing": false
}

## GET /log - returns information about previous calls in active session

fields in response:
"beginning" - when the call is happend
"duration" - duration of the call in seconds
"name" - name from contacts
"number" - target phone number
"timesQueried" - indicates how many times this record was fetched

example:
[
  {
    "beginning": "2022-02-16T09:26:13+01:00",
    "duration": "0",
    "name": "Tom",
    "number": "+48123123123",
    "timesQueried": 0
  },
  {
    "beginning": "2022-02-16T10:56:51+01:00",
    "duration": "284",
    "name": "Jack",
    "number": "+48123123123",
    "timesQueried": 0
  }
]
