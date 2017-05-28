import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MyMain {
	public static void main(String args[]) throws Exception{

		System.out.println("Please enter your Choice ::");
		System.out.println("1. To run the Server");
		System.out.println("2. To run the Client");
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));		
		int choice = Integer.parseInt(br.readLine());
		
		if(choice == 1){		
			Server s = new Server();
		}
		else if(choice == 2){
			Client c = new Client();
		}
		else{
			System.out.println("Your choice is incorrect");
		}
	}
}
