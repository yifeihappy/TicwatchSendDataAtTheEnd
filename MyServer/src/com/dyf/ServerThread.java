package com.dyf;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.PrintStream;  
import java.net.Socket;  
import java.text.SimpleDateFormat;  
import java.util.Date;
import java.util.StringTokenizer;  
  
public class ServerThread implements Runnable {  
      
    Socket socket;  
    //PrintStream output;  
    BufferedReader in;  
    FileWriter accelrometerFile;//加速度传感器
    FileWriter gyroscopeFile;//陀螺仪
    FileWriter gravityFile;//重力传感器
    FileWriter magnetFile;//磁场感应传感器
    public static final int TYPE_ACCELEROMETER = 1;
    public static final int TYPE_MAGNET = 2;
    public static final int TYPE_GYROSCOPE = 4;
    public static final int TYPE_GRAVITY = 9;
  
    public ServerThread(Socket socket,FileWriter accelrometerFile,
    		FileWriter gyroscopeFile, FileWriter gravityFile, FileWriter magnetFile) throws IOException {  
        this.socket = socket;  
        this.accelrometerFile = accelrometerFile;
        this.gyroscopeFile = gyroscopeFile;
        this.gravityFile = gravityFile;
        this.magnetFile = magnetFile;
    }  
       
    public void run() {          
        String buffer;  
        try {  
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));   
            buffer = ""; //if init buffer = null, will get a string which like "null..."  
            String line = null;  
              
            System.out.println("get Sensor data");
            while ((line = in.readLine()) != null) {  
                buffer = buffer + line;   
                System.out.println(line);
                StringTokenizer tokens = new StringTokenizer(line, ",");
                if(tokens.countTokens() == 5) {//根据传输数据的格式，通过“，”分割后，应该是有五个
                	int sensorType = Integer.parseInt(tokens.nextToken());
                	switch (sensorType) {
    				case TYPE_ACCELEROMETER:
    		            accelrometerFile.write(line+"\n");
    		            accelrometerFile.flush();
    					break;
    				case TYPE_GRAVITY:
    					gravityFile.write(line+"\n");
    					gravityFile.flush();
    					break;
    				case TYPE_GYROSCOPE:
    					gyroscopeFile.write(line+"\n");
    					gyroscopeFile.flush();
    					break;
    				case TYPE_MAGNET:
    					magnetFile.write(line+"\n");
    					magnetFile.flush();
    					break;
    				default:
    					break;
    				}
                }
            }  



              
        } catch (IOException e) {  
            // TODO: handle exception  
            e.printStackTrace();  
        }  
        
    }  
    
    public void anyData(String buffer) {
    	
    	
    }
      
}  

