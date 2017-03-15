package com.edwardraff.jsatexamples;


import com.edwardraff.jsatfx.Plot;
import java.util.Random;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javax.swing.JFrame;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.distributions.multivariate.NormalM;
import jsat.linear.DenseMatrix;
import jsat.linear.DenseVector;
import jsat.linear.Matrix;
import jsat.linear.Vec;

/**
 * It can often be useful to generate synthetic data sets to test out classifiers on, and get a feel for how they work. 
 * 
 * @author Edward Raff
 */
public class GeneratingDataExample
{
    public static void main(String[] args)
    {
        //We create a new data set. This data set will have 2 dimensions so we can visualize it, and 4 target class values
        ClassificationDataSet dataSet = new ClassificationDataSet(2, new CategoricalData[0], new CategoricalData(4));

        //We can generate data from a multivarete normal distribution. The 'M' at the end stands for Multivariate 
        NormalM normal;

        //The normal is specifed by a mean and covariance matrix. The covariance matrix must be symmetric. 
        //We use a simple covariance matrix for each data point for simplicity
        Matrix covariance = new DenseMatrix(new double[][]
        {
            {1.0, 0.0}, //Try altering these values to see the change!
            {0.0, 1.0} //Just make sure its still symetric & a valid covariance matrix! 
        });

        //And we create 3 different means
        Vec mean0 = DenseVector.toDenseVec(0.0, 0.0);
        Vec mean1 = DenseVector.toDenseVec(0.0, 4.0);
        Vec mean2 = DenseVector.toDenseVec(4.0, 0.0);
        Vec mean3 = DenseVector.toDenseVec(4.0, 4.0);

        Vec[] means = new Vec[] {mean0, mean1, mean2, mean3};

        //We now generate out data
        for(int i = 0; i < means.length; i++)
        {
            normal = new NormalM(means[i], covariance);
            for(Vec sample : normal.sample(300, new Random()))
                dataSet.addDataPoint(sample, new int[0], i);
        }
        
        JFrame jFrame = new JFrame("2D Visualization");
        JFXPanel panel = new JFXPanel();
        jFrame.add(panel);
        jFrame.setSize(400, 400);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(() -> 
        {
            //this will create the plot that does the visualization
            ScatterChart<Number, Number> plot = Plot.scatterC(dataSet);
            Scene scene = new Scene(plot);
            panel.setScene(scene);
        });
    }
}
