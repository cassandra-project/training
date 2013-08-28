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

import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartPanel;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.training.activity.ActivityModel;
import eu.cassandra.training.response.ResponseModel;
import eu.cassandra.training.utils.ChartUtils;

/**
 * This class is used for implementing the Person Models in the Training
 * Module of Cassandra Project. The models created here are compatible with the
 * Person Models used in the main Cassandra Platform and can be easily
 * exported to the user's Library.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class Person
{
  /**
   * This variable provides the name of the Person model.
   */
  private String name = "";

  /**
   * This variable provides the type of the Person model.
   */
  private String type = "";

  /**
   * This variable provides the name of the installation the Person model
   * corresponds to.
   */
  private String installation = "";

  /**
   * This variable is an list of the Activity models corresponding to the Person
   * Model.
   */
  private ArrayList<ActivityModel> activityModels;

  /**
   * This variable is an list of the Response models corresponding to the Person
   * Model.
   */
  private ArrayList<ResponseModel> responseModels;

  /**
   * This variable provides the id of the Person model as sent by the
   * Cassandra Platform.
   */
  private String personID = "";

  /**
   * A simple constructor of an Person Model.
   */
  public Person ()
  {
    activityModels = new ArrayList<ActivityModel>();
    responseModels = new ArrayList<ResponseModel>();
  }

  /**
   * The constructor of an Person Model.
   * 
   * @param name
   *          The name of the Person Model
   * @param installation
   *          The name of the installation that the Person Model is installed
   */
  public Person (String name, String installation)
  {
    this.name = name;
    this.installation = installation;
    activityModels = new ArrayList<ActivityModel>();
    responseModels = new ArrayList<ResponseModel>();
  }

  /**
   * This is a getter function of the Person model name.
   * 
   * @return the name of the Person model.
   */
  public String getName ()
  {
    return name;
  }

  /**
   * This is a getter function of the id of the Person model.
   * 
   * @return the id of the Person model.
   */
  public String getPersonID ()
  {
    return personID;
  }

  /**
   * This is a getter function of the size of the Activity models of the Person
   * Model.
   * 
   * @return the number of Activity Models present on the Appliance model.
   */
  public int getActivityModelsSize ()
  {
    return activityModels.size();
  }

  /**
   * This is a getter function of the size of the Response models of the Person
   * Model.
   * 
   * @return the number of Reponse Models present on the Appliance model.
   */
  public int getResponseModelsSize ()
  {
    return responseModels.size();
  }

  /**
   * This is a setter function of the id of the Person model.
   * 
   * @param the
   *          id give from Cassandra server to the Person model.
   */
  public void setPersonID (String id)
  {
    personID = id;
  }

  /**
   * This function is used to add and Activity Model to the Person Model
   * 
   * @param activity
   *          The Activity Model in need of addition.
   */
  public void addActivity (ActivityModel activity)
  {
    activityModels.add(activity);
  }

  public ActivityModel findActivity (Appliance appliance)
  {

    ActivityModel result = null;
    String temp = this.name + " " + appliance.getName() + " Activity Model";
    // System.out.println("Name:" + name);
    for (ActivityModel activity: activityModels) {
      // System.out.println(activity.getName());
      if (activity.getName().equalsIgnoreCase(temp)) {
        result = activity;
        break;
      }
    }
    return result;
  }

  /**
   * This function is searching for an Activity Model in the Person given
   * a certain name.
   * 
   * @param name
   *          The name of the Activity Model in search of.
   * @param suffix
   *          The flag showing a suffix is needed to find the correct Activity
   *          Model.
   * @return the found Activity Model.
   */
  public ActivityModel findActivity (String name, boolean suffix)
  {

    ActivityModel result = null;
    String temp = "";
    if (suffix)
      temp = this.name + " " + name + " Activity Model";
    else
      temp = name;

    for (ActivityModel activity: activityModels) {
      if (activity.getName().equalsIgnoreCase(temp)) {
        result = activity;
        break;
      }
    }
    return result;
  }

  /**
   * This function is searching for an Response Model in the Person given
   * a certain name.
   * 
   * @param name
   *          The name of the Activity Model in search of.
   * @return the found Response Model.
   */
  public ResponseModel findResponse (String name)
  {

    ResponseModel result = null;

    for (ResponseModel response: responseModels) {

      if (response.getName().equalsIgnoreCase(name)) {
        result = response;
        break;
      }
    }
    return result;
  }

  /**
   * This function is used for the training of the new Activity Models when a
   * single appliance is at hand.
   * 
   * @param appliance
   *          The base appliance for the Activity Model.
   * @param distributions
   *          The distribution types selected by the user on the GUI.
   * @throws IOException
   */
  public void train (Appliance appliance, String[] distributions)
    throws IOException
  {
    ActivityModel exists =
      findActivity(name + " " + appliance.getName() + " Activity Model", false);

    if (exists != null)
      activityModels.remove(exists);

    ActivityModel activityModel = new ActivityModel(appliance, name);
    activityModel.train(distributions);
    activityModels.add(activityModel);
  }

  /**
   * This function is used for the training of the new Activity Models when an
   * temporary Activity is used.
   * 
   * @param activity
   *          The base temporary activity for the Activity Model.
   * @param distributions
   *          The distribution types selected by the user on the GUI.
   * @throws IOException
   */
  public void train (ActivityTemp activity, String[] distributions)
    throws IOException
  {
    ActivityModel exists =
      findActivity(activity.getName() + " Activity Model", false);

    if (exists != null)
      activityModels.remove(exists);

    ActivityModel activityModel = activity.toActivityModel(name);
    activityModel.train(distributions);
    activityModels.add(activityModel);
  }

  /**
   * It enables the creation of a graphical representation of a response model
   * based on the user's preferences.
   * 
   * @param activity
   *          The selected base Activity Model.
   * @param response
   *          The selected response type.
   * @param basicScheme
   *          The imported basic pricing scheme.
   * @param newScheme
   *          The imported new pricing scheme.
   * @return a chart panel with the resulting Response model graphical
   *         representation.
   */
  public ChartPanel previewResponse (ActivityModel activity, int response,
                                     double[] basicScheme, double[] newScheme)
  {
    return ResponseModel.previewResponseModel(activity, response, basicScheme,
                                              newScheme);
  }

  /**
   * It enables the creation of a response model based on the user's
   * preferences.
   * 
   * @param activity
   *          The selected base Activity Model.
   * @param response
   *          The selected response type.
   * @param basicScheme
   *          The imported basic pricing scheme.
   * @param newScheme
   *          The imported new pricing scheme.
   * @return the name of the resulting Response model.
   */
  public String createResponse (ActivityModel activity, int responseType,
                                double[] basicScheme, double[] newScheme)
    throws IOException
  {
    String responseTemp = "";

    switch (responseType) {

    case 0:
      responseTemp = "Optimal";
      break;
    case 1:
      responseTemp = "Normal";
      break;
    case 2:
      responseTemp = "Discrete";
    }

    if (activity.getActivity()) {

      String temp =
        activity.getNameActivity().replace(" Activity",
                                           " Response Model (Optimal)");

      String temp2 =
        activity.getNameActivity().replace(" Activity",
                                           " Response Model (Normal)");

      String temp3 =
        activity.getNameActivity().replace(" Activity",
                                           " Response Model (Discrete)");

      ResponseModel exists = findResponse(temp);

      if (exists != null) {
        // System.out.println("Optimal Exists!");
        responseModels.remove(exists);
      }
      else {
        exists = findResponse(temp2);
        if (exists != null) {
          // System.out.println("Normal Exists!");
          responseModels.remove(exists);
        }
        else {
          exists = findResponse(temp3);
          if (exists != null) {
            // System.out.println("Discrete Exists!");
            responseModels.remove(exists);
          }
        }
      }

    }
    else {
      String temp =
        name + " " + activity.getAppliancesOf()[0]
                + " Response Model (Optimal)";

      String temp2 =
        name + " " + activity.getAppliancesOf()[0] + " Response Model (Normal)";

      String temp3 =
        name + " " + activity.getAppliancesOf()[0]
                + " Response Model (Discrete)";

      ResponseModel exists = findResponse(temp);

      // System.out.println(temp + " " + temp2 + " " + temp3);

      if (exists != null) {
        // System.out.println("Optimal Exists!");
        responseModels.remove(exists);
      }
      else {
        exists = findResponse(temp2);
        if (exists != null) {
          // System.out.println("Normal Exists!");
          responseModels.remove(exists);
        }
        else {
          exists = findResponse(temp3);
          if (exists != null) {
            // System.out.println("Discrete Exists!");
            responseModels.remove(exists);
          }
        }
      }
    }

    String result = "";

    ResponseModel response = new ResponseModel(activity, name, responseType);

    response.respond(responseType, basicScheme, newScheme);

    responseModels.add(response);

    result = response.toString();

    return result;
  }

  @Override
  public String toString ()
  {
    return name;
  }

  /**
   * Creating a JSON object out of the Person model.
   * 
   * @return the JSON object created from Person model.
   */
  public DBObject toJSON (String installationID)
  {

    DBObject temp = new BasicDBObject();

    temp.put("name", name);
    temp.put("type", type);
    temp.put("description", name + " " + type);
    temp.put("inst_id", installationID);

    return temp;

  }

  /**
   * This function is used to present the basic information of the Person
   * Model on the console.
   */
  public void status ()
  {
    System.out.println("Name: " + name);
    System.out.println("Person Of Installation: " + installation);
    System.out.println("Activity Models:" + activityModels.toString());
    System.out.println("Response Models:" + responseModels.toString());
  }

  /**
   * It enables the creation of a graphical statistic graph for an overview of
   * the Person Model.
   * 
   * @return a chart panel with the resulting statistical overview graphical
   *         representation.
   */
  public ChartPanel statisticGraphs ()
  {
    String title = name + " Statistics";

    return ChartUtils.createPieChart(title, this);
  }
}
