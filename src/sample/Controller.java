package sample;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.converter.DateStringConverter;

public class Controller {
    public int Pricelimitv = 1;   //默认为1 不限
    public boolean Ischange = false;
    public int choice =1;  //默认为3 推荐方案
    public int starttime = 1;  //默认出发时间段不限
    public int overtime = 1;   //默认到达时间段不限
    public int totaltime = 1; //默认总体时间不限
    public TextField startcity;
    public TextField tocity;
    public DatePicker date;
    public String start_city;
    public String over_city;
    public String datechoosen;
    RecommendTicket recommendTicket=new RecommendTicket();

    @FXML
    private MenuItem MenuItem1;
    @FXML
    private MenuItem MenuItem2;
    @FXML
    private MenuItem MenuItem3;
    @FXML
    private MenuItem MenuItem4;
    @FXML
    private Button Return1;
    @FXML
    private Button Return2;
    @FXML
    private Button Return3;
    @FXML
    private SplitPane Pricelimit;
    @FXML
    private SplitPane otherlimit;
    @FXML
    private SplitPane timelimit2;
    @FXML
    private Accordion timelimit1;
    @FXML
    private RadioButton RadioButton1;
    @FXML
    private RadioButton RadioButton2;
    @FXML
    private RadioButton RadioButton3;
    @FXML
    private RadioButton RadioButton4;
    @FXML
    private RadioButton RadioButton5;
    @FXML
    private RadioButton RadioButton6;
    @FXML
    private RadioButton RadioButton7;
    @FXML
    private RadioButton RadioButton8;
    @FXML
    private CheckBox checkbox1;
    @FXML
    private CheckBox checkbox2;
    @FXML
    private CheckBox checkbox3;
    @FXML
    private CheckBox checkbox4;
    @FXML
    private CheckBox checkbox5;
    @FXML
    private CheckBox checkbox6;
    @FXML
    private CheckBox checkbox7;
    @FXML
    private CheckBox checkbox8;
    @FXML
    private CheckBox checkbox9;
    @FXML
    private CheckBox checkbox10;
    @FXML
    private Button make;
    @FXML
    private CheckBox choice1;
    @FXML
    private CheckBox choice2;
    @FXML
    private CheckBox choice3;
    @FXML
    private Button lastButton;
    @FXML
    public SplitPane LastFrame;
    @FXML
    private AnchorPane Pane1;
    @FXML
    private AnchorPane Pane2;
    @FXML
    private AnchorPane Pane3;
    @FXML
    private AnchorPane Pane4;
    @FXML
    private AnchorPane Pane5;
    @FXML
    private AnchorPane Pane6;
    @FXML
    private Label la00;
    @FXML
    private Label la01;
    @FXML
    private Label la02;
    @FXML
    private Label la03;
    @FXML
    private Label la04;
    @FXML
    private Label la05;
    @FXML
    private Label la06;

    @FXML
    private Label la10;
    @FXML
    private Label la11;
    @FXML
    private Label la12;
    @FXML
    private Label la13;
    @FXML
    private Label la14;
    @FXML
    private Label la15;
    @FXML
    private Label la16;

    @FXML
    private Label la20;
    @FXML
    private Label la21;
    @FXML
    private Label la22;
    @FXML
    private Label la23;
    @FXML
    private Label la24;
    @FXML
    private Label la25;
    @FXML
    private Label la26;

    @FXML
    private Label la30;
    @FXML
    private Label la31;
    @FXML
    private Label la32;
    @FXML
    private Label la33;
    @FXML
    private Label la34;
    @FXML
    private Label la35;
    @FXML
    private Label la36;

    @FXML
    private Label la40;
    @FXML
    private Label la41;
    @FXML
    private Label la42;
    @FXML
    private Label la43;
    @FXML
    private Label la44;
    @FXML
    private Label la45;
    @FXML
    private Label la46;

    @FXML
    private Label la50;
    @FXML
    private Label la51;
    @FXML
    private Label la52;
    @FXML
    private Label la53;
    @FXML
    private Label la54;
    @FXML
    private Label la55;
    @FXML
    private Label la56;

    @FXML
    private Label la60;
    @FXML
    private Label la61;
    @FXML
    private Label la62;
    @FXML
    private Label la63;
    @FXML
    private Label la64;
    @FXML
    private Label la65;
    @FXML
    private Label la66;

