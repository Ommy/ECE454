sampleData = load '$input' using PigStorage(',');
REGISTER UDFs.jar;

samplesToExpressions = foreach sampleData generate $0 as key:chararray, ($1 ..) as expressions:tuple();

expressions = foreach samplesToExpressions generate flatten(TOBAG(UDFs.SplitByGene(expressions))) as value;

genes = foreach expressions generate TOBAG(value) as o;

x = foreach genes generate flatten(genes) as key;

dump x;
describe x;