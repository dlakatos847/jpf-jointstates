package hu.bme.mit.ftsrg.jointstates.examples;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class DummyClient {
	public static void main(String[] args) {
		Socket echoSocket = null;
		PrintWriter out = null;
		try {
			InetAddress addr = InetAddress.getByName("127.0.0.1");
			int portNumber = 8080;
			echoSocket = new Socket(addr, portNumber);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			// BufferedReader in = new BufferedReader(new InputStreamReader(
			// echoSocket.getInputStream()));

			out.write(97);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				echoSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.close();
		}
	}
}
