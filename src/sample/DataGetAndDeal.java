package sample;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
/**
 * Created by steven on 2017/3/18.
 */


public class DataGetAndDeal {
    enum StartTimeConstrain{
        ALL,MORN,AFTERN,NIGHT;
    }
    enum ArriveTimeConstrain{
        ALL,
        MORN,//06：00 - 12:00
        AFTERN,//12:00 - 18:00
        NIGHT; // 18:00 - 06:00
    }
    enum TransferConstrain{
        IS, NOT;
    }
    enum VehicleTypeConstrain{
        ALL, FLIGHT,/*高铁*/CRH,/*动车*/DPT;
    }
    enum TicketPriceConstrain{
        ALL,ZeroToONE,ONETOTWO,TWOTOFOUR,FOURMORE;
    }
    enum CostTimeConstrain{
        ALL,ZEROTOFOUR,FOURTOEIGHT,EIGHTTOTWELVE,MORETWELVE;
    }
    //存储车票数量
    int TicketNumbers;
    //核心变量 存储所有车票信息的链表
    ArrayList<DataSave> InfoTicketList = new ArrayList<DataSave>();
    // 私有json对象，承接网站返回的数据
    private JSONObject resultJsonObjectTrain;
    private JSONObject resultJsonObjectFlight;
    private int p = 25;         //判断是否是航班的最后一页
    StartTimeConstrain startTimeConstrain = StartTimeConstrain.ALL;
    ArriveTimeConstrain arriveTimeConstrin = ArriveTimeConstrain.ALL;
    TransferConstrain transferConstrin = TransferConstrain.NOT;
    VehicleTypeConstrain vehicleTypeConstrain = VehicleTypeConstrain.ALL;
    TicketPriceConstrain ticketPriceConstrain = TicketPriceConstrain.ALL;
    CostTimeConstrain costTimeConstrain = CostTimeConstrain.ALL;
    //排序变量接口 ---按出行时间
    Comparator<DataSave> comparatorByCostTime = new Comparator<DataSave>() {
        public int compare(DataSave o1, DataSave o2) {
            ////按照出行时间由短到长，若出行时间相同则票价低的排在前面
            if (o1.getCostTime() > o2.getCostTime())
                return 1;
            else if (o1.getCostTime() < o2.getCostTime())
                return -1;
            else
                return o1.getTicketPrice() > o2.getTicketPrice() ? 1 : -1;
        }
    };
    //排序接口 -- 按照票价
    Comparator<DataSave> comparatorByPrice = new Comparator<DataSave>() {
        public int compare(DataSave o1, DataSave o2) {
            //按照票价由低到高，若票价相同则时间短的排在前面
            if (o1.getTicketPrice() > o2.getTicketPrice())
                return 1;
            else if (o1.getTicketPrice() < o2.getTicketPrice())
                return -1;
            else
                return o1.getCostTime() > o2.getCostTime() ? 1 : -1;
        }
    };

    //得到火车票信息
    public void GetTrainTicketInfoAndDeal(ConfigInform infoConfig) throws Exception {


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://www.chepiao100.com/api/yupiao");
        CloseableHttpResponse response;
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("userid", ConfigInform.userID));
        formparams.add(new BasicNameValuePair("seckey", ConfigInform.key));
        formparams.add(new BasicNameValuePair("date", infoConfig.getDate()));
        formparams.add(new BasicNameValuePair("startStation", infoConfig.getStartStation()));
        formparams.add(new BasicNameValuePair("arriveStation", infoConfig.getArriveStation()));
        UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httpPost.setEntity(uefEntity);
        response = httpClient.execute(httpPost);
        HttpEntity httpEntity = response.getEntity();

        BufferedReader bufferedReader;
        StringBuilder entityStringBuilder = new StringBuilder();

