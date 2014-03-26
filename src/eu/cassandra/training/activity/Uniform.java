package eu.cassandra.training.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.training.response.Incentive;
import eu.cassandra.training.response.IncentiveVector;
import eu.cassandra.training.response.Pricing;
import eu.cassandra.training.response.PricingVector;
import eu.cassandra.training.utils.Constants;

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

/**
 * @author Antonios Chrysopoulos
 * @version prelim
 * @since 2012-28-06
 */
public class Uniform implements ProbabilityDistribution
{
  /**
   * The name of the Normal distribution.
   */
  private String name = "";

  /**
   * The type of the Normal distribution.
   */
  private String type = "";

  /**
   * A boolean variable that shows if the values of the Normal distribution
   * histogram
   * has been precomputed or not.
   */
  protected boolean precomputed;

  /**
   * A variable presenting the number of bins that are created for the histogram
   * containing the values of the Normal distribution.
   */
  protected int numberOfBins;

  /**
   * The starting point of the bins for the precomputed values.
   */
  protected double precomputeFrom;

  /**
   * The ending point of the bins for the precomputed values.
   */
  protected double precomputeTo;

  /**
   * An array containing the probabilities of each bin precomputed for the
   * Normal distribution.
   */
  protected double[] histogram;

  /**
   * This is an array that contains the probabilities that the distribution has
   * value over a threshold.
   */
  private double[] greaterProbability;

  /** The id of the distribution as given by the Cassandra server. */
  private String distributionID = "";

  /**
   * @param start
   *          Starting value of the Uniform distribution.
   * @param end
   *          Ending value of the Uniform distribution.
   */
  public Uniform (double start, double end)
  {
    name = "Generic";
    type = "Uniform Distribution";
    precomputeFrom = start;
    precomputeTo = end;
    precomputed = false;
    estimateGreaterProbability();
  }

  /**
   * @param start
   *          Starting value of the Uniform distribution.
   * @param end
   *          Ending value of the Uniform distribution.
   * @param startTime
   *          variable that shows if this is a start time distribution or not.
   */
  public Uniform (double start, double end, boolean startTime)
  {
    name = "Generic";
    type = "Uniform Distribution";
    precomputeFrom = start;
    precomputeTo = end;

    if (startTime)
      precompute((int) start, (int) end, 1440);
    else
      precompute((int) start, (int) end, (int) (end + 1));

    estimateGreaterProbability();
  }

  /**
   * Constructor of a Uniform distribution with parameters parsed from a file.
   * 
   * @param filename
   *          The file name of the input file.
   */
  public Uniform (String filename) throws FileNotFoundException
  {
    name = filename;
    type = "Uniform Distribution";
    File file = new File(filename);
    Scanner input = new Scanner(file);
    String nextLine = input.nextLine();
    // System.out.println(nextLine);
    String[] temp = nextLine.split(":");
    int startValue = Integer.parseInt(temp[1]);

    nextLine = input.nextLine();
    temp = nextLine.split(":");
    int endValue = Integer.parseInt(temp[1]);

    if (name.contains("StartTime"))
      precompute(startValue, endValue, 1440);
    else
      precompute(startValue, endValue, endValue + 1);

    input.close();
    estimateGreaterProbability();
  }

  public String getType ()
  {
    return "Uniform";
  }

  public String getDescription ()
  {
    String description = "Uniform probability density function";
    return description;
  }

  public int getNumberOfParameters ()
  {
    return 2;
  }

  public double getParameter (int index)
  {
    switch (index) {
    case 0:
      return precomputeFrom;
    case 1:
      return precomputeTo;
    default:
      return 0.0;
    }

  }

  public void setParameter (int index, double value)
  {
    switch (index) {
    case 0:
      precomputeFrom = value;
      break;
    case 1:
      precomputeTo = value;
      break;
    default:
      return;
    }
  }

  public double getProbability (double x)
  {
    if (x > precomputeTo || x < precomputeFrom) {
      return 0.0;
    }
    else {
      if (precomputeTo == precomputeFrom && x == precomputeTo)
        return 1.0;
      else
        return 1.0 / (double) (precomputeTo - precomputeFrom + 1);
    }
  }

  public double getPrecomputedProbability (double x)
  {
    if (!precomputed) {
      return -1;
    }
    else if (x > precomputeTo || x < precomputeFrom) {
      return -1;
    }
    return histogram[(int) (x - precomputeFrom)];
  }

  public int getPrecomputedBin ()
  {
    if (!precomputed) {
      return -1;
    }
    Random random = new Random();
    // double div = (precomputeTo - precomputeFrom) / (double) numberOfBins;
    double dice = random.nextDouble();
    double sum = 0;
    for (int i = 0; i < numberOfBins; i++) {
      sum += histogram[i];
      // if(dice < sum) return (int)(precomputeFrom + i * div);
      if (dice < sum)
        return i;
    }
    return -1;
  }

  public void precompute (int endValue)
  {
    precompute(0, endValue, endValue + 1);
  }

  public double[] getHistogram ()
  {
    if (precomputed == false) {
      System.out.println("Not computed yet!");
      return null;
    }

    return histogram;
  }

  @Override
  public double[] getGreaterProbability ()
  {
    return greaterProbability;
  }

  public void status ()
  {
    System.out.print("Uniform Distribution with ");
    System.out.println("Precomputed: " + precomputed);
    if (precomputed) {
      System.out.print("Number of Beans: " + numberOfBins);
      System.out.print(" Starting Point: " + precomputeFrom);
      System.out.println(" Ending Point: " + precomputeTo);
    }
    System.out.println();

  }

  public static void main (String[] args)
  {
    System.out.println("Testing Start Time Uniform Distribution Creation.");

    int start = (int) (1440 * Math.random());
    int end = (int) (start + (1440 - start) * Math.random());

    Uniform u = new Uniform(start, end);
    u.precompute(start, end, 1440);

    u.status();

    System.out.println("Testing Random Bins");
    for (int i = 0; i < 10; i++) {
      int temp = u.getPrecomputedBin();
      System.out.println("Random Bin: " + temp + " Possibility Value: "
                         + u.getPrecomputedProbability(temp));
    }

    System.out.println("Testing Duration Uniform Distribution Creation.");

    start = 10;
    end = 12;

    u = new Uniform(start, end);
    u.precompute(start, end, (int) end + 1);

    u.status();

    System.out.println("Testing Random Bins");
    for (int i = 0; i < 10; i++) {
      int temp = u.getPrecomputedBin();
      System.out.println("Random Bin: " + temp + " Possibility Value: "
                         + u.getPrecomputedProbability(temp));
    }

  }

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public double getProbability (int x)
  {
    if (x < 0)
      return 0;
    else
      return histogram[x];
  }

  @Override
  public double getProbabilityLess (int x)
  {
    return 1 - getProbabilityGreater(x);
  }

  @Override
  public double getProbabilityGreater (int x)
  {
    double prob = 0;

    int start = (int) x;

    for (int i = start + 1; i < histogram.length; i++)
      prob += histogram[i];

    return prob;
  }

  private void estimateGreaterProbability ()
  {
    greaterProbability = new double[histogram.length];

    for (int i = 0; i < histogram.length; i++)
      greaterProbability[i] = getProbabilityGreater(i);

  }

  @Override
  public double getPrecomputedProbability (int x)
  {
    if (!precomputed)
      return -1;
    else
      return histogram[x];

  }

