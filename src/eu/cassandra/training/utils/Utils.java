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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import eu.cassandra.training.activity.ActivityModel;

/**
 * This class contains static functions that are used for general purposes
 * throughout the Training Module.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */

public class Utils
{

  /**
   * This function is used for aggregating the start time distribution in
   * 10-minute intervals to create the start time binned distribution.
   * 
   * @param values
   *          The start time distribution values' array.
   * @return the start time binned distribution values' array.
   */
  public static double[] aggregateStartTimeDistribution (double[] values)
  {
    double[] result = new double[values.length / Constants.TEN_MINUTES];

    for (int i = 0; i < result.length; i++)
      for (int j = 0; j < Constants.TEN_MINUTES; j++)
        result[i] += values[i * Constants.TEN_MINUTES + j];

    return result;
  }

  /**
   * This function is called when the temporary files must be removed from the
   * temporary folder used to store the csv and xls used to create the entity
   * models during the procedure of training and disaggregation. It is done when
   * the program starts, when the program ends and when the reset button is
   * pressed by the user.
   */
  public static void cleanFiles ()
  {
    File directory = new File(Constants.tempFolder);
    File files[] = directory.listFiles();
    String extension = "";
    for (int index = 0; index < files.length; index++) {
      {
        extension =
          files[index].getAbsolutePath().substring(files[index]
                                                           .getAbsolutePath()
                                                           .length() - 3,
                                                   files[index]
                                                           .getAbsolutePath()
                                                           .length());
        if (extension.equalsIgnoreCase("csv")
            || extension.equalsIgnoreCase("txt")) {
          boolean wasDeleted = files[index].delete();
          if (!wasDeleted) {
            System.out.println("Not Deleted File " + files[index].toString());
          }
        }
      }
    }
  }

  public static void histogramValues (double[] values)
  {
    double sum = 0;

    for (int i = 0; i < values.length; i++)
      sum += values[i];
    System.out.println("Array of Histogram:" + Arrays.toString(values));
    System.out.println("Summary:" + sum);

  }

  /**
   * This is the parser for the measurement file. It parses through the file and
   * checks for errors. It can parse through .csv and .xls file and uses
   * different libraries for each file type.
   * 
   * @param measurementsFile
   *          The file name of the measurements file.
   * @param power
   *          The type of data sets contained within the file (only active or
   *          active and reactive power)
   * @return the line of error or -1 if no error is found.
   * @throws IOException
   */
  public static int parseMeasurementsFile (String measurementsFile,
                                           boolean power) throws IOException
  {

    int result = -1;

    String extension =
      measurementsFile.substring(measurementsFile.length() - 3,
                                 measurementsFile.length());

    switch (extension) {

    case "csv":

      File file = new File(measurementsFile);
      Scanner scanner = new Scanner(file);
      scanner.nextLine();
      int counter = 2;
      while (scanner.hasNext()) {

        String line = scanner.nextLine();

        String[] testString = line.split(",");

        if (power) {
          if (testString.length != 2)
            result = counter;
          try {
            Double.parseDouble(testString[1]);
          }
          catch (NumberFormatException e) {
            result = counter;
          }
        }
        else {
          if (testString.length != 3)
            result = counter;
          try {
            Double.parseDouble(testString[1]);
            Double.parseDouble(testString[2]);
          }
          catch (NumberFormatException e) {
            result = counter;
          }

          if (result != -1)
            break;
        }
        counter++;
      }

      scanner.close();
      System.out.println("Your csv file has been read!");
      break;

    case "xls":

      HSSFWorkbook workbook =
        new HSSFWorkbook(new FileInputStream(measurementsFile));

      // Get the first sheet.
      HSSFSheet sheet = workbook.getSheetAt(0);
      for (int i = 0; i < sheet.getLastRowNum(); i++) {
        // Set value of the first cell.
        HSSFRow row = sheet.getRow(i + 1);

        if (power) {
          if (row.getCell(2) != null)
            result = i + 2;
          try {
            Double.parseDouble(row.getCell(1).toString());
          }
          catch (NumberFormatException e) {
            result = i + 2;
          }
        }
        else {
          if (row.getCell(3) != null)
            result = i + 2;
          try {
            Double.parseDouble(row.getCell(1).toString());
            Double.parseDouble(row.getCell(2).toString());
          }
          catch (NumberFormatException e) {
            result = i + 2;
          }
        }

        if (result != -1)
          break;
      }

      System.out.println("Your excel file has been read!");
      break;

    }

    return result;

  }

