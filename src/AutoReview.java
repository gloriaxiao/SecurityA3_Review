import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class AutoReview {

	private static String path = "";
	private static ArrayList<String> logStrings = new ArrayList<String>();
	
	public static void main(String[] args) {
		if(args.length != 1){
			System.out.println("ERROR: Expected 1 arguments but got " + args.length); 
			System.exit(0); 
		}
		path = args[0]; 
		if (path.charAt(0) == '~') {
			path = System.getProperty("user.home") + path.substring(1); 
		}
		//Check that the input path points to a valid log file
		int idx = path.lastIndexOf("/");
		String directory = path.substring(idx-3, idx);
		if(!directory.equals("log")){
			System.out.println("ERROR: The input is not a valid log file");
			System.exit(0);
		}
		try{
			FileInputStream fstream = new FileInputStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String nextline;
			while((nextline = br.readLine()) != null){
				logStrings.add(nextline);
			}
			br.close();
		} catch(Exception e){
			System.err.println("File Read Error: " + e.getMessage());	
		}
		
	}
	

}
