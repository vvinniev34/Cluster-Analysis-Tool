package com.kmeans_clustering.kmeans_clustering;

import jakarta.persistence.*;

@Entity
public class DataPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int label;
    private int x;
    private int y;
    private int val;
    @ManyToOne
    private KMData kmdata;

    protected DataPoint(){}

    public DataPoint(int x, int y, int val, int label, KMData kmdata){
        this.x = x;
        this.y = y;
        this.val = val;
        this.label = label;
        this.kmdata = kmdata;
    }

    public long getId() {
        return this.id;
    }

    public int getX(){
        return this.x;
    }

    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return this.y;
    }

    public void setY(int y){
        this.y = y;
    }

    public int getVal(){
        return this.val;
    }

    public void setVal(int val){
        this.val = val;
    }

    public int getLabel(){
        return this.label;
    }

    public void setLabel(int label){
        this.label = label;
    }
}
