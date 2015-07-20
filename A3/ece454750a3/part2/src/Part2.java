import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;

public class Part2 {

    enum GeneEnum { SampleCounter }

    public static class GeneMapper extends Mapper<Object, Text, IntWritable, DoubleWritable> {

        private final static DoubleWritable mValue = new DoubleWritable();
        private IntWritable mKey = new IntWritable();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer input = new StringTokenizer(value.toString(), ",");
            //Skip first token
            input.nextToken();

            int index = 1;
            while (input.hasMoreTokens()) {
                Double expression = Double.parseDouble(input.nextToken());
                mValue.set(expression);
                mKey.set(index);
                context.write(mKey, mValue);
                index++;
            }

            context.getCounter(GeneEnum.SampleCounter).increment(1L);
        }

    }

    public static class GeneReducer extends Reducer<IntWritable, DoubleWritable, Text, DoubleWritable> {

        private long mapperCounter;

        @Override
        public void setup(Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            Cluster cluster = new Cluster(conf);
            Job currentJob = cluster.getJob(context.getJobID());
            mapperCounter = currentJob.getCounters().findCounter(GeneEnum.SampleCounter).getValue();
        }

        private Text toGeneString(int gene) {
            return new Text("gene_" + gene);
        }

        public void reduce(IntWritable key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
            double count = 0.0;

            for (DoubleWritable value: values) {
                if (Double.compare(value.get(), 0.5) >= 0) {
                    count++;
                }
            }

            double Sx = count / (double)mapperCounter;
            context.write(toGeneString(key.get()), new DoubleWritable(Sx));
        }

    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Part2");

        job.setJarByClass(Part2.class);
        job.setMapperClass(Part2.GeneMapper.class);
        job.setReducerClass(Part2.GeneReducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(DoubleWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
