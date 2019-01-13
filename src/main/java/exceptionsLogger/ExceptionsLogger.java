package exceptionsLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExceptionsLogger {
	private String exceptionsLoggerFilePath;
	
	public ExceptionsLogger(String exceptionsLoggerFilePath) {
		this.exceptionsLoggerFilePath = exceptionsLoggerFilePath;
	}
	
	public void log(Exception exception) {
		File logFile = new File(exceptionsLoggerFilePath);
		PrintWriter pw = null;
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		
		try {
			pw = new PrintWriter(new FileOutputStream(logFile, true));
			
			pw.print("\r\n\r\n\r\n-----------------------" + dtf.format(now) + "-----------------\r\n\r\n");

			for(StackTraceElement ste : exception.getStackTrace()) {
				pw.println(ste);
			}
			Throwable cause = exception.getCause();
			  if (cause != null) {
				pw.println("Caused by: ");
			    cause.printStackTrace(pw);
			  }
			pw.print("\r\n\r\n-----------------------------------------------------------\r\n\r\n\r\n");
			
		} catch (Exception e) {}
		finally {
			if(pw != null) {
				pw.flush();
				pw.close();
			}
		}
	}
}
