Take Home Challenge
===================
Anvyl coding challenge for processing log files.

##Building the project
```
scalac src/main/scala/*.scala
```

##Running the program
```
scala ReadLogs
```

##Sample Output
```
Please enter a date: 2017-07-20
Max time: 0.095991 secs
Average time: 0.093119 secs
Path: /orders.html
	Code: 200: 535
	Code: 404: 33
	Code: 503: 20
Path: /orders.php
	Code: 200: 91
	Code: 404: 4
	Code: 503: 7
```