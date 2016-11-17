# Parallel Text Analysis

Gathers word frequency from file or twitter and outputs as word cloud. Completed as term project for Multicore Computing for Professor Vijay Garg. 

By:
- Jayesh Joshi
- Vijay Manohar
- Nico Cortes

## Features
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

## Dependencies
The dependencies are laid out in the pom.xml for simple maven usage. Requires the following API:

1. Kumo - { https://github.com/kennycason/kumo }
  Used to print a word cloud from the word frequencies gathered.
2. Twitter4J { http://twitter4j.org/en/index.html }
  Used to gather tweets from Twitter. To use this function within the source code, you must follow the directions in { https://apps.twitter.com/} to create a new App and get the required Consumer/Token keys. This must be put into the TwitterAccess.java.
3. JFreeChart { http://www.jfree.org/jfreechart/ } 
  Used to display a line chart of total runtime for each algorithm.
  
