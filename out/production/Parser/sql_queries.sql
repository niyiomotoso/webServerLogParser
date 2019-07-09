

-- TEST 1: MySQL query to find IPs that mode more than a certain number of requests for a given time period.
SELECT ip from parser.general_ip_logs  WHERE date >= '2017-01-01.13:00:00' and date <= '2017-01-01.14:00:00' GROUP BY ip
HAVING  COUNT(ip) > 100 ORDER BY ip;


-- TEST 2: Write MySQL query to find requests made by a given IP.
SELECT * from general_ip_logs where ip = "192.168.77.101";
