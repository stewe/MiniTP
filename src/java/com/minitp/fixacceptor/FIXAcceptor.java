package com.minitp.fixacceptor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import quickfix.Acceptor;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RuntimeError;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;

public class FIXAcceptor {
  
  private FIXAcceptorApp application;
  private MessageStoreFactory storeFactory;
  private SessionSettings settings;
  private LogFactory logFactory;
  private MessageFactory messageFactory;
  private Acceptor acceptor;

  public FIXAcceptor(String settingsFile) {
    try {
      settings = new SessionSettings(new FileInputStream(settingsFile));
      storeFactory = new FileStoreFactory(settings);
      logFactory = new FileLogFactory(settings);
      messageFactory = new DefaultMessageFactory();
      application = new FIXAcceptorApp();
      acceptor = new SocketAcceptor(application, storeFactory, settings, logFactory, messageFactory);
    }
    catch(FileNotFoundException | ConfigError e) {
      e.printStackTrace();
    }
  }

  public void start() throws RuntimeError, ConfigError{
    acceptor.start();
  }
  
  public void stop() {
    acceptor.stop();
  }
}
