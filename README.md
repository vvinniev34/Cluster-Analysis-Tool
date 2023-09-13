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

**/kmeans_clustering_react**
- react code for Cluster Analysis Tool frontend
- interact with this code to cluster data points and upload
- use normal npm commands to run and interact

**/kmeans_clustering_springboot**
- springboot code for Cluster Analysis Tool backend
- runs in background while frontend runs locally
- use typical java -jar commands or IDE to run locally on your device
- currently needs to be ran locally in conjunction with the react frontend, but may be hosted in the future

**/test_data**
- test data provided for the user to use
- files are formatted in a way program can read when uploaded to the cluster anyalysis tool
- each line represents a data point with each column representing a feature it contains

# How It Works

A parallelized implementation of the k-means unsupervised ML algorithm is used to cluster data points. 
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
