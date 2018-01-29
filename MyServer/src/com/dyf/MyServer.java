package com.dyf;


import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;  
import java.net.Socket;  
  
public class MyServer {  
      
    private static final int PORT = 5100; //spy on the port 5000  
    
    FileWriter dataFile;
    
    void initMyserver() {  
        try {  
            ServerSocket serverSocket = new ServerSocket(PORT);  
            dataFile = new FileWriter("sensorData.txt");
            
            InetAddress ia = null;
            try{
            	ia = ia.getLocalHost();
            	String localname = ia.getHostName();
            	String localip = ia.getHostAddress();
            	System.out.println("本机名称是："+localname);
            	System.out.println("本机的ip是："+localip);
            } catch (Exception e) {
				// TODO: handle exception
            	System.out.println(e);
            	return;
			}
            System.out.println("Server start......");  
            while(true) {  

                  
                //If there is not connect,it will block.  
                //Listen to the connect from client.  
                //If there is a connect ,it will return a socket.  
                Socket socket = serverSocket.accept();  
              
                //build a thread to deal with the socket  
                //Thread thread = new Thread(new ServerThread(socket));  
                Thread thread = new Thread(new ServerThread(socket,dataFile));
                thread.start();  
            }  
        } catch (IOException e) {  
            // TODO: handle exception  
        	System.out.println(e);
        }  
        
        try {
			dataFile.flush();
			dataFile.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
    }  
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
        MyServer myServer = new MyServer();  
        myServer.initMyserver();  
    }  
  
}  
