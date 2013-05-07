package eu.cassandra.training.response;

public class Incentive
{

  private int startMinute;
  private int endMinute;
  private boolean penalty;
  private double base;
  private double difference;
  private double beforeDifference;
  private double afterDifference;

  public Incentive (int start, int end, double base, double bDiff,
                    double aDiff, double diff)
  {
    startMinute = start;
    endMinute = end;
    this.base = base;
    difference = Math.abs(diff);
    penalty = (diff > 0);
    beforeDifference = bDiff;
    afterDifference = aDiff;
  }

  public int getStartMinute ()
  {
    return startMinute;
  }

  public int getEndMinute ()
  {
    return endMinute;
  }

  public double getDifference ()
  {
    return difference;
  }

  public boolean isPenalty ()
  {
    return penalty;
  }

  public double getBeforeDifference ()
  {
    return beforeDifference;
  }

  public double getAfterDifference ()
  {
    return afterDifference;
  }

  public double getBase ()
  {
    return base;
  }

  public double getPrice ()
  {

    if (isPenalty())
      return base + difference;
    else
      return base - difference;

  }

  public void status ()
  {
    System.out.println("Start Minute: " + startMinute);
    System.out.println("End Minute: " + endMinute);
    System.out.println("Penalty: " + penalty);
    System.out.println("Base:" + base);
    System.out.println("Difference: " + difference);
    System.out.println("Before Difference: " + beforeDifference);
    System.out.println("After Difference: " + afterDifference);

  }

}
