package eu.cassandra.training.response;

public class Price
{

  private int startMinute;
  private int endMinute;
  private double price;
  private String type;

  public Price (int start, int end, double price, String type)
  {
    startMinute = start;
    endMinute = end;
    this.price = price;
    this.type = type;
  }

  public int getStartMinute ()
  {
    return startMinute;
  }

  public int getEndMinute ()
  {
    return endMinute;
  }

  public double getPrice ()
  {
    return price;
  }

  public String getType ()
  {
    return type;
  }

  public void status ()
  {
    System.out.println("Start Minute: " + startMinute);
    System.out.println("End Minute: " + endMinute);
    System.out.println("Price: " + price);
    System.out.println("Pricing Type: " + type);

  }

}
