package Part1;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class GeneReducer extends Reducer<Object, DoubleWritable, Text, DoubleWritable> {

    private DoubleWritable result = new DoubleWritable();

    public void reduce(Text key, Iterable<DoubleWritable> values, Reducer.Context context) throws IOException, InterruptedException {

        double max = 0.0;

        for (DoubleWritable value : values) {
            if (Double.compare(value.get(), max) > 0) {
                max = value.get();
            }
        }

        result.set(max);
        context.write(key, result);
    }
}
