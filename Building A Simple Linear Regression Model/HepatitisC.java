import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/*******************************************************************************
 *  
 *  Takes in a file containing the Variables required for the establisment of
 *  a Simple Linear Regression Model Line i.e Y = mx + c;
 * 
 *  User can define the index of the Dependent and Independent Variable for
 *  which the Model needs to be created and the MapReduce Program will use 
 *  the given variables.
 *
 *  Author : Aayushi Shrivastava
 ******************************************************************************/

public class HepatitisC {

  public static class DataMapper extends Mapper<Object, Text, Text, Text>
{
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException 
    {
            //Declaring Variables Required
            double x; // This is going to be our Independent Variable i.e x in the equation Y = mx+c
            double y; // This is going to be our Dependent Variable i.e Y in the equation Y = mx + c
            double xy;
            double x_sq;
            double y_sq;
            String subject;
            String data[]=value.toString().split(",");
            // Checking the length of the data so that we don't run into an ArrayIndexOutOfBounds
            // Checking the data for NA values, if we get them, we ignore them
            if ((data.length >= 13) && !(data[1].equals("NA")) && !(data[7].equals("NA")))
            {
               x = Double.parseDouble(data[1]);
               y = Double.parseDouble(data[8]);
               // Calculating variables required for our Linear Regression Model
               xy = x*y;
               x_sq = x*x;
               y_sq = y*y;
               subject = data[13];
               // Send all the calculated values to our Reducer
               String recs = String.format("%.2f-%.2f-%.2f-%.2f-%.2f-1",x,y,xy,x_sq,y_sq);
               context.write(new Text("Records:"), new Text(recs));
            }
                
    }
}

  public static class DataReducer extends Reducer<Text,Text,Text,Text> 
  {
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException     
    {
        // Declaring Variables
        double sum_y = 0;
        double sum_x_sq = 0;
        double sum_x= 0;
        double sum_xy = 0;
        int n = 0; // This variable will count our number of observations, which is required for our Regression Formula
        double sq_sum_x = 0;;
        double slope;
        double intercept;
        for (Text rec : values)
        {
            //Retrieving all the calculated values from the Mapper
            String variables[] = rec.toString().split("-");
            sum_y += Double.parseDouble(variables[1]);
            sum_x_sq += Double.parseDouble(variables[3]);
            sum_x += Double.parseDouble(variables[0]);
            sum_xy += Double.parseDouble(variables[2]);
            n += Integer.parseInt(variables[5]);

        }
        //Calculating slope i.e m in the equation Y = mx + c
        slope = ((n*sum_xy)-(sum_x*sum_y))/((n*sum_x_sq) - (sum_x*sum_x));
        //Calculating the intercept i.e c in the equation Y = mx + c
        intercept = ((sum_y*sum_x_sq)-(sum_x*sum_xy))/((n*sum_x_sq)- (sum_x*sum_x));
        double new_chol = 24;
        double new_bilirubin_level = slope*new_chol+ intercept;
        String new_bilirubin_level_str = String.format("%s",new_bilirubin_level);
        //Formatting the Linear Regression Equation
        String formula = String.format("Y = (%.2f)x + %.2f",slope,intercept);
        //Finally writing the Formula out to the user
        context.write(new Text("Simple Linear Regression Formula for Bilirubin = m(Cholesterol) + c:"), new Text(formula));
        context.write(new Text("New Bilirubin Levels for Cholesterol level 11.17:"), new Text(new_bilirubin_level_str));
        
        
    }
}

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "HepatitisC");
    job.setJarByClass(HepatitisC.class);
    job.setMapperClass(DataMapper.class);
    job.setReducerClass(DataReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