  @Override
  public double[] movingAverage (double[] values, Incentive incentive,
                                 float awareness, float sensitivity)
  {
    // Initialize the auxiliary variables.
    int side = -1;
    int startIndex = incentive.getStartMinute();
    int endIndex = incentive.getEndMinute();
    double overDiff = 0, overDiffTemp = 0;
    double temp = 0;
    String type = "";
    // double sum = 0;

    // First, the incentive type is checked (penalty or reward) and then the
    // before and after values are checked to see how the residual percentage
    // will be distributed.
    if (incentive.isPenalty()) {

      if (incentive.getBeforeDifference() > 0
          && incentive.getAfterDifference() < 0)
        type = "Both";

      if (incentive.getBeforeDifference() > 0
          && incentive.getAfterDifference() >= 0)
        type = "Left";

      if (incentive.getBeforeDifference() <= 0
          && incentive.getAfterDifference() < 0)
        type = "Right";

      if (incentive.getBeforeDifference() < 0
          && incentive.getAfterDifference() > 0)
        type = "None";
    }
    else {

      if (incentive.getBeforeDifference() < 0
          && incentive.getAfterDifference() > 0)
        type = "Both";

      if (incentive.getBeforeDifference() < 0
          && incentive.getAfterDifference() <= 0)
        type = "Left";

      if (incentive.getBeforeDifference() >= 0
          && incentive.getAfterDifference() > 0)
        type = "Right";

      if (incentive.getBeforeDifference() > 0
          && incentive.getAfterDifference() < 0)
        type = "None";

    }

    // System.out.println("Penalty: " + incentive.isPenalty() + " Type: " +
    // type);

    if (!type.equalsIgnoreCase("None")) {
      // In case of penalty the residual percentage is moved out of the window
      // to close distance, either on one or both sides accordingly
      if (incentive.isPenalty()) {

        for (int i = startIndex; i < endIndex; i++) {

          temp = incentive.getBase() * values[i] / incentive.getPrice();
          // System.out.println("Temp = " + temp);
          overDiffTemp = (values[i] - temp) * awareness * sensitivity;
          overDiff += overDiffTemp;
          values[i] -= overDiffTemp;

          // overDiff += values[i] - temp;
          // values[i] = temp;

        }
        // System.out.println("Over Difference = " + overDiff);
        double additive = overDiff / Constants.SHIFTING_WINDOW_IN_MINUTES;

        switch (type) {

        case "Both":

          side = Constants.SHIFTING_WINDOW_IN_MINUTES / 2;

          for (int i = 0; i < side; i++) {

            int before = startIndex - i;
            if (before < 0)
              before += Constants.MINUTES_PER_DAY;
            int after = endIndex + i;
            if (after > Constants.MINUTES_PER_DAY - 1)
              after %= Constants.MINUTES_PER_DAY;

            values[before] += additive;
            values[after] += additive;

          }
          break;

        case "Left":

          side = Constants.SHIFTING_WINDOW_IN_MINUTES;

          for (int i = 0; i < side; i++) {

            int before = startIndex - i;
            if (before < 0)
              before += Constants.MINUTES_PER_DAY;
            values[before] += additive;

          }
          break;

        case "Right":

          side = Constants.SHIFTING_WINDOW_IN_MINUTES;

          for (int i = 0; i < side; i++) {

            int after = endIndex + i;
            if (after > Constants.MINUTES_PER_DAY - 1)
              after %= Constants.MINUTES_PER_DAY;

            values[after] += additive;
          }
        }
      }
      // In case of reward a percentage of the close distances are moved in the
      // window, either from one or both sides accordingly.
      else {
        side = Constants.SHIFTING_WINDOW_IN_MINUTES * 2;
        switch (type) {

        case "Both":

          for (int i = startIndex - side; i < startIndex; i++) {

            int index = i;

            if (index < 0)
              index += Constants.MINUTES_PER_DAY;

            temp = incentive.getPrice() * values[index] / incentive.getBase();
            overDiffTemp = (values[index] - temp) * awareness * sensitivity;
            overDiff += overDiffTemp;
            values[index] -= overDiffTemp;

            // overDiff += values[index] - temp;
            // values[index] = temp;
          }

          for (int i = endIndex; i < endIndex + side; i++) {

            int index = i;

            if (index > Constants.MINUTES_PER_DAY - 1)
              index %= Constants.MINUTES_PER_DAY;

            temp = incentive.getPrice() * values[index] / incentive.getBase();
            overDiffTemp = (values[index] - temp) * awareness * sensitivity;
            overDiff += overDiffTemp;
            values[index] -= overDiffTemp;

            // temp = incentive.getPrice() * values[index] /
            // incentive.getBase();
            // overDiff += values[index] - temp;
            // values[index] = temp;
          }
          break;

        case "Left":

          for (int i = startIndex - 2 * side; i < startIndex; i++) {

            int index = i;

            if (index < 0)
              index += Constants.MINUTES_PER_DAY;

            temp = incentive.getPrice() * values[index] / incentive.getBase();
            overDiffTemp = (values[index] - temp) * awareness * sensitivity;
            overDiff += overDiffTemp;
            values[index] -= overDiffTemp;

            // temp = incentive.getPrice() * values[index] /
            // incentive.getBase();
            // overDiff += values[index] - temp;
            // values[index] = temp;
          }
          break;

        case "Right":

          for (int i = endIndex; i < endIndex + 2 * side; i++) {

            int index = i;

            if (index > Constants.MINUTES_PER_DAY - 1)
              index %= Constants.MINUTES_PER_DAY;

            temp = incentive.getPrice() * values[index] / incentive.getBase();
            overDiffTemp = (values[index] - temp) * awareness * sensitivity;
            overDiff += overDiffTemp;
            values[index] -= overDiffTemp;

            // temp = incentive.getPrice() * values[index] /
            // incentive.getBase();
            // overDiff += values[index] - temp;
            // values[index] = temp;
          }

        }
        // System.out.println("Over Difference = " + overDiff);

        double additive = overDiff / (endIndex - startIndex);

        for (int i = startIndex; i < endIndex; i++)
          values[i] += additive;

      }

    }
    // for (int i = 0; i < values.length; i++)
    // sum += values[i];
    // System.out.println("Summary: " + sum);

    return values;
  }

