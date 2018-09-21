# ReserveGap

This provides a command line tool for comparing a new proposed reservation date range with a set of existing reservations.

### Inputs
* A list of valid campsites
* A list of existing reservations
* A date range for the new reservations
* The number of gap days making a site unavailable

The input file should be of this format:
```
{
  "search": {
    "startDate": "2018-06-04",
    "endDate": "2018-06-06"
  },
  "campsites": [
    {
      "id": 1,
      "name": "Cozy Cabin"
    },
    {
      "id": 2,
      "name": "Comfy Cabin"
    },
    {
      "id": 3,
      "name": "Rustic Cabin"
    },
    {
      "id": 4,
      "name": "Rickety Cabin"
    },
    {
      "id": 5,
      "name": "Cabin in the Woods"
    }
  ],
  "reservations": [
    {"campsiteId": 1, "startDate": "2018-06-01", "endDate": "2018-06-03"},
    {"campsiteId": 1, "startDate": "2018-06-08", "endDate": "2018-06-10"},
    {"campsiteId": 2, "startDate": "2018-06-01", "endDate": "2018-06-01"},
    {"campsiteId": 2, "startDate": "2018-06-02", "endDate": "2018-06-03"},
    {"campsiteId": 2, "startDate": "2018-06-07", "endDate": "2018-06-09"},
    {"campsiteId": 3, "startDate": "2018-06-01", "endDate": "2018-06-02"},
    {"campsiteId": 3, "startDate": "2018-06-08", "endDate": "2018-06-09"},
    {"campsiteId": 4, "startDate": "2018-06-07", "endDate": "2018-06-10"}
  ]
}
```

### Output
* The list of campsites that can accept this reservation
  * There are no conflicting reservations for that site
  * There are no reservations within the <gap> days of the proposed range

Output will look like this
```
Availble Camp Sites:
	Comfy Cabin
	Rickety Cabin
	Cabin in the Woods
```

## Building and Running
To compile into an executable jar use maven
```
$ mvn clean compile assembly:single
```

To run the jar
```
$ java -jar target/ReserveGap-0.0.1-SNAPSHOT-jar-with-dependencies.jar
usage: **** FileName Required
ReserveGap [options] fileName
 -g <gap>   Maximum gap in days to consider blocking (default 1)
 -h         This Help
$ _
```
To use the provided test file
```
$ java -jar target/ReserveGap-0.0.1-SNAPSHOT-jar-with-dependencies.jar src/test/resources/test-case.json
Availble Camp Sites:
	Comfy Cabin
	Rickety Cabin
	Cabin in the Woods
$ _
```
