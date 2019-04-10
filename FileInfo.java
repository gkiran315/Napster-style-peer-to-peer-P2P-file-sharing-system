import javafx.util.Pair;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

@SuppressWarnings("serial")

public class FileInfo implements Serializable
{
	int peerid;
	String fileName;
	int portNumber;
    ArrayList<Pair<String, Integer>> arrList = new ArrayList<>();
}