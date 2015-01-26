package com.minitp.operators;

import java.util.concurrent.atomic.AtomicReference;

import com.minitp.logging.LogFunctionality;
import com.ohua.lang.Function;


public abstract class TradingAdmin {
  
  public static class SetTradingOperator
  {
    
    @Function
    public Object[] setTrading(boolean newTrading, AtomicReference<Boolean> oldTrading) {
      oldTrading.set(newTrading);
      if(LogFunctionality.DEBUG)
        System.out.println("(set-trading): set trading to " + newTrading);
      return new Object[] { newTrading };
    }
    
  }
  
  public static class TradingAllowedOperator
  {
    
    @Function
    public Object[] tradingAllowed(AtomicReference<Boolean> trading) {
      boolean trad = trading.get();
      if (trad) {
        if(LogFunctionality.DEBUG)
          System.out.println("(trading-allowed): Trading is allowed.");
      } else {
        if(LogFunctionality.DEBUG)
          System.out.println("(trading-allowed): Trading is not allowed.");
      }
      return new Object[] { trad };
    }
    
  }
  
}