  @Override
  public double[] discreteOptimal (double[] values, PricingVector pricing,
                                   float awareness, float sensitivity)
  {
    // Initialize the auxiliary variables.
    double temp = 0, additive = 0, overDiff = 0, overDiffTemp = 0, sum = 0;
    Pricing tempPricing;
    // double sum = 0;

    int start, start2, end, end2, duration;
    double previousPrice, newPrice;

    if (pricing.getNumberOfPenalties() > 0) {

      Map<Integer, Double> percentageMap = new TreeMap<Integer, Double>();
      ArrayList<Integer> tempList = new ArrayList<Integer>(pricing.getBases());
      tempList.addAll(pricing.getRewards());

      for (Integer index: tempList)
        sum += pricing.getPricings(index).getGainRatio();

      for (Integer index: tempList)
        percentageMap.put(index, pricing.getPricings(index).getGainRatio()
                                 / sum);

      // System.out.println("Percentage Map: " + percentageMap.toString());

      for (Integer index: pricing.getPenalties()) {
        overDiff = 0;
        sum = 0;

        tempPricing = pricing.getPricings(index);
        start = tempPricing.getStartMinute();
        end = tempPricing.getEndMinute();
        previousPrice = tempPricing.getPreviousPrice();
        newPrice = tempPricing.getCurrentPrice();

        for (int i = start; i <= end; i++) {

          temp = ((previousPrice * values[i]) / newPrice);
          // System.out.println("Temp = " + temp);
          overDiffTemp = (values[i] - temp) * awareness * sensitivity;
          overDiff += overDiffTemp;
          values[i] -= overDiffTemp;
        }

        // System.out.println("OverDiff for index " + index + ": " + overDiff);

        for (Integer index2: tempList) {
          start2 = pricing.getPricings(index2).getStartMinute();
          end2 = pricing.getPricings(index2).getEndMinute();
          duration = end2 - start2;
          additive = overDiff * percentageMap.get(index2) / duration;
          // System.out.println("Additive for index " + index2 + ": " +
          // additive);
          for (int i = start2; i < end2; i++)
            values[i] += additive;
        }

        for (int i = 0; i < values.length; i++)
          sum += values[i];

        // System.out.println("Summary: " + sum);

      }

    }
    else if (pricing.getNumberOfRewards() > 0) {

      Pricing tempPricing2 = null;
      ArrayList<Pricing> tempList = new ArrayList<Pricing>();

      for (Integer index: pricing.getRewards())
        tempList.add(pricing.getPricings(index));

      Collections.sort(tempList, comp);

      // System.out.println("Rewards List: " + tempList.toString());

      for (int i = 0; i < tempList.size(); i++) {

        tempPricing2 = tempList.get(i);
        newPrice = tempPricing2.getCurrentPrice();
        start2 = tempPricing2.getStartMinute();
        end2 = tempPricing2.getEndMinute();
        duration = end2 - start2;

        for (Integer index: pricing.getBases()) {
          overDiff = 0;
          sum = 0;

          tempPricing = pricing.getPricings(index);
          start = tempPricing.getStartMinute();
          end = tempPricing.getEndMinute();
          previousPrice = tempPricing.getCurrentPrice();

          for (int j = start; j <= end; j++) {

            temp = newPrice * values[j] / previousPrice;
            overDiffTemp = (values[j] - temp) * awareness * sensitivity;
            overDiff += overDiffTemp;
            values[j] -= overDiffTemp;

          }

          // System.out.println("OverDiff for index " + index + ": " +
          // overDiff);

          additive = overDiff / duration;
          // System.out.println("Additive for index " + i + ": " + additive);

          for (int j = start2; j < end2; j++)
            values[j] += additive;
        }

        for (int j = 0; j < values.length; j++)
          sum += values[j];

        // System.out.println("Summary: " + sum);

      }

    }

    return values;
  }

