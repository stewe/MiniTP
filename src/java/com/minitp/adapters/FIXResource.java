package com.minitp.adapters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RuntimeError;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;

import com.ohua.engine.AbstractExternalActivator;
import com.ohua.engine.AbstractExternalActivator.ManagerProxy;
import com.ohua.engine.flowgraph.elements.operator.OperatorID;
import com.ohua.engine.resource.management.AbstractConnection;
import com.ohua.engine.resource.management.AbstractResource;
import com.ohua.engine.resource.management.ResourceAccess;

public class FIXResource extends AbstractResource {
  
  private FIXEventDispatch application;
  private Initiator initiator;
  boolean initiatorStarted = false;
  private FIXConnection cnn;
  
  public FIXResource(ManagerProxy arg0, Map<String, String> arg1) {
    super(arg0, arg1);
    String fileName = (String) arg1.get("settings");
    SessionSettings settings;
    try {
      settings = new SessionSettings(new FileInputStream(fileName));
      MessageStoreFactory storeFactory = new FileStoreFactory(settings);
      LogFactory logFactory = new FileLogFactory(settings);
      MessageFactory messageFactory = new DefaultMessageFactory();
      application = new FIXEventDispatch();
      initiator  = new SocketInitiator(application, storeFactory, settings, logFactory, messageFactory);
      
    }
    catch(FileNotFoundException | ConfigError e) {
      e.printStackTrace();
    }
    
  }
  
  @Override
  protected AbstractConnection getConnection(Object... args) {
    // create all FIX stuff needed here and then a connection resembles to an application in FIX
    // terminology
    try {
      if (!initiatorStarted) {
        initiator.start();
        initiatorStarted = true;
      }
    }
    catch(RuntimeError | ConfigError e) {
      e.printStackTrace();
    }
    cnn = new FIXConnection(initiator, application);
    return cnn;
  }
  
  /**
   * We do not provide one as we implement blocking IO inside the incoming connections.
   */
  @Override
  protected AbstractExternalActivator getExternalActivator(OperatorID arg0) {
    return null;
  }
  
  @Override
  public void validate() {
    // TODO validation of the file name can be done here.
  }
  
  @Override
  public ResourceAccess getResourceAccess(OperatorID operatorID, String opChckPtArtifactID) {
    return null;
  }
  
}
