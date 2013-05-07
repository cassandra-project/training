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

public class Constants
{

  public static final int MINUTES_PER_DAY = 1440;
  public static final int NUMBER_OF_BINS = 144;
  public static final int MINUTES_PER_BIN = 10;
  public static final int SHIFTING_WINDOW_IN_MINUTES = 60;

  public static final String DAYS_FILE = "Files/daysArray";
  public static final String EVENTS_FILE = "Files/eventsAll";
  public static final String START_TIME_FILE = "Files/startTime";
  public static final String DURATION_FILE = "Files/duration";
  public static final String DAILY_TIMES_FILE = "Files/dailyTimes";
  public static final String START_TIME_BINNED_FILE = "Files/startTimeBinned";

}
