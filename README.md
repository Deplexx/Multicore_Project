EE 361 C Project

Parallel Text Analysis

Gathers word frequency from file or twitter and outputs as word cloud.


Current supports the follwing Hash Algorithms:

1. Java's Concurrent Hash Map - Java's implementation of Concurrent Hash Map Algorithm
2. Fine Hash Map - Fine hash map with fine get, coarse insert/resize. Uses base Java HashMap to implement.
3. Quad Hash Map - Fine Hash map with fine get, coarse insert/resize with Quadratic probing.
4. Lock Free Chain Hash Map - Chain Hash map implemented with Lock-Free hashing.
5. Cuckoo Hash Map - Hash Map implemented using the Cuckoo algorithm.
6. Hopscotch Hash Map - Hash Map implemented using the Hopscotch algorithm.


As per the project requirements, we use JFreeChart to plot the time it takes for
these algorithms to complete. We also have the option for user to output the 
word frequencies as a word cloud using Kumo java API.

All of these input/output are currently taken in top  of the driver class file.
