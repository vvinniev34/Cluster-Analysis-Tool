# Cluster-Analysis-Tool
Parallelized implementation of the k-means clustering algorithm to enable efficient cluster creation, editing, and visualization

# Overview

This is an implementation of the k-means clustering algorithm, allowing users to easily cluster provided data points through the use of nmist datasets or forms along with individual data point uploads. Users will be able to view data points associated with each cluster and manipulate each data point as they like. 

Examples of usage:
- making groups out of similar interests
- grouping similar households by electricity usage by month
- stratifying types of users based upon usage

Just tie the data point features with a value and the Cluster Analysis Tool will be able to cluster the most similar groups together. 

This application is meant to run locally, but it may be hosted on a website in the future. 

# What's Contained

1. /kmeans_clustering_react
    - React code for Cluster Analysis Tool frontend
    - Interact with this code to cluster data points and upload
    - Use normal npm commands to run and interact

2. /kmeans_clustering_springboot
    - Springboot code for Cluster Analysis Tool backend
    - Runs in background while frontend runs locally
    - Use typical java -jar commands or IDE to run locally on your device
    - Currently needs to be ran locally in conjunction with the react frontend, but may be hosted in the future

3. /test_data
    - Test data provided for the user to use
    - Files are formatted in a way program can read when uploaded to the cluster anyalysis tool
    - Each line represents a data point with each column representing a feature it contains

# How It Works

A parallelized implementation of the k-means unsupervised ML algorithm is used to cluster data points. 

**Standard K-Means Clustering Approach**

Prior to entering the main algorithm, the following occurs first:

1. User provides data points to the Cluster Analysis Tool to cluster and provides as input, the number of groups/clusters they want to make

2. The Spring Boot backend receives the data points and saves them into a PostgreSQl database

3. Each data point is randomly assigned to a cluster, with attempts to keep clusters relatively balanced at this point

At this point, the algorithm is ready to execute the main algorithm in which the following steps are repeated until no changes are made to the clusters:

1. Current cluster centers are calculated based upon which data points are assigned to them at this step

2. Each data point's distance is compared to all calculated cluster centers using Euclidean distance formulas.
    - If a different cluster center's distance is closer to the data point than the currently assigned cluster, we reassign the data point to that cluster
    - If a change in assingment is made, we mark the change to check if the steps need to be repeated

3. If no changes are made, the algorithm has converged, and the loop is completed; However, if a change is made, we repeat these steps

Once the algorithm converges and exits the loop, each data point is assigned to a cluster with the number of clusters matching the amount provided by the user. Clusters are organized by data point distance based upon their features. 

**Necessary Concerns For Parallelization**

The main concerns made when implementing parallization into a program were data dependencies, memory contention, load balances, efficiency vs parallelism, and most importantly, correctness and synchronization. 

1. Data Dependencies
    - Identifying and managing data dependencies is crucial. Some tasks may depend on the results of others, and proper synchronization and data handling are required to maintain correctness.
    - Deadlocks can occur when multiple threads or processes are waiting for each other to release resources. Identifying and preventing deadlocks is crucial to the stability of a parallel program.

2. Memory Contention
    - Causes performance issues from competition for access to the same memory resources from multiple threads or processes along with possible unpredictable behavior or crashes

3. Load Balances
    - Uneven distribution of work among threads or processes can result in cores being underutilized while others are overloaded

4. Efficiency vs Parallelism (Amdahl's Law)
    - Important to strike a balance between parallelism and minimizing overhead to mantain efficiency.
    - Parallelization is limited by the fraction of the code that cannot be parallelized.

5. Correctness and Synchronization
    - Ensuring that the parallelized program produces correct results is a primary concern. Concurrent execution can lead to race conditions, data inconsistencies, and other synchronization issues.
    - Proper synchronization mechanisms such as locks and barriers must be used to control access to shared resources and coordinate threads or processes.

**Parallelization Steps**

1. Initialization
    - The method starts by initializing cluster centroids and data assignments.
    - Parallelism is skipped at this step due to computational overhead costs. Using typical computation method is faster in majority of cases unless an uncountable amount of data points is entered in as input. 

3. Parallel Resetting of Cluster Features (ResetFeatures):
    - This step resets cluster features to zero.
    - Multiple threads (ResetFeatures) are created to perform this task in parallel. Each thread is responsible for resetting a portion of the cluster features.

4. Parallel Summation of Data Point Features (SumFeatures):
    - In this step, data point features are summed to calculate new cluster centroids.
    - Multiple threads (SumFeatures) work in parallel, each handling a subset of the data points.
    - Using locks (ReentrantLock) ensures that concurrent access to shared cluster feature data is synchronized to prevent data corruption.

5. Parallel Division of Cluster Features (DivideFeatures):
    - The cluster features are divided by the count of assigned data points.
    - Multiple threads (DivideFeatures) perform this operation in parallel. Each thread processes a portion of the cluster features.

6. Parallel Assignment of Data Points (AssignDataPoints):
    - In this step, data points are reassigned to the nearest cluster centroid.
    - Multiple threads (AssignDataPoints) are responsible for processing different subsets of data points.
    - Each thread updates cluster counts (clustCounts) and tracks local changes (localNChanges) in parallel.
    - Synchronization ensures that cluster counts are updated safely.

7. Convergence Check and Iteration:
    - After parallel assignment, a convergence check is performed to determine if further iterations are required.
    - If any data points were reassigned (localNChanges > 0), the loop continues to the next iteration. Otherwise, it exits.

8. Saving Results:
    - The method saves the updated cluster information, but this part is not inherently parallel as it deals with database interactions and data storage.
