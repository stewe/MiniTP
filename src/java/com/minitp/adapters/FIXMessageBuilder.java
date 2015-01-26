package com.minitp.adapters;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

import quickfix.ConfigError;
import quickfix.Group;
import quickfix.Message.Header;
import quickfix.SessionSettings;
import quickfix.StringField;
import quickfix.field.BeginString;
import quickfix.field.BidPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LeavesQty;
import quickfix.field.NoRelatedSym;
import quickfix.field.OfferPx;
import quickfix.field.OrdRejReason;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.Price;
import quickfix.field.QuoteID;
import quickfix.field.QuoteReqID;
import quickfix.field.SenderCompID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.TransactTime;
import quickfix.fix50.ExecutionReport;
import quickfix.fix50.NewOrderSingle;
import quickfix.fix50.Quote;
import quickfix.fix50.QuoteRequest;

import com.minitp.logging.LogFunctionality;
import com.ohua.lang.Function;

public abstract class FIXMessageBuilder {
  
  public static class FIXRfsBuilder {
    @Function
    public Object[] buildFixRfs(String id, String time, String product, String requester, String provider, String target) {
      
      QuoteRequest quoteRequest = new QuoteRequest(new QuoteReqID(id));
      Group group = new Group(NoRelatedSym.FIELD, Symbol.FIELD);
      group.setField(new Symbol(product));
      quoteRequest.addGroup(group);
      Header header = quoteRequest.getHeader();
      header.setField(new BeginString("FIXT.1.1"));
      header.setField(new TargetCompID(target));
      SessionSettings ss;
      try {
        ss = new SessionSettings("./resources/settings-prov");
        String senderCompId = ss.getDefaultProperties().getProperty("SenderCompID");
        header.setField(new SenderCompID(senderCompId));
      }
      catch(ConfigError e) {
        e.printStackTrace();
        return null;
      }
      quoteRequest.setField(new StringField(5001, requester));
      quoteRequest.setField(new StringField(5002, provider));
      if(LogFunctionality.DEBUG)
        System.out.println("(build-fix-rfs): " + quoteRequest);
      return new Object[] { quoteRequest };
    }
  }
  
  public static class FIXOrderBuilder {
    @Function
    public Object[] buildFixOrder(Map<String,String> msgMap, String target) {
      ClOrdID orderId = new ClOrdID(msgMap.get("id"));
      Side side = new Side(Side.BUY); //TODO
      TransactTime time = new TransactTime(new Date());
      OrdType type = new OrdType('D');
      String requester = msgMap.get("requester");
      String provider = msgMap.get("provider");
      
      NewOrderSingle nos = new NewOrderSingle(orderId, side, time, type);
      nos.setField(new Symbol(msgMap.get("product")));
      nos.setField(new QuoteID(msgMap.get("rfsid")));
      nos.setField(new Price(new Double(msgMap.get("pricelimit"))));
      Header header = nos.getHeader();
      header.setField(new BeginString("FIXT.1.1"));
      header.setField(new TargetCompID(target));
      SessionSettings ss;
      try {
        ss = new SessionSettings("./resources/settings-prov");
        String senderCompId = ss.getDefaultProperties().getProperty("SenderCompID");
        header.setField(new SenderCompID(senderCompId));
      }
      catch(ConfigError e) {
        e.printStackTrace();
        return null;
      }
      nos.setField(new StringField(5001, requester));
      nos.setField(new StringField(5002, provider));
      if(LogFunctionality.DEBUG)
        System.out.println("(build-fix-order): " + nos);
      return new Object[] { nos };
    }
  }
  
  public static class FIXPriceBuilder {
    @Function
    public Object[] buildFixPrice(String id, String time, String rfsID, String buyPrice, String sellPrice, String provider, String requester, String target) {
      QuoteID quoteID = new QuoteID(id);
      Quote quote = new Quote(quoteID);
      quote.setField(new QuoteReqID(rfsID));
      quote.setField(new BidPx(new Double(buyPrice)));
      quote.setField(new OfferPx(new Double(sellPrice)));
      
      Header header = quote.getHeader();
      header.setField(new BeginString("FIXT.1.1"));
      header.setField(new TargetCompID(target));
      SessionSettings ss;
      try {
        ss = new SessionSettings("./resources/settings-req");
        String senderCompId = ss.getDefaultProperties().getProperty("SenderCompID");
        header.setField(new SenderCompID(senderCompId));
      }
      catch(ConfigError e) {
        e.printStackTrace();
        return null;
      }
      quote.setField(new StringField(5001, requester));
      quote.setField(new StringField(5002, provider));
      
      if(LogFunctionality.DEBUG)
        System.out.println("(build-fix-price): " + quote);
      return new Object[] { quote };
    }
  }
  
  public static class FIXReportBuilder {
    @Function
    public Object[] buildFixReport(List<String> report, String target) {
      OrderID orderID = null;
      ExecID execID = null;
      ExecType execType = null;
      OrdStatus ordStatus = null;
      Side side = new Side(Side.BUY);
      LeavesQty leavesQty = new LeavesQty(0);
      CumQty cumQty = new CumQty(0);
      LastPx lastPx = null;
      Symbol symbol = null;
      OrdRejReason ordRejReason = null;
      boolean executed = false;
      String requester = null;
      String provider = null;
      
      for(String string : report) {
        String[] keyValue = string.split("=");
        switch (keyValue[0]) {
          case "id":
            execID = new ExecID(keyValue[1]);
            break;
          case "orderid":
            orderID = new OrderID(keyValue[1]);
            break;
          case "product":
            symbol = new Symbol(keyValue[1]);
            break;
          case "isexecuted":
            if (keyValue[1].equalsIgnoreCase("true")) executed = true;
            break;
          case "executedprice":
            lastPx = new LastPx(new Double(keyValue[1]));
            break;
          case "rejectionreason":
            int reason = OrdRejReason.OTHER;
            try {
              Field declaredField = OrdRejReason.class.getDeclaredField(keyValue[1]);
              reason = (Integer) declaredField.get(null);
            }
            catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
              e.printStackTrace();
            }
            ordRejReason = new OrdRejReason(reason);
            break;
          case "requester":
            requester = keyValue[1];
            break;
          case "provider":
            provider = keyValue[1];
            break;
          default:
            break;
        }
      }
      if (execID == null || orderID == null || side == null || leavesQty == null || cumQty == null) {
        return null;
      } else {
        if (executed) {
          execType = new ExecType('2');
          ordStatus = new OrdStatus('2'); 
        } else {
          execType = new ExecType('8');
          ordStatus = new OrdStatus('8');
        }
        ExecutionReport execRep = new ExecutionReport(orderID, execID, execType, ordStatus, side, leavesQty, cumQty);
        execRep.setField(symbol);
        if (lastPx != null) execRep.setField(lastPx); 
        if (ordRejReason != null) execRep.setField(ordRejReason); 
        
        Header header = execRep.getHeader();
        header.setField(new BeginString("FIXT.1.1"));
        header.setField(new TargetCompID(target));
        SessionSettings ss;
        try {
          ss = new SessionSettings("./resources/settings-req");
          String senderCompId = ss.getDefaultProperties().getProperty("SenderCompID");
          header.setField(new SenderCompID(senderCompId));
        }
        catch(ConfigError e) {
          e.printStackTrace();
          return null;
        }
        execRep.setField(new StringField(5001, requester));
        execRep.setField(new StringField(5002, provider));
        if(LogFunctionality.DEBUG)
          System.out.println("(build-fix-report): " + execRep);
        return new Object[] { execRep };
      }
      
    }
  }
  
  
}
