sampleData = load '$input' using PigStorage(',');
REGISTER UDFs.jar;

geneExpressions = foreach sampleData GENERATE $0 as key:chararray, ($1 ..) as expressions:tuple();

maxResults = FOREACH geneExpressions generate key, UDFs.FindMax(expressions) as values;

concatenatedKeys = foreach maxResults generate CONCAT(key, ',') as key, values as values;

results = foreach concatenatedKeys generate CONCAT(key, values);

store results into '$output';