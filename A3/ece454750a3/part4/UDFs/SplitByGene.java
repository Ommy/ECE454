package UDFs;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.*;
import org.apache.pig.impl.logicalLayer.schema.Schema;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class SplitByGene extends EvalFunc<DataBag> {

    @Override
    public DataBag exec(Tuple tuple) throws IOException {
        if (tuple == null || tuple.size() == 0) {
            return null;
        }

        List<String> expressions = Arrays.asList(tuple.get(0).toString().replace("(", "").replace(")", "").split(","));

        TupleFactory tf = TupleFactory.getInstance();

        BagFactory bf = BagFactory.getInstance();
        DataBag bag = bf.newDefaultBag();

        for (Integer i = 0; i < expressions.size(); i++) {
            Tuple t = tf.newTuple();
            t.append("gene_" + (i+1));
            t.append(expressions.get(i));

            bag.add(t);
        }

        return bag;
    }

    @Override
    public Schema outputSchema(Schema input) {
        try{
            Schema.FieldSchema geneFs = new Schema.FieldSchema("gene", DataType.CHARARRAY);

            Schema.FieldSchema exprFs = new Schema.FieldSchema("expr", DataType.CHARARRAY);

            Schema tupleSchema = new Schema();
            tupleSchema.add(geneFs);
            tupleSchema.add(exprFs);

            Schema outerTupleSchema = new Schema(tupleSchema);

            Schema.FieldSchema otFs = new Schema.FieldSchema("samples", outerTupleSchema, DataType.BAG);

            return new Schema(otFs);
        }catch (Exception e){
            return null;
        }
    }
}
