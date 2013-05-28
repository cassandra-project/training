package eu.cassandra.training.entities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class ActivityTemp
{
  private String name;
  private ArrayList<Integer[]> events = new ArrayList<Integer[]>();

  public ActivityTemp (String name)
  {
    this.name = name;
  }

  public String getName ()
  {
    return name;
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
    OutputStream output = new FileOutputStream("Files/" + name + " events.csv");
    PrintStream printOut = new PrintStream(output);
    System.setOut(printOut);

    System.out.println("Start Time, End Time");

    for (Integer[] temp: events) {
      System.out.println(temp[0] + "," + temp[1]);
    }

    System.setOut(realSystemOut);
    output.close();
  }

  public void status ()
  {
    System.out.println("Activity:" + name);
    System.out.println("Number of Events:" + events.size());
  }

}
