package eu.cassandra.training.response;

import java.util.ArrayList;

import eu.cassandra.training.utils.Constants;

public class PricingVector
{

  ArrayList<Price> prices = new ArrayList<Price>();
  int numberOfPenalties = 0;
  int numberOfRewards = 0;
  int numberOfBases = 0;
  int indexOfCheapest = -1;

  public PricingVector (double[] basicScheme, double[] newScheme)
  {
    boolean startFlag = false;
    int start = -1;
    int end = -1;
    String type = "";
    double currentValue = 0;
    double previousValue = 0;

    for (int i = 0; i < newScheme.length; i++) {
      // System.out.println("Index:" + i);

      if (previousValue != newScheme[i]) {
        // System.out
        // .println("In for difference!" + previousValue + " " + diff[i]);

        if (startFlag == false) {
          // System.out.println("In for start!");
          currentValue = newScheme[i];
          if (newScheme[i] == basicScheme[i])
            type = "Base";
          else if (newScheme[i] < basicScheme[i])
            type = "Reward";
          else
            type = "Penalty";

          startFlag = true;
          start = i;
        }
        else {
          // System.out.println("In for end!");
          end = i - 1;
          startFlag = false;
          prices.add(new Price(start, end, currentValue, type));

          // System.out.println("In for start kapaki!");
          currentValue = newScheme[i];
          if (newScheme[i] == basicScheme[i])
            type = "Base";
          else if (newScheme[i] < basicScheme[i])
            type = "Reward";
          else
            type = "Penalty";
          startFlag = true;
          start = i;

        }
      }
      previousValue = newScheme[i];
    }

    if (startFlag) {
      // System.out.println("In for end of index!");
      end = Constants.MINUTES_PER_DAY - 1;
      startFlag = false;
      prices.add(new Price(start, end, currentValue, type));
    }

    analyze();

    show();
  }

  private void analyze ()
  {

    double minPrice = Double.POSITIVE_INFINITY;
    int minDur = 0;
    int newDur = 0;

    for (int i = 0; i < prices.size(); i++) {

      if (prices.get(i).getType().equalsIgnoreCase("Base"))
        numberOfBases++;
      else if (prices.get(i).getType().equalsIgnoreCase("Reward"))
        numberOfRewards++;
      else
        numberOfPenalties++;

      if (minPrice > prices.get(i).getPrice()) {
        minPrice = prices.get(i).getPrice();
        minDur = prices.get(i).getEndMinute() - prices.get(i).getStartMinute();
        indexOfCheapest = i;
      }
      else if (minPrice == prices.get(i).getPrice()) {
        newDur = prices.get(i).getEndMinute() - prices.get(i).getStartMinute();
        if (minDur < newDur) {
          minDur = newDur;
          indexOfCheapest = i;
        }
      }

    }

  }

  private void show ()
  {
    // for (int i = 0; i < prices.size(); i++)
    // prices.get(i).status();
    System.out.println("Penalties: " + numberOfPenalties);
    System.out.println("Rewards: " + numberOfRewards);
    System.out.println("Base: " + numberOfBases);

    System.out.println("Cheapest Pricing: ");
    prices.get(indexOfCheapest).status();

  }

  public ArrayList<Price> getPrices ()
  {
    return prices;
  }

  public Price getPrices (int index)
  {
    return prices.get(index);
  }

  public int getCheapest ()
  {

    return indexOfCheapest;

  }
}
