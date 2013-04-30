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

import jMEF.MixtureModel;
import jMEF.PVector;
import jMEF.UnivariateGaussian;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;


public class EM
{
  int n;
  int interval;
  int median;
  Integer[] temp;
  int max;

  public EM ()
  {
    n = 0;
    interval = 0;
    median = 0;
  }

  public void createGMM (String input, String output, String variable)
    throws FileNotFoundException
  {

    temp = readFile(input);

    if (variable.equals("Duration") || variable.equals("DailyTimes")) {
      max = findMax();
      n = 10;
    }
    else if (variable.equals("StartTime")) {
      max = Constants.MINUTES_PER_DAY;
      n = 30;
    }
    else if (variable.equals("StartTimeBinned")) {
      max = Constants.MINUTES_PER_DAY / Constants.MINUTES_PER_BIN;
      n = 30;
    }

    interval = (int) (max / n);
    int median = (int) (interval / 2);

    // Initial mixture model
    MixtureModel mm = new MixtureModel(n);
    mm.EF = new UnivariateGaussian();
    for (int i = 0; i < n; i++) {
      PVector param = new PVector(2);
      param.array[0] = interval * i + median;
      param.array[1] = param.array[0] / 10;
      mm.param[i] = param;
      mm.weight[i] = i + 1;
    }
    mm.normalizeWeights();
    // System.out.println("Initial mixture model \n" + mm + "\n");

    double[] whatever = new double[1];

    PVector[] points = mm.drawRandomPoints(temp.length);

    for (int i = 0; i < temp.length; i++) {

      whatever[0] = temp[i];
      points[i].setArray(whatever.clone());

    }

    Vector<PVector>[] clusters = KMeans.run(points, n);

    // Classical EM
    MixtureModel mmc;
    mmc = ExpectationMaximization1D.initialize(clusters);
    // System.out.println("Mixture model initial state \n" + mmc + "\n");
    mmc = ExpectationMaximization1D.run(points, mmc);

    // System.out.println("Mixture model estimated using classical EM \n" +
    // mmc
    // + "\n");
    //
    GMM2File(mmc, output);

  }

  public void createNormal (String input, String output, String variable)
    throws FileNotFoundException
  {

    temp = readFile(input);
    n = 1;

    if (variable.equals("Duration") || variable.equals("DailyTimes"))
      max = findMax();
    else if (variable.equals("StartTime"))
      max = Constants.MINUTES_PER_DAY;
    else if (variable.equals("StartTimeBinned"))
      max = Constants.MINUTES_PER_DAY / Constants.MINUTES_PER_BIN;

    interval = (int) (max / n);

    int median = (int) (interval / 2);

    // Initial mixture model
    MixtureModel mm = new MixtureModel(n);
    mm.EF = new UnivariateGaussian();
    for (int i = 0; i < n; i++) {
      PVector param = new PVector(2);
      param.array[0] = interval * i + median;
      param.array[1] = param.array[0] / 10;
      mm.param[i] = param;
      mm.weight[i] = i + 1;
    }
    mm.normalizeWeights();
    // System.out.println("Initial mixture model \n" + mm + "\n");

    double[] whatever = new double[1];

    PVector[] points = mm.drawRandomPoints(temp.length);

    for (int i = 0; i < temp.length; i++) {

      whatever[0] = temp[i];
      points[i].setArray(whatever.clone());

    }

    Vector<PVector>[] clusters = KMeans.run(points, n);

    // Classical EM
    MixtureModel mmc;
    mmc = ExpectationMaximization1D.initialize(clusters);
    // System.out.println("Mixture model initial state \n" + mmc + "\n");
    mmc = ExpectationMaximization1D.run(points, mmc);

    // System.out.println("Mixture model estimated using classical EM \n" +
    // mmc
    // + "\n");

    Gaussian2File(mmc, output);

  }

  private Integer[] readFile (String filename) throws FileNotFoundException
  {
    ArrayList<Integer> temp = new ArrayList<Integer>();

    File file = new File(filename);
    Scanner input = new Scanner(file);
    String nextLine;

    while (input.hasNext()) {
      nextLine = input.nextLine();
      temp.add(Integer.parseInt(nextLine));
    }

    input.close();

    Integer[] result = new Integer[temp.size()];

    result = temp.toArray(result);
    /*
     * for (int i = 0; i < result.length; i++) System.out.println("Index " +
     * i + ": " + result[i]);
     */
    return result;
  }

  private void GMM2File (MixtureModel mm, String filename)
    throws FileNotFoundException
  {

    String line;

    int n = mm.size;
    double[] weights = mm.weight;
    Double[] means = new Double[n];
    Double[] sigmas = new Double[n];

    for (int i = 0; i < n; i++) {

      line = mm.param[i].toString().substring(3, 15);
      line = line.replace(" ", "");
      means[i] = Double.parseDouble(line);

      line =
        mm.param[i].toString().substring(16,
                                         mm.param[i].toString().length() - 1);
      line = line.replace(" ", "");

      sigmas[i] = Double.parseDouble(line);

    }

    DecimalFormat df = new DecimalFormat("#.##########");
    PrintStream realSystemOut = System.out;

    OutputStream output = new FileOutputStream(filename);
    PrintStream printOut = new PrintStream(output);
    System.setOut(printOut);

    System.out.println("Max Value:" + max);

    System.out.println(n);

    for (int i = 0; i < n; i++) {

      System.out.print(df.format(weights[i]));
      if (i != n - 1)
        System.out.print("-");
      else
        System.out.println();
    }

    for (int i = 0; i < n; i++) {

      System.out.print(df.format(means[i]));
      if (i != n - 1)
        System.out.print("-");
      else
        System.out.println();

    }

    for (int i = 0; i < n; i++) {

      if (sigmas[i] < 0.00000001) {
        sigmas[i] = 0.00000001;
      }

      System.out.print(df.format(sigmas[i]));
      if (i != n - 1)
        System.out.print("-");
      else
        System.out.println();

    }

    System.setOut(realSystemOut);

  }

  private void Gaussian2File (MixtureModel mm, String filename)
    throws FileNotFoundException
  {

    String line;

    int n = mm.size;
    Double[] means = new Double[n];
    Double[] sigmas = new Double[n];

    for (int i = 0; i < n; i++) {

      line = mm.param[i].toString().substring(3, 15);
      line = line.replace(" ", "");
      means[i] = Double.parseDouble(line);

      line =
        mm.param[i].toString().substring(16,
                                         mm.param[i].toString().length() - 1);
      line = line.replace(" ", "");

      sigmas[i] = Double.parseDouble(line);

    }

    DecimalFormat df = new DecimalFormat("#.##########");
    PrintStream realSystemOut = System.out;

    OutputStream output = new FileOutputStream(filename);
    PrintStream printOut = new PrintStream(output);
    System.setOut(printOut);

    System.out.println("Max Value:" + max);

    for (int i = 0; i < n; i++) {

      System.out.print(df.format(means[i]));
      if (i != n - 1)
        System.out.print("-");
      else
        System.out.println();

    }

    for (int i = 0; i < n; i++) {

      System.out.print(df.format(sigmas[i]));
      if (i != n - 1)
        System.out.print("-");
      else
        System.out.println();

    }

    System.setOut(realSystemOut);

  }

  public int findMax ()
  {

    int maxValue = -1;

    for (int i = 0; i < temp.length; i++) {
      maxValue = Math.max(temp[i], maxValue);
    }

    return maxValue;
  }

}