    @FXML
    private Label la70;
    @FXML
    private Label la71;
    @FXML
    private Label la72;
    @FXML
    private Label la73;
    @FXML
    private Label la74;
    @FXML
    private Label la75;
    @FXML
    private Label la76;

    @FXML
    private Label la80;
    @FXML
    private Label la81;
    @FXML
    private Label la82;
    @FXML
    private Label la83;
    @FXML
    private Label la84;
    @FXML
    private Label la85;
    @FXML
    private Label la86;

    @FXML
    private Label la90;
    @FXML
    private Label la91;
    @FXML
    private Label la92;
    @FXML
    private Label la93;
    @FXML
    private Label la94;
    @FXML
    private Label la95;
    @FXML
    private Label la96;
    @FXML
    private  AnchorPane Paneout1;
    @FXML
    private  AnchorPane Paneout2;
    @FXML
    private Rectangle Rectangle1;
    private Label[][] labels = {{la00, la01, la02, la03, la04, la05, la06}, {la10, la11, la12, la13, la14, la15, la16}, {la20, la21, la22, la23, la24, la25, la26},
            {la30, la31, la32, la33, la34, la35, la36}, {la40, la41, la42, la43, la44, la45, la46}, {la50, la51, la52, la53, la54, la55, la56},
            {la60, la61, la62, la63, la64, la65, la66}, {la70, la71, la72, la73, la74, la75, la76}, {la80, la81, la82, la83, la84, la85, la86},
            {la90, la91, la92, la93, la94, la95, la96}};

    byte zhongzhuan = 0;
    DataGetAndDeal dataGetAndDeal = new DataGetAndDeal();
    ConfigInform configInform = new ConfigInform();


    public void handleMenuItem1(ActionEvent event) {       //具体时间限制响应
        timelimit1.setVisible(true);
    }

    public void handleMenuItem2(ActionEvent event) {   //总价限制响应
        //Pricelimit.setVisible(true);
        //Pane1.setVisible(true);
        //Pane2.setVisible(true);
        Pricelimit.setLayoutX(100);
        Pricelimit.setLayoutY(401);
       // labels[0][0].setText("0000");
    }

    public void handleMenuItem3(ActionEvent event) {    //整体时间限制响应
       // timelimit2.setVisible(true);
       // Pane5.setVisible(true);
       // Pane6.setVisible(true);
        timelimit2.setLayoutX(475);
        timelimit2.setLayoutY(401);
    }

    public void handleMenuItem4(ActionEvent event) {     //其他限制响应
       // otherlimit.setVisible(true);
       // Pane3.setVisible(true);
       // Pane4.setVisible(true);
       otherlimit.setLayoutX(839);
        otherlimit.setLayoutY(401);
    }

    public void handlecheckbox1(ActionEvent event) {
        checkbox2.setSelected(false);
        checkbox3.setSelected(false);
        checkbox4.setSelected(false);
        checkbox5.setSelected(false);
        dataGetAndDeal.ticketPriceConstrain = DataGetAndDeal.TicketPriceConstrain.ALL;

    }

    public void handlecheckbox2(ActionEvent event) {
        checkbox1.setSelected(false);
        checkbox3.setSelected(false);
        checkbox4.setSelected(false);
        checkbox5.setSelected(false);
        dataGetAndDeal.ticketPriceConstrain = DataGetAndDeal.TicketPriceConstrain.ZeroToONE;
    }

    public void handlecheckbox3(ActionEvent event) {
        checkbox1.setSelected(false);
        checkbox2.setSelected(false);
        checkbox4.setSelected(false);
        checkbox5.setSelected(false);
        dataGetAndDeal.ticketPriceConstrain = DataGetAndDeal.TicketPriceConstrain.ONETOTWO;
    }

    public void handlecheckbox4(ActionEvent event) {
        checkbox1.setSelected(false);
        checkbox2.setSelected(false);
        checkbox3.setSelected(false);
        checkbox5.setSelected(false);
        dataGetAndDeal.ticketPriceConstrain = DataGetAndDeal.TicketPriceConstrain.TWOTOFOUR;
    }

    public void handlecheckbox5(ActionEvent event) {
        checkbox1.setSelected(false);
        checkbox2.setSelected(false);
        checkbox3.setSelected(false);
        checkbox4.setSelected(false);
        dataGetAndDeal.ticketPriceConstrain = DataGetAndDeal.TicketPriceConstrain.FOURMORE;
    }

    public void handleReturn1(ActionEvent event) {
        Pricelimit.setLayoutX(-7);
        Pricelimit.setLayoutY(872);
    }

    public void handleToggleButton1(ActionEvent event) {

        if (zhongzhuan % 2 == 0) {
            dataGetAndDeal.transferConstrin = DataGetAndDeal.TransferConstrain.NOT;
        } else {
            dataGetAndDeal.transferConstrin = DataGetAndDeal.TransferConstrain.IS;
        }
        zhongzhuan += 1;
    }

    public void handlechoice1(ActionEvent event) {
        choice2.setSelected(false);
        choice3.setSelected(false);
        choice = 1;
    }

    public void handlechoice2(ActionEvent event) {
        choice1.setSelected(false);
        choice3.setSelected(false);
        choice = 2;
    }

    public void handlechoice3(ActionEvent event) {
        choice2.setSelected(false);
        choice1.setSelected(false);
        choice = 3;
    }

    public void handleReturn2(ActionEvent event) {
        otherlimit.setLayoutX(-7);
        otherlimit.setLayoutY(1143);
    }

    public void handleRadioButton1(ActionEvent event) {
        RadioButton2.setSelected(false);
        RadioButton3.setSelected(false);
        RadioButton4.setSelected(false);

        dataGetAndDeal.startTimeConstrain = DataGetAndDeal.StartTimeConstrain.ALL;
    }

    public void handleRadioButton2(ActionEvent event) {
        RadioButton1.setSelected(false);
        RadioButton3.setSelected(false);
        RadioButton4.setSelected(false);
        dataGetAndDeal.startTimeConstrain = DataGetAndDeal.StartTimeConstrain.MORN;
    }

    public void handleRadioButton3(ActionEvent event) {
        RadioButton1.setSelected(false);
        RadioButton2.setSelected(false);
        RadioButton4.setSelected(false);
        dataGetAndDeal.startTimeConstrain = DataGetAndDeal.StartTimeConstrain.AFTERN;

    }

    public void handleRadioButton4(ActionEvent event) {
        RadioButton1.setSelected(false);
        RadioButton2.setSelected(false);
        RadioButton3.setSelected(false);
        dataGetAndDeal.startTimeConstrain = DataGetAndDeal.StartTimeConstrain.NIGHT;

    }

    public void handleRadioButton5(ActionEvent event) {
        RadioButton6.setSelected(false);
        RadioButton7.setSelected(false);
        RadioButton8.setSelected(false);
        dataGetAndDeal.arriveTimeConstrin = DataGetAndDeal.ArriveTimeConstrain.ALL;
    }

    public void handleRadioButton6(ActionEvent event) {
        RadioButton5.setSelected(false);
        RadioButton7.setSelected(false);
        RadioButton8.setSelected(false);
        dataGetAndDeal.arriveTimeConstrin = DataGetAndDeal.ArriveTimeConstrain.MORN;
    }

    public void handleRadioButton7(ActionEvent event) {
        RadioButton5.setSelected(false);
        RadioButton6.setSelected(false);
        RadioButton8.setSelected(false);
        dataGetAndDeal.arriveTimeConstrin = DataGetAndDeal.ArriveTimeConstrain.AFTERN;
    }

    public void handleRadioButton8(ActionEvent event) {
        RadioButton5.setSelected(false);
        RadioButton6.setSelected(false);
        RadioButton7.setSelected(false);
        dataGetAndDeal.arriveTimeConstrin = DataGetAndDeal.ArriveTimeConstrain.NIGHT;
    }

    public void handlecheckbox6(ActionEvent event) {
        checkbox7.setSelected(false);
        checkbox8.setSelected(false);
        checkbox9.setSelected(false);
        checkbox10.setSelected(false);
        dataGetAndDeal.costTimeConstrain = DataGetAndDeal.CostTimeConstrain.ALL;
    }

    public void handlecheckbox7(ActionEvent event) {
        checkbox6.setSelected(false);
        checkbox8.setSelected(false);
        checkbox9.setSelected(false);
        checkbox10.setSelected(false);
        dataGetAndDeal.costTimeConstrain = DataGetAndDeal.CostTimeConstrain.ZEROTOFOUR;
    }

    public void handlecheckbox8(ActionEvent event) {
        checkbox6.setSelected(false);
        checkbox7.setSelected(false);
        checkbox9.setSelected(false);
        checkbox10.setSelected(false);
        dataGetAndDeal.costTimeConstrain = DataGetAndDeal.CostTimeConstrain.FOURTOEIGHT;
    }

    public void handlecheckbox9(ActionEvent event) {
        checkbox6.setSelected(false);
        checkbox7.setSelected(false);
        checkbox8.setSelected(false);
        checkbox10.setSelected(false);
        dataGetAndDeal.costTimeConstrain = DataGetAndDeal.CostTimeConstrain.EIGHTTOTWELVE;
    }

    public void handlecheckbox10(ActionEvent event) {
        checkbox6.setSelected(false);
        checkbox7.setSelected(false);
        checkbox8.setSelected(false);
        checkbox9.setSelected(false);
        dataGetAndDeal.costTimeConstrain = DataGetAndDeal.CostTimeConstrain.MORETWELVE;
    }

    public void handleReturn3(ActionEvent event) {
        timelimit2.setLayoutX(1057);
        timelimit2.setLayoutY(911);
    }

    public void handlemakechoice(ActionEvent event) {
        timelimit1.setVisible(false);
    }

    public void handleclose(ActionEvent event) {
        LastFrame.setLayoutX(361);
        LastFrame.setLayoutY(813);
        Rectangle1.setLayoutX(0);
        Rectangle1.setLayoutY(795);
    }

    public void handleSearch(ActionEvent event) throws Exception {
        //date.getValue();
        configInform.setDate(date.getValue().toString());
        //System.out.print(configInform.getDate());
        //System.out.print("\n");
        //
        configInform.setStartStation(new String(startcity.getText().getBytes("utf-8")));
        //System.out.print(startcity.getText());
        // System.out.print("\n");
        // System.out.print(tocity.getText());
        //System.out.print("\n");
        configInform.setArriveStation(new String(tocity.getText().getBytes("utf-8")));
        configInform.setUrlFlght(configInform.getStartStation(), configInform.getArriveStation(), configInform.getDate(), 0);

         //ConfigInform configInform = new ConfigInform();
        //configInform.setStartStation("北京");
       // configInform.setArriveStation("上海");
        //configInform.setDate("2017-03-24");
       // configInform.setUrlFlght("北京","上海","2017-03-24",0);
       // DataGetAndDeal dataGetAndDeal = new DataGetAndDeal();
        //测试代码，可以输出
       dataGetAndDeal.GetTrainTicketInfoAndDeal(configInform);
        //System.out.print('\n');
        dataGetAndDeal.DealFlghtInfo(configInform);
        ArrayList<DataSave> recommendt=new ArrayList<DataSave>();
       // System.out.print(dataGetAndDeal.arriveTimeConstrin);
        //System.out.print(dataGetAndDeal.startTimeConstrain);
        //System.out.print(dataGetAndDeal.costTimeConstrain);
        int numbers=10;
        switch (choice) {
            case 1:
                Collections.sort(dataGetAndDeal.InfoTicketList, dataGetAndDeal.comparatorByPrice);
                numbers = numbers < dataGetAndDeal.TicketNumbers ?10:dataGetAndDeal.TicketNumbers;
                recommendt = dataGetAndDeal.InfoTicketList;
                break;
            case 2:
                Collections.sort(dataGetAndDeal.InfoTicketList, dataGetAndDeal.comparatorByCostTime);
                numbers = numbers < dataGetAndDeal.TicketNumbers ?10:dataGetAndDeal.TicketNumbers;
                recommendt = dataGetAndDeal.InfoTicketList;
                break;
            case 3:
                recommendTicket.TimeAndMoneyBothOk(dataGetAndDeal);
                numbers = numbers < recommendTicket.recommendNumbers ?10:recommendTicket.recommendNumbers ;
                recommendt =recommendTicket.reommendticket;
                break;
            default:
                break;

        }
        int i = 0;


//        if (dataGetAndDeal.TicketNumbers < 10) {
//           m = dataGetAndDeal.TicketNumbers;
//           //System.out.print(m);
//        }

        if(i<numbers) {
            la00.setText(recommendt.get(i).getTrainCode());
            la01.setText(recommendt.get(i).getSeatType());
            la02.setText(recommendt.get(i).getStartTime() + '/' + recommendt.get(i).getEndTime());
            la03.setText(recommendt.get(i).getStartStation() + '/' + recommendt.get(i).getArriveStation());
            la04.setText(recommendt.get(i).getCostTime() / 60 + "小时" + recommendt.get(i).getCostTime() % 60 + "分");
            la05.setText(recommendt.get(i).getTicketPrice() + "元");
            la06.setText(recommendt.get(i).getSeatNum() + " ");
            i++;
        }
       if(i<numbers) {
           la10.setText(recommendt.get(i).getTrainCode());
           la11.setText(recommendt.get(i).getSeatType());
           la12.setText(recommendt.get(i).getStartTime() + '/' +recommendt.get(i).getEndTime());
           la13.setText(recommendt.get(i).getStartStation() + '/' + recommendt.get(i).getArriveStation());
           la14.setText(recommendt.get(i).getCostTime() / 60 + "小时" + recommendt.get(i).getCostTime() % 60 + "分");
           la15.setText(recommendt.get(i).getTicketPrice() + "元");
           la16.setText(recommendt.get(i).getSeatNum() + " ");
           i++;
       }
       if(i<numbers) {
           la20.setText(recommendt.get(i).getTrainCode());
           la21.setText(recommendt.get(i).getSeatType());
           la22.setText(recommendt.get(i).getStartTime() + '/' + recommendt.get(i).getEndTime());
           la23.setText(recommendt.get(i).getStartStation() + '/' + recommendt.get(i).getArriveStation());
           la24.setText(recommendt.get(i).getCostTime() / 60 + "小时" + recommendt.get(i).getCostTime() % 60 + "分");
           la25.setText(recommendt.get(i).getTicketPrice() + "元");
           la26.setText(recommendt.get(i).getSeatNum() + " ");
           i++;
       }
       if(i<numbers) {
           la30.setText(recommendt.get(i).getTrainCode());
           la31.setText(recommendt.get(i).getSeatType());
           la32.setText(recommendt.get(i).getStartTime() + '/' + recommendt.get(i).getEndTime());
           la33.setText(recommendt.get(i).getStartStation() + '/' + recommendt.get(i).getArriveStation());
           la34.setText(recommendt.get(i).getCostTime() / 60 + "小时" + recommendt.get(i).getCostTime() % 60 + "分");
           la35.setText(recommendt.get(i).getTicketPrice() + "元");
           la36.setText(recommendt.get(i).getSeatNum() + " ");
           i++;
       }
       if(i<numbers) {
           la40.setText(recommendt.get(i).getTrainCode());
           la41.setText(recommendt.get(i).getSeatType());
           la42.setText(recommendt.get(i).getStartTime() + '/' + recommendt.get(i).getEndTime());
           la43.setText(recommendt.get(i).getStartStation() + '/' + recommendt.get(i).getArriveStation());
           la44.setText(recommendt.get(i).getCostTime() / 60 + "小时" +recommendt.get(i).getCostTime() % 60 + "分");
           la45.setText(recommendt.get(i).getTicketPrice() + "元");
           la46.setText(recommendt.get(i).getSeatNum() + " ");
           i++;
       }
       if(i<numbers) {
           la50.setText(recommendt.get(i).getTrainCode());
           la51.setText(recommendt.get(i).getSeatType());
           la52.setText(recommendt.get(i).getStartTime() + '/' + recommendt.get(i).getEndTime());
           la53.setText(recommendt.get(i).getStartStation() + '/' + recommendt.get(i).getArriveStation());
           la54.setText(recommendt.get(i).getCostTime() / 60 + "小时" + recommendt.get(i).getCostTime() % 60 + "分");
           la55.setText(recommendt.get(i).getTicketPrice() + "元");
           la56.setText(recommendt.get(i).getSeatNum() + " ");
           i++;
       }
        if(i<numbers) {
            la60.setText(recommendt.get(i).getTrainCode());
            la61.setText(recommendt.get(i).getSeatType());
            la62.setText(recommendt.get(i).getStartTime() + '/' + recommendt.get(i).getEndTime());
            la63.setText(recommendt.get(i).getStartStation() + '/' + recommendt.get(i).getArriveStation());
            la64.setText(recommendt.get(i).getCostTime() / 60 + "小时" + recommendt.get(i).getCostTime() % 60 + "分");
            la65.setText(recommendt.get(i).getTicketPrice() + "元");
            la66.setText(recommendt.get(i).getSeatNum() + " ");
            i++;
        }
        if(i<numbers) {
            la70.setText(recommendt.get(i).getTrainCode());
            la71.setText(recommendt.get(i).getSeatType());
            la72.setText(recommendt.get(i).getStartTime() + '/' + recommendt.get(i).getEndTime());
            la73.setText(recommendt.get(i).getStartStation() + '/' + recommendt.get(i).getArriveStation());
            la74.setText(recommendt.get(i).getCostTime() / 60 + "小时" + recommendt.get(i).getCostTime() % 60 + "分");
            la75.setText(recommendt.get(i).getTicketPrice() + "元");
            la76.setText(recommendt.get(i).getSeatNum() + " ");
            i++;
        }
        if(i<numbers) {
            la80.setText(recommendt.get(i).getTrainCode());
            la81.setText(recommendt.get(i).getSeatType());
            la82.setText(recommendt.get(i).getStartTime() + '/' + recommendt.get(i).getEndTime());
            la83.setText(recommendt.get(i).getStartStation() + '/' + recommendt.get(i).getArriveStation());
            la84.setText(recommendt.get(i).getCostTime() / 60 + "小时" + recommendt.get(i).getCostTime() % 60 + "分");
            la85.setText(recommendt.get(i).getTicketPrice() + "元");
            la86.setText(recommendt.get(i).getSeatNum() + " ");
            i++;
        }
        if(i<numbers) {
            la90.setText(recommendt.get(i).getTrainCode());
            la91.setText(recommendt.get(i).getSeatType());
            la92.setText(recommendt.get(i).getStartTime() + '/' + recommendt.get(i).getEndTime());
            la93.setText(recommendt.get(i).getStartStation() + '/' + recommendt.get(i).getArriveStation());
            la94.setText(recommendt.get(i).getCostTime() / 60 + "小时" + recommendt.get(i).getCostTime() % 60 + "分");
            la95.setText(recommendt.get(i).getTicketPrice() + "元");
            la96.setText(recommendt.get(i).getSeatNum() + " ");
            i++;
        }
        //i=0;
       /* for (i = 0; i < m; i++) {


            // System.out.print("第"+i+"次：");
            //System.out.print("出发时间：");
            labels[i][0].setText("段升顺");
            //labels[i][0].setText(dataGetAndDeal.InfoTicketList.get(i).getTrainCode());
            //System.out.print('\n');
            labels[i][1].setText(dataGetAndDeal.InfoTicketList.get(i).getSeatType());
            labels[i][2].setText(dataGetAndDeal.InfoTicketList.get(i).getStartTime() + '/' + dataGetAndDeal.InfoTicketList.get(i).getEndTime());
            labels[i][3].setText(dataGetAndDeal.InfoTicketList.get(i).getStartStation() + '/' + dataGetAndDeal.InfoTicketList.get(i).getArriveStation());
            labels[i][4].setText(dataGetAndDeal.InfoTicketList.get(i).getCostTime() / 60 + "小时" + dataGetAndDeal.InfoTicketList.get(i).getCostTime() % 60 + "分");
            labels[i][5].setText(dataGetAndDeal.InfoTicketList.get(i).getTicketPrice() + "元");
            labels[i][6].setText(dataGetAndDeal.InfoTicketList.get(i).getSeatNum() + "个");

        }*/
     /* for(DataSave s:dataGetAndDeal.InfoTicketList)
        {
            i++;
            System.out.print("第"+i+"次：");
            System.out.print("出发时间：");
            System.out.print(s.getStartTime());
            System.out.print('\n');
            System.out.print("到达时间：");
            System.out.print(s.getEndTime());
            System.out.print('\n');
            System.out.print("车次：");
            System.out.print(s.getTrainCode());
            System.out.print('\n');
            System.out.print("座位类型：");

            System.out.print(s.getSeatType());
            System.out.print('\n');

            System.out.print("座位数量：");
            System.out.print(s.getSeatNum());
            System.out.print('\n');
            System.out.print("出行时间");

            System.out.print(s.getCostTime());


            System.out.print('\n');


            System.out.print("票价:");
            System.out.print(s.getTicketPrice());
            System.out.print('\n');

        }
*/
      //  LastFrame.setVisible(true);
        LastFrame.setLayoutX(361);
        LastFrame.setLayoutY(14);
         //Paneout1.setVisible(true);
        Rectangle1.setLayoutX(0);
        Rectangle1.setLayoutY(0);
         //Paneout2.setVisible(true);




    /*public void showDateTime(ActionEvent event) {

        System.out.println("Button Clicked!");

        Date now= new Date();

        DateFormat df = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
        String dateTimeString = df.format(now);
        // Show in VIEW


    }

*/


    }
}
