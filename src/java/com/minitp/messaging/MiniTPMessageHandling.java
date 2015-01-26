package com.minitp.messaging;

import java.util.ArrayList;
import java.util.List;

import com.minitp.logging.LogFunctionality;
import com.ohua.lang.Function;
import com.ohua.lang.compile.analysis.qual.ReadOnly;

public class MiniTPMessageHandling {
  
  public static class MsgDeserializer {
    @Function
    public Object[] deserializeMinitp(String msg){
      
      final char nul = Character.toChars(00)[0];
      String[] fields = msg.split(String.valueOf(nul));
      List<String> deserializedMsg = new ArrayList<String>();
      
      String devLog = "(get-msg-type): ";
      for(String field : fields) {
        if(LogFunctionality.DEBUG)
          devLog += "field: " + field + "\n";
        deserializedMsg.add(field);
      }
      if(LogFunctionality.DEBUG)
        System.out.println(devLog);
      return new Object[] { deserializedMsg };
    }
  }
  
  public static class MsgSerializer {
    @Function
    public Object[] serializeMinitp(@ReadOnly List<String> msgList) {
      final char nul = Character.toChars(00)[0];
      String serializedMsg = "";
      for(String elem : msgList) {
        serializedMsg += elem + nul;
      }
      if(LogFunctionality.DEBUG)
        System.out.println("(serialize-minitp): " + serializedMsg);
      return new Object[] { serializedMsg };
    }
  }
  
  public static class TypeAdder {
    @Function
    public Object[] addTypeToList(@ReadOnly List<String> list, String type) {
      List<String> typedList = new ArrayList<String>();
      typedList.add("type=" + type);
      for(String value : list) {
        typedList.add(value);
      }
      if(LogFunctionality.DEBUG)
        System.out.println("(add-type-to-list): " + typedList);
      return new Object[] { typedList };
    }
  }
  
}
