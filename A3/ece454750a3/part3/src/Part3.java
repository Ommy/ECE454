import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.math.BigDecimal;

public class Part3 {

    enum GeneEnum {NumberOfSamplesCounter}

    private static final String TEMP_OUTPUT_PATH = "temp_path";
    private static final String NUMBER_OF_SAMPLES_COUNTER = "number_of_samples_counter";

    public static class GeneDotProductReducer extends Reducer<Text, Text, NullWritable, Text> {

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String[] left = values.iterator().next().toString().split(",");
            String[] right = values.iterator().next().toString().split(",");

            BigDecimal sum = BigDecimal.ZERO;
            for (Integer expression = 0; expression < left.length; expression++) {
                left[expression] = left[expression].trim();
                right[expression] = right[expression].trim();

                if (left[expression].equals("0.0") || right[expression].equals("0.0")) {
                    continue;
                }

                try {
                    BigDecimal a = new BigDecimal(left[expression]);
                    BigDecimal b = new BigDecimal(right[expression]);
                    sum = sum.add(a.multiply(b));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            if (sum.compareTo(BigDecimal.ZERO) != 0) {
                context.write(NullWritable.get(), new Text(key.toString() + "," + sum.toPlainString()));
            }
        }
    }

    public static class GeneDotProductMapper extends Mapper<Object, Text, Text, Text> {

        private final Text mKey = new Text();
        private final Text mValue = new Text();
        private Long numberOfSamples;

        @Override
        public void map(Object inKey, Text value, Context context) throws IOException, InterruptedException {
            numberOfSamples = Long.parseLong(context.getConfiguration().get(NUMBER_OF_SAMPLES_COUNTER));
            String[] input = value.toString().split(",", 2);

            Integer currentSample = Integer.parseInt(input[0].replace("sample_", ""));
            mValue.set(input[1]);

            for (int sample = 1; sample <= numberOfSamples; sample++) {
                if (currentSample < sample) {
                    mKey.set("sample_" + currentSample + "," + "sample_" + sample);
                } else if (currentSample > sample) {
                    mKey.set("sample_" + sample + "," + "sample_" + currentSample);
                }

                context.write(mKey, mValue);
            }
        }
    }

    public static class GeneMapper extends Mapper<Object, Text, NullWritable, Text> {

        @Override
        public void map(Object key, Text text, Context context) throws IOException, InterruptedException {
            context.write(NullWritable.get(), text);
            context.getCounter(GeneEnum.NumberOfSamplesCounter).increment(1L);
        }

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job1 = Job.getInstance(conf, "Job 1");

        job1.setJarByClass(Part3.class);
        job1.setMapperClass(Part3.GeneMapper.class);

        job1.setMapOutputKeyClass(NullWritable.class);
        job1.setMapOutputValueClass(Text.class);

        job1.setInputFormatClass(TextInputFormat.class);
        job1.setOutputFormatClass(TextOutputFormat.class);

        TextInputFormat.addInputPath(job1, new Path(args[0]));
        TextOutputFormat.setOutputPath(job1, new Path(TEMP_OUTPUT_PATH));

        job1.waitForCompletion(true);

        Long counter = job1.getCounters().findCounter(GeneEnum.NumberOfSamplesCounter).getValue();
        conf.set(NUMBER_OF_SAMPLES_COUNTER, counter.toString());

        Job job2 = Job.getInstance(conf,"Job 2");
        System.out.println("Counter in Main: " + counter);

        job2.setJarByClass(Part3.class);
        job2.setMapperClass(Part3.GeneDotProductMapper.class);
        job2.setReducerClass(Part3.GeneDotProductReducer.class);

        job2.setMapOutputKeyClass(Text.class);
        job2.setMapOutputValueClass(Text.class);

        job2.setOutputKeyClass(NullWritable.class);
        job2.setOutputValueClass(Text.class);

        job2.setInputFormatClass(TextInputFormat.class);
        job2.setOutputFormatClass(TextOutputFormat.class);

        TextInputFormat.addInputPath(job2, new Path(TEMP_OUTPUT_PATH));
        TextOutputFormat.setOutputPath(job2, new Path(args[1]));

        job2.waitForCompletion(true);

        FileSystem fs = FileSystem.get(conf);
        fs.delete(new Path(TEMP_OUTPUT_PATH), true);
    }
}
