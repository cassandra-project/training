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

import java.io.FileNotFoundException;
import java.util.Arrays;

import org.jfree.chart.ChartPanel;

import eu.cassandra.training.behaviour.BehaviourModel;
import eu.cassandra.training.behaviour.Histogram;
import eu.cassandra.training.utils.ChartUtils;
import eu.cassandra.training.utils.Utils;

public class ResponseModel extends BehaviourModel
{

  PeakFinder pf = null;
  ValleyFinder vf = null;
  String responseType = "";

  public ResponseModel ()
  {
    super();
  }

  public ResponseModel (BehaviourModel behaviour, String person,
                        int responseType) throws FileNotFoundException
  {
    applianceOf = new String[0];
    applianceOf = behaviour.getAppliancesOf();
    nameActivity = person + " " + applianceOf[0] + " Response Activity";
    name = nameActivity + " Response Model";
    this.person = person;
    switch (responseType) {

    case 0:
      this.responseType = "Best";
      break;
    case 1:
      this.responseType = "Normal";
      break;
    case 2:
      this.responseType = "Worst";
    }

    name = name + " (" + this.responseType + ")";
    fileMap = behaviour.getFileMap();
    distributionTypes = behaviour.getDistributionTypes();
    consumptionEventRepo = behaviour.getConsumptionEventRepo();

    String[] types =
      { "DailyTimes", "Duration", "StartTime", "StartTimeBinned" };

    for (int i = 0; i < types.length; i++)
      fillDistribution(fileMap.get(types[i]), distributionTypes.get(types[i]),
                       i);

  }

  public static ChartPanel previewResponseModel (BehaviourModel behaviour,
                                                 int responseType,
                                                 double[] basicScheme,
                                                 double[] newScheme)
  {
    // PeakFinder pf =
    // new
    // PeakFinder(behaviour.getStartTimeBinnedDistribution().getHistogram());
    //
    // int peakIndex = pf.findGlobalMaximum().getIndexMinute();

    double[] before =
      Arrays.copyOf(behaviour.getStartTimeBinned().getHistogram(), behaviour
              .getStartTimeBinned().getHistogram().length);

    // System.out.println(peakIndex);

    double[] after =
      behaviour.getStartTime().shiftingPreview(responseType, basicScheme,
                                               newScheme);

    return ChartUtils.createResponseHistogram("Response",
                                              "10 Minute Intervals",
                                              "Probability", before, after);

  }

  public void respond (int responseType, double[] basicScheme,
                       double[] newScheme)
  {

    startTime.shifting(responseType, basicScheme, newScheme);

    startTimeBinned =
      new Histogram(name + "Start Time Binned",
                    Utils.aggregateStartTimeDistribution(startTime
                            .getHistogram()));

  }

  public String toString ()
  {
    return name;
  }

  public void status ()
  {
    super.status();
    System.out.println("Response Type:" + responseType);
  }
}
