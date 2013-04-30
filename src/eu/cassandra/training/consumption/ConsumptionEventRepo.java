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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import eu.cassandra.training.utils.Constants;

public class ConsumptionEventRepo
{
  String appliance;

  ArrayList<ConsumptionEvent> events = new ArrayList<ConsumptionEvent>();

  Map<DateTime, Integer> numberEventsPerDate = new TreeMap<DateTime, Integer>();

  Map<DateTime, ArrayList<ConsumptionEvent>> eventsPerDate =
    new HashMap<DateTime, ArrayList<ConsumptionEvent>>();

  Map<Integer, Double> eventsDurationHistogram = new TreeMap<Integer, Double>();
  Map<Integer, Double> eventsDailyTimesHistogram =
    new TreeMap<Integer, Double>();
  Map<Integer, Double> eventsStartTimeHistogram =
    new TreeMap<Integer, Double>();
  Map<Integer, Double> eventsStartTimeBinnedHistogram =
    new TreeMap<Integer, Double>();

  // =================CREATION FUNCTIONS==============================//

  public ConsumptionEventRepo (String appliance)
  {

    this.appliance = appliance;

  }

  public void addEvent (ConsumptionEvent e, int upperThreshold,
                        int lowerThreshold)
  {

    long temp = e.getDuration().getStandardMinutes();

    if (temp < upperThreshold && temp > lowerThreshold)
      events.add(e);
  }

  public void createEventPerDateHashmap ()
  {

    Map<DateTime, ArrayList<ConsumptionEvent>> tempMap =
      new HashMap<DateTime, ArrayList<ConsumptionEvent>>();
    ArrayList<ConsumptionEvent> events = getEvents();

    DateTime temp = events.get(0).getStartDate();
    DateTime temp2 = events.get(events.size() - 1).getStartDate();

    while (!temp.isAfter(temp2)) {

      tempMap.put(temp, new ArrayList<ConsumptionEvent>());
      temp = temp.plusDays(1);

    }

    for (int i = 0; i < events.size(); i++) {
      DateTime loop = events.get(i).getStartDate();
      ArrayList<ConsumptionEvent> tempList = tempMap.get(loop);
      tempList.add(events.get(i));
      tempMap.put(loop, tempList);
    }

    eventsPerDate = new TreeMap<DateTime, ArrayList<ConsumptionEvent>>(tempMap);

    for (DateTime date: eventsPerDate.keySet())
      numberEventsPerDate.put(date, eventsPerDate.get(date).size());
    //
    // System.out.println(eventsPerDate.toString());
    // System.out.println(numberEventsPerDate.toString());
  }

  public void clean ()
  {

    numberEventsPerDate.clear();
    eventsPerDate.clear();

    eventsDurationHistogram.clear();
    eventsDailyTimesHistogram.clear();
    eventsStartTimeHistogram.clear();
    eventsStartTimeBinnedHistogram.clear();

  }

  public void cleanEvents ()
  {

    events.clear();

  }

  public void analyze () throws FileNotFoundException
  {

    clean();

    createEventPerDateHashmap();

    System.out.println("Overall Days:" + eventsPerDate.keySet().size());

    createDurationHistogram();
    createDailyTimesHistogram();
    createStartTimeHistogram();
    createStartTimeBinnedHistogram(Constants.MINUTES_PER_BIN,
                                   Constants.NUMBER_OF_BINS);

    // ChartUtils.createHistogram("Duration", "Minutes", "Possibility",
    // eventsDurationHistogram);
    // DurationHistogramToFile();

    // ChartUtils.createHistogram("DailyTimes", "Daily Times",
    // "Possibility",
    // eventsDailyTimesHistogram);
    // DailyTimesHistogramToFile();

    // ChartUtils.createHistogram("StartTime", "Minute Of Day",
    // "Possibility",
    // eventsStartTimeHistogram);
    // StartTimeHistogramToFile();
    // ChartUtils.createHistogram("StartTimeBinned", "Ten-Minute Of Day",
    // "Possibility", eventsStartTimeHistogram);
    // StartTimeBinnedHistogramToFile();
    /*
     * typeToFile(Constants.EVENTS_FILE + temp + appliance + ".csv", temp);
     * 
     * System.out.println("Events Per Date HashMap " +
     * eventsPerDate.toString());
     * System.out.println("Event Duration Histogram " +
     * eventsDurationHistogram.get(i).toString());
     * 
     * System.out.println("Event Daily Times Histogram " +
     * eventsDailyTimesHistogram.get(i).toString());
     * 
     * System.out.println("Event Start Time Histogram " +
     * eventsStartTimeHistogram.get(i).toString());
     * 
     * System.out.println("Event Start Time Binned Histogram " +
     * eventsStartTimeBinnedHistogram.get(i).toString());
     */
  }

