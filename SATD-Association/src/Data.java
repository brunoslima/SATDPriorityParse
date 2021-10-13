import java.util.ArrayList;

public class Data {
    
    private ArrayList<Satd> satdCollection;
    private ArrayList<Issue> issueCollection;
    
    public Data(){
        this.satdCollection = new ArrayList<Satd>();
        this.issueCollection = new ArrayList<Issue>();
    }

    public ArrayList<Satd> getSatdCollection() {
        return satdCollection;
    }

    public void setSatdCollection(ArrayList<Satd> satdCollection) {
        this.satdCollection = satdCollection;
    }

    public void setSatd(Satd satdInstance) {
        this.satdCollection.add(satdInstance);
    }
    
    public String showSatd(){
        
        String resultSatd = "Description | Path | Resource | Line | Type \n";
        
        for(int i = 0; i < this.satdCollection.size(); i++){
            
            resultSatd += this.satdCollection.get(i).show() + "\n";
        }
        
        return resultSatd;
    }
    
    public ArrayList<Issue> getIssueCollection() {
        return issueCollection;
    }

    public void setIssueCollection(ArrayList<Issue> issueCollection) {
        this.issueCollection = issueCollection;
    }

    public void setIssue(Issue issueInstance) {
        this.issueCollection.add(issueInstance);
    }
    
    public String showIssues(){
        
        String resultIssue = "Type | Description | Path | Resource | Line | Rework | Severity \n";
        
        for(int i = 0; i < this.issueCollection.size(); i++){
            
            resultIssue += this.issueCollection.get(i).show() + "\n";
        }
        
        return resultIssue;
    }
    
}
