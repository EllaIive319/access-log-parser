import java.util.Scanner;
import java.io.File;

public class Main
{
    public static void main(String[] args) {
        int counter = 0;
        while(true) {
            String path = new Scanner(System.in).nextLine();
            File file = new File(path);
            boolean fileexists = file.exists();
            boolean isDirectory = file.isDirectory();

            if ( (!fileexists) || (isDirectory) ) {
                System.out.println("Файл несуществует или указанный путь - путь к папке.");
                continue;
            }
            else  {
                System.out.println("Путь указан верно");
                counter = counter + 1;
                System.out.println("Это файл номер " + counter);
            }
        }
    }
}