  @Override
  public double[] discreteAverage (double[] values, PricingVector pricing,
                                   float awareness, float sensitivity)
  {

    // Initialize the auxiliary variables.
    double temp = 0;
    double sum = 0;
    double overDiff = 0, overDiffTemp = 0;
    int start, end;
    double newPrice;
    int durationCheapest = 0;

    // Finding the cheapest window in the day.
    ArrayList<Integer> cheapest = pricing.getCheapest();

    for (Integer index: cheapest) {
      int startCheapest = pricing.getPricings(index).getStartMinute();
      int endCheapest = pricing.getPricings(index).getEndMinute();
      durationCheapest += endCheapest - startCheapest;
    }

    double cheapestPrice =
      pricing.getPricings(pricing.getCheapest().get(0)).getCurrentPrice();

    // Moving from all the available vectors residual percentages to the
    // cheapest one.
    for (int i = 0; i < pricing.getPricings().size(); i++) {

      if (cheapest.contains(i) == false) {
        sum = 0;
        overDiff = 0;
        start = pricing.getPricings(i).getStartMinute();
        end = pricing.getPricings(i).getEndMinute();
        newPrice = pricing.getPricings(i).getCurrentPrice();

        for (int j = start; j <= end; j++) {

          temp = cheapestPrice * values[j] / newPrice;
          overDiffTemp = (values[j] - temp) * awareness * sensitivity;
          overDiff += overDiffTemp;
          values[j] -= overDiffTemp;

        }

        double additive = overDiff / durationCheapest;

        for (Integer index: cheapest) {
          int startCheapest = pricing.getPricings(index).getStartMinute();
          int endCheapest = pricing.getPricings(index).getEndMinute();

          for (int j = startCheapest; j <= endCheapest; j++)
            values[j] += additive;

        }

        for (int j = 0; j < values.length; j++)
          sum += values[j];
        System.out.println("Summary after index " + i + ": " + sum);

      }

    }

    return values;
  }

  @Override
  public void shifting (int shiftingCase, double[] basicScheme,
                        double[] newScheme, float awareness, float sensitivity)
  {

    if (shiftingCase == 0) {

      histogram =
        shiftingOptimal(basicScheme, newScheme, awareness, sensitivity);
    }
    else if (shiftingCase == 1) {

      histogram =
        shiftingNormal(basicScheme, newScheme, awareness, sensitivity);
    }
    else if (shiftingCase == 2) {

      histogram =
        shiftingDiscrete(basicScheme, newScheme, awareness, sensitivity);
    }
    else {
      System.out.println("ERROR in shifting function");
    }

  }

  @Override
  public double[] shiftingPreview (int shiftingCase, double[] basicScheme,
                                   double[] newScheme, float awareness,
                                   float sensitivity)
  {

    double[] result = new double[Constants.MINUTES_PER_DAY];

    if (shiftingCase == 0) {

      result = shiftingOptimal(basicScheme, newScheme, awareness, sensitivity);
    }
    else if (shiftingCase == 1) {

      result = shiftingNormal(basicScheme, newScheme, awareness, sensitivity);
    }
    else if (shiftingCase == 2) {

      result = shiftingDiscrete(basicScheme, newScheme, awareness, sensitivity);
    }
    else {
      System.out.println("ERROR in shifting function");
    }

    return result;

  }

  @Override
  public void shiftingDaily (double energyRatio, float awareness,
                             float sensitivity)
  {
    histogram = shiftingDailyPreview(energyRatio, awareness, sensitivity);
  }

  @Override
  public double[] shiftingDailyPreview (double energyRatio, float awareness,
                                        float sensitivity)
  {
    double[] temp = Arrays.copyOf(histogram, histogram.length);

    double diff = (energyRatio - 1) * (awareness * sensitivity);

    System.out.println("Diff: " + diff);

    double[] result;
    if (diff > 0) {

      if (diff > 1) {
        result = new double[1];
        result[0] = 1;
      }
      else
        result = reduceUse(temp, diff);

    }
    else
      result = increaseUse(temp, Math.abs(diff));

    System.out.println("Final:" + Arrays.toString(result));

    return result;

  }

