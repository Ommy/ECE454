package Part1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;

public class Driver {

    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Part 1");
        job.setJarByClass(Driver.class);
        job.setMapperClass(GeneMapper.class);


    }
}
