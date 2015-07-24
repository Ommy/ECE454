def main():
    theirs_filename = "genes_score.txt"
    ours_filename = "ours.out"

    ours = []
    theirs = []

    with open(theirs_filename, "r") as theirs_file:
        theirs = [line.strip().split(",") for line in theirs_file]

    with open(ours_filename, "r") as ours_file:
        ours = [line.strip().split(",") for line in ours_file]

    for t, o in zip(sorted(theirs), sorted(ours)):
        if t[1] != o[1]:
            print(t[0] + ", t:" + t[1] + ", o:" + o[1])

main()
