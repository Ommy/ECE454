package UDFs;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

import java.io.IOException;
import java.math.BigDecimal;

public class DotProduct extends EvalFunc<BigDecimal> {
    @Override
    public BigDecimal exec(Tuple tuple) throws IOException {
        if (tuple == null || tuple.size() == 0) {
            return null;
        }

        String[] array = tuple.get(0).toString().split("\\),\\(");
        String[] left = array[0].replaceAll("\\(","").split(",");
        String[] right = array[1].replaceAll("\\)","").split(",");

        System.out.println(array[1]);

        BigDecimal sum = BigDecimal.ZERO;

        for (int i = 0; i < left.length; i++) {
            BigDecimal leftNumber = new BigDecimal(left[i]);
            BigDecimal rightNumber = new BigDecimal(right[i]);
            System.out.println("LEFT: " + leftNumber + " Right: " + rightNumber);
            if (leftNumber.compareTo(BigDecimal.ZERO) != 0 && rightNumber.compareTo(BigDecimal.ZERO) != 0) {
                sum = sum.add(leftNumber.multiply(rightNumber));
            }
        }

        return sum;
    }
}
