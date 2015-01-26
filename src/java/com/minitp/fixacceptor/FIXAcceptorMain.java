package com.minitp.fixacceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import quickfix.ConfigError;
import quickfix.RuntimeError;

public class FIXAcceptorMain {
  
  public static void main(String[] args) throws RuntimeError, ConfigError {
    FIXAcceptor acceptor = new FIXAcceptor("./resources/settings-acceptor");
    acceptor.start();
    
    System.out.println("Enter 'q' to quit:");
    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
    try {
      while(!"q".equalsIgnoreCase(bufferRead.readLine())) {}
    }
    catch(IOException e) {
      e.printStackTrace();
    }
    
    acceptor.stop();
  }
  
}
