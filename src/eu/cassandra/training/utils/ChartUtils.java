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

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.function.Function2D;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ChartUtils
{

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

  public static <T> void createHistogram (String title, String x, String y,
                                          Map<T, Double> data)
  {

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    for (T key: data.keySet()) {
      dataset.addValue(data.get(key), y, (Comparable) key);
    }

    PlotOrientation orientation = PlotOrientation.VERTICAL;
    boolean show = false;
    boolean toolTips = false;
    boolean urls = false;
    JFreeChart chart =
      ChartFactory.createBarChart(title, x, y, dataset, orientation, show,
                                  toolTips, urls);
    int width = 1024;
    int height = 768;

    try {
      ChartUtilities.saveChartAsPNG(new File("Charts/Histogram/" + title
                                             + ".PNG"), chart, width, height);
    }
    catch (IOException e) {
    }

  }

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

  public static void createPieChart (String title, double[] data)
  {

    DefaultPieDataset dataset = new DefaultPieDataset();
    dataset.setValue("Night", data[0]);
    dataset.setValue("Morning", data[1]);
    dataset.setValue("Noon", data[2]);
    dataset.setValue("Evening", data[3]);

    JFreeChart chart =
      ChartFactory.createPieChart(title, dataset, true, true, true);

    PiePlot plot = (PiePlot) chart.getPlot();
    PieSectionLabelGenerator generator =
      new StandardPieSectionLabelGenerator("{0} = {2}", new DecimalFormat("0"),
                                           new DecimalFormat("0.00%"));
    plot.setLabelGenerator(generator);

    int width = 500;
    int height = 300;

    try {
      ChartUtilities.saveChartAsPNG(new File("Charts/Pie/" + title + ".PNG"),
                                    chart, width, height);
    }
    catch (IOException e) {
    }

  }

  public static void createPeakPieChart (String title, double[] data)
  {

    DefaultPieDataset dataset = new DefaultPieDataset();

    for (int i = 0; i < data.length; i++) {

      dataset.setValue("Appliance " + i, data[i]);

    }

    JFreeChart chart =
      ChartFactory.createPieChart(title, dataset, true, true, true);

    PiePlot plot = (PiePlot) chart.getPlot();
    PieSectionLabelGenerator generator =
      new StandardPieSectionLabelGenerator("{0} = {2}", new DecimalFormat("0"),
                                           new DecimalFormat("0.00%"));
    plot.setLabelGenerator(generator);

    int width = 500;
    int height = 300;

    try {
      ChartUtilities.saveChartAsPNG(new File("Charts/Pie/" + title + ".PNG"),
                                    chart, width, height);
    }
    catch (IOException e) {
    }

  }

  public static void createPieChart (String title, int[] data)
  {

    DefaultPieDataset dataset = new DefaultPieDataset();
    dataset.setValue("Night", data[0]);
    dataset.setValue("Morning", data[1]);
    dataset.setValue("Noon", data[2]);
    dataset.setValue("Evening", data[3]);

    JFreeChart chart =
      ChartFactory.createPieChart(title, dataset, true, true, true);

    PiePlot plot = (PiePlot) chart.getPlot();
    PieSectionLabelGenerator generator =
      new StandardPieSectionLabelGenerator("{0} = {2}", new DecimalFormat("0"),
                                           new DecimalFormat("0.00%"));
    plot.setLabelGenerator(generator);

    int width = 500;
    int height = 300;

    try {
      ChartUtilities.saveChartAsPNG(new File("Charts/Pie/" + title + ".PNG"),
                                    chart, width, height);
    }
    catch (IOException e) {
    }

  }

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

}
