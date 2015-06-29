ncores=1

if [ $# -eq 1 ]
then
    ncores=$1
fi

# Input files
graphs=($(ls graph*.txt | sed 's/.txt//'))

for i in "${graphs[@]}"
do
    echo "Running with graph file $i with $ncores cores"
    ant -Dgraph=$i -Dncores=$ncores a2
done
