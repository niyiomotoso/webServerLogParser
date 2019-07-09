
This is a Java Parser library in  that parses web server access log file, loads the log to MySQL and checks if a given IP makes more than a certain number of requests for the given duration. 


SAMPLE COMMAND


<br>
From project parent directory, enter:<br><br>
cd out/artifacts/parser_jar <br>

java -cp "parser.jar" Parser --accesslog="access.log" --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100 
