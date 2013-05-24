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

package eu.cassandra.training.behaviour;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import eu.cassandra.training.utils.EM;

public class BehaviourModel
{
  protected static final int DAILY_TIMES = 0;
  protected static final int DURATION = 1;
  protected static final int START_TIME = 2;
  protected static final int START_TIME_BINNED = 3;

  protected String name = "";
  protected String nameActivity = "";
  protected String type = "";
  protected String activityID = "";
  protected String behaviourID = "";
  protected String dailyID = "";
  protected String durationID = "";
  protected String startID = "";
  // protected String startBinnedID = "";

  protected boolean shiftable = false;
  protected String dayType = "working";
  protected String[] applianceOf;
  protected String person;
  protected ConsumptionEventRepo consumptionEventRepo;
  protected ProbabilityDistribution startTime, startTimeBinned, duration,
          dailyTimes;
  protected Map<String, String> fileMap = new HashMap<String, String>();
  protected Map<String, String> distributionTypes =
    new HashMap<String, String>();

  public BehaviourModel ()
  {
    name = "";
    consumptionEventRepo = new ConsumptionEventRepo(name);
  }

  public BehaviourModel (Appliance appliance, String person)
    throws FileNotFoundException
  {
    nameActivity = person + " " + appliance.getName() + " Activity";
    name = person + " " + appliance.getName() + " Behaviour Model";
    this.person = person;
    applianceOf = new String[1];
    applianceOf[0] = appliance.getName();
    consumptionEventRepo = new ConsumptionEventRepo(applianceOf[0]);
    consumptionEventRepo.readEventsFile(appliance.getEventsFile());
  }

  public String getName ()
  {
    return name;
  }

  public String getNameActivity ()
  {
    return nameActivity;
  }

  public String getType ()
  {
    return type;
  }

  public String getDayType ()
  {
    return dayType;
  }

  public boolean getShiftable ()
  {
    return shiftable;
  }

  public String getActivityID ()
  {
    return activityID;
  }

  public String getBehaviourID ()
  {
    return behaviourID;
  }

  public String getDurationID ()
  {
    return durationID;
  }

  public String getDailyID ()
  {
    return dailyID;
  }

  public String getStartID ()
  {
    return startID;
  }

  // public String getStartBinnedID ()
  // {
  // return startBinnedID;
  // }

  public String[] getAppliancesOf ()
  {
    return applianceOf;
  }

  public String getPerson ()
  {
    return person;
  }

  public ConsumptionEventRepo getConsumptionEventRepo ()
  {
    return consumptionEventRepo;
  }

  public ProbabilityDistribution getDailyTimes ()
  {
    return dailyTimes;
  }

  public ProbabilityDistribution getDuration ()
  {
    return duration;
  }

  public ProbabilityDistribution getStartTime ()
  {
    return startTime;
  }

  public ProbabilityDistribution getStartTimeBinned ()
  {
    return startTimeBinned;
  }

  public Map<String, String> getDistributionTypes ()
  {
    return distributionTypes;
  }

  public String getDistributionTypes (String key)
  {
    return distributionTypes.get(key);
  }

  public Map<String, String> getFileMap ()
  {
    return fileMap;
  }

  public String getFileMap (String key)
  {
    return fileMap.get(key);
  }

  public void setActivityID (String id)
  {
    activityID = id;
  }

  public void setBehaviourID (String id)
  {
    behaviourID = id;
  }

  public void setDailyID (String id)
  {
    dailyID = id;
  }

  public void setDurationID (String id)
  {
    durationID = id;
  }

  public void setStartID (String id)
  {
    startID = id;
  }

  // public void setStartBinnedID (String id)
  // {
  // startBinnedID = id;
  // }

  public void train (String[] distributions) throws IOException
  {

    for (int i = 0; i < 4; i++) {

      String file = fileDistribution(i);
      fillDistribution(file, distributions[i], i);

    }

  }

