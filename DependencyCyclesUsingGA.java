import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Random;


/*
 * 
 * Author: Mawal A. Mohammed
 * 
 * This code is created to evaluate the performance of the genetic algorithm 
 * in detection of Circular dependency in GRL goal models
 * 
 * 
 */


public class DependencyCyclesUsingGA {
	
	int ModelNo=1;
	String fileLoc="C:/Users/mawal/Dropbox/1thesis work/SBSE Paper/test_file"+ModelNo+".jucm";
    
	 Document doc;
	 int allLinks[][];
	 int noOfActors=0;
	 int numLinks=0;
	 int numElements=0;
	 int identifiedCycles[][];
	 int numOfIdentifiedCycles=0;
	 int noIterations=1000000;
	 int populationSize=20;
	 int temperature;
	 double coolingRate = 0.05;
	 double precision =0;
	 double recall=0;
	 int noOfcyclesInTestData[]= new int [] {5,2,3,4,1,1,1};
	 int noOfExp=10;
	 int noOfExpTracker=0;
	 int currentIter=0;
	 int iterationTracker=0;
	 double finalResults[][]=new double [noOfExp][noOfcyclesInTestData[ModelNo-1]+3];
	
	 String path = "C:/Users/mawal/Dropbox/1thesis work/SBSE Paper/DA_results_file"+ModelNo+".csv";
	 
	 File file = new File(path);
    
	 PrintWriter writer ;

	
  void  extractInformation (){
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
		
		
		for (int temp = 0; temp < linkList.getLength(); temp++) {

			Node nNode = linkList.item(temp);
					
			
					
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				
					
					allLinks[temp][0]= Integer.parseInt(eElement.getAttribute("id"));
					allLinks[temp][1]=Integer.parseInt(eElement.getAttribute("dest"));
					allLinks[temp][2]=Integer.parseInt(eElement.getAttribute("src"));
				
				
				}
		}
		
		identifiedCycles=new int [numLinks][numLinks];
		 
		  for (int i=0; i<noOfExp; i++){
		    	finalResults[i][0]=i+1;
		    	
		    }
		    	
		    writer = new PrintWriter(file);
		    
		    writer.print("# of links in the model:");
		    writer.print(",");
		    writer.print(numLinks);
		    writer.println();
		    
		    writer.print("# of elements in the model:");
		    writer.print(",");
		    writer.print(numElements);
		    writer.println();
		    
		    writer.print("# of actors in the model:");
		    writer.print(",");
		    writer.print(noOfActors);
		    writer.println();
		    
