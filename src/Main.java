import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

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

        String[] parseLine = new String[7];
        String line;
        char[] temp;
        double countLines = 0;
        double countYandex = 0;
        double countGoogle = 0;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.length() >= 1024)
                    throw new RuntimeException("Line is out of range");
                for (int i = 0; i<7; i++){
                    parseLine[i] = "";
                }
                temp = line.toCharArray();
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
                while (temp[i] != '"') { // Метод Get
                    parseLine[2] += temp[i];
                    i++;
                }
                i += 2;
                while (temp[i] != ' ') { // HTTP-ответ
                    parseLine[3] += temp[i];
                    i++;
                }
                i++;
                while (temp[i] != ' ') { // Размер в байтах
                    parseLine[4] += temp[i];
                    i++;
                }
                i += 2;
                while (temp[i] != '"') { // Путь к странице
                    parseLine[5] += temp[i];
                    i++;
                }
                i += 3;

                while (temp[i] != '"') { // User-Agent
                    parseLine[6] += temp[i];
                    i++;
                }
                i = 0;
                String firstBrackets = "";
                String User_Agent = parseLine[6];
                temp = User_Agent.toCharArray();
                int flag = 0;
                for (char c : temp) {
                    if (c == ')')
                        break;
                    if (flag == 1)
                        firstBrackets += c;
                    if (c == '(')
                        flag = 1;
                }
                String fragment = null;
                firstBrackets = firstBrackets.replaceAll(" ","");
                String[] parts = firstBrackets.split(";");
                if (parts.length >= 2)
                    fragment = parts[1];
                else
                    fragment = "-";
                String fragment1 = "";
                for (char c : fragment.toCharArray()){
                    if (c == '/')
                        break;
                    fragment1 += c;
                }
                fragment = fragment1;
                if (fragment.equals("YandexBot"))
                    countYandex++;

                if (fragment.equals("Googlebot")) {
                    countGoogle++;

                }
                countLines++;

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Доля Яндекс ботов" + " " + (countYandex/countLines));
        System.out.println("Доля Гугл ботов" + " " + (countGoogle/countLines));
    }
}