# Circular-Dependency-in-GRL
This plugin is used to help detect and correct dependency cycles in GRL.
The source code and a downloadable version of the pulgin are provided.

Important notes: 

      (1) This tool works on GRL models created with jUCMNav tool. 
       Therefore, jUCMNav plugin needs to be installed before installing this plugin

      (2) This plugin is developed and tested on Eclipse committers 6-2019

      (3) This plugin is a beta version
         
      (4) Source code is located in :
         
       org.eclipse.jucmnav.grl.CircularDependencyDetection -> 
       
       /src/org/eclipse/jucmnav/grl/CircularDependencyDetection


Installation Instructions:

First way: 

            Go to Eclipse Help Menu -> Install New Software -> Add
                          
                          Name:GRLDependencyCyclesDetector
                          Location: http://softwareengineeringresearch.net/GRLDependencyCyclesDetector/

Second way: 

            (1) Download GRLDependencyCyclesDetector folder from this repository to your machine

            (2) Expand the downloaded RAR 
                        
            (3) Go to Eclipse Help Menu -> Install New Software -> Add -> Local -> Location of the expanded RAR
                               
                          Name:GRLDependencyCyclesDetector
                         
