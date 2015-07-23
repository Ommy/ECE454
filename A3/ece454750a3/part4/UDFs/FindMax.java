package UDFs;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.util.StringUtils;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindMax extends EvalFunc<String>{

    private String toGeneString(int i) {
        return "gene_" + (i+1);
    }

    @Override
    public String exec(Tuple tuple) throws IOException {
        if (tuple == null || tuple.size() == 0) {
            return null;
        }
        List<String> allTuples = Arrays.asList(tuple.get(0).toString().replace("(", "").replace(")","").split(","));
        AbstractCollection<String> solutions = new ArrayList<>();

        Double max = 0.0;
        for (int i = 0; i < allTuples.size(); i++) {
            Double currentValue = Double.parseDouble(allTuples.get(i));
            if (Double.compare(currentValue, max) > 0) {
                max = currentValue;
            }
        }

        for (int i = 0; i < allTuples.size(); i++) {
            if (Double.compare(max, Double.parseDouble(allTuples.get(i))) == 0) {
                solutions.add(toGeneString(i));
            }
        }

        return StringUtils.join(solutions, ",");
    }
}
