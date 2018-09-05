/**
 * Created by steven on 2017/3/18.
 */
package sample;
import java.net.URLEncoder;
public class ConfigInform {
    static final String urlTrain = "http://www.chepiao100.com/api/yupiao";
    static final String startTrainRequestPost ="startStation";
    static final String arriveTrainRequestPost = "arriveStation";
    //配置userid
    static final  String userID="1234567890@qq.com";
    //配置秘钥
    static final String key="3ca44f554837c5b7cd92c20f0570739c";

    //得到机票的网站
    private String urlFlght;
    //出发日期
    private String date;
    //出发站
    private String startStation;
    //到站
    private String arriveStation;

    //网站url
    public String getUrlFlght() throws Exception {
        if(urlFlght==null)
            throw new Exception("url is not initial");
        return urlFlght;
    }

    public void setUrlFlght(String startStation,String arriveStation,String date,int startNumber) throws Exception {

        this.urlFlght= "https://m.flight.qunar.com/ncs/api/domestic/flightlist?depCity=" + URLEncoder.encode(startStation, "UTF-8") + "&arrCity=" + URLEncoder.encode(arriveStation, "UTF-8") + "&goDate="+date+"&firstRequest=true&startNum="+String.valueOf(startNumber)+"&sort=5";

//        System.out.print(urlFlght+'\n');
//
//        System.out.print(URLEncoder.encode(startStation, "UTF-8")+'\n' );
//        System.out.print(URLEncoder.encode(arriveStation, "UTF-8")+'\n');
    }

    //---日期
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    //--出发城市
    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    //----到达城市

    public String getArriveStation() {
        return arriveStation;
    }

    public void setArriveStation(String arriveStation) {
        this.arriveStation = arriveStation;
    }
}
