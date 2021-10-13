import java.util.ArrayList;

public class Satd {

	private String description;
	private String resource;
	private String path;
	private String line;
	private String type;
    private ArrayList<Context> contextInstances;
    private String priority;
	
	public Satd(String description, String resource, String path, String line, String type) {
		
		this.description = description;
		this.resource = resource;
		this.path = path + "/" + resource;
		this.line = line;
		this.type = type;
		this.contextInstances = new ArrayList<Context>();
	}
	
	public Satd(String description, String resource, String path, String line) {
		
		this.description = description;
		this.resource = resource;
		this.path = path + "/" + resource;
		this.line = line;
		this.contextInstances = new ArrayList<Context>();
	}
	
	public Satd(String description, String path, String line, String priority, boolean p) {
		
		this.description = description;
		this.path = path;
		this.line = line;
		this.priority = priority;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public ArrayList<Context> getContextInstances() {
		return contextInstances;
	}

	public void setContextInstances(ArrayList<Context> contextInstances) {
		this.contextInstances = contextInstances;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String show() {
		
		return(this.description + " | " + 
		       this.path + " | " + 
		       this.resource + " | " + 
		       this.line + " | " + 
		       this.type);
	}

}
