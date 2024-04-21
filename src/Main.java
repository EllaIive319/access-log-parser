import java.io.*;
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

        String line;

        try {
            int len_first = reader.readLine().length(), max_length = len_first, min_length = len_first, count = 1;
            while ((line = reader.readLine()) != null) {
                if (line.length() >= 1024)
                    throw new RuntimeException("Line is out of range");
                count += 1;
                if (min_length > line.length())
                    min_length = line.length();
                if (max_length < line.length())
                    max_length = line.length();
            }
            System.out.print(count + " " + max_length + " " + min_length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
