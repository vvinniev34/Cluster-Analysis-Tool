package com.kmeans_clustering.kmeans_clustering;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Entity
public class KMClust {
    @Id
    private int label;
    private int nclust;
    private int dim;
    private float[] features;
    private int[] counts;

    protected KMClust(){}

    public KMClust(int label, int nclust, int dim, float[] features, int[] counts){
        this.label = label;
        this.nclust = nclust;
        this.features = features;
        this.counts = counts;
    }

    public int getLabel() {
        return this.label;
    }

    public int getNClust() {
        return this.nclust;
    }

    public int getDim() {
        return this.dim;
    }

    public void setDim(int dim){
        this.dim = dim;
    }

    public float[]  getFeatures() {
        return this.features;
    }

    public void setFeatures(float[] features){
        this.features = features;
    }

    public int[] getCounts() {
        return this.counts;
    }

    public void setCounts(int[] counts){
        this.counts = counts;
    }
}
