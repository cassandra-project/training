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

package eu.cassandra.training.utils;

import eu.cassandra.training.entities.Appliance;

public class ImportUtils
{

  protected static double[] activePower;
  protected static double[] reactivePower;

  public static Appliance importEvents (String filename)
  {
    Appliance appliance = new Appliance();
    return appliance;
  }

  public static double[] parseScheme (String scheme)
  {
    double[] data = new double[Constants.MINUTES_PER_DAY];

    String[] lines = scheme.split("\n");

    int startTime = -1;
    int endTime = -1;

    for (String line: lines) {

      String start = line.split("-")[0];

      int startHour = Integer.parseInt(start.split(":")[0]);
      int startMinute = Integer.parseInt(start.split(":")[1]);

      String end = line.split("-")[1];

      int endHour = Integer.parseInt(end.split(":")[0]);
      int endMinute = Integer.parseInt(end.split(":")[1]);

      startTime = startHour * 60 + startMinute;
      endTime = endHour * 60 + endMinute;

      System.out.println("Start: " + startTime + " End: " + endTime);

      double value = Double.parseDouble(line.split("-")[2]);

      if (startTime < endTime) {
        for (int i = startTime; i <= endTime; i++)
          data[i] = value;
      }
    }

    return data;
  }
}
