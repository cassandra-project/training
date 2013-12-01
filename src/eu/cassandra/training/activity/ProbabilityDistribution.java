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

import java.util.Comparator;

import com.mongodb.DBObject;

import eu.cassandra.training.response.Incentive;
import eu.cassandra.training.response.Pricing;
import eu.cassandra.training.response.PricingVector;

/**
 * This is the Probability Distribution interface, used for implementing the
 * distribution types supported in the Training Module of Cassandra Project. The
 * same class has been used, with small alterations in the main Cassandra
 * Platform.
 * 
 * @author Christos Diou, Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public interface ProbabilityDistribution
{

  /**
   * This comparator is used for sorting the points of interest based on the
   * minute of interest.
   */
  public static Comparator<Pricing> comp = new Comparator<Pricing>() {
    @Override
    public int compare (Pricing poi1, Pricing poi2)
    {
      return Double.compare(poi1.getGainRatio(), poi1.getGainRatio());
    }
  };

  /**
   * Return a string with the name of the distribution.
   * 
   * @return String with description of distribution.
   */
  public String getName ();

  /**
   * Return a string describing the distribution in free text.
   * 
   * @return String with description of distribution.
   */
  public String getDescription ();

  /**
   * Return the number of parameters of this distribution.
   * 
   * @return An int with the number of distribution parameters.
   */
  public int getNumberOfParameters ();

  /**
   * Return a distribution parameter.
   * 
   * @param index
   *          Index of the parameter (starting from 0).
   * @return The parameter value.
   */
  public double getParameter (int index);

  /**
   * Set a parameter value
   * 
   * Most implementations are expected to set the parameters at the
   * constructor, but this function should also be implemented.
   * 
   * @param index
   *          Index of the parameter (starting from 0).
   * @return value The parameter value
   */
  public void setParameter (int index, double value);

  /**
   * Precomputes a set of distribution values.
   * 
   * Given a set of "bins" this method computes the probability of
   * randomly drawing a value from each bin, including the bin
   * starting value and not including the end value. The computed
   * value is stored in a histogram vector and can later be directly
   * accessed using the method getPrecomputedProbability(). If the
   * distribution is a probability density function, then this
   * function may compute the integral of the pdf for the bin value
   * range, otherwise the function will only compute the probability
   * at the starting value of the bin.
   * 
   * @param startValue
   *          The starting value of the probability
   *          distribution domain or the lower bound for which probabilities
   *          will be pre-computed.
   * @param endValue
   *          The ending value of the probability
   *          distribution domain or the lower bound for which probabilities
   *          will be pre-computed.
   * @param nBins
   *          The number of bins that will be used for the given
   *          value range.
   */
  public void precompute (int startValue, int endValue, int nBins);

  /**
   * Get the probability value P(x).
   * 
   * @param x
   *          The input value.
   * @return The probability value at x (P(x)).
   */
  public double getProbability (int x);

  /**
   * Get the probability value P(X > x).
   * 
   * @param x
   *          The input value.
   * @return The probability value at x (P(x)).
   */
  public double getProbabilityLess (int x);

  /**
   * Get the probability value P(X < x).
   * 
   * @param x
   *          The input value.
   * @return The probability value at x (P(x)).
   */
  public double getProbabilityGreaterEqual (int x);

  /**
   * Get the precomputed probability value for the x.
   * 
   * @param x
   *          The input value.
   * @return The precomputed probability value of the bin that
   *         contains x. If no probabilities have been pre-computed, then
   *         this function returns -1.
   */
  public double getPrecomputedProbability (int x);

  /**
   * Gets a random integer between 0 and the number of nBins.
   * 
   * @return A random integer following the distribution of the precomputed
   *         histogram
   */
  public int getPrecomputedBin ();

  /**
   * Shows the general attributes of the distribution.
   * 
   */
  public void status ();

  /**
   * Shifting function for the probability distribution
   * 
   * @param shiftingCase
   *          The selected response type by the user.
   * @param basicScheme
   *          The basic pricing scheme as imported by the user.
   * @param newScheme
   *          The new pricing scheme as imported by the user.
   * 
   */
  public void shifting (int shiftingCase, double[] basicScheme,
                        double[] newScheme, float awareness, float sensitivity);

  /**
   * Makes a preview of the shifting function
   * 
   * @param shiftingCase
   *          The selected response type by the user.
   * @param basicScheme
   *          The basic pricing scheme as imported by the user.
   * @param newScheme
   *          The new pricing scheme as imported by the user.
   * 
   * @return the new Start Time distribution as a result of the shifting.
   */
  public double[] shiftingPreview (int shiftingCase, double[] basicScheme,
                                   double[] newScheme, float awareness,
                                   float sensitivity);

  /**
   * The Optimal Case Scenario shifting function
   * 
   * @param basicScheme
   *          The base pricing scheme as imported by the user.
   * 
   * @param newScheme
   *          The new pricing scheme as imported by the user.
   * 
   * @return the new Start Time distribution as a result of the optimal case
   *         scenario shifting.
   */
  public double[] shiftingOptimal (double[] basicScheme, double[] newScheme,
                                   float awareness, float sensitivity);

  /**
   * The Normal Case Scenario shifting function
   * 
   * @param newScheme
   *          The new pricing scheme as imported by the user.
   * 
   * @return the new Start Time distribution as a result of the normal case
   *         scenario shifting.
   */
  public double[] shiftingNormal (double[] basicScheme, double[] newScheme,
                                  float awareness, float sensitivity);

  /**
   * The Discrete Case Scenario shifting function
   * 
   * @param basicScheme
   *          The base pricing scheme as imported by the user.
   * 
   * @param newScheme
   *          The new pricing scheme as imported by the user.
   * 
   * @return the new Start Time distribution as a result of the discrete case
   *         scenario shifting.
   */
  public double[] shiftingDiscrete (double[] basicScheme, double[] newScheme,
                                    float awareness, float sensitivity);

  /**
   * Returning the distribution ID
   * 
   * @return
   */
  public String getDistributionID ();

  /**
   * Setter of the distribution ID
   * 
   * @param id
   *          The id of the distribution as given from the Cassandra server.
   */
  public void setDistributionID (String id);

  /**
   * Creating a JSON object out of the distribution
   * 
   * @param activityModelID
   *          The id of the activity model the distribution belongs to.
   * 
   * @return the JSON object created from the distribution.
   */
  public DBObject toJSON (String activityModelID);

  /**
   * Return the distribution probability histogram.
   * 
   * @param
   * @return The values of the distribution histogram.
   */
  public double[] getHistogram ();

  /**
   * This function is used to estimate the new start time distribution after the
   * optimal case scenario shifting is applied.
   * 
   * @param values
   *          the start time distribution before shifting
   * @param pricing
   *          the pricing vector as it has been estimated from the basic and new
   *          pricing schemas.
   * @return the new start time distribution.
   */
  public double[] discreteOptimal (double[] values, PricingVector pricing,
                                   float awareness, float sensitivity);

  /**
   * This function is used to estimate the new start time distribution after the
   * discrete case scenario shifting is applied.
   * 
   * @param values
   *          the start time distribution before shifting
   * @param pricing
   *          the pricing vector as it has been estimated from the basic and new
   *          pricing schemas.
   * @return the new start time distribution.
   */
  public double[] discreteAverage (double[] values, PricingVector pricing,
                                   float awareness, float sensitivity);

  /**
   * This function is used to estimate the new start time distribution after the
   * normal case scenario shifting is applied.
   * 
   * @param values
   *          the start time distribution before shifting
   * @param incentive
   *          an incentives as it has been estimated from the basic and
   *          new pricing schemas.
   * @return the new start time distribution.
   */
  public double[] movingAverage (double[] values, Incentive incentive,
                                 float awareness, float sensitivity);
}
