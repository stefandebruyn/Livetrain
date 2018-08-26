package livetrain;

import java.util.Locale;

public class Log {
    
    private Log() {}

    public static void add(String... entries) {
        System.out.printf(Locale.getDefault(), "\n\n[%.10fs]\n", Clock.timestamp());
        
        append(entries);
    }
  
    public static void append(String... entries) {
        for (String entry : entries)
            System.out.println(entry);
    }
}
