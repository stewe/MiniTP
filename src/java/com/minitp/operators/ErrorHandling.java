package com.minitp.operators;

import com.ohua.lang.Function;

public class ErrorHandling
{ 
  @Function
  public Object[] reportUnknownRequest(String type){
    System.out.println("ERROR: Unknown request type encountered. Dropping request.");
    return new Object[0];
  }
}
