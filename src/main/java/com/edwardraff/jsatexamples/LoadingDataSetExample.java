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
import jsat.classifiers.DataPoint;

/**
 * A simple example on loading up a data set. 
 * 
 * @author Edward Raff
 */
public class LoadingDataSetExample
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
         /* These first two lines are to help us get the file we want from the 
         * resource folder under src/main/resources . This way we always get the
         * files included in the examples. 
         * You can change them to however you please to get another file from 
         * disk or somewhere else. 
         */
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File file = new File(classloader.getResource("iris.arff").getFile());
        DataSet dataSet = ARFFLoader.loadArffFile(file);
        System.out.println("There are " + dataSet.getNumFeatures() + " features for this data set.");
        System.out.println(dataSet.getNumCategoricalVars() + " categorical features");
        System.out.println("They are:");
        for(int i = 0; i <  dataSet.getNumCategoricalVars(); i++)
            System.out.println("\t" + dataSet.getCategoryName(i));
        System.out.println(dataSet.getNumNumericalVars() + " numerical features");
        System.out.println("They are:");
        for(int i = 0; i <  dataSet.getNumNumericalVars(); i++)
            System.out.println("\t" + dataSet.getNumericName(i));
        
        System.out.println("\nThe whole data set");
        for(int i = 0; i < dataSet.getSampleSize(); i++)
        {
            DataPoint dataPoint = dataSet.getDataPoint(i);
            System.out.println(dataPoint);
        }
        
    }
}