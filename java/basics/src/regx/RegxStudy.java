package regx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wenjusun on 2/25/2016.
 */
public class RegxStudy {

    public static void main(String args[]){
        String patterns ="^.*(oss.svcmot.cn|local|dev.sdc2wide.blurdev.com|qa.blurdev.com|gss1c.gsrv.svcmot.com|gss2c.gsrv.svcmot.com|gss4c.gsrv.svcmot.com|gss5a.gsrv.svcmot.com|sdc.blurdev.com|sdc200.blurdev.com|gss.gce.google.com|va3.svcmot.com|sdc200.pp.svcmot.com|chinacloudapp.cn|d-mmi|s-mmi|p-mmi).*$";

//        String hostname="ind01-s.novalocal";
//        String hostname="prx01-ind-s-mmi";
        String hostname="ind001-indigo.oss.svcmot.cn";
        Pattern pattern = Pattern.compile(patterns);
        System.out.println(pattern.pattern());

        Matcher matcher = pattern.matcher(hostname);

        if(matcher.matches()){
            System.out.println(matcher.groupCount());
            System.out.println(matcher.group(1));
        }
        else
        {
            System.out.println("No Matches");
        }

    }
}
