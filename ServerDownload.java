 
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
   
public class ServerDownload extends Thread
{
    int peerServerPort;
    String  directoryPath=null;
    ServerSocket dwldServerSocket;
    Socket dwldSocket=null;
    /*
    public ServerDownload()
    {
       
    }
    */
    ServerDownload(int peerServerPort,String directoryPath) {        
    	this.peerServerPort=peerServerPort;
    	this.directoryPath=directoryPath;    
    }
    public void run(){
    	try {
    			dwldServerSocket = new ServerSocket(peerServerPort);
    			dwldSocket = dwldServerSocket.accept();
    			new ServerDownloadThread(dwldSocket,directoryPath).start();
        } 
    	catch (IOException ex) {
    			Logger.getLogger(ServerDownload.class.getName()).log(Level.SEVERE, null, ex);
    	}
    }    
} 
class ServerDownloadThread extends Thread
{
    Socket dwldThreadSocket;
    String directoryPath;
    public ServerDownloadThread(Socket dwldThreadSocket,String directoryPath)
    {
        this.dwldThreadSocket=dwldThreadSocket;       
        this.directoryPath=directoryPath;
    }
    @SuppressWarnings({ "unused", "resource" })
	public void run()
    {
        try
        {
            ObjectOutputStream objOS = new ObjectOutputStream(dwldThreadSocket.getOutputStream());
            ObjectInputStream objIS = new ObjectInputStream(dwldThreadSocket.getInputStream());
            
            String fileName = (String)objIS.readObject();
            String fileLocation;// Stores the directory name
            while(true)
            {
                File myFile = new File(directoryPath+"//"+fileName);
                long length = myFile.length();
                
                byte [] byte_arr = new byte[(int)length];
                
                objOS.writeObject((int)myFile.length());
                objOS.flush();
                
                FileInputStream FIS=new FileInputStream(myFile);
                BufferedInputStream objBIS = new BufferedInputStream(FIS);
                objBIS.read(byte_arr,0,(int)myFile.length());
                
                //System.out.println("Sending the file of " +byte_arr.length+ " bytes");
                
                objOS.write(byte_arr,0,byte_arr.length);
                
                objOS.flush();                
            }
        }
        catch(Exception e)
        {
            
        }
    }
}