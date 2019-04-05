import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    @SuppressWarnings({"unchecked", "rawtypes", "resource", "unused"})
    //public static void main(String args[]) throws Exception
    public Client() {
        Socket socket;
        ArrayList al;
        ArrayList<FileInfo> arrList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        ObjectInputStream ois;
        ObjectOutputStream oos;
        String string;
        //Object o,b;
        String directoryPath;
        int peerServerPort;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Welcome to the Client ::");
            System.out.println(" ");
            System.out.println("Enter the directory that contain the files -->");
            directoryPath = br.readLine();

            System.out.println("Enter the port number on which the peer should act as server ::");
            peerServerPort = Integer.parseInt(br.readLine());

            ServerDownload objServerDownload = new ServerDownload(peerServerPort, directoryPath);
            objServerDownload.start();
			
			/*Socket clientThread = new Socket("localhost",7799);
			
			ObjectOutputStream objOutStream = new ObjectOutputStream(clientThread.getOutputStream());
			ObjectInputStream objInStream = new ObjectInputStream(clientThread.getInputStream());*/

            socket = new Socket("localhost", 7799);
            System.out.println("Connection has been established with the client");

            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("Enter the peerid for this directory ::");
            int readpid = Integer.parseInt(br.readLine());

            File folder = new File(directoryPath);
            File[] listofFiles = folder.listFiles();
            FileInfo currentFile;
            File file;

            assert listofFiles != null;
            for (File listofFile : listofFiles) {
                currentFile = new FileInfo();
                file = listofFile;
                currentFile.fileName = file.getName();
                currentFile.peerid = readpid;
                currentFile.portNumber = peerServerPort;
                arrList.add(currentFile);
            }

            oos.writeObject(arrList);
            //System.out.println("The complete ArrayList :::"+arrList);

            while (true) {
                System.out.println("Enter the desired file name that you want to downloaded from the list of the files available in the Server or Exit ::");
                String fileNameToDownload = br.readLine();

                if (fileNameToDownload.equalsIgnoreCase("Exit")) {
                    oos.writeObject(fileNameToDownload);
                    System.out.println("Disconnecting...");
                    socket.close();
                    return;
                }

                oos.writeObject(fileNameToDownload);

                System.out.println("Waiting for the reply from Server...!!");

                boolean check;
                check = (boolean) ois.readObject();

                if (check) {
                    System.out.println("File Not Found");
                    continue;
                }

                ArrayList<FileInfo> peers;
                peers = (ArrayList<FileInfo>) ois.readObject();

                for (FileInfo peer : peers) {
                    int result = peer.peerid;
                    int port = peer.portNumber;
                    System.out.println("The file is stored at peer id " + result + " on port " + port);
                }


                System.out.println("Enter the respective port number of the above peer id :");
                int clientAsServerPortNumber = Integer.parseInt(br.readLine());

                System.out.println("Enter the desired peer id from which you want to download the file from :");
                int clientAsServerPeerid = Integer.parseInt(br.readLine());

                clientAsServer(clientAsServerPortNumber, fileNameToDownload, directoryPath);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void clientAsServer(int clientAsServerPortNumber, String fileNamedwld, String directoryPath) throws ClassNotFoundException {
        try {
            @SuppressWarnings("resource")
            Socket clientAsServersocket = new Socket("localhost", clientAsServerPortNumber);

            ObjectOutputStream clientAsServerOOS = new ObjectOutputStream(clientAsServersocket.getOutputStream());
            ObjectInputStream clientAsServerOIS = new ObjectInputStream(clientAsServersocket.getInputStream());

            clientAsServerOOS.writeObject(fileNamedwld);
            int readBytes = (int) clientAsServerOIS.readObject();

            //System.out.println("Number of bytes that have been transferred are ::"+readBytes);

            byte[] b = new byte[readBytes];
            clientAsServerOIS.readFully(b);
            OutputStream fileOPstream = new FileOutputStream(directoryPath + "//" + fileNamedwld);

            @SuppressWarnings("resource")

            BufferedOutputStream BOS = new BufferedOutputStream(fileOPstream);
            BOS.write(b, 0, readBytes);

            System.out.println("Requested file - " + fileNamedwld + ", has been downloaded to your desired directory " + directoryPath);
            System.out.println(" ");
            System.out.println("Display file " + fileNamedwld);

            BOS.flush();
            fileOPstream.close();

            Desktop.getDesktop().open(new File(directoryPath));

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

