import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class OddEven {

  public static class mapr 
    extends Mapper<LongWritable, Text, Text, IntWritable> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String data[]=value.toString().split(",");
	for(String num:data)
	{
		int number =Integer.parseInt(num);
		if((number % 2) ==1)
		{
			context.write(new Text("odd"),new IntWritable(number));
		}
		else
		{
			context.write(new Text("even"),new IntWritable(number));
		}
             }
        }

    }

public static class redr 
     extends Reducer<Text, IntWritable, Text, IntWritable> {
    private IntWritable value = new IntWritable(0);
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
	if(key.equals("odd"))
	{
	        for (IntWritable value : values)
            		{
			sum += value.get();
		}
	}
	else
	{
		for (IntWritable value : values)
            		{
			sum += value.get();
		}
	}

              context.write(key, new IntWritable(sum));
    }
  }
  
public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        @SuppressWarnings("deprecation")
		Job job = new Job(conf, "OddEven");
        job.setJarByClass(OddEven.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(mapr.class);
        job.setReducerClass(redr.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setNumReduceTasks(1);

        FileInputFormat .setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean success = job.waitForCompletion(true);
        System.out.println(success);
    }

}
