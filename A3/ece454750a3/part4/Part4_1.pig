sampleData = LOAD '$input' USING PigStorage(',');
REGISTER UDFs.jar;

geneExpressions = FOREACH sampleData GENERATE $0 AS key:CHARARRAY, ($1 ..) AS expressions:TUPLE();

maxResults = FOREACH geneExpressions GENERATE key, UDFs.FindMax(expressions) AS values;

concatenatedKeys = FOREACH maxResults GENERATE CONCAT(key, ',') AS key, values AS values;

results = FOREACH concatenatedKeys GENERATE CONCAT(key, values);

STORE results INTO '$output';
