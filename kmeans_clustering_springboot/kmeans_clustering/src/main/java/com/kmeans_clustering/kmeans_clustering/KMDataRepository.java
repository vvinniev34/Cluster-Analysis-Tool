package com.kmeans_clustering.kmeans_clustering;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface KMDataRepository extends CrudRepository<KMData, Integer> {
    KMData findByLabel(int label);
}
