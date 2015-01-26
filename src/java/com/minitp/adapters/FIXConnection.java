package com.minitp.adapters;

import quickfix.Initiator;

import com.ohua.engine.resource.management.AbstractConnection;

public class FIXConnection extends AbstractConnection {

  private Initiator _initiator = null;
  private FIXEventDispatch _application = null;
  
  protected FIXConnection(Initiator initiator, FIXEventDispatch application) {
   _initiator = initiator;
   _application = application;
  }
  
  @Override
  protected void close() throws Throwable {
    _initiator.stop();
  }
  
  @Override
  protected FIXResourceConnection getRestrictedConnection() {
    return new FIXResourceConnection(super.getConnectionID(), _application);
  }
  
  @Override
  protected void commit() throws Throwable {
    throw new UnsupportedOperationException("Currently transactions are not supported for FIX connections.");
  }
  
  @Override
  protected void rollback() throws Throwable {
    throw new UnsupportedOperationException("Currently transactions are not supported for FIX connections.");
  }
  
}
