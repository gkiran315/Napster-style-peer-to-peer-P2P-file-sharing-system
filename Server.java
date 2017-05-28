 
import java.io.*;
import java.net.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Scanner;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Integer;

@SuppressWarnings("unused")

public class Server
{
    public static ArrayList<FileInfo> globalArray = new ArrayList<FileInfo>();
 
    @SuppressWarnings("resource")
	//public static void main(String args[])
    public Server() throws NumberFormatException, IOException
    {
    	
		
    	ServerSocket serverSocket=null;
    	Socket socket = null;
    	try{
    			serverSocket = new ServerSocket(7799);
    			System.out.println("Server started!! ");
    			System.out.println(" ");
    			System.out.println("Waiting for the Client to be connected ..");
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    	while(true)
    	{
    		try{
    				socket = serverSocket.accept();
    				//serverSocket.close();
    		}
    		catch(IOException e)
    		{
    			System.out.println("I/O error: " +e);
    		}
    		new ServerTestClass(socket,globalArray).start();
    	}
    }
}

class ServerTestClass extends Thread
{
	protected Socket socket;
	ArrayList<FileInfo> globalArray;
	public ServerTestClass(Socket clientSocket,ArrayList<FileInfo> globalArray)
	{
		this.socket=clientSocket;
		this.globalArray=globalArray;
	}

	ArrayList<FileInfo> filesList=new ArrayList<FileInfo>();
   	ObjectOutputStream oos;
	ObjectInputStream ois;
	String str;
	int index;

    @SuppressWarnings("unchecked")
	public void run()
    {
    	try
    	{  
    		InputStream is=socket.getInputStream();
    		oos = new ObjectOutputStream(socket.getOutputStream());
    		ois = new ObjectInputStream(is);     
    		filesList=(ArrayList<FileInfo>)ois.readObject();
    		System.out.println("All the available files from the given directory have been recieved to the Server!");      
    		for(int i=0;i<filesList.size() ;i++)
    		{
    			globalArray.add(filesList.get(i));
    		}
    		System.out.println("Total number of files available in the Server that are received from all the connected clients: " +globalArray.size());
    	}
    	
    	catch(IndexOutOfBoundsException e){
    		System.out.println("Index out of bounds exception");
    	}
    	catch(IOException e){
    		System.out.println("I/O exception");
    	}
    	catch(ClassNotFoundException e){
    		System.out.println("Class not found exception");
    	}
    	
    	try {
    			str = (String) ois.readObject();
    	}
    	catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServerTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    	
        ArrayList<FileInfo> sendingPeers = new ArrayList<FileInfo>();
        System.out.println("Searching for the file name...!!!"); 
           
        for(int j=0;j<globalArray.size();j++)
        {
           FileInfo fileInfo=globalArray.get(j);
           Boolean tf=fileInfo.fileName.equals(str);
           if(tf)
           {
        	   index = j;
        	   sendingPeers.add(fileInfo);
           }
        }
        
        try {
        	oos.writeObject(sendingPeers);
        } 
        catch (IOException ex) {
         Logger.getLogger(ServerTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
    

 

