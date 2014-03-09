package hu.bme.mit.ftsrg.jointstates;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.DefaultAttributor;
import gov.nasa.jpf.vm.MethodInfo;

public class JointstatesAttributor extends DefaultAttributor {

  public static final Object serverSocketAcceptFlag = new Object();

  public JointstatesAttributor(Config conf) {
    super(conf);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void setMethodInfoAttributes(MethodInfo mi) {
    super.setMethodInfoAttributes(mi);
    if (mi.getClassInfo().getName().equals("java.net.ServerSocket") && mi.getUniqueName().equals("accept()Ljava/net/Socket;")) {
      mi.setAttr(serverSocketAcceptFlag);
    }
  }
}
