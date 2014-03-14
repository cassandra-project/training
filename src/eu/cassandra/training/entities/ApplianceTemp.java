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
import java.util.Arrays;

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
  private final String name;

  /**
   * This variable contains the type of the temporary appliance model.
   */
  private final String type;

  /**
   * This variable contains the name of the installation the temporary appliance
   * model is connected to.
   */
  private final String installation;

  /**
   * This variable contains the name of the activity that the temporary
   * appliance model is part of.
   */
  private final String activity;

  /**
   * This variable contains the value of the active activeOnly consumption of
   * the temporary appliance model.
   */
  private double p = 0;

  /**
   * This variable contains the value of the reactive activeOnly consumption of
   * the temporary appliance model.
   */
  private double q = 0;

  /**
   * This variable contains the duration of the operation of an appliance. This
   * is used mostly in refrigerators and freezers.
   */
  private int duration = 10;

  /**
   * This variable contains the distance between switching events on the
   * operation of an appliance. This is used mostly in refrigerators and
   * freezers.
   */
  private int distance = 0;

  /**
   * This variable contains the array of active power consumption of an
   * appliance, used mostly in the washing machine appliances
   */
  private double[] pValues = null;

  /**
   * This variable contains the array of reactive power consumption of an
   * appliance, used mostly in the washing machine appliances
   */
  private double[] qValues = null;

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
   *          The active consumption of the temporary appliance
   * @param q
   *          The reactive consumption of the temporary appliance
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
   *          The active consumption of the temporary appliance
   * @param q
   *          The reactive consumption of the temporary appliance
   * @param duration
   *          The duration of the appliance operation
   * @param distance
   *          The distance between switching events in the operation of the
   *          appliance
   */
  public ApplianceTemp (String name, String installation, String type,
                        String activity, double p, double q, int duration,
                        int distance)
  {
    this.name = name;
    this.installation = installation;
    this.type = type;
    this.activity = activity;
    this.p = p;
    this.q = q;
    this.duration = duration;
    this.distance = distance;
  }

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
   * @param pValues
   *          The array of active consumption of the temporary appliance
   * @param qValues
   *          The array of reactive consumption of the temporary appliance
   */
  public ApplianceTemp (String name, String installation, String type,
                        String activity, double[] pValues, double[] qValues)
  {
    this.name = name;
    this.installation = installation;
    this.type = type;
    this.activity = activity;
    this.pValues = pValues;
    this.qValues = qValues;
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
    System.out.println("PValues:" + Arrays.toString(pValues));
    System.out.println("QValues:" + Arrays.toString(qValues));
    System.out.println("Duration:" + duration);
    System.out.println("Distance:" + distance);

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
    String activeModel = "";
    String reactiveModel = "";
    boolean base = false;

    boolean refFlag = activity.contains("Refrigeration");
    boolean wmFlag = name.contains("Washing");

    if (refFlag) {
      activeModel =
        "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" :" + p
                + ", \"d\" :" + duration
                + ", \"s\": 0.0}, {\"p\" : 0 , \"d\" :" + distance
                + ", \"s\": 0.0}]}]}";

      reactiveModel =
        "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"q\" :" + q
                + ", \"d\" :" + duration
                + ", \"s\": 0.0}, {\"q\" : 0 , \"d\" :" + distance
                + ", \"s\": 0.0}]}]}";

      base = true;
    }
    else if (wmFlag) {

      // System.out.println(Arrays.toString(pValues));
      // System.out.println(Arrays.toString(qValues));

      for (int i = 0; i < pValues.length; i++) {

        if (i == 0)
          activeModel =
            "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" :"
                    + pValues[i] + ", \"d\" : 1 , \"s\": 0.0}";
        else if (i == pValues.length - 1)
          activeModel += "]}]}";
        else
          activeModel += ",{\"p\" :" + pValues[i] + ", \"d\" : 1 , \"s\": 0.0}";

        if (i == 0)
          reactiveModel =
            "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"q\" :"
                    + qValues[i] + ", \"d\" : 1 , \"s\": 0.0}";
        else if (i == pValues.length - 1)
          reactiveModel += "]}]}";
        else
          reactiveModel +=
            ",{\"q\" :" + qValues[i] + ", \"d\" : 1 , \"s\": 0.0}";

      }

      // System.out.println(activeModel);
      // System.out.println(reactiveModel);
    }
    else {
      activeModel =
        "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"p\" :" + p
                + ", \"d\" :" + duration + ", \"s\": 0.0}]}]}";

      reactiveModel =
        "{ \"n\" : 0, \"params\" : [{ \"n\" : 1, \"values\" : [ {\"q\" :" + q
                + ", \"d\" :" + duration + ", \"s\": 0.0}]}]}";

    }

    Appliance appliance =
      new Appliance(name, installation, activeModel, reactiveModel, "", base);

    appliance.setType(type);
    appliance.setActivity(activity);

    return appliance;
  }
}
