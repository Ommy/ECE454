sampleData = LOAD '$input' USING PigStorage(',');
REGISTER UDFs.jar;

samplesToExpressions = FOREACH sampleData GENERATE $0 AS key:CHARARRAY, ($1 ..) AS expressions:TUPLE();

samplesToExpressionsCopy = FOREACH sampleData GENERATE $0 AS key:CHARARRAY, ($1 ..) AS expressions:TUPLE();

crossProduct = CROSS samplesToExpressions, samplesToExpressionsCopy;

filteredCrossProduct = FILTER crossProduct BY $0 < $2;

mappedCrossProduct = FOREACH filteredCrossProduct GENERATE CONCAT(CONCAT($0, ','), $2) AS key, TOTUPLE($1, $3) AS value;

sampleDotProduct = FOREACH mappedCrossProduct GENERATE key, UDFs.DotProduct(value) AS value;

filteredDotProduct = FILTER sampleDotProduct BY value != '';

finalOutput = FOREACH filteredDotProduct GENERATE CONCAT(CONCAT(key, ','), value);

STORE finalOutput INTO '$output';
