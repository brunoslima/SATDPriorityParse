
public class Context {

	public String type;
	public int priority;
	public String lineInitial;
	public String lineFinal;
	
	public Context() {

	}
	
	public Context(String type, int priority, String lineInitial, String lineFinal) {

		this.type = type;
		this.priority = priority;
		this.lineInitial = lineInitial;
		this.lineFinal = lineFinal;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public String getLineInitial() {
		return lineInitial;
	}
	
	public void setLineInitial(String lineInitial) {
		this.lineInitial = lineInitial;
	}
	
	public String getLineFinal() {
		return lineFinal;
	}
	
	public void setLineFinal(String lineFinal) {
		this.lineFinal = lineFinal;
	}
	
	public String show() {
		
		return(this.type + " | " + 
			       this.priority + " | " + 
			       this.lineInitial + " | " + 
			       this.lineFinal);
	}
	
}