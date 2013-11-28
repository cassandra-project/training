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

package eu.cassandra.training.entities;

import java.io.FileNotFoundException;

/**
 * This class is used for implementing the temporary appliances that will become
 * later the Appliance Entity models of the Training Module of Cassandra
 * Project. The models created here are compatible with the Appliance Models
 * exported by the Disaggregation Module.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class ApplianceTemp
{
  /**
   * This variable contains the name of the temporary appliance model.
   */
  private String name;

  /**
   * This variable contains the type of the temporary appliance model.
   */
  private String type;

  /**
   * This variable contains the name of the installation the temporary appliance
   * model is connected to.
   */
  private String installation;

  /**
   * This variable contains the name of the activity that the temporary
   * appliance model is part of.
   */
  private String activity;

  /**
   * This variable contains the value of the active activeOnly consumption of
   * the
   * temporary appliance model.
   */
  private double p;

  /**
   * This variable contains the value of the reactive activeOnly consumption of
   * the
   * temporary appliance model.
   */
  private double q;

  /**
   * Simple constructor of a temporary appliance object.
   * 
   * @param name
   *          The name of the temporary appliance
   * @param installation
   *          The name of the installation
   * @param type
   *          The type of the temporary appliance
   * @param activity
   *          The name of the activity the temporary appliance is connected to
   * @param p
   *          The active activeOnly consumption of the temporary appliance
   * @param q
   *          The reactive activeOnly consumption of the temporary appliance
   */
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

  /**
   * This function is used to present the basic information of the temporary
   * appliance model on the console.
   */
  public void status ()
  {
    System.out.println("Appliance:" + name);
    System.out.println("Installation:" + installation);
    System.out.println("Type:" + type);
    System.out.println("Activity:" + activity);
    System.out.println("P:" + p);
    System.out.println("Q:" + q);

  }

  /**
   * This function is converting the temporary activity to a fully functional
   * Appliance Model.
   * 
   * @return an Appliance Model based on the temporary appliance data.
   * @throws FileNotFoundException
   */
  public Appliance toAppliance ()
  {
    String powerModel = "";
    String reactiveModel = "";
    boolean base = false;
    if (activity.equalsIgnoreCase("Refrigeration Refrigerator")) {
      powerModel =
        "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" :"
                + p
                + ", \"d\" : 20, \"s\": 0.0}, {\"p\" : 0 , \"d\" : 20, \"s\": 0.0}]}]}";

      reactiveModel =
        "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"q\" :"
                + q
                + ", \"d\" : 20, \"s\": 0.0}, {\"q\" : 0 , \"d\" : 20, \"s\": 0.0}]}]}";
    }
    else {
      powerModel =
        "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" :" + p
                + ", \"d\" : 10, \"s\": 0.0}]}]}";

      reactiveModel =
        "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"q\" :" + q
                + ", \"d\" : 10, \"s\": 0.0}]}]}";

    }
    if (activity.equalsIgnoreCase("Refrigeration"))
      base = true;

    Appliance appliance =
      new Appliance(name, installation, powerModel, reactiveModel, "", base);

    appliance.setType(type);
    appliance.setActivity(activity);

    return appliance;
  }
}