  private String fileDistribution (int variable) throws FileNotFoundException
  {

    String variablePath = "";
    String file = "";

    switch (variable) {

    case DAILY_TIMES:

      variablePath = Constants.DAILY_TIMES_FILE;
      break;

    case DURATION:

      variablePath = Constants.DURATION_FILE;
      break;

    case START_TIME:

      variablePath = Constants.START_TIME_FILE;
      break;

    case START_TIME_BINNED:

      variablePath = Constants.START_TIME_BINNED_FILE;
      break;

    default:

      System.out.println("Distribution Error");

    }

    file = variablePath + name + ".csv";

    // file = variablePath + ".csv";

    // System.out.println("Case: " + variable + " File: " + file);

    return file;
  }

  public void fillDistribution (String file, String type, int index)
    throws IOException
  {

    Scanner input = new Scanner(file);
    String newFile = "Files/";
    EM em = null;
    String variable = "";

    if (index == 0)
      variable = "DailyTimes";
    else if (index == 1)
      variable = "Duration";
    else if (index == 2)
      variable = "StartTime";
    else if (index == 3)
      variable = "StartTimeBinned";

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
        consumptionEventRepo.StartTimeHistogramToFile(file);
        startTime = new Histogram(file);
      }
      else if (index == 3) {
        consumptionEventRepo.StartTimeBinnedHistogramToFile(file);
        startTimeBinned = new Histogram(file);
      }
      else
        System.out.println("ERROR in index");

      break;

    case "Normal":

      newFile += type + variable + name + ".csv";
      consumptionEventRepo.attributeToFile(file, variable);
      em = new EM();
      em.createNormal(file, newFile, variable);

      if (index == 0)
        dailyTimes = new Gaussian(newFile);
      else if (index == 1)
        duration = new Gaussian(newFile);
      else if (index == 2)
        startTime = new Gaussian(newFile);
      else if (index == 3)
        startTimeBinned = new Gaussian(newFile);
      else
        System.out.println("ERROR in index");

      break;

    case "GMM":

      newFile += type + variable + name + ".csv";
      consumptionEventRepo.attributeToFile(file, variable);
      em = new EM();
      em.createGMM(file, newFile, variable);

      if (index == 0)
        dailyTimes = new GaussianMixtureModels(newFile);
      else if (index == 1)
        duration = new GaussianMixtureModels(newFile);
      else if (index == 2)
        startTime = new GaussianMixtureModels(newFile);
      else if (index == 3)
        startTimeBinned = new GaussianMixtureModels(newFile);
      else
        System.out.println("ERROR in index");

      break;

    default:
      System.out.println("ERROR in distribution type");

    }
    // System.out.println(type + " " +variable + " Distribution Created");
    distributionTypes.put(variable, type);
    fileMap.put(variable, file);

    input.close();

  }

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

  public ChartPanel createDailyTimesDistributionChart ()
  {

    String variable = "Daily Times Distribution";
    String x = "Daily Times";
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

  public ChartPanel createStartTimeDistributionChart ()
  {
    String variable = "Start Time Distribution";
    String x = "Start Time";
    String y = "Probability";

    switch (distributionTypes.get("StartTime")) {

    case "Histogram":

      return ChartUtils.createHistogram(variable, x, y,
                                        startTime.getHistogram());

    default:

      return ChartUtils.createMixtureDistribution(variable, x, y,
                                                  startTime.getHistogram());

    }

  }

  public ChartPanel createStartTimeBinnedDistributionChart ()
  {
    String variable = "Start Time Binned Distribution";
    String x = "10 Minutes Interval";
    String y = "Probability";

    // System.out.println(name + " " + distributionTypes.toString());

    switch (distributionTypes.get("StartTimeBinned")) {

    case "Histogram":

      return ChartUtils.createHistogram(variable, x, y,
                                        startTimeBinned.getHistogram());

    default:

      return ChartUtils.createMixtureDistribution(variable, x, y,
                                                  startTimeBinned
                                                          .getHistogram());

    }

  }

  public String toString ()
  {
    return name;
  }

  public DBObject activityToJSON (String personID)
  {

    DBObject temp = new BasicDBObject();

    temp.put("name", nameActivity);
    temp.put("type", type);
    temp.put("description", nameActivity + " " + type);
    temp.put("pers_id", personID);

    return temp;

  }

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

  public void status ()
  {
    System.out.println("Name: " + name);
    System.out.println("Appliance Of: " + applianceOf);
    System.out.println("Person:" + person);
    System.out.println("Distribution Types:" + distributionTypes.toString());
    System.out.println("File Map:" + fileMap.toString());
  }

}
