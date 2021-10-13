
public class Issue {

	private String type;
	private String description;
	private String path;
	private String resource;
	private String line;
	private String rework;
	private String severity;
	private int context;
	
	public Issue(String type, String description, String path, String resource, String line, String rework,
			String severity) {
		
		this.type = type;
		this.description = description;
		this.path = path;
		this.resource = resource;
		this.line = line;
		this.rework = rework;
		this.severity = severity;
	}
	
	public Issue(String type, String description, String path, String resource, String line, String rework,
			String severity, int context) {
		
		this.type = type;
		this.description = description;
		this.path = path;
		this.resource = resource;
		this.line = line;
		this.rework = rework;
		this.severity = severity;
		this.context = context;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getRework() {
		return rework;
	}

	public void setRework(String rework) {
		this.rework = rework;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}
	
	public int getContext() {
		return context;
	}

	public void setContext(int context) {
		this.context = context;
	}

	public String show() {
		
		return(this.type + " | " + 
		       this.description + " | " + 
		       this.path + " | " + 
		       this.resource + " | " + 
		       this.line + " | " + 
		       this.rework + " | " + 
		       this.severity + " | " +
		       this.context);
	}
	
}
