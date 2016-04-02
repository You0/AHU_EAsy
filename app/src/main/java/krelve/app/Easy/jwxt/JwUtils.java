package krelve.app.Easy.jwxt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import krelve.app.Easy.Kpplication;
import krelve.app.Easy.image.ImagePreProcess;
import krelve.app.Easy.net.HttpConnectionUtils;


public class JwUtils {
    public static String NAME = null;
    private static String GBKUrl = null;
    private String NJ = null;
    private static String BaseUrl = null;
    private String Username = null;
    private String Password = null;
    private static String Site = null;
    private String Name = null;
    private String Encrypt = null;
    private static String CrossUrl = null;
    private static String SpecialUrl = null;
    private static String SportUrl = null;
    private static String Subject_content = null;
    private static String Grade_content = null;
    public String TeacherUrl = null;
    public ArrayList<String> SubjectContent = new ArrayList<String>();
    public ArrayList<String> CrossSubjectContent = new ArrayList<String>();
    public ArrayList<String> SportSubjectContent = new ArrayList<String>();
    public ArrayList<String> TeacherInfoContent = new ArrayList<String>();
    public int flag = 0;
    private ImagePreProcess imagePreProcess;
    private String ImageUrl = null;
    public static String Cookies = null;
    public ArrayList<String> PostData = new ArrayList<String>();
    public String __VIEWSTATE;
    public String __EVENTVALIDATION;
    public static JwUtils jwUtils = new JwUtils();
    static int isFirst = 0;

    private JwUtils(){

    }

    public void Init(String Site,String Username,String Password)
    {
        this.Username = Username;
        this.Password = Password;
        this.Site = Site;
        BaseUrl = "http://xk"+Site+".ahu.cn/";
        ImageUrl = "http://xk"+ Site +".ahu.cn/CheckCode.aspx";
        if(Site.equals("1")){
            ImageUrl = "http://xk"+ Site +".ahu.cn/CheckCode1.aspx";
        }
        CrossUrl = BaseUrl+ "zylb.aspx?xh="+Username+"&nj="+"20"+Username.substring(2,4);
        imagePreProcess = new ImagePreProcess(Kpplication.getContext(),ImageUrl);
    }



