package sample;

/**
 * Created by zty56 on 2017/3/22.
 */
/**
 * Created by steven on 2017/3/21.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;


public class RecommendTicket {
    ArrayList<DataSave> reommendticket=new ArrayList<DataSave>();
    int numners;
    int FlightBuss;
    int FlightEcon;
    int GaoBuss;
    int Gaofirst;
    int GaoSecnod;
    int DongFirst;
    int DongSecond;
    int Train;

    int StartMorn;
    int StartAftern;
    int StartNight;
    int recommendNumbers;
    String filePath="C:/TicketRec/data.txt";
    float startMorn;
    float startAftern;
    float startNight;
    float flightBus;
    float flightEcon ;
    float gaoBuss;
    float gaofirst;
    float gaoSecnod;
    float dongFirst;
    float dongSecond ;
    float train;


    public void Getdata() throws Exception {

        try {

            File file = new File(filePath);
            if (!file.exists()) {
                File file1 = new File("C:\\TicketRec");
                file1.mkdir();
                file1 = new File("C:\\TicketRec", "data.txt");
                file1.createNewFile();
                //System.out.println("多级层文件夹下文件创建成功！创建了的文件为:" + file1.getAbsolutePath() + ",上级文件为:" + file1.getParent());
                FileWriter fw = new FileWriter(filePath);
                String content = String.valueOf(numners)+"\r\n"+String.valueOf(FlightBuss)+"\r\n"+String.valueOf(FlightEcon)+"\r\n"+String.valueOf(GaoBuss)+"\r\n"+String.valueOf(Gaofirst)+"\r\n"+String.valueOf(GaoSecnod)+"\r\n"+String.valueOf(DongFirst)+"\r\n"+String.valueOf(DongSecond)+"\r\n"+String.valueOf(Train)+"\r\n"+String.valueOf(StartMorn)+"\r\n"+String.valueOf(StartAftern)+"\r\n"+String.valueOf(StartNight);
                fw.write(content);
                fw.close();
                return;

            } else {

                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file));//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                numners = Integer.parseInt(bufferedReader.readLine());
                FlightBuss = Integer.parseInt(bufferedReader.readLine()) ;
                FlightEcon = Integer.parseInt(bufferedReader.readLine()) ;
                GaoBuss = Integer.parseInt(bufferedReader.readLine());
                Gaofirst = Integer.parseInt(bufferedReader.readLine()) ;
                GaoSecnod = Integer.parseInt(bufferedReader.readLine()) ;
                DongFirst = Integer.parseInt(bufferedReader.readLine()) ;
                DongSecond = Integer.parseInt(bufferedReader.readLine()) ;
                Train = Integer.parseInt(bufferedReader.readLine()) ;
                StartMorn = Integer.parseInt(bufferedReader.readLine()) ;
                StartAftern = Integer.parseInt(bufferedReader.readLine()) ;
                StartNight = Integer.parseInt(bufferedReader.readLine());
                read.close();

                StartMorn = StartMorn / numners;
                StartAftern = StartAftern / numners;
                StartNight  = StartNight / numners;
                StartNight = FlightBuss / numners;
                FlightEcon = FlightEcon / numners;
                GaoBuss = GaoBuss / numners;
                Gaofirst= Gaofirst / numners;
                GaoSecnod = GaoSecnod / numners;
                DongFirst = DongFirst / numners;
                DongSecond= DongSecond / numners;
                Train = Train / numners;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateData(DataSave datasave) throws IOException {
        numners += 1;
        String code = datasave.getTrainCode();
        String seat = datasave.getSeatType();
        int time = Integer.parseInt(datasave.getStartTime().substring(0,2));
        if(datasave.getFlightCompany()!=null)
        {
            if(seat.equals("商务舱"))
            {
                FlightBuss = FlightBuss + 1;
            }
            else if(seat.equals("经济舱"))
                FlightEcon +=1;

        }

        else if(code.startsWith("G")||code.startsWith("D"))
        {
            if(seat.equals("商务座"))
                GaoBuss += 1;
            else if(seat.equals("一等座"))
                Gaofirst +=1;
            else
                GaoSecnod +=1;
        }
        else if(code.startsWith("C")  )
        {
            if(seat.equals("一等座"))
                DongFirst += 1;
            else
                DongSecond +=1;

        }


        if(time >=6 &&time < 12 )
            StartMorn  += 1;
        else if(time>=12 && time < 18)
            StartAftern += 1;
        else StartNight += 1;
        FileWriter fw = new FileWriter(filePath);
        String content = String.valueOf(numners)+"\r\n"+String.valueOf(FlightBuss)+"\r\n"+String.valueOf(FlightEcon)+"\r\n"+String.valueOf(GaoBuss)+"\r\n"+String.valueOf(Gaofirst)+"\r\n"+String.valueOf(GaoSecnod)+"\r\n"+String.valueOf(DongFirst)+"\r\n"+String.valueOf(DongSecond)+"\r\n"+String.valueOf(Train)+"\r\n"+String.valueOf(StartMorn)+"\r\n"+String.valueOf(StartAftern)+"\r\n"+String.valueOf(StartNight);

        fw.write(content);
        fw.close();
    }
    //接口可以改的做简单的内容推荐
    public void TimeAndMoneyBothOk(DataGetAndDeal getAndDeal ){
        // Collections.sort(getAndDeal.InfoTicketList,getAndDeal.comparatorByPrice);
        double stangPrice=0;
        int ecnonubers=0;
        for(DataSave s :getAndDeal.InfoTicketList)
        {
            if(s.getSeatType().equals("经济舱"))
            {
                ecnonubers++;
                stangPrice += s.getTicketPrice();
            }
        }
        stangPrice /= ecnonubers;

        int seconNumbes=0;
        if(ecnonubers==0)
        {
            for(DataSave s:getAndDeal.InfoTicketList)
            {
                if(s.getTrainCode().startsWith("G")&&s.getSeatType().equals("二等座"))
                {
                    seconNumbes++;
                    stangPrice += s.getTicketPrice();
                }
            }
            stangPrice /= seconNumbes;

        }
        int numbers=0;
        if(seconNumbes==0&&ecnonubers==0)
        {
            for(DataSave s:getAndDeal.InfoTicketList)
            {
                if(!s.getSeatType().equals("商务舱")&&!s.getSeatType().equals("商务座"))
                {
                   numbers++;
                   stangPrice += s.getTicketPrice();
                }

            }
            stangPrice /= numbers;

        }
        Collections.sort(getAndDeal.InfoTicketList,getAndDeal.comparatorByCostTime);
        //ArrayList<DataSave> returnData=new ArrayList<DataSave>();

        int end = (int )(getAndDeal.TicketNumbers * 0.5);
        DataSave dataSave;
        for(int k = 0;k<end;++k) {
            dataSave = getAndDeal.InfoTicketList.get(k);
            if (dataSave.getTicketPrice() < stangPrice) {
                recommendNumbers ++;
                reommendticket.add(dataSave);
            }
        }
       /* for(DataSave s :getAndDeal.InfoTicketList)
        {
            if(s.getTrainCode().startsWith("G")&&s.getSeatType().equals("一等座")&&j == 1)
            {
                stangPrice +=s.getTicketPrice();
                j++;

            }
            else if(s.getSeatType().equals("经济舱")&&i == 1)
            {
                stangPrice +=s.getTicketPrice();
                i++;
            }
            if(i==2&&j==2)
            {
                stangPrice = stangPrice*0.65;
                break;
            }


        }
        int FlightNum=0;
        if(j == 1&&i == 1)
        {

            for(DataSave s:getAndDeal.InfoTicketList)
            {
                if(s.getSeatType().equals("经济舱"))
                {
                    stangPrice +=s.getTicketPrice();
                    FlightNum++;
                }
            }
            stangPrice /= FlightNum;

        }
        if(FlightNum == 0&&j==1&&i==1)
        {
            for(DataSave s:getAndDeal.InfoTicketList)
            {
                stangPrice +=s.getTicketPrice();
            }
            stangPrice/=getAndDeal.TicketNumbers;
        }
        //System.out.print("-----------------------------------\n"+stangPrice);
        //double midPrice = getAndDeal.InfoTicketList.get(getAndDeal.TicketNumbers / 2).getTicketPrice()*1.1;
        Collections.sort(getAndDeal.InfoTicketList,getAndDeal.comparatorByCostTime);
        //ArrayList<DataSave> returnData=new ArrayList<DataSave>();

        int end = (int )(getAndDeal.TicketNumbers * 0.5);
        DataSave dataSave;
        for(int k = 0;k<end;++k) {
            dataSave = getAndDeal.InfoTicketList.get(k);
            if (dataSave.getTicketPrice() < stangPrice) {
                recommendNumbers ++;
                reommendticket.add(dataSave);
            }
        }
if(recommendNumbers==0)
{
    if(j==2&&i==2)
    {
        for(DataSave s:getAndDeal.InfoTicketList)
        {
            if(s.getSeatType().startsWith("G")&&s.getSeatType().equals("二等座")) {
                recommendNumbers ++;
                reommendticket.add(s);
            }
            else if(s.getSeatType().equals("经济舱")) {
                reommendticket.add(s);
                recommendNumbers ++;
            }
        }

    }
    else if(j==2)
    {
        for(DataSave s:getAndDeal.InfoTicketList)
        {
            if(s.getSeatType().startsWith("G")&&s.getSeatType().equals("二等座"))
            {
                reommendticket.add(s);
                recommendNumbers ++;
            }
        }
    }
    else if(i==2)
    {
        for(DataSave s:getAndDeal.InfoTicketList)
        {
            if(s.getSeatType().equals("经济舱"))
            {
                reommendticket.add(s);
                recommendNumbers ++;
            }
        }
    }

}
      */  //Collections.sort(returnData,getAndDeal.comparatorByPrice);
        //eturn returnData;

    }
    //基于用户内容的推荐
    public void  ContentBasedRecom(DataGetAndDeal getAndDeal) {
        //ArrayList<DataSave> returnData=new ArrayList<DataSave>();
        final int all = 5;
        int count = 0;

        if (flightBus > 0.4) {


            if (startMorn > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("商务舱") && time >= 6 && time < 12) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startAftern > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("商务舱") && time >= 12 && time < 18) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startNight > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("商务舱") && (time >= 18 || time < 6)) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("商务舱")) {

                        if ((time >= 8 && time <= 11) || (time > 14 && time < 20)) {
                            reommendticket.add(dataSave);
                            count++;
                        }
                    }
                    if (count == all)
                        break;
                }
            }
        }
        if(flightEcon > 0.4) {
            if (startMorn > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("经济舱") && time >= 6 && time < 12) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startAftern > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("经济舱") && time >= 12 && time < 18) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startNight > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("经济舱") && (time >= 18 || time < 6)) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("经济舱")) {

                        if ((time >= 8 && time <= 11) || (time > 14 && time < 20)) {
                            reommendticket.add(dataSave);
                            count++;
                        }
                    }
                    if (count == all)
                        break;
                }
            }
        }



        if(GaoBuss > 0.4) {
            if (startMorn > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("商务座") && time >= 6 && time < 12) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startAftern > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("商务座") && time >= 12 && time < 18) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startNight > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("商务座") && (time >= 18 || time < 6)) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count ==all)
                        break;
                }

            } else {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("商务座")) {

                        if ((time >= 8 && time <= 11) || (time > 14 && time < 20)) {
                            reommendticket.add(dataSave);
                            count++;
                        }
                    }
                    if (count == all)
                        break;
                }
            }
        }


        if(Gaofirst > 0.4) {
            if (startMorn > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("一等座") &&(dataSave.getTrainCode().startsWith("G")||dataSave.getTrainCode().startsWith("C"))&& time >= 6 && time < 12) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startAftern > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("一等座") &&(dataSave.getTrainCode().startsWith("G")||dataSave.getTrainCode().startsWith("C"))&& time >= 12 && time < 18) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startNight > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("一等座") &&(dataSave.getTrainCode().startsWith("G")||dataSave.getTrainCode().startsWith("C"))&& (time >= 18 || time < 6)) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count ==all)
                        break;
                }

            } else {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("一等座")&&(dataSave.getTrainCode().startsWith("G")||dataSave.getTrainCode().startsWith("C"))) {

                        if ((time >= 8 && time <= 11) || (time > 14 && time < 20)) {
                            reommendticket.add(dataSave);
                            count++;
                        }
                    }
                    if (count ==all)
                        break;
                }
            }
        }

        if(GaoSecnod > 0.4) {
            if (startMorn > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("二等座") &&(dataSave.getTrainCode().startsWith("G")||dataSave.getTrainCode().startsWith("C"))&& time >= 6 && time < 12) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startAftern > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("二等座") &&(dataSave.getTrainCode().startsWith("G")||dataSave.getTrainCode().startsWith("C"))&& time >= 12 && time < 18) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startNight > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("二等座") &&(dataSave.getTrainCode().startsWith("G")||dataSave.getTrainCode().startsWith("C"))&& (time >= 18 || time < 6)) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("二等座")&&(dataSave.getTrainCode().startsWith("G")||dataSave.getTrainCode().startsWith("C"))) {

                        if ((time >= 8 && time <= 11) || (time > 14 && time < 20)) {
                            reommendticket.add(dataSave);
                            count++;
                        }
                    }
                    if (count == all)
                        break;
                }
            }
        }




        if(DongFirst > 0.4) {
            if (startMorn > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("一等座") &&(dataSave.getTrainCode().startsWith("D"))&& time >= 6 && time < 12) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startAftern > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("一等座") &&(dataSave.getTrainCode().startsWith("D"))&& time >= 12 && time < 18) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startNight > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("一等座") &&(dataSave.getTrainCode().startsWith("D"))&& (time >= 18 || time < 6)) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("一等座")&&(dataSave.getTrainCode().startsWith("D"))) {

                        if ((time >= 8 && time <= 11) || (time > 14 && time < 20)) {
                            reommendticket.add(dataSave);
                            count++;
                        }
                    }
                    if (count == all)
                        break;
                }
            }
        }

        if(DongSecond > 0.4) {
            if (startMorn > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("二等座") &&(dataSave.getTrainCode().startsWith("D"))&& time >= 6 && time < 12) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startAftern > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("二等座") &&(dataSave.getTrainCode().startsWith("D"))&& time >= 12 && time < 18) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startNight > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("二等座") &&(dataSave.getTrainCode().startsWith("D"))&& (time >= 18 || time < 6)) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && dataSave.getSeatType().equals("二等座")&&(dataSave.getTrainCode().startsWith("D"))) {

                        if ((time >= 8 && time <= 11) || (time > 14 && time < 20)) {
                            reommendticket.add(dataSave);
                            count++;
                        }
                    }
                    if (count == all)
                        break;
                }
            }
        }

        if(Train > 0.4) {
            if (startMorn > 0.5) {

                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all &&  (dataSave.getTrainCode().startsWith("G")&&dataSave.getTrainCode().startsWith("C")&&dataSave.getTrainCode().startsWith("D")&&dataSave.getFlightCompany()==null)&& time >= 6 && time < 12) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else if (startAftern > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all  && (dataSave.getTrainCode().startsWith("G")&&dataSave.getTrainCode().startsWith("C")&&dataSave.getTrainCode().startsWith("D")&&dataSave.getFlightCompany()==null)&& time >= 12 && time < 18) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count ==all)
                        break;
                }

            } else if (startNight > 0.5) {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all  && (dataSave.getTrainCode().startsWith("G")&&dataSave.getTrainCode().startsWith("C")&&dataSave.getTrainCode().startsWith("D")&&dataSave.getFlightCompany()==null)&& (time >= 18 || time < 6)) {
                        reommendticket.add(dataSave);
                        count++;
                    }
                    if (count == all)
                        break;
                }

            } else {
                for (DataSave dataSave : getAndDeal.InfoTicketList) {
                    int time = Integer.parseInt(dataSave.getStartTime().substring(0, 2));
                    if (count < all && (dataSave.getTrainCode().startsWith("G")&&dataSave.getTrainCode().startsWith("C")&&dataSave.getTrainCode().startsWith("D")&&dataSave.getFlightCompany()==null)) {

                        if ((time >= 8 && time <= 11) || (time > 14 && time < 20)) {
                            reommendticket.add(dataSave);
                            count++;
                        }
                    }
                    if (count == all)
                        break;
                }
            }
        }

        TimeAndMoneyBothOk(getAndDeal);
//        for(DataSave s: reommendticket)
//        {
//            for(DataSave j:reommendticket)
//            {
//                if(!s.getSeatType().equals(j.getSeatType()) && !s.getTrainCode().equals(j.getTrainCode()))
//                {
//                    reommendticket.add(s);
//                }
//            }
//        }
        //returnData.add();
        //return reommendticket;
    }

}













