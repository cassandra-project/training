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

package eu.cassandra.training.consumption;

// This is used to create the test and train folds and create the files 
// for the simulation runs

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.joda.time.DateTime;

import eu.cassandra.training.utils.Constants;

public class EventDetector
{

  // ConsumptionEventRepo consumptionEventRepo = new ConsumptionEventRepo();
  HashMap<String, Integer> upperThresholds = new HashMap<String, Integer>();
  HashMap<String, Integer> lowerThresholds = new HashMap<String, Integer>();
  ConsumptionEventRepo consumptionEventRepo;

  public EventDetector (String home, String appliance) throws SQLException,
    InstantiationException, IllegalAccessException, ClassNotFoundException
  {

    upperThresholds.put("1", 100);
    upperThresholds.put("2", 400);
    upperThresholds.put("5", 100);
    upperThresholds.put("10", 100);
    upperThresholds.put("11", 20);
    upperThresholds.put("12", 100);
    upperThresholds.put("13", 11);
    upperThresholds.put("15", 11);
    upperThresholds.put("18", 11);
    upperThresholds.put("19", 400);
    upperThresholds.put("20", 100);
    upperThresholds.put("22", 180);
    upperThresholds.put("23", 100);

    lowerThresholds.put("1", 0);
    lowerThresholds.put("2", 0);
    lowerThresholds.put("5", 0);
    lowerThresholds.put("10", 0);
    lowerThresholds.put("11", 0);
    lowerThresholds.put("12", 0);
    lowerThresholds.put("13", 0);
    lowerThresholds.put("15", 0);
    lowerThresholds.put("18", 0);
    lowerThresholds.put("19", 0);
    lowerThresholds.put("20", 4);
    lowerThresholds.put("22", 60);
    lowerThresholds.put("23", 0);

    boolean flagStarted = false;

    DateTime dateTimeStart = new DateTime(), dateTimeEnd = new DateTime();
    DateTime dateStart = new DateTime();
    DateTime dateEnd = new DateTime();

    Connection conn = null;
    String url = "jdbc:mysql://localhost/";
    String dbName = "dehems";
    String driver = "com.mysql.jdbc.Driver";
    String userName = "root";
    // String password = "cranberries";
    String password = "";

    String query =
      "select date_minute, energy_usage from data_apptype_minute where uid = "
              + home + " and apptype_id = " + appliance
              + " order by date_minute ASC";

    Class.forName(driver).newInstance();
    conn = DriverManager.getConnection(url + dbName, userName, password);
    Statement stmt = conn.createStatement();

    ResultSet rs = stmt.executeQuery(query);

    while (rs.next()) {

      if (rs.getInt(2) > 500 && flagStarted == false) {
        String dbtime = rs.getString(1);
        int year = Integer.parseInt(dbtime.substring(0, 4));
        // System.out.print("Year: " + year);
        int month = Integer.parseInt(dbtime.substring(4, 6));
        // System.out.print(" Month: " + month);
        int day = Integer.parseInt(dbtime.substring(6, 8));
        // System.out.print(" Day: " + day);
        int hour = Integer.parseInt(dbtime.substring(8, 10));
        // System.out.print(" Hour: " + hour);
        int minute = Integer.parseInt(dbtime.substring(10, 12));
        // System.out.println(" Minute: " + minute);

        dateTimeStart = new DateTime(year, month, day, hour, minute);
        dateStart = new DateTime(year, month, day, 0, 0);

        // consumptionEventRepo.addEventPerDate(dateStart);

        flagStarted = true;
      }

      if (rs.getInt(2) <= 500 && flagStarted == true) {
        String dbtime = rs.getString(1);
        int year = Integer.parseInt(dbtime.substring(0, 4));
        // System.out.print("Year: " + year);
        int month = Integer.parseInt(dbtime.substring(4, 6));
        // System.out.print(" Month: " + month);
        int day = Integer.parseInt(dbtime.substring(6, 8));
        // System.out.print(" Day: " + day);
        int hour = Integer.parseInt(dbtime.substring(8, 10));
        // System.out.print(" Hour: " + hour);
        int minute = Integer.parseInt(dbtime.substring(10, 12));
        // System.out.println(" Minute: " + minute);

        dateTimeEnd = new DateTime(year, month, day, hour, minute);
        dateEnd = new DateTime(year, month, day, 0, 0);

        ConsumptionEvent event =
          new ConsumptionEvent(consumptionEventRepo.getEvents().size() + 1,
                               dateTimeStart, dateStart, dateTimeEnd, dateEnd);

        consumptionEventRepo.addEvent(event, upperThresholds.get(appliance),
                                      lowerThresholds.get(appliance));

        // event.status();

        flagStarted = false;
      }

    }

    consumptionEventRepo.eventsToFile(Constants.EVENTS_FILE + appliance
                                      + ".csv");

    conn.close();
  }

}
