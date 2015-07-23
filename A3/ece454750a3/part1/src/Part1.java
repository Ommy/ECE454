import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.*;

public class Part1 {

    public static class GeneMapper extends Mapper<Object, Text, NullWritable, Text> {

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer tokenizer = new StringTokenizer(value.toString(), ",");

            StringBuilder sb = new StringBuilder();
            if (tokenizer.hasMoreTokens()) {
                sb.append(tokenizer.nextToken().trim());
            }

            List<Double> expressionList = new ArrayList<>();
            while (tokenizer.hasMoreTokens()) {
                expressionList.add(Double.parseDouble(tokenizer.nextToken().trim()));
            }

            double max = Collections.max(expressionList);
            for (int i = 0; i < expressionList.size(); i++) {
                if (Double.compare(expressionList.get(i), max) == 0) {
                    sb.append(",gene_");
                    sb.append(i + 1);
                }
            }

            context.write(NullWritable.get(), new Text(sb.toString()));
        }

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Driver");

        job.setJarByClass(Part1.class);

        job.setMapperClass(GeneMapper.class);

        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}
