README.txt 

#Running sudo apt-get update before installing java
sudo apt-get update
#Get java installed 
sudo apt-get install openjdk-8-jdk 
sudo apt-get install wget

cd to directory of source code 
cd src
javac AutoReview.java  
java AutoReview /path/to/log/file/
(Note: the path to log file must end with .log/, don’t forget the /)