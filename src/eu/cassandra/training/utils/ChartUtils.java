/*
Copyright 2011-2013 The Cassandra Consortium (cassandra-fp7.eu)


Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package eu.cassandra.training.utils;

import java.awt.Color;
import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.CategoryLabelWidthType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.function.Function2D;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

import eu.cassandra.training.entities.Person;

/**
 * This class contains static functions that are used for the creation of
 * visualization charts that are appearing in the Training Module GUI.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class ChartUtils
{

  /**
   * This function is used for the visualization of a Comparative Response Model
   * Histogram.
   * 
   * @param title
   *          The title of the chart.
   * @param x
   *          The unit on the X axis of the chart.
   * @param y
   *          The unit on the Y axis of the chart.
   * @param dataBefore
   *          The array of values before the response.
   * @param dataAfter
   *          The array of values after the response.
   * @return a chart panel with the graphical representation.
   */
  public static ChartPanel createResponseHistogram (String title, String x,
                                                    String y,
                                                    double[] dataBefore,
                                                    double[] dataAfter)
  {
    XYSeries series1 = new XYSeries("Basic Pricing Scheme");
    for (int i = 0; i < dataBefore.length; i++) {
      series1.add(i, dataBefore[i]);
    }

    XYSeries series2 = new XYSeries("New Pricing Scheme");
    for (int i = 0; i < dataAfter.length; i++) {
      series2.add(i, dataAfter[i]);
    }

    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);
    dataset.addSeries(series2);

    PlotOrientation orientation = PlotOrientation.VERTICAL;
    boolean show = true;
    boolean toolTips = false;
    boolean urls = false;

    JFreeChart chart =
      ChartFactory.createXYLineChart(title, x, y, dataset, orientation, show,
                                     toolTips, urls);
    XYPlot xyplot = (XYPlot) chart.getPlot();
    xyplot.setDomainPannable(true);
    xyplot.setRangePannable(true);
    xyplot.setForegroundAlpha(0.85F);
    NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
    numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    return new ChartPanel(chart);
  }

  /**
   * This function is used for the visualization of a Comparative Response Model
   * Histogram.
   * 
   * @param title
   *          The title of the chart.
   * @param x
   *          The unit on the X axis of the chart.
   * @param y
   *          The unit on the Y axis of the chart.
   * @param dataBefore
   *          The array of values before the response.
   * @param dataAfter
   *          The array of values after the response.
   * @return a chart panel with the graphical representation.
   */
  public static ChartPanel createDailyResponseHistogram (String title,
                                                         String x, String y,
                                                         double[] dataBefore,
                                                         double[] dataAfter)
  {
    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    for (int i = 0; i < dataBefore.length; i++) {
      dataset.addValue(dataBefore[i], "Basic Scheme", "" + i + "");
      if (i < dataAfter.length)
        dataset.addValue(dataAfter[i], "New Scheme", "" + i + "");
      else
        dataset.addValue(0, "New Scheme", "" + i + "");
    }

    JFreeChart chart = ChartFactory.createBarChart3D(title, // chart title
                                                     x, // domain axis label
                                                     y, // range axis label
                                                     dataset, // data
                                                     PlotOrientation.VERTICAL, // orientation
                                                     true, // include legend
                                                     true, // tooltips
                                                     false // urls
            );

    final CategoryPlot plot = chart.getCategoryPlot();
    plot.setForegroundAlpha(1.0f);

    // left align the category labels...
    final CategoryAxis axis = plot.getDomainAxis();
    final CategoryLabelPositions p = axis.getCategoryLabelPositions();

    final CategoryLabelPosition left =
      new CategoryLabelPosition(RectangleAnchor.LEFT,
                                TextBlockAnchor.CENTER_LEFT,
                                TextAnchor.CENTER_LEFT, 0.0,
                                CategoryLabelWidthType.RANGE, 0.30f);
    axis.setCategoryLabelPositions(CategoryLabelPositions
            .replaceLeftPosition(p, left));

    return new ChartPanel(chart);
  }

  /**
   * This function is used for the visualization of a Line Diagram.
   * 
   * @param title
   *          The title of the chart.
   * @param x
   *          The unit on the X axis of the chart.
   * @param y
   *          The unit on the Y axis of the chart.
   * @param data
   *          The array of values.
   * @return a chart panel with the graphical representation.
   */
  public static ChartPanel createLineDiagram (String title, String x, String y,
                                              double[] data)
  {

    XYSeries series1 = new XYSeries("Active Power");
    for (int i = 0; i < data.length; i++) {
      series1.add(i, data[i]);
    }

    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);

    PlotOrientation orientation = PlotOrientation.VERTICAL;
    boolean show = true;
    boolean toolTips = false;
    boolean urls = false;

    JFreeChart chart =
      ChartFactory.createXYLineChart(title, x, y, dataset, orientation, show,
                                     toolTips, urls);

    return new ChartPanel(chart);
  }

  public static ChartPanel createLineDiagram (String title, String x, String y,
                                              double[] data, double[] data2)
  {

    XYSeries series1 = new XYSeries("Active Power");
    for (int i = 0; i < data.length; i++) {
      series1.add(i, data[i]);
    }

    XYSeries series2 = new XYSeries("Reactive Power");
    for (int i = 0; i < data2.length; i++) {
      series2.add(i, data2[i]);
    }

    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);
    dataset.addSeries(series2);

    PlotOrientation orientation = PlotOrientation.VERTICAL;
    boolean show = true;
    boolean toolTips = false;
    boolean urls = false;

    JFreeChart chart =
      ChartFactory.createXYLineChart(title, x, y, dataset, orientation, show,
                                     toolTips, urls);

    return new ChartPanel(chart);
  }

  /**
   * This function is used for the visualization of a Histogram.
   * 
   * @param title
   *          The title of the chart.
   * @param x
   *          The unit on the X axis of the chart.
   * @param y
   *          The unit on the Y axis of the chart.
   * @param data
   *          The array of values.
   * @return a chart panel with the graphical representation.
   */
  public static ChartPanel createHistogram (String title, String x, String y,
                                            Double[] data)
  {

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    for (int i = 0; i < data.length; i++) {
      dataset.addValue(data[i], y, (Comparable) i);
    }

    PlotOrientation orientation = PlotOrientation.VERTICAL;
    boolean show = false;
    boolean toolTips = false;
    boolean urls = false;

    JFreeChart chart =
      ChartFactory.createBarChart(title, x, y, dataset, orientation, show,
                                  toolTips, urls);

    return new ChartPanel(chart);
  }

  /**
   * This function is used for the visualization of a Histogram.
   * 
   * @param title
   *          The title of the chart.
   * @param x
   *          The unit on the X axis of the chart.
   * @param y
   *          The unit on the Y axis of the chart.
   * @param data
   *          The array of values.
   * @return a chart panel with the graphical representation.
   */
  public static ChartPanel createHistogram (String title, String x, String y,
                                            double[] data)
  {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    for (int i = 0; i < data.length; i++) {
      dataset.addValue(data[i], y, (Comparable) i);
    }

    PlotOrientation orientation = PlotOrientation.VERTICAL;
    boolean show = false;
    boolean toolTips = false;
    boolean urls = false;

    JFreeChart chart =
      ChartFactory.createBarChart(title, x, y, dataset, orientation, show,
                                  toolTips, urls);

    return new ChartPanel(chart);
  }

  /**
   * This function is used for the visualization of two Area Diagrams.
   * 
   * @param title
   *          The title of the chart.
   * @param x
   *          The unit on the X axis of the chart.
   * @param y
   *          The unit on the Y axis of the chart.
   * @param doubles
   *          The array of values of the first array.
   * @param doubles2
   *          The array of values of the second array.
   * @return a chart panel with the graphical representation.
   */
  public static ChartPanel createArea (String title, String x, String y,
                                       Double[] doubles, Double[] doubles2)
  {
    JFreeChart chart = null;
    if (doubles.length != doubles2.length) {
      System.out.println("ERROR with lengths.");
    }
    else {
      Double[][] data = new Double[2][doubles.length];

      data[0] = doubles;
      data[1] = doubles2;

      final CategoryDataset dataset =
        DatasetUtilities.createCategoryDataset("Power ", "Type ", data);

      chart =
        ChartFactory.createAreaChart(title, x, y, dataset,
                                     PlotOrientation.VERTICAL, true, true,
                                     false);

      chart.setBackgroundPaint(Color.white);

      final CategoryPlot plot = chart.getCategoryPlot();
      plot.setForegroundAlpha(0.5f);

      // plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
      plot.setBackgroundPaint(Color.lightGray);

    }
    return new ChartPanel(chart);
  }

  /**
   * This function is used for the visualization of a Gaussian (Normal)
   * Distribution.
   * 
   * @param title
   *          The title of the chart.
   * @param x
   *          The unit on the X axis of the chart.
   * @param y
   *          The unit on the Y axis of the chart.
   * @param mean
   *          The mean parameter of the distribution.
   * 
   * @param mean
   *          The standard deviation parameter of the distribution.
   * @return a chart panel with the graphical representation.
   */
  public static ChartPanel createNormalDistribution (String title, String x,
                                                     String y, double mean,
                                                     double sigma)
  {

    if (sigma < 0.01)
      sigma = 0.01;
    Function2D normal = new NormalDistributionFunction2D(mean, sigma);
    XYDataset dataset =
      DatasetUtilities.sampleFunction2D(normal, 0, (mean + 4 * sigma), 100,
                                        "Normal");
    PlotOrientation orientation = PlotOrientation.VERTICAL;
    boolean show = false;
    boolean toolTips = false;
    boolean urls = false;

    JFreeChart chart =
      ChartFactory.createXYLineChart(title, x, y, dataset, orientation, show,
                                     toolTips, urls);

    return new ChartPanel(chart);

  }

  /**
   * This function is used for the visualization of a Gaussian Mixture
   * Distribution.
   * 
   * @param title
   *          The title of the chart.
   * @param x
   *          The unit on the X axis of the chart.
   * @param y
   *          The unit on the Y axis of the chart.
   * @param data
   *          The array of values.
   * @return a chart panel with the graphical representation.
   */
  public static ChartPanel createMixtureDistribution (String title, String x,
                                                      String y, double[] data)
  {
    XYSeries series1 = new XYSeries("First");

    for (int i = 0; i < data.length; i++) {
      series1.add(i, data[i]);
    }

    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);

    PlotOrientation orientation = PlotOrientation.VERTICAL;
    boolean show = false;
    boolean toolTips = false;
    boolean urls = false;

    JFreeChart chart =
      ChartFactory.createXYLineChart(title, x, y, dataset, orientation, show,
                                     toolTips, urls);

    return new ChartPanel(chart);

  }

  /**
   * This function is used for parsing and presenting the basic pricing schema.
   * 
   * @param basic
   *          The basic pricing schema
   * @return a chart panel with the
   *         graphical representation.
   */
  public static ChartPanel parsePricingScheme (String basic)
  {

    double[] data = new double[Constants.MINUTES_PER_DAY];

    String[] lines = basic.split("\n");

    int startTime = -1;
    int endTime = -1;

    for (String line: lines) {

      String start = line.split("-")[0];

      int startHour = Integer.parseInt(start.split(":")[0]);
      int startMinute = Integer.parseInt(start.split(":")[1]);

      String end = line.split("-")[1];

      int endHour = Integer.parseInt(end.split(":")[0]);
      int endMinute = Integer.parseInt(end.split(":")[1]);

      startTime = startHour * 60 + startMinute;
      endTime = endHour * 60 + endMinute;

      System.out.println("Start: " + startTime + " End: " + endTime);

      double value = Double.parseDouble(line.split("-")[2]);

      if (startTime < endTime) {
        for (int i = startTime; i <= endTime; i++)
          data[i] = value;
      }
    }

    XYSeries series1 = new XYSeries("Basic Pricing Scheme");
    for (int i = 0; i < data.length; i++) {
      series1.add(i, data[i]);
    }

    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);

    PlotOrientation orientation = PlotOrientation.VERTICAL;
    boolean show = true;
    boolean toolTips = false;
    boolean urls = false;

    JFreeChart chart =
      ChartFactory.createXYLineChart("Pricing Scheme", "Minute of Day",
                                     "Euros/kWh", dataset, orientation, show,
                                     toolTips, urls);

    return new ChartPanel(chart);
  }

  /**
   * This function is used for parsing and presenting the basic and the new
   * pricing schema.
   * 
   * @param basic
   *          The basic pricing schema
   * @param after
   *          The new pricing schema
   * 
   * @return a chart panel with the
   *         graphical representation.
   */
  public static ChartPanel parsePricingScheme (String basic, String after)
  {

    double[] data = Utils.parseScheme(basic);

    double[] data2 = Utils.parseScheme(after);

    XYSeries series1 = new XYSeries("Basic Pricing Scheme");
    for (int i = 0; i < data.length; i++) {
      series1.add(i, data[i]);
    }

    XYSeries series2 = new XYSeries("New Pricing Scheme");
    for (int i = 0; i < data2.length; i++) {
      series2.add(i, data2[i]);
    }

    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);
    dataset.addSeries(series2);

    PlotOrientation orientation = PlotOrientation.VERTICAL;
    boolean show = true;
    boolean toolTips = false;
    boolean urls = false;

    JFreeChart chart =
      ChartFactory.createXYLineChart("Pricing Schemes", "Minute of Day",
                                     "Euros/kWh", dataset, orientation, show,
                                     toolTips, urls);

    return new ChartPanel(chart);
  }

  /**
   * This function is used for creating a pie chart of a Person Model's
   * statistical attributes.
   * 
   * @param title
   *          The chart's title
   * @param person
   *          The person under consideration
   * 
   * @return a chart panel with the statistical graphical representation.
   */
  public static ChartPanel createPieChart (String title, Person person)
  {

    DefaultPieDataset dataset = new DefaultPieDataset();
    dataset.setValue("Activity Models", person.getActivityModelsSize());
    dataset.setValue("Response Models", person.getResponseModelsSize());

    JFreeChart chart =
      ChartFactory.createPieChart(title, dataset, true, true, true);

    PiePlot plot = (PiePlot) chart.getPlot();
    PieSectionLabelGenerator generator =
      new StandardPieSectionLabelGenerator("{0} = {1}", new DecimalFormat("0"),
                                           new DecimalFormat("0.00%"));
    plot.setLabelGenerator(generator);

    return new ChartPanel(chart);
  }

}
