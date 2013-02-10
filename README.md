KMeans-in-Hadoop
================

Implementation of the KMeans clustering algorithm in Hadoop.

/****** Running Map_Reduce for Kmeans on a single node cluster. ******/
/**** Authors: Vineet Aguiar


/ ************* Setup Instruction *******************/
To copy values into HDFS location.

>$ hadoop dfs -copyFromLocal <sourcepath> <hdfs destination>
>

/ ************* Instructions to run *******************/

The submission contains a .jar file called "KMeans_MapRed.jar" which takes in three input parameters.

So call the Jar file followed by the following parameters.
1st parameter - hdfs location of the seed file on the current machine.
2nd parameter - hdfs location of the data file.
3rd parameter - hdfs location of the output folder.(This folder shoud not exist. The programs creates a new folder with the specified pathname and stores all the outputs in that folder.)

Program execution example:
hadoop jar <Project path> <seed_filepath> <actual filepath> <output folder>


/************ Output Files *****************/
The output files contain only the centroid values for each of the iteration.
This output from the reducer is used as the stopping condtion for the driver function.
When the newly calculated centroids match the old values for the centroids, then MapReduce Program stops.

The MapReduce runs for the same number of iterations as the normal MepReduce function.


/ ************* HOW TO VERIFY THE OUTPUT with normal Kmeans **************/
We use the Reducer to print the values that belongs to the cluster during the reducinig operation.
Thus the last iteration gives us the value of the Clusters and their mapping of Gene-Ids with the Centroids.

This file is found with the Reducer's job tracker and the file over the last iteration gives us the clusters.

Steps:
1) http://localhost:50030/jobtracker.jsp
2) Click on the last completed job.
3) Click on the reduce label in the status table.
4) Click on the <task id>
5) Click on the log file link.
Our clusters appear as the "stdout logs".




