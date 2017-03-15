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
import java.util.List;
import jsat.classifiers.*;
import jsat.classifiers.svm.PlattSMO;
import jsat.classifiers.svm.SupportVectorLearner.CacheMode;
import jsat.distributions.kernels.RBFKernel;
import jsat.io.LIBSVMLoader;
import jsat.parameters.RandomSearch;

/**
 *
 * @author Edward Raff
 */
public class EasyParameterSearch
{
    public static void main(String[] args) throws IOException
    {
        //Downloaded dataset from 
        //https://www.csie.ntu.edu.tw/~cjlin/libsvmtools/datasets/binary/diabetes
        /* These first two lines are to help us get the file we want from the 
         * resource folder under src/main/resources . This way we always get the
         * files included in the examples. 
         * You can change them to however you please to get another file from 
         * disk or somewhere else. 
         */
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File file = new File(classloader.getResource("diabetes.libsvm").getFile());
        ClassificationDataSet dataset = LIBSVMLoader.loadC(file);
        
        ///////First, the code someone new would use////////
        PlattSMO model = new PlattSMO(new RBFKernel());
        model.setCacheMode(CacheMode.FULL);//Small dataset, so we can do this
        
        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(model, dataset);
        cme.evaluateCrossValidation(10);
        
        System.out.println("Error rate: " + cme.getErrorRate());
        
        /*
         * Now some easy code to tune the model. Because the parameter values
         * can be impacted by the dataset, we should split the data int a train 
         * and test set to avoid overfitting. 
         */

        List<ClassificationDataSet> splits = dataset.randomSplit(0.75, 0.25);
        ClassificationDataSet train = splits.get(0), test = splits.get(1);
        
        RandomSearch search = new RandomSearch((Classifier)model, 3);
        search.setTrials(100);
        if(search.autoAddParameters(train) > 0)//this method adds parameters, and returns the number of parameters added
        {
            //that way we only do the search if there are any parameters to actually tune
            cme = new ClassificationModelEvaluation(search, train);
            cme.evaluateTestSet(test);
            System.out.println("Tuned Error rate: " + cme.getErrorRate());
        }
        else//otherwise we will just have to trust our original CV error rate
            System.out.println("This model dosn't seem to have any easy to tune parameters");
    }
}
