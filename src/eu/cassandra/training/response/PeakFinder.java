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
package eu.cassandra.training.response;

import java.util.ArrayList;
import java.util.List;

import eu.cassandra.training.utils.ChartUtils;
import eu.cassandra.training.utils.Constants;

public class PeakFinder
{

  double[] loadVector;
  double[] intervalVector;

  ArrayList<Peak> localMaxima;
  ArrayList<Peak> localIntervalMaxima;

  public PeakFinder (double[] loadVector)
  {

    this.loadVector = loadVector;

    localMaxima = new ArrayList<Peak>();
    localIntervalMaxima = new ArrayList<Peak>();

    findLocalMaxima();
    findLocalIntervalMaxima();

  }

  public ArrayList<Peak> getLocalMaxima ()
  {
    return localMaxima;
  }

  public List<Peak> getLocalMaxima (int size)
  {
    List<Peak> result = localMaxima.subList(0, size);
    return result;
  }

  public ArrayList<Peak> getLocalIntervalMaxima ()
  {
    return localIntervalMaxima;
  }

  public List<Peak> getLocalIntervalMaxima (int size)
  {
    List<Peak> result = localIntervalMaxima.subList(0, size);
    return result;
  }

  public Peak findGlobalMaximum ()
  {

    if (localMaxima.size() == 0) {
      System.out.println("No peak found");
      Peak temp = new Peak(-1, 0);
      return temp;
    }

    return localMaxima.get(0);

  }

  public Peak findGlobalIntervalMaximum ()
  {

    if (localIntervalMaxima.size() == 0) {
      System.out.println("No peak found");
      Peak temp = new Peak(-1, 0);
      return temp;
    }

    return localIntervalMaxima.get(0);

  }

  public void findLocalMaxima ()
  {

    double left = Double.NEGATIVE_INFINITY;
    double mid = Double.NEGATIVE_INFINITY;
    double right = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < loadVector.length - 1; i++) {

      left = mid;
      mid = right;
      right = loadVector[i];

      if ((right < mid) && (mid > left))
        addLocalMaximum(new Peak(i - 1, loadVector[i - 1]));
    }

    localMaxima.toString();

  }

  public void findLocalIntervalMaxima ()
  {

    intervalVector = createIntervalVector();

    double left = Double.NEGATIVE_INFINITY;
    double mid = Double.NEGATIVE_INFINITY;
    double right = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < intervalVector.length - 1; i++) {

      left = mid;
      mid = right;
      right = intervalVector[i];

      if ((right < mid) && (mid > left))
        addLocalIntervalMaximum(new Peak(i - 1, intervalVector[i - 1]));
    }

    localIntervalMaxima.toString();

  }

  public Peak findGlobalIntervalMaxima (int lowerLimit, int upperLimit)
  {

    for (int i = 0; i < localIntervalMaxima.size(); i++)
      if (localIntervalMaxima.get(i).getIndexMinute() >= lowerLimit
          && localIntervalMaxima.get(i).getIndexMinute() < upperLimit)
        return localIntervalMaxima.get(i);

    System.out.println("No peak found in the interval " + lowerLimit + " - "
                       + upperLimit);
    Peak temp = new Peak(-1, 0);
    return temp;
  }

  private double[] createIntervalVector ()
  {
    double[] newVector =
      new double[(int) (loadVector.length / Constants.MINUTES_PER_BIN)];

    for (int i = 0; i < newVector.length; i++) {

      for (int j = 0; j < Constants.MINUTES_PER_BIN; j++) {

        newVector[i] += loadVector[i * Constants.MINUTES_PER_BIN + j];

      }

      // System.out.println("Index: " + i + " Vector Value: " +
      // newVector[i]);

    }

    ChartUtils.createHistogram("Consumer Interval", "minutes", "Consumption",
                               newVector);

    return newVector;

  }

  private void addLocalMaximum (Peak local)
  {

    int i = 0;

    if (localMaxima.size() == 0)
      localMaxima.add(local);
    else {

      for (i = 0; i < localMaxima.size(); i++) {
        if (localMaxima.get(i).getValue() < local.getValue()) {
          localMaxima.add(i, local);
          break;
        }
      }

      if (i == localMaxima.size())
        localMaxima.add(local);
    }

  }

  private void addLocalIntervalMaximum (Peak local)
  {

    int i = 0;
    // System.out.println("Adding new local Maxima " + local.toString());

    if (localIntervalMaxima.size() == 0)
      localIntervalMaxima.add(local);
    else {

      for (i = 0; i < localIntervalMaxima.size(); i++) {
        if (localIntervalMaxima.get(i).getValue() < local.getValue()) {
          localIntervalMaxima.add(i, local);
          break;
        }
      }

      if (i == localIntervalMaxima.size())
        localIntervalMaxima.add(local);
    }

  }

  public List<Peak> partLocalMaxima (int size)
  {

    return localMaxima.subList(0, size - 1);

  }

  public List<Peak> partLocalIntervalMaxima (int size)
  {

    return localIntervalMaxima.subList(0, size - 1);

  }

  public void showList ()
  {

    for (int i = 0; i < localMaxima.size(); i++) {

      System.out.print("Index: " + i + " ");
      System.out.println(localMaxima.get(i).toString());
    }

  }

  public void showListInterval ()
  {

    for (int i = 0; i < localIntervalMaxima.size(); i++) {

      System.out.print("Index: " + i + " ");
      System.out.println(localIntervalMaxima.get(i).toString());
    }

  }

}
