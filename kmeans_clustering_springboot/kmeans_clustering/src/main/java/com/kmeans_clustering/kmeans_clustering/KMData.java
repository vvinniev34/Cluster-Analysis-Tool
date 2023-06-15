package com.kmeans_clustering.kmeans_clustering;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class KMData {
    @Id
    private int label;
    private int ndata;
    private int dim;
    private int[] assigns;
    @OneToMany (mappedBy = "kmdata")
    @OrderBy("x ASC, y ASC")
    private List<DataPoint> features;

    protected KMData(){}

    public KMData(int label){
        this.label = label;
    }

    public int getNData() {
        return this.ndata;
    }

    public void setNData(int ndata){
        this.ndata = ndata;
    }

    public int getDim() {
        return this.dim;
    }

    public void setDim(int dim){
        this.dim = dim;
    }

    public int getLabel() {
        return this.label;
    }

    public int[] getAssigns(){
        return this.assigns;
    }

    public void setAssigns(int[] assigns){
        this.assigns = assigns;
    }

    public List<DataPoint> getFeatures() {
        return this.features;
    }
}
