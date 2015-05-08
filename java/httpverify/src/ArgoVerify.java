import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.oauth.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

 
public class ArgoVerify {
    private static Log log = LogFactory.getLog(ArgoVerify.class);

    private static final long MILLISECS_PER_SEC = 1000L;
    private static final int OFFSET = 10;

    private static String AGENT_APPID = "UNk";
    private static String AGENT_SECRET = "igu";
    private final int maxRetry = 5;
    private static String phoneNumber=null;
    private static String serialNumber=null;
    private static String cloud=null;
    private Map<String,String> clouds = new HashMap<String,String>();

    public ArgoVerify() {
        clouds.put("staging-cn","https://staging.com/");
        clouds.put("prod-cn","https://svc.com/");
    }

    private String getIndigoHost() {
        return clouds.get(cloud);
    }

    private String getIndigoAppID() {
        return AGENT_APPID;
    }

    class DateWindow {
        long since;
        long till;

        public DateWindow() {
            Date yesterdaysDate = new Date();
            yesterdaysDate.setTime(System.currentTimeMillis() - 86400000);

            long twoWeeks = 14 * 86400000;
            Date dateFrom = new Date(yesterdaysDate.getTime() - twoWeeks);
            since = dateFrom.getTime();
            till = yesterdaysDate.getTime();
        }
    }

    private boolean execute(String url, JsonObject searchCriteria, String method) {
        DefaultHttpClient client = new DefaultHttpClient();

        HttpParams params = client.getParams();
//        HttpConnectionParams.setConnectionTimeout(params, 600);
//        HttpConnectionParams.setSoTimeout(params, 600);


        HttpRequestBase httpRequest = null;
        if ("POST".equals(method)) {
            log.debug("POST:" + url);
            httpRequest = new HttpPost(url);
        } else {
            log.debug("GET:" + url);
            httpRequest = new HttpGet(url);
        }

        try {
            OAuthMessage oauthMessage = computeOAuthRequest(method, url);
            httpRequest.addHeader("Authorization", oauthMessage.getAuthorizationHeader(""));

            if (httpRequest instanceof HttpPost) {
                StringEntity se = new StringEntity(searchCriteria.toString());
                ((HttpPost) httpRequest).setEntity(se);
            }
            httpRequest.setHeader("Content-Type", "application/json");


            HttpResponse response = null;
            try {
                response = client.execute(httpRequest);
            } catch (IOException e) {
                log.error(e);
            }
            int retry = 0;
            //log.info(response.getStatusLine().getStatusCode());
            while (response == null
                    && ++retry < maxRetry) {

                try {
                    response = client.execute(httpRequest);
                } catch (IOException e) {
                    log.error(e);
                }
            }
            if (retry > 0) {
                log.debug("retry:" + retry);
            }

            if (response != null) {
                log.debug(String.format("%d,%s", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
//                log.debug(String.format("%s", EntityUtils.toString(response.getEntity())));
                response.getEntity();
                return false;
            } else {
                log.error("Nothing got");
                return true;
            }
        } catch (Exception e) {
            log.error(e);
            return true;
        }

/*
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity());
            JsonObject jo = new JsonParser().parse(result).getAsJsonObject();
            return jo.has("deviceGuids") ? jo.getAsJsonArray("deviceGuids") : jo.has("deviceGuidsList") ? jo.getAsJsonArray("deviceGuidsList") : null;
        }

        return null;
*/

    }


    private OAuthMessage computeOAuthRequest(final String methodType, final String url) throws IOException, OAuthException, URISyntaxException {

        // set the consumer key to the accountid
        OAuthConsumer consumer = new OAuthConsumer(null, AGENT_APPID, AGENT_SECRET, null);
        OAuthAccessor accessor = new OAuthAccessor(consumer);
        String authToken = "" + System.currentTimeMillis() / MILLISECS_PER_SEC;

        accessor.accessToken = authToken;

        accessor.requestToken = null;
        accessor.tokenSecret = null;

        OAuthMessage oAuthRequest = new OAuthMessage(methodType, url, null);
        oAuthRequest.addParameter(OAuth.OAUTH_TIMESTAMP,
                String.valueOf((System.currentTimeMillis() / MILLISECS_PER_SEC) + OFFSET));
        oAuthRequest.addRequiredParameters(accessor);
        return oAuthRequest;
    }

    public static String getPhoneNumber() {
        return phoneNumber;
    }

    public static String getSerialNumber() {
        return serialNumber;
    }

    boolean findDevices() {
        String url = getIndigoHost()+"v1/dp/devices/find.json?appid=UNM3VNYQNPFDBTJFFL4LYYQL7RLYEQGK";
//        String searchCriteria = "{\"criteria\":[{\"name\":\"deviceid\",\"value\":\"2288171958295126016\"}]}";
//        String searchCriteria = "{\"criteria\":[{\"name\":\"phoneNumber\",\"value\":\""+getPhoneNumber()+"\"}]}";
        String searchCriteria = "{\"criteria\":[{\"name\":\"serialNumber\",\"value\":\""+getSerialNumber()+"\"}]}";

        return execute(url, new JsonParser().parse(searchCriteria).getAsJsonObject(), "POST");

    }


    boolean initData(String serialNumber) {

        DateWindow dw = new DateWindow();
        String url = getIndigoHost() + "v1/gmc/device/init.json/" + serialNumber + "?appid=" + getIndigoAppID() + "&since=" + dw.since + "&till=" + dw.till;
        return execute(url, null, "GET");
    }

    boolean getAnalyticsData(String serialNumber) {
        DateWindow dw = new DateWindow();
        String url = getIndigoHost() + "v1/gmc/device/charts.json/" + serialNumber + "?appid=" + getIndigoAppID() + "&since=" + dw.since + "&till=" + dw.till;
        return execute(url, null, "GET");
    }

    void executeDataRequest() {
        log.debug("----job started----");

        long tmstart = 0;
        tmstart = System.currentTimeMillis();

        boolean findDeviceError = findDevices();

        long findDeviceTimeCost = System.currentTimeMillis() - tmstart;

        tmstart = System.currentTimeMillis();
        boolean initDataError =initData(getSerialNumber());

        long initDataTimeCost = System.currentTimeMillis() - tmstart;

        tmstart = System.currentTimeMillis();

        boolean getAnalyticsDataError =  getAnalyticsData(getSerialNumber());

        long getAnalyticsDataTimeCost = System.currentTimeMillis() - tmstart;

        log.info(String.format("TIMEMETRIC,%s=%d,%s=%d,%s=%d,%s,%s,%s", "findDeviceTimeCost", findDeviceTimeCost,
                "initDataTimeCost", initDataTimeCost,
                "getAnalyticsDataTimeCost", getAnalyticsDataTimeCost,findDeviceError,initDataError,getAnalyticsDataError));

        log.debug("----job completed----");
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            executeDataRequest();
//            System.out.println(",,,,,,,,,");
        }
    };

    public void doCheckJob() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 3, 60 * 1000 * 5);
    }


    public static void main(String args[]) {
        ArgoVerify argo = new ArgoVerify();

        String url = "";

        try {

            if(args.length<2){
                log.error("cloud(staging-cn,prod-cn) and serialnumber should be given");
            }
            else{
                cloud = args[0];
                serialNumber = args[1];
                //argo.executeDataRequest();
                argo.doCheckJob();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
