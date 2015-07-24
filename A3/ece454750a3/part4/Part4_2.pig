sampleData = load '$input' using PigStorage(',');
REGISTER UDFs.jar;

samples = FOREACH sampleData GENERATE $0 AS snum:CHARARRAY, ($1 ..) AS expressions:TUPLE();

expressions = FOREACH samples GENERATE FLATTEN(UDFs.SplitByGene(expressions)) AS (gene:CHARARRAY, expr:CHARARRAY);

genes = FOREACH (GROUP expressions BY gene) {
	only_expressions = FOREACH expressions GENERATE FLATTEN(expr) AS (expr);
	only_related = FILTER only_expressions BY ((DOUBLE)expr > 0.5);

	GENERATE group AS gene, ((DOUBLE)COUNT(only_related) / (DOUBLE)COUNT(only_expressions)) AS score;
};

write = FOREACH genes GENERATE CONCAT(CONCAT(gene, ','), (CHARARRAY)score);

STORE write INTO '$output';
