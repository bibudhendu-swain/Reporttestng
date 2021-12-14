package com.test.abc;


import org.apache.commons.io.FileUtils;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import sun.util.calendar.CalendarDate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


public class ReportSuite implements ISuiteListener {
    //  PdfReport p;
    static LinkedHashMap<String, ArrayList<Integer>> suiteMap;
    static LinkedHashMap<String, ArrayList<ArrayList<String>>> scenarioMap;
    String suiteName; String testName="";
    int total;int passed;
    int failed;
    int skipped;
    int tpassed=0;
    int tfailed=0;
    int tskipped=0;

    String method;
    String status;
    String fReason;
    String ssLink;
    File reportParentDir;
    File reportDir;
    static File ssDir;
    File htmlTemplateFile;
    private String endtime;
    private String starttime;
    private String totaltime;
    private Date startDate;
    static String browser;



    public void onStart(ISuite suite) {
        // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        suiteMap = new LinkedHashMap<String, ArrayList<Integer>>();
        scenarioMap= new LinkedHashMap<String, ArrayList<ArrayList<String>>>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss");
        startDate = new Date();
        System.out.println(formatter.format(startDate));
        suiteName=suite.getName();

        reportParentDir = new File(System.getProperty("user.dir")+"Reports");
        if (!reportParentDir.exists()) {
            reportParentDir.mkdir();
        }
        File srcStyle = new File("Template" + File.separator + "style");
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date1 = new Date();
        starttime=formatter1.format(date1);
        reportDir = new File(System.getProperty("user.dir")+ File.separator +"Reports" + File.separator + formatter.format(startDate));
        reportDir.mkdir();
        File home=new File("Template" + File.separator + "Suite.html");
        try {
            FileUtils.copyDirectory(srcStyle,reportDir );
            FileUtils.copyFileToDirectory(home,reportDir);
        } catch (IOException e) {
            e.printStackTrace();
        }


        htmlTemplateFile = new File(reportDir.getAbsolutePath()+File.separator+"Suite.html");


        ssDir= new File(reportDir+File.separator+"Screenshots");
        if(!ssDir.exists()){
            ssDir.mkdir();
        }
    }

    public void onFinish(ISuite suite) {
       // System.out.println("suiteMap is " + suiteMap);

       // System.out.println(formatter.format(date));
       /* long diff = d2.getTime() - d1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        System.out.print(diffDays + " days, ");
        System.out.print(diffHours + " hours, ");
        System.out.print(diffMinutes + " minutes, ");
        System.out.print(diffSeconds + " seconds.");*/
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date1 = new Date();
        endtime=formatter1.format(date1);

        try {



            String htmlString = null;
            htmlString = FileUtils.readFileToString(htmlTemplateFile,"UTF-8");
            htmlString = htmlString.replace("$suiteTitle", suiteName);

            StringBuilder tr= new StringBuilder();


            String tbd;

              Set<String> s=suiteMap.keySet();
              Iterator<String> itr= s.iterator();
              while(itr.hasNext()){
                 // System.out.println(itr.next());
                  testName=itr.next();
                  passed= suiteMap.get(testName).get(0);
                  failed= suiteMap.get(testName).get(1);
                  skipped= suiteMap.get(testName).get(2);
                  total= suiteMap.get(testName).get(3);
                  tpassed=tpassed+passed;
                  tfailed=tfailed+failed;
                  tskipped=tskipped+skipped;

                  tbd= "<tr style=\"height: auto;\">\n" +
                          "                <td class=\"u-border-1 u-border-palette-4-base u-first-column u-table-cell\">\n" +
                          "                  <a href=\""+testName+".html\" class=\"u-active-none u-border-none u-btn u-button-link u-button-style u-hover-none u-none u-text-palette-1-base u-btn-1\" target=\"_blank\">"+testName+"</a>\n" +
                          "                </td>\n" +
                          "                <td class=\"u-border-1 u-border-grey-30 u-table-cell\">"+passed+"</td>\n" +
                          "                <td class=\"u-border-1 u-border-grey-30 u-table-cell\">"+failed+"</td>\n" +
                          "                <td class=\"u-border-1 u-border-grey-30 u-table-cell\">"+skipped+"</td>\n" +
                          "                <td class=\"u-border-1 u-border-grey-30 u-table-cell\">"+total+"</td>\n" +
                          "              </tr>";
                  tr.append(tbd);

              }
            long diff = date1.getTime()-startDate.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            System.out.println(diffSeconds);
            System.out.println(diffMinutes);
            System.out.println(diffHours);
            System.out.println(diffDays);
            htmlString = htmlString.replace("$tr", tr.toString());
            htmlString = htmlString.replace("$startTime", starttime).replace("$browser", Test1.browser).replace("$totalTime", String.valueOf(diffHours)+" hr "+String.valueOf(diffMinutes)+" mm "+String.valueOf(diffSeconds)+" ss")
                    .replace("$suiteName", suiteName).replace("$p",String.valueOf(tpassed)).replace("$f",String.valueOf(tfailed)).replace("$s",String.valueOf(tskipped));
            FileUtils.writeStringToFile(htmlTemplateFile, htmlString);

            Iterator<String> it= scenarioMap.keySet().iterator();


            while(it.hasNext()){
                String key=it.next();
                File scenario=new File("Template" + File.separator + "Scenario.html");
                File destScenarioFile=new File(reportDir+File.separator+key+".html");
                StringBuilder ssb= new StringBuilder();
                String str;
                FileUtils.copyFile(scenario,destScenarioFile);
                for(int i=0;i<scenarioMap.get(key).size();i++){
                    //System.out.println(key);
                    System.out.println(scenarioMap.get(key).get(i));
                        String screenshot="";
                        method= scenarioMap.get(key).get(i).get(0);
                        status=scenarioMap.get(key).get(i).get(1);
                        if(status.equalsIgnoreCase("PASSED")){
                            status= "<span style=\"color: green;\">"+status+"</span>";
                        }else if(status.equalsIgnoreCase("FAILED")){
                            status= "<span style=\"color: red;\">"+status+"</span>";
                            screenshot="Screenshot";
                        }
                        fReason=scenarioMap.get(key).get(i).get(2);
                        ssLink=scenarioMap.get(key).get(i).get(3);


                        str="<tr style=\"height: auto;\">\n" +
                                "                <td class=\"u-border-1 u-border-grey-30 u-first-column u-grey-5 u-table-cell u-table-cell-9\">"+method+"</td>\n" +
                                "                <td class=\"u-border-1 u-border-grey-30 u-table-cell\">"+status+"</td>\n" +
                                "                <td class=\"u-border-1 u-border-grey-30 u-table-cell\">"+fReason+"</td>\n" +
                                "                <td class=\"u-border-1 u-border-grey-30 u-table-cell u-table-cell-8\">\n" +
                                "                  <a href="+ssLink+" class=\"u-active-none u-border-none u-btn u-button-link u-button-style u-file-link u-hover-none u-none u-text-palette-1-base u-btn-1\" target=\"_blank\">"+screenshot+"</a>\n" +
                                "                </td>\n" +
                                "              </tr>";
                        ssb.append(str);
                    }


                String shtmlString = null;
                shtmlString = FileUtils.readFileToString(destScenarioFile,"UTF-8");
                shtmlString=shtmlString.replace("$ScenarioName", key);
                shtmlString = shtmlString.replace("$str", ssb.toString());
                shtmlString = shtmlString.replace("$scenarioTitle", key);
                FileUtils.writeStringToFile(destScenarioFile, shtmlString);

                }





        } catch (IOException e) {
            e.printStackTrace();




        }
    }

}



