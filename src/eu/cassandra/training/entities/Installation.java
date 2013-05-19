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
package eu.cassandra.training.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jfree.chart.ChartPanel;

import eu.cassandra.training.utils.ChartUtils;

public class Installation
{
  String name;
  ArrayList<Appliance> appliances;
  Person person;
  String measurementsFile;
  double[] activePower = null;
  double[] reactivePower = null;

  public Installation ()
  {
    name = "";
    measurementsFile = "";
    appliances = new ArrayList<Appliance>();
    person = null;
  }

  public Installation (String filename, boolean power) throws IOException
  {
    File file = new File(filename);
    name = file.getName().substring(0, file.getName().length() - 4);
    measurementsFile = filename;
    appliances = new ArrayList<Appliance>();
    person = new Person("Person", name);

    parseMeasurementsFile(power);

  }

  public void addAppliance (Appliance appliance)
  {
    appliances.add(appliance);
  }

  public Appliance findAppliance (String name)
  {

    Appliance result = null;

    for (Appliance appliance: appliances) {

      if (appliance.getName().equalsIgnoreCase(name)) {
        result = appliance;
        break;
      }
    }
    return result;
  }

  public String getName ()
  {

    return name;
  }

  public ArrayList<Appliance> getAppliances ()
  {

    return appliances;
  }

  public Person getPerson ()
  {

    return person;
  }

  public String getMeasurementsFile ()
  {

    return measurementsFile;
  }

  public double[] getActivePower ()
  {

    return activePower;
  }

  public double getActivePower (int index)
  {

    return activePower[index];
  }

  public double[] getReactivePower ()
  {

    return reactivePower;
  }

  public double getReactivePower (int index)
  {

    return reactivePower[index];
  }

  public void parseMeasurementsFile (boolean power) throws IOException
  {

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

        temp.add(Double.parseDouble(line.split(",")[1]));

        if (!power)
          temp2.add(Double.parseDouble(line.split(",")[2]));

      }

      scanner.close();
      break;

    case "xls":

      HSSFWorkbook workbook =
        new HSSFWorkbook(new FileInputStream(measurementsFile));

      // Get the first sheet.
      HSSFSheet sheet = workbook.getSheetAt(0);
      for (int i = 0; i < sheet.getLastRowNum(); i++) {

        // Set value of the first cell.
        HSSFRow row = sheet.getRow(i + 1);
        temp.add(row.getCell(1).getNumericCellValue());
        if (!power)
          temp2.add(row.getCell(2).getNumericCellValue());
      }

      break;

    }

    activePower = new double[temp.size()];

    for (int i = 0; i < temp.size(); i++)
      activePower[i] = temp.get(i);

    if (!power) {
      reactivePower = new double[temp2.size()];
      for (int i = 0; i < temp2.size(); i++)
        reactivePower[i] = temp2.get(i);
    }

  }

  public ChartPanel measurementsChart (boolean power) throws IOException
  {

    if (power)
      return ChartUtils.createLineDiagram(name + " Measurements", "Time Step",
                                          "Power", activePower);
    else

      return ChartUtils.createLineDiagram(name + " Measurements", "Time Step",
                                          "Power", activePower, reactivePower);
  }

  public void clear ()
  {
    appliances.clear();
  }

  public String toString ()
  {
    return name;
  }

  public void status ()
  {
    System.out.println("Name: " + name);
    System.out.println("Measurement File: " + measurementsFile);
    System.out.println("Appliances: " + appliances.toString());
    System.out.println("Person: " + person.toString());
    System.out.println("Active Power:" + activePower.toString());
    System.out.println("Reactive Power:" + reactivePower.toString());
  }
}