  // =========================GETTER
  // FUNCTIONS================================//

  public ArrayList<ConsumptionEvent> getEvents ()
  {
    return events;
  }

  public Map<DateTime, Integer> getNumberEventsPerDate ()
  {

    return numberEventsPerDate;

  }

  public Map<DateTime, ArrayList<ConsumptionEvent>> getEventsPerDate ()
  {

    return eventsPerDate;

  }

  public Map<Integer, Double> getDurationHistogram ()
  {
    return eventsDurationHistogram;
  }

  public Map<Integer, Double> getDailyTimesHistogram ()
  {

    return eventsDailyTimesHistogram;
  }

  public Map<Integer, Double> getStartTimeHistogram ()
  {
    return eventsStartTimeHistogram;
  }

  public Map<Integer, Double> getStartTimeBinnedHistogram ()
  {

    return eventsStartTimeBinnedHistogram;
  }

  // =========================PRINTING
  // FUNCTIONS==============================//

  public void showEvents ()
  {

    for (int i = 0; i < events.size(); i++) {
      events.get(i).status();
    }

  }

  public String showStartDate ()
  {

    DateTimeFormatter fmt2 = DateTimeFormat.forPattern("yyyy-MM-dd");

    return events.get(0).getStartDate().toString(fmt2);
  }

  public String showEndDate ()
  {

    DateTimeFormatter fmt2 = DateTimeFormat.forPattern("yyyy-MM-dd");

    return events.get(events.size() - 1).getStartDate().toString(fmt2);
  }

  // =========================HISTOGRAM FUNCTIONS===========================//

  public void createDurationHistogram ()
  {

    Map<Integer, Double> tempDurationHistogram = new HashMap<Integer, Double>();
    ArrayList<ConsumptionEvent> events = getEvents();

    for (int i = 0; i < events.size(); i++) {

      Integer temp = (int) events.get(i).getDuration().getStandardMinutes();

      if (tempDurationHistogram.containsKey(temp))
        tempDurationHistogram.put(temp, tempDurationHistogram.get(temp) + 1);
      else
        tempDurationHistogram.put(temp, Double.valueOf(1));
    }

    // System.out.println(tempDurationHistogram.toString());
    double sum = 0;

    for (Integer duration: tempDurationHistogram.keySet()) {

      tempDurationHistogram.put(duration,
                                Double.valueOf(tempDurationHistogram
                                        .get(duration) / events.size()));

      sum += tempDurationHistogram.get(duration);
    }

    eventsDurationHistogram =
      new TreeMap<Integer, Double>(tempDurationHistogram);
    /*
     * System.out.print("Number of Events for type " + type + ":" +
     * events.size() + " "); System.out.println(sortedMap.toString());
     * System.out.println(sum);
     */

  }

  public void createDailyTimesHistogram ()
  {
    Map<Integer, Double> tempDailyTimesHistogram =
      new HashMap<Integer, Double>();

    Map<DateTime, Integer> eventsPerDate = getNumberEventsPerDate();

    for (DateTime date: eventsPerDate.keySet()) {

      Integer temp = eventsPerDate.get(date);

      if (tempDailyTimesHistogram.containsKey(temp))
        tempDailyTimesHistogram
                .put(temp, tempDailyTimesHistogram.get(temp) + 1);
      else
        tempDailyTimesHistogram.put(temp, Double.valueOf(1));
    }

    // System.out.println(tempDailyTimesHistogram.toString());
    double total = 0;

    for (Integer times: tempDailyTimesHistogram.keySet()) {

      total += tempDailyTimesHistogram.get(times);

    }

    double sum = 0;
    for (Integer dailyTimes: tempDailyTimesHistogram.keySet()) {

      tempDailyTimesHistogram.put(dailyTimes,
                                  Double.valueOf(tempDailyTimesHistogram
                                          .get(dailyTimes) / total));

      sum += tempDailyTimesHistogram.get(dailyTimes);
    }

    eventsDailyTimesHistogram =
      new TreeMap<Integer, Double>(tempDailyTimesHistogram);

    // System.out.println(eventsDailyTimesHistogram.toString());
    /*
     * System.out.print("Number of Events for type " + type + ":" +
     * events.size() + " ");
     * System.out.println(tempDailyTimesHistogram.toString());
     * System.out.println(sum);
     */

  }

