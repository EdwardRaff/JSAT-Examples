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
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import jsat.ARFFLoader;
import jsat.classifiers.ClassificationDataSet;
import jsat.io.CSV;
import jsat.linear.Vec;

/**
 * This example shows you how to write a dataset to a CSV file and then read it back in. 
 * @author Edward Raff
 */
public class CSVExample
{
    public static void main(String... args) throws IOException
    {
        /*
         * Most of the examples in JSAT use the ARFF or libsvm file storage 
         * formats. This is becasue the libsvm and Weka websites have a lot of 
         * datasets ready-to go. But what if you want to export your data as a 
         * CSV file? This is useful or excel and is a format almost *everything*
         * can read. 
         */
        
        
        //First lets load the iris dataset as an example
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File irisArffFile = new File(classloader.getResource("iris.arff").getFile());
        
        ClassificationDataSet irisDataSet = ARFFLoader.loadArffFile(irisArffFile).asClassificationDataSet(0);
        
        //Now we have the iris dataset in memory. We can write it out to a CSV file. The ARFF and LIBSVM writers have a similar style
        //You can give the write method a Path object, or a Writer object, giving you flexability about how and where the file is written to
        File irisCSVFile = new File(irisArffFile.getParent(), "iris.csv");
        CSV.write(irisDataSet, irisCSVFile.toPath());
        
        //We can also load the iris dataset back in, and see that everyhing matches! Lets try that
        
        //CSV files aren't a standard format, and don't have any header to tell 
        //us what is stored where. So we need to be explicit about how to read 
        //in the data
        
        //lines_to_skip will skip the specified number of lines in a CSV. This 
        //is to skip over any header information that may have been added by 
        //someone. JSAT dosn't write out any CSV headers, so we don't have to 
        //skip anything
        int lines_to_skip = 0;
        //With CSV files, we don't know which features are numeric and which are
        //categorical. The default assumption is that features are numeric, and
        //JSAT needs the user to tell it which columns of the CSV file are 
        //categorical. The dataset we wrote out dosn't have any categorical 
        //features, so we can use the empty set to indiate that all features are
        //numeric. 
        Set<Integer> categoricalFeatures = Collections.EMPTY_SET;
        //Since we are reading in a classification dataset, we can use the readC
        //method to do this. It needs us to specify which column has the target 
        //feature (what we are trying to predict). JSAT always puts the target 
        //feature in the first column, so we will use index 0. 
        ClassificationDataSet irisData2 = CSV.readC(0, irisCSVFile.toPath(), lines_to_skip, categoricalFeatures);
        
        //We have now read in a CSV file! Lets check if everythin matches!
        for(int i = 0; i < irisDataSet.getSampleSize(); i++)
        {
            Vec orig_i = irisDataSet.getDataPoint(i).getNumericalValues();
            Vec new_i = irisData2.getDataPoint(i).getNumericalValues();
            //We give the equality comparison a little wiggle room, you can get 
            //slightly different values when convertig a float to a string and 
            //back again. If you want exact matches, use the JSATData format
            if(!orig_i.equals(new_i, 1e-10))
                System.out.println("OH NO, features aren't equal!");
        }
        //The pring statment above should have never occured, so all the
        //datapoints matched! Same order and values! We were able to write our
        //and read back in a CSV data set!
        
        //Now lets clean up by deleting that CSV file. 
        irisCSVFile.delete();
    }
}
