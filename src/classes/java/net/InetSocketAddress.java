//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package java.net;

public class InetSocketAddress extends SocketAddress {
  private static final long serialVersionUID = -4081117541123282986L;
  private String hostname;
  private InetAddress inet;
  private int port;

  @Override
  public String toString(){
    return "MODEL CLASS: java.net.InetSocketAddress";
  }
  
  public InetSocketAddress(InetAddress inet, int port) {
    this.inet = inet;
    this.port = port;
  }

  public InetSocketAddress(int port) {
    this.port = port;
  }

  public InetSocketAddress(String hostname, int port) {
    // nothing to do with the host name
    this.port = port;
  }

  public final String getHostName() {
    return hostname;
  }

  public final InetAddress getAddress() {
    return inet;
  }

  public final int getPort() {
    return port;
  }

  public static InetSocketAddress createUnresolved(String hostname, int port) {
    InetSocketAddress sa = new InetSocketAddress(hostname, port);
    return sa;
  }
}
