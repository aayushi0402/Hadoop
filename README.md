# APACHE HADOOP

Hadoop Documentation: https://hadoop.apache.org/

- To know about the Hadoop Architecture
- See handy Hadoop commands
- Download Hadoop

## Installation Prerequisites 
- Ubuntu on a Virtual Machine or on a Dual Boot Mode
- Java 8 is preferred. Java 7 can be used for Version 2.7 and later of Hadoop, while Java 6 supports Hadoop versions of 2.6 or earlier.
- A good amount of RAM (Minimum 8GB)

## Hadoop Installation Guide
Once you have installed Ubuntu, you can begin with the installation by opening the Terminal window.
Below are the steps I used to follow in order to get Hadoop up and running in my system.

1. First set of steps:
```sh
# Run Updates
sudo apt update
sudo apt install openjdk-8-jdk -y
#Check Java Version
java -version; javac -version
sudo apt install openssh-server openssh-client -y
#Create a new user for Hadoop
sudo adduser hadoopuser
su - hadoopuser
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 0600 ~/.ssh/authorized_keys
ssh localhost
```
2. Second set of steps: Installing Hadoop
```sh
Download using wget
wget https://downloads.apache.org/hadoop/common/hadoop-3.2.2/hadoop-3.2.2.tar.gz
tar xzf hadoop-3.2.2.tar.gz
```

3. Edit Configuration Files for Hadoop with the following content:
 a. sudo nano .bashrc [In case you get an error saying hadoopuser is not sudo user then follow below steps]:
    - sudo aayushi
    - sudo adduser hadoopuser 


1. Add the following to .bashrc file
```
sudo nano .bashrc
#The bashrc opens for editing
export HADOOP_HOME=/home/hadoopuser/hadoop-3.2.1
export HADOOP_INSTALL=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin
export HADOOP_OPTS"-Djava.library.path=$HADOOP_HOME/lib/nativ"
 #SAVE THE FILE AND EXIT
 #Then run below command:
 source ~/.bashrc
```

2. Add the following to hadoop-env.sh file
```
sudo nano $HADOOP_HOME/etc/hadoop/hadoop-env.sh
#Add below line at the end of the file
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

3. Add the following to core-site.xml file
```
#Add below lines between opening and closing tags for configuration i.e "<configuration>" and "<"/configuration>"

   <property>
        <name>hadoop.tmp.dir</name>
        <value>/home/hadoopuser/tmpdata</value>
        <description>A base for other temporary directories.</description>
    </property>
    <property>
        <name>fs.default.name</name>
        <value>hdfs://localhost:9000</value>
        <description>The name of the default file system></description>
    </property>
```

4. Add the following to hdfs-site.xml file
```
#Add below lines between opening and closing tags for configuration i.e "<configuration>" and "<"/configuration>"

<property>
  <name>dfs.data.dir</name>
  <value>/home/hadoopuser/dfsdata/namenode</value>
</property>
<property>
  <name>dfs.data.dir</name>
  <value>/home/hadoopuser/dfsdata/datanode</value>
</property>
<property>
  <name>dfs.replication</name>
  <value>1</value>
</property>
```

5. Add the following to mapred-site.xml file
```
#Add below lines between opening and closing tags for configuration i.e "<configuration>" and "<"/configuration>"

<property>
  <name>mapreduce.framework.name</name>
  <value>yarn</value>
</property>
```

6. Add the following to yarn-site.xml file
```
#Add below lines between opening and closing tags for configuration i.e "<configuration>" and "<"/configuration>"

<property>
  <name>yarn.nodemanager.aux-services</name>
  <value>mapreduce_shuffle</value>
</property>
<property>
  <name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
  <value>org.apache.hadoop.mapred.ShuffleHandler</value>
</property>
<property>
  <name>yarn.resourcemanager.hostname</name>
  <value>127.0.0.1</value>
</property>
<property>
  <name>yarn.acl.enable</name>
  <value>0</value>
</property>
<property>
  <name>yarn.nodemanager.env-whitelist</name>
  <value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PERPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAPRED_HOME</value>
</property>
```
## Launching Hadoop

``` sh
hdfs namenode -format

./start-dfs.sh
./start-yarn.sh

# Test if Hadoop Daemons are up and running: you should see 6 components running.
jps

# Create a test directory to check HDFS is working properly
hadoop fs -mkdir /HadoopStorageTest/Test.txt
# List it:
hadoop fs -ls /HadoopStorageTest/
```

## Compiling and Creating JAR Files
 
 ```sh
 #Setting the path for the hadoop classpath
 export HADOOP_CLASSPATH=$(hadoop classpath)
 
 # Compiling your java program file(that contains Mapper, Reducer and Driver class all in one file) and placing the output of the classes in a folder named /classes in your local directory
 
 sudo javac -classpath ${HADOOP_CLASSPATH} -d '/home/aayushi/Documents/Hadoop/DinosaurAnalysis/classes' '/home/aayushi/Documents/Hadoop/DinosaurAnalysis/Dino.java'
 
 # Creating JAR from the classes folder
 sudo jar -cvf Dino.jar -C '/home/aayushi/Documents/Hadoop/DinosaurAnalysis/classes' .
 
 # Running the JAR file
 hadoop jar '/home/aayushi/Documents/Hadoop/DinosaurAnalysis/Dino.jar' Dino /DinoAnalysis/Input /DinoAnalysis/Output
 ```
