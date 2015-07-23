package UDFs;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.BagFactory;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class SplitByGene extends EvalFunc<Tuple> {

    @Override
    public Tuple exec(Tuple tuple) throws IOException {
        if (tuple == null || tuple.size() == 0) {
            return null;
        }

        List<String> expressions = Arrays.asList(tuple.get(0).toString().replace("(", "").replace(")", "").split(","));

        System.out.println("Sutpid: " + expressions);
        StringBuilder sb = new StringBuilder();

        TupleFactory tf = TupleFactory.getInstance();
        Tuple ot = tf.newTuple();

        for (Integer i = 0; i < expressions.size(); i++) {
            Tuple t = tf.newTuple();
            t.append("gene_" + (i+1));
            t.append(expressions.get(i));

            ot.append(t);
            if (i != 0) {
                sb.append(",");
            }
            sb.append("(");
            sb.append("gene_");
            sb.append(i+1);
            sb.append("=");
            sb.append(expressions.get(i));
            sb.append(")");
        }

        return ot;
    }
}