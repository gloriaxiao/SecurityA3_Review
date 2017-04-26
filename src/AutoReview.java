import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AutoReview {

	private static String path = "";
	private static int BOUND = 10; 
	private static ArrayList<String> logStrings = new ArrayList<String>();
	private static HashMap<AttackInfo, Integer> map = 
			new HashMap<AttackInfo, Integer>(); 
	
	private static HashMap<String, Integer> date_map = new HashMap<String, Integer>(); 
	
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
				if (nextline.contains("authentication failure")) {
					logStrings.add(nextline);
				}
			}
			br.close();
		} catch(Exception e){
			System.err.println("File Read Error: " + e.getMessage());	
		}
		
		int date_base = 0; 
		String system = ""; 
		for (String s: logStrings) {
			String[] pieces = s.split(" "); 
			String time = pieces[0] + " " + pieces[1];
			system = pieces[3]; 
			String user = ""; 
			int userIndex = s.lastIndexOf("user="); 
			if (userIndex == -1) {
				System.out.println("clause doesn't contain user=; skipping");
				continue; 
			} else {
				user = s.substring(userIndex + 5); 
			}
			String attack_type = ""; 
			if (s.contains("sshd")) {
				attack_type = "sshd"; 
			} else if (s.contains("sudo")) {
				attack_type = "sudo"; 
			} else if (s.contains("su")) {
				attack_type = "su"; 
			} else {
				System.out.println("clause doesn't contain attak type; skipping"); 
				continue; 
			}
			if (!(date_map.containsKey(time))) {
				date_map.put(time, date_base); 
				date_base++; 
			}
			AttackInfo newInfo = new AttackInfo(user, time, attack_type); 
			if (map.containsKey(newInfo)) {
				map.put(newInfo, map.get(newInfo) + 1); 
			} else {
				map.put(newInfo, 1); 
			}
		}
		
		ArrayList<AttackInfo> all_attacks = new ArrayList<AttackInfo>(); 
		for (AttackInfo ai: map.keySet()) {
			if (map.get(ai) >= BOUND) {
				all_attacks.add(ai); 
			}
		}
		
		Collections.sort(all_attacks, new Comparator<AttackInfo>() {
			@Override
			public int compare(AttackInfo o1, AttackInfo o2) {
				if (!date_map.containsKey(o1.time)) {
					System.out.println("comparator can't find time " + o1.time); 
					return 0; 
				} else if (!date_map.containsKey(o2.time)) {
					System.out.println("comparator can't find time " + o2.time); 
					return 0; 
				} else {
					return date_map.get(o1.time).compareTo(date_map.get(o2.time)); 
				}
			}		
		});
		
		DateFormat dtf = new SimpleDateFormat("MM dd HH:mm:ss"); 
		Calendar cal = Calendar.getInstance(); 
		String header = dtf.format(cal.getTime());
		header = intToMonth(Integer.parseInt(header.substring(0, 2))) + " " + header.substring(3); 
		
		if (all_attacks.size() == 0) {
			System.out.println(header + " " + system + " auth[4]: OK");
		} else {
			System.out.print(header + " " + system + " auth[4]: INTRUSION. "); 
			for (int i = 0; i < all_attacks.size(); i++) {
				AttackInfo ai = all_attacks.get(i); 
				System.out.print(ai.time +  " user " + all_attacks.get(i).user 
						+ " experienced " + map.get(ai) + " " + ai.attack_type + " attacks");
				if (i != all_attacks.size() - 1) {
					System.out.print("; "); 
				} else {
					System.out.println("."); 
				}
			}
		}
	
	}
	
	public static String intToMonth(int n) {
		if (n == 1) {
			return "Jan"; 
		} else if (n == 2) {
			return "Feb"; 
		} else if (n == 3) {
			return "Mar"; 
		} else if (n == 4) {
			return "Apr"; 
		} else if (n == 5) {
			return "May"; 
		} else if (n == 6) {
			return "Jun"; 
		} else if (n == 7) {
			return "Jul"; 
		} else if (n == 8) {
			return "Aug"; 
		} else if (n == 9) {
			return "Sep"; 
		} else if (n == 10) {
			return "Oct"; 
		} else if (n == 11) {
			return "Nov"; 
		} else if (n == 12) {
			return "Dec"; 
		} else {
			return ""; 
		}
	}
}

class AttackInfo {
	String user; 
	String time; 
	String attack_type; 
	
	public AttackInfo(String u, String t, String a) {
		user = u; 
		time = t; 
		attack_type = a; 
	}
	
	@Override 
	public boolean equals(Object o) {
		if (o == this) return true; 
		if (!(o instanceof AttackInfo)) return false; 
		AttackInfo obj = (AttackInfo) o; 
		return obj.user.equals(user) && obj.time.equals(time) 
				&& obj.attack_type.equals(attack_type); 
	}
	
	@Override
	public int hashCode() {
		String concat = user + ";" + time + ";" + attack_type; 
		return concat.hashCode(); 
	}
	
}