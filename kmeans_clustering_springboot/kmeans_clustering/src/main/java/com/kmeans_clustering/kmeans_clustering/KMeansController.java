package com.kmeans_clustering.kmeans_clustering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class KMeansController {

    @Autowired
    private KMeansService kmeansService;

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(method = RequestMethod.GET, value = "/clusters/counts/{label}")
    public int[] getClusterCountsByLabel(@PathVariable int label){
        if (label == -1)    return new int[]{-1};
        System.out.println("0");
        int[] ret = kmeansService.getClusterCountsByLabel(label);
        if (ret == null){
            System.out.println("0.5");
            return new int[]{0};
        }
        else{
            System.out.println("0.75");
            return ret;
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(method = RequestMethod.GET, value = "/clusters/calculate_counts/{label}-{nclust}-{maxIterations}")
    public int[] calculateClusterCounts(@PathVariable int label, @PathVariable int nclust, @PathVariable int maxIterations){
        if (maxIterations == -1)    return new int[]{-1};
        System.out.println("1");
        kmeansService.kmeansClustering(nclust, maxIterations, label);
        return kmeansService.getClusterCountsByLabel(label);
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(method = RequestMethod.GET, value = "/clusters/datapoints/{label}")
    public int[] getKMData(@PathVariable int label){
        if (label == -1)    return new int[]{-1};
        System.out.println("2");
        return kmeansService.getKMDataByLabel(label).getAssigns();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/uploadKMData/{label}")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable int label) {
        try {
            byte[] bytes = file.getBytes();
            String text = new String(bytes, "UTF-8");

            // Process the text as needed
            kmeansService.addKMData(text, label);

            return "File uploaded successfully.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload file.";
        }
    }
}
