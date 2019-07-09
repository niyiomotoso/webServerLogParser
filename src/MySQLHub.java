

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


public class MySQLHub {
        private Connection connect = null;
        private Statement statement = null;
        private PreparedStatement preparedStatement = null;
        private ResultSet resultSet = null;
    public static void main(String [] args) throws Exception {
        System.out.println("200");
    }
        public MySQLHub(){
            // To load the MySQL driver
            try {

            Class.forName("com.mysql.jdbc.Driver");
            // Setting up the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/parser?"
                            + "user=root&password=");
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        public void BatchInsertLogIntoDB( ArrayList<HashMap<String, String>> ipList) throws Exception {
            try {
                System.out.println("Initiating Batch-Insert to DB for ALL logs in the log file");
                // Statements to issue SQL queries to the database
                statement = connect.createStatement();

                String insertSql = "insert into  parser.general_ip_logs values (default ,?, ?, ?, ?,?)";
                PreparedStatement preparedStatement = connect.prepareStatement(insertSql);

                for(int i =0; i< ipList.size(); i++){

                }

                for (HashMap<String, String> payload : ipList) {
                    preparedStatement.setString(1, payload.get("ipAddress"));
                    preparedStatement.setString(2, payload.get("Status"));
                    preparedStatement.setString(3, payload.get("Request"));
                    preparedStatement.setString(4, payload.get("Date"));
                    preparedStatement.setString(5, payload.get("UserAgent"));
                    preparedStatement.addBatch();
                }

                System.out.println("Started Batch-Insert to DB for ALL logs in the log file ...");
                preparedStatement.executeBatch();
                System.out.println("Finished Batch-Insert to DB for ALL logs in the log file!");

            } catch (Exception e) {
                throw e;
            } finally {
                close();
            }

        }

    public void SingleInsertIpIntoDB( HashMap<String, String> ipMap) throws Exception {
        try {

           statement = connect.createStatement();
            String comment = "";

            if(ipMap.get("duration").equals("daily")){
                comment = "blocked for sending " + ipMap.get("threshold")+" requests in one day" ;

            }else if(ipMap.get("duration").equals("hourly")) {
                comment = "blocked for sending " + ipMap.get("threshold")+" requests in one hour" ;
            }

          String insertSql = "insert into  parser.filtered_ip_addresses values (default ,?, ?, ?, ?,?, default )";
            PreparedStatement preparedStatement = connect.prepareStatement(insertSql);

                preparedStatement.setString(1, ipMap.get("ip"));
                preparedStatement.setString(2, ipMap.get("startDate"));
                preparedStatement.setString(3, ipMap.get("duration"));
                preparedStatement.setString(4, ipMap.get("threshold"));
                preparedStatement.setString(5, comment);

            preparedStatement.executeUpdate();
            System.out.println("Inserted filtered IP "+ipMap.get("ip") + " into filtered_ip_addresses table with comment");



        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }

    }

        // closing the resultSet
        private void close() {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (statement != null) {
                    statement.close();
                }

                if (connect != null) {
                    connect.close();
                }
            } catch (Exception e) {

            }
        }



    }

