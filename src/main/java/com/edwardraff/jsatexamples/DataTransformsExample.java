package com.edwardraff.jsatexamples;


import com.edwardraff.jsatfx.Plot;
import java.io.File;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javax.swing.JFrame;
import jsat.ARFFLoader;
import jsat.DataSet;
import jsat.classifiers.ClassificationDataSet;
import jsat.datatransform.DataTransform;
import jsat.datatransform.PCA;
import jsat.datatransform.ZeroMeanTransform;

/**
 * For a number of reasons, it may be beneficial to apply some sort of transformation to a data set. 
 * This could be to improve accuracy, reduce computation time, or other reasons. 
 * Here we show an example on how to apply PCA for visualization. 
 * 
 * @author Edward Raff
 */
public class DataTransformsExample
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
        //We specify '0' as the class we would like to make the target class. 
        ClassificationDataSet cDataSet = new ClassificationDataSet(dataSet, 0);

        //The IRIS data set has 4 numerical attributes, unfortunately humans are not good at visualizing 4 dimensional things.
        //Instead, we can reduce the dimensionality down to two. 

        //PCA needs the data samples to have a mean of ZERO, so we need a transform to ensue this property as well
        DataTransform zeroMean = new ZeroMeanTransform(cDataSet);
        cDataSet.applyTransform(zeroMean);
        
        //PCA is a transform that attempts to reduce the dimensionality while maintaining all the variance in the data. 
        //PCA also allows us to specify the exact number of dimensions we would like 
        DataTransform pca = new PCA(cDataSet, 2, 1e-9);
        
        //We can now apply the transformations to our data set
        cDataSet.applyTransform(pca);
        
        //We can now visualize our 2 dimensional data set!
        
        //First create the GUI
        JFrame jFrame = new JFrame("2D Visualization");
        JFXPanel panel = new JFXPanel();
        jFrame.add(panel);
        jFrame.setSize(400, 400);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Platform.runLater(() -> 
        {
            //this will create the plot that does the visualization
            ScatterChart<Number, Number> plot = Plot.scatterC(cDataSet);
            Scene scene = new Scene(plot);
            panel.setScene(scene);
        });
    }
}
