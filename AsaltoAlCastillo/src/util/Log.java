package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Log {

    private static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    protected boolean logEnabled = true;
    
    /* Imprime en pantalla el mensaje pasado por par�metro, la hora actual y la clase que realiz� la llamada */
    protected void log(String message) {
        if (logEnabled) {
            System.out.println(timeFormat.format(new Date(System.currentTimeMillis())));
        }
    }
}
