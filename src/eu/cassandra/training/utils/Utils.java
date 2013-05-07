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
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Utils
{

  public static boolean parseMeasurementsFile (String measurementsFile,
                                               boolean power)
    throws IOException
  {

    boolean result = true;

    ArrayList<Double> temp = new ArrayList<Double>();
    ArrayList<Double> temp2 = new ArrayList<Double>();

    String extension =
      measurementsFile.substring(measurementsFile.length() - 3,
                                 measurementsFile.length());

    switch (extension) {

    case "csv":

      File file = new File(measurementsFile);
      Scanner scanner = new Scanner(file);
      scanner.nextLine();
      while (scanner.hasNext()) {

        String line = scanner.nextLine();

        String[] testString = line.split(",");

        if (power) {
          result = (testString.length == 2);
          try {
            Double.parseDouble(testString[1]);
          }
          catch (NumberFormatException e) {
            result = false;
          }
        }
        else {
          result = (testString.length == 3);
          try {
            Double.parseDouble(testString[1]);
            Double.parseDouble(testString[2]);
          }
          catch (NumberFormatException e) {
            result = false;
          }

          if (result == false)
            break;
        }
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
          result = (row.getCell(2) == null);
          try {
            Double.parseDouble(row.getCell(1).toString());
          }
          catch (NumberFormatException e) {
            result = false;
          }
        }
        else {
          result = (row.getCell(3) == null);
          try {
            Double.parseDouble(row.getCell(1).toString());
            Double.parseDouble(row.getCell(2).toString());
          }
          catch (NumberFormatException e) {
            result = false;
          }
        }

        if (result == false)
          break;
      }

      System.out.println("Your excel file has been read!");
      break;

    }

    return result;

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

      // System.out.println("Start: " + startTime + " End: " + endTime);

      double value = Double.parseDouble(line.split("-")[2]);

      if (startTime < endTime) {
        for (int i = startTime; i <= endTime; i++)
          data[i] = value;
      }
    }

    return data;
  }

  public static boolean parsePricingScheme (String scheme)
  {
    boolean result = true;

    String[] lines = scheme.split("\n");

    int startTime = -1;
    int endTime = -1;

    for (String line: lines) {

      String[] testString = line.split("-");

      if (testString.length != 3) {
        result = false;
        break;
      }

      String start = line.split("-")[0];

      try {
        Integer.parseInt(start.split(":")[0]);
        Integer.parseInt(start.split(":")[1]);
      }
      catch (NumberFormatException e) {
        result = false;
        break;
      }

      int startHour = Integer.parseInt(start.split(":")[0]);
      int startMinute = Integer.parseInt(start.split(":")[1]);

      String end = line.split("-")[1];

      try {
        Integer.parseInt(end.split(":")[0]);
        Integer.parseInt(end.split(":")[1]);
      }
      catch (NumberFormatException e) {
        result = false;
        break;
      }

      int endHour = Integer.parseInt(end.split(":")[0]);
      int endMinute = Integer.parseInt(end.split(":")[1]);

      startTime = startHour * 60 + startMinute;
      endTime = endHour * 60 + endMinute;

      if (startTime > endTime) {
        result = false;
      }
      else {
        try {
          Double.parseDouble(line.split("-")[2]);
        }
        catch (NumberFormatException e) {
          result = false;
        }
      }

      if (!result)
        break;

    }

    return result;
  }

  public static double[] aggregateStartTimeDistribution (double[] values)
  {
    double[] result = new double[values.length / Constants.MINUTES_PER_BIN];

    for (int i = 0; i < result.length; i++)
      for (int j = 0; j < Constants.MINUTES_PER_BIN; j++)
        result[i] += values[i * Constants.MINUTES_PER_BIN + j];

    return result;
  }
}
