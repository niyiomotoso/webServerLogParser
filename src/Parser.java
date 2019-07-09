
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;

public class Parser {
    private static HashMap<String, String> argumentParams = new HashMap<>();

    private static HashMap<String, ArrayList<HashMap<String, String>>> filteredIpAddresses = new HashMap<>();
    private static ArrayList<HashMap<String, String>> ipList = new ArrayList<>();
    private static long startDateLong = 0;
    private static long startDatePlusDurationLong = 0;

    private static String startDate = null;
    private static String threshold = null;
    private static String duration = null;
    private static String logFileLocation = null;
    public static void main(String [] args) throws Exception {

        InputStream inputS = null;
        Reader reader = null;
        BufferedReader bufferedReader = null; // buffered for readLine()
        try {

            getArgumentKeyValue(args);

            startDate = argumentParams.get("startDate");
            threshold = argumentParams.get("threshold");
            duration = argumentParams.get("duration");
            logFileLocation = argumentParams.get("accesslog");

            if(logFileLocation == null ) {
                System.out.println("Incomplete parameters supplied, access.log path not specified");
                System.exit(1);
            }else  if(startDate == null){
                System.out.println("Incomplete parameters supplied, startDate not specfied");
                System.exit(1);
            }else  if( threshold ==null  ){
                System.out.println("Incomplete parameters supplied, threshold not specified");
                System.exit(1);
            }else if(duration == null ){
                System.out.println("Incomplete parameters supplied, duration not specified");
                System.exit(1);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");

            Date startDate_AsDate = new Date();
            try {
                startDate_AsDate = sdf.parse(startDate);
            } catch (ParseException ex) {
                java.util.logging.Logger.getLogger(Parser.class.getName()).log(Level.ALL.SEVERE, null, ex);
            }

            startDateLong = startDate_AsDate.getTime();

            if(duration.equals("daily")){
                startDatePlusDurationLong = startDateLong + (24*60*60*1000); //plus 24 hours


            }else if(duration.equals("hourly")) {
                startDatePlusDurationLong = startDateLong + (1*60*60*1000); //plus 1 hour
            }

            String s;
            inputS = new FileInputStream(logFileLocation);
            reader = new InputStreamReader(inputS, "UTF-8"); //  charset set to default
            bufferedReader = new BufferedReader(reader);

            while ((s = bufferedReader.readLine()) != null) {
                String [] lineText = s.split("\\|"); //split each line statement by the regex | to get the dates and message

                String date = lineText[0];
                String ipAddress = lineText[1];
                String restMethod = lineText[2];
                String responseCode = lineText[3];
                String source = lineText[4];

                HashMap<String, String> payload = new HashMap<>();
                payload.put("Date", date);
                payload.put("ipAddress", ipAddress);
                payload.put("Status", responseCode);
                payload.put("UserAgent", source);
                payload.put("Request", restMethod);

                ipList.add(payload);

                if(applyDurationFilter(payload)){
                    addToIpFile(ipAddress, payload);
                }
            }

            applyThresholdFilter(threshold);

            MySQLHub hb = new MySQLHub();
            hb.BatchInsertLogIntoDB(ipList);

            }
        catch (Exception e)
        {
            System.err.println(e.getMessage()); // handle exception
        }
        finally {
            if (bufferedReader != null) { try { bufferedReader.close(); } catch(Throwable t) { /* ensure close happens */ } }
            if (reader != null) { try { reader.close(); } catch(Throwable t) { /* ensure close happens */ } }
            if (inputS != null) { try { inputS.close(); } catch(Throwable t) { /* ensure close happens */ } }
        }

    }

    private static void applyThresholdFilter(String threshold)  throws Exception  {

        for (Map.Entry<String, ArrayList<HashMap<String, String>>> entry : filteredIpAddresses.entrySet() ) {
            String key = entry.getKey();
            ArrayList<HashMap<String, String>> ipLogs = entry.getValue();
            if(ipLogs.size() >= Integer.valueOf(threshold)){
                System.out.println("found ip address  "+key);
                HashMap<String, String> ipMap = new HashMap<>();
                ipMap.put("ip", key);
                ipMap.put("startDate", startDate);
                ipMap.put("threshold", threshold);
                ipMap.put("duration", duration);

                MySQLHub hb = new MySQLHub();
                hb.SingleInsertIpIntoDB(ipMap);


            }
        }
    }

    private static boolean applyDurationFilter(HashMap<String, String> payload) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Date dateInView = new Date();
        try {
            dateInView = sdf.parse(payload.get("Date"));
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(Parser.class.getName()).log(Level.ALL.SEVERE, null, ex);
        }

        if(  dateInView.getTime() >= startDateLong &&   dateInView.getTime() <=  startDatePlusDurationLong ){
            return true;
        }
        else{
            return false;
        }


    }

    private static void getArgumentKeyValue(String [] args) {
        for (String arg:  args) {
            String [] arg_array = arg.split("=");
            argumentParams.put(arg_array[0].substring(2), arg_array[1]);
            argumentParams.put(arg_array[0].substring(2), arg_array[1]);

        }

    }

    private static void addToIpFile(String ipAddress, HashMap<String, String> payload){
        if(filteredIpAddresses.containsKey(ipAddress)){
            ArrayList<HashMap<String, String>> ipLogs = filteredIpAddresses.get(ipAddress);
            ipLogs.add(payload);
            filteredIpAddresses.replace(ipAddress, ipLogs);
        }
        else{
            ArrayList<HashMap<String, String>> ipLogs = new ArrayList<>();
            ipLogs.add(payload);
            filteredIpAddresses.put(ipAddress, ipLogs);
        }
    }



}
