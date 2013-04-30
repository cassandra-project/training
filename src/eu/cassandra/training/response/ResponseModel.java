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

import eu.cassandra.training.behaviour.BehaviourModel;

public class ResponseModel extends BehaviourModel
{

  PeakFinder pf = null;
  ValleyFinder vf = null;

  public ResponseModel ()
  {
    super();
  }

  public void createResponseModel (int responseType, double[] basicScheme,
                                   double[] newScheme)
  {
    // {
    //
    // PeakFinder pf = new PeakFinder(startTimeBinned.getHistogram());
    //
    // int peakIndex = pf.findGlobalMaximum().getIndexMinute();
    //
    // System.out.println(peakIndex);
    //
    // startTimeBinned.movePeak(peakIndex, 3);
    //
  }

  public double[] respond (String name, int responseType, double[] basicScheme,
                           double[] newScheme) throws FileNotFoundException
  {
    // double[] newActive = Arrays.copyOf(activePower, activePower.length);
    // double[] newReactive = Arrays.copyOf(reactivePower,
    // reactivePower.length);
    //
    // String[] types =
    // { "DailyTimes", "Duration", "StartTime", "StartTimeBinned" };
    //
    // String[] distributions = new String[types.length];
    // String[] fileString = new String[types.length];
    //
    // for (int i = 0; i < types.length; i++) {
    // distributions[i] = distributionTypes.get(types[i]);
    // fileString[i] = fileMap.get(types[i]);
    // }
    //
    // Appliance newAppliance =
    // new Appliance(name, consumptionModelString, this.consumptionEventRepo,
    // newActive, newReactive);
    //
    // newAppliance.distributionFromFiles(fileString, distributions);
    //
    // newAppliance.createResponse(responseType, basicScheme, newScheme);
    //
    // return newAppliance;
    return null;
  }
}
