import java.io.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;

import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;

import org.apache.hadoop.util.*;

public class CountryPartition extends Configured implements Tool
{
   //Map class
	
   public static class MapClass extends Mapper<LongWritable,Text,Text,Text>
   {
      public void map(LongWritable key, Text value, Context context)
      {
         try{
            String[] str = value.toString().split(",", -1);
            String country=str[1];
            context.write(new Text(country), new Text(value));
         }
         catch(Exception e)
         {
            System.out.println(e.getMessage());
         }
      }
   }
   
   //Reducer class
	
   public static class ReduceClass extends Reducer<Text,Text,Text,FloatWritable>
   {
      public float max = -1;
      public void reduce(Text key, Iterable <Text> values, Context context) throws IOException, InterruptedException
      {
         max = -1;
         String max_key="";
         for (Text val : values)
         {
            String[] str = val.toString().split(",", -1);
            if(Float.parseFloat(str[2])>max)
            max=Float.parseFloat(str[2]);
            max_key= key.toString();
         }
         context.write(new Text(max_key), new FloatWritable(max));
      }
   }
   
   //Partitioner class
	
   public static class RankPartitioner extends
   Partitioner < Text, Text >
   {
      @Override
      public int getPartition(Text key, Text value, int numReduceTasks)
      {
         String[] str = value.toString().split(",");
         int rank = Integer.parseInt(str[0]);
         
         if(numReduceTasks == 0)
         {
            return 0;
         }
         
         if(rank<=20)
         {
            return 0;
         }
         else if(rank>20 && rank<=50)
         {
            return 1 % numReduceTasks;
         }
         else
         {
            return 2 % numReduceTasks;
         }
      }
   }
   
   @Override
   public int run(String[] arg) throws Exception
   {
      Configuration conf = getConf();
		
      Job job = new Job(conf, "topsal");
      job.setJarByClass(CountryPartition.class);
		
      FileInputFormat.setInputPaths(job, new Path(arg[0]));
      FileOutputFormat.setOutputPath(job,new Path(arg[1]));
		
      job.setMapperClass(MapClass.class);
		
      job.setMapOutputKeyClass(Text.class);
      job.setMapOutputValueClass(Text.class);
      
      //set partitioner statement
		
      job.setPartitionerClass(RankPartitioner.class);
      job.setReducerClass(ReduceClass.class);
      job.setNumReduceTasks(3);
      job.setInputFormatClass(TextInputFormat.class);
		
      job.setOutputFormatClass(TextOutputFormat.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(Text.class);
		
      System.exit(job.waitForCompletion(true)? 0 : 1);
      return 0;
   }
   
   public static void main(String ar[]) throws Exception
   {
      int res = ToolRunner.run(new Configuration(), new CountryPartition(),ar);
      System.exit(0);
   }
}
