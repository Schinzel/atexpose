package com.atexpose;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class SocketRWUtil {
	private static final int BUF_SIZE = 100;

	/**
	 * Reads a message from a socket.
	 *
	 * @param socket The socket to read from.
	 *
	 * @return The message read.
	 */
	public static String read(Socket socket) throws IOException {
		InputStream is = socket.getInputStream();
		StringBuffer sb = new StringBuffer();
		char chrFirstChar = (char) is.read();
		sb.append(chrFirstChar);
		byte[] arr = new byte[BUF_SIZE];
		int bytesRead = 0;
		//While there is more than one byte available and the read does not return end of stream
		while ((is.available() > 0) && ((bytesRead = is.read(arr)) != -1)) {
			//Append byte retreived from stream to string buffer
			sb.append(new String(arr, 0, bytesRead, Charset.forName("UTF-8")));
		}
		return sb.toString();
	}

	/**
	 * Write the argument message to the socket. 
	 * 
	 * @param socket The socket to write to.
	 * @param sMessage The message to write.
	 *
	 */
	public static void write(Socket socket, String sMessage) throws IOException {
		write(socket, sMessage.getBytes(Charset.forName("UTF-8")));
	}

	public static void write(Socket socket, String sMessage, String sEncoding) throws IOException {
		write(socket, sMessage.getBytes(sEncoding));
	}

	public static void write(Socket socket, byte[] abMessage) throws IOException {
		OutputStream out = socket.getOutputStream();
		out.write(abMessage);
		out.flush();
	}
}