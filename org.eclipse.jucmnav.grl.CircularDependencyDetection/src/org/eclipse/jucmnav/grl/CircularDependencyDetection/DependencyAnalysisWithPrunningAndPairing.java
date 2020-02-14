package org.eclipse.jucmnav.grl.CircularDependencyDetection;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Arrays;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DependencyAnalysisWithPrunningAndPairing {
	
	int allLinks[][];
	 int prunningMatrix[][];
	 int linksPairs[][];
	 int remainingLinksToBeSearched[][];
	 int identifiedCycles[][];
	 int numOfIdentifiedCycles=0;
	 boolean linksToBeSearched[];
	 int noOfActors=0;
	 int numLinks=0;
	 int numRemainingLinks=0;
	 int numOfPair=0;
	 int numElements=0;
	 
	 int noIterations=1000000;
	 int tmp=1000;
	 int temperature;
	 double coolingRate = 0.05;
	 double precision =0;
	 double recall=0;
	
	
	 int noOfExpTracker=0;
	int currentIter=0;
	int iterationTracker=0;
	
	
	

    
	void  extractInformation (Document doc, String fileLoc){
			try {

				File fXmlFile = new File(fileLoc);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				doc = dBuilder.parse(fXmlFile);
						
				doc.getDocumentElement().normalize();
	
	NodeList linkList = doc.getElementsByTagName("links");
	NodeList elementList=doc.getElementsByTagName("intElements");
	NodeList actorList = doc.getElementsByTagName("actors");
	noOfActors=actorList.getLength();
	
	numElements=elementList.getLength();
	numLinks=linkList.getLength();
	
	
	allLinks=new int [numLinks][3];
	//System.out.println(Arrays.deepToString(allLinks));
	
	for (int temp = 0; temp < linkList.getLength(); temp++) {

		Node nNode = linkList.item(temp);
				
		
				
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;
			
				
				allLinks[temp][0]= Integer.parseInt(eElement.getAttribute("id"));
				allLinks[temp][1]=Integer.parseInt(eElement.getAttribute("dest"));
				allLinks[temp][2]=Integer.parseInt(eElement.getAttribute("src"));
			
			
			}
	}
	
	linksToBeSearched=new boolean [numLinks];
	Arrays.fill(linksToBeSearched, Boolean.TRUE);
	
	prunningMatrix=new int [numLinks][numLinks];
	
	
	for (int temp = 0; temp < numElements; temp++) {

		Node nNode = elementList.item(temp);
				
		
				
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;
			String[] linksSrc =eElement.getAttribute("linksSrc").split(" ");
			String[] linksDest =eElement.getAttribute("linksDest").split(" ");
			
			if (!eElement.getAttribute("linksSrc").equals(""))
			{
				
				int[] linksSrcAsIntegers = new int[linksSrc.length]; 
			
				for (int src = 0; src < linksSrcAsIntegers.length; src++){
					linksSrcAsIntegers[src] = Integer.parseInt(linksSrc[src]); 
					int linksSrcIndex=linkIndex(linksSrcAsIntegers[src]);
					if (!eElement.getAttribute("linksDest").equals("")){
						
						int[] linksDestAsIntegers = new int[linksDest.length]; 
					
						for (int dest = 0; dest < linksDestAsIntegers.length; dest++){
							linksDestAsIntegers[dest] = Integer.parseInt(linksDest[dest]); 
							int linksDestIndex=linkIndex(linksDestAsIntegers[dest]);
							prunningMatrix[linksSrcIndex][linksDestIndex]=1;
			
						}	
					}
				
			}
			
	}
	}
	}
	
	
	pruneLinks();

	remainingLinks();
	
    identifiedCycles=new int [numRemainingLinks][numRemainingLinks];
  
    preparePairsofLinks();
    
   
    	
   
    
  
    
    } catch (Exception e) {
	e.printStackTrace();
    }
}

int linkIndex(int element){
  
  for (int i=0; i<numLinks; i++){
		if (allLinks[i][0]==element)
			return i;
	}
  
  return -1;
}
  
boolean isEmptySrc(int index){
  for(int i=0; i<numLinks; i++)
	  if(prunningMatrix[index][i]!=0)
	  return Boolean.FALSE;
	 
return Boolean.TRUE;
  
}

void pruneDest(int index){
  for(int i=0; i<numLinks; i++)
	  prunningMatrix[i][index]=0;
  }

boolean isEmptyDest(int index){
  for(int i=0; i<numLinks; i++)
	  if(prunningMatrix[i][index]!=0)
	  return Boolean.FALSE;
	 
return Boolean.TRUE;
  
}

void pruneSrc(int index){
  for(int i=0; i<numLinks; i++)
	  prunningMatrix[index][i]=0;
  }

void pruneLinks(){
 
 for(int i=0; i<numLinks; i++){
	 for(int j=0; j<numLinks; j++){
		if (isEmptySrc(j))
			pruneDest(j);
		 
	 }
	 
 }
 
 for(int i=0; i<numLinks; i++){
	 for(int j=0; j<numLinks; j++){
		if (isEmptyDest(j))
			pruneSrc(j);
		 
	 }
	 
 }
}

