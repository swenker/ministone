import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class CheckNetwork {
    private static Log logger = LogFactory.getLog(CheckNetwork.class);
    private static String dbhost="dbserver";

    final String driverClass = "com.mysql.jdbc.Driver";

    final String url = "jdbc:mysql://%s:3306/spfw_care";
    final String username = "care_admin";
    private static String password = "";

    public static void main(String args[]) {
        dbhost = args[0];
        if(args.length>1)
            password = args[1];

        logger.info("dbhost:"+dbhost);
        CheckNetwork checkNetwork = new CheckNetwork();
        checkNetwork.doCheckJob();
//        checkNetwork.checkDBQuery();
    }


    Connection getConnection() {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            return DriverManager.getConnection(String.format(url,dbhost), username, password);
        } catch (SQLException e) {
            logger.error("",e);
            return null;
        }
    }


    synchronized  void checkDBQuery() {
        logger.info("----job started----");
        long tmstart = System.currentTimeMillis();
        Connection dbcon = getConnection();
        long tmcost = System.currentTimeMillis()-tmstart;
        String infoPattern = "TIMEMETRIC: %s costs %d";
        logger.info(String.format(infoPattern,"get-connection",tmcost));

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        if (dbcon == null) {
            logger.warn("failed to get db connection");
        } else {

            try {
                tmstart = System.currentTimeMillis();

                pstmt = dbcon.prepareStatement("select id from spfw_domains order by id limit 1");
                rs = pstmt.executeQuery();



                if (rs.next()) {
                    int id = rs.getInt("id");

                }

                tmcost = System.currentTimeMillis()-tmstart;

                logger.info(String.format(infoPattern,"get-result",tmcost));

            } catch (SQLException e) {
                logger.error("query",e);
            } finally {
                try {
                    if(rs!=null) rs.close();
                } catch (SQLException e) {

                }
                try {
                    if(pstmt!=null) pstmt.close();
                } catch (SQLException e) {

                }

            }


            try{
                dbcon.close();
            }catch (SQLException e){

            }
        }
        logger.info("----job completed----");
    }

    public void doCheckJob() {
        Timer timer = new Timer();
//        timer.schedule(timerTask,3000,60*1000*5);
        timer.scheduleAtFixedRate(timerTask,3,60*1000*5);


    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            checkDBQuery();
//            System.out.println(",,,,,,,,,");
        }
    };
}
