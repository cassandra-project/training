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
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Utils
{

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
          ;
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

  public static double[] aggregateStartTimeDistribution (double[] values)
  {
    double[] result = new double[values.length / Constants.MINUTES_PER_BIN];

    for (int i = 0; i < result.length; i++)
      for (int j = 0; j < Constants.MINUTES_PER_BIN; j++)
        result[i] += values[i * Constants.MINUTES_PER_BIN + j];

    return result;
  }
}
