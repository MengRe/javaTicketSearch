package sample;
/**
 * Created by steven on 2017/3/18.
 */
/**
 * Created by steven on 2017/3/18.
 */

public class DataSave {
    private String startTime;
    private String endTime;
    private int costTime;
    private String seatType;
    private double ticketPrice;
    private String seatNum;
    private String trainCode;
    private String flightCompany;
    private String startStation;
    private String arriveStation;

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }


    public String getArriveStation() {
        return arriveStation;
    }

    public void setArriveStation(String arriveStation) {
        this.arriveStation = arriveStation;
    }

    public static double getDoubleValue (String str)
    {
        double r = 0;
        if (str != null && str.length() != 0) {
            StringBuffer bf = new StringBuffer();
            double t = 0;
            char[] chars = str.toCharArray();
            char c;
            StringBuffer b = new StringBuffer();
            for (int i = 0; i < chars.length; i++) {
                c = chars[i];
                if (c >= '0' && c <= '9') {
                    bf.append(c);
                } else if (c == '.') {
                    b.append(chars[i+1]);
                    t = 0.1 * Integer.parseInt(b.toString());
                    break;
                } else {
                    if (bf.length() != 0) {
                        break;
                    }
                }
            }
            try {
                r =  Integer.parseInt(bf.toString())+t;
            } catch (Exception e) {
            }
        }
        return r;
    }
    //---------出发时间
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    //------到达时间
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    //---------花费时间
    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime)
    {
        this.costTime = costTime;
    }
    public void setCostTime(String costTime) {

        this.costTime =Integer.parseInt(costTime.substring(0,costTime.indexOf("小")))*60 + Integer.parseInt(costTime.substring(costTime.indexOf("时")+1,costTime.indexOf("分")));

    }

    //-----车座类型
    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }
    //-----车票价格

    public double getTicketPrice() {
        return ticketPrice;
    }
    public void setTicketPrice(double ticketPrice){this.ticketPrice = ticketPrice;}

    public void setTicketPrice(String ticketPrice) {

        this.ticketPrice = getDoubleValue(ticketPrice);
        //System.out.print(this.ticketPrice+'\n');
    }
    //-------车座数量

    public String getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(String seatNum) {
        this.seatNum = seatNum;
    }

    //---------车次代号

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }


    //-----航班信息
    public String getFlightCompany() {
        return flightCompany;
    }

    public void setFlightCompany(String flightCompany) {
        this.flightCompany = flightCompany;
    }
}

