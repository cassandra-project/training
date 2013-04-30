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
import eu.cassandra.training.utils.ChartUtils;

public class ResponseModel extends BehaviourModel
{

  PeakFinder pf = null;
  ValleyFinder vf = null;

  public ResponseModel ()
  {
    super();
  }

  public ResponseModel (BehaviourModel behaviour) throws FileNotFoundException
  {
    applianceOf = behaviour.getApplianceOf();
    name = applianceOf + " Response Model";
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
    PeakFinder pf =
      new PeakFinder(behaviour.getStartTimeBinnedDistribution().getHistogram());

    int peakIndex = pf.findGlobalMaximum().getIndexMinute();

    double[] before =
      Arrays.copyOf(behaviour.getStartTimeBinnedDistribution().getHistogram(),
                    behaviour.getStartTimeBinnedDistribution().getHistogram().length);

    System.out.println(peakIndex);

    double[] after =
      behaviour.getStartTimeBinnedDistribution().movePeakPreview(peakIndex, 3);

    return ChartUtils.createResponseHistogram("Response",
                                              "10 Minute Intervals",
                                              "Probability", before, after);

  }

  public void respond (int responseType, double[] basicScheme,
                       double[] newScheme)
  {

    PeakFinder pf = new PeakFinder(startTimeBinned.getHistogram());

    int peakIndex = pf.findGlobalMaximum().getIndexMinute();

    System.out.println(peakIndex);

    startTimeBinned.movePeak(peakIndex, 3);

  }

}