  public void createStartTimeHistogram ()
  {

    Map<Integer, Double> tempStartTimeHistogram =
      new HashMap<Integer, Double>();

    ArrayList<ConsumptionEvent> events = getEvents();

    for (int i = 0; i < events.size(); i++) {

      Integer temp = events.get(i).getStartMinuteOfDay();

      if (tempStartTimeHistogram.containsKey(temp))
        tempStartTimeHistogram.put(temp, tempStartTimeHistogram.get(temp) + 1);
      else
        tempStartTimeHistogram.put(temp, Double.valueOf(1));
    }

    // System.out.println(tempStartTimeHistogram.toString());
    double sum = 0;

    for (Integer startTime: tempStartTimeHistogram.keySet()) {

      tempStartTimeHistogram.put(startTime,
                                 Double.valueOf(tempStartTimeHistogram
                                         .get(startTime) / events.size()));

      sum += tempStartTimeHistogram.get(startTime);
    }

    eventsStartTimeHistogram =
      new TreeMap<Integer, Double>(tempStartTimeHistogram);
    /*
     * System.out.print("Number of Events for type " + type + ":" +
     * events.size() + " ");
     * System.out.println(tempStartTimeHistogram.toString());
     * System.out.println(sum);
     */

  }

  public void
    createStartTimeBinnedHistogram (int minuteInterval, int intervals)
  {

    int fold;

    Map<Integer, Double> tempStartTimeBinnedHistogram =
      new HashMap<Integer, Double>();

    Map<Integer, Double> eventsStartTimeHistogram = getStartTimeHistogram();

    // System.out.println(eventsStartTimeHistogram.toString());

    for (int i = 0; i < intervals; i++) {

      double tempSum = 0;

      for (int j = 0; j < minuteInterval; j++) {

        int tick = minuteInterval * i + j;
        if (eventsStartTimeHistogram.containsKey(tick))
          tempSum += eventsStartTimeHistogram.get(tick);
      }

      tempStartTimeBinnedHistogram.put(i, tempSum);

    }

    double sum = 0;

    for (Integer startTime: tempStartTimeBinnedHistogram.keySet()) {

      sum += tempStartTimeBinnedHistogram.get(startTime);
    }

    eventsStartTimeBinnedHistogram =
      new TreeMap<Integer, Double>(tempStartTimeBinnedHistogram);
    /*
     * System.out.println(tempStartTimeBinnedHistogram.toString());
     * System.out.println(sum);
     */

  }

  private double[] Map2Double (int max, Map<Integer, Double> temp)
  {

    double[] result = new double[max];

    for (int i = 0; i < max; i++) {
      if (temp.containsKey(i))
        result[i] = temp.get(i);
      else
        result[i] = 0;
    }

    return result;
  }

  // =========================FILE CREATING
  // FUNCTIONS========================//

