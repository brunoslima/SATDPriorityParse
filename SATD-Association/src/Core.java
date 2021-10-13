import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Core {

    private ArrayList<AssociationCore> associationCollection;
    
	public Core() {
		this.associationCollection = new ArrayList<AssociationCore>();
	}

	public Core(ArrayList<AssociationCore> associationCollection) {
		
		this.associationCollection = associationCollection;
	}

	public ArrayList<AssociationCore> getAssociationCollection() {
		
		return associationCollection;
	}

	public void setAssociationCollection(ArrayList<AssociationCore> associationCollection) {
		
		this.associationCollection = associationCollection;
	}

	public void makeAssociationLimiar(Data data, int limiar){
		
		boolean association;
		
		for (int i = 0; i < data.getSatdCollection().size(); i++) {
			
			AssociationCore a = new AssociationCore();
			association = false;
			for (int j = 0; j < data.getIssueCollection().size(); j++) {
				
				if(data.getSatdCollection().get(i).getPath().equals(data.getIssueCollection().get(j).getPath())) {
					
					String stringLineSATD, stringLineISSUE;
					stringLineSATD = data.getSatdCollection().get(i).getLine();
					stringLineISSUE = data.getIssueCollection().get(j).getLine();

					if(stringLineISSUE.equals("N") == true) {
						
						if(!association) {
							a.setSatdInstance(data.getSatdCollection().get(i));
							association = true;
						}
						a.getIssuesInstances().add(data.getIssueCollection().get(j));
					}
					else {
						
				        String elements[];
						elements = stringLineSATD.split("-");
						if(elements.length == 1){
							stringLineSATD = elements[0];
						}
						else stringLineSATD = elements[1];
						
						int lineSATD, lineISSUE, limitMIN, limitMAX;
						
						lineSATD = Integer.parseInt(stringLineSATD);
						lineISSUE = Integer.parseInt(stringLineISSUE);
						
						limitMIN = lineSATD - limiar;
						limitMAX = lineSATD + limiar;
						if(lineISSUE > limitMIN && lineISSUE < limitMAX) {
							
							if(!association) {
								a.setSatdInstance(data.getSatdCollection().get(i));
								association = true;
							}
							a.getIssuesInstances().add(data.getIssueCollection().get(j));
						}
					}
					
				}
			}
			
			if(association) {
				this.associationCollection.add(a);
			}
		}
	}
	
	public void makeAssociationContext(Data data){

        int lineInitial, lineFinal, lineIssue;
        ArrayList<Issue> listIssueAux;

        for(int i = 0; i < data.getSatdCollection().size(); i++) {

            AssociationCore a = new AssociationCore();
            a.setSatdInstance(data.getSatdCollection().get(i));

            listIssueAux = new ArrayList<>();
            lineInitial = Integer.parseInt(data.getSatdCollection().get(i).getContextInstances().get(0).lineInitial);
            lineFinal = Integer.parseInt(data.getSatdCollection().get(i).getContextInstances().get(0).lineFinal);

            //add all issues associate with a specific satd
            for (int j = 0; j < data.getIssueCollection().size(); j++) {

                if(data.getSatdCollection().get(i).getPath().equals(data.getIssueCollection().get(j).getPath())) {

                    if(data.getIssueCollection().get(j).getLine().equals("N")) continue;
                    else lineIssue = Integer.parseInt(data.getIssueCollection().get(j).getLine());

                    if(lineIssue > lineInitial && lineIssue < lineFinal) {

                        listIssueAux.add(data.getIssueCollection().get(j));
                    }

                }//end if equals path

            }//end for1 internal

            for(int j = data.getSatdCollection().get(i).getContextInstances().size()-1; j >= 0; j--) {

                lineInitial = Integer.parseInt(data.getSatdCollection().get(i).getContextInstances().get(j).lineInitial);
                lineFinal = Integer.parseInt(data.getSatdCollection().get(i).getContextInstances().get(j).lineFinal);

                for(int k = 0; k < listIssueAux.size(); k++) {

                    lineIssue = Integer.parseInt(listIssueAux.get(k).getLine());

                    if(lineIssue > lineInitial && lineIssue < lineFinal) {
                        listIssueAux.get(k).setContext(data.getSatdCollection().get(i).getContextInstances().get(j).getPriority());
                        a.getIssuesInstances().add(listIssueAux.get(k));
                        listIssueAux.remove(k);
                        k--;
                    }
                }

            }//end for2 internal

            this.associationCollection.add(a);

        }//end for external

    }
	
	public void makePriorizationContext(){
		
		double priorityNivel = 0.0;
        for(int i = 0; i < this.associationCollection.size(); i++){
            
        	priorityNivel = Priorization.getPriorityNivel(this.associationCollection.get(i));
            this.associationCollection.get(i).setPriorityNivel(priorityNivel);
        }
        
        this.orderByPriority();
		
	}
	
	public void makePriorizationContextTerms(){
		
		double priorityNivel = 0.0;
		
        for(int i = 0; i < this.associationCollection.size(); i++){
            
        	priorityNivel = Priorization.getPriorityNivelTerms(this.associationCollection.get(i));
            this.associationCollection.get(i).setPriorityNivel(priorityNivel);
        }
        
        this.orderByPriorityIssues();
		
	}
	
	public void makePriorizationContextIssues(){
		
		double priorityNivel = 0.0;
		
        for(int i = 0; i < this.associationCollection.size(); i++){
            
        	priorityNivel = Priorization.getPriorityNivelIssues(this.associationCollection.get(i));
            this.associationCollection.get(i).setPriorityNivel(priorityNivel);
        }
        
        this.orderByPriorityIssues();
		
	}
	
	public void orderByPriority() {
		
		Collections.sort(this.associationCollection, new CompareTo());
		this.normalize();
	}
	
	public void orderByPriorityIssues() {
		
		Collections.sort(this.associationCollection, new CompareTo());
		//this.normalize();
	}
	
	class CompareTo implements Comparator<AssociationCore> {
	    public int compare(AssociationCore a1, AssociationCore a2) {
	        if (a1.getPriorityNivel() < a2.getPriorityNivel()) return +1;
	        else if (a1.getPriorityNivel() > a2.getPriorityNivel()) return -1;
	        else return 0;
	    }
	}
	
	public void normalize() {
		
		double max, min, base, prorityNormalize = 0;
		
		max = this.associationCollection.get(0).getPriorityNivel();
		min = this.associationCollection.get(this.associationCollection.size()-1).getPriorityNivel();
		base = max - min;
		
        for(int i = 0; i < this.associationCollection.size(); i++){
            
        	if(base == 0) base = 1;
        	prorityNormalize = (this.associationCollection.get(i).getPriorityNivel() - min) / base;
        	
            this.associationCollection.get(i).setPriorityNivel(prorityNormalize);
        }//end for
        
	}
	
	public void show() {
		
        String result = "";
        
        for(int i = 0; i < this.associationCollection.size(); i++){
            
            result += this.associationCollection.get(i).show() + "\n";
        }
        
        System.out.println(result);
	}
	
	public void showPriority() {
		
        String result = "";
        
        for(int i = 0; i < this.associationCollection.size(); i++){
            
            result += this.associationCollection.get(i).getSatdInstance().getDescription() + " - PN: " + this.associationCollection.get(i).getPriorityNivel() + "\n";
        }
        
        System.out.println(result);
	}
	
}
