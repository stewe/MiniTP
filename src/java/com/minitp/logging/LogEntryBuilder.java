package com.minitp.logging;

import com.ohua.lang.Function;

public abstract class LogEntryBuilder
{
  
  public static class AdminRequestLogEntryBuilder
  {
    @Function
    public Object[] buildAdminLogEntry(boolean tradingAllowed) {
      String logEntry = Boolean.toString(tradingAllowed);
      if(LogFunctionality.DEBUG)
        System.out.println("(build-admin-log-entry): " + logEntry);
      return new Object[] { logEntry };
    }
  }
  
}
