/*
 * Copyright (C) 2015 Edward Raff
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jsat.classifiers.*;
import jsat.classifiers.linear.LogisticRegressionDCD;
import jsat.classifiers.linear.kernelized.KernelSGD;
import jsat.classifiers.svm.extended.AMM;
import jsat.classifiers.trees.RandomForest;
import jsat.datatransform.LinearTransform;
import jsat.io.LIBSVMLoader;
import jsat.parameters.RandomSearch;
import jsat.utils.SystemInfo;

/**
 *
 * @author Edward Raff
 */
public class EasyParameterSearch2
{
    public static void main(String[] args) throws IOException
    {
        //You should check out EasyParameterSearch first before looking at this one! 
        
        //Lets use the slightly larger MNIST dataset, dataset downloaded from 
        //https://www.csie.ntu.edu.tw/~cjlin/libsvmtools/datasets/multiclass.html#mnist
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        ClassificationDataSet train = LIBSVMLoader.loadC(new File(classloader.getResource("mnist").getFile()));
        //LIBSVM format isn't very good for sparse datasets when you don't see all features
        //the extra arguments help deal with issues where a feature isn't seen in the test set but is in the training set
        ClassificationDataSet test = LIBSVMLoader.loadC(new File(classloader.getResource("mnist.t").getFile()), 0.5, train.getNumNumericalVars());
        
        //Lets normalize the mnist datasets into the range of [0, 1]
        //original example used already scaled versions of mnist, but that broke Github's file size limit
        LinearTransform transform = new LinearTransform(train);
        train.applyTransform(transform);
        test.applyTransform(transform);
        
        /*
         * New, we create a list of models we would like to try on our dataset.
         * Below I've picked 4 of my favorite models to use. They tend to work
         * well on most datasets most of the time, so I like to use them as 
         * general starting points.
         */
        List<Classifier> models = new ArrayList<>();
        models.add(new OneVSAll(new LogisticRegressionDCD(), true));//a fast exact LR algorithm
        models.add(new AMM());//A non linear model with linear-like efficency 
        models.add(new RandomForest());//everyone's favorite tree ensemble 
        models.add(new KernelSGD());//A faster approximate version of an SVM
        
        //For this example, we will make the search parallel 
        ExecutorService exec = Executors.newFixedThreadPool(SystemInfo.LogicalCores);
        
        for(Classifier model : models)//loop over each model we want to try
        {
            System.out.println("Testing model: " + model.getClass().getSimpleName());
            RandomSearch search = new RandomSearch((Classifier)model, 3);
            search.setTrials(10);//you may want to do more than 10 models for serious work, but that also increases training time
            if(search.autoAddParameters(train) > 0)//this method adds parameters, and returns the number of parameters added
            {
                //adding the exec tot he constructor makes it use multiple threads when possible 
                ClassificationModelEvaluation cme = new ClassificationModelEvaluation(search, train, exec);
                cme.evaluateTestSet(test);
                System.out.println("\tTuned Error rate: " + cme.getErrorRate());
            }
            else//otherwise we will evaluation
            {
                ClassificationModelEvaluation cme = new ClassificationModelEvaluation(model, train, exec);
                cme.evaluateTestSet(test);
                System.out.println("\tError rate: " + cme.getErrorRate());
            }
        }
        
        /*
         * Testing model: OneVSAll
         * 	Tuned Error rate: 0.08109999999999995
         * Testing model: AMM
         * 	Tuned Error rate: 0.03939999999999999
         * Testing model: RandomForest
         * 	Error rate: 0.042300000000000004
         * Testing model: KernelSGD
         * 	Tuned Error rate: 0.04920000000000002
         */
        
        exec.shutdownNow();
    }
}
