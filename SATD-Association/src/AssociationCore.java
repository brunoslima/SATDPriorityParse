import java.util.ArrayList;

public class AssociationCore {

	private Satd satdInstance;
    private ArrayList<Issue> issuesInstances;
    private double priorityNivel;
    
	public AssociationCore() {
		this.issuesInstances = new ArrayList<Issue>();
	}

	public AssociationCore(Satd satdInstance, ArrayList<Issue> issuesInstances) {
		
		this.satdInstance = satdInstance;
		this.issuesInstances = issuesInstances;
	}
	
	public Satd getSatdInstance() {
		return satdInstance;
	}
	public void setSatdInstance(Satd satdInstance) {
		this.satdInstance = satdInstance;
	}
	public ArrayList<Issue> getIssuesInstances() {
		return issuesInstances;
	}
	public void setIssuesInstances(ArrayList<Issue> issuesInstances) {
		this.issuesInstances = issuesInstances;
	}
	
	public double getPriorityNivel() {
		return priorityNivel;
	}

	public void setPriorityNivel(double priorityNivel) {
		this.priorityNivel = priorityNivel;
	}

	public String show() {
		
        String resultAssociation = "-> Association \n\t" + this.satdInstance.getDescription() + "\n\t" + this.satdInstance.getPath() + " - " + this.satdInstance.getLine() + "\n";
        
        for(int i = 0; i < this.issuesInstances.size(); i++){
            
            resultAssociation += "\t\t" + i + " = " + this.issuesInstances.get(i).getContext() + " - " + 
                                                      this.issuesInstances.get(i).getType() + " - " + 
            										  this.issuesInstances.get(i).getPath() + " - " + 
            		                                  this.issuesInstances.get(i).getLine() + "\n";
        }
        
        return resultAssociation;
	}
	
    public String showAssoc() {

        String resultAssociation = "-> Association \n\t" + this.satdInstance.getDescription() +
                "\n\t Context = " + this.satdInstance.getContextInstances().get(0).lineInitial + " - " + this.satdInstance.getContextInstances().get(0).lineFinal +
                "\n\t - " + this.satdInstance.getPath() + "-" + this.satdInstance.getLine() + "\n\t";

        for(int i = 0; i < this.issuesInstances.size(); i++){

            resultAssociation += "\t\tISSUE = " + this.getIssuesInstances().get(i).getType() + " - " +
                    this.issuesInstances.get(i).getPath() + " - " +
                    this.issuesInstances.get(i).getLine() + "\n";
        }

        return resultAssociation;
    }
	
}
