import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;

public class Part2 {

    enum GeneEnum {SampleNumberCounter}

    public static class GeneReducer extends Reducer<IntWritable, DoubleWritable, NullWritable, Text> {

        private long numberOfSamples = 0;

        @Override
        public void setup(Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            Cluster cluster = new Cluster(conf);
            Job currentJob = cluster.getJob(context.getJobID());
            numberOfSamples = currentJob.getCounters().findCounter(GeneEnum.SampleNumberCounter).getValue();
        }

        public void reduce(IntWritable key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
            long numberOfRelatedSamples = 0;
            for (DoubleWritable expression: values) {
                if (Double.compare(expression.get(), 0.5) > 0) {
                    numberOfRelatedSamples++;
                }
            }

            BigDecimal score = new BigDecimal(numberOfRelatedSamples).divide(new BigDecimal(numberOfSamples), MathContext.UNLIMITED);
            context.write(NullWritable.get(), new Text("gene_" + key.get() + "," + score.toPlainString()));
        }
    }

    public static class GeneMapper extends Mapper<Object, Text, IntWritable, DoubleWritable> {

        private final IntWritable mKey = new IntWritable();
        private final DoubleWritable mValue = new DoubleWritable();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] input = value.toString().split(",");

            for (int sample = 1; sample < input.length; sample++) {
                Double expression = Double.parseDouble(input[sample]);
                mKey.set(sample);
                mValue.set(expression);
                context.write(mKey, mValue);
            }

            context.getCounter(GeneEnum.SampleNumberCounter).increment(1L);
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
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
