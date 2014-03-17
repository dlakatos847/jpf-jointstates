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
package hu.bme.mit.ftsrg.jointstates.command;

import java.util.List;

/**
 * @author David Lakatos <david.lakatos.hu@gmail.com>
 * 
 */
public class ProvidedData {
  private int depth;
  private List<Integer> ports;
  private ProvidedDataType type;

  /**
   * @param depth
   * @param ports
   */
  public ProvidedData(int depth, List<Integer> ports, ProvidedDataType type) {
    super();
    this.depth = depth;
    this.ports = ports;
    this.type = type;
  }

  public int getDepth() {
    return this.depth;
  }

  public List<Integer> getPorts() {
    return this.ports;
  }

  public ProvidedDataType getType() {
    return this.type;
  }

}
