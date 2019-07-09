CREATE DATABASE IF NOT EXISTS parser;
USE parser;

CREATE TABLE IF NOT EXISTS general_ip_logs
             (id integer primary key AUTO_INCREMENT,
              ip VARCHAR(50) DEFAULT NULL,
              status INT DEFAULT NULL ,
              request VARCHAR(50) DEFAULT NULL,
              date TIMESTAMP,
              user_Agent VARCHAR(200) DEFAULT NULL
          )


CREATE TABLE IF NOT EXISTS filtered_ip_addresses
             (id integer primary key AUTO_INCREMENT,
              ip VARCHAR(50) DEFAULT NULL,
              startDate VARCHAR (50) DEFAULT NULL ,
              duration VARCHAR(10) DEFAULT NULL,
              threshold INT DEFAULT NULL,
              comment VARCHAR(200) DEFAULT NULL
              createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
          )

