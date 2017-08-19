package com.dyf;


import java.io.FileWriter;
import java.io.IOException;  
import java.net.ServerSocket;  
import java.net.Socket;  
  
public class MyServer {  
      
    private static final int PORT = 5000; //spy on the port 5000  
    
    FileWriter accelrometerFile;//加速度传感器
    FileWriter gyroscopeFile;//陀螺仪
    FileWriter gravityFile;//重力传感器
    FileWriter magnetFile;//磁场感应传感器
    
    void initMyserver() {  
        try {  
            ServerSocket serverSocket = new ServerSocket(PORT);  
              
            accelrometerFile = new FileWriter("accelrometer.txt");
            gyroscopeFile = new FileWriter("gyroscope.txt");
            gravityFile = new FileWriter("gravity.txt");
            magnetFile = new FileWriter("magnet.txt");
            System.out.println("Server start......");  
            while(true) {  

                  
                //If there is not connect,it will block.  
                //Listen to the connect from client.  
                //If there is a connect ,it will return a socket.  
                Socket socket = serverSocket.accept();  
              
                //build a thread to deal with the socket  
                //Thread thread = new Thread(new ServerThread(socket));  
                Thread thread = new Thread(new ServerThread(socket,accelrometerFile,
                		gyroscopeFile, gravityFile,magnetFile));
                thread.start();  
            }  
        } catch (IOException e) {  
            // TODO: handle exception  
        }  
        
        try {
        	accelrometerFile.flush();
			accelrometerFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try{
        	gravityFile.flush();
        	gravityFile.close();
        } catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        try{
        	magnetFile.flush();
        	magnetFile.close();
        } catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        try{
        	gyroscopeFile.flush();
        	gyroscopeFile.close();
        } catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }  
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
        MyServer myServer = new MyServer();  
        myServer.initMyserver();  
    }  
  
}  
