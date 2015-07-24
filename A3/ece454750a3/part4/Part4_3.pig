sampleData = load '$input' using PigStorage(',');
REGISTER UDFs.jar;

samplesToExpressions = foreach sampleData generate $0 as key:chararray, ($1 ..) as expressions:tuple();

samplesToExpressionsCopy = foreach sampleData generate $0 as key:chararray, ($1 ..) as expressions:tuple();

crossProduct = CROSS samplesToExpressions, samplesToExpressionsCopy;

filteredCrossProduct = filter crossProduct by $0 < $2;

mappedCrossProduct = foreach filteredCrossProduct generate CONCAT(CONCAT($0, ','), $2) as key, TOTUPLE($1, $3) as value;

sampleDotProduct = foreach mappedCrossProduct generate key, UDFs.DotProduct(value) as value;

store sampleDotProduct into '$output';