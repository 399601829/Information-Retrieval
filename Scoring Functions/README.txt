Will work best if the below commands are run into a Ubuntu machine with Java 7.

Uncompress the following zip files -> these are files from the project 1
1) docids.txt.zip
2) termids.txt.zip
3) doc_index.txt.zip
4) term_info.txt.zip

For compiling code:
javac -cp \* Query.java OkapiTF.java TF_IDF.java BM25.java LaplaceSmoothing.java Jelinek_Mercer_Smoothing.java Info.java

For Running the code:

java -cp .:\* Query --score TF

java -cp .:\* Query --score TF-IDF

java -cp .:\* Query --score BM25

java -cp .:\* Query --score Laplace

java -cp .:\* Query --score JM