void remainingLinks(){
  int noRemaininglinks=0;
  
  for(int i=0; i<numLinks; i++)
	  linksToBeSearched[i]=!(isEmptySrc(i));
//  System.out.println(Arrays.toString(linksToBeSearched));
  
  for(int i=0; i<numLinks; i++)
	  if(linksToBeSearched[i])
		  noRemaininglinks++;
  
  remainingLinksToBeSearched= new int [noRemaininglinks][3];
  
  int index=0;
  for(int i=0; i<numLinks; i++)
	  if(linksToBeSearched[i]){
		  remainingLinksToBeSearched[index][0]=allLinks[i][0];
		  remainingLinksToBeSearched[index][1]=allLinks[i][1];
		  remainingLinksToBeSearched[index][2]=allLinks[i][2];
		  
		  index++;
	  }
  numRemainingLinks=index;
  
}

void preparePairsofLinks(){
  for(int i=0; i<numLinks; i++)
		 for(int j=0; j<numLinks; j++)
			if (prunningMatrix[i][j]==1)
				numOfPair++;
			
  linksPairs= new int[numOfPair][2];
  int index=0; 
  for(int i=0; i<numLinks; i++)
		 for(int j=0; j<numLinks; j++)
			if (prunningMatrix[i][j]==1){
				linksPairs[index][0]=allLinks[i][0];
				linksPairs[index][1]=allLinks[j][0];
				index++;
			}
  
}

void intialzation (int [] sol ){
  
//  do{
  Random rand1 = new Random();
  Random rand2 = new Random();
  int randIndex; 
  int prob;
 
  Arrays.fill(sol, 0);
 	  
  boolean included [] = new boolean [numRemainingLinks] ;
  Arrays.fill(included, Boolean.FALSE);
  
  for (int i=0; i<numRemainingLinks; i++){
	  prob = rand1.nextInt(numRemainingLinks);
	
	  if (prob >(currentIter%numRemainingLinks-1)) 
		  included[i]=Boolean.TRUE;		  
	 //System.out.println(" reminder " +Arrays.toString(included));
  }
  
  boolean chosen [] = new boolean [numRemainingLinks] ;
  Arrays.fill(chosen, Boolean.FALSE);
  
  for (int i=0; i<numRemainingLinks; i++){
	  
	  if (included[i]){
		  randIndex= rand2.nextInt(numRemainingLinks);
	  	  
	  if (!chosen[randIndex]){
		 
		  sol [i]=remainingLinksToBeSearched[randIndex][0];
		  chosen[randIndex]=Boolean.TRUE;
	  }else{
		  i--;
	  }
	  
	  }else{
		  
		  sol [i]=0;
		  
	  }
	  
  }
  }

int firstLink(int sol[]){
  int frst=-1;
  for (int i=0; i<numRemainingLinks; i++)
	  if (sol[i]!=0)
	  {
		  frst=i;
		  i=numRemainingLinks;
	  }
		
return frst;
}

int lastLink(int sol[]){
  int lst=-1;
  for (int i=numRemainingLinks-1; i>=0; i--)
	  if (sol[i]!=0)
	  {
		  lst=i;
		  i=0;
	  }
		
return lst;
}

int nextLink(int c, int sol[]){
int nxt=-1;
for (int i=c+1; i<numRemainingLinks; i++)
	  if (sol[i]!=0)
	  {
		  nxt=i;
		  i=numRemainingLinks;
	  }
		
return nxt;
}

int lengthOfLink(int sol[]){
  
  int noOfIncludedlinks=0;
   
  for (int i=0; i<numRemainingLinks; i++){
	  if (sol[i]!=0)
		  noOfIncludedlinks++;
	  	  }
return noOfIncludedlinks;
}

int crosspondingIndex(int lnk){
  
	  int crossIndex=-1;
  for (int i=0; i<numRemainingLinks; i++){
	  if (remainingLinksToBeSearched[i][0]==lnk)
	  {
		  crossIndex=i;
		  i=numRemainingLinks;
	  }
	  
  }
 return crossIndex;
}

int  containLink(int link,int sol[])
{

  for (int i=0; i<numRemainingLinks; i++){
	  if (sol[i]==link){
		return i;
	  }
  }
  return -1;
}

int numOfConsecutiveLinks(int sol[]){
  
  int noConsecutiveLinks=0;
  int crossPrev;
  int crossNext;
  
  int noOfIncludedlinks=lengthOfLink(sol);
  
//  System.out.println("noOfIncludedlinks"+noOfIncludedlinks);
  int previous= firstLink(sol);
  int next=nextLink(previous,sol); 
  
  if (noOfIncludedlinks>1){
  
  for (int i=0; i<noOfIncludedlinks-1; i++){
	  
				
		crossPrev=crosspondingIndex(sol[previous]);
		
		crossNext=crosspondingIndex(sol[next]);
		 
		 if (remainingLinksToBeSearched[crossNext][1]== remainingLinksToBeSearched[crossPrev][2])
			 noConsecutiveLinks++;
		
		 previous=next;
		 next=nextLink(previous,sol);
  		 }
		 }
  
  return noConsecutiveLinks; 
}

