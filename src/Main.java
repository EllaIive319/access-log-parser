import java.io.*;
import java.util.*;
import java.util.stream.DoubleStream;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.sum;
import static java.lang.Math.abs;

//Курсовой проект. Задание #2 по теме "Stream API"

class LocalDateTime{
    List<Integer> time = new ArrayList<Integer>();
    List<String> dateTime = new ArrayList<String>();
    public LocalDateTime(String date){
        char[] temp = date.toCharArray();
        int i = 0;
        String timeArr="";
        while (temp[i]!=':') {
            timeArr += temp[i];
            i++;
        }
        dateTime = List.of(timeArr.split("/"));
        i++;
        timeArr="";
        while (temp[i]!=' ') {
            timeArr += temp[i];
            i++;
        }
        String[] tempTime = timeArr.split(":");
        for (i = 0; i<tempTime.length; i++) {
            this.time.add(parseInt(tempTime[i]));
        }

    }
    public String toString(){
        return this.time + " " + dateTime;
    }
}

class UserAgent {
    String operationSystem = "";
    String browser = "";
    String secondPart="";

    public UserAgent(String line){
        String firstBrackets = "";
        char[] temp = line.toCharArray();
        int flag = 0;
        for (char c : temp) {
            if (c == ' ')
                break;
            this.browser += c;
        }
        for (char c : temp) {
            if (c == ')')
                break;

            if (flag == 1)
                firstBrackets += c;
            if (c == '(')
                flag = 1;
        }

        firstBrackets = firstBrackets.replaceAll(" ","");
        String[] parts = firstBrackets.split(";");
        if (parts.length >= 2) {
            this.operationSystem = parts[0];
            this.secondPart = parts[1];
        } else {
            this.operationSystem = "-";
            this.secondPart = "-";
        }
    }
    public boolean isBot(){
        return secondPart.contains("bot") || secondPart.contains("Bot");
    }
    public String toString(){
        return this.browser+ " " + this.operationSystem;
    }
}

enum HttpMethod {
    GET ("GET"),
    PUT ("PUT"),
    POST ("POST");
    final String method;

    HttpMethod(String method) {
        this.method = method;
    }
}

class LogEntry {
    final String ipAddr;
    final LocalDateTime time;
    final HttpMethod method;
    final String path;
    final int responseCode;
    final int responseSize;
    final String referer;
    final UserAgent agent;

    public LogEntry(String line){
        String[] parseLine = new String[8];

        for (int i = 0; i<8; i++){
            parseLine[i] = "";
        }
        char[] temp = line.toCharArray();
        int i = 0;

        while (temp[i] != ' ') { // IP
            parseLine[0] += temp[i];
            i++;
        }
        while (temp[i] != '[') { // идем до даты-времени
            i++;
        }
        i++;
        while (temp[i] != ']') { // Дата-время
            parseLine[1] += temp[i];
            i++;
        }
        i += 3;
        while (temp[i] != ' ') { // Метод Get
            parseLine[2] += temp[i];
            i++;
        }
        i++;
        while (temp[i] != ' ') { // Метод Get
            parseLine[3] += temp[i];
            i++;
        }
        while (temp[i] != '"') { // Метод Get
            i++;
        }
        i += 2;
        while (temp[i] != ' ') { // HTTP-ответ
            parseLine[4] += temp[i];
            i++;
        }
        i++;
        while (temp[i] != ' ') { // Размер в байтах
            parseLine[5] += temp[i];
            i++;
        }
        i += 2;
        while (temp[i] != '"') { // Путь к странице
            parseLine[6] += temp[i];
            i++;
        }
        i += 3;

        while (temp[i] != '"') { // User-Agent
            parseLine[7] += temp[i];
            i++;
        }
        this.ipAddr = parseLine[0];
        this.time = new LocalDateTime(parseLine[1]);
        this.method = HttpMethod.valueOf(parseLine[2]);
        this.referer = parseLine[6];
        this.responseCode = parseInt(parseLine[4]);
        this.responseSize = parseInt(parseLine[5]);
        this.path = parseLine[3];
        this.agent = new UserAgent(parseLine[7]);
    }

    public String getIpAddr() {
        return this.ipAddr;
    }
    public LocalDateTime getTime() {
        return this.time;
    }
    public HttpMethod getMethod() {
        return this.method;
    }
    public String getReferer() {
        return this.referer;
    }
    public int getResponseCode() {
        return this.responseCode;
    }
    public int getResponseSize() {
        return this.responseSize;
    }
    public UserAgent getAgent() {
        return this.agent;
    }
    public String getPath() {
        return this.path;
    }


    public String toString(){
        return this.ipAddr+" "+this.time+" "+this.method+" "+this.referer+" "+this.responseCode+" "+this.responseSize+" "+this.path+" "+this.agent;
    }
}

class Statistics {
    double countEnter = 0;
    double countFailRequest = 0;
    ArrayList<Double> counters = new ArrayList<Double>();
    long totalTraffic;
    LocalDateTime minTime;
    LocalDateTime maxTime;
    HashSet<String> addr = new HashSet<String>();
    HashMap<String, Integer> setOfOperationSys = new HashMap<String, Integer>();
    HashMap<String, Integer> ipAddr = new HashMap<String, Integer>();
    HashMap<Integer,Integer> second = new HashMap<>();
    HashSet<String> domens = new HashSet<>();
    int timeSeconds;
    int countSeconds=0;

    public Statistics(){
        this.totalTraffic = 0;

        this.maxTime = new LocalDateTime("25/Sep/2022:06:25:04 +0300");
        this.minTime = new LocalDateTime("25/Sep/2022:06:25:04 +0300");
    }

