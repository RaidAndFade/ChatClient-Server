package com.yofungate.chat.client;

import java.awt.AWTException;
import java.awt.Dimension;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.swing.JFrame;

public class StartClient{
	public static Client Cframe = new Client();
	static StringBuilder sb;
	public static void main(String[] args) throws AWTException{
		try{
			InetAddress ip = InetAddress.getLocalHost();

		    Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
		    int d = 0;
		    while(d<1) {
		      NetworkInterface network = networks.nextElement();
		      byte[] mac = network.getHardwareAddress();

		      if(mac != null) {
		        d++;
		        StringBuilder sb = new StringBuilder();
		        for (int i = 1; i < mac.length; i++) {
		          sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
		        }
				Cframe.macAddr = sb.toString();
		      }
		    }
		}catch(Exception e){}
		Cframe.clientName = args[0];
        Cframe.setVisible(true);
        Cframe.setSize(300, 270);
        Cframe.setFocusable(true);
        Cframe.setMinimumSize(new Dimension(300, 60));
        Cframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

