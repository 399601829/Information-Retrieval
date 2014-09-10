PS. The code is implemented completely in JAVA. The commands given below will work flawlessly on a linux terminal. I have used Ubuntu operating system for the same.

1) Goto folder /pr1/Indexer/src
2) Put the "corpus" folder and "stoplist.txt" inside /pr1/Indexer/src


Part 1:

1) Make sure on the command prompt you are in /pr1/Indexer/src directory
2) Run the following command to compile the program ::  javac -cp \* Indexer.java Tokenizer.java
3) Run the following command to run the program :: java -cp .:\* Indexer
4) The below are the files generated in the process ::
	docids.txt
	termids.txt
	doc_index.txt

Extra Credit -- Constant Memory :: 

Requirement : Your program's memory usage is constant with respect to the number of documents, terms, and term positions indexed
	In my program it means that the following three hashmaps should not grow as per the number of documents, terms, and term positions indexed
	a) LinkedHashMap<Long, String> docids = new LinkedHashMap<>();  // For documents
	b) LinkedHashMap<String, Long> termids_afterstemming = new LinkedHashMap<>();  // for tearmids
	c) LinkedHashMap<Long, HashMap> finaldocumentandPositionSet = new LinkedHashMap<>();   //  term positions 

Solution implemented :: I have put a cap of N (defined below) on each hashmap such that after a count of N is reached I write the contents of the hasmap to
the file. And this goes on for each set of N records. So even if the number of documents increase, my program memory works in the constant memory
of N records.

For docids    :: N =1000 (Docids will work in a constant memory of 1000 records) In my program :: Tokenizer.constantdocmemory
For termids :: N = 50,000 (Termids will work in a constant memory of 50,000 records) In my program :: Tokenizer.constanttermmemory
For termpositions :: N = 1000  (Termpositions will work in a constant memory of 1000 records) In my program ::Tokenizer.constanttermpositionmemory

In my program you can vary the constants that I have set to any value you wish to set and then your program will run in that constant memory specified by you.

Part 2: 

1) Make sure on the command prompt you are in /pr1/Indexer/src directory and the files docids.txt, termids.txt and doc_index.txt are in the directory
2) Run the following command to compile the program ::  javac -cp \* InvertedIndexCreator.java
3) Run the following command to run the program :: java -cp .:\* InvertedIndexCreator
4) The below are the files generated in the process ::
	term_info.txt
	term_index.txt

Extra Credit 1 :: Your program's memory usage is constant with respect to the number of documents and term positions indexed.
Requirement : Your program's memory usage is constant with respect to the number of documents, terms, and term positions indexed
	In my program it means that the following three hashmaps should not grow as per the number of documents, terms, and term positions indexed
	a) LinkedHashMap<Long, String> docids = new LinkedHashMap<>();  // For documents
	b) LinkedHashMap<String, Long> termids_afterstemming = new LinkedHashMap<>();  // for tearmids
	c) LinkedHashMap<Long, HashMap> finaldocumentandPositionSet = new LinkedHashMap<>();   //  term positions 

Solution implemented :: I have put a cap of N (defined below) on each hashmap such that after a count of N is reached I write the contents of the hasmap to
the file. And this goes on for each set of N records. So even if the number of documents increase, my program memory works in the constant memory
of N records.

For docids    :: N =1000 (Docids will work in a constant memory of 1000 records) In my program :: Tokenizer.constantdocmemory
For termids :: N = 50,000 (Termids will work in a constant memory of 50,000 records) In my program :: Tokenizer.constanttermmemory
For termpositions :: N = 1000  (Termpositions will work in a constant memory of 1000 records) In my program ::Tokenizer.constanttermpositionmemory

Extra Credit 2 :: Your program's memory usage is constant with respect to the number of terms indexed, and its runtime is linear with respect to the length of the input file.
	In my program it means that the following one hashmap should not grow as per the number of of terms indexed :: 
		LinkedHashMap<String, LinkedHashMap<String, List<String>>> invertedIndex= new LinkedHashMap<>();

Solution implemented :: I have put a cap of N (defined below) on that hashmap such that after a count of N is reached I write the contents of the hasmap to
the file. And this goes on for each set of N records. So even if the number of documents increase, my program memory works in the constant memory
of N records.

For terms indexed    :: N =50,000 (terms indexed will work in a constant memory of 50000 records) In my program :: constanttermsindexedmemory

In my program you can vary the constants that I have set to any value you wish to set and then your program will run in that constant memory specified by you.


Constant Time Explaination :: 

I have the following things into consideration ::
	1) Scan the doc_index.txt file in linear time O (n).
	2) The Inverted index get and put operations take O(1) time.

Hence,  The total time taken in the above case would be O (n) --> Linear Runtime

Part 3:

1) Make sure on the command prompt you are in /pr1/Indexer/src directory and make sure all the above files docids.txt, 
termids.txt, doc_index.txt, term_info.txt and term_index.txt are in the directory
2) Run the following command to compile the program ::  javac -cp \* IndexReader.java
3) Run the following set of commands to check the output::
	a) java -cp .:\* IndexReader --doc clueweb12-0206wb-76-27846 
	b) java -cp .:\* IndexReader --doc clueweb12-1300wb-03-29019
	c) java -cp .:\* IndexReader --term chocolate
	d) java -cp .:\* IndexReader --term pattaya
	e) java -cp .:\* IndexReader --term dark --doc clueweb12-0000tw-13-04988
	f) java -cp .:\* IndexReader --term chocolate --doc clueweb12-0000tw-13-04988

 

