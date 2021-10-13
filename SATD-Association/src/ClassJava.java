import java.util.ArrayList;

public class ClassJava {

	private ArrayList<String> lines;
	
	public ClassJava() {
		this.lines = new ArrayList<>();
	}

	public ArrayList<String> getLines() {
		return lines;
	}

	public void setLine(ArrayList<String> lines) {
		this.lines = lines;
	}
	
	public void show() {
		
		for(int i = 0; i < this.lines.size(); i++) {
		
			System.out.println((i+1) + "  " + this.lines.get(i));
		}
		
	}
	
}
