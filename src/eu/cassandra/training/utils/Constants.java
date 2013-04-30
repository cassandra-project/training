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

import java.text.SimpleDateFormat;

import org.joda.time.DateTime;

public class Constants
{

  public static final SimpleDateFormat sdf =
    new SimpleDateFormat("YYYYMddHHmm");

  public static final DateTime SUMMER_START_DATE = new DateTime(2010, 6, 1, 0,
                                                                0);
  public static final DateTime SUMMER_END_DATE =
    new DateTime(2010, 8, 31, 0, 0);
  public static final DateTime AUTUMN_START_DATE = new DateTime(2010, 9, 1, 0,
                                                                0);
  public static final DateTime AUTUMN_END_DATE = new DateTime(2010, 11, 31, 0,
                                                              0);
  public static final DateTime WINTER_START_DATE = new DateTime(2010, 12, 1, 0,
                                                                0);
  public static final DateTime WINTER_END_DATE =
    new DateTime(2010, 2, 28, 0, 0);
  public static final DateTime SPRING_START_DATE = new DateTime(2010, 3, 1, 0,
                                                                0);
  public static final DateTime SPRING_END_DATE =
    new DateTime(2011, 5, 31, 0, 0);

  public static final int HUNDRED = 100;
  public static final int THOUSAND = 1000;

  public static final int SECONDS_PER_MINUTE = 60;
  public static final int MINUTES_PER_HOUR = 60;
  public static final int MINUTES_PER_QUARTER_DAY = 360;
  public static final int MINUTES_PER_DAY = 1440;
  public static final int HOURS_PER_DAY = 24;
  public static final int DAYS_PER_WEEK = 7;
  public static final int DAYS_PER_MONTH = 30;

  public static final int DOUBLE_PHASE_START_1 = 6;
  public static final int DOUBLE_PHASE_START_2 = 15;

  public static final int TRIPLE_PHASE_START_1 = 6;
  public static final int TRIPLE_PHASE_START_2 = 12;
  public static final int TRIPLE_PHASE_START_3 = 18;

  public static final int NUMBER_OF_BINS = 144;
  public static final int MINUTES_PER_BIN = 10;

  public static final int NUMBER_OF_FOLDS = 4;
  public static final int APPLIANCE_NUMBER_OF_RUNS = 10;
  public static final int TIMES_OF_SHIFTING = 4;
  public static final int NUMBER_OF_TRIES = 10;

  public static final int NUMBER_OF_PERIODS = 4;
  public static final int NIGHT = 0;
  public static final int MORNING = 1;
  public static final int NOON = 2;
  public static final int EVENING = 3;
  public static final int NIGHT_PERIOD_START = NIGHT * MINUTES_PER_QUARTER_DAY;
  public static final int MORNING_PERIOD_START = MORNING
                                                 * MINUTES_PER_QUARTER_DAY;
  public static final int NOON_PERIOD_START = NOON * MINUTES_PER_QUARTER_DAY;
  public static final int EVENING_PERIOD_START = EVENING
                                                 * MINUTES_PER_QUARTER_DAY;

  public static final double BASE_LOAD = 0.01;

  public static final String DAYS_FILE = "Files/daysArray";
  public static final String EVENTS_FILE = "Files/eventsAll";
  public static final String START_TIME_FILE = "Files/startTime";
  public static final String DURATION_FILE = "Files/duration";
  public static final String DAILY_TIMES_FILE = "Files/dailyTimes";
  public static final String START_TIME_BINNED_FILE = "Files/startTimeBinned";

  public static final int MAXIMUM_NUMBER_OF_APPLIANCES = 5;

}
