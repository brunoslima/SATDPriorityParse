import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.Soundbank;

public class FileAplication {
    
    public static void readSATD(String arq, Data data) throws FileNotFoundException {

        String line = "";
        String elements[];    
        Map<String,String> valuesLineSATD = new HashMap<String,String>();

        Scanner readFileSatd = new Scanner(new BufferedReader(new FileReader(arq)));
        
        while (readFileSatd.hasNextLine()) {

            line = readFileSatd.nextLine();
            elements = line.split(";");
            line = elements[0] + " - " + elements[1] + " - " + elements[2] + " - " + elements[3];
            if(!valuesLineSATD.containsKey(line)){
            	
                valuesLineSATD.put(line,line);
                Satd satdInstance = new Satd(elements[0],elements[1],elements[2],elements[3]);
                data.getSatdCollection().add(satdInstance);
            }
            
        }
        
    }
    
    public static void readISSUES(String arq, Data data) throws FileNotFoundException {

        String line = "";
        String elements[];    
        Scanner readFileIssue = new Scanner(new BufferedReader(new FileReader(arq)));
        
        while (readFileIssue.hasNextLine()) {

            line = readFileIssue.nextLine();
            elements = line.split(";");
            
            Issue issueInstance = new Issue(elements[0],elements[1],elements[2],elements[3],elements[4],elements[5],elements[6]);
            data.getIssueCollection().add(issueInstance);
        }
        
    }
    
    public static void readClass(String arq, ClassJava javaFile) throws FileNotFoundException {
    	
        Scanner readFileSatd = new Scanner(new BufferedReader(new FileReader(arq)));
        
        while (readFileSatd.hasNextLine()) {

            javaFile.getLines().add(readFileSatd.nextLine());
        }
    }

