/*
 * Copyright (C) 2017 Edward Raff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.edwardraff.jsatexamples;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;
import jsat.ARFFLoader;
import jsat.classifiers.ClassificationDataSet;
import jsat.clustering.Clusterer;
import jsat.clustering.GapStatistic;
import jsat.clustering.evaluation.ClusterEvaluation;
import jsat.clustering.evaluation.NormalizedMutualInformation;
import jsat.clustering.kmeans.GMeans;
import jsat.clustering.kmeans.HamerlyKMeans;
import jsat.clustering.kmeans.KMeans;
import jsat.clustering.kmeans.KMeansPDN;
import jsat.clustering.kmeans.XMeans;
import jsat.datatransform.Imputer;
import jsat.datatransform.LinearTransform;

/**
 * K-Means is one of the most commonly used algorithms for clustering, but has a
 * weakness in that you need to tell it how many clusters "K" you want. This
 * example shows a number of different ways one can search for the "K" in
 * K-Means.
 *
 * @author Edward Raff
 */
public class KMeansAndK
{
    public static void main(String[] args)
    {
        //the data sets we we use, all have only numeric features and a class label
        String[] dataSetName = new String[]
        {
            "breast-w.arff", "heart-statlog.arff", "ionosphere.arff", "iris.arff", "sonar.arff", 
        };
        ClassificationDataSet[] dataSets = new ClassificationDataSet[dataSetName.length];
        
        for(int i = 0; i < dataSetName.length; i++)
        {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            File file = new File(classloader.getResource(dataSetName[i]).getFile());
            //We know that there is only one categorical feature for each of these data sets, and it is the class label. So we use '0' as the argument 
            dataSets[i] = ARFFLoader.loadArffFile(file).asClassificationDataSet(0);
            dataSets[i].applyTransform(new Imputer(dataSets[i]));//impute missing values in the dataset
            dataSets[i].applyTransform(new LinearTransform(dataSets[i]));//scale feature values to [0, 1]
        }
        
        /**
         * This map contains 4 different methods that try to infer the "best"
         * value of k for k-means. These don't always work well, but its
         * something lots of people are interested in.
         */
        Map<String, Clusterer> methodsToEval = new LinkedHashMap<String, Clusterer>()
        {{
            put("PDN KMeans", new KMeansPDN());
            put("Gap-Means", new GapStatistic());
            put("X-Means", new XMeans());
            put("G-Means", new GMeans());
        }};
        
        /**
         * We will compare these with 3 different values of k that we will
         * explicitly cluster for. Feel free to add/remove values from the list
         */
        int[] kToTest = new int[]{2, 3, 6};
        
        /**
         * We will use the NMI as our evaluation criteria. It compares the
         * clustering results with the class labels. The class labels aren't
         * necessarily the best ground truth for clusters. In fact, how to
         * properly evaluate clustering algorithms is a very open question! But
         * this is a commonly used method.
         *
         * The ClusterEvaluation interface dictates that values near 0 are
         * better, and larger values are worse. NMI is usually the opposite, but
         * obeys the interface. Read the NMI's Javadoc for more details.
         */
        ClusterEvaluation evaluator = new NormalizedMutualInformation();
        
        /**
         * And finally, we will use a normal k-means algorithm to do clustering
         * when we specify the number of clusters we want. JSAT implements a
         * number of different algorithms that all solve the k-means problem,
         * and are better in different scenarios. This one is likely to be the
         * best for most users.
         */
        KMeans simpleKMeans = new HamerlyKMeans();
        
        
        /*
         * Lets print out a simple header. First two values will be our data set
         * name and the number of classes in that data set. Then for each model 
         * in c1ToEval, we will print out the value of k it determined and the 
         * evaluation of that clustering. Finally we will print out the 
         * evaluation for running K-Means with some select values of k
         */
        System.out.printf("%-20.20s", "Data Set: classes");
        for( String name : methodsToEval.keySet())
            System.out.printf("%-15.15s", "| k , " + name + "");
        for( int k : kToTest)
            System.out.printf("%-15.15s", "| " + k + "-Means");
        System.out.println();
        
        //now we will loop through every data set, and evaluate all of our clustering algorithms
        for(int i = 0; i < dataSets.length; i++)
        {
            ClassificationDataSet data = dataSets[i];
            System.out.printf("%-15s: %2d | ", dataSetName[i], data.getClassSize());
            //print out the number of clusters chosen and the evaluation for each automatic version of k-means
            int[] clusteringResults = new int[data.getSampleSize()];//hold the clustering results, this version is manditory to use NMI
            for( Clusterer clusterer : methodsToEval.values())
            {
                //when we call this constructor, the algorithm is expected to figure out the number of clusters on its own
               clusterer.cluster(data, clusteringResults);
                //the number of clusters found can be determined from the maximimum cluster ID returned, +1 since 0 is a cluster ID. 
                int kFound = IntStream.of(clusteringResults).max().getAsInt()+1;;
                System.out.printf("%4d , %.3f | ",kFound, evaluator.evaluate(clusteringResults, data));
            }
            //now lets print out our results when we specify the value of k we want to try
            for(int k : kToTest)
            {
                //run k-means with a specific value of k, and keep track of cluster assignments
                clusteringResults = simpleKMeans.cluster(data, k, clusteringResults);
                //now evaluate the cluster assignments and print a score
                System.out.printf("    %.3f    | ",evaluator.evaluate(clusteringResults, data));
            }
            System.out.println();
        }
        
    }
}
