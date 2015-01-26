package com.minitp.logging;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import com.ohua.lang.Function;
import com.ohua.lang.compile.analysis.qual.ReadOnly;

public abstract class LogFunctionality
{
  public static final boolean DEBUG = false;
  
  public static class Logger
  {
    @Function
    public Object[] logEntry(String entry, List<String> log, boolean logIt) {
      long timeInMillis = GregorianCalendar.getInstance().getTimeInMillis();
      Timestamp time = new Timestamp(timeInMillis);
      if(logIt)
        log.add(time + " : " + entry);
      if(DEBUG) System.out.println("(log-entry): " + entry);
      return new Object[] {};
    }
  }
  
  public static class PriceInfoLogEntryBuilder
  {
    @Function
    public Object[] buildPriceLogEntry(@ReadOnly String id, @ReadOnly String time, @ReadOnly String rfsID, @ReadOnly String buyPrice, @ReadOnly String sellPrice,
                                       @ReadOnly String provider, @ReadOnly String requester)
    {
      String logEntry = id + " : " + time + " : " + rfsID + " : " + buyPrice + " : " + sellPrice + " : "
          + provider + " : " + requester;
      if(DEBUG) System.out.println("(build-price-log-entry): " + logEntry);
      return new Object[] { logEntry};
    }
  }
  
  public static class RfsLogEntryBuilder
  {
    @Function
    public Object[] buildRfsLogEntry(String id, String time, String product, String requester, String provider) {
      String logEntry = id + " : " + time + " : " + product + " : " + requester + " : " + provider;
      if (DEBUG) System.out.println("(build-rfs-log-entry): " + logEntry);
      return new Object[] { logEntry };
    }
  }
  
  public static class OrderLogEntryBuilder
  {
    @Function
    public Object[] buildOrderLogEntry(Map<String, String> order) {
      StringBuffer buf = new StringBuffer();
      for(Map.Entry<String, String> e : order.entrySet()) {
        buf.append(e.getKey());
        buf.append(" : ");
        buf.append(e.getValue());
        buf.append("\n");
      }
      String logEntry = buf.toString();
      if (DEBUG) System.out.println("(order-log-entry): "+ logEntry);
      return new Object[] { logEntry };
    }
  }
  
  public static class ReportLogEntryBuilder
  {
    @Function
    public Object[] buildReportLogEntry(@ReadOnly List<String> report) {
      String logEntry = Arrays.deepToString(report.toArray());
      if (DEBUG) System.out.println("(build-report-log-entry): " + logEntry);
      return new Object[] { logEntry };
    }
  }
  
}
