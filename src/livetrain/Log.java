package livetrain;

import java.util.Locale;

/**
 * Telemetry pipeline for debugging purposes
 */
public class Log {
    
    private Log() {}

    /**
     * Add a log entry
     * 
     * @param entries Lines
     */
    public static void add(String... entries) {
        System.out.printf(Locale.getDefault(), "\n[%.10fs]\n", Clock.timestamp());
        
        append(entries);
    }
  
    /**
     * Add on to the last entry
     * 
     * @param entries Lines
     */
    public static void append(String... entries) {
        for (String entry : entries)
            System.out.println(entry);
    }
}
