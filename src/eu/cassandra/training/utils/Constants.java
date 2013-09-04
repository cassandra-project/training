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

/**
 * This class contains static constants that are used throughout the Training
 * Module GUI.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class Constants
{

  public static final int DAILY_TIMES = 0;
  public static final int DURATION = 1;
  public static final int START_TIME = 2;
  public static final int START_TIME_BINNED = 3;

  public static final int MINUTES_PER_DAY = 1440;
  public static final int HOURS_PER_DAY = 24;
  public static final int QUARTERS_PER_DAY = 96;
  public static final int TEN_MINUTES_PER_DAY = 144;
  public static final int FIVE_MINUTES_PER_DAY = 288;
  public static final int MINUTES_PER_HOUR = 60;
  public static final int QUARTER = 15;
  public static final int TEN_MINUTES = 10;
  public static final int FIVE_MINUTES = 5;
  public static final int ONE_MINUTE = 1;

  public static final int SHIFTING_WINDOW_IN_MINUTES = 60;

  public static final String DAYS_FILE = "Files/daysArray";
  public static final String EVENTS_FILE = "Files/eventsAll";
  public static final String START_TIME_FILE = "Files/startTime";
  public static final String DURATION_FILE = "Files/duration";
  public static final String DAILY_TIMES_FILE = "Files/dailyTimes";
  public static final String START_TIME_BINNED_FILE = "Files/startTimeBinned";

  public static final double SMALL_NUMBER = 1.0E7;

  public static final int HOUR_SAMPLE_LIMIT = 2 * HOURS_PER_DAY;
  public static final int QUARTER_SAMPLE_LIMIT = 2 * QUARTERS_PER_DAY;
  public static final int TEN_MINUTE_SAMPLE_LIMIT = 2 * TEN_MINUTES_PER_DAY;
  public static final int FIVE_MINUTE_SAMPLE_LIMIT = 2 * FIVE_MINUTES_PER_DAY;

  public static final int LOW_SAMPLE_MIXTURE = 1;
  public static final int MEDIUM_SAMPLE_MIXTURE = 3;
  public static final int HIGH_SAMPLE_MIXTURE = 5;
  public static final int VERY_HIGH_SAMPLE_MIXTURE = 10;
}
