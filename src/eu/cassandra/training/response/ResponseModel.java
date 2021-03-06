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

package eu.cassandra.training.response;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.jfree.chart.ChartPanel;

import eu.cassandra.training.activity.ActivityModel;
import eu.cassandra.training.activity.Histogram;
import eu.cassandra.training.entities.Person;
import eu.cassandra.training.utils.ChartUtils;
import eu.cassandra.training.utils.Utils;

/**
 * This class is used for implementing the Response models created in the
 * Training Module of Cassandra Project. The models created here are compatible
 * with the response models in the platform and can be exported as such.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class ResponseModel extends ActivityModel
{

  /**
   * This variable shows the type of response scenario this response model
   * represents.
   */
  String responseType = "";

  /**
   * This variable shows the activity model that was used as a base for the
   * response model.
   */
  ActivityModel activityModel;

  /**
   * Simple constructor of an Response model.
   */
  public ResponseModel ()
  {
    super();
  }

  /**
   * A constructor of an Response model used in case we know some of the input
   * variables.
   * 
   * @param activity
   *          The base Activity model selected from the user
   * @param person
   *          The Person model this Response model is corresponding to.
   * @param responseType
   *          The selected response type from the user (Optimal, Normal,
   *          Discrete Case)
   * @throws IOException
   */
  public ResponseModel (ActivityModel activity, Person person, int responseType)
    throws IOException
  {
    appliancesOf = activity.getAppliancesOf();
    activityModel = activity;
    if (activity.getActivity()) {

      String temp = activity.getNameActivity().replace(" Activity", "");

      nameActivity = temp + " Response Activity";
      name = temp + " Response Model";
    }
    else {
      nameActivity =
        person + " " + appliancesOf[0].getName() + " Response Activity";
      name = person + " " + appliancesOf[0].getName() + " Response Model";
    }

    this.person = person;
    switch (responseType) {

    case 0:
      this.responseType = "Optimal";
      break;
    case 1:
      this.responseType = "Normal";
      break;
    case 2:
      this.responseType = "Discrete";
    }

    name = name + " (" + this.responseType + ")";
    fileMap = new HashMap<String, String>(activity.getFileMap());
    distributionTypes =
      new HashMap<String, String>(activity.getDistributionTypes());
    consumptionEventRepo = activity.getConsumptionEventRepo();

    String[] types =
      { "DailyTimes", "Duration", "StartTime", "StartTimeBinned" };

    for (int i = 0; i < types.length; i++)
      fillDistribution(fileMap.get(types[i]), distributionTypes.get(types[i]),
                       i);

    Utils.estimateExpectedPower(this);
  }

  /**
   * It enables the creation of a response model based on the user's
   * preferences.
   * 
   * @param responseType
   *          The selected response type.
   * @param basicScheme
   *          The imported basic pricing scheme.
   * @param newScheme
   *          The imported new pricing scheme.
   * @throws IOException
   */
  public void respond (int responseType, double[] basicScheme,
                       double[] newScheme, float awareness, float sensitivity)
    throws IOException
  {

    double energyRatio = Utils.estimateEnergyRatio(basicScheme, newScheme);

    dailyTimes =
      new Histogram(name + " Start Time",
                    dailyTimes.shiftingDailyPreview(energyRatio, awareness,
                                                    sensitivity));

    startTime =
      new Histogram(name + " Start Time", activityModel.getStartTime()
              .shiftingPreview(responseType, basicScheme, newScheme, awareness,
                               sensitivity));

    distributionTypes.put("StartTime", "Histogram");
    distributionTypes.put("StartTimeBinned", "Histogram");

    startTimeBinned =
      new Histogram(name + "Start Time Binned",
                    Utils.aggregateStartTimeDistribution(startTime
                            .getHistogram()));

    Utils.estimateExpectedPower(this);
  }

  /**
   * It enables the creation of a graphical representation of a response model
   * based on the user's preferences.
   * 
   * @param activity
   *          The selected base Activity Model.
   * @param responseType
   *          The selected response type.
   * @param basicScheme
   *          The imported basic pricing scheme.
   * @param newScheme
   *          The imported new pricing scheme.
   * @param awareness
   *          The awareness of the person.
   * @param sensitivity
   *          The sensitivity of the person.
   * @return a chart panel with the resulting Response model graphical
   *         representation.
   */
  public static ChartPanel previewResponseModel (ActivityModel activity,
                                                 int responseType,
                                                 double[] basicScheme,
                                                 double[] newScheme,
                                                 float awareness,
                                                 float sensitivity)
  {

    double[] before = null;

    if (activity.getDistributionTypes().get("StartTime")
            .equalsIgnoreCase("Histogram")) {
      before =
        Arrays.copyOf(activity.getStartTime().getHistogram(), activity
                .getStartTime().getHistogram().length);

      before = Utils.aggregateStartTimeDistribution(before);
    }
    else {
      before =
        Arrays.copyOf(activity.getStartTimeBinned().getHistogram(), activity
                .getStartTimeBinned().getHistogram().length);
    }

    double[] after =
      activity.getStartTime()
              .shiftingPreview(responseType, basicScheme, newScheme, awareness,
                               sensitivity);

    after = Utils.aggregateStartTimeDistribution(after);

    return ChartUtils.createResponseHistogram("Response",
                                              "10 Minute Intervals",
                                              "Probability", before, after);

  }

  /**
   * It enables the creation of a graphical representation of the daily times
   * response model
   * based on the user's preferences.
   * 
   * @param activity
   *          The selected base Activity Model.
   * @param energyRatio
   *          The energy ratio of the given pricing schemes
   * @param awareness
   *          The awareness of the person.
   * @param sensitivity
   *          The sensitivity of the person.
   * @return a chart panel with the resulting Response model graphical
   *         representation.
   */
  public static ChartPanel previewDailyResponseModel (ActivityModel activity,
                                                      double energyRatio,
                                                      float awareness,
                                                      float sensitivity)
  {

    double[] before =
      Arrays.copyOf(activity.getDailyTimes().getHistogram(), activity
              .getDailyTimes().getHistogram().length);

    double[] after =
      activity.getDailyTimes().shiftingDailyPreview(energyRatio, awareness,
                                                    sensitivity);

    return ChartUtils
            .createDailyResponseHistogram("Daily Times Response",
                                          "Times Per Day", "Probability",
                                          before, after);

  }

  @Override
  public void status ()
  {
    super.status();
    System.out.println("Response Type:" + responseType);
  }

  @Override
  public String toString ()
  {
    return name;
  }
}
