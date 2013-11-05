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

package eu.cassandra.training.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.jfree.chart.ChartPanel;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.training.consumption.ConsumptionEventRepo;
import eu.cassandra.training.entities.Appliance;
import eu.cassandra.training.utils.ChartUtils;
import eu.cassandra.training.utils.Constants;
import eu.cassandra.training.utils.MixtureCreator;
import eu.cassandra.training.utils.Utils;

/**
 * This class is used for implementing the Activity models created in the
 * Training Module of Cassandra Project. The models created here are compatible
 * with the activity models in the platform and can be exported as such.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class ActivityModel
{

  /**
   * This variable contains the name of the activity model as appears in the
   * lists of the Training Module.
   */
  protected String name = "";

  /**
   * This variable contains the name of the activity that the activity model is
   * part of.
   */
  protected String nameActivity = "";

  /**
   * This variable contains the type of the activity model.
   */
  protected String type = "";

  /**
   * This variable shows if the activity model can be shifted or not (due to
   * monetary or other types of incentives).
   */
  protected boolean shiftable = false;

  /**
   * The daytype variable is used to show the type of days or season this
   * activity model can be used (working day, non working day, any etc.).
   */
  protected String dayType = "any";

  /**
   * This array contains the appliances that are participating in the activity
   * model.
   */
  protected String[] applianceOf;

  /**
   * This is the name of the person type that this activity model corresponds
   * to. In the Training Module, this person is the equivalent of all the
   * inhabitants of the installation since the measurements can not be related
   * to a certain person present.
   */
  protected String person;

  /**
   * This variable contains the consumption event that are related with the
   * activity model. They are used in order to create the activity model given
   * the distribution types selected by the user.
   */
  protected ConsumptionEventRepo consumptionEventRepo;

  /**
   * These variables are the distributions that correspond to each of the random
   * variables needed to fully define an activity model (Daily Times, Duration,
   * Start Time). Also, there is Start Time Binned distribution which is used
   * for presentation purposes only since it is an aggregated version of the
   * Start Time distribution.
   */
  protected ProbabilityDistribution startTime, startTimeBinned, duration,
          dailyTimes;

  /**
   * This map contains the files defining the distributions selected for each
   * aforementioned random variable
   */
  protected Map<String, String> fileMap = new HashMap<String, String>();

  /**
   * This map contains the distribution types selected for each
   * aforementioned random variable by the user for the training procedure.
   */
  protected Map<String, String> distributionTypes =
    new HashMap<String, String>();

  /**
   * This variable contains the string id that the activity that contains the
   * current activity model gets when it is exported to the main Cassandra
   * platform from the Training Module.
   */
  protected String activityID = "";

  /**
   * This variable contains the string id that the activity model gets when it
   * is exported to the main Cassandra platform from the Training Module.
   */
  protected String activityModelID = "";

  /**
   * This variable contains the string id that the Daily Times distribution
   * contained within the activity model gets when it is exported to the main
   * Cassandra platform from the Training Module.
   */
  protected String dailyID = "";

  /**
   * This variable contains the string id that the Duration distribution
   * contained within the activity model gets when it is exported to the main
   * Cassandra platform from the Training Module.
   */
  protected String durationID = "";

  /**
   * This variable contains the string id that the Start Time distribution
   * contained within the activity model gets when it is exported to the main
   * Cassandra platform from the Training Module.
   */
  protected String startID = "";

  /**
   * This variable shows if the activity model is associated with an activity or
   * just a single appliance.
   */
  protected boolean activity = false;

  /**
   * Simple constructor of an Activity model.
   */
  public ActivityModel ()
  {
    name = "";
    consumptionEventRepo = new ConsumptionEventRepo(name);
  }

  /**
   * A constructor of an activity model used in case we know some of the input
   * variables.
   * 
   * @param appliance
   *          The appliance that is participating in the activity model.
   * @param person
   *          The name of the person type that this activity model belongs to.
   */
  public ActivityModel (Appliance appliance, String person)
    throws FileNotFoundException
  {
    nameActivity = person + " " + appliance.getName() + " Activity";
    name = person + " " + appliance.getName() + " Activity Model";
    this.person = person;
    applianceOf = new String[1];
    applianceOf[0] = appliance.getName();
    consumptionEventRepo = new ConsumptionEventRepo(applianceOf[0]);
    consumptionEventRepo.readEventsFile(appliance.getEventsFile());
  }

  /**
   * A constructor of an activity model used in case we know most of the input
   * variables.
   * 
   * @param activity
   *          The name of the activity this activity model belongs to.
   * @param person
   *          The name of the person type that this activity model belongs to.
   * @params appliances
   *         The array of appliances participating in the activity model.
   * @param eventsFile
   *          The filename of the event file used for the training procedure.
   * 
   */
  public ActivityModel (String activity, String person, String[] appliances,
                        String eventsFile) throws FileNotFoundException
  {
    nameActivity = person + " " + activity + " Activity";
    name = person + " " + activity + " Activity Model";
    this.activity = true;
    this.person = person;
    applianceOf = appliances;
    consumptionEventRepo = new ConsumptionEventRepo(activity);
    consumptionEventRepo.readEventsFile(eventsFile);
  }

  /**
   * This function is used as a getter for the name variable of the activity
   * model.
   * 
   * @return activity model's name.
   */
  public String getName ()
  {
    return name;
  }

  /**
   * This function is used as a getter for the name variable of the activity
   * that the activity model is contained.
   * 
   * @return activity's name.
   */
  public String getNameActivity ()
  {
    return nameActivity;
  }

  /**
   * This function is used as a getter for the boolean variable activity.
   * 
   * @return activity model's activity variable.
   */
  public boolean getActivity ()
  {
    return activity;
  }

  /**
   * This function is used as a getter for the activity model ID.
   * 
   * @return activity model's id.
   */
  public String getActivityModelID ()
  {
    return activityModelID;
  }

  /**
   * This function is used as a getter for the duration distribution ID.
   * 
   * @return duration distribution's id.
   */
  public String getDurationID ()
  {
    return durationID;
  }

  /**
   * This function is used as a getter for the daily times distribution ID.
   * 
   * @return daily times distribution's id.
   */
  public String getDailyID ()
  {
    return dailyID;
  }

  /**
   * This function is used as a getter for the start time distribution ID.
   * 
   * @return start time distribution's id.
   */
  public String getStartID ()
  {
    return startID;
  }

  /**
   * This function is used as a getter for the appliances participating in the
   * activity model.
   * 
   * @return array of participating appliances.
   */
  public String[] getAppliancesOf ()
  {
    return applianceOf;
  }

  /**
   * This function is used as a getter for the consumption event repo of the
   * activity model.
   * 
   * @return the consumption event repository.
   */
  public ConsumptionEventRepo getConsumptionEventRepo ()
  {
    return consumptionEventRepo;
  }

  /**
   * This function is used as a getter for the daily times distribution.
   * 
   * @return daily times distribution.
   */
  public ProbabilityDistribution getDailyTimes ()
  {
    return dailyTimes;
  }

  /**
   * This function is used as a getter for the duration distribution.
   * 
   * @return duration distribution.
   */
  public ProbabilityDistribution getDuration ()
  {
    return duration;
  }

  /**
   * This function is used as a getter for the start time distribution.
   * 
   * @return start time distribution.
   */
  public ProbabilityDistribution getStartTime ()
  {
    return startTime;
  }

  /**
   * This function is used as a getter for the daily times binned distribution.
   * 
   * @return daily times binned distribution.
   */
  public ProbabilityDistribution getStartTimeBinned ()
  {
    return startTimeBinned;
  }

  /**
   * This function is used as a getter for the distribution types map.
   * 
   * @return distribution types map.
   */
  public Map<String, String> getDistributionTypes ()
  {
    return distributionTypes;
  }

  /**
   * This function is used as a getter for the file map.
   * 
   * @return file map.
   */
  public Map<String, String> getFileMap ()
  {
    return fileMap;
  }

  /**
   * This function is used as a setter for the activity ID.
   * 
   * @param id
   *          the new activity id.
   */
  public void setActivityID (String id)
  {
    activityID = id;
  }

  /**
   * This function is used as a setter for the activity model ID.
   * 
   * @param id
   *          the new activity model id.
   */
  public void setActivityModelID (String id)
  {
    activityModelID = id;
  }

  /**
   * This function is used as a setter for the daily times distribution ID.
   * 
   * @param id
   *          the new daily times distribution id.
   */
  public void setDailyID (String id)
  {
    dailyID = id;
  }

  /**
   * This function is used as a setter for the duration distribution ID.
   * 
   * @param id
   *          the new duration distribution id.
   */
  public void setDurationID (String id)
  {
    durationID = id;
  }

  /**
   * This function is used as a setter for the start time distribution ID.
   * 
   * @param id
   *          the new start time distribution id.
   */
  public void setStartID (String id)
  {
    startID = id;
  }

  /**
   * This function is used to initialize the training procedure for the daily
   * times distribution ID.
   * 
   * @param distributions
   *          the user selected distribution types for the training procedure.
   */
  public void train (String[] distributions) throws IOException
  {

    // For each of the 4 ditributions that have to be defined for the activity
    // model.
    for (int i = 0; i < 4; i++) {

      // Find the correct file containing the distribution definition values.
      String file = fileDistribution(i);
      // Fill the distribution with the values.
      fillDistribution(file, distributions[i], i);

    }

  }

  /**
   * This function is used in order to set the correct file that will be used as
   * an input for the distribution values.
   * 
   * @param variable
   *          the distribution static code integer number (0 -> Daily Times, 1
   *          -> Duration, 2 -> Start Time, 3 -> Start Time Binned) that will
   *          lead to the correct file.
   */
  private String fileDistribution (int variable) throws FileNotFoundException
  {

    String variablePath = "";
    String file = "";

    switch (variable) {

    case Constants.DAILY_TIMES:

      variablePath = Constants.DAILY_TIMES_FILE;
      break;

    case Constants.DURATION:

      variablePath = Constants.DURATION_FILE;
      break;

    case Constants.START_TIME:

      variablePath = Constants.START_TIME_FILE;
      break;

    case Constants.START_TIME_BINNED:

      variablePath = Constants.START_TIME_BINNED_FILE;
      break;

    default:

      System.out.println("Distribution Error");

    }

    file = variablePath + name + ".csv";

    return file;
  }

  /**
   * This function fills the distributions of the activity model with the
   * correct values as they were produced by the training procedure.
   * 
   * @param file
   *          the file name of the file containing the values for the
   *          distribution
   * 
   * @param type
   *          The type of the distribution as chosen by the user
   * 
   * @param index
   *          The distribution that is defined as set by the integer numbers
   *          described above
   */
  public void fillDistribution (String file, String type, int index)
    throws IOException
  {

    // Parsing the selected file
    Scanner input = new Scanner(file);
    String newFile = Constants.tempFolder;
    MixtureCreator mixtureCreator = null;
    String variable = "";

    // See the random variable distribution under construction
    if (index == 0)
      variable = "DailyTimes";
    else if (index == 1)
      variable = "Duration";
    else if (index == 2)
      variable = "StartTime";
    else if (index == 3)
      variable = "StartTimeBinned";

    // Given the distribution type selected from the user, the distribution file
    // is constructed by the event repo and then the costructor of the
    // probability distribution selected is called to create the object. It
    // should be noted that the start time binned distribution is created with
    // the start time distribution.
    switch (type) {

    case "Histogram":
      if (index == 0) {
        consumptionEventRepo.DailyTimesHistogramToFile(file);
        dailyTimes = new Histogram(file);
      }
      else if (index == 1) {
        consumptionEventRepo.DurationHistogramToFile(file);
        duration = new Histogram(file);
      }
      else if (index == 2) {
        consumptionEventRepo.createStartTimeHistogram2();
        consumptionEventRepo.StartTimeHistogramToFile(file);
        startTime = new Histogram(file);
      }
      else if (index == 3) {
        if (distributionTypes.get("StartTime").equalsIgnoreCase("Histogram")) {
          consumptionEventRepo.StartTimeBinnedHistogramToFile(file);
          startTimeBinned = new Histogram(file);
        }
        else {
          double[] temp =
            Utils.aggregateStartTimeDistribution(startTime.getHistogram());
          startTimeBinned = new Histogram(file + " Binned", temp);
        }

      }
      else
        System.out.println("ERROR in index");

      break;

    case "Normal":

      newFile += type + variable + name + ".csv";
      consumptionEventRepo.attributeToFile(file, variable);
      mixtureCreator = new MixtureCreator();
      mixtureCreator.createNormal(file, newFile, variable, false);

      if (index == 0)
        dailyTimes = new Gaussian(newFile);
      else if (index == 1)
        duration = new Gaussian(newFile);
      else if (index == 2) {
        startTime = new Gaussian(newFile);
        double[] temp =
          Utils.aggregateStartTimeDistribution(startTime.getHistogram());
        startTimeBinned = new Histogram(file + " Binned", temp);
      }
      else if (index == 3) {

      }
      else
        System.out.println("ERROR in index");

      break;

    case "GMM":

      newFile += type + variable + name + ".csv";
      consumptionEventRepo.attributeToFile(file, variable);
      mixtureCreator = new MixtureCreator();
      mixtureCreator.createGMM(file, newFile, variable);

      if (index == 0)
        dailyTimes = new GaussianMixtureModels(newFile);
      else if (index == 1)
        duration = new GaussianMixtureModels(newFile);
      else if (index == 2) {
        startTime = new GaussianMixtureModels(newFile);
        double[] temp =
          Utils.aggregateStartTimeDistribution(startTime.getHistogram());
        startTimeBinned = new Histogram(file + " Binned", temp);
      }
      else if (index == 3) {

      }
      else
        System.out.println("ERROR in index");

      break;

    default:
      System.out.println("ERROR in distribution type");

    }
    // The maps are filled with the correct distribution file and type
    // accordingly.
    distributionTypes.put(variable, type);
    fileMap.put(variable, file);

    input.close();

  }

  /**
   * This function creates the Duration distribution chart when demanded by the
   * user.
   * 
   * @return the chart panel containing the Duration distribution chart.
   */
  public ChartPanel createDurationDistributionChart ()
  {

    String variable = "Duration Distribution";
    String x = "Minutes";
    String y = "Probability";

    switch (distributionTypes.get("Duration")) {

    case "Histogram":

      return ChartUtils
              .createHistogram(variable, x, y, duration.getHistogram());

    default:

      return ChartUtils.createMixtureDistribution(variable, x, y,
                                                  duration.getHistogram());

    }

  }

  /**
   * This function creates the Daily Times distribution chart when demanded by
   * the user.
   * 
   * @return the chart panel containing the Daily Times distribution chart.
   */
  public ChartPanel createDailyTimesDistributionChart ()
  {

    String variable = "Daily Times Distribution";
    String x = "Number of Daily Times";
    String y = "Probability";

    switch (distributionTypes.get("DailyTimes")) {

    case "Histogram":

      return ChartUtils.createHistogram(variable, x, y,
                                        dailyTimes.getHistogram());

    default:

      return ChartUtils.createMixtureDistribution(variable, x, y,
                                                  dailyTimes.getHistogram());

    }

  }

  /**
   * This function creates the Start Time distribution chart when demanded by
   * the user.
   * 
   * @return the chart panel containing the Start Time distribution chart.
   */
  public ChartPanel createStartTimeDistributionChart ()
  {
    String variable = "Start Time Distribution";
    String x = "Start Time Minute of the Day";
    String y = "Probability";

    switch (distributionTypes.get("StartTime")) {

    case "Histogram":

      // Utils.histogramValues(startTime.getHistogram());

      return ChartUtils.createHistogram(variable, x, y,
                                        startTime.getHistogram());

    default:

      return ChartUtils.createMixtureDistribution(variable, x, y,
                                                  startTime.getHistogram());

    }

  }

  /**
   * This function creates the Start Time Binned distribution chart when
   * demanded by the user.
   * 
   * @return the chart panel containing the Start Time Binned distribution
   *         chart.
   */
  public ChartPanel createStartTimeBinnedDistributionChart ()
  {
    String variable = "Start Time Binned Distribution";
    String x =
      "Start Time in " + consumptionEventRepo.getBinSize()
              + " Minutes Interval";
    String y = "Probability";

    switch (distributionTypes.get("StartTimeBinned")) {

    case "Histogram":

      // Utils.histogramValues(startTimeBinned.getHistogram());

      return ChartUtils.createHistogram(variable, x, y,
                                        startTimeBinned.getHistogram());

    default:

      return ChartUtils.createMixtureDistribution(variable, x, y,
                                                  startTimeBinned
                                                          .getHistogram());

    }

  }

  @Override
  public String toString ()
  {
    return name;
  }

  /**
   * This function creates the JSON schema of the activity, when the user
   * demands the export of the activity model from the Training Module to the
   * main Cassandra Platform.
   * 
   * @param personID
   *          This is the id of the person entity in which the activity will be
   *          put under in the library tree.
   * 
   * @return a DBObject that is defined in accordance with the Activity JSON
   *         schema.
   */
  public DBObject activityToJSON (String personID)
  {

    DBObject temp = new BasicDBObject();

    temp.put("name", nameActivity);
    temp.put("type", type);
    temp.put("description", nameActivity + " " + type);
    temp.put("pers_id", personID);

    return temp;

  }

  /**
   * This function creates the JSON schema of the activity model, when the user
   * demands the export of the activity model from the Training Module to the
   * main Cassandra Platform.
   * 
   * @param appliancesID
   *          This is the array of ids of the appliances contained in the
   *          activity model.
   * 
   * @return a DBObject that is defined in accordance with the Activity
   *         Model JSON schema.
   */
  public DBObject toJSON (String[] appliancesID)
  {

    DBObject temp = new BasicDBObject();

    temp.put("name", name);
    temp.put("type", type);
    temp.put("description", name + " " + type);
    temp.put("shiftable", shiftable);
    temp.put("day_type", dayType);
    temp.put("containsAppliances", appliancesID);
    temp.put("act_id", activityID);
    temp.put("duration", durationID);
    temp.put("startTime", startID);
    temp.put("repeatsNrOfTime", dailyID);

    return temp;

  }

  /**
   * This function creates the JSON schema of the activity model, when the user
   * demands the export of the activity model from the Training Module to the
   * main Cassandra Platform.
   * 
   * @param appliancesID
   *          This is the array of ids of the appliances contained in the
   *          activity model.
   * @param activityID
   *          This is the id of the activity in which the activity model will be
   *          contained in the Library.
   * 
   * @return a DBObject that is defined in accordance with the Activity
   *         Model JSON schema.
   */
  public DBObject toJSON (String[] appliancesID, String activityID)
  {

    DBObject temp = new BasicDBObject();

    temp.put("name", name);
    temp.put("type", type);
    temp.put("description", name + " " + type);
    temp.put("shiftable", shiftable);
    temp.put("day_type", dayType);
    temp.put("containsAppliances", appliancesID);
    temp.put("act_id", activityID);
    temp.put("duration", durationID);
    temp.put("startTime", startID);
    temp.put("repeatsNrOfTime", dailyID);

    return temp;

  }

  /**
   * This function is used to present the basic information of the activity
   * model on the console.
   */
  public void status ()
  {
    System.out.println("Name: " + name);
    System.out.println("Type: " + type);
    System.out.println("Activity: " + nameActivity);
    System.out.println("Day Type: " + dayType);
    System.out.println("Shiftable: " + shiftable);
    System.out.println("Appliance Of: " + Arrays.toString(applianceOf));
    System.out.println("Person:" + person);
    System.out.println("Distribution Types:" + distributionTypes.toString());
    System.out.println("File Map:" + fileMap.toString());
  }

}
