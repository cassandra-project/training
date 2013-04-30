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

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ConsumptionEvent
{
  private int id = 0;
  private DateTime startDateTime, endDateTime;
  private DateTime startDate, endDate;
  private Duration duration;
  private boolean weekday;
  private int startMinuteOfDay;
  private int endMinuteOfDay;

  public ConsumptionEvent ()
  {
    startDateTime = new DateTime();
    endDateTime = new DateTime();
    startDate = new DateTime();
    endDate = new DateTime();
    duration = new Duration(0);
  }

  public ConsumptionEvent (int id)
  {
    this();
    this.id = id;
  }

  public ConsumptionEvent (int id, DateTime startDT, DateTime startD,
                           DateTime endDT, DateTime endD)
  {
    this.id = id;
    startDateTime = startDT;
    endDateTime = endDT;
    startDate = startD;
    endDate = endD;

    calculateParameters();

  }

  public void setStartDateTime (DateTime startDateTime)
  {
    this.startDateTime = startDateTime;
  }

  public void setEndDateTime (DateTime endDateTime)
  {
    this.endDateTime = endDateTime;
  }

  public void setStartDate (DateTime startDate)
  {
    this.startDate = startDate;
  }

  public void setEndDate (DateTime endDate)
  {
    this.endDate = endDate;
  }

  public void setMinuteOfDay (int minute)
  {
    startMinuteOfDay = minute;
  }

  public void setWeekday (boolean weekday)
  {
    this.weekday = weekday;
  }

  public DateTime getStartDateTime ()
  {
    return startDateTime;
  }

  public DateTime getEndDateTime ()
  {
    return endDateTime;
  }

  public DateTime getStartDate ()
  {
    return startDate;
  }

  public DateTime getEndDate ()
  {
    return endDate;
  }

  public Duration getDuration ()
  {
    return duration;
  }

  public int getId ()
  {
    return id;
  }

  public int getStartMinuteOfDay ()
  {
    return startMinuteOfDay;
  }

  public int getEndMinuteOfDay ()
  {
    return endMinuteOfDay;
  }

  public boolean getWeekday ()
  {
    return weekday;
  }

  public void calculateParameters ()
  {
    duration = new Interval(startDateTime, endDateTime).toDuration();
    startMinuteOfDay = startDateTime.getMinuteOfDay();
    endMinuteOfDay = endDateTime.getMinuteOfDay();
    if (startDate.getDayOfWeek() > 6)
      weekday = false;
    else
      weekday = true;
  }

  public void status ()
  {

    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    DateTimeFormatter fmt2 = DateTimeFormat.forPattern("yyyy-MM-dd");

    System.out.println("Id:" + id);
    System.out.println("Start DateTime:" + startDateTime.toString(fmt));
    System.out.println("Start Date:" + startDate.toString(fmt2));
    System.out.println("End DateTime:" + endDateTime.toString(fmt));
    System.out.println("End Date:" + endDate.toString(fmt2));
    System.out.println("Duration:" + duration.toPeriod());
    System.out.println("Weekday:" + weekday);
    System.out.println("Minute Of The Day:" + startMinuteOfDay);

  }

  public String toString ()
  {

    return ("Event " + Integer.toString(id));

  }

}
