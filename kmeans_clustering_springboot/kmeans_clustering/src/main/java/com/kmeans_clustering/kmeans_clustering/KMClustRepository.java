package com.kmeans_clustering.kmeans_clustering;

import org.springframework.data.repository.CrudRepository;

public interface KMClustRepository extends CrudRepository<KMClust, Integer> {

    KMClust findByLabel(int label);

    KMClust findByLabelAndNclust(int label, int nclust);
}
