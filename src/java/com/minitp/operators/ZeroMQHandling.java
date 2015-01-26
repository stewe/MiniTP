package com.minitp.operators;

import com.minitp.logging.LogFunctionality;
import com.ohua.distributed.connection.resource.ZeroMQPullConnection.ZeroMQPull;
import com.ohua.distributed.connection.resource.ZeroMQPushConnection.ZeroMQPush;
import com.ohua.lang.Function;

public abstract class ZeroMQHandling {
  
  public static class ZeroMQAccepter {
    @Function
    public Object[] acceptZeromq(ZeroMQPull cnn) {
      String rcvd = cnn.pull();
      if(LogFunctionality.DEBUG)
        System.out.println("(accept-zeromq): " + rcvd);
      return new Object[] {rcvd};
    }
  }
  
  public static class ZeroMQSender {
    @Function
    public Object[] sendZeromq(String msg, ZeroMQPush cnn) {
      cnn.push(msg);
      if(LogFunctionality.DEBUG)
        System.out.println("(send-zeromq): sent " + msg);
      return new Object[] {};
    }
  }
  
}
