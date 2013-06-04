package eu.cassandra.training.entities;

public class ApplianceTemp
{

  private String name, type;
  private String installation;
  private String activity;
  private double p, q;

  public ApplianceTemp (String name, String installation, String type,
                        String activity, double p, double q)
  {
    this.name = name;
    this.installation = installation;
    this.type = type;
    this.activity = activity;
    this.p = p;
    this.q = q;
  }

  public String getName ()
  {
    return name;
  }

  public String getInstallation ()
  {
    return installation;
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
    System.out.println("Installation:" + installation);
    System.out.println("Type:" + type);
    System.out.println("Activity:" + activity);
    System.out.println("P:" + p);
    System.out.println("Q:" + q);

  }

  public Appliance toAppliance ()
  {
    String powerModel =
      "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" :" + p
              + ", \"d\" : 10, \"s\": 0.0}]}]}";

    String reactiveModel =
      "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"q\" :" + q
              + ", \"d\" : 10, \"s\": 0.0}]}]}";

    Appliance appliance =
      new Appliance(name, installation, powerModel, reactiveModel, "",
                    new double[0], new double[0]);

    appliance.setType(type);
    appliance.setActivity(activity);

    return appliance;
  }

  // private String searchEventsFile ()
  // {
  //
  // String result = "";
  // String nameTemp = "";
  //
  // File folder = new File("./Files/");
  // String[] files = folder.list();
  // if (name.split(" ").length != 1) {
  // nameTemp = name.replaceAll("[0-9]", "");
  // nameTemp = nameTemp.trim();
  // // System.out.println(nameTemp);
  // }
  // else {
  // nameTemp = name;
  // // System.out.println(nameTemp);
  // }
  //
  // String comp = activity + " " + nameTemp + " events.csv";
  // // for (String file: files) {
  // //
  // // if (comp.equalsIgnoreCase(file))
  // // System.out.println(file);
  // //
  // // }
  //
  // result = "./Files/" + comp;
  //
  // return result;
  //
  // }
}
