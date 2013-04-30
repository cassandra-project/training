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
import java.util.Arrays;

import eu.cassandra.training.utils.ChartUtils;
import eu.cassandra.training.utils.Constants;

public class ValleyFinder
{

  double[] loadVector;
  double[] intervalVector;

  ArrayList<Valley> valleys;
  ArrayList<Valley> valleysInterval;

  public ValleyFinder (double[] loadVector, boolean equal)
  {

    this.loadVector = loadVector;

    valleys = new ArrayList<Valley>();
    valleysInterval = new ArrayList<Valley>();
    intervalVector = createIntervalVector();

    findValleys();
    findIntervalValleys();

  }

  public ArrayList<Valley> getValleys ()
  {
    return valleys;
  }

  public ArrayList<Valley> getValleysInterval ()
  {
    return valleysInterval;
  }

  public double[] getLoadVector ()
  {
    return loadVector;
  }

  public double[] getIntervalVector ()
  {
    return intervalVector;
  }

  public Valley findClosestValley (int index, int offset)
  {

    if (valleys.size() == 0) {
      System.out.println("No valleys found");
      return null;
    }

    int closestIndex = 0;
    double closestDistance = Double.POSITIVE_INFINITY;

    for (int i = 0; i < valleys.size(); i++) {

      if (valleys.get(i).getIndexMinuteStart() > index) {
        int startDistance =
          Math.abs(index - valleys.get(i).getIndexMinuteStart());
        if (closestDistance > startDistance && startDistance > offset
            && valleys.get(i).getDuration() >= offset) {
          closestDistance = startDistance;
          closestIndex = i;
        }
      }
      else {
        int endDistance = Math.abs(index - valleys.get(i).getIndexMinuteEnd());

        if (closestDistance > endDistance && endDistance > offset
            && valleys.get(i).getDuration() >= offset) {
          closestDistance = endDistance;
          closestIndex = i;
        }
      }

    }

    System.out.println("Closest Index: " + closestIndex);

    return valleys.get(closestIndex);

  }

  public Valley findLowerValley ()
  {

    if (valleys.size() == 0) {
      System.out.println("No valleys found");
      return null;
    }

    int lowerIndex = 0;

    for (int i = 0; i < valleys.size(); i++) {

      if (valleys.get(lowerIndex).getMean() > valleys.get(i).getMean())
        lowerIndex = i;

    }

    return valleys.get(lowerIndex);

  }

  public Valley findWiderValley ()
  {

    if (valleys.size() == 0) {
      System.out.println("No valleys found");
      return null;
    }

    int maxIndex = -1;
    int maxDuration = 0;

    for (int i = 0; i < valleys.size(); i++) {

      if (maxDuration < valleys.get(i).getDuration()) {
        maxDuration = valleys.get(i).getDuration();
        maxIndex = i;
      }

    }

    return valleys.get(maxIndex);

  }

  public Valley findClosestValleyInterval (int index, int offset)
  {

    if (valleysInterval.size() == 0) {
      System.out.println("No valleys found");
      return null;
    }

    int closestIndex = 0;
    double closestDistance = Double.POSITIVE_INFINITY;

    for (int i = 0; i < valleysInterval.size(); i++) {

      if (valleysInterval.get(i).getIndexMinuteStart() > index) {
        int startDistance =
          Math.abs(index - valleysInterval.get(i).getIndexMinuteStart());
        if (closestDistance > startDistance && startDistance > offset
            && valleysInterval.get(i).getDuration() >= offset) {
          closestDistance = startDistance;
          closestIndex = i;
        }
      }
      else {
        int endDistance =
          Math.abs(index - valleysInterval.get(i).getIndexMinuteEnd());

        if (closestDistance > endDistance && endDistance > offset
            && valleysInterval.get(i).getDuration() >= offset) {
          closestDistance = endDistance;
          closestIndex = i;
        }
      }

    }

    System.out.println("Closest Index: " + closestIndex);

    return valleysInterval.get(closestIndex);

  }

  public Valley findLowerValleyInterval ()
  {

    if (valleysInterval.size() == 0) {
      System.out.println("No valleys found");
      return null;
    }

    int lowerIndex = 0;

    for (int i = 0; i < valleysInterval.size(); i++) {

      if (valleysInterval.get(lowerIndex).getMean() > valleysInterval.get(i)
              .getMean())
        lowerIndex = i;

    }

    return valleysInterval.get(lowerIndex);

  }

  public Valley findWiderValleyInterval ()
  {

    if (valleysInterval.size() == 0) {
      System.out.println("No valleys found");
      return null;
    }

    int maxIndex = -1;
    int maxDuration = 0;

    for (int i = 0; i < valleysInterval.size(); i++) {

      if (maxDuration < valleysInterval.get(i).getDuration()) {
        maxDuration = valleysInterval.get(i).getDuration();
        maxIndex = i;
      }

    }

    return valleysInterval.get(maxIndex);

  }

  public void findValleys ()
  {

    double left = Double.POSITIVE_INFINITY;
    double mid = Double.POSITIVE_INFINITY;
    double right = Double.POSITIVE_INFINITY;
    int start = 0;
    int end = 0;
    boolean flagStarted = false;

    for (int i = 1; i < loadVector.length - 1; i++) {

      left = loadVector[i - 1];
      mid = loadVector[i];
      right = loadVector[i + 1];

      // System.out.println("Left: " + left + " Mid: " + mid + " Right: "
      // +
      // right);
      if ((flagStarted == false) && (mid < left)) {
        start = i;
        flagStarted = true;
      }

      if ((flagStarted == true) && (right > mid)) {
        end = i;
        // System.out.println("Start: " + start + " End: " + end);
        double[] temp = Arrays.copyOfRange(loadVector, start, end + 1);
        // System.out.println(Arrays.toString(temp));
        Valley valley = new Valley(start, end, temp);
        valleys.add(valley);
        flagStarted = false;
      }

    }

  }

  public void findIntervalValleys ()
  {

    double left = Double.POSITIVE_INFINITY;
    double mid = Double.POSITIVE_INFINITY;
    double right = Double.POSITIVE_INFINITY;
    int start = 0;
    int end = 0;
    boolean flagStarted = false;

    for (int i = 1; i < intervalVector.length - 1; i++) {

      left = intervalVector[i - 1];
      mid = intervalVector[i];
      right = intervalVector[i + 1];

      // System.out.println("Left: " + left + " Mid: " + mid + " Right: "
      // +
      // right);
      if ((flagStarted == false) && (mid < left)) {
        start = i;
        flagStarted = true;
      }

      if ((flagStarted == true) && (right > mid)) {
        end = i;
        // System.out.println("Start: " + start + " End: " + end);
        double[] temp = Arrays.copyOfRange(intervalVector, start, end + 1);
        // System.out.println(Arrays.toString(temp));
        Valley valley = new Valley(start, end, temp);
        valleysInterval.add(valley);
        flagStarted = false;
      }

    }

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

  public void showList ()
  {

    for (int i = 0; i < valleys.size(); i++) {

      System.out.print("Index: " + i + " ");
      System.out.println(valleys.get(i).toString());
    }

  }

  public void showListInterval ()
  {

    for (int i = 0; i < valleysInterval.size(); i++) {

      System.out.print("Index: " + i + " ");
      System.out.println(valleysInterval.get(i).toString());
    }

  }

}