  /**
   * This function is used for parsing through the basic pricing schema to check
   * for errors.
   * 
   * @param scheme
   *          The input pricing schema
   * @return the line of error, or -1 if no error found.
   */
  public static int parsePricingScheme (String scheme)
  {
    int result = -1;

    String[] lines = scheme.split("\n");

    int startTime = -1;
    int endTime = -1;
    int counter = 1;
    for (String line: lines) {

      String[] testString = line.split("-");

      if (testString.length != 3) {
        result = counter;
        break;
      }

      String start = line.split("-")[0];

      try {
        Integer.parseInt(start.split(":")[0]);
        Integer.parseInt(start.split(":")[1]);
      }
      catch (NumberFormatException e) {
        result = counter;
        break;
      }

      int startHour = Integer.parseInt(start.split(":")[0]);
      int startMinute = Integer.parseInt(start.split(":")[1]);

      if (startHour > 23 || startHour < 0) {
        result = counter;
        break;
      }

      if (startMinute > 59 || startMinute < 0) {
        result = counter;
        break;
      }

      String end = line.split("-")[1];

      try {
        Integer.parseInt(end.split(":")[0]);
        Integer.parseInt(end.split(":")[1]);
      }
      catch (NumberFormatException e) {
        result = counter;
        break;
      }

      int endHour = Integer.parseInt(end.split(":")[0]);
      int endMinute = Integer.parseInt(end.split(":")[1]);

      if (endHour > 23 || endHour < 0) {
        result = counter;
        break;
      }

      if (endMinute > 59 || endMinute < 0) {
        result = counter;
        break;
      }

      startTime = startHour * 60 + startMinute;
      endTime = endHour * 60 + endMinute;

      if (startTime > endTime) {
        result = counter;
      }
      else {
        try {
          Double.parseDouble(line.split("-")[2]);
        }
        catch (NumberFormatException e) {
          result = counter;
        }
      }

      if (result != -1)
        break;

      counter++;
    }

    return result;
  }

  /**
   * This function is used for parsing through the basic pricing schema and
   * returns the array of prices for the daily schedule.
   * 
   * @param scheme
   *          The input pricing schema
   * @return an array of the prices by minute of day.
   */
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

      // System.out.println("Start: " + startTime + " End: " + endTime);

      double value = Double.parseDouble(line.split("-")[2]);

      if (startTime < endTime) {
        for (int i = startTime; i <= endTime; i++)
          data[i] = value;
      }
    }

    return data;
  }

  public static double estimateEnergyRatio (double[] basicScheme,
                                            double[] newScheme)
  {
    double baseEnergy = 0;
    double newEnergy = 0;

    for (int i = 0; i < basicScheme.length; i++) {
      baseEnergy += basicScheme[i];
      newEnergy += newScheme[i];
    }

    double energyRatio = newEnergy / baseEnergy;

    System.out.println("Energy Ratio: " + energyRatio);

    return energyRatio;
  }

  public static void estimateExpectedPower (ActivityModel activity)
    throws IOException
  {

    int durationMax =
      Math.min(Constants.MINUTES_PER_DAY, activity.getDurationMax());

    double[] result = new double[Constants.MINUTES_PER_DAY];

    System.out.println("Activity " + activity.getName() + " duration max: "
                       + durationMax);

    System.out.println("Appliances for Activity: "
                       + Arrays.toString(activity.getAppliancesOf()));

    // System.out.println("Duration Histogram: "
    // + Arrays.toString(activity.getDuration()
    // .getHistogram()));
    //
    // System.out.println("Greater Probability: "
    // + Arrays.toString(activity.getDuration()
    // .getGreaterProbability()));

    for (int i = 0; i < activity.getAppliancesOf().length; i++) {

      Double[] applianceConsumption =
        activity.getAppliancesOf()[i].getActiveConsumptionModel();

      boolean staticConsumption =
        activity.getAppliancesOf()[i].getStaticConsumption();

      for (int j = 0; j < result.length; j++)
        result[j] +=
          aggregatedProbability(activity, applianceConsumption, j, durationMax,
                                staticConsumption);

    }

    System.out.println(Arrays.toString(result));

    int days = activity.getDays();

    for (int i = 0; i < result.length; i++)
      result[i] *= days;

    System.out.println(Arrays.toString(result));

    activity.setExpectedPower(result);

  }

  private static double aggregatedProbability (ActivityModel activity,
                                               Double[] consumption, int index,
                                               int durationMax,
                                               boolean staticConsumption)
  {

    double result = 0;

    for (int i = 0; i < durationMax; i++) {

      // System.out
      // .println("Index "
      // + ((TrainingConstants.MINUTES_PER_DAY + index - i) %
      // TrainingConstants.MINUTES_PER_DAY)
      // + ": Duration Probability: "
      // + activity.getDuration().getProbabilityGreaterEqual(i)
      // + " Start Time Probability: "
      // + activity.getStartTime().getProbability(index - i)
      // + " Consumption: " + consumption[i % consumption.length]);

      if (staticConsumption)
        result +=
          activity.getDuration().getProbabilityGreaterEqual(i)
                  * activity
                          .getStartTime()
                          .getProbability((Constants.MINUTES_PER_DAY + index - i)
                                                  % Constants.MINUTES_PER_DAY)
                  * consumption[0];
      else
        result +=
          activity.getDuration().getProbabilityGreaterEqual(i)
                  * activity
                          .getStartTime()
                          .getProbability((Constants.MINUTES_PER_DAY + index - i)
                                                  % Constants.MINUTES_PER_DAY)
                  * consumption[i % consumption.length];

    }

    return result;

  }

}
