package net.flamgop.gui;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainWindow {

    public static double[] xSetContinental = new double[] {
            -1, -0.7, -0.5, -0.2, 0, 0.3, 0.35, 0.5, 0.65, 0.7, 1
    };
    public static double[] ySetContinental = new double[] {
            50, 52, 69, 72, 87, 99, 112, 134, 160, 189, 200
    };

    public static double[] xSetErosion = new double[] {
            40, 44, 56, 86, 89, 91, 100, 140, 153, 172, 230
    };
    public static double[] ySetErosion = new double[] {
            -200, -183, -167, -150, -135, 0, 135, 150, 167, 183, 200
    };

    public static double[] xSetPV = new double[] {
            -323, -214, -167, -121, -60, 0, 60, 121, 167, 214, 323
    };
    public static double[] ySetPV = new double[] {
            50, 52, 69, 72, 87, 99, 112, 134, 160, 189, 200
    };

    public static float threshold = 0.5f;
    public static boolean use3dNoise = true;

    public MainWindow() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout());

        XYSeries series = new XYSeries("Spline");

        for (int i = 0; i < xSetContinental.length; i++) {
            series.add(xSetContinental[i], ySetContinental[i]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart("Continentalness Scalar",
                "Continentalness",
                "Erosion",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        chart.getXYPlot().setRenderer(new XYSplineRenderer(25));

        ChartPanel chartPanel = new ChartPanel(chart);

        var draggingModel = new DraggingModel(chartPanel, series);
        chartPanel.addMouseMotionListener(draggingModel);

        chartPanel.addChartMouseListener(new ChartMouseListener() {
            @Override public void chartMouseClicked(ChartMouseEvent event) {}
            @Override public void chartMouseMoved(ChartMouseEvent event) {}
        });

        chartPanel.setMouseZoomable(false);
        chartPanel.setMouseWheelEnabled(false);

        panel.add(chartPanel);

        XYSeries series1 = new XYSeries("Spline");

        for (int i = 0; i < xSetErosion.length; i++) {
            series1.add(xSetErosion[i], xSetErosion[i]);
        }

        XYSeriesCollection dataset1 = new XYSeriesCollection();
        dataset1.addSeries(series1);

        JFreeChart chart1 = ChartFactory.createXYLineChart("Erosion Scalar",
                "Continentalness",
                "PV",
                dataset1,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        chart1.getXYPlot().setRenderer(new XYSplineRenderer(25));

        ChartPanel chartPanel1 = new ChartPanel(chart1);

        var draggingModel1 = new DraggingModel1(chartPanel1, series1);
        chartPanel1.addMouseMotionListener(draggingModel1);

        chartPanel1.addChartMouseListener(new ChartMouseListener() {
            @Override public void chartMouseClicked(ChartMouseEvent event) {}
            @Override public void chartMouseMoved(ChartMouseEvent event) {}
        });

        chartPanel1.setMouseZoomable(false);
        chartPanel1.setMouseWheelEnabled(false);

        panel.add(chartPanel1);

        XYSeries series2 = new XYSeries("Spline");

        for (int i = 0; i < xSetPV.length; i++) {
            series2.add(xSetPV[i], xSetPV[i]);
        }

        XYSeriesCollection dataset2 = new XYSeriesCollection();
        dataset2.addSeries(series2);

        JFreeChart chart2 = ChartFactory.createXYLineChart("PV Scalar",
                "Erosion",
                "Height",
                dataset2,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        chart2.getXYPlot().setRenderer(new XYSplineRenderer(25));

        ChartPanel chartPanel2 = new ChartPanel(chart2);

        var draggingModel2 = new DraggingModel2(chartPanel2, series2);
        chartPanel2.addMouseMotionListener(draggingModel2);

        chartPanel2.addChartMouseListener(new ChartMouseListener() {
            @Override public void chartMouseClicked(ChartMouseEvent event) {}
            @Override public void chartMouseMoved(ChartMouseEvent event) {}
        });

        chartPanel2.setMouseZoomable(false);
        chartPanel2.setMouseWheelEnabled(false);

        panel.add(chartPanel2);

        JFrame frame = new JFrame("Spline Modifier");
        // Get the icon from the resources
        frame.setIconImage(new ImageIcon(MainWindow.class.getResource("/icon.png")).getImage());

        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
