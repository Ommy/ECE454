package Part1;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

class GeneMapper extends Mapper<Object, Text, Text, DoubleWritable> {

    private final static DoubleWritable writable = new DoubleWritable();
    private Text text = new Text();

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer tokenizer = new StringTokenizer(value.toString());
        while (tokenizer.hasMoreTokens()) {
            text.set(tokenizer.nextToken());
            context.write(text, writable);
        }
    }

}