  public void eventsToFile (String filename)
  {
    try {

      DateTime startBase = events.get(0).getStartDate();
      DateTime endBase = events.get(events.size() - 1).getEndDate().plusDays(1);

      long endTick =
        new Interval(startBase, endBase).toDuration().getStandardMinutes();

      PrintStream realSystemOut = System.out;
      OutputStream output = new FileOutputStream(filename);
      PrintStream printOut = new PrintStream(output);
      System.setOut(printOut);

      System.out.println("End:" + endTick);

      for (int i = 0; i < events.size(); i++) {

        long startDistance =
          new Interval(startBase, events.get(i).getStartDateTime())
                  .toDuration().getStandardMinutes();

        long endDistance =
          new Interval(startBase, events.get(i).getEndDateTime()).toDuration()
                  .getStandardMinutes();

        System.out.println(startDistance + "-" + endDistance);

      }

      System.setOut(realSystemOut);

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void daysToFile (String filename, int[] daysArray)
  {
    try {

      PrintStream realSystemOut = System.out;
      OutputStream output = new FileOutputStream(filename + ".csv");
      PrintStream printOut = new PrintStream(output);
      System.setOut(printOut);

      System.out.println("Overall Days:" + eventsPerDate.keySet().size());

      for (int i = 0; i < daysArray.length; i++)
        System.out.println(daysArray[i]);

      System.setOut(realSystemOut);

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void attributeToFile (String filename, String attribute)
  {
    try {

      ArrayList<ConsumptionEvent> events = getEvents();
      Map<DateTime, Integer> numberEvents = getNumberEventsPerDate();
      int temp = 0;

      PrintStream realSystemOut = System.out;

      OutputStream output = new FileOutputStream(filename);
      PrintStream printOut = new PrintStream(output);
      System.setOut(printOut);

      switch (attribute) {

      case "DailyTimes":
        for (DateTime date: numberEvents.keySet()) {
          temp = numberEvents.get(date);
          System.out.println(temp);
        }
        break;

      case "Duration":
        for (int i = 0; i < events.size(); i++) {
          temp = (int) (events.get(i).getDuration().getStandardMinutes());
          System.out.println(temp);
        }
        break;

      case "StartTime":
        for (int i = 0; i < events.size(); i++) {
          temp = (int) (events.get(i).getStartMinuteOfDay());
          System.out.println(temp);
        }
        break;

      case "StartTimeBinned":
        for (int i = 0; i < events.size(); i++) {
          temp =
            (int) (events.get(i).getStartMinuteOfDay() / Constants.MINUTES_PER_BIN);
          System.out.println(temp);
        }
        break;

      default:
        System.out.println("ERROR");

      }

      System.setOut(realSystemOut);

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void DurationHistogramToFile (String file)
  {
    try {

      DecimalFormat df = new DecimalFormat("#.#####");
      Map<Integer, Double> temp = getDurationHistogram();

      PrintStream realSystemOut = System.out;

      OutputStream output = new FileOutputStream(file);
      PrintStream printOut = new PrintStream(output);
      System.setOut(printOut);
      System.out.println("Histogram");
      System.out.println("0-0");

      for (Integer duration: temp.keySet()) {

        System.out.println(duration + "-" + df.format(temp.get(duration)));

      }

      System.setOut(realSystemOut);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void DailyTimesHistogramToFile (String file)
  {
    try {

      DecimalFormat df = new DecimalFormat("#.#####");
      Map<Integer, Double> temp = getDailyTimesHistogram();

      PrintStream realSystemOut = System.out;

      OutputStream output = new FileOutputStream(file);
      PrintStream printOut = new PrintStream(output);
      System.setOut(printOut);

      System.out.println("Histogram");

      for (Integer duration: temp.keySet()) {

        System.out.println(duration + "-" + df.format(temp.get(duration)));

      }

      System.setOut(realSystemOut);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void StartTimeHistogramToFile (String file)
  {
    try {

      DecimalFormat df = new DecimalFormat("#.#####");
      Map<Integer, Double> temp = getStartTimeHistogram();

      PrintStream realSystemOut = System.out;

      OutputStream output = new FileOutputStream(file);
      PrintStream printOut = new PrintStream(output);
      System.setOut(printOut);

      System.out.println("Histogram");

      for (int i = 0; i < Constants.MINUTES_PER_DAY; i++) {

        if (temp.containsKey(i))
          System.out.println(i + "-" + df.format(temp.get(i)));
        else
          System.out.println(i + "-0");
      }

      System.setOut(realSystemOut);

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void StartTimeBinnedHistogramToFile (String file)
  {
    try {

      DecimalFormat df = new DecimalFormat("#.#####");
      Map<Integer, Double> temp = getStartTimeBinnedHistogram();

      PrintStream realSystemOut = System.out;

      OutputStream output = new FileOutputStream(file);
      PrintStream printOut = new PrintStream(output);
      System.setOut(printOut);

      System.out.println("Histogram");

      for (Integer duration: temp.keySet()) {

        System.out.println(duration + "-" + df.format(temp.get(duration)));

      }

      System.setOut(realSystemOut);

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void readEventsFile (String filename) throws FileNotFoundException
  {

    int startMinute = 0;
    int endMinute = 0;
    int counter = 0;
    DateTime startDateTime = new DateTime();
    DateTime endDateTime = new DateTime();
    DateTime startDate = new DateTime();
    DateTime endDate = new DateTime();
    DateTime date = new DateTime(2010, 1, 1, 0, 0);

    File file = new File(filename);

    Scanner scanner = new Scanner(file);

    String line = scanner.nextLine();
    String[] temp = new String[2];

    while (scanner.hasNext()) {

      line = scanner.nextLine();
      temp = line.split("-");

      startMinute = Integer.parseInt(temp[0]);
      endMinute = Integer.parseInt(temp[1]);

      startDateTime = date.plusMinutes(startMinute);
      endDateTime = date.plusMinutes(endMinute);

      startDate =
        new DateTime(startDateTime.getYear(), startDateTime.getMonthOfYear(),
                     startDateTime.getDayOfMonth(), 0, 0);
      endDate =
        new DateTime(endDateTime.getYear(), endDateTime.getMonthOfYear(),
                     endDateTime.getDayOfMonth(), 0, 0);

      events.add(new ConsumptionEvent(counter++, startDateTime, startDate,
                                      endDateTime, endDate));

      // events.get(counter - 1).status();

    }

    scanner.close();

    analyze();

  }

}
