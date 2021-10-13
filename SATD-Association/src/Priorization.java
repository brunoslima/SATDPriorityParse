import java.util.ArrayList;

public class Priorization {

	public static double getPriorityNivel(AssociationCore a) {
		
		double priorityNivel = 0.0;
		
		if(getEquationIssues(a.getIssuesInstances()) != 0) {
			
			priorityNivel = getEquationSATD(a.getSatdInstance().getDescription()) * getEquationIssues(a.getIssuesInstances());
		}
		else {
			
			priorityNivel = getEquationSATD(a.getSatdInstance().getDescription());
		}
		
		return priorityNivel;
	}
	
	public static double getPriorityNivelTerms(AssociationCore a) {
		
		double priorityNivel = 0.0;
		
		priorityNivel = getEquationSATD(a.getSatdInstance().getDescription());
		return priorityNivel;
	}
	
	public static double getPriorityNivelIssues(AssociationCore a) {
		
		double priorityNivel = 0.0;
		
		priorityNivel = getEquationIssues(a.getIssuesInstances());
		return priorityNivel;
	}
	
	public static double getEquationSATD(String description) {
		
		double priority = 1.0;
		description = description.toLowerCase();
		
		//dictionary
		if(description.contains("serious")) priority += 1;
		if(description.contains("urgent")) priority += 1;
		if(description.contains("fundamental")) priority += 1;
		if(description.contains("terrible")) priority += 1;
		if(description.contains("fugly") || description.contains("horrible")) priority += 1;
		if(description.contains("add it now")) priority += 1;
		if(description.contains("system malfunctioning")) priority += 1;
		if(description.contains("fail") || description.contains("fails") || description.contains("failure")) priority += 1;
		if(description.contains("fatal")) priority += 1;
		if(description.contains("problematic") || description.contains("problem") || description.contains("issue")) priority += 1;
		if(description.contains("bug") || description.contains("bugfix")) priority += 1;
		if(description.contains("error") || description.contains("error handling")) priority += 1;
		if(description.contains("broken")) priority += 1;
		if(description.contains("exception")) priority += 1;
		if(description.contains("untested") || (description.contains("not") && description.contains("tested")) || description.contains("needs tested")) priority += 1;
		if(description.contains("more tests")) priority += 1;
		if(description.contains("improve this test")) priority += 1;
		if(description.contains("yuck")) priority += 1;
		if(description.contains("ugly")) priority += 1;
		if(description.contains("stupid")) priority += 1;
		if(description.contains("junk")) priority += 1;
		if(description.contains("not work")) priority += 1;
		if(description.contains("need the correctness")) priority += 1;
		if(description.contains("wrong") || description.contains("wrong result") || description.contains("this is wrong") || description.contains("probably wrong")) priority += 1;
		if(description.contains("implement") && description.contains("properly")) priority += 1;
		if(description.contains("improve this a lot") || description.contains("improve")) priority += 1;
		if(description.contains("tidy this up")) priority += 1;
		if(description.contains("fixme") || description.contains("fix") || description.contains("fix it") || description.contains("must fix it") || description.contains("to fix") || description.contains("we ought to fix")) priority += 1;
		if(description.contains("killing")) priority += 1;
		if(description.contains("slow")) priority += 1;
		if(description.contains("inefficient")) priority += 1;
		if(description.contains("optimize")) priority += 1;
		if(description.contains("do this more efficiently")) priority += 1;
		if(description.contains("check") || description.contains("check this result")) priority += 1;
		if(description.contains("nasty")) priority += 1;
		if(description.contains("might hang")) priority += 1;
		if(description.contains("fragile")) priority += 1;
		if(description.contains("hack")) priority += 1;
		if(description.contains("ugliest")) priority += 1;
		if(description.contains("fugly code")) priority += 1;
		if(description.contains("code violates")) priority += 1;
		if(description.contains("silly")) priority += 1;
		if(description.contains("don't like")) priority += 1;
		if(description.contains("does not make sense")) priority += 1;
		if(description.contains("this needs work")) priority += 1;
		if(description.contains("find a cleaner way to do this")) priority += 1;
		if(description.contains("bail out")) priority += 1;
		
		return priority;
	}
	
	public static double getEquationIssues(ArrayList<Issue> issues) {
		
		double priority = 0.0, sum = 0.0;
		
		for(Issue i : issues) {
			sum = 0.0;
			
			if(i.getSeverity().equals("BLOCKER")) {
				sum += 4;
			}
			else if(i.getSeverity().equals("CRITICAL")) {
				sum += 3;
			}
			else if(i.getSeverity().equals("MAJOR")) {
				sum += 2;
			}
			else if(i.getSeverity().equals("MINOR")) {
				sum += 1;
			}
			else { //INFO
				sum += 0;
			}
			
			if(!i.getRework().equals("N")){
				sum *= Integer.parseInt(i.getRework());
			}
			
			sum /= i.getContext();
			
			priority += sum;
			
		}//end for		
		
		return priority;
	}//end
	
}
