package eu.cassandra.training.entities;

public class ApplianceTemp
{

  private String name, type;
  private String activity;
  private double p, q;

  public ApplianceTemp (String name, String type, String activity, double p,
                        double q)
  {

    this.name = name;
    this.type = type;
    this.activity = activity;
    this.p = p;
    this.q = q;

  }

  public String getName ()
  {
    return name;
  }

  public String getType ()
  {
    return type;
  }

  public String getActivity ()
  {
    return activity;
  }

  public double getP ()
  {
    return p;
  }

  public double getQ ()
  {
    return q;
  }

  public void status ()
  {

    System.out.println("Appliance:" + name);
    System.out.println("Type:" + type);
    System.out.println("Activity:" + activity);
    System.out.println("P:" + p);
    System.out.println("Q:" + q);

  }

  public void toAppliance ()
  {

  }

}
