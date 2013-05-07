package eu.cassandra.training.response;

import java.util.ArrayList;

import eu.cassandra.training.utils.Constants;

public class IncentiveVector
{

  ArrayList<Incentive> incentives = new ArrayList<Incentive>();
  int numberOfPenalties = 0;
  int numberOfRewards = 0;
  int indexOfLargerPenalty = -1;
  int indexOfLargerReward = -1;

  public IncentiveVector (double[] basicScheme, double[] newScheme)
  {
    double[] diff = new double[Constants.MINUTES_PER_DAY];
    boolean startFlag = false;
    int start = -1;
    int end = -1;
    double base = 0;
    double bDiff = 0;
    double aDiff = 0;
    double previousValue = 0;

    for (int i = 0; i < diff.length; i++) {
      // System.out.println("Index:" + i);

      diff[i] = newScheme[i] - basicScheme[i];
      // System.out.println("Difference:" + diff[i]);

      if (previousValue != diff[i]) {
        // System.out
        // .println("In for difference!" + previousValue + " " + diff[i]);

        if (startFlag == false) {
          // System.out.println("In for start!");
          if (i != 0)
            bDiff = newScheme[i] - newScheme[i - 1];
          else
            bDiff = newScheme[i] - newScheme[Constants.MINUTES_PER_DAY - 1];
          base = basicScheme[i];
          startFlag = true;
          start = i;
        }
        else {
          // System.out.println("In for end!");
          aDiff = newScheme[i] - newScheme[i - 1];
          end = i - 1;
          startFlag = false;
          incentives.add(new Incentive(start, end, base, bDiff, aDiff,
                                       previousValue));

          if (diff[i] != 0) {
            // System.out.println("In for start kapaki!");
            base = basicScheme[i];
            bDiff = newScheme[i] - newScheme[i - 1];
            startFlag = true;
            start = i;

          }
        }
      }
      previousValue = diff[i];
    }

    if (startFlag) {
      // System.out.println("In for end of index!");
      aDiff = newScheme[0] - newScheme[Constants.MINUTES_PER_DAY - 1];
      end = Constants.MINUTES_PER_DAY - 1;
      startFlag = false;
      incentives.add(new Incentive(start, end, base, bDiff, aDiff,
                                   previousValue));
    }

    analyze();

    // show();
  }

  private void analyze ()
  {

    double maxPrice = Double.NEGATIVE_INFINITY;
    double minPrice = Double.POSITIVE_INFINITY;

    for (int i = 0; i < incentives.size(); i++) {

      if (incentives.get(i).isPenalty())
        numberOfPenalties++;
      else
        numberOfRewards++;

      if (maxPrice < incentives.get(i).getPrice()) {
        maxPrice = incentives.get(i).getPrice();
        indexOfLargerPenalty = i;
      }

      if (minPrice > incentives.get(i).getPrice()) {
        minPrice = incentives.get(i).getPrice();
        indexOfLargerReward = i;
      }

    }

  }

  private void show ()
  {
    for (int i = 0; i < incentives.size(); i++)
      incentives.get(i).status();
    System.out.println("Penalties: " + numberOfPenalties);
    System.out.println("Rewards: " + numberOfRewards);
    if (numberOfPenalties > 0)
      System.out.println("Larger Penalty: "
                         + incentives.get(indexOfLargerPenalty).getPrice());
    if (numberOfRewards > 0)
      System.out.println("Larger Reward: "
                         + incentives.get(indexOfLargerReward).getPrice());
  }

  public ArrayList<Incentive> getIncentives ()
  {
    return incentives;
  }

}
