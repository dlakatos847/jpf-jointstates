package hu.bme.mit.ftsrg.jointstates;

import gov.nasa.jpf.jvm.bytecode.InstructionFactory;
import gov.nasa.jpf.vm.Instruction;

public class JointstatesInstructionFactory extends InstructionFactory {
  public static Object flag = new Object();

  @Override
  public Instruction invokevirtual(String clsName, String methodName, String methodSignature) {
    Instruction i = super.invokevirtual(clsName, methodName, methodSignature);
    if (clsName.equals("java/net/ServerSocket") && methodName.equals("accept")) {
      i.setAttr(flag);
    }
    return i;
  }
}
