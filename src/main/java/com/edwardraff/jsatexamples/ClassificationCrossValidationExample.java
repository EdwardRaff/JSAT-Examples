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
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.ClassificationModelEvaluation;
import jsat.classifiers.Classifier;
import jsat.classifiers.bayesian.NaiveBayes;

/**
 * Testing data on the same data used to train is considered bad, and can overstate the true accuracy of a classifier. 
 * Cross Validation is a method to evaluate a model by cycling through the whole data set. While this takes more time,
 * it uses all the data for both training and testing, without ever testing a data point that was trained on. 
 * 
 * @author Edward Raff
 */
public class ClassificationCrossValidationExample
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

        //We do not train the classifier, we let the modelEvaluation do that for us!
        Classifier classifier = new NaiveBayes();
                
        ClassificationModelEvaluation modelEvaluation = new ClassificationModelEvaluation(classifier, cDataSet);
        
        //The number of folds is how many times the data set will be split and trained and tested. 10 is a common value
        modelEvaluation.evaluateCrossValidation(10);
        
        System.out.println("Cross Validation error rate is " + 100.0*modelEvaluation.getErrorRate() + "%");
        
        //We can also obtain how long it took to train, and how long classification took
        System.out.println("Trainig time: " + modelEvaluation.getTotalTrainingTime()/1000.0 + " seconds");
        System.out.println("Classification time: " + modelEvaluation.getTotalClassificationTime()/1000.0 + " seconds\n");
        
        //The model can print a 'Confusion Matrix' this tells us about the errors our classifier made. 
        //Each row represents all the data points that belong to a given class. 
        //Each column represents the predicted class
        //That means values in the diagonal indicate the number of correctly classifier points in each class. 
        //Off diagonal values indicate mistakes
        modelEvaluation.prettyPrintConfusionMatrix();
        
    }
}