    public String chinaToUnicode(String str){
        String result="";
        for (int i = 0; i < str.length(); i++){
            int chr1 = (char) str.charAt(i);
            if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文)
                result+="%25u" + Integer.toHexString(chr1);
            }else{
                result+=str.charAt(i);
            }
        }
        return result;
    }






    public boolean getName()
    {
        String result;
        int length;
        String tempUrl = BaseUrl+"xs_main.aspx?xh="+Username;
        HttpConnectionUtils util = new HttpConnectionUtils(tempUrl);
        HttpURLConnection connection =  util.GetConnection("GET","",this.Cookies);
        result = Read(connection);
        util.release();
        String regex = "<span id=\"xhxm\">.*?同学";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result);
        if (!matcher.find()) {
            return false;
        }
        result = matcher.group(0);
        length = result.length();
        result = result.substring(16, length - 2);
        System.out.println(result);
        NAME = result;
        result = chinaToUnicode(result);

        GBKUrl = BaseUrl + "xsxk.aspx?xh="+Username+"&xm="+result+"&gnmkdm=N121101";
        SportUrl = BaseUrl+"xstyk.aspx?xh="+Username+"&xm="+result+"&gnmkdm=N121102";
        try{
            Subject_content = BaseUrl + "xsxkqk.aspx?xh=" + Username + "&xm=" + URLEncoder.encode(result, "gb2312") + "&gnmkdm=N121615";
            Grade_content = BaseUrl + "xscjcx.aspx?xh=" + Username + "&xm=" + chinaToUnicode(result) + "&gnmkdm=N121605";
            //http://xk2.ahu.cn/xscjcx.aspx?xh=E41414005&xm=%B2%CC%BD%A8%D3%EE&gnmkdm=N121605
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;

    }









    public String ShuaKe(ArrayList<String> postdata)
    {
        String response = null;
        int length = 0;

        try{
            for(int i=0;i<postdata.size();i=i+2)
            {
                HttpConnectionUtils util = new HttpConnectionUtils(postdata.get(i));
                HttpURLConnection connection =  util.GetConnection("POST",postdata.get(i+1),this.Cookies);
                util.connect();
                response = Read(connection);
                //System.out.println(response);
                String regex = "alert.*?script";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response);
                if(!matcher.find())
                {
                    return ("出现异常！！");

                }

                length = matcher.group(0).length();
                response = matcher.group(0).substring(7, length-11);
                return "正在拼命抢课....\n消息反馈:"+response;

            }
        }catch(Exception e){
            return "服务器崩溃中...";
        }

        return "出现异常！";
    }



    public String Read(HttpURLConnection Connection) {
        BufferedReader is = null;
        StringBuffer result = new StringBuffer();
        String line = null;
        try {
            is = new BufferedReader(new InputStreamReader(Connection.getInputStream(), "gbk"));
            // is = new BufferedReader(new InputStreamReader());
            line = is.readLine();

            while (line != null) {
                result.append(line + "\n");
                line = is.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();

    }



    public String ReadWithOut(HttpURLConnection Connection) {
        BufferedReader is = null;
        StringBuffer result = new StringBuffer();
        String line = null;
        try {
            is = new BufferedReader(new InputStreamReader(Connection.getInputStream(), "gbk"));
            // is = new BufferedReader(new InputStreamReader());
            line = is.readLine();

            while (line != null) {
                result.append(line);
                line = is.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();

    }





    public String GETEVENTVALIDATION(String Html) {
        int end = 0;
        String regEx = "id=\"__EVENTVALIDATION\" value=.*\"";
        Pattern pattern = null;
        Matcher matcher = null;
        try{
            pattern = Pattern.compile(regEx);
            matcher = pattern.matcher(Html);
            if (!matcher.find()) {
                return null;
            }
            end = matcher.group(0).length();
            return URLEncoder.encode(matcher.group(0).substring(30, end - 1), "utf-8");

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }





    public String GETVIEWSTATE(String Html) {
        int end = 0;
        String regEx = "id=\"__VIEWSTATE\" value=.*\"";
        Pattern pattern = null;
        Matcher matcher = null;
        try{
            pattern = Pattern.compile(regEx);
            matcher = pattern.matcher(Html);
            if (!matcher.find()) {
                return null;
            }
            end = matcher.group(0).length();
            return URLEncoder.encode(matcher.group(0).substring(24, end - 1), "utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }

        return  null;


    }







    public ArrayList<String> ParseMainHtml(String result)
    {
        ArrayList<String> ParseContent = new ArrayList<String>();
        Document doc = Jsoup.parse(result);
        Elements content = doc.getElementsByClass("formbox");
        doc = Jsoup.parse(content.toString());
        Element Count = doc.getElementById("Label4");
        if(Count!=null){
            ParseContent.add(Count.text());
        }
        content = doc.getElementsByTag("a");
        for (Element link : content) {
            if(link.hasAttr("onclick"))
            {
                ParseContent.add(link.text());
                ParseContent.add(link.attr("onclick").toString());
            }
        }

        return ParseContent;
    }









    public void GetTeacherInfo(String TeacherUrl)
    {
        this.TeacherUrl = TeacherUrl;
        String response = null;
        //ArrayList<String> arrayList = new ArrayList<String>();

        try{
            HttpConnectionUtils util = new HttpConnectionUtils(TeacherUrl);
            HttpURLConnection connection =  util.GetConnection("GET","",this.Cookies);
            response = Read(connection);
            util.release();


            Document doc = Jsoup.parse(response);
            Elements elements = doc.getElementsByTag("tr");
            for(Element e:elements)
            {
                if(!e.hasAttr("class")){
                    TeacherInfoContent.add(e.text().toString().substring(2, 48));
                    Elements tempelms = e.select("td");
                    for(int i=0;i<tempelms.size();i++){
                        if(i==1){
                            TeacherInfoContent.add(tempelms.get(i).toString());
                        }
                    }

                }
            }

            // System.out.println("请选择老师");

            // for(int i=0;i<arrayList.size()/2;i++)
            // {
            //     System.out.println(i+":"+arrayList.get(i*2));
            // }

            // Scanner s=new Scanner(System.in);
            // int choose = s.nextInt();

            __VIEWSTATE = GETVIEWSTATE(response);
            __EVENTVALIDATION = GETEVENTVALIDATION(response);




            //System.out.println(tempPostdata);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void ChooseTeacher(int pos)
    {
        String tempPostdata=null;
        try{
            tempPostdata =
                    "__EVENTTARGET=Button1&__EVENTARGUMENT=&__VIEWSTATE="
                            + __VIEWSTATE+"&xkkh="+ URLEncoder.encode(TeacherInfoContent.get(pos * 2 + 1).substring(61, 90), "GBK")+
                            "&RadioButtonList1=0&__EVENTVALIDATION="+
                            __EVENTVALIDATION;
        }catch (Exception e){
            e.printStackTrace();
        }

        PostData.add(TeacherUrl);
        PostData.add(tempPostdata);
    }







    public void PublicSubject(){
        String result = null;


        try{
            HttpConnectionUtils utils = new HttpConnectionUtils(GBKUrl);
            HttpURLConnection connection = utils.GetConnection("GET", "", this.Cookies);
            connection.setRequestProperty("Referer", BaseUrl+"xs_main.aspx?xh="+Username);
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36 OPR/34.0.2036.50");

            result = Read(connection);
            utils.release();
            __VIEWSTATE = GETVIEWSTATE(result);
            __EVENTVALIDATION = GETEVENTVALIDATION(result);
            ArrayList<String> arrayList = new ArrayList<String>();

            arrayList.add("__EVENTTARGET");arrayList.add("#");
            arrayList.add("__EVENTARGUMENT");arrayList.add("#");
            arrayList.add("__LASTFOCUS");arrayList.add("#");
            arrayList.add("__VIEWSTATE");arrayList.add(__VIEWSTATE);
            arrayList.add("zymc");arrayList.add("%C8%AB%B2%BF%BF%CE%B3%CC");
            arrayList.add("xx");arrayList.add("#");
            arrayList.add("Button3");arrayList.add("%CC%D8%CA%E2%BF%CE%B3%CC");
            arrayList.add("__EVENTVALIDATION");arrayList.add(__EVENTVALIDATION);

            connection = utils.GetConnection("POST", arrayList, this.Cookies);
            utils.connect();
            result = Read(connection);

            __VIEWSTATE = GETVIEWSTATE(result);
            __EVENTVALIDATION = GETEVENTVALIDATION(result);


            arrayList = ParseMainHtml(result);
            SubjectContent.addAll(arrayList);


            arrayList.clear();
            arrayList.add("__EVENTTARGET");arrayList.add("kcmcgrid%24ctl14%24ctl01");
            arrayList.add("__EVENTARGUMENT");arrayList.add("#");
            arrayList.add("__LASTFOCUS");arrayList.add("#");
            arrayList.add("__VIEWSTATE");arrayList.add(__VIEWSTATE);
            arrayList.add("zymc");arrayList.add("%C8%AB%B2%BF%BF%CE%B3%CC");
            arrayList.add("xx");arrayList.add("#");
            arrayList.add("__EVENTVALIDATION");arrayList.add(__EVENTVALIDATION);
            connection = utils.GetConnection("POST", arrayList, this.Cookies);
            utils.connect();
            result = Read(connection);
            arrayList = ParseMainHtml(result);
            arrayList.remove(0);
            SubjectContent.addAll(arrayList);

            arrayList.clear();
            for(int i=1;i<SubjectContent.size();i=i+6)
            {
                arrayList.add(this.BaseUrl + SubjectContent.get(i+1).substring(13, 106));
                arrayList.add(SubjectContent.get(i+2));
            }

            SubjectContent.clear();
            SubjectContent.addAll(arrayList);

            // Scanner s=new Scanner(System.in);
            // int choose = s.nextInt();
            //System.out.println(SubjectContent.get(choose*2));


        }catch(Exception e){
            e.printStackTrace();
        }




    }

    public String ListBox1;
    public void querySportsTeacher(int choose){
        ArrayList<String> arrayList = new ArrayList<String>();
        String result;
        HttpConnectionUtils utils = new HttpConnectionUtils(SportUrl);
        HttpURLConnection connection;
        // int choose = in.nextInt();
        ListBox1 = SportSubjectContent.get(choose).substring(0, 7);
        SportSubjectContent.clear();
        arrayList.add("__EVENTTARGET");arrayList.add("ListBox1");
        arrayList.add("__EVENTARGUMENT");arrayList.add("#");
        arrayList.add("__LASTFOCUS");arrayList.add("#");
        arrayList.add("__VIEWSTATE");arrayList.add(__VIEWSTATE);
        arrayList.add("DropDownList1");arrayList.add("%CF%EE%C4%BF");
        arrayList.add("ListBox1");arrayList.add(ListBox1);
        arrayList.add("__EVENTVALIDATION");arrayList.add(__EVENTVALIDATION);

        connection = utils.GetConnection("POST", arrayList, this.Cookies);
        utils.connect();
        result = Read(connection);
        __VIEWSTATE = GETVIEWSTATE(result);
        __EVENTVALIDATION = GETEVENTVALIDATION(result);
        //System.out.println(result);
        SportSubjectContent.clear();
        Document doc = Jsoup.parse(result);
        Elements elms = doc.getElementsByTag("option");
        arrayList.clear();
        for(Element e:elms){
            SportSubjectContent.add(e.text());
            SportSubjectContent.add(e.toString());
        }
        // System.out.println("请选择编号:");
        // for(int i=56;i<arrayList.size()-flag;i=i+2)
        // {
        //     System.out.println(i+":"+arrayList.get(i) );
        // }
    }

    public void GetSportsTeacherPostData(int pos)
    {
        HttpConnectionUtils utils = new HttpConnectionUtils(SportUrl);
        HttpURLConnection connection;
        String result;
        pos = pos + 56;
        //System.out.println(URLEncoder.encode(arrayList.get(choose+1).substring(15, 77),"BGK"));
        //arrayList.clear();
        String ListBox2=null;
        try{
            ListBox2 = URLEncoder.encode(SportSubjectContent.get(pos + 1).substring(15, 77), "GBK");
        }catch (Exception e){
            e.printStackTrace();
        }
        SportSubjectContent.clear();
        SportSubjectContent.add("__EVENTTARGET");SportSubjectContent.add("ListBox2");
        SportSubjectContent.add("__EVENTARGUMENT");SportSubjectContent.add("#");
        SportSubjectContent.add("__LASTFOCUS");SportSubjectContent.add("#");
        SportSubjectContent.add("__VIEWSTATE");SportSubjectContent.add(__VIEWSTATE);
        SportSubjectContent.add("DropDownList1");SportSubjectContent.add("%CF%EE%C4%BF");
        SportSubjectContent.add("ListBox1");SportSubjectContent.add(ListBox1);
        SportSubjectContent.add("ListBox2");SportSubjectContent.add(ListBox2);
        SportSubjectContent.add("__EVENTVALIDATION");SportSubjectContent.add(__EVENTVALIDATION);


        connection = utils.GetConnection("POST", SportSubjectContent, this.Cookies);
        utils.connect();
        result = Read(connection);

        //System.out.println(result);


        __VIEWSTATE = GETVIEWSTATE(result);
        __EVENTVALIDATION = GETEVENTVALIDATION(result);


        String tempPostdata =
                "__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE="
                        + __VIEWSTATE+"&DropDownList1="+"%CF%EE%C4%BF"+"&ListBox1="+
                        "TY64005"+"&ListBox2="+ListBox2+
                        "&RadioButtonList1=0&button3="+"%D1%A1%B6%A8%BF%CE%B3%CC"+
                        "&__EVENTVALIDATION="+__EVENTVALIDATION;

        PostData.add(SportUrl);
        PostData.add(tempPostdata);



    }

    public void SportSubject(){

        String result = null;

        try {
            HttpConnectionUtils utils = new HttpConnectionUtils(SportUrl);
            HttpURLConnection connection = utils.GetConnection("GET", "", this.Cookies);

            result = Read(connection);
            utils.release();
            __VIEWSTATE = GETVIEWSTATE(result);
            __EVENTVALIDATION = GETEVENTVALIDATION(result);
            SportSubjectContent.clear();
            Document doc = Jsoup.parse(result);
            Elements elms = doc.getElementsByTag("option");
            for(Element e:elms){
                SportSubjectContent.add(e.text());
            }

            flag = 0;
            if(SportSubjectContent.size()==29){
                flag = 2;
            }


            // for(int i = 3;i<=27;i++){
            //     System.out.println(i+":"+SportSubjectContent.get(i));
            // }



        }catch(Exception e){


        }


    }





    public void SpecialSubject() {
        String result = null;
        //String tempUrl = "http://xk2.ahu.cn/xsxk.aspx?xh=E41414005&xm=%u8521%u5efa%u5b87&gnmkdm=N121101";

        try {
            HttpConnectionUtils utils = new HttpConnectionUtils(GBKUrl);
            HttpURLConnection connection = utils.GetConnection("GET", "", this.Cookies);
            connection.setRequestProperty("Referer", BaseUrl+"xs_main.aspx?xh="+Username);
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36 OPR/34.0.2036.50");

            result = Read(connection);
            utils.release();
            __VIEWSTATE = GETVIEWSTATE(result);
            __EVENTVALIDATION = GETEVENTVALIDATION(result);

            ArrayList<String> arrayList = new ArrayList<String>();

            arrayList.add("__EVENTTARGET");arrayList.add("#");
            arrayList.add("__EVENTARGUMENT");arrayList.add("#");
            arrayList.add("__LASTFOCUS");arrayList.add("#");
            arrayList.add("__VIEWSTATE");arrayList.add(__VIEWSTATE);
            arrayList.add("zymc");arrayList.add("#");
            arrayList.add("xx");arrayList.add("#");
            arrayList.add("Button2");arrayList.add("%D1%A1%D0%DE%BF%CE%B3%CC");
            arrayList.add("__EVENTVALIDATION");arrayList.add(__EVENTVALIDATION);

            connection = utils.GetConnection("POST", arrayList, this.Cookies);
            utils.connect();
            result = Read(connection);

            __VIEWSTATE = GETVIEWSTATE(result);
            __EVENTVALIDATION = GETEVENTVALIDATION(result);

            Document doc = Jsoup.parse(result);
            Element element = doc.getElementById("xnxq");
            String tempUrl = element.text().toString();
            tempUrl = tempUrl.substring(0, 9)+tempUrl.charAt(14);
            tempUrl = BaseUrl+"xskc.aspx?xnxq="+tempUrl+"&xh="+Username;
            utils.release();
            utils.setURL(tempUrl);
            connection = utils.GetConnection("GET", "", this.Cookies);
            result = Read(connection);
            // System.out.println(result);
            String tempV = GETVIEWSTATE(result);
            String tempE = GETEVENTVALIDATION(result);
            arrayList.clear();

            arrayList.add("__VIEWSTATE");arrayList.add(tempV);
            arrayList.add("ListBox1");arrayList.add("%C8%AB%B2%BF");
            arrayList.add("Button1");arrayList.add("%D1%A1++%B6%A8");
            arrayList.add("__EVENTVALIDATION");arrayList.add(tempE);
            connection = utils.GetConnection("POST", arrayList, this.Cookies);
            utils.connect();
            utils.release();

            utils.setURL(GBKUrl);
            arrayList.clear();

            arrayList.add("__EVENTTARGET");arrayList.add("zymc");
            arrayList.add("__EVENTARGUMENT");arrayList.add("#");
            arrayList.add("__LASTFOCUS");arrayList.add("#");
            arrayList.add("__VIEWSTATE");arrayList.add(__VIEWSTATE);
            arrayList.add("zymc");arrayList.add("%C8%AB%B2%BF%7C%7C%CB%D8%D6%CA%BD%CC%D3%FD%D1%A1%D0%DE%BF%CE5");
            arrayList.add("xx");arrayList.add("#");
            arrayList.add("__EVENTVALIDATION");arrayList.add(__EVENTVALIDATION);

            connection = utils.GetConnection("POST", arrayList, this.Cookies);
            utils.connect();
            result = Read(connection);
            utils.release();

            arrayList = ParseMainHtml(result);
            SubjectContent.addAll(arrayList);
            __VIEWSTATE = GETVIEWSTATE(result);
            __EVENTVALIDATION = GETEVENTVALIDATION(result);

            for(int i=1;i<=14;i++)
            {
                arrayList.clear();
                if(i<10){
                    arrayList.add("__EVENTTARGET");arrayList.add("kcmcgrid%24ctl14%24ctl0"+i);
                }else if(i==10){
                    arrayList.add("__EVENTTARGET");arrayList.add("kcmcgrid%24ctl14%24ctl"+i);
                }else if(i==11){
                    __EVENTVALIDATION = GETEVENTVALIDATION(result);
                    __VIEWSTATE = GETVIEWSTATE(result);
                    arrayList.add("__EVENTTARGET");arrayList.add("kcmcgrid%24ctl14%24ctl0"+(i-4));
                }else if(i>11&&i<14){
                    arrayList.add("__EVENTTARGET");arrayList.add("kcmcgrid%24ctl14%24ctl0"+(i-4));
                }
                else if(i==14){
                    arrayList.add("__EVENTTARGET");arrayList.add("kcmcgrid%24ctl14%24ctl"+(i-4));
                }
                arrayList.add("__EVENTARGUMENT");arrayList.add("#");
                arrayList.add("__LASTFOCUS");arrayList.add("#");
                arrayList.add("__VIEWSTATE");arrayList.add(__VIEWSTATE);
                arrayList.add("zymc");arrayList.add("%C8%AB%B2%BF%7C%7C%CB%D8%D6%CA%BD%CC%D3%FD%D1%A1%D0%DE%BF%CE5");
                arrayList.add("xx");arrayList.add("#");
                arrayList.add("__EVENTVALIDATION");arrayList.add(__EVENTVALIDATION);

                connection = utils.GetConnection("POST", arrayList, this.Cookies);
                utils.connect();
                result = Read(connection);
                arrayList =  ParseMainHtml(result);
                arrayList.remove(0);
                SubjectContent.addAll(arrayList);

            }

            arrayList.clear();
            for(int i=1;i<SubjectContent.size();i=i+6)
            {
                arrayList.add(this.BaseUrl + SubjectContent.get(i+1).substring(13, 106));
                arrayList.add(SubjectContent.get(i+2));
            }
            SubjectContent.clear();
            SubjectContent.addAll(arrayList);

            // System.out.println("请选择编号");
            // for(int i=0;i<SubjectContent.size()/2;i++)
            // {
            //     System.out.println(i+":"+SubjectContent.get(i*2+1));
            // }

            // Scanner s=new Scanner(System.in);
            // int choose = s.nextInt();
            //System.out.println(SubjectContent.get(choose*2));
            // GetTeacherInfo(SubjectContent.get(choose*2));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    public void queryCrossTeacher(int pos)
    {
        GetTeacherInfo(this.BaseUrl+ CrossSubjectContent.get(pos*3+1));
    }




    public String GrageContent()
    {
        String result;
        ArrayList<String> arrayList = new ArrayList<>();
        try{
            HttpConnectionUtils utils = new HttpConnectionUtils(Grade_content);
            //http://xk2.ahu.cn/xscjcx.aspx?xh=E41414005&xm=%B2%CC%BD%A8%D3%EE&gnmkdm=N121605
            //http://xk2.ahu.cn/xscjcx.aspx?xh=E41414005&xm=%2525u8521%2525u5efa%2525u5b87&gnmkdm=N121605

            System.out.println(Grade_content);
            HttpURLConnection connection = utils.GetConnection("GET", "", this.Cookies);
            connection.setRequestProperty("Referer", BaseUrl+"xs_main.aspx?xh="+Username);
            result = Read(connection);
            //System.out.println(result);
            __VIEWSTATE = GETVIEWSTATE(result);
            __EVENTVALIDATION = GETEVENTVALIDATION(result);

            arrayList.clear();
            arrayList.add("__EVENTTARGET");arrayList.add("#");
            arrayList.add("__EVENTARGUMENT");arrayList.add("#");
            arrayList.add("__VIEWSTATE");arrayList.add(__VIEWSTATE);
            arrayList.add("hidLanguage");arrayList.add("#");
            arrayList.add("ddlXN");arrayList.add("#");
            arrayList.add("ddlXQ");arrayList.add("#");
            arrayList.add("ddl_kcxz");arrayList.add("#");
            arrayList.add("btn_zcj");arrayList.add("%C0%FA%C4%EA%B3%C9%BC%A8");
            arrayList.add("__EVENTVALIDATION");arrayList.add(__EVENTVALIDATION);

            connection = utils.GetConnection("POST", arrayList, Cookies);
            connection.setRequestProperty("Referer", Grade_content);
//            connection.setRequestProperty("User-Agent",
//                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36 OPR/34.0.2036.50");
            utils.connect();

            result = ReadWithOut(connection);
            utils.release();
            return result;

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }





    public String ClassContent(){
        String result;
        try{
            HttpConnectionUtils utils = new HttpConnectionUtils(Subject_content);
            HttpURLConnection connection = utils.GetConnection("GET", "", this.Cookies);
            connection.setRequestProperty("Referer", BaseUrl+"xs_main.aspx?xh="+Username);
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36 OPR/34.0.2036.50");

            result = ReadWithOut(connection);
            utils.release();
            return result;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }





    public void CrossSubject(String SubjectName) {
        String result = null;
        HttpConnectionUtils utils = new HttpConnectionUtils(CrossUrl);

        try {
            HttpURLConnection connection = utils.GetConnection("GET", "", this.Cookies);
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36 OPR/34.0.2036.50");

            result = Read(connection);
            utils.release();

            __VIEWSTATE = GETVIEWSTATE(result);
            __EVENTVALIDATION = GETEVENTVALIDATION(result);

            ArrayList<String> arrayList = new ArrayList<String>();
            //          Scanner in = new Scanner(System.in);
//            System.out.println("请搜索课程名字：");
            SubjectName = URLEncoder.encode(SubjectName, "GBK");
            __VIEWSTATE = GETVIEWSTATE(result);
            __EVENTVALIDATION = GETEVENTVALIDATION(result);
            arrayList.clear();
            arrayList.add("__EVENTTARGET");arrayList.add("#");
            arrayList.add("__EVENTARGUMENT");arrayList.add("#");
            arrayList.add("__LASTFOCUS");arrayList.add("#");
            arrayList.add("__VIEWSTATE");arrayList.add(__VIEWSTATE);
            arrayList.add("cx");arrayList.add("cx");
            arrayList.add("DropDownList2");arrayList.add("36");
            arrayList.add("TextBox1");arrayList.add(SubjectName);
            arrayList.add("RadioButtonList1");arrayList.add("2");
            arrayList.add("Button3");arrayList.add("%C8%B7++%B6%A8");
            arrayList.add("__EVENTVALIDATION");arrayList.add(__EVENTVALIDATION);

            connection = utils.GetConnection("POST", arrayList, Cookies);
            utils.connect();
            result = Read(connection);
            utils.release();
            //System.out.println(result);
            Document document = Jsoup.parse(result);
            Elements elms = document.getElementsByClass("datelist");

            for(Element e:elms)
            {
                Elements e1 = e.select("tr");
                for(Element e2:e1){
                    CrossSubjectContent.add(e2.text());
                    Elements e3 = e2.select("a");
                    for(Element e4:e3){
                        CrossSubjectContent.add(e4.toString().substring(34, 130)+"&xh="+Username);
                    }
                }

            }
            CrossSubjectContent.remove(0);
//            System.out.println("请选择");
//            for(int i=0;i<CrossSubjectContent.size()/3;i++)
//            {
//                System.out.println(i+CrossSubjectContent.get(i*3));
//
//            }
//
//            int choose = in.nextInt();
//            GetTeacherInfo(this.BaseUrl+ CrossSubjectContent.get(choose*3+1));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }










    public  boolean OrcImage_Login() {
        System.out.println("OrcImage_Login");
        String result = null;
        String Cookies = null;
        Scanner in = null;
        HttpURLConnection httpURLConnection = null;
        imagePreProcess.downloadImage();
        Cookies = imagePreProcess.Cookies;
        try {
            HttpConnectionUtils httpConnectionUtils = new HttpConnectionUtils(BaseUrl+"default2.aspx");
            String code = imagePreProcess.getAllOcr("data/data/krelve.app.kuaihu/0.png");
            ArrayList<String> arrayList = new ArrayList<String>();
            httpURLConnection = httpConnectionUtils.GetConnection("GET", "", "");
            result = Read(httpURLConnection);
            __VIEWSTATE = GETVIEWSTATE(result);
            __EVENTVALIDATION = GETEVENTVALIDATION(result);

            arrayList.add("__VIEWSTATE");arrayList.add(__VIEWSTATE);
            arrayList.add("txtUserName");arrayList.add(Username);
            arrayList.add("TextBox2");arrayList.add(Password);
            arrayList.add("txtSecretCode");arrayList.add(code);
            arrayList.add("RadioButtonList1");arrayList.add("%D1%A7%C9%FA");
            arrayList.add("Button1");arrayList.add("#");
            arrayList.add("lbLanguage");arrayList.add("#");
            arrayList.add("hidPdrs");arrayList.add("#");
            arrayList.add("hidsc");arrayList.add("#");
            arrayList.add("__EVENTVALIDATION");arrayList.add(__EVENTVALIDATION);

            httpURLConnection = httpConnectionUtils.GetConnection("POST", arrayList, Cookies);
            httpConnectionUtils.connect();
            httpConnectionUtils.release();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //in.close();
        }
        this.Cookies = Cookies;
        if(isFirst == 1){
            return true;
        }
        if(isFirst==0&&getName()){
            isFirst = 1;
            return true;
        }
        return false;
    }
}
