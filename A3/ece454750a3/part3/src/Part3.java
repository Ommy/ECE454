import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class Part3 {

    enum GeneEnum { SampleCounter }
    private static final String TEMP_OUTPUT_PATH = "temp_path";

    public static class GeneDotProductReducer extends Reducer<Text, Text, Text, DoubleWritable> {

        private final DoubleWritable mValue = new DoubleWritable();

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String[] left = values.iterator().next().toString().split(",");
            String[] right = values.iterator().next().toString().split(",");

            Double sum = 0.0;
            for (Integer i = 0; i < left.length; i++) {
                sum += (Double.parseDouble(left[i]) * Double.parseDouble(right[i]));
            }

            if (Double.compare(sum, 0.0) > 0) {
                mValue.set(sum);
                context.write(key, mValue);
            }
        }
    }

    public static class GeneDotProductMapper extends Mapper<Object, Text, Text, Text> {

        private final Text mKey = new Text();
        private final Text mValue = new Text();
        private long mapperCounter;

        @Override
        public void map(Object inKey, Text value, Context context) throws IOException, InterruptedException {
            mapperCounter = Long.parseLong(context.getConfiguration().get("counter"));
            String[] input = value.toString().split(",", 2);
            int key = 0;
            for (int i = 1; i <= mapperCounter; i++) {
                key = Integer.parseInt(input[0]);
                if (key < i) {
                    mKey.set("sample_" + key + "," + "sample_" + i);
                    mValue.set(input[1]);
                    context.write(mKey, mValue);
                } else if (key > i) {
                    mKey.set("sample_" + i + "," + "sample_" + key);
                    mValue.set(input[1]);
                    context.write(mKey, mValue);
                }
            }
        }
    }

    public static class GeneMapper extends Mapper<Object, Text, Text, Text> {

        private final Text mKey = new Text();
        private final Text mValue = new Text();

        @Override
        public void map(Object key, Text text, Context context) throws IOException, InterruptedException {
            String[] values = text.toString().split(",", 2);

            mKey.set(values[0].replace("sample_", "") + ",");
            mValue.set(values[1]);

            context.write(mKey, mValue);
            context.getCounter(GeneEnum.SampleCounter).increment(1L);
        }

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job1 = Job.getInstance(conf, "Job 1");

        job1.setJarByClass(Part3.class);
        job1.setMapperClass(Part3.GeneMapper.class);

        job1.setMapOutputKeyClass(Text.class);
        job1.setMapOutputValueClass(Text.class);

        job1.setInputFormatClass(TextInputFormat.class);
        job1.setOutputFormatClass(TextOutputFormat.class);

        TextInputFormat.addInputPath(job1, new Path(args[0]));
        TextOutputFormat.setOutputPath(job1, new Path(TEMP_OUTPUT_PATH));
        job1.waitForCompletion(true);


        String counter = job1.getCounters().findCounter(GeneEnum.SampleCounter).getValue() + "";
        conf.set("counter", job1.getCounters().findCounter(GeneEnum.SampleCounter).getValue() + "");
        Job job2 = Job.getInstance(conf,"Job 2");
        System.out.println("Counter in Main: " + counter);

        job2.setJarByClass(Part3.class);
        job2.setMapperClass(Part3.GeneDotProductMapper.class);
        job2.setReducerClass(Part3.GeneDotProductReducer.class);

        job2.setMapOutputKeyClass(Text.class);
        job2.setMapOutputValueClass(Text.class);

        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(DoubleWritable.class);

        job2.setInputFormatClass(TextInputFormat.class);
        job2.setOutputFormatClass(TextOutputFormat.class);

        TextInputFormat.addInputPath(job2, new Path(TEMP_OUTPUT_PATH));
        TextOutputFormat.setOutputPath(job2, new Path(args[1]));
        System.exit(job2.waitForCompletion(true) ? 0 : 1);
    }
}
