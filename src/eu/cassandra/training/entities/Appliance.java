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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.jfree.chart.ChartPanel;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.training.consumption.ActiveConsumptionModel;
import eu.cassandra.training.consumption.ReactiveConsumptionModel;
import eu.cassandra.training.consumption.TripletPower;
import eu.cassandra.training.consumption.TripletReactive;
import eu.cassandra.training.utils.ChartUtils;

/**
 * This class is used for implementing the Appliance Models in the Training
 * Module of Cassandra Project. The models created here are compatible with the
 * Appliance Models used in the main Cassandra Platform and can be easily
 * exported to the User's Library.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class Appliance
{
  /**
   * This variable provides the name of the Appliance model.
   */
  private String name = "";

  /**
   * This variable provides the name of the installation the Appliance model is
   * contained into.
   */
  private String installation = "";

  /**
   * This variable provides the name of the activity the Appliance model is part
   * of.
   */
  private String activity = "";

  /**
   * This variable provides the type of the Appliance model.
   */
  private String type = "Generic";

  /**
   * This variable provides the energy class of the Appliance model.
   */
  private String energyClass = "";

  /**
   * This boolean variable states if this is a base load or not.
   */
  private boolean base = false;

  /**
   * This boolean variable states if this is appliance is controllable or not.
   */
  private boolean controllable = false;

  /**
   * This boolean variable states if this is appliance is shiftable or not.
   */
  private boolean shiftable = false;

  /**
   * This boolean variable states if this is appliance has static consumption or
   * not.
   */
  private boolean staticConsumption = true;

  /**
   * This is the value of the standby consumption of the appliance.
   */
  private double standbyConsumption = 0.0;

  /**
   * This variable presents this appliance's active active consumption model
   * in
   * form of a string.
   */
  private String activeConsumptionModelString = "";

  /**
   * This variable presents this appliance's reactive active consumption
   * model in
   * form of a string.
   */
  private String reactiveConsumptionModelString = "";

  /**
   * This variable presents this appliance's active active consumption
   * model.
   */
  private ActiveConsumptionModel activeConsumptionModel =
    new ActiveConsumptionModel();

  /**
   * This variable presents this appliance's active active consumption
   * model.
   */
  private ReactiveConsumptionModel reactiveConsumptionModel =
    new ReactiveConsumptionModel();

  /**
   * This variable contains the file name of the events file corresponding to
   * this appliance.
   */
  private String eventsFile = "";

  /**
   * This variable provides the id of the Appliance model as sent by the
   * Cassandra Platform.
   */
  private String applianceID = "";

  /**
   * The constructor of an Appliance Model.
   * 
   * @param name
   *          The name of the Appliance Model
   * @param installation
   *          The name of the installation that the Appliance Model is installed
   * @param activeModel
   *          The active active consumption of the Appliance Model
   * @param reactiveModel
   *          The reactive active consumption of the Appliance Model
   * @param eventFile
   *          The event file of the Appliance Model
   */
  public Appliance (String name, String installation, String activeModel,
                    String reactiveModel, String eventFile, Boolean... base)
  {

    this.name = name;
    this.installation = installation;
    this.eventsFile = eventFile;
    activeConsumptionModelString = activeModel;
    reactiveConsumptionModelString = reactiveModel;
    DBObject dbo = (DBObject) JSON.parse(activeConsumptionModelString);
    activeConsumptionModel.init(dbo);
    dbo = (DBObject) JSON.parse(reactiveConsumptionModelString);
    reactiveConsumptionModel.init(dbo);
    if (base.length == 1)
      this.base = base[0];

    staticConsumption = checkStatic();
  }

  // TODO Remove after disaggregation encapsulation
  public Appliance (String name, String powerModelFile,
                    String reactiveModelFile, String eventFile,
                    Installation installation, boolean power, Boolean... base)
    throws IOException
  {
    this.name = name;
    this.installation = installation.getName();
    parseConsumptionModel(powerModelFile);
    this.eventsFile = eventFile;
    if (base.length == 1)
      this.base = base[0];

    staticConsumption = checkStatic();
  }

  /**
   * This is a getter function of the Appliance model name.
   * 
   * @return the name of the appliance model.
   */
  public String getName ()
  {
    return name;
  }

  /**
   * This is a getter function of the activity the Appliance model corresponds
   * to.
   * 
   * @return the activity that the Appliance model corresponds to.
   */
  public String getActivity ()
  {
    return activity;
  }

  /**
   * This is a getter function of the id of the Appliance model.
   * 
   * @return the id of the Appliance model.
   */
  public String getApplianceID ()
  {
    return applianceID;
  }

  /**
   * This is a getter function of the name of the installation of the Appliance
   * model.
   * 
   * @return the name of the installation.
   */
  public String getInstallation ()
  {
    return installation;
  }

  /**
   * This is a getter function of the events file of the Appliance model.
   * 
   * @return the events file of the Appliance model.
   */
  public String getEventsFile ()
  {
    return eventsFile;
  }

  /**
   * This is a getter function of the static consumption variable of the
   * Appliance model.
   * 
   * @return the events file of the Appliance model.
   */
  public boolean getStaticConsumption ()
  {
    return staticConsumption;
  }

  /**
   * This is a setter function of the activity the Appliance model belongs to.
   * 
   * @param the
   *          activity of the Appliance model.
   */
  public void setActivity (String activity)
  {
    this.activity = activity;
  }

  /**
   * This is a setter function of the type of the Appliance model.
   * 
   * @param the
   *          type of the Appliance model.
   */
  public void setType (String type)
  {
    this.type = type;
  }

  /**
   * This is a setter function of the id of the Appliance model.
   * 
   * @param the
   *          id give from Cassandra server to the Appliance model.
   */
  public void setApplianceID (String id)
  {
    applianceID = id;
  }

  /**
   * This function is used to create an array of example active active
   * consumption in order to create the chart for the preview.
   * 
   * @return an array with the active active consumption for limited time
   *         interval.
   */
  public Double[] getActiveConsumptionModel ()
  {

    ArrayList<Double> temp = new ArrayList<Double>();
    int times = activeConsumptionModel.getOuterN();
    if (times == 0)
      times = 2;
    // Number of repeats
    for (int i = 0; i < times; i++) {
      // System.out.println("Time: " + i);
      // Number of patterns in each repeat
      for (int j = 0; j < activeConsumptionModel.getPatternN(); j++) {
        // System.out.println("Pattern: " + j);
        int internalTimes = activeConsumptionModel.getN(j);
        if (internalTimes == 0)
          internalTimes = 2;
        // System.out.println("Internal Times: " + k);
        for (int k = 0; k < internalTimes; k++) {
          ArrayList<TripletPower> tripplets =
            activeConsumptionModel.getPattern(j);
          for (int l = 0; l < tripplets.size(); l++) {
            // System.out.println("TripletPower: " + l);
            for (int m = 0; m < tripplets.get(l).d; m++) {
              temp.add(tripplets.get(l).p);
            }
          }
        }
      }
    }
    Double[] result = new Double[temp.size()];
    temp.toArray(result);
    return result;

  }

  /**
   * This function is used to create an array of example reactiver
   * powerconsumption in order to create the chart for the preview.
   * 
   * @return an array with the active active consumption for limited time
   *         interval.
   */
  public Double[] getReactiveConsumptionModel ()
  {

    ArrayList<Double> temp = new ArrayList<Double>();
    int times = reactiveConsumptionModel.getOuterN();
    if (times == 0)
      times = 2;
    // Number of repeats
    for (int i = 0; i < times; i++) {
      // System.out.println("Time: " + i);
      // Number of patterns in each repeat
      for (int j = 0; j < reactiveConsumptionModel.getPatternN(); j++) {
        // System.out.println("Pattern: " + j);
        int internalTimes = reactiveConsumptionModel.getN(j);
        if (internalTimes == 0)
          internalTimes = 2;
        // System.out.println("Internal Times: " + k);
        for (int k = 0; k < internalTimes; k++) {
          ArrayList<TripletReactive> tripplets =
            reactiveConsumptionModel.getPattern(j);
          for (int l = 0; l < tripplets.size(); l++) {
            // System.out.println("TripletPower: " + l);
            for (int m = 0; m < tripplets.get(l).d; m++) {
              temp.add(tripplets.get(l).q);
            }
          }
        }
      }
    }
    Double[] result = new Double[temp.size()];
    temp.toArray(result);
    return result;

  }

  /**
   * This function is the parser of the consumption model provided by the user.
   * 
   * @param filename
   *          The file name of the consumption model
   * @throws IOException
   */
  public void parseConsumptionModel (String filename) throws IOException
  {

    File file = new File(filename);

    String model = "";

    String extension =
      filename.substring(filename.length() - 3, filename.length());

    Scanner scanner = new Scanner(file);
    switch (extension) {

    case "son":

      while (scanner.hasNext())
        model = model + scanner.nextLine();
      break;
    default:

      while (scanner.hasNext())
        model = model + scanner.nextLine();

      model.replace(" ", "");
    }
    scanner.close();

    activeConsumptionModelString = model;
    DBObject dbo = (DBObject) JSON.parse(activeConsumptionModelString);
    activeConsumptionModel.init(dbo);

    reactiveConsumptionModelString =
      activeConsumptionModelString.replace("p", "q");
    reactiveConsumptionModelString =
      reactiveConsumptionModelString.replace("qara", "para");
    System.out.println(reactiveConsumptionModelString);
    dbo = (DBObject) JSON.parse(reactiveConsumptionModelString);
    reactiveConsumptionModel.init(dbo);

  }

  @Override
  public String toString ()
  {
    return name;
  }

  /**
   * Creating a JSON object out of the appliance model
   * 
   * @param installationID
   *          The id of the installation model the appliance belongs to.
   * 
   * @return the JSON object created from the appliance.
   */
  public DBObject toJSON (String installationID)
  {

    DBObject temp = new BasicDBObject();

    temp.put("name", name);
    temp.put("type", type);
    temp.put("description", name + " " + type);
    temp.put("controllable", controllable);
    temp.put("shiftable", shiftable);
    temp.put("base", base);
    temp.put("energy_class", "Class A");
    temp.put("standy_consumption", standbyConsumption);
    temp.put("inst_id", installationID);

    return temp;

  }

  /**
   * Creating a JSON object out of the appliance's active and reactive active
   * consumption models.
   * 
   * @return the JSON object created from the active and reactive active
   *         consumption models.
   */
  public DBObject powerConsumptionModelToJSON ()
  {
    DBObject temp = new BasicDBObject();

    temp.put("name", name + " Consumption Model");
    temp.put("type", type);
    temp.put("description", "P and Q Consumption Model");
    temp.put("app_id", applianceID);
    temp.put("pmodel", JSON.parse(activeConsumptionModelString));
    temp.put("qmodel", JSON.parse(reactiveConsumptionModelString));
    temp.put("pvalues", new double[1]);
    temp.put("qvalues", new double[1]);
    return temp;

  }

  /**
   * This function is utilized to create the sample active consumption model
   * to
   * be graphically represented in the Training Module.
   * 
   * @return a chart panel with the consumption model graph.
   */
  public ChartPanel consumptionGraph ()
  {

    return ChartUtils.createArea(name + " Consumption Model", "Time Step",
                                 "Power", getActiveConsumptionModel(),
                                 getReactiveConsumptionModel());
  }

  /**
   * This function is utilized to check if the consumption model is a static one
   * (having the same value all the operation cycle) or not.
   * 
   * @return a boolean variable if the model has static consumption or not.
   */
  private boolean checkStatic ()
  {
    boolean result = true;

    Double[] values = activeConsumptionModel.getValues();

    // System.out.println("Appliance: " + name + " Model: "
    // + activeConsumptionModelString);
    //
    // System.out.println("Appliance: " + name + " Values: "
    // + Arrays.toString(values));

    for (int i = 0; i < values.length - 1; i++) {
      // System.out.println("Previous: " + values[i].doubleValue() + " Next: "
      // + values[i + 1].doubleValue());
      if (values[i].doubleValue() != values[i + 1].doubleValue()) {
        // System.out.println("IN");
        result = false;
        break;
      }
    }

    return result;
  }

  public double getMeanActiveConsumption ()
  {

    double result = 0;

    Double[] activeConsumptionModel = getActiveConsumptionModel();

    for (int i = 0; i < activeConsumptionModel.length; i++) {

      result += activeConsumptionModel[i];

    }

    return result / activeConsumptionModel.length;

  }

  /**
   * This function is used to present the basic information of the Appliance
   * Model on the console.
   */
  public void status ()
  {
    System.out.println("Name: " + name);
    System.out.println("Type: " + type);
    System.out.println("Appliance Of Installation: " + installation);
    System.out.println("Base: " + base);
    System.out.println("Controllable: " + controllable);
    System.out.println("Shiftable: " + shiftable);
    System.out.println("Static Consumption: " + staticConsumption);
    System.out.println("Energy Class: " + energyClass);
    System.out.println("StandBy Consumption: " + standbyConsumption);
    System.out.println("Events File: " + eventsFile);
    System.out.println("Power Consumption Model:"
                       + activeConsumptionModelString);
    System.out.println("Reactive Power Consumption Model:"
                       + reactiveConsumptionModelString);
  }
}
