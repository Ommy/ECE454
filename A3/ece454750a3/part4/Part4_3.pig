sampleData = load '$input' using PigStorage(',');
REGISTER UDFs.jar;

samplesToExpressions = foreach sampleData generate $0 as key:chararray, ($1 ..) as expressions:tuple();

copy = foreach sampleData generate $0 as key:chararray, ($1 ..) as expressions:tuple();


crossed = CROSS samplesToExpressions, copy;

crossed = filter crossed by $0 < $2;

crossed = foreach crossed generate CONCAT(CONCAT($0, ','), $2) as key, TOTUPLE($1, $3) as value;

udfRes = foreach crossed generate key, UDFs.DotProduct(value) as value;

store udfRes into '$output';