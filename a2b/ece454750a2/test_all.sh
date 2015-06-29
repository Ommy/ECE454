ncores=$1

# Input files
graphs=($(ls graph*.txt | sed 's/.txt//'))

for i in "${graphs[@]}"
do
    echo "Running with graph file $i with $1 cores"
    ant -Dgraph=$i -Dncores=$ncores a2
done
