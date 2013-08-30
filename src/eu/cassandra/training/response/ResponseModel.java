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
  public ResponseModel (ActivityModel activity, String person, int responseType)
    throws IOException
  {
    applianceOf = activity.getAppliancesOf();
    activityModel = activity;
    if (activity.getActivity()) {

      String temp = activity.getNameActivity().replace(" Activity", "");

      nameActivity = temp + " Response Activity";
      name = temp + " Response Model";
    }
    else {
      nameActivity = person + " " + applianceOf[0] + " Response Activity";
      name = person + " " + applianceOf[0] + " Response Model";
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
   * @return a chart panel with the resulting Response model graphical
   *         representation.
   */
  public static ChartPanel previewResponseModel (ActivityModel activity,
                                                 int responseType,
                                                 double[] basicScheme,
                                                 double[] newScheme)
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
      activity.getStartTime().shiftingPreview(responseType, basicScheme,
                                              newScheme);

    after = Utils.aggregateStartTimeDistribution(after);

    return ChartUtils.createResponseHistogram("Response",
                                              "10 Minute Intervals",
                                              "Probability", before, after);

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
   */
  public void respond (int responseType, double[] basicScheme,
                       double[] newScheme)
  {

    startTime =
      new Histogram(name + " Start Time", activityModel.getStartTime()
              .shiftingPreview(responseType, basicScheme, newScheme));

    distributionTypes.put("StartTime", "Histogram");
    distributionTypes.put("StartTimeBinned", "Histogram");

    startTimeBinned =
      new Histogram(name + "Start Time Binned",
                    Utils.aggregateStartTimeDistribution(startTime
                            .getHistogram()));

  }

  @Override
  public String toString ()
  {
    return name;
  }

  @Override
  public void status ()
  {
    super.status();
    System.out.println("Response Type:" + responseType);
  }
}
