import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")

public class Server {
    static ArrayList<FileInfo> globalArray = new ArrayList<>();

    @SuppressWarnings("resource")
    //public static void main(String args[])
    public Server() throws NumberFormatException {


        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(7799);
            System.out.println("Server started!! ");
            System.out.println(" ");
            System.out.println("Waiting for the Client to be connected ..");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                assert serverSocket != null;
                socket = serverSocket.accept();
                //serverSocket.close();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            new ServerTestClass(socket).start();
        }
    }

    public void deleteFromArray(Socket socket) {
        for (int i = 0; i < globalArray.size(); i++) {
            if (globalArray.get(i).portNumber == socket.getPort()) {
                System.out.println("Deleted File " + globalArray.get(i).fileName);
                globalArray.remove(i);
            }
        }
    }
}

class ServerTestClass extends Thread {
    private Socket socket;
    private ArrayList<FileInfo> filesList = new ArrayList<>();
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private String str;
    ServerTestClass(Socket clientSocket) {
        this.socket = clientSocket;
    }
    //int index;

    @SuppressWarnings("unchecked")
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(is);
            filesList = (ArrayList<FileInfo>) ois.readObject();
            System.out.println("All the available files from the given directory have been recieved to the Server!");
            Server.globalArray.addAll(filesList);
            System.out.println("Total number of files available in the Server that are received from all the connected clients: " + Server.globalArray.size());
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Index out of bounds exception");
        } catch (IOException e) {
            System.out.println("I/O exception");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found exception");
        }
        while (true) {
            try {
                str = (String) ois.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(ServerTestClass.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (str.equalsIgnoreCase("Exit")) {
                for (int i = 0; i < Server.globalArray.size(); i++) {
                    if (filesList.contains(Server.globalArray.get(i))) {
                        System.out.println("Deleted Element with Name: " + Server.globalArray.get(i).portNumber);
                        Server.globalArray.remove(i);
                    }
                }
                System.out.println("Disconnected!!");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            ArrayList<FileInfo> sendingPeers = new ArrayList<>();
            System.out.println("Searching for the file name...!!!");

            for (int j = 0; j < Server.globalArray.size(); j++) {
                FileInfo fileInfo = Server.globalArray.get(j);
                Boolean tf = fileInfo.fileName.equals(str);
                if (tf) {
                    //index = j;
                    sendingPeers.add(fileInfo);
                }
            }

            boolean check = sendingPeers.isEmpty();

            try {
                oos.writeObject(check);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!check) {
                try {
                    oos.writeObject(sendingPeers);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
    

 

