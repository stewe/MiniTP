package com.minitp.messaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.minitp.logging.LogFunctionality;
import com.ohua.lang.Function;
import com.ohua.lang.compile.analysis.qual.ReadOnly;

public abstract class MiniTPRequestParsing {
  
  public static class RequestTypeParser {
    @Function
    public Object[] parseRequestType(@ReadOnly List<String> request) {
      assert request.get(0).startsWith("type");
      String[] s = request.get(0).split("=");
      assert s.length == 2;
      String type = s[1];
      if(LogFunctionality.DEBUG)
        System.out.println("(parse-request-type): " + type);
      return new Object[] { type,
                            new ArrayList<String>(request.subList(1, request.size())) };
    }
  }
  
  public static class RFSRequestParser {
    @Function
    public Object[] parseRfsRequest(List<String> request) {
      String id = null;
      String time = null;
      String product = null;
      String requester = null;
      String provider = null;
      
      for(String requestLine : request) {
        String[] keyValue = requestLine.split("=");
        switch(keyValue[0]) {
          case "id":
            id = keyValue[1];
            break;
          case "time":
            time = keyValue[1];
            break;
          case "product":
            product = keyValue[1];
            break;
          case "requester":
            requester = keyValue[1];
            break;
          case "provider":
            provider = keyValue[1];
            break;
          default:
            System.out.println("(parse-rfs-request): WARNING: Received unknown data : "
                + Arrays.deepToString(request.toArray()));
        }
      }
      if(LogFunctionality.DEBUG)
        System.out.println("(parse-rfs-request): RFS request parsed!");
      return new Object[] { id,
                            time,
                            product,
                            requester,
                            provider};
    }
  }
  
  public static class AdminRequestParser {
    @Function
    public Object[] parseAdminRequest(List<String> request) {
      boolean trading = false;
      for(String line : request) {
        String[] keyValue = line.split("=");
        if(keyValue[0].equalsIgnoreCase("trading")) {
          if(keyValue[1].equalsIgnoreCase("enabled")) {
            trading = true;
            break;
          }
        }
      }
      if(LogFunctionality.DEBUG)
        System.out.println("(parse-admin-request): " + trading);
      return new Object[] { trading };
    }
  }
  
  public static class RegisterRequestParser {
    @Function
    public Object[] parseRegisterRequest(@ReadOnly List<String> request) {
      String provider = "";
      String addr = "";
      String port = "";
      
      for(String line : request) {
        String[] keyValue = line.split("=");
        switch(keyValue[0]) {
          case "provider":
            provider = keyValue[1];
            break;
          case "addr":
            addr = keyValue[1];
            break;
          case "port":
            port = keyValue[1];
            break;
          default:
            System.out.println("(parse-register-request): WARNING: Received unknown data: "
                + Arrays.deepToString(request.toArray()));
            break;
        }
      }
      if(LogFunctionality.DEBUG)
        System.out.println("(parse-register-request): Parsed register request: "
            + provider + " " + addr + " " + port);
      return new Object[] { provider,
                            addr,
                            port };
    }
  }
  
  public static class OrderRequestParser {
    @Function
    public Object[] parseOrderRequest(@ReadOnly List<String> request) {
      String provider = null;
      String requester = null;
      Map<String, String> m = new HashMap<>();
      for(String requestLine : request) {
        String[] keyValue = requestLine.split("=");
        
        switch(keyValue[0]) {
          case "provider":
            provider = keyValue[1];
            m.put(keyValue[0], provider);
            break;
          case "requester":
            requester = keyValue[1];
            m.put(keyValue[0], requester);
            break;
          case "id":
          case "time":
          case "rfsid":
          case "product":
          case "isrequesterbuying":
          case "pricelimit":
            m.put(keyValue[0], keyValue[1]);
            break;
          default:
            System.out.println("(parse-order-request): WARNING: Received unknown request data: "
                + Arrays.deepToString(request.toArray()));
            break;
        }
      }
      if(LogFunctionality.DEBUG)
        System.out.println("(parse-order-request): Order request parsed!");
      return new Object[] { requester,
                            provider,
                            m };
    }
  }
  
  public static class PriceResponseParser {
    @Function
    public Object[] parsePriceInfo(@ReadOnly List<String> msg) {
      String id = null;
      String time = null;
      String rfsID = null;
      String buyPrice = null;
      String sellPrice = null;
      String provider = null;
      String requester = null;
      
      for(String requestLine : msg) {
        String[] keyValue = requestLine.split("=");
        
        switch(keyValue[0]) {
          case "price":
            break;
          case "id":
            id = keyValue[1];
            break;
          case "time":
            time = keyValue[1];
            break;
          case "rfsid":
            rfsID = keyValue[1];
            break;
          case "buyprice":
            buyPrice = keyValue[1];
            break;
          case "sellprice":
            sellPrice = keyValue[1];
            break;
          case "provider":
            provider = keyValue[1];
            break;
          case "requester":
            requester = keyValue[1];
            break;
          case "type":
            break;
          default:
            System.out.println("(parse-price-info): WARNING:Received unknown request data: "
                + Arrays.deepToString(msg.toArray()));
            break;
        }
      }
      if(LogFunctionality.DEBUG)
        System.out.println("(parse-price-info): Price info parsed!");
      return new Object[] { id,
                            time,
                            rfsID,
                            buyPrice,
                            sellPrice,
                            provider,
                            requester};
    }
  }
  
}
