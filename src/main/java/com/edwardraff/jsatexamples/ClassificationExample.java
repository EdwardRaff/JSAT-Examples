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
import jsat.ARFFLoader;
import jsat.DataSet;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.classifiers.bayesian.NaiveBayes;

/**
 * A simple example where we load up a data set for classification purposes. 
 * 
 * @author Edward Raff
 */
public class ClassificationExample
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File file = new File(classloader.getResource("iris.arff").getFile());
        DataSet dataSet = ARFFLoader.loadArffFile(file);
        
        //We specify '0' as the class we would like to make the target class. 
        ClassificationDataSet cDataSet = new ClassificationDataSet(dataSet, 0);
        
        int errors = 0;
        Classifier classifier = new NaiveBayes();
        classifier.trainC(cDataSet);
        
        for(int i = 0; i < dataSet.getSampleSize(); i++)
        {
            DataPoint dataPoint = cDataSet.getDataPoint(i);//It is important not to mix these up, the class has been removed from data points in 'cDataSet' 
            int truth = cDataSet.getDataPointCategory(i);//We can grab the true category from the data set
            
            //Categorical Results contains the probability estimates for each possible target class value. 
            //Classifiers that do not support probability estimates will mark its prediction with total confidence. 
            CategoricalResults predictionResults = classifier.classify(dataPoint);
            int predicted = predictionResults.mostLikely();
            if(predicted != truth)
                errors++;
            System.out.println( i + "| True Class: " + truth + ", Predicted: " + predicted + ", Confidence: " + predictionResults.getProb(predicted) );
        }
        
        System.out.println(errors + " errors were made, " + 100.0*errors/dataSet.getSampleSize() + "% error rate" );
    }
}