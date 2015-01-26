package com.minitp.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.SendingTime;
import quickfix.field.Symbol;
import quickfix.fix50.ExecutionReport;
import quickfix.fix50.NewOrderSingle;
import quickfix.fix50.Quote;
import quickfix.fix50.QuoteRequest;

import com.minitp.logging.LogFunctionality;
import com.ohua.lang.Function;


public abstract class Adapters {
  
  public static class FIXAccepter {
    @Function
    public Object[] acceptFix(FIXResourceConnection cnn) {
      Object[] rcvd = cnn.receive();
      if(LogFunctionality.DEBUG)
        System.out.println("(accept-fix): received " + Arrays.deepToString(rcvd));
      return rcvd;
    }
  }
  
  public static class FIXSender {
    @Function
    public Object[] sendFix(Message msg, FIXResourceConnection cnn) {
      boolean result = cnn.send(msg);
      if(LogFunctionality.DEBUG)
        System.out.println("(send-fix): sent " + msg);
      return new Object[] { result };
    }
  }
  
  public static class FIXParser extends MessageCracker {
    
    private Object[] _result = null;
    
    @Function
    public Object[] parseFix(Message msg, SessionID id){
      if(LogFunctionality.DEBUG)
        System.out.println("(parse-fix): parsing msg of type " + msg.getClass());
      
      _result = null;
      if(msg.isAdmin()){
        return null;
      }else if(msg.isApp()){
        try {
          super.crack(msg, id);
        }
        catch(UnsupportedMessageType | FieldNotFound | IncorrectTagValue e) {
          // TODO throw proper exception here!
          e.printStackTrace();
          throw new RuntimeException(e);
        }
        Object[] result = _result;
        _result = null;
        
        if(LogFunctionality.DEBUG)
          System.out.println("(parse-fix): returning " + result[0]);
        return result;
      }else{
        assert false;
        throw new RuntimeException("impossible");
      }
    }
    
    @Handler
    public void quoteRequestHandler(QuoteRequest msg, SessionID id){
      List<String> msgList = new ArrayList<String>();
      msgList.add("type=rfs");
      
      try {
        msgList.add("id=" + msg.getQuoteReqID().getValue());
        msgList.add("time=" + msg.getHeader().getField(new SendingTime()).getValue());
        msgList.add("product=" + msg.getGroup(1, 146).getField(new Symbol()).getValue());
        msgList.add("requester=" + msg.getString(5001));
        if (msg.isSetField(5002)) msgList.add("provider=" + msg.getString(5002));
      }
      catch(FieldNotFound e) {
        e.printStackTrace();
        _result = null;
        return;
      }
      _result = new Object[] { msgList };
    }
    
    @Handler
    public void quoteHandler(Quote msg, SessionID id) {
      List<String> msgList = new ArrayList<String>();
      msgList.add("type=price");
    
      try {
        msgList.add("id=" + msg.getQuoteID().getValue());
        msgList.add("time=" + msg.getHeader().getField(new SendingTime()).getValue());
        msgList.add("rfsid=" + msg.getQuoteReqID().getValue());
        msgList.add("sellprice=" + msg.getOfferPx().getValue());
        msgList.add("buyprice=" + msg.getBidPx().getValue());
        msgList.add("requester=" + msg.getString(5001));
        msgList.add("provider=" + msg.getString(5002));
      }
      catch(FieldNotFound e) {
        e.printStackTrace();
        _result = null;
        return;
      }
      _result = new Object[] { msgList };
    }
    
    @Handler
    public void newSingleOrderHandler(NewOrderSingle msg, SessionID id) {
      List<String> msgList = new ArrayList<String>();
      msgList.add("type=order");
      try {
        msgList.add("id=" + msg.getClOrdID().getValue());
        msgList.add("time=" + msg.getHeader().getField(new SendingTime()).getValue());
        msgList.add("rfsid=" + msg.getString(117));
        msgList.add("product=" + msg.getSymbol().getValue());
        msgList.add("pricelimit=" + msg.getPrice().getValue());
        msgList.add("requester=" + msg.getString(5001));
        msgList.add("provider=" + msg.getString(5002));
      }
      catch(FieldNotFound e) {
        e.printStackTrace();
        _result = null;
        return;
      }
      _result = new Object[] { msgList };
    }
    
    @Handler
    public void executionReportHandler(ExecutionReport msg, SessionID id) {
      List<String> msgList = new ArrayList<String>();
      msgList.add("type=report");
      try {
        msgList.add("id=" + msg.getExecID().getValue());
        msgList.add("time=" + msg.getHeader().getField(new SendingTime()).getValue());
        msgList.add("orderid=" + msg.getOrderID().getValue());
        msgList.add("product=" + msg.getSymbol().getValue());
        boolean isExecuted = false;
        if (msg.getExecType().getValue() == '2') isExecuted = true;
        msgList.add("isexecuted=" + String.valueOf(isExecuted));
        if (isExecuted) {
          msgList.add("executedprice=" + String.valueOf(msg.getLastPx().getValue()));
        } else {
          msgList.add("rejectionreason=" + String.valueOf(msg.getOrdRejReason()));
        }
        msgList.add("requester=" + msg.getString(5001));
        msgList.add("provider=" + msg.getString(5002));
      }
      catch(FieldNotFound e) {
        e.printStackTrace();
        _result = null;
        return;
      }
      _result = new Object[] { msgList };
    }
      
  }
  
  public static class FIXSerializer {
    @Function
    public Object[] serializeFix(Message msg){
      return new Object[] {msg.toString()};
    }
  }
}
