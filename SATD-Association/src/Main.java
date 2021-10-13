import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws IOException {
				
		Data data = new Data();
		ClassJava javaFile = new ClassJava();
		
		FileAplication.readSATD("data//apache-ant-1.7.0-base.csv", data);
		FileAplication.readISSUES("data//apache-ant-1.7.0-issues.csv", data);
		
		//FileAplication.readSATD("data//jmeter-2.6-base.csv", data);
		//FileAplication.readISSUES("data//jmeter-2.6-issues.csv", data);
		
		//FileAplication.readSATD("data//jedit-4.2-base.csv", data);
		//FileAplication.readISSUES("data//jedit-4.2-issues.csv", data);
		
		//FileAplication.readSATD("data//jfreechart-1.0.3-base.csv", data);
		//FileAplication.readISSUES("data//jfreechart-1.0.3-issues.csv", data);
				
		//FileAplication.readSATD("data//squirrel-3.0.3-base.csv", data);
		//FileAplication.readISSUES("data//squirrel-3.0.3-issues.csv", data);
		
		ContextIdentifier.searchSATDLocation(data);
		Core core = new Core();
		core.makeAssociationContext(data);
		core.makePriorizationContext();
		//core.makePriorizationContextTerms();
		//core.makePriorizationContextIssues();
		//core.showPriority();

		try {
			FileAplication.writePriorityList("DataResult/list-priority.csv", core);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//============================================================
		//ANALYSIS====================================================
		//============================================================
		System.out.println("Amount SATD: " + data.getSatdCollection().size());
		
		System.out.println("Write Issues Associate!");
		FileAplication.writeIssuesAssociatesAmount("DataResult/issues-associates.csv", core);
		
		System.out.println("Write Issues Severity Amount!");
		FileAplication.writeIssuesSeverityAmount("DataResult/issues-severity-amount.csv", core);
		
		System.out.println("Write Issues Greater Severity!");
		FileAplication.writeIssuesAssociatesGreaterSeverity("DataResult/issues-greater-severity.csv", core);
		
		System.out.println("Write Issues Associates Frequent!");
		FileAplication.writeIssuesAssociatesFrequent("DataResult/issues-frequent.csv", core);
		
	}
	
}
