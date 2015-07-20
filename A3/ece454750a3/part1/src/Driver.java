package Part1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.StringUtils;

import java.io.IOException;
import java.util.*;

public class Driver {

    public static class GeneReducer extends Reducer<Text, Text, Text, Text> {

        private Text result = new Text();

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            double max = 0.0;
            List<String> solution = new ArrayList<>();
            Map<Integer, Double> geneMapping = new HashMap<>();
            for (Text value : values) {
                String[] kvp = value.toString().split(",");
                geneMapping.put(Integer.parseInt(kvp[0]), Double.parseDouble(kvp[1]));
            }
            for (Map.Entry<Integer, Double> entry : geneMapping.entrySet()) {
                if (Double.compare(entry.getValue(), max) > 0) {
                    max = entry.getValue();
                }
            }
            for (Map.Entry<Integer, Double> entry : geneMapping.entrySet()) {
                if (Double.compare(entry.getValue(), max) == 0) {
                    solution.add("gene_" + (entry.getKey()));
                }
            }
            result.set(StringUtils.join(",", solution));
            context.write(key, result);
        }
    }

    public static class GeneMapper extends Mapper<Object, Text, Text, Text> {

        private final static Text mValue = new Text();
        private Text mKey = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer tokenizer = new StringTokenizer(value.toString(), ",");
            if (tokenizer.hasMoreTokens()) {
                mKey.set(tokenizer.nextToken());
            }
            int gene = 1;

            while (tokenizer.hasMoreTokens()) {
                mValue.set(gene + "," + tokenizer.nextElement().toString());
                context.write(mKey, mValue);
                gene++;
            }
        }

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Driver");

        job.setJarByClass(Driver.class);

        job.setMapperClass(GeneMapper.class);
        job.setReducerClass(GeneReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}
