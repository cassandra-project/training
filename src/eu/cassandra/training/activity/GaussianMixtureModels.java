/*   
   Copyright 2011-2012 The Cassandra Consortium (cassandra-fp7.eu)

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

package eu.cassandra.training.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.training.response.Incentive;
import eu.cassandra.training.response.IncentiveVector;
import eu.cassandra.training.response.PricingVector;
import eu.cassandra.training.utils.Constants;

/**
 * This class is used for implementing a Gaussian Mixture Models distribution to
 * use in the activity models that are created in the Training Module of
 * Cassandra Project. The same class has been used, with small alterations in
 * the main Cassandra Platform.
 * 
 * @author Christos Diou, Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class GaussianMixtureModels implements ProbabilityDistribution
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
   * This variable is an array of the weights of the gaussian distributions that
   * are comprising the mixture.
   */
  protected double[] pi;

  /**
   * This variable is an array of the Gaussian Distributions that are
   * comprising the mixture.
   */
  protected Gaussian[] gaussians;

  /**
   * A boolean variable that shows if the values of the Normal distribution
   * histogram has been precomputed or not.
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

  /** The id of the distribution as given by the Cassandra server. */
  private String distributionID = "";

  /**
   * Constructor. Create a set of n Gaussian Distributions and sets the
   * parameters of the each distribution, with mean 0 and standard
   * deviation 1.
   * 
   * @param n
   *          The number of Gaussian Distributions
   */
  public GaussianMixtureModels (int n)
  {
    name = "Generic Mixture";
    type = "Gaussian Mixture Models";
    pi = new double[n];
    for (int i = 0; i < n; i++) {
      pi[i] = (1.0 / n);
      gaussians[i] = new Gaussian();
    }
    precomputed = false;
  }

  /**
   * Constructor. Create a set of n Gaussian Distributions and sets the
   * parameters of the each distribution using the given arrays of values.
   * 
   * @param n
   *          The number of Gaussian Distributions.
   * @param pi
   *          An array containing the weights of the mixture.
   * @param mu
   *          An array containing the mean values of the mixture.
   * @param s
   *          An array containing the standard deviation values of the mixture.
   */
  public GaussianMixtureModels (int n, double[] pi, double[] mu, double[] s)
  {
    name = "Generic Mixture";
    type = "Gaussian Mixture Models";
    gaussians = new Gaussian[n];
    for (int i = 0; i < n; i++) {
      this.pi = pi;
      gaussians[i] = new Gaussian(mu[i], s[i]);
    }
    precomputed = false;
  }

  /**
   * Constructor of a Gaussian Mixture distribution with parameters parsed from
   * a file.
   * 
   * @param filename
   *          The file name of the input file.
   */
  public GaussianMixtureModels (String filename) throws FileNotFoundException
  {
    name = filename;
    type = "Gaussian Mixture Models";
    File file = new File(filename);
    Scanner input = new Scanner(file);
    String nextLine = input.nextLine();

    String[] temp = nextLine.split(":");
    int maxValue = Integer.parseInt(temp[1]);

    // Read the number of mixtures
    nextLine = input.nextLine();
    int n = Integer.parseInt(nextLine);
    String[] line = new String[n];

    // Read the percentage of presence
    nextLine = input.nextLine();
    line = nextLine.split("-");
    pi = new double[n];
    for (int i = 0; i < n; i++) {

      pi[i] = Double.parseDouble(line[i].replace(",", "."));

    }

    // Read the means
    nextLine = input.nextLine();
    line = nextLine.split("-");
    double[] mu = new double[n];
    for (int i = 0; i < n; i++) {

      mu[i] = Double.parseDouble(line[i].replace(",", "."));

    }

    // Read the sigmas
    nextLine = input.nextLine();
    line = nextLine.split("-");
    double[] s = new double[n];
    for (int i = 0; i < n; i++) {

      s[i] = Double.parseDouble(line[i].replace(",", "."));

    }

    gaussians = new Gaussian[n];
    for (int i = 0; i < n; i++) {
      gaussians[i] = new Gaussian(mu[i], s[i]);
    }

    precompute(0, maxValue, maxValue);

    // status();

    input.close();

  }

  @Override
  public String getName ()
  {
    return name;
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

  @Override
  public String getDescription ()
  {
    String description = "Gaussian Mixture Models probability density function";
    return description;
  }

  @Override
  public int getNumberOfParameters ()
  {
    return 3 * pi.length;
  }

  @Override
  public void precompute (int startValue, int endValue, int nBins)
  {
    if (startValue >= endValue) {
      System.out.println("The end point is before the start point.");
      return;
    }
    precomputeFrom = startValue;
    precomputeTo = endValue;
    numberOfBins = nBins;
    histogram = new double[nBins];

    for (int i = 0; i < gaussians.length; i++) {
      gaussians[i].precompute(startValue, endValue, nBins);
    }

    for (int i = 0; i < nBins; i++) {

      for (int j = 0; j < gaussians.length; j++) {
        histogram[i] += pi[j] * gaussians[j].getPrecomputedProbability(i);
      }

    }

    precomputed = true;
  }

  @Override
  public double getProbability (int x)
  {
    double sum = 0;
    for (int j = 0; j < pi.length; j++) {
      sum += pi[j] * gaussians[j].getProbability(x);
    }
    return sum;
  }

  @Override
  public double getParameter (int index)
  {
    return 0;
  }

  @Override
  public void setParameter (int index, double value)
  {
  }

  @Override
  public double getPrecomputedProbability (int x)
  {
    if (!precomputed) {
      return -1;
    }
    double div = (precomputeTo - precomputeFrom) / (double) numberOfBins;
    int bin = (int) Math.floor((x - precomputeFrom) / div);
    if (bin == numberOfBins) {
      bin--;
    }
    return histogram[bin];
  }

  @Override
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

  @Override
  public double[] getHistogram ()
  {
    return histogram;
  }

  @Override
  public void status ()
  {

    System.out.print("Gaussian Mixture with");
    System.out.println(" Number of Mixtures:" + pi.length);
    for (int i = 0; i < pi.length; i++) {
      System.out.print("Mixture " + i);
      System.out.print(" Mean: " + gaussians[i].getParameter(0));
      System.out.print(" Sigma: " + gaussians[i].getParameter(1));
      System.out.print(" Weight: " + pi[i]);
      System.out.println();
    }
    System.out.println("Precomputed: " + precomputed);
    if (precomputed) {
      System.out.print("Number of Beans: " + numberOfBins);
      System.out.print(" Starting Point: " + precomputeFrom);
      System.out.println(" Ending Point: " + precomputeTo);
    }
    System.out.println(Arrays.toString(histogram));
  }

  @Override
  public double getProbabilityGreaterEqual (int x)
  {
    double prob = 0;

    int start = (int) x;

    for (int i = start; i < histogram.length; i++)
      prob += histogram[i];

    return prob;
  }

  @Override
  public double getProbabilityLess (int x)
  {
    return 1 - getProbabilityGreaterEqual(x);
  }

  @Override
  public void shifting (int shiftingCase, double[] basicScheme,
                        double[] newScheme)
  {

    histogram = shiftingPreview(shiftingCase, basicScheme, newScheme);

  }

  @Override
  public double[] shiftingPreview (int shiftingCase, double[] basicScheme,
                                   double[] newScheme)
  {

    double[] result = new double[Constants.MINUTES_PER_DAY];

    if (shiftingCase == 0) {

      result = shiftingOptimal(newScheme);
    }
    else if (shiftingCase == 1) {

      result = shiftingNormal(basicScheme, newScheme);
    }
    else if (shiftingCase == 2) {

      result = shiftingDiscrete(basicScheme, newScheme);
    }
    else {
      System.out.println("ERROR in shifting function");
    }

    return result;

  }

  @Override
  public double[] shiftingOptimal (double[] newScheme)
  {
    double[] result = new double[Constants.MINUTES_PER_DAY];

    double sum = 0;

    for (int i = 0; i < newScheme.length; i++) {
      result[i] = histogram[i] / newScheme[i];
      sum += result[i];
    }

    for (int i = 0; i < result.length; i++)
      result[i] /= sum;

    return result;

  }

  @Override
  public double[] shiftingNormal (double[] basicScheme, double[] newScheme)
  {
    double[] result = Arrays.copyOf(histogram, histogram.length);

    PricingVector pricingVector = new PricingVector(basicScheme, newScheme);

    IncentiveVector inc = new IncentiveVector(basicScheme, newScheme);

    if (pricingVector.getPrices().size() > 1)
      for (Incentive incentive: inc.getIncentives())
        result = movingAverage(result, incentive);

    return result;

  }

  @Override
  public double[] shiftingDiscrete (double[] basicScheme, double[] newScheme)
  {
    double[] result = Arrays.copyOf(histogram, histogram.length);

    PricingVector pricingVector = new PricingVector(basicScheme, newScheme);

    if (pricingVector.getPrices().size() > 1)
      result = discreteAverage(result, pricingVector);

    return result;
  }

  @Override
  public double[] movingAverage (double[] values, Incentive incentive)
  {
    // Initialize the auxiliary variables.
    int side = -1;
    int startIndex = incentive.getStartMinute();
    int endIndex = incentive.getEndMinute();
    double overDiff = 0;
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
          overDiff += values[i] - temp;
          values[i] = temp;

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
            overDiff += values[index] - temp;
            values[index] = temp;
          }

          for (int i = endIndex; i < endIndex + side; i++) {

            int index = i;

            if (index > Constants.MINUTES_PER_DAY - 1)
              index %= Constants.MINUTES_PER_DAY;

            temp = incentive.getPrice() * values[index] / incentive.getBase();
            overDiff += values[index] - temp;
            values[index] = temp;
          }
          break;

        case "Left":

          for (int i = startIndex - 2 * side; i < startIndex; i++) {

            int index = i;

            if (index < 0)
              index += Constants.MINUTES_PER_DAY;

            temp = incentive.getPrice() * values[index] / incentive.getBase();
            overDiff += values[index] - temp;
            values[index] = temp;
          }
          break;

        case "Right":

          for (int i = endIndex; i < endIndex + 2 * side; i++) {

            int index = i;

            if (index > Constants.MINUTES_PER_DAY - 1)
              index %= Constants.MINUTES_PER_DAY;

            temp = incentive.getPrice() * values[index] / incentive.getBase();
            overDiff += values[index] - temp;
            values[index] = temp;
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
  public double[] discreteAverage (double[] values, PricingVector pricing)
  {

    // Initialize the auxiliary variables.
    double temp = 0;
    // double sum = 0;
    double overDiff = 0;
    int start, end;
    double newPrice;

    // Finding the cheapest window in the day.
    int cheapest = pricing.getCheapest();
    int startCheapest =
      pricing.getPrices(pricing.getCheapest()).getStartMinute();
    int endCheapest = pricing.getPrices(pricing.getCheapest()).getEndMinute();
    int durationCheapest = endCheapest - startCheapest;
    double cheapestPrice = pricing.getPrices(pricing.getCheapest()).getPrice();

    // Moving from all the available vectors residual percentages to the
    // cheapest one.
    for (int i = 0; i < pricing.getPrices().size(); i++) {

      if (i != cheapest) {
        // sum = 0;
        overDiff = 0;
        start = pricing.getPrices(i).getStartMinute();
        end = pricing.getPrices(i).getEndMinute();
        newPrice = pricing.getPrices(i).getPrice();

        for (int j = start; j <= end; j++) {
          temp = cheapestPrice * values[j] / newPrice;
          overDiff += values[j] - temp;
          values[j] = temp;
        }

        double additive = overDiff / durationCheapest;

        for (int j = startCheapest; j <= endCheapest; j++)
          values[j] += additive;

        // for (int j = 0; j < values.length; j++)
        // sum += values[j];
        // System.out.println("Summary after index " + i + ": " + sum);

      }

    }

    return values;
  }

  @Override
  public DBObject toJSON (String activityModelID)
  {
    double[] values = new double[1];
    DBObject temp = new BasicDBObject();
    BasicDBList param = new BasicDBList();

    for (int i = 0; i < gaussians.length; i++) {
      DBObject paramItem = new BasicDBObject();

      double[] means = new double[gaussians.length];
      double[] sigmas = new double[gaussians.length];

      means[i] = gaussians[i].mean;
      sigmas[i] = gaussians[i].sigma;

      paramItem.put("w", pi[i]);
      paramItem.put("mean", means[i]);
      paramItem.put("std", sigmas[i]);

      param.add(paramItem);
    }

    temp.put("name", name);
    temp.put("type", type);
    temp.put("description", name + " " + type);
    temp.put("distrType", type);
    temp.put("actmod_id", activityModelID);
    temp.put("parameters", param);
    temp.put("values", values);
    return temp;

  }

}
