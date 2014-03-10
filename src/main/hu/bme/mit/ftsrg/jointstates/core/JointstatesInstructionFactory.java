/* Copyright (C) 2007 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration
 * (NASA).  All Rights Reserved.
 *
 * This software is distributed under the NASA Open Source Agreement
 * (NOSA), version 1.3.  The NOSA has been approved by the Open Source
 * Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
 * directory tree for the complete NOSA document.
 *
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
 * KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
 * LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
 * SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
 * THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
 * DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
 */
package hu.bme.mit.ftsrg.jointstates.core;

import gov.nasa.jpf.jvm.bytecode.InstructionFactory;
import gov.nasa.jpf.vm.Instruction;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class JointstatesInstructionFactory extends InstructionFactory {
  public static final Object acceptFlag = new Object();
  public static final Object connectFlag = new Object();

  @Override
  public Instruction invokevirtual(String clsName, String methodName, String methodSignature) {
    Instruction i = super.invokevirtual(clsName, methodName, methodSignature);
    if (clsName.equals("java/net/Socket") && methodName.equals("connect")) {
      i.setAttr(connectFlag);
    } else if (clsName.equals("java/net/ServerSocket") && methodName.equals("accept")) {
      i.setAttr(acceptFlag);
    }
    return i;
  }

}