boolean isCompleteCycle(int [] sol){
  Boolean isCycle=Boolean.TRUE;
  
  int crossPrev;
  int crossNext;
  
  int noOfIncludedlinks=lengthOfLink(sol);

  int previous= firstLink(sol);
  int next=nextLink(previous,sol); 
  
  if (noOfIncludedlinks>2){
  
  for (int i=0; i<noOfIncludedlinks-1; i++){
	  
	 
	
				
		crossPrev=crosspondingIndex(sol[previous]);
		
		crossNext=crosspondingIndex(sol[next]);
					 
		 
		 if (remainingLinksToBeSearched[crossNext][1]!= remainingLinksToBeSearched[crossPrev][2])
			 return Boolean.FALSE;
			
		
		 previous=next;
		 next=nextLink(previous,sol);
  		 }
		 
 
  int crosslst=crosspondingIndex(sol[lastLink(sol)]);
  int crossfrst=crosspondingIndex(sol[firstLink(sol)]);
  

	if (remainingLinksToBeSearched[crossfrst][1]!=remainingLinksToBeSearched[crosslst][2])
		isCycle=Boolean.FALSE;
  }else 
		 isCycle=Boolean.FALSE;
  
  return isCycle;
}
 


void tweak(int sol[], int newSol[]){
  boolean done=Boolean.FALSE;
  Random rand1 = new Random();
  Random rand2 = new Random();
  Random rand = new Random();
//  boolean isZero=Boolean.FALSE;
  for (int i=0; i<numRemainingLinks; i++){
	  newSol[i]=sol[i];
  }
  while (!done){
  int position=rand1.nextInt(numRemainingLinks);
  int valueIndex=rand2.nextInt(numOfPair);
  int prob;
  prob = rand.nextInt(numRemainingLinks);
  

 if ( sol[position]!=0)
  {
	  newSol[position]=0;
	  done=Boolean.TRUE;
	  
	
  }
  else if ( prob > (currentIter+1)%(numRemainingLinks/4) )
  {
	  int contains1= containLink(linksPairs[valueIndex][0],newSol);
	  int contains2 =containLink(linksPairs[valueIndex][1],newSol);
	  if (contains1==-1 && contains2==-1){
		 newSol[position]=linksPairs[valueIndex][0];
		 newSol[((position+1)%numRemainingLinks)]=linksPairs[valueIndex][1];
	  done=Boolean.TRUE;
	  }else if (contains1!=-1 && contains2==-1){
		  newSol[((contains1+1)%numRemainingLinks)]=linksPairs[valueIndex][1];
		  done=Boolean.TRUE;
	  
	 } else if (contains1==-1 && contains2!=-1){
		 newSol[((contains2-1)+numRemainingLinks)%numRemainingLinks]=linksPairs[valueIndex][0];
		 done=Boolean.TRUE;
     } else if (contains1!=-1 && contains2!=-1){
    	 newSol[contains2]=0;
    	 newSol[((contains1+1)%numRemainingLinks)]=linksPairs[valueIndex][1];
		 done=Boolean.TRUE;
     } 
}
}

}

double fitnessFunction( int sol[]){
  
  
 
  if (isCompleteCycle(sol))
   return 100;
  else if (lengthOfLink(sol)>2){
	  double nc=numOfConsecutiveLinks(sol);
	  double len=lengthOfLink(sol);
	  return (nc/len)*100;
  }
  else if (lengthOfLink(sol)==2)
  {
	  int crosslst=crosspondingIndex(sol[lastLink(sol)]);
	  int crossfrst=crosspondingIndex(sol[firstLink(sol)]);
	  		
		if (remainingLinksToBeSearched[crossfrst][1]==remainingLinksToBeSearched[crosslst][2])
			return 66.0;
		else return 0.0;
  }else return 0.0;
  
	  
}

void prepareResults(int [] sol){
 
  Arrays.sort(sol);
  
  
  for (int i=0; i<numOfIdentifiedCycles;i++)
	  if(Arrays.equals(sol,identifiedCycles[i]))
			return;
			
	 for (int i=0;i<numRemainingLinks;i++)
	 identifiedCycles[numOfIdentifiedCycles][i]=sol[i];
	  
	  numOfIdentifiedCycles++;
	  
 
}

void printResults(){
 for (int i=0; i<numOfIdentifiedCycles;i++){
	 
	// System.out.println("Cycle No. " + (i+1) +  " { ");
	 
	 // for (int j=0;j<numRemainingLinks;j++)
		//  if(identifiedCycles[i][j]!=0)
			 // System.out.println( identifiedCycles[i][j] );
		  
 }	  
	 
}

void collectStatistics(){
 System.out.println("number of links: " + numLinks );
	System.out.println("remaining links: "+ numRemainingLinks);
	System.out.println("number of elements: " + numElements );
	System.out.println("remaining actors: "+ noOfActors);
}





}
			