    public static void writePriorityList(String name, Core core) throws IOException {

        FileWriter arq = null;
        try {
            arq = new FileWriter(name);
        } catch (IOException ex) {
            Logger.getLogger(FileAplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter record = new PrintWriter(arq);

        record.println("SATD;Path;Line;Priority");
        for (int i = 0; i < core.getAssociationCollection().size(); i++) {

            record.print(core.getAssociationCollection().get(i).getSatdInstance().getDescription() + ";");
            record.print(core.getAssociationCollection().get(i).getSatdInstance().getPath() + ";");
            record.print(core.getAssociationCollection().get(i).getSatdInstance().getLine() + ";");
            record.println(core.getAssociationCollection().get(i).getPriorityNivel());

        }

        arq.close();
    }
    
    public static void writeIssuesAssociatesAmount(String name, Core core) throws IOException {

        FileWriter arq = null;
        try {
            arq = new FileWriter(name);
        } catch (IOException ex) {
            Logger.getLogger(FileAplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter record = new PrintWriter(arq);

        record.println("Type;Description;Class;Path;Line;Severity;Rework;Context");
        for (int i = 0; i < core.getAssociationCollection().size(); i++) {

        	for(int j = 0; j < core.getAssociationCollection().get(i).getIssuesInstances().size(); j++) {
        		
                record.print(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getType() + ";");
                record.print(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getDescription() + ";");
                record.print(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getResource() + ";");
                record.print(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getPath() + ";");
                record.print(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getLine() + ";");
                record.print(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getSeverity() + ";");
                record.print(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getRework() + ";");
                record.println(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getContext());

        	}

        }

        arq.close();
    }
    
    public static void writeIssuesSeverityAmount(String name, Core core) throws IOException {

    	int blocker, critical, major, minor, info;
    	blocker = critical = major = minor = info = 0;

        for (int i = 0; i < core.getAssociationCollection().size(); i++) {

        	for(int j = 0; j < core.getAssociationCollection().get(i).getIssuesInstances().size(); j++) {
        		
        		if(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getSeverity().equals("BLOCKER")) blocker++;
        		else if(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getSeverity().equals("CRITICAL")) critical++;
        		else if(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getSeverity().equals("MAJOR")) major++;
        		else if(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getSeverity().equals("MINOR")) minor++;
        		else if(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getSeverity().equals("INFO")) info++;
        	}
        }
        
        FileWriter arq = null;
        try {
            arq = new FileWriter(name);
        } catch (IOException ex) {
            Logger.getLogger(FileAplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter record = new PrintWriter(arq);
        
        record.println("BLOCKER;" + blocker);
        record.println("CRITICAL;" + critical);
        record.println("MAJOR;" + major);
        record.println("MINOR;" + minor);
        record.println("INFO;" + info);
        
        arq.close();
    }
    
    public static void writeIssuesAssociatesGreaterSeverity(String name, Core core) throws IOException {

        ArrayList<Issue> issuesBlocker = new ArrayList<>();
        ArrayList<Issue> issuesCritical = new ArrayList<>();
    	
        for (int i = 0; i < core.getAssociationCollection().size(); i++) {

        	for(int j = 0; j < core.getAssociationCollection().get(i).getIssuesInstances().size(); j++) {
        		
        		if(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getSeverity().equals("BLOCKER")) {
        			
        			issuesBlocker.add(core.getAssociationCollection().get(i).getIssuesInstances().get(j));
        		}
        		else if(core.getAssociationCollection().get(i).getIssuesInstances().get(j).getSeverity().equals("CRITICAL")) {
        			
        			issuesCritical.add(core.getAssociationCollection().get(i).getIssuesInstances().get(j));
        		}
        		
        	}
        }
        
        FileWriter arq = null;
        try {
            arq = new FileWriter(name);
        } catch (IOException ex) {
            Logger.getLogger(FileAplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter record = new PrintWriter(arq);
        
        for(int i = 0; i < issuesBlocker.size(); i++) {
        	
            record.print(issuesBlocker.get(i).getType() + ";");
            record.print(issuesBlocker.get(i).getDescription() + ";");
            record.print(issuesBlocker.get(i).getResource() + ";");
            record.print(issuesBlocker.get(i).getPath() + ";");
            record.print(issuesBlocker.get(i).getLine() + ";");
            record.print(issuesBlocker.get(i).getSeverity() + ";");
            record.print(issuesBlocker.get(i).getRework() + ";");
            record.println(issuesBlocker.get(i).getContext());
        }
        
        for(int i = 0; i < issuesCritical.size(); i++) {
        	
            record.print(issuesCritical.get(i).getType() + ";");
            record.print(issuesCritical.get(i).getDescription() + ";");
            record.print(issuesCritical.get(i).getResource() + ";");
            record.print(issuesCritical.get(i).getPath() + ";");
            record.print(issuesCritical.get(i).getLine() + ";");
            record.print(issuesCritical.get(i).getSeverity() + ";");
            record.print(issuesCritical.get(i).getRework() + ";");
            record.println(issuesCritical.get(i).getContext());
        }
        
        arq.close();
    }
    
    
    public static void writeIssuesAssociatesFrequent(String name, Core core) throws IOException {

        Map<String,Integer> issuesFrequent = new HashMap<String,Integer>();
        int frequent;
        ArrayList<Issue> issuesAssociates = new ArrayList<>();
    	
        for (int i = 0; i < core.getAssociationCollection().size(); i++) {

        	for(int j = 0; j < core.getAssociationCollection().get(i).getIssuesInstances().size(); j++) {
        		
        		String typeIssue = core.getAssociationCollection().get(i).getIssuesInstances().get(j).getType();
        		issuesAssociates.add(core.getAssociationCollection().get(i).getIssuesInstances().get(j));
        		
        		if(!issuesFrequent.containsKey(typeIssue)) {
        			
        			issuesFrequent.put(typeIssue, 1);
        		}
        		else {
        			frequent = issuesFrequent.get(typeIssue) + 1;
        			issuesFrequent.put(typeIssue,frequent);
        		}
        		
        	}
        }
        
        
        List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(issuesFrequent.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() { 
            public int compare(Map.Entry<String, Integer> o1,  
                               Map.Entry<String, Integer> o2) 
            { 
                return (o2.getValue()).compareTo(o1.getValue()); 
            } 
        }); 
          
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>(); 
        for (Map.Entry<String, Integer> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        }
        
        
        FileWriter arq = null;
        try {
            arq = new FileWriter(name);
        } catch (IOException ex) {
            Logger.getLogger(FileAplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter record = new PrintWriter(arq);
        
        for (Map.Entry<String,Integer> pair : temp.entrySet()) {
            record.print(pair.getKey() + ";");
            record.print(pair.getValue() + ";");
            
            for(int i = 0; i < issuesAssociates.size(); i++) {
            	
            	if(pair.getKey().equals(issuesAssociates.get(i).getType())) {
                    record.print(issuesAssociates.get(i).getDescription() + ";");
                    record.print(issuesAssociates.get(i).getSeverity() + ";");
                    record.println(issuesAssociates.get(i).getRework());
            		break;
            	}
            }
           
        }
        
        arq.close();
    }
    
    public static Map<String,Satd> getSATDPriorityVersion(String arq, Data data, Map<String,Satd> satdPriority) throws FileNotFoundException {
    
        String line = "";
        String elements[];    
        Map<String,String> valuesLineSATD = new HashMap<String,String>();
        ArrayList<Satd> listSatdVersion = new ArrayList<>();
        
        //read satd Version File
        Scanner readFileSatd = new Scanner(new BufferedReader(new FileReader(arq)));
        
        while (readFileSatd.hasNextLine()) {

            line = readFileSatd.nextLine();
            elements = line.split(";");
            line = elements[0] + " - " + elements[1] + " - " + elements[2] + " - " + elements[3];
            if(!valuesLineSATD.containsKey(line)){
            	
                valuesLineSATD.put(line,line);
                Satd satdInstance = new Satd(elements[0],elements[1],elements[2],elements[3]);
                listSatdVersion.add(satdInstance);
            }
            
        }
        
        String satdBase = "";
        String satdVersion = "";
        Boolean equal;
        
        for (int i = 0; i < data.getSatdCollection().size(); i++) {

        	equal = false;
        	
        	for(int j = 0; j < listSatdVersion.size(); j++) {
        		
        		satdBase = data.getSatdCollection().get(i).getDescription() + ";" + data.getSatdCollection().get(i).getPath() + ";" + data.getSatdCollection().get(i).getResource();
        		satdVersion = listSatdVersion.get(j).getDescription() + ";" + listSatdVersion.get(j).getPath() + ";" + listSatdVersion.get(j).getResource();
        		
        		if(satdBase.equals(satdVersion)) {
        			equal = true;
        			break;
        		}
        	}
        	
        	if(equal == false && !satdPriority.containsKey(satdBase)) {
        		
        		satdPriority.put(satdBase,data.getSatdCollection().get(i));
        	}
        	
        }
    	
    	return satdPriority;
    }
    
    public static void writeSATDPriorityVersion(String name, Map<String,Satd> satdPriority) throws IOException {

        FileWriter arq = null;
        try {
            arq = new FileWriter(name);
        } catch (IOException ex) {
            Logger.getLogger(FileAplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter record = new PrintWriter(arq);

        for (Map.Entry<String,Satd> pair : satdPriority.entrySet()) {
            record.print(pair.getValue().getDescription() + ";");
            record.print(pair.getValue().getPath() + ";");
            record.println(pair.getValue().getLine() + ";");
            
        }

        arq.close();
    }
    
    public static Map<String,Integer> inicializeTerms(){
    	
        Map<String,Integer> terms = new HashMap<String,Integer>();

    	terms.put("serious", 0);
    	terms.put("urgent", 0);
    	terms.put("fundamental", 0);
    	terms.put("terrible", 0);
    	terms.put("fugly", 0);
    	terms.put("horrible", 0);
    	terms.put("add it now", 0);
    	terms.put("system malfunctioning", 0);
    	terms.put("fail", 0);
    	terms.put("fatal", 0);
    	terms.put("problem", 0);
    	terms.put("issue", 0);
    	terms.put("bug", 0);
    	terms.put("error", 0);
    	terms.put("broken", 0);
    	terms.put("exception", 0);
    	terms.put("untested", 0);
    	terms.put("not tested", 0);
    	terms.put("needs tested", 0);
    	terms.put("more tests", 0);
    	terms.put("improve this test", 0);
    	terms.put("yuck", 0);
    	terms.put("ugly", 0);
    	terms.put("stupid", 0);
    	terms.put("junk", 0);
    	terms.put("not work", 0);
    	terms.put("need the correctness", 0);
    	terms.put("wrong", 0);
    	terms.put("implement", 0);
    	terms.put("properly", 0);
    	terms.put("improve", 0);
    	terms.put("tidy this up", 0);
    	terms.put("fix", 0);
    	terms.put("killing", 0);
    	terms.put("slow", 0);
    	terms.put("inefficient", 0);
    	terms.put("optimize", 0);
    	terms.put("do this more efficiently", 0);
    	terms.put("check", 0);
    	terms.put("nasty", 0);
    	terms.put("might hang", 0);
    	terms.put("fragile", 0);
    	terms.put("hack", 0);
    	terms.put("ugliest", 0);
    	terms.put("fugly code", 0);
    	terms.put("code violates", 0);
    	terms.put("silly", 0);
    	terms.put("don't like", 0);
    	terms.put("does not make sense", 0);
    	terms.put("this needs work", 0);
    	terms.put("find a cleaner way to do this", 0);
    	terms.put("bail out", 0);
        
    	return terms;
    	
    }
    
    public static Map<String,Integer> countTermsAssociate(Data data){
    
    	Map<String,Integer> terms = inicializeTerms();
    	
    	for (Map.Entry<String,Integer> pair : terms.entrySet()) {

        	for (int i = 0; i < data.getSatdCollection().size(); i++) {
        	
        		if(data.getSatdCollection().get(i).getDescription().contains(pair.getKey())) {
        			
        			int count = terms.containsKey(pair.getKey()) ? terms.get(pair.getKey()) : 0;
        			terms.put(pair.getKey(), count + 1);
        			
        		}
        		
        	}
            
    	}
    	
        List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(terms.entrySet()); 
        
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() { 
            public int compare(Map.Entry<String, Integer> o1,  
                               Map.Entry<String, Integer> o2) 
            { 
                return (o2.getValue()).compareTo(o1.getValue()); 
            } 
        }); 
          
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>(); 
        for (Map.Entry<String, Integer> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        }
    	
    	
    	return temp;
    }
    
    public static int getFPSeverity(ArrayList<Satd> satdList, Core core) {
    	
    	int fpSeverity = 0;
    	String satdDataString;
    	String satdListString;
    	String severity;
    	Boolean equal = false;
    	
    	for(int i = 0; i < core.getAssociationCollection().size(); i++) {
    		
    		equal = false; 
    		
    		for(int j = 0; j < satdList.size(); j++) {
    			        		
        		satdDataString = core.getAssociationCollection().get(i).getSatdInstance().getDescription();
        		satdListString = satdList.get(j).getDescription();
    			
    			if(satdDataString.equals(satdListString)) {
    				equal = true;
    				break;
    			}
        		
    		}//end for internal
    		
			if(equal) {
				
				for(int k = 0; k < core.getAssociationCollection().get(i).getIssuesInstances().size(); k++) {
					severity = core.getAssociationCollection().get(i).getIssuesInstances().get(k).getSeverity();
					
					if(severity.equalsIgnoreCase("BLOCKER") || severity.equalsIgnoreCase("CRITICAL")) {
						fpSeverity++;
						break;
					}	
				}	
			}
    		
    	}//end for external
    	
    	return fpSeverity;
    }
    
    
}//End class
