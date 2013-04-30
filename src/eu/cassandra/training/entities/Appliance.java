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
import java.util.Arrays;
import java.util.Scanner;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import eu.cassandra.training.consumption.ConsumptionModel;
import eu.cassandra.training.consumption.Triplet;

public class Appliance
{

  private String name;
  private String consumptionModelString;
  private ConsumptionModel consumptionModel = new ConsumptionModel();
  private String eventsFile;
  private double[] activePower;
  private double[] reactivePower;

  public Appliance ()
  {
    name = "";
    eventsFile = "";
    consumptionModelString = "";
    activePower = new double[0];
    reactivePower = new double[0];
  }

  public Appliance (String name, String cModel, String eventFile,
                    double[] active)
  {
    this.name = name;
    this.eventsFile = eventFile;
    consumptionModelString = cModel;
    DBObject dbo = (DBObject) JSON.parse(consumptionModelString);
    consumptionModel.init(dbo);
    activePower = active;
  }

  public Appliance (String name, String cModel, String eventFile,
                    double[] active, double[] reactive)
  {

    this.name = name;
    this.eventsFile = eventFile;
    consumptionModelString = cModel;
    DBObject dbo = (DBObject) JSON.parse(consumptionModelString);
    consumptionModel.init(dbo);
    activePower = active;
    reactivePower = reactive;

  }

  public Appliance (String name, String cModelFile, String eventFile,
                    Installation installation, boolean power)
    throws IOException
  {

    this.name = name;
    parseConsumptionModel(cModelFile);
    this.eventsFile = eventFile;

    activePower = installation.getActivePower();
    if (!power)
      reactivePower = installation.getReactivePower();

  }

  public String getName ()
  {
    return name;
  }

  public String getEventsFile ()
  {
    return eventsFile;
  }

  public String getConsumptionModelString ()
  {
    return consumptionModelString;
  }

  public Double[] getConsumptionModel ()
  {

    ArrayList<Double> temp = new ArrayList<Double>();
    int times = consumptionModel.getOuterN();
    if (times == 0)
      times = 2;
    // Number of repeats
    for (int i = 0; i < times; i++) {
      // System.out.println("Time: " + i);
      // Number of patterns in each repeat
      for (int j = 0; j < consumptionModel.getPatternN(); j++) {
        // System.out.println("Pattern: " + j);
        int internalTimes = consumptionModel.getN(j);
        if (internalTimes == 0)
          internalTimes = 2;
        // System.out.println("Internal Times: " + k);
        for (int k = 0; k < internalTimes; k++) {
          ArrayList<Triplet> tripplets = consumptionModel.getPattern(j);
          for (int l = 0; l < tripplets.size(); l++) {
            // System.out.println("Triplet: " + l);
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

  public double[] getActivePower ()
  {
    return activePower;
  }

  public double getActivePower (int index)
  {
    return activePower[index];
  }

  public double[] getReactivePower ()
  {
    if (reactivePower.length == 0)
      System.out
              .println("No Reactive Power measurements available for this appliance");

    return reactivePower;
  }

  public double getReactivePower (int index)
  {
    if (reactivePower.length == 0) {
      System.out
              .println("No Reactive Power measurements available for this appliance");
      return 0;
    }
    return reactivePower[index];
  }

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

    consumptionModelString = model;
    DBObject dbo = (DBObject) JSON.parse(consumptionModelString);
    consumptionModel.init(dbo);
  }

  public void status ()
  {

    System.out.println("Name: " + name);
    System.out.println("Events File: " + eventsFile);
    System.out.println("Consumption Model:" + consumptionModel.toString());
    System.out.println("Active Power:" + Arrays.toString(activePower));
    System.out.println("Reactive Power:" + Arrays.toString(reactivePower));

  }
}
