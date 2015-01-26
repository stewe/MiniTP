package com.minitp.adapters;

import java.util.concurrent.ArrayBlockingQueue;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;

public class FIXEventDispatch implements Application {
    
  private ArrayBlockingQueue<Object[]> _messageQueue = new ArrayBlockingQueue<>(100);
  
  protected Object[] receive() {
    try {
      return _messageQueue.take();
    }
    catch(InterruptedException e) {
      assert false;
    }
    return null;
  }
  
  @Override
  public void fromAdmin(Message arg0, SessionID arg1) throws FieldNotFound, IncorrectDataFormat,
                                                     IncorrectTagValue, RejectLogon
  {
    try {
      _messageQueue.put(new Object[]{arg0, arg1});
    }
    catch(InterruptedException e) {
      assert false;
    }
  }
  
  @Override
  public void fromApp(Message arg0, SessionID arg1) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
                                                   UnsupportedMessageType
  {
    try {
      _messageQueue.put(new Object[]{arg0, arg1});
    }
    catch(InterruptedException e) {
      assert false;
    }
  }
  
  @Override
  public void onCreate(SessionID arg0) {
    // session management callback
  }
  
  @Override
  public void onLogon(SessionID arg0) {
    // session management callback
    
  }
  
  @Override
  public void onLogout(SessionID arg0) {
    // session management callback
  }
  
  @Override
  public void toAdmin(Message arg0, SessionID arg1) {
    // for logging purposes
  }
  
  @Override
  public void toApp(Message arg0, SessionID arg1) throws DoNotSend {
    // for logging purposes
  }
  
}
