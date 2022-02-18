package net.flamgop.gui;

import net.flamgop.generator.FastNoiseLite;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;

public class DraggingModel1 extends MouseMotionAdapter {

    private XYDataItem selected = null;
    private int selectedIndex;
    private ChartPanel panel;
    private XYSeries series;

    public DraggingModel1(ChartPanel chartPanel, XYSeries series) {
        this.panel = chartPanel;
        this.series = series;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        var entity = panel.getEntityForPoint(e.getX(), e.getY());
        if (entity instanceof XYItemEntity xyItem && series.getDataItem(xyItem.getItem()) != null) {
            selected = series.getDataItem(xyItem.getItem());
            selectedIndex = xyItem.getItem();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selected != null) {
            adjustEntityPositionToMouse(e.getX(), e.getY());
        }
    }

    void adjustEntityPositionToMouse(int x, int y) {
        FastNoiseLite.Vector2 normalised = convertToNormalisedCoords(x, y);
        var mx = normalised.x;
        var my = normalised.y;
        var chart = panel.getChart();
        var plot = chart.getXYPlot();
        var range = plot.getDomainAxis().getRange();
        var domain = plot.getRangeAxis().getRange();

        series.remove(selected.getX());
        MainWindow.xSetErosion[selectedIndex] = range.getLowerBound() + mx * range.getLength();
        MainWindow.ySetErosion[selectedIndex] = domain.getLowerBound() + my * domain.getLength();

        Arrays.sort(MainWindow.xSetErosion);
        Arrays.sort(MainWindow.ySetErosion);

        var item = new XYDataItem(range.getLowerBound() + mx * range.getLength(), domain.getLowerBound() + my * domain.getLength());
        series.add(item, true);
        selected = item;
    }

    FastNoiseLite.Vector2 convertToNormalisedCoords(int x, int y) {
        var dataArea = panel.getScreenDataArea();
        var dx = dataArea.getMaxX() - dataArea.getMinX();
        var dy = dataArea.getMaxY() - dataArea.getMinY();

        var normalX = (x - dataArea.getMinX()) / dx;
        var normalY = 1.0-(y - dataArea.getMinY()) / dy;
        return new FastNoiseLite.Vector2((float) normalX, (float) normalY);
    }
}
