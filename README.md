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

# What's In The Repo

**/kmeans_clustering_react**
- react code for cluster anlaysis tool frontend
- interact with this code to cluster data points and upload
- use normal npm commands to run and interact

**/kmeans_clustering_springboot**
- springboot code for cluster analysis tool backend
- runs in background while frontend runs locally
- use typical java -jar commands or IDE to run locally on your device
- currently needs to be ran locally in conjunction with the react frontend, but may be hosted in the future

**/test_data**
- test data provided for the user to use
- files are formatted in a way program can read when uploaded to the cluster anyalysis tool
- each line represents a data point with each column representing a feature it contains