        try {
            bufferedReader = new BufferedReader
                    (new InputStreamReader(httpEntity.getContent(), "UTF-8"), 8 * 1024);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                entityStringBuilder.append(line + "\n");
            }
            //利用从HttpEntity中得到的String生成JsonObject
            resultJsonObjectTrain = new JSONObject(entityStringBuilder.toString());
            //System.out.print(resultJsonObjectTrain);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*处理resultJsonObject得到车票信息*/
        if(!resultJsonObjectTrain.get("errMsg").equals("Y"))//成功返回Y
        {
            return;
        }
        JSONArray data1 = resultJsonObjectTrain.getJSONArray("data");
            int lenghtJson=data1.length();
            JSONObject job;
            String startTime;
            int stime;
            String endTime;
            int etime;
            String takeTime;
            int costTime;
            for(int i=0;i<lenghtJson;i++)
            {
            //dataSave=new DataSave();//判断出火车的类型再决定申请几个对象
            job = data1.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
            //System.out.print(job);
            String trainCode = job.getString("trainCode");
            //出发时间
            startTime = job.getString("startTime");
            stime = Integer.parseInt(startTime.substring(0,2));
            //到达时间
            endTime = job.getString("endTime");
            etime = Integer.parseInt(endTime.substring(0,2));
            //花费时间
            takeTime = job.getString("takeTime");
            costTime = Integer.parseInt(takeTime.substring(0,takeTime.indexOf("小")))*60 + Integer.parseInt(takeTime.substring(takeTime.indexOf("时")+1,takeTime.indexOf("分")));
            startTimeHandle("business","business-prc",
                    "one-seat","one-seat-prc",
                    "two-seat","two-seat-prc",
                    "best-seat","best-seat-prc",
                    "hard-seat","hard-seat-prc",
                    "hard-sleeper","hard-sleeper-prc",
                    "soft-seat","soft-seat-prc",
                    "soft-sleeper","soft-sleeper-prc",
                    trainCode,costTime,startTime,endTime,job,stime,etime,
            "startStation","arriveStation");

            /*处理job对象，*/
            //每处理完一个job对象若符合要求，将该对象利用ArrayList的add的方法添加到InfoTicketList，
        //注意一定要new DataSave 对象，可能会申请多个对象
    }
    }

    //得到飞机票信息
    private void GetFlightTicketInfoAnd(ConfigInform infoConfig) throws Exception {

                  X509TrustManager x509m = new X509TrustManager() {


            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }


            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }


            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        };
        // 获取一个SSLContext实例
        SSLContext s = SSLContext.getInstance("SSL");
        // 初始化SSLContext实例
        s.init(null, new TrustManager[]{x509m},
                new java.security.SecureRandom());
        SSLSocketFactory ssf = s.getSocketFactory();
        String depCity = URLEncoder.encode(infoConfig.getStartStation(), "UTF-8");
        String arrCity = URLEncoder.encode(infoConfig.getArriveStation(), "UTF-8");
        URL url = new URL(infoConfig.getUrlFlght());
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setSSLSocketFactory(ssf);
        InputStreamReader in = new InputStreamReader(con.getInputStream(), "utf-8");
        BufferedReader bfreader = new BufferedReader(in);
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = bfreader.readLine()) != null) {
            sb.append(line);
        }
        //提取信息,存入Train_ticket类中
        //String t = sb.toString();  //String t 中储存了我们需要的信息
        DealWithJsonFight(sb);


    }


    //处理飞机的
    public void GetDataIn1(DataSave dataSave,JSONObject binfo,JSONObject extparamsobject,String seat,Double Price)
    {
        TicketNumbers ++;
        dataSave.setStartTime(binfo.getString("depTime"));   //存StartTime
        dataSave.setEndTime(binfo.getString("arrTime"));     //存EndTime
        dataSave.setCostTime(binfo.getString("flightTime"));   //存CostTime
        dataSave.setSeatType(seat);   //存SeatType
        dataSave.setTicketPrice(Price);
        dataSave.setSeatNum("有");
        dataSave.setTrainCode(binfo.getString("airCode"));
        dataSave.setStartStation(binfo.getString("depAirport"));
        dataSave.setArriveStation(binfo.getString("arrAirport"));
        //dataSave.setFlightCompany(binfo.getString("fullName"));
        InfoTicketList.add(dataSave);

    }

    private void DealWithJsonFight(StringBuffer sb) {
        //处理resultJsonObjectFlight对象


        resultJsonObjectFlight = new JSONObject(sb.toString());
        if (resultJsonObjectFlight.getString("msg").equals("查询成功!")) {
        JSONObject a = resultJsonObjectFlight.getJSONObject("data");
        JSONArray flights = a.getJSONArray("flights");
        int lenghtJson2 = flights.length();
        p = lenghtJson2;
        JSONObject job2, binfo, extparamsobject;
        String extparams;

        DataSave dataSave;



    for (int j = 0; j < lenghtJson2; j++) {
        dataSave = new DataSave();//判断出火车的类型再决定申请几个对象
        job2 = flights.getJSONObject(j);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
        binfo = job2.getJSONObject("binfo");
        extparams = job2.getString("extparams");
        extparamsobject = new JSONObject(extparams);
        int depTime = Integer.parseInt(binfo.getString("depTime").substring(0, 2));
        int arrTime = Integer.parseInt(binfo.getString("arrTime").substring(0, 2));
        Double Price = 0.0;
        int Time = 0;
        String costTime = binfo.getString("flightTime");
        Time = Integer.parseInt(costTime.substring(0, costTime.indexOf("小"))) * 60 + Integer.parseInt(costTime.substring(costTime.indexOf("时") + 1, costTime.indexOf("分")));

        if (extparams.indexOf("economyClassMinPrice") > -1) {

            Price = Double.parseDouble(extparamsobject.getString("economyClassMinPrice"));
            GetDataIn(dataSave, binfo, extparamsobject, Time, Price, depTime, arrTime, 1);

                /*dataSave.setStartTime(binfo.getString("depTime"));   //存StartTime
                dataSave.setEndTime(binfo.getString("arrTime"));     //存EndTime
                dataSave.setCostTime(binfo.getString("flightTime"));   //存CostTime
                dataSave.setSeatType("经济舱");   //存SeatType
                dataSave.setTicketPrice(extparamsobject.getString("economyClassMinPrice"));
                dataSave.setTrainCode(binfo.getString("airCode"));*/
            //dataSave.setFlightCompany(binfo.getString("fullName"));
        }


        if (extparams.indexOf("businessClassMinPrice") > -1) {
            Price = Double.parseDouble(extparamsobject.getString("businessClassMinPrice"));
            GetDataIn(dataSave, binfo, extparamsobject, Time, Price, depTime, arrTime, 2);
                    /*dataSave.setStartTime(binfo.getString("depTime"));   //存StartTime
                    dataSave.setEndTime(binfo.getString("arrTime"));     //存EndTime
                    dataSave.setCostTime(binfo.getString("flightTime"));   //存CostTime
                    dataSave.setSeatType("商务舱");   //存SeatType
                    dataSave.setTicketPrice(extparamsobject.getString("businessClassMinPrice"));
                    dataSave.setTrainCode(binfo.getString("airCode"));*/
            //dataSave.setFlightCompany(binfo.getString("fullName"));
        }
        //System.out.print(lenghtJson2);
            /*System.out.println(dataSave.getStartTime());
            System.out.println(dataSave.getEndTime());
            System.out.println(dataSave.getCostTime());
            System.out.println(dataSave.getSeatType());
            System.out.println(dataSave.getTicketPrice());
            System.out.println(dataSave.getTrainCode());
            System.out.println(dataSave.getFlightCompany());*/
        //System.out.println(extparamsobject.getString("economyClassMinPrice"));


            /*处理job对象，*/

        //每处理完一个job对象若符合要求，将该对象利用ArrayList的add的方法添加到InfoTicketList，
        //注意一定要new DataSave 对象，可能会申请多个对象
        //InfoTicketList.add(dataSave);

        //String s=job.getString("startTime");

        //System.out.println(s);
        // 得到 每个对象中的属性值
    }
}
        //for(int l=0;l<InfoTicketList.size();l++)
        //System.out.println(InfoTicketList.get(l).getCostTime());
    }


    public void DealFlghtInfo(ConfigInform configInform) throws Exception {
        int i=0;
        while (p == 25) {
            configInform.setUrlFlght(configInform.getStartStation(),configInform.getArriveStation(),configInform.getDate(),i*25);
            i++;
            //先调用GetFlightTicketInfoAndDeal，再调用DealWithJsonFight，
            //若DealWithJsonFight中能取到信息，则继续循环调用两个函数
            GetFlightTicketInfoAnd(configInform);
        }

    }

    public void GetDataIn(DataSave dataSave,JSONObject binfo,JSONObject extparamsobject,int Time,Double Price,int depTime,int arrTime,int flag)
    {
        String seat;
        if(flag==1)
            seat="经济舱";
        else
            seat="商务舱";

        switch (startTimeConstrain) {
            case ALL:
                switch (arriveTimeConstrin) {
                    case ALL:
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                    case MORN:
                        if (arrTime < 6 || arrTime > 12)
                            return;
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                    case AFTERN:
                        if (arrTime < 12 || arrTime > 18)
                            return;
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                    case NIGHT:
                        if (arrTime < 18 || arrTime > 6)
                            return;
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                }
                break;
            case MORN:
                if (depTime < 6 || depTime > 12)
                    return;
                switch (arriveTimeConstrin) {
                    case ALL:
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                    case MORN:
                        if (arrTime < 6 || arrTime > 12)
                            return;
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                    case AFTERN:
                        if (arrTime < 12 || arrTime > 18)
                            return;
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                    case NIGHT:
                        if (arrTime < 18 || arrTime > 6)
                            return;
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                }
                break;

            case AFTERN:
                if (depTime < 12 || depTime > 18)
                    return;
                switch (arriveTimeConstrin) {
                    case ALL:
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                    case MORN:
                        if (arrTime < 6 || arrTime > 12)
                            return;
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                    case AFTERN:
                        if (arrTime < 12 || arrTime > 18)
                            return;
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                    case NIGHT:
                        if (arrTime < 18 || arrTime > 6)
                            return;
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                }
                break;
            case NIGHT:
                if (depTime < 18 || depTime > 6)
                    return;
                switch (arriveTimeConstrin) {
                    case ALL:
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                    case MORN:
                        if (arrTime < 6 || arrTime > 12)
                            return;
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                    case AFTERN:
                        if (arrTime < 12 || arrTime > 18)
                            return;
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                    case NIGHT:
                        if (arrTime < 18 || arrTime > 6)
                            return;
                        switch (ticketPriceConstrain) {
                            case ALL:
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;


                            case ZeroToONE:
                                if (Price > 1000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case ONETOTWO:
                                if (Price <= 1000 || Price > 2000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;

                            case TWOTOFOUR:
                                if (Price <= 2000 || Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                            case FOURMORE:
                                if (Price > 4000)
                                    return;
                                switch (costTimeConstrain) {
                                    case ALL:GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        break;
                                    case ZEROTOFOUR:if (Time>=240)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case FOURTOEIGHT:if (Time<240||Time>=480)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case EIGHTTOTWELVE:if (Time<480||Time>=720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                    case MORETWELVE:if (Time<720)
                                        return;
                                        GetDataIn1(dataSave,binfo,extparamsobject,seat,Price);
                                        return;
                                }
                                break;
                        }
                        break;
                }
                break;


        }
    }
    //存储提取的变量
    private DataSave dataSave;
    //提取车次信息
    private void FUNC_hCHANGE(String seatNum, double price,
                              String seatType, String trainCode,
                              String startTime,String  endTime,
                              int costTime,
                              String startStation, String arriveStation)
    {
        TicketNumbers ++;
        dataSave = new DataSave();
        dataSave.setArriveStation(arriveStation);
        dataSave.setStartStation(startStation);
        dataSave.setTicketPrice(price);
        dataSave.setCostTime(costTime);
        dataSave.setSeatType(seatType);
        dataSave.setTrainCode(trainCode);
        dataSave.setStartTime(startTime);
        dataSave.setEndTime(endTime);
        dataSave.setSeatNum(seatNum);

        InfoTicketList.add(dataSave);
    }
    //处理票价
    private  void FuncTicketSec(JSONObject job,
                                String seatType, String seat_price,
                                String trainCode,String startTime,String endTime,
                                int costTime,String ChinaSeat,
                                String startStation, String arriveStation)
    {
        String seatNum = job.getString(seatType);
        String startstation = job.getString(startStation);
        String arrivestation = job.getString(arriveStation);
        double price;
        if(!seatNum.equals("--")&&!seatNum.equals("无"))//有票
        {
            price= Double.parseDouble(job.getString(seat_price).substring(1));
            switch (ticketPriceConstrain) {
                case ZeroToONE://票价符合要求
                    if(price < 1000)
                        FUNC_hCHANGE(seatNum, price, ChinaSeat, trainCode, startTime,endTime, costTime,startstation,arrivestation);
                    break;
                case ONETOTWO:
                    if(price >= 1000 && price < 2000)
                        FUNC_hCHANGE(seatNum, price, ChinaSeat, trainCode, startTime,endTime, costTime,startstation,arrivestation);
                    break;
                case TWOTOFOUR:
                    if(price >= 2000 && price < 40000)
                        FUNC_hCHANGE(seatNum, price, ChinaSeat, trainCode, startTime,endTime, costTime,startstation,arrivestation);
                    break;
                case FOURMORE:
                    if(price >= 4000)
                        FUNC_hCHANGE(seatNum, price, ChinaSeat, trainCode, startTime,endTime, costTime,startstation,arrivestation);
                    break;
                case ALL:
                    FUNC_hCHANGE(seatNum, price, ChinaSeat, trainCode, startTime,endTime, costTime,startstation,arrivestation);
                    break;
                default:
                    break;
            }

        }
    }

    //机票处理
    private void TicketDeal( String buiness_seat,String buiness_prc,
                             String one_seat,   String  one_seat_prc,
                             String two_seat, String two_seat_prc,
                             String best_seat, String best_seat_prc,
                             String hard_seat, String hard_seat_prc,
                             String hard_sleeper, String hard_slepper_pric,
                             String soft_seat,  String soft_seat_prc,
                             String soft_sleeper, String soft_sleeper_prc,
                             String trainCode,    int costTime,
                             String startTime,String  endTime,
                             JSONObject job,
                             String startStation, String arriveStation)
    {

        switch (vehicleTypeConstrain)
        {
            case CRH:
                if(trainCode.startsWith("G")||trainCode.startsWith("C")) {
                    //取票
                    FuncTicketSec(job, buiness_seat, buiness_prc, trainCode, startTime, endTime, costTime, "商务座",startStation,arriveStation);
                    FuncTicketSec(job, one_seat, one_seat_prc, trainCode, startTime, endTime, costTime, "一等座",startStation,arriveStation);
                    FuncTicketSec(job, two_seat, two_seat_prc, trainCode, startTime, endTime, costTime, "二等座",startStation,arriveStation);
                    FuncTicketSec(job, best_seat, best_seat_prc, trainCode, startTime, endTime, costTime, "特等座",startStation,arriveStation);


                }
                break;
            //动车
            case DPT:
                if(trainCode.startsWith("D"))
                {
                    FuncTicketSec(job, soft_sleeper, soft_sleeper_prc, trainCode, startTime, endTime, costTime, "软卧",startStation,arriveStation);
                    FuncTicketSec(job, one_seat, one_seat_prc, trainCode, startTime, endTime, costTime, "一等座",startStation,arriveStation);
                    FuncTicketSec(job, two_seat, two_seat_prc, trainCode, startTime, endTime, costTime, "二等座",startStation,arriveStation);
                }
                break;
            case ALL: {
                FuncTicketSec(job, buiness_seat, buiness_prc, trainCode, startTime, endTime, costTime, "商务座",startStation,arriveStation);
                FuncTicketSec(job, best_seat, best_seat_prc, trainCode, startTime, endTime, costTime, "特等座",startStation,arriveStation);
                FuncTicketSec(job, one_seat, one_seat_prc, trainCode, startTime, endTime, costTime, "一等座",startStation,arriveStation);
                FuncTicketSec(job, two_seat, two_seat_prc, trainCode, startTime, endTime, costTime, "二等座",startStation,arriveStation);
                FuncTicketSec(job, soft_sleeper, soft_sleeper_prc, trainCode, startTime, endTime, costTime, "软卧",startStation,arriveStation);
                FuncTicketSec(job, hard_seat, hard_seat_prc, trainCode, startTime, endTime, costTime, "硬座",startStation,arriveStation);
                FuncTicketSec(job, hard_sleeper, hard_slepper_pric, trainCode, startTime, endTime, costTime, "硬卧",startStation,arriveStation);
                FuncTicketSec(job, soft_seat, soft_seat_prc, trainCode, startTime, endTime, costTime, "软座",startStation,arriveStation);

            }
            break;
            default:
                break;

        }

    }
    //出行时间限制
    private void TimeHandle(String buiness_seat,String buiness_prc,
                            String one_seat,   String  one_seat_prc,
                            String two_seat, String two_seat_prc,
                            String best_seat, String best_seat_prc,
                            String hard_seat, String hard_seat_prc,
                            String hard_sleeper, String hard_slepper_pric,
                            String soft_seat,  String soft_seat_prc,
                            String soft_sleeper, String soft_sleeper_prc,
                            String trainCode,    int costTime,
                            String startTime,String  endTime,
                            JSONObject job,
                            String startStation, String arriveStation) {
        //----时间限制
        switch (costTimeConstrain) {
            case ZEROTOFOUR:
                //改一下代码----------
                if (costTime > 0 && costTime < 240)
                    // System.out.print("我在0-4小时");
                    //----票价限制
                    TicketDeal(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                            two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                            hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                            soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                            startTime, endTime, job,startStation,arriveStation);
                break;
            case FOURTOEIGHT:
                if (costTime >= 240 && costTime < 480) {
                    //System.out.print("我在4-8小时");
                    //----票价限制
                    TicketDeal(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                            two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                            hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                            soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                            startTime, endTime, job,startStation,arriveStation);
                }
                break;
            case EIGHTTOTWELVE:
                if (costTime >= 480 && costTime < 720)
                    TicketDeal(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                            two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                            hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                            soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                            startTime, endTime, job,startStation,arriveStation);
                break;
            case MORETWELVE:
                if (costTime >= 720)
                    TicketDeal(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                            two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                            hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                            soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                            startTime, endTime, job,startStation,arriveStation);
                break;
            case ALL:
                TicketDeal(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                        two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                        hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                        soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                        startTime, endTime, job,startStation,arriveStation);
                break;
            default:break;

        }
    }
    //到达时间限制
    private void arriveTimeHandle ( String buiness_seat,String buiness_prc,
                                    String one_seat,   String  one_seat_prc,
                                    String two_seat, String two_seat_prc,
                                    String best_seat, String best_seat_prc,
                                    String hard_seat, String hard_seat_prc,
                                    String hard_sleeper, String hard_slepper_pric,
                                    String soft_seat,  String soft_seat_prc,
                                    String soft_sleeper, String soft_sleeper_prc,
                                    String trainCode,    int costTime,
                                    String startTime,String  endTime,
                                    JSONObject job,int etime,
                                    String startStation, String arriveStation)
    {
        //----到达时间
        switch (arriveTimeConstrin) {
            case MORN:
                if (etime >= 6 && etime < 12)
                    TimeHandle(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                            two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                            hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                            soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                            startTime, endTime, job,startStation,arriveStation);
                break;
            case AFTERN:
                if (etime >= 12 && etime < 18)
                    TimeHandle(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                            two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                            hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                            soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                            startTime, endTime, job,startStation,arriveStation);                break;
            case NIGHT:
                if (etime >= 18 || etime < 6)
                    TimeHandle(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                            two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                            hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                            soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                            startTime, endTime, job,startStation,arriveStation);                break;
            case ALL:
                TimeHandle(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                        two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                        hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                        soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                        startTime, endTime, job,startStation,arriveStation);
                break;
            default: break;
        }
    }

    //出发时间限制
    private void startTimeHandle ( String buiness_seat,String buiness_prc,
                                   String one_seat,   String  one_seat_prc,
                                   String two_seat, String two_seat_prc,
                                   String best_seat, String best_seat_prc,
                                   String hard_seat, String hard_seat_prc,
                                   String hard_sleeper, String hard_slepper_pric,
                                   String soft_seat,  String soft_seat_prc,
                                   String soft_sleeper, String soft_sleeper_prc,
                                   String trainCode,    int costTime,
                                   String startTime,String  endTime,
                                   JSONObject job,int stime,int etime,
                                   String startStation, String arriveStation)
    {
        //----到达时间
        switch (startTimeConstrain) {
            case MORN:
                if (stime >= 6 && stime < 12)
                    arriveTimeHandle(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                            two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                            hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                            soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                            startTime, endTime, job,etime,startStation,arriveStation);
                break;
            case AFTERN:
                if (stime >= 12 && stime < 18)
                    arriveTimeHandle(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                            two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                            hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                            soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                            startTime, endTime, job,etime,startStation,arriveStation);
                break;
            case NIGHT:
                if (stime >= 18 || stime < 6)
                    arriveTimeHandle(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                            two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                            hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                            soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                            startTime, endTime, job,etime,startStation,arriveStation);
                break;
            case ALL:
                arriveTimeHandle(buiness_seat,buiness_prc, one_seat, one_seat_prc,
                        two_seat, two_seat_prc, best_seat,  best_seat_prc, hard_seat, hard_seat_prc,
                        hard_sleeper,  hard_slepper_pric, soft_seat,   soft_seat_prc,
                        soft_sleeper, soft_sleeper_prc, trainCode,  costTime,
                        startTime, endTime, job,etime,startStation,arriveStation);
                break;
            default: break;
        }
    }

}

