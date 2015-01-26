package com.minitp.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.minitp.logging.LogFunctionality;
import com.ohua.lang.Function;
import com.ohua.lang.compile.analysis.qual.ReadOnly;

public abstract class MiniTPMessageBuilder {
  
  
  public static class OrderMessageBuilder
  {
    @Function
    public Object[] buildOrderMsg(@ReadOnly Map<String, String> order, String requester, String provider) {
      List<String> msg = new ArrayList<>();
      msg.add("type=order");
      for(Map.Entry<String, String> entry : order.entrySet()) {
        msg.add(entry.getKey() + "=" + entry.getValue());
      }
      msg.add("requester=" + requester);
      msg.add("provider=" + provider);
      if(LogFunctionality.DEBUG)
        System.out.println("(build-order-msg): " + msg);
      return new Object[] { msg };
    }
  }
  
  public static class PriceInfoBuilder
  {
    
    @Function
    public Object[] buildPriceMsg(@ReadOnly String id,
                                  @ReadOnly String time,
                                  @ReadOnly String rfsID,
                                  @ReadOnly String buyPrice,
                                  @ReadOnly String sellPrice,
                                  @ReadOnly String provider,
                                  @ReadOnly String requester) {
      List<String> msg = new ArrayList<String>();
      msg.add("type=price");
      msg.add("id=" + id);
      msg.add("time=" + time);
      msg.add("rfsid=" + rfsID);
      msg.add("buyprice=" + buyPrice);
      msg.add("sellprice=" + sellPrice);
      msg.add("provider=" + provider);
      msg.add("requester=" + requester);
      if(LogFunctionality.DEBUG)
        System.out.println("(build-price-message): " + msg);
      return new Object[] { msg };
    }
    
  }
  
  public static class RFSMessageBuilder
  {
    @Function
    public Object[] buildRfsMsg(String id, String time, String product, String requester, String provider) {
      List<String> msg = new ArrayList<>();
      msg.add("type=rfs");
      msg.add("id=" + id);
      msg.add("time=" + time);
      msg.add("product=" + product);
      msg.add("requester=" + requester);
      msg.add("provider=" + provider);
      if(LogFunctionality.DEBUG)
        System.out.println("(build-rfs-message): " + msg);
      return new Object[] { msg };
    }
  } 
}
