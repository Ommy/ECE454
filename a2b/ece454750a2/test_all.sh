./get_graphs.sh
ncores=1
bold=$(tput bold)
normal=$(tput sgr0)
RED='\033[0;31m'
GREEN='\033[0;32m'

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
    sort -o $i.ours $i.out
    sort -o $i.sort $i.sort
    if ! diff -q $i.sort $i.ours > /dev/null ; then
        echo "${bold}${RED}$i output is not correct${normal}"
    else
        echo "${bold}${GREEN}$i output matches theirs!${normal}"
    fi
done
