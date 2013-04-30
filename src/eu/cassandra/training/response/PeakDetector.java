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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import eu.cassandra.training.utils.ChartUtils;
import eu.cassandra.training.utils.Constants;

public class PeakDetector
{

  public static void main (String[] args)
  {

    Connection conn = null;
    String url = "jdbc:mysql://localhost/";
    String dbName = "dehems";
    String driver = "com.mysql.jdbc.Driver";
    String userName = "root";
    // String password = "cranberries";
    String password = "";

    String date = "20100810";

    int[] INTERVALS = { 0, 36, 72, 108, 144 };

    String[] INTERVALS_STRING = { "Night", "Morning", "Noon", "Evening" };

    String dateMinuteStart = date + "0000";
    String dateMinuteEnd = date + "2359";
    HashMap<String, Peak> peaksPerDayInterval = new HashMap<String, Peak>();

    String query =
      "select distinct uid from data_total_minute where date_minute = "
              + dateMinuteStart + " AND electric > 0 order by uid ASC limit 1";

    try {

      Class.forName(driver).newInstance();
      conn = DriverManager.getConnection(url + dbName, userName, password);
      Statement stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery(query);

      ArrayList<Integer> uids = new ArrayList<Integer>();

      while (rs.next()) {

        uids.add(rs.getInt(1));

      }

      System.out.println("List Of UID:" + uids.toString());
      System.out.println("Size:" + uids.size());

      double[] resultMatrix = new double[Constants.MINUTES_PER_DAY];

      System.out.println("Household 0");
      query =
        "select date_minute, electric from data_total_minute where uid = "
                + uids.get(0) + " AND date_minute BETWEEN " + dateMinuteStart
                + " AND " + dateMinuteEnd + " order by date_minute ASC";

      ResultSet rs2 = stmt.executeQuery(query);
      int counter = 0;

      while (rs2.next()) {

        resultMatrix[counter] = rs2.getLong(2);
        counter++;

      }

      ChartUtils.createHistogram("Consumer 0", "minutes", "Consumption",
                                 resultMatrix);

      PeakFinder pf = new PeakFinder(resultMatrix);
      pf.findLocalIntervalMaxima();
      pf.showListInterval();
      System.out.println(pf.findGlobalIntervalMaximum().toString());

      System.out.println(INTERVALS);

      for (int i = 0; i < INTERVALS.length - 1; i++) {

        peaksPerDayInterval.put(INTERVALS_STRING[i], pf
                .findGlobalIntervalMaxima(INTERVALS[i], INTERVALS[i + 1]));

        System.out.println(INTERVALS_STRING[i]
                           + ": "
                           + peaksPerDayInterval.get(INTERVALS_STRING[i])
                                   .toString());
      }

      conn.close();

      // System.out.println("Disconnected from database");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
