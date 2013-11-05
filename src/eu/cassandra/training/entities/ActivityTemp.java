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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.jfree.chart.ChartPanel;

import eu.cassandra.training.activity.ActivityModel;
import eu.cassandra.training.utils.Constants;

/**
 * This class is used for implementing the temporary activity that will become
 * later the Activity Model in the Training Module of Cassandra Project. The
 * models created here are compatible with the activity models exported by the
 * Disaggregation Module.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class ActivityTemp
{
  /**
   * This variable contains the name of the temporary activity model.
   */
  private final String name;

  /**
   * This variable contains a list of the consumption events corresponding to
   * the temporary activity model.
   */
  private final ArrayList<Integer[]> events = new ArrayList<Integer[]>();

  /**
   * This variable contains the file name of the events file of the temporary
   * activity.
   */
  private String eventsFile = "";

  /**
   * This variable contains a list of the appliances that are part of the
   * temporary activity.
   */
  private ArrayList<Appliance> appliances = new ArrayList<Appliance>();

  /**
   * Simple constructor of a temporary activity object.
   */
  public ActivityTemp (String name)
  {
    this.name = name;
  }

  /**
   * This is a getter function of the temporary activity's name.
   * 
   * @return the name of the temporary activity.
   */
  public String getName ()
  {
    return name;
  }

  /**
   * This is a getter function of the temporary activity's list of appliances.
   * 
   * @return the list of appliances of the temporary activity.
   */
  public ArrayList<Appliance> getAppliances ()
  {
    return appliances;
  }

  /**
   * This is a setter function of the temporary activity's list of appliances.
   * 
   * @param applianes
   *          The list of appliances of the temporary activity.
   */
  public void setAppliances (ArrayList<Appliance> appliances)
  {
    this.appliances = appliances;
  }

  /**
   * A function used to add an event in the event list of the temporary
   * activity.
   * 
   * @param start
   *          The start time of the consumption event.
   * 
   * @param end
   *          The end time of the consumption event.
   * 
   */
  public void addEvent (int start, int end)
  {
    Integer[] temp = new Integer[2];
    temp[0] = start;
    temp[1] = end;
    events.add(temp);
  }

  /**
   * This function is giving the capability of creating an event file out of the
   * list of consumption events imported from the user to this temporary
   * activity.
   * 
   */
  public void createEventFile () throws IOException
  {
    PrintStream realSystemOut = System.out;
    eventsFile = Constants.tempFolder + name + " events.csv";
    OutputStream output = new FileOutputStream(eventsFile);
    PrintStream printOut = new PrintStream(output);
    System.setOut(printOut);

    System.out.println("Start Time, End Time");

    for (Integer[] temp: events) {
      System.out.println(temp[0] + "-" + temp[1]);
    }

    System.setOut(realSystemOut);
    output.close();

  }

  /**
   * This function is used to present the basic information of the temporary
   * activity model on the console.
   */
  public void status ()
  {
    System.out.println("Activity:" + name);
    System.out.println("Events File:" + eventsFile);
    System.out.println("Number of Events:" + events.size());
    System.out.println("Appliances:" + appliances.toString());
  }

  @Override
  public String toString ()
  {
    return name;
  }

  /**
   * This function is used to provide an example of the consumption of the
   * appliances that are present in the temporary activity by presenting a
   * graphical chart of the consumption model of the first available appliance.
   * 
   * @return a chart panel with the consumption model.
   */
  public ChartPanel consumptionGraph ()
  {
    return appliances.get(0).consumptionGraph();
  }

  /**
   * This function is converting the temporary activity to a fully functional
   * Activity Model.
   * 
   * @param person
   *          The person name that the Activity Model belongs to.
   * @return an Activity Model based on the temporary activity data.
   * @throws FileNotFoundException
   */
  public ActivityModel toActivityModel (String person)
    throws FileNotFoundException
  {
    String[] appliances = new String[this.appliances.size()];

    for (int i = 0; i < appliances.length; i++)
      appliances[i] = this.appliances.get(i).getName();

    ActivityModel result =
      new ActivityModel(name, person, appliances, eventsFile);

    return result;
  }
}