  private double[] reduceUse (double[] result, double diff)
  {
    int index = result.length - 1;
    double diffTemp = diff;
    double sum = 0;

    System.out.println("Before:" + Arrays.toString(result));

    while (diffTemp > 0) {

      double reduction = Math.min(result[index], diffTemp);

      result[index] -= reduction;
      diffTemp -= reduction;

      System.out.println("Index: " + index + " Reduction: " + reduction
                         + " DiffTemp: " + diffTemp);

      index--;

    }

    // Fixes out of bounds error
    index = Math.max(0, index);

    System.out.println("After:" + Arrays.toString(result));

    for (int i = 0; i <= index; i++)
      sum += result[i];

    for (int i = 0; i <= index; i++)
      result[i] += (result[i] / sum) * diff;

    sum = 0;

    for (int i = 0; i < result.length; i++)
      sum += result[i];

    System.out.println("After Normalization:" + Arrays.toString(result));

    System.out.println("Summary: " + sum);

    index = result.length - 1;

    while (result[index] == 0)
      index--;

    return Arrays.copyOfRange(result, 0, index + 1);

  }

  private double[] increaseUse (double[] result, double diff)
  {
    int index = 0;
    double diffTemp = diff;
    double sum = 0;

    System.out.println("Before:" + Arrays.toString(result));

    while (diffTemp > 0) {

      double reduction = Math.min(result[index], diffTemp);

      result[index] -= reduction;
      diffTemp -= reduction;

      System.out.println("Index: " + index + " Reduction: " + reduction
                         + " DiffTemp: " + diffTemp);

      index++;

    }

    index = Math.min(index, result.length - 1);

    System.out.println("After:" + Arrays.toString(result));

    for (int i = index; i < result.length; i++)
      sum += result[i];

    for (int i = index; i < result.length; i++)
      result[i] += (result[i] / sum) * diff;

    sum = 0;

    for (int i = 0; i < result.length; i++)
      sum += result[i];

    System.out.println("After Normalization:" + Arrays.toString(result));

    System.out.println("Summary: " + sum);

    return Arrays.copyOf(result, result.length);
  }

  @Override
  public double[] shiftingOptimal (double[] basicScheme, double[] newScheme,
                                   float awareness, float sensitivity)
  {

    double[] result = Arrays.copyOf(this.histogram, this.histogram.length);

    PricingVector pricingVector = new PricingVector(basicScheme, newScheme);

    if (pricingVector.getPricings().size() > 1)
      result = discreteOptimal(result, pricingVector, awareness, sensitivity);

    return result;

  }

  @Override
  public double[] shiftingNormal (double[] basicScheme, double[] newScheme,
                                  float awareness, float sensitivity)
  {
    double[] result = Arrays.copyOf(this.histogram, this.histogram.length);

    PricingVector pricingVector = new PricingVector(basicScheme, newScheme);

    IncentiveVector inc = new IncentiveVector(basicScheme, newScheme);

    if (pricingVector.getPricings().size() > 1)
      for (Incentive incentive: inc.getIncentives())
        result = movingAverage(result, incentive, awareness, sensitivity);

    return result;

  }

  @Override
  public double[] shiftingDiscrete (double[] basicScheme, double[] newScheme,
                                    float awareness, float sensitivity)
  {
    double[] result = Arrays.copyOf(this.histogram, this.histogram.length);

    PricingVector pricingVector = new PricingVector(basicScheme, newScheme);

    if (pricingVector.getPricings().size() > 1)
      result = discreteAverage(result, pricingVector, awareness, sensitivity);

    return result;
  }

  @Override
  public DBObject toJSON (String activityModelID)
  {

    double[] values = histogram;
    DBObject temp = new BasicDBObject();
    DBObject[] param = new BasicDBObject[1];

    param[0] = new BasicDBObject();

    temp.put("name", name);
    temp.put("type", type);
    temp.put("description", name + " " + type);
    temp.put("distrType", type);
    temp.put("actmod_id", activityModelID);
    temp.put("parameters", param);
    temp.put("values", values);

    return temp;

  }

  @Override
  public void precompute (int startValue, int endValue, int nBins)
  {
    // TODO Auto-generated method stub
    numberOfBins = nBins;
    histogram = new double[nBins];

    if (startValue > endValue) {
      System.out.println("Starting point greater than ending point");
      return;
    }
    else {

      for (int i = startValue; i <= endValue; i++) {

        histogram[i] = 1.0 / (double) (precomputeTo - precomputeFrom + 1);

      }
    }
    precomputed = true;

    System.out.println(Arrays.toString(histogram));
  }

  @Override
  public String getDistributionID ()
  {
    return distributionID;
  }

  @Override
  public void setDistributionID (String id)
  {
    distributionID = id;
  }
}
