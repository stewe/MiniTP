package com.minitp.operators;

import com.minitp.logging.LogFunctionality;
import com.ohua.lang.Function;

public abstract class Piping {
  
  public static class PipeOneOperator {
    
    @Function
    public Object[] pipe(Object o) {
      
      if(LogFunctionality.DEBUG)
        System.out.println("(pipe): " + o.getClass().getCanonicalName());
      
      return new Object[] {o};
    }
  }
  
}