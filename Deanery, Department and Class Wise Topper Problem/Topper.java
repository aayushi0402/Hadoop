import java.io.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;

import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;

import org.apache.hadoop.util.*;

public class Topper {

  public static class MapIt
       extends Mapper<Object, Text, Text, Text>{
    @Override
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
            String data[]=value.toString().split(",");
            String key1 = data[0];
            String dean_deets = String.format("%s-%s",data[0],data[5]);
            String dept_deets = String.format("%s-%s",data[0],data[5]);
            String class_deets = String.format("%s-%s",data[0],data[5]);
            //Send only Deanery specific details to Deanery Keys
            context.write(new Text(String.format("Deanery: %s Topper ----->",data[6])), new Text(dean_deets));
            //Send only Department specific details to Deanery Keys
            context.write(new Text(String.format("Department: %s Topper ----->",data[2])), new Text(dept_deets));
            //Send only Class specific details to Deanery Keys
            context.write(new Text(String.format("Class: %s Topper ----->",data[3])), new Text(class_deets));
                
    }
}

  public static class ReduceIt
       extends Reducer<Text,Text,Text,Text> {
    public void reduce(Text key, Iterable <Text> values,
                       Context context
                       ) throws IOException, InterruptedException     {

        int sum=0;
        String max_roll = "";
        int max_marks = 0;
        String tied_marks = "";
        for (Text rec : values)
        {
            //Retrieving all the calculated values from the Mapper
            String [] variables = rec.toString().split("-");
            String roll = variables[0];
            String mark = variables[1];
            if (Integer.parseInt(mark) == max_marks)
            {
                tied_marks = tied_marks + " and " + String.format("%s %s",roll,mark); //To handle tied scores
            }
            else if (Integer.parseInt(mark) > max_marks)
            {
                max_roll = roll;
                max_marks = Integer.parseInt(mark);
                tied_marks = String.format("%s %s",roll,mark); //To handle tied scored
            }

        }
        String output_str = String.format("%s",tied_marks);
        context.write(key, new Text(output_str));
    }
}
//Partitioner class
	
public static class PartitionIt extends Partitioner < Object, Text >
   {
      @Override
      public int getPartition(Object key, Text value, int numReduceTasks)
      {
         String[] str = key.toString().split(":");
         String div = str[0];
         
         if(numReduceTasks == 0)
         {
            return 0;
         }
         
         if(div.equals("Deanery"))
         {
            return 0;
         }
         else if(div.equals("Department"))
         {
            return 1;
         }
         else
         {
            return 2;
         }
      }
   }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Practice");
    job.setJarByClass(Topper.class);
    job.setMapperClass(MapIt.class);
    job.setPartitionerClass(PartitionIt.class);
    job.setReducerClass(ReduceIt.class);
    job.setNumReduceTasks(3);
    job.setInputFormatClass(TextInputFormat.class);
	job.setOutputFormatClass(TextOutputFormat.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
