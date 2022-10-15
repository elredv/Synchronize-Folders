package SynchronizeFolders.Service;

import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;

public class ServiceLogging {
	private ServiceLogging(){}

	public static void log(String txt) {
		String curTime = new GregorianCalendar().toZonedDateTime().format(DateTimeFormatter.ofPattern("hh:mm:ss"));
		System.out.println("[LOG] " + curTime + " | " + txt);
//        String curMonthYear = new GregorianCalendar().toZonedDateTime().format(DateTimeFormatter.ofPattern("MM_yyyy"));
//        String curDate = new GregorianCalendar().toZonedDateTime().format(DateTimeFormatter.ofPattern("dd"));
	}
}
