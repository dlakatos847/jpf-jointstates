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

public class InetAddress {

	static final InetAddress LOCALHOST = new InetAddress(new byte[] { 127, 0, 0, 1 });
	private byte[] address;
	
	@Override
	public String toString(){
	  return "Model class: java.net.InetAddress";
	}

	private InetAddress(byte[] address) {
		this.address = address;
	}

	public static InetAddress[] getAllByName(String host) throws UnknownHostException {
		return new InetAddress[] { LOCALHOST };
	}

	public static InetAddress getByName(String host) throws UnknownHostException {
		return LOCALHOST;
	}

	public static InetAddress getByAddress(byte[] addr) throws UnknownHostException {
		return new InetAddress(addr);
	}

	public String getCanonicalHostName() {
		if (this == LOCALHOST) {
			return "localhost";
		} else {
			return "[InetAddress : getCanonicalHostName()] stub method";
		}
	}

	public String getHostName() {
		return "[InetAddress : getHostName()] stub method";
	}

	public String getHostAddress() {
		return address[0] + "." + address[1] + "." + address[2] + "." + address[3];
	}

	public static InetAddress getLocalHost() throws UnknownHostException {
		return LOCALHOST;
	}

	public byte[] getAddress() {
		return address;
	}
}
