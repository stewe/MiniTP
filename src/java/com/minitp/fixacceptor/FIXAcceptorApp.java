package com.minitp.fixacceptor;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.StringField;
import quickfix.UnsupportedMessageType;

public class FIXAcceptorApp implements Application{

  @Override
  public void onCreate(SessionID sessionId) {
  }

  @Override
  public void onLogon(SessionID sessionId) {
  }

  @Override
  public void onLogout(SessionID sessionId) {
  }

  @Override
  public void toAdmin(Message message, SessionID sessionId) {
  }

  @Override
  public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat,
                                                             IncorrectTagValue, RejectLogon
  {
  }

  @Override
  public void toApp(Message message, SessionID sessionId) throws DoNotSend {
  }

  @Override
  public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat,
                                                           IncorrectTagValue, UnsupportedMessageType
  {
    
    System.out.println("Received: " + message);
    String targetCompID = sessionId.getTargetCompID();
    String target = null;
    // TODO this only works for one requester client, one requester gw, one provider client and one provider gw
    // to support multiple nodes, e.g. use maps or the field "DeliverToCompID" in FIX
    switch (targetCompID) {
      case "MINITP-REQ":
        target = message.getField(new StringField(5001)).getValue();
        break;
      case "MINITP-PROV":
        target = message.getField(new StringField(5002)).getValue();
        break;
      case "REQUESTER-CLIENT":
        target = "MINITP-REQ";
        message.setField(new StringField(5001, "REQUESTER-CLIENT"));
        break;
      case "PROVIDER-CLIENT":
        target = "MINITP-PROV";
        message.setField(new StringField(5002, "PROVIDER-CLIENT"));
        break;
    }
    SessionID targetSessionID = new SessionID(sessionId.getBeginString(), sessionId.getSenderCompID(), target);
    try {
      Session.sendToTarget(message, targetSessionID);
      System.out.println("[FIXAcceptorApp]: sent " + message + "; " + targetSessionID);
    }
    catch(SessionNotFound e) {
      e.printStackTrace();
    }
  }
  
}