		    writer.print("# of cycles in the model:");
		    writer.print(",");
		    writer.print(noOfcyclesInTestData[ModelNo-1]);
		    writer.println();
		    writer.println();
		    

	    } catch (Exception e) {
		e.printStackTrace();
	    }
}
	  
 void intialzation (int [] [] sol, int populationSize){
	  
 for ( int p=0; p < populationSize; p++){
		
	  do{
	  Random rand1 = new Random();
	  Random rand2 = new Random();
	  int randIndex; 
	  int prob;
	 
	  Arrays.fill(sol[p], 0);		  
		  		 
	 
	  
	  boolean included [] = new boolean [numLinks] ;
	  Arrays.fill(included, Boolean.FALSE);
	  
	  for (int i=0; i<numLinks; i++){
		  prob = rand1.nextInt(numLinks);
		
		  if (prob >(currentIter%numLinks-1)) 
			  included[i]=Boolean.TRUE;		  
	  }
	  
	  boolean chosen [] = new boolean [numLinks] ;
	  Arrays.fill(chosen, Boolean.FALSE);
	  
	  	for (int i=0; i<numLinks; i++){
		  
		  if (included[i]){
			  randIndex= rand2.nextInt(numLinks);
		  	  
		  if (!chosen[randIndex]){
			 
			  sol [p][i]=allLinks[randIndex][0];
			  chosen[randIndex]=Boolean.TRUE;
		  }else{
			  i--;
		  }
		  
		  }else{
			  
			  sol [p][i]=0;
			  
		  }
		  
	  } 
	  }while(lengthOfLink(sol[p])<3);
  }
 }
 
  
  int firstLink(int sol[]){
	  int frst=-1;
	  for (int i=0; i<numLinks; i++)
		  if (sol[i]!=0)
		  {
			  frst=i;
			  i=numLinks;
		  }
			
	return frst;
}
  
  int lastLink(int sol[]){
	  int lst=-1;
	  for (int i=numLinks-1; i>=0; i--)
		  if (sol[i]!=0)
		  {
			  lst=i;
			  i=0;
		  }
			
	return lst;
}
  
  int nextLink(int c, int sol[]){
	int nxt=-1;
	for (int i=c+1; i<numLinks; i++)
		  if (sol[i]!=0)
		  {
			  nxt=i;
			  i=numLinks;
		  }
			
	return nxt;
}
  
  int lengthOfLink(int sol[]){
	  
	  int noOfIncludedlinks=0;
	   
	  for (int i=0; i<numLinks; i++){
		  if (sol[i]!=0)
			  noOfIncludedlinks++;
		  	  }
  return noOfIncludedlinks;
  }
  
  int crosspondingIndex(int lnk){
	  
  	  int crossIndex=-1;
	  for (int i=0; i<numLinks; i++){
		  if (allLinks[i][0]==lnk)
		  {
			  crossIndex=i;
			  i=numLinks;
		  }
		  
	  }
	 return crossIndex;
  }
	
  boolean containsLinkByIndex(int l,int sol[])
  {
	  boolean contained=Boolean.FALSE;
	  for (int i=0; i<numLinks; i++){
		  if (sol[i]==allLinks[l][0]){
			  contained=Boolean.TRUE;
		  return contained;
		  }
	  }
	  return contained;
  }
  
  boolean containsLink(int link,int sol[])
  {
	  boolean contained=Boolean.FALSE;
	  for (int i=0; i<numLinks; i++){
		  if (sol[i]==link){
			  contained=Boolean.TRUE;
		  return contained;
		  }
	  }
	  return contained;
  }

  int numOfConsecutiveLinks(int sol[]){
	  
	  int noConsecutiveLinks=0;
	  int crossPrev;
	  int crossNext;
	  
	  int noOfIncludedlinks=lengthOfLink(sol);
	  
	  int previous= firstLink(sol);
	  int next=nextLink(previous,sol); 
	  
	  if (noOfIncludedlinks>1){
	  
	  for (int i=0; i<noOfIncludedlinks-1; i++){
		  
		 
		
					
			crossPrev=crosspondingIndex(sol[previous]);
			
			crossNext=crosspondingIndex(sol[next]);
						 
			 if (allLinks[crossNext][1]== allLinks[crossPrev][2])
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
	  
	  //System.out.println("noOfIncludedlinks"+noOfIncludedlinks);
	  int previous= firstLink(sol);
	  int next=nextLink(previous,sol); 
	  
	  if (noOfIncludedlinks>2){
	  
	  for (int i=0; i<noOfIncludedlinks-1; i++){
					
			crossPrev=crosspondingIndex(sol[previous]);
			
			crossNext=crosspondingIndex(sol[next]);
			 
			 if (allLinks[crossNext][1]!= allLinks[crossPrev][2])
				 return Boolean.FALSE;
				
			
			 previous=next;
			 next=nextLink(previous,sol);
	  		 }
			 
	 
	  int crosslst=crosspondingIndex(sol[lastLink(sol)]);
	  int crossfrst=crosspondingIndex(sol[firstLink(sol)]);
	  
	
		if (allLinks[crossfrst][1]!=allLinks[crosslst][2])
			isCycle=Boolean.FALSE;
	  }else 
			 isCycle=Boolean.FALSE;
	  
	  return isCycle;
  }
 
  void crossover (int [][] population, int ind1, int ind2) {
	  
	 int temp;
	 
	 for (int i=0; i < numLinks; i++) {
		  
		 if (!containsLink(population[ind2][i], population[ind1]) && !containsLink(population[ind1][i], population[ind2])) {
			 temp = population[ind1][i];
			 population[ind1][i]  = population[ind2][i];
			 population[ind2][i]=temp;
		 }
			 
	  }
	}
  
  void mutation(int sol[], int newSol[]){
	  boolean done=Boolean.FALSE;
	  Random rand1 = new Random();
	  Random rand2 = new Random();
	  Random rand = new Random();
	
	  for (int i=0; i<numLinks; i++){
		  newSol[i]=sol[i];
	  }
	  while (!done){
	  int position=rand1.nextInt(numLinks);
	  int valueIndex=rand2.nextInt(numLinks);
	  int prob;
	  prob = rand.nextInt(numLinks);
	  
	
	 if ( sol[position]!=0)
	  {
		  newSol[position]=0;
		  done=Boolean.TRUE;
		  
		
	  }
	  else if ( prob > (currentIter+1)%(numLinks/4) && !containsLinkByIndex(valueIndex,newSol) ){
		  newSol[position]=allLinks[valueIndex][0];
		  done=Boolean.TRUE;
		  
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
		  		
			if (allLinks[crossfrst][1]==allLinks[crosslst][2])
				return 66.0;
			else return 0.0;
	  }else return 0.0;
	  
		  
 }

  void prepareResults(int [] sol){
		 
	  Arrays.sort(sol);
	  
	  for (int i=0; i<numOfIdentifiedCycles;i++)
		  if(Arrays.equals(sol,identifiedCycles[i]))
				return;

	  for (int i=0;i<numLinks;i++)
		 
		  identifiedCycles[numOfIdentifiedCycles][i]=sol[i];
		  
		  numOfIdentifiedCycles++;
		  
		  finalResults[noOfExpTracker][numOfIdentifiedCycles]=iterationTracker+1;
	 
  }
  
 void printResults(){
	 for (int i=0; i<numOfIdentifiedCycles;i++){
		 
		 System.out.println("Cycle No. " + (i+1) +  " { ");
		 
		  for (int j=0;j<numLinks;j++)
			  if(identifiedCycles[i][j]!=0)
				  System.out.println( identifiedCycles[i][j] );
			  
			  
	 }	  
		 
 }
 
 void collectStatistics(){
	 System.out.println("number of links: " + numLinks );
		System.out.println("remaining links: "+ numLinks);
		System.out.println("number of elements: " + numElements );
		System.out.println("remaining actors: "+ noOfActors);
 }
 
  void calculateResultsStatistics(){
		 double ne=noOfExp;
		 System.out.println("Precision: "+ (precision/ne)*100); 
		 System.out.println("Recall: "+ (recall/ne)*100); 
	 }
  
 void writeToExcel() {
  
      writer.print("Experiment #");
      writer.print(",");
       
      for(int j=2;j<(noOfcyclesInTestData[ModelNo-1]+2);j++){
          
          
          writer.print("Cycle No. "+ (j-1));
          writer.print(",");
          
   		}
      
      writer.print("Recall");
      writer.print(",");
      writer.print("Time needed (s)");
      writer.println();
      
      for(int i=0;i<noOfExp;i++){
			
	  for(int j=0;j<(noOfcyclesInTestData[ModelNo-1]+3);j++){
      
		  if (j>0 && j < (noOfcyclesInTestData[ModelNo-1]+1)){
			  if (finalResults[i][j]==0.0)
		        	 writer.print("Not Found");
		        else
				  writer.print(finalResults[i][j]);
			  } else 
				  writer.print(finalResults[i][j]);
  	  
         if(j<(noOfcyclesInTestData[ModelNo-1]+2)) writer.print(",");
         
  		}
	writer.println();
     
      }
      writer.println();
      writer.print("Averages:");
      writer.print(",");
     
      
      
for(int j=1;j<(noOfcyclesInTestData[ModelNo-1]+3);j++){
          double sum=0;
          
	for(int i=0;i<noOfExp;i++){
		sum+=finalResults[i][j];
	}
		
          writer.print(sum/noOfExp);
          if(j<(noOfcyclesInTestData[ModelNo-1]+2))  writer.print(",");
          
   		}

writer.println();
writer.println();
writer.print("Average complexity:");
writer.print(",");

for(int j=1;j<(noOfcyclesInTestData[ModelNo-1]+1);j++){
    double sum=0;
    
for(int i=0;i<noOfExp;i++){
	sum+=finalResults[i][j];
}
	if(sum!=0)
    writer.print(Math.log10(sum/noOfExp)/Math.log10((double)numLinks));
	else
	writer.print(0);	
    if(j<(noOfcyclesInTestData[ModelNo-1]))  writer.print(",");
    
		}
    
  }
 
final Comparator<Double[]> arrayComparator = new Comparator<Double[]>() {
     @Override
     public int compare(Double[] pf1, Double[] pf2) {
         return pf2[0].compareTo(pf1[0]);
     }
 };
  public static void main(String argv[]) {

	  DependencyCyclesUsingGA da= new DependencyCyclesUsingGA();
	 
	  final Double[][] populationFitness = new Double[da.populationSize][2];
	  
	  long time1=0,time2=0;
	  
	  int population [][];
	  int tempPopulation [][];
	  
	  System.out.println("Started");
		 
		 da.extractInformation();
		 
		 Calendar cal = Calendar.getInstance();
	     SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	     String timeStart = sdf.format(cal.getTime());
	     
	     System.out.println(timeStart);

	    //da.collectStatistics();
	  
	
	     for(int exp=0;exp<da.noOfExp; exp++)
		 {
			 	da.noOfExpTracker=exp;
				
				da.numOfIdentifiedCycles=0;
				 
				 try {	
					
				     time1=0;time2=0;
				 
				
				     time1=System.currentTimeMillis();
				     
					 } catch (Exception e) 
					 {
						 System.out.println("Start time cannot be retrieved! " );
					 }
				 
		 if (da.numLinks>2){
		
			 population = new int[da.populationSize][da.numLinks];
			 
			 tempPopulation = new int[da.populationSize][da.numLinks];
			 
			 da.currentIter=3;
				
			 da.intialzation(population, da.populationSize);
			 
			// System.out.println("before::" + Arrays.deepToString(population));
		
			  for (int j=0; j<da.noIterations &&  da.numOfIdentifiedCycles < da.noOfcyclesInTestData[da.ModelNo-1]; j++)  {
			 
				  da.iterationTracker=j;
				//  da.currentIter++;
			
				  for (int pop = 0; pop < da.populationSize; pop++) {
				  
					  if(da.isCompleteCycle(population[pop])){
						
						  da.prepareResults(population[pop]);
						  //break;
					  }
				  }
				  
				  for(int pop=0;pop<da.populationSize; pop++)
					  
					 {
					  
					  populationFitness [pop][0] = da.fitnessFunction(population [pop]);
					  populationFitness [pop][1] =  (double) pop;
					  
					 }
				  
						  
				  Arrays.sort(populationFitness, da.arrayComparator);
				 
				  for(int i=0; i<population.length; i++)
					  for(int k=0; k<population[i].length; k++)
						  tempPopulation[i][k]=population[i][k];
				  
				  for(int i=0; i<population.length; i++) {
					  int replace = populationFitness[i][1].intValue();
					 // System.out.println(replace);
					  for(int k=0; k<population[i].length; k++) {
						  
						  tempPopulation [i][k] = population [replace][k];
										  
					  }
				  }	  
				 
				//  for(int i=0; i<population.length; i++)
				//	  for(int k=0; k<population[i].length; k++)
				//		  population[i][k]=tempPopulation[i][k];
				  
				//  System.out.println("before crossover::" + Arrays.deepToString(tempPopulation));
				  
				  for (int pop = 0; pop < da.populationSize/2; pop++) {
					  
					  da.crossover(tempPopulation, pop, pop+1);					  
				  }
				    
			//	  System.out.println("After crossover::" + Arrays.deepToString(tempPopulation));

				  for (int pop = 0; pop < da.populationSize/2; pop++) {
					  
					  da.mutation(tempPopulation[pop], tempPopulation[(da.populationSize/2)+pop]);					  
				  }
				  
			//	  System.out.println("before::" + Arrays.deepToString(population));
				  
				  for(int i=da.populationSize/2; i<population.length; i++)
					  for(int k=0; k<population[i].length; k++)
						  population[i][k] = tempPopulation[i][k];
				  
				
		  }
			 
	 }
		  else 
			  System.out.println("Number of links is less than two, the model cannot include cycles. Thank you");
				 double nc=da.numOfIdentifiedCycles;
				 double cc=da.noOfcyclesInTestData[da.ModelNo-1];
				 if(nc==0)
					 da.precision+=1;
				 else
					 da.precision+=nc/nc;
				 da.recall+=nc/cc;
				 da.finalResults[exp][da.noOfcyclesInTestData[da.ModelNo-1]+1]= nc/cc;
				 
				
					 
					
					 time2=System.currentTimeMillis();
					 long diff =  time2- time1;
		 			 double diffSeconds = diff/1000.0;
		 			 da.finalResults[exp][da.noOfcyclesInTestData[da.ModelNo-1]+2]=(double)diffSeconds;
		 			 
		 		
				 
		}
				 da.calculateResultsStatistics();
				 //System.out.println("finalResults::" + Arrays.deepToString(da.finalResults));
				 try {
				 da.writeToExcel();
				 } 
				 catch (Exception e) 
				 {
					 System.out.println("could not write to file: " );
				 }
				 
				 cal = Calendar.getInstance();
			     sdf = new SimpleDateFormat("HH:mm:ss");
				 String timeStop = sdf.format(cal.getTime());
				 System.out.println(timeStop);
				
				 System.out.println("Finished");
				
					  da.writer.close();
		 
	 }
	     
	  
	
}

