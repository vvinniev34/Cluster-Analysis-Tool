package com.kmeans_clustering.kmeans_clustering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class KMeansService {
    @Autowired
    private DataPointRepository dataPointRepository;
    @Autowired
    private KMClustRepository kmclustRepository;
    @Autowired
    private KMDataRepository kmdataRepository;

    // returns counts array of the corresponding clusters
    public int[] getClusterCountsByLabel(int label){
        KMClust clust =  kmclustRepository.findByLabel(label);
        return clust != null ? clust.getCounts() : null;
    }

    // returns KMData which contains list of DataPoints and assigns array which shows their individual assignments
    public KMData getKMDataByLabel(int label){
        return kmdataRepository.findByLabel(label);
    }

    // when file is uploaded, KMData object is created, along with all its DataPoints
    public void addKMData(String text, int label){
        KMData data = new KMData(label);
        String[] values = text.split("[\\s]+");
        int tot_lines = text.split("\r\n|\r|\n").length;
        int tot_tokens = (values.length / tot_lines);

        data.setNData(tot_lines);
        data.setDim(tot_tokens);
        kmdataRepository.save(data);

        for (int i = 0; i < tot_lines; i++){
            for (int j = 0; j < tot_tokens; j++){
                DataPoint dataPoint = new DataPoint(i, j,  Integer.parseInt(values[i * tot_tokens + j]), label, data);
                dataPointRepository.save(dataPoint);
            }
        }
        System.out.println("ndata: " + tot_lines + ", dim: " + tot_tokens);
    }
    class ResetFeatures extends Thread{
        int clustNClust;
        int clustDim;
        int clustDimStart;
        int clustDimEnd;
        float[] clustFeatures;
        public ResetFeatures(int clustNClust, int clustDim, int clustDimStart, int clustDimEnd,
                             float[] clustFeatures){
            this.clustNClust = clustNClust;
            this.clustDim = clustDim;
            this.clustDimStart = clustDimStart;
            this.clustDimEnd = clustDimEnd;
            this.clustFeatures = clustFeatures;
        }
        public void run() {
            for (int c = 0; c < clustNClust; c++) {
                for (int d = clustDimStart; d < clustDimEnd/*clustDim*/; d++) {
                    clustFeatures[c * clustDim + d] = 0.0f;
                }
            }
        }
    }

    class SumFeatures extends Thread{
        int dataStart;
        int dataEnd;
        int clustDim;
        int dataDim;
        int[] dataAssigns;
        float[] clustFeatures;
        DataPoint[] dataFeatures;
        ReentrantLock[] locks;
        public SumFeatures(int dataStart, int dataEnd, int clustDim, int dataDim, int[] dataAssigns,
                           float[] clustFeatures, DataPoint[] dataFeatures, ReentrantLock[] locks){
            this.dataStart = dataStart;
            this.dataEnd = dataEnd;
            this.clustDim = clustDim;
            this.dataDim = dataDim;
            this.dataAssigns = dataAssigns;
            this.clustFeatures = clustFeatures;
            this.dataFeatures = dataFeatures;
            this.locks = locks;
        }

        public void run(){
            for (int i = dataStart; i < dataEnd; i++){
                int c = dataAssigns[i];
                for (int d = 0; d < clustDim; d++){
                    locks[c * clustDim + d].lock();
                    try {
                        clustFeatures[c * clustDim + d] += dataFeatures[i * dataDim + d].getVal();
                    } finally {
                        locks[c * clustDim + d].unlock();
                    }
                }
            }
        }
    }

    class DivideFeatures extends Thread{
        int clustNClust;
        int clustDimStart;
        int clustDimEnd;
        int clustDim;
        int[] clustCounts;
        float[] clustFeatures;
        public DivideFeatures(int clustNClust, int clustDimStart, int clustDimEnd, int clustDim,
                              int[] clustCounts, float[] clustFeatures){
            this.clustNClust = clustNClust;
            this.clustDimStart = clustDimStart;
            this.clustDimEnd = clustDimEnd;
            this.clustDim = clustDim;
            this.clustCounts = clustCounts;
            this.clustFeatures = clustFeatures;
        }

        public void run(){
            for (int c = 0; c < clustNClust; c++){
                if (clustCounts[c] > 0){
                    for (int d = clustDimStart; d < clustDimEnd/*clustDim*/; d++){
                        clustFeatures[c * clustDim + d] /= clustCounts[c];
                    }
                }
            }
        }
    }

    class AssignDataPoints extends Thread {
        int start;
        int end;
        AtomicInteger nchanges;
        int clustNClust;
        int clustDim;
        int dataDim;
        int[] clustCounts;
        int[] dataAssigns;
        float[]  clustFeatures;
        DataPoint[] dataFeatures;
        ReentrantLock[] locks;
        public AssignDataPoints(int start, int end, AtomicInteger nchanges, int clustNClust, int clustDim, int dataDim,
                                int[] clustCounts, int[] dataAssigns, float[] clustFeatures,
                                DataPoint[] dataFeatures, ReentrantLock[] locks){
            this.start = start;
            this.end = end;
            this.nchanges = nchanges;
            this.clustNClust = clustNClust;
            this.clustDim = clustDim;
            this.dataDim = dataDim;
            this.clustCounts = clustCounts;
            this.dataAssigns = dataAssigns;
            this.clustFeatures = clustFeatures;
            this.dataFeatures = dataFeatures;
            this.locks = locks;
        }
        public synchronized void synchronizedAddCounts(int index, int value){
            clustCounts[index] += value;
        }

        public void run(){
            int localNChanges = 0;
            for (int i = start; i < end; i++){
                int best_clust = -1;
                float best_distsq = (float)Integer.MAX_VALUE;
                for (int c = 0; c < clustNClust; c++){
                    float distsq = 0.0f;
                    for (int d = 0; d < clustDim; d++){
                        float diff = dataFeatures[i * dataDim + d].getVal() - clustFeatures[c * clustDim + d];
                        distsq += diff * diff;
                    }
                    if (distsq < best_distsq){
                        best_clust = c;
                        best_distsq = distsq;
                    }
                }
                synchronizedAddCounts(best_clust, 1);
                if (best_clust != dataAssigns[i]){
                    localNChanges += 1;
                    dataAssigns[i] = best_clust;
                }
            }
            nchanges.addAndGet(localNChanges);
        }
    }

    // calculate clusters and creates a new KMClust object to send to database
    // clusters have many to many relationship with KMData as we use label and nclust
    // if key: label,nclust exits, return calculated value, otherwise recalculate
    public int[] kmeansClustering(int nclust, int maxIterations, int label){
        KMData kmdata = kmdataRepository.findByLabel(label);
        int dataNData = kmdata.getNData();
        int dataDim = kmdata.getDim();
        int[] dataAssigns = new int[dataNData];
        List<DataPoint> dataFeaturesList = kmdata.getFeatures();
        DataPoint[] dataFeatures = dataFeaturesList.toArray(new DataPoint[dataFeaturesList.size()]);

        int clustNClust = nclust;
        int clustDim = dataDim;
        int[] clustCounts = new int[nclust];
        float[] clustFeatures = new float[nclust * clustDim];
        ReentrantLock[] locks = new ReentrantLock[nclust * clustDim];
        for (int i = 0; i < nclust * clustDim; i++){
            locks[i] = new ReentrantLock();
        }

        // assign each data point to a random cluster
        for (int i = 0; i < dataNData; i++){
            int c = i % clustNClust;
            dataAssigns[i] = c;
        }

        for (int c = 0; c < clustNClust; c++){
            int icount = dataNData / clustNClust;
            int extra = 0;
            if (c < (dataNData % clustNClust)){
                extra = 1;
            }
            clustCounts[c] = icount + extra;
        }

        int curiter = 1;
        AtomicInteger nchanges = new AtomicInteger(dataNData);
        int NUM_THREADS = 8;
        int DATA_ELEMENTS_PER_THREAD = (dataNData / NUM_THREADS);
        int DIM_ELEMENTS_PER_THREAD = (clustDim / NUM_THREADS);
        while (nchanges.get() > 0 && curiter <= maxIterations){
            ResetFeatures[] resetFeatures = new ResetFeatures[NUM_THREADS];
            for (int i = 0; i < NUM_THREADS; i++){
                int clustDimStart = DIM_ELEMENTS_PER_THREAD * i;
                int clustDimEnd = i != NUM_THREADS - 1 ? clustDimStart + DIM_ELEMENTS_PER_THREAD : clustDim;
                resetFeatures[i] = new ResetFeatures(clustNClust, clustDim, clustDimStart,
                        clustDimEnd, clustFeatures);
                resetFeatures[i].start();
            }
            try {
                for (int i = 0; i < NUM_THREADS; i++) {
                    resetFeatures[i].join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SumFeatures[] sumFeatures = new SumFeatures[NUM_THREADS];
            for (int i = 0; i < NUM_THREADS; i++){
                int dataStart = DATA_ELEMENTS_PER_THREAD * i;
                int dataEnd = i != NUM_THREADS - 1 ? dataStart + DATA_ELEMENTS_PER_THREAD : dataNData;
                sumFeatures[i] = new SumFeatures(dataStart, dataEnd, clustDim, dataDim, dataAssigns,
                        clustFeatures, dataFeatures, locks);
                sumFeatures[i].start();
            }
            try {
                for (int i = 0; i < NUM_THREADS; i++) {
                    sumFeatures[i].join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            DivideFeatures[] divideFeatures = new DivideFeatures[NUM_THREADS];
            for (int i = 0; i < NUM_THREADS; i++){
                int clustDimStart = DIM_ELEMENTS_PER_THREAD * i;
                int clustDimEnd = i != NUM_THREADS - 1 ? clustDimStart + DIM_ELEMENTS_PER_THREAD : clustDim;
                divideFeatures[i] = new DivideFeatures(clustNClust, clustDimStart, clustDimEnd, clustDim,
                        clustCounts, clustFeatures);
                divideFeatures[i].start();
            }
            try {
                for (int i = 0; i < NUM_THREADS; i++) {
                    divideFeatures[i].join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int c = 0; c < clustNClust; c++){
                clustCounts[c] = 0;
            }

            nchanges.set(0);
            AssignDataPoints[] assignDataPoints = new AssignDataPoints[NUM_THREADS];
            for (int i = 0; i < NUM_THREADS; i++){
                int start = DATA_ELEMENTS_PER_THREAD * i;
                int end = i != NUM_THREADS - 1 ? start + DATA_ELEMENTS_PER_THREAD : dataNData;
                assignDataPoints[i] = new AssignDataPoints(start, end, nchanges, clustNClust, clustDim, dataDim,
                        clustCounts, dataAssigns, clustFeatures,  dataFeatures, locks);
                assignDataPoints[i].start();
            }
            try {
                for (int i = 0; i < NUM_THREADS; i++) {
                    assignDataPoints[i].join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("nchanges: " + nchanges.get() + " | ");
            for (int i = 0; i < clustCounts.length; i++){
                System.out.print(clustCounts[i] + " ");
            }
            System.out.println();
            curiter++;
        }
        System.out.println("Converged after " + curiter + " iterations\n");

        kmdata.setAssigns(dataAssigns);

        KMClust clust = kmclustRepository.findByLabelAndNclust(label, nclust);
        if (clust != null){
            clust.setDim(clustDim);
            clust.setFeatures(clustFeatures);
            clust.setCounts(clustCounts);
        }
        else{
            clust = new KMClust(label, clustNClust, clustDim, clustFeatures, clustCounts);
        }
        kmclustRepository.save(clust);
        System.out.println("KMCLUST saved with nclust:" + clustNClust + " clustDim:" + clustDim + " clustFeaturesSize:" + clustFeatures.length + " clustCountsLength:" + clustCounts.length);

        return clustCounts;
    }
}
