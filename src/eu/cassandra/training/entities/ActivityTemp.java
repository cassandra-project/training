package eu.cassandra.training.entities;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.jfree.chart.ChartPanel;

import eu.cassandra.training.behaviour.BehaviourModel;

public class ActivityTemp
{
  private String name;
  private ArrayList<Integer[]> events = new ArrayList<Integer[]>();
  private String eventsFile = "";
  private ArrayList<Appliance> appliances = new ArrayList<Appliance>();

  public ActivityTemp (String name)
  {
    this.name = name;
  }

  public String getName ()
  {
    return name;
  }

  public String getEventFile ()
  {
    return eventsFile;
  }

  public ArrayList<Appliance> getAppliances ()
  {
    return appliances;
  }

  public void setAppliances (ArrayList<Appliance> appliances)
  {
    this.appliances = appliances;
  }

  public ArrayList<Integer[]> getEvents ()
  {
    return events;
  }

  public void addEvent (int start, int end)
  {
    Integer[] temp = new Integer[2];
    temp[0] = start;
    temp[1] = end;
    events.add(temp);
  }

  public void createEventFile () throws IOException
  {
    PrintStream realSystemOut = System.out;
    eventsFile = "Files/" + name + " events.csv";
    OutputStream output = new FileOutputStream(eventsFile);
    PrintStream printOut = new PrintStream(output);
    System.setOut(printOut);

    System.out.println("Start Time, End Time");

    for (Integer[] temp: events) {
      System.out.println(temp[0] + "-" + temp[1]);
    }

    System.setOut(realSystemOut);
    output.close();

  }

  public void status ()
  {
    System.out.println("Activity:" + name);
    System.out.println("Events File:" + eventsFile);
    System.out.println("Number of Events:" + events.size());
    System.out.println("Appliances:" + appliances.toString());
  }

  public String toString ()
  {
    return name;
  }

  public ChartPanel consumptionGraph ()
  {
    return appliances.get(0).consumptionGraph();
  }

  public BehaviourModel toBehaviourModel (String person)
    throws FileNotFoundException
  {
    String[] appliances = new String[this.appliances.size()];

    for (int i = 0; i < appliances.length; i++)
      appliances[i] = this.appliances.get(i).getName();

    BehaviourModel result =
      new BehaviourModel(name, person, appliances, eventsFile);

    return result;
  }
}
