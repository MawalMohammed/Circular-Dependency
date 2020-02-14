package org.eclipse.jucmnav.grl.CircularDependencyDetection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import seg.jUCMNav.editors.UCMNavMultiPageEditor;

public class DetectionOutputInterface {


UCMNavMultiPageEditor jucmEditor;
private IFile activeFile;
Document jucmDoc;
String fileLocation;




 public DetectionOutputInterface(UCMNavMultiPageEditor jmEditor, IFile jmFile, Document jmDoc, String fileLoc) {
	 
	jucmEditor=jmEditor;
	activeFile=jmFile;
	jucmDoc=jmDoc;
	fileLocation=fileLoc;
	
	

	}
		

//overly populated model BS
public void dependencyCyclesDetector() {
	
	try {
		
		DependencyAnalysisWithPrunningAndPairing da= new DependencyAnalysisWithPrunningAndPairing();
		 			
			double fit, newFit; 
		 
			 int solution [];
			 int newSolution [];
			 int bestSolution [];
			 
			 da.extractInformation(jucmDoc, fileLocation );
			 
			 da.numOfIdentifiedCycles=0;
	
			 if (da.numRemainingLinks>2){
					
			  solution =new int [da.numRemainingLinks];
			  newSolution=new int [da.numRemainingLinks];
			  bestSolution=new int [da.numRemainingLinks];
			  
			  for (int j=0; j<da.noIterations ; j++)  {
				  
				  da.intialzation(solution);
				
				  for (int i=0;i<da.numRemainingLinks;i++)
					bestSolution[i]=solution[i];
				  	da.currentIter++;
				  	da.temperature=da.tmp;
			 
				  	while (da.temperature > 1) {
			
				  		da.tweak(solution,newSolution);	 
				 
				  		fit= da.fitnessFunction(solution);
					
				  		newFit= da.fitnessFunction(newSolution);
		
				  		if (newFit > fit){
				  			for (int i=0;i<da.numRemainingLinks;i++)
				  				solution[i]=newSolution[i];
				  			for (int i=0;i<da.numRemainingLinks;i++)
				  				bestSolution[i]=solution[i];
				  			
				  		}
			 
				  		else if (Math.pow(Math.E, (newFit -fit)/ da.temperature)> Math.random())
				  		{
					
				  			for (int i=0;i<da.numRemainingLinks;i++)
				  				solution[i]=newSolution[i];
						
				  		}
				  
			
				  		da.temperature *= 1-da.coolingRate;
				  		
				  		if(da.isCompleteCycle(bestSolution)){
				  			writeMarkers(activeFile,IMarker.SEVERITY_WARNING ,"A dependency cycle is detected, Remove it and start over", prepareAsAsequenceOfElements(bestSolution));
				  			j=da.noIterations;
				  			break;
				  		}
				  	}
			
			  }
			da.printResults();
	 }
			 else if (da.numRemainingLinks<=2)
				 writeMarkers(activeFile,IMarker.SEVERITY_INFO ,"The model does not include dependency cycles","Graph");
			 else writeMarkers(activeFile,IMarker.SEVERITY_INFO ,"Number of links is less than two, the model cannot include cycles.","Graph");
	
	} catch (Exception e) {
		e.printStackTrace();
	}
}			

	private void writeMarkers(IResource resource, int severity, String message, String location) {
        try {
            IMarker marker = resource.createMarker("org.eclipse.jucmnav.grl.CircularDependencyDetection.DependencyCyclesMarker");
           
            marker.setAttribute(IMarker.SEVERITY, severity);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.LOCATION, location);
           
          
          
           } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
private String prepareAsAsequenceOfElements(int [] bestSolution) {
	String cycle = new String("");
	NodeList intElementsList = jucmDoc.getElementsByTagName("intElements");
	String [][] elements=new String [intElementsList.getLength()][2];
	for (int i=0; i<intElementsList.getLength();i++) {
		Node nNode = intElementsList.item(i);
		Element eElement = (Element) nNode;
		elements[i][0]=eElement.getAttribute("id");
		elements[i][1]=eElement.getAttribute("name");
	}
	int count =0;
	for (int i=0;i<bestSolution.length;i++) {
		if(bestSolution[i]!=0) {
			count++;
		}
	}
	int [] bestSolutionNoZeros=new int [count];	
	count =0;
	for (int i=0;i<bestSolution.length;i++) {
		if(bestSolution[i]!=0) {
			bestSolutionNoZeros[count]=bestSolution[i];
			count++;
		}
	}
	
	for (int i=0;i<bestSolutionNoZeros.length;i++) {
		
			String src=getLinkSrc(Integer.toString(bestSolutionNoZeros[i]));
				for (int j=0;j<elements.length;j++) {
					
					if(src.equalsIgnoreCase(elements[j][0])) {
					cycle=cycle+elements[j][1];
						if (i<bestSolutionNoZeros.length-1)
							cycle=cycle+"-->";
					}
				}
		}
	
		
	return cycle;
}

private String getLinkSrc(String id) {
	NodeList linkList = jucmDoc.getElementsByTagName("links");
	for (int temp = 0; temp < linkList.getLength(); temp++) {
		Node nNode = linkList.item(temp);
		Element eElement = (Element) nNode;
		if(eElement.getAttribute("id").equalsIgnoreCase(id)) {
			return	eElement.getAttribute("src");
		
		}
	}
	return "";
}

}
