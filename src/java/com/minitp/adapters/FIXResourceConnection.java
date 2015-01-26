package com.minitp.adapters;

import quickfix.Message;
import quickfix.Session;
import quickfix.SessionNotFound;

import com.ohua.engine.resource.management.ConnectionID;
import com.ohua.engine.resource.management.ResourceConnection;

public class FIXResourceConnection extends ResourceConnection {

  private FIXEventDispatch _dispatch = null;
  
  public FIXResourceConnection(ConnectionID arg0, FIXEventDispatch application) {
    super(arg0);
    _dispatch = application;
  }
  
  public Object[] receive(){
    return _dispatch.receive();
  }
  
  public boolean send(Message msg){
    try {
      return Session.sendToTarget(msg);
    }
    catch(SessionNotFound e) {
      e.printStackTrace();
      return false;
    }
  }  

}