    public void addEntry(LogEntry LE){
        if ((this.timeSeconds != LE.getTime().time.get(2))||(this.countSeconds == 0)){
            this.timeSeconds = LE.getTime().time.get(2);
            this.countSeconds++;
        }

        this.totalTraffic += LE.getResponseSize();
        if ((this.minTime.time.get(0) > LE.getTime().time.get(0)) && (parseInt(this.minTime.dateTime.get(0)) >= parseInt(LE.getTime().dateTime.get(0))))
            this.minTime = LE.getTime();
        if ((this.maxTime.time.get(0) < LE.getTime().time.get(0)) || (parseInt(this.maxTime.dateTime.get(0)) < parseInt(LE.getTime().dateTime.get(0))))
            this.maxTime = LE.getTime();

        if (!LE.getAgent().isBot()) { //Проверяем не бот ли, после этого считаем посещения
            this.countEnter++;
            if (this.ipAddr.get(LE.getIpAddr()) > 0)
                this.ipAddr.put(LE.getIpAddr(), this.ipAddr.get(LE.getIpAddr()) + 1);
            this.ipAddr.putIfAbsent(LE.getIpAddr(),1);

        }
        if (LE.getResponseCode() == 200) // список всех существующих страниц
            this.addr.add(LE.getPath());

        if (Integer.toString(LE.getResponseCode()).matches("[4-5].*$"))
            this.countFailRequest++;


        this.setOfOperationSys.putIfAbsent(LE.getAgent().operationSystem, 0);
        if (this.setOfOperationSys.get(LE.getAgent().operationSystem) >= 0)
            this.setOfOperationSys.put(LE.getAgent().operationSystem, this.setOfOperationSys.get(LE.getAgent().operationSystem) + 1);

        if (!Objects.equals(LE.getReferer(), "-")) {
            char[] temp = LE.getReferer().toCharArray();
            int i = 0;
            String domenStr = null;
            try {
                while (temp[i] != '/') {
                    i++;
                }
                i += 2;
                domenStr = "";

                while (temp[i] != '/') {
                    domenStr += temp[i];
                    i++;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                domenStr = null;
            }
            if (domenStr != null)
                this.domens.add(domenStr);
        }

    }

    public HashSet<String> getExistsPages(){ //возвращаем список существующих страниц
        return this.addr;
    }

    public  HashMap<String, Double> getOSRatio(){ //возвращаем список доли ОС
        double sum = 0;
        HashMap<String, Double> ratioSet = new HashMap<String, Double>();
        for (double val : this.setOfOperationSys.values()) {
            sum += val;
        }
        for (String s: this.setOfOperationSys.keySet()){
            ratioSet.putIfAbsent(s, (double)this.setOfOperationSys.get(s)/sum);
        }
        return ratioSet;
    }

    public double countAverage() { //Среднее число пользователей в час
        double hours = abs(24*(parseInt(this.maxTime.dateTime.get(0)) - parseInt(this.minTime.dateTime.get(0))) - (this.maxTime.time.get(0) - this.minTime.time.get(0)));
        return this.countEnter/hours;
    }

    public double countAverageFails(){ //Среднее число ошибок в час
        double hours = abs(24*(parseInt(this.maxTime.dateTime.get(0)) - parseInt(this.minTime.dateTime.get(0))) - (this.maxTime.time.get(0) - this.minTime.time.get(0)));
        return this.countFailRequest/hours;
    }

    public double countAverageUnique(){ //Среднее число запросов от одного пользователя
        return this.countEnter/this.ipAddr.size();
    }
    public int MaximumUnique() {
        int maximum=0;
        for (String key : this.ipAddr.keySet()){

            if (this.ipAddr.get(key)>maximum)
                maximum=this.ipAddr.get(key);
        }
        return maximum;
    }
    public String peakActivity(){
        int[] time = new int[2];
        int maximum = 0;
        int sec = 0;
        for (Integer i: this.second.keySet()) {
            if (this.second.get(i) > maximum) {
                maximum = this.second.get(i);
                sec = i;
            }
        }
        time[0] = sec;
        time[1] = maximum;
        return Arrays.toString(time);
    }
    public HashSet<String> getDomensSet(){
        return this.domens;
    }
    public double getTrafficRate() {
        double hours = abs(24*(parseInt(this.maxTime.dateTime.get(0)) - parseInt(this.minTime.dateTime.get(0))) - (this.maxTime.time.get(0) - this.minTime.time.get(0)));
        return this.totalTraffic/hours;
    }
}
public class Main
{
    public static void main(String[] args) {
        int counter = 0;
        String path = null;
        while (true) {
            String path_temp = new Scanner(System.in).nextLine();
            File file = new File(path_temp);
            boolean fileexists = file.exists();
            boolean isDirectory = file.isDirectory();

            if ((!fileexists) || (isDirectory)) {
                System.out.println("Файл несуществует или указанный путь - путь к папкке.");
                continue;
            } else {
                System.out.println("Путь указан верно");
                path = path_temp;
                break;
            }
        }

        BufferedReader reader;
        try {
            FileReader fileReader = new FileReader(path);
            reader = new BufferedReader(fileReader);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        String line;
        List<LogEntry> LE = new ArrayList<LogEntry>();
        Statistics stat = new Statistics();
        int i = 0;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.length() >= 1024)
                    throw new RuntimeException("Line is out of range");
                LogEntry LElement = new LogEntry(line);
                LE.add(i, LElement);
                stat.addEntry(LElement);

                i++;

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(stat.MaximumUnique());

    }
}