package UDFs;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class DotProduct extends EvalFunc<String> {

    @Override
    public String exec(Tuple tuple) throws IOException {
        Tuple list = (Tuple) tuple.iterator().next();
        List<Object> left = ((Tuple) list.get(0)).getAll();
        List<Object> right = ((Tuple) list.get(1)).getAll();

        Double sum = 0.0;

        for (int i = 0; i < left.size(); i++) {
            Double leftNumber = Double.parseDouble(left.get(i).toString());
            Double rightNumber = Double.parseDouble(right.get(i).toString());

            if (leftNumber.compareTo(0.0) != 0 && rightNumber.compareTo(0.0) != 0) {
                sum += leftNumber * rightNumber;
            }
        }

        return sum.toString();
    }
}
