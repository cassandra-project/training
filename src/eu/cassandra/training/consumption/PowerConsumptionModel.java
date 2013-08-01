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

import java.util.ArrayList;
import java.util.Arrays;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * This class stores the variables of an active activeOnly consumption model. It has
 * no functionality except parsing a JSON string and return the values on
 * demand.
 * 
 * @author kyrcha
 * 
 */
public class PowerConsumptionModel
{

  /** How many times the patterns repeat */
  private int outerN;

  /** The number of patterns */
  private int patternN;

  /** Total duration of the consumption model */
  private int totalDuration;

  /** How many times each pattern runs */
  private int[] n;

  /** Total duration per pattern */
  private int[] patternDuration;

  /** An array storing the consumption patterns */
  private ArrayList<TripletPower>[] patterns;

  /** The string representation of the active activeOnly consumption model */
  private String model;

  /**
   * This is the simple constructor of the active activeOnly consumption model.
   */
  public PowerConsumptionModel ()
  {
  }

  /**
   * This is the constructor of the active activeOnly consumption model.
   * 
   * @param amodel
   *          The string of the JSON schema that fully defines the active activeOnly
   *          consumption model to implement.
   */
  public PowerConsumptionModel (String amodel)
  {
    model = amodel;
    DBObject modelObj = (DBObject) JSON.parse(model);
    init(modelObj);
  }

  /**
   * This is the main function that create the triplets of the pattern
   * 
   * @param modelObjactive
   *          The active activeOnly consumption model JSON schema as DBObject.
   * 
   */
  public void init (DBObject modelObj)
  {

    outerN = ((Integer) modelObj.get("n")).intValue();
    BasicDBList patternsObj = (BasicDBList) modelObj.get("params");
    patternN = patternsObj.size();
    patterns = new ArrayList[patternN];
    n = new int[patternN];
    patternDuration = new int[patternN];
    for (int i = 0; i < patternN; i++) {
      n[i] = ((Integer) ((DBObject) patternsObj.get(i)).get("n")).intValue();
      BasicDBList values =
        ((BasicDBList) ((DBObject) patternsObj.get(i)).get("values"));
      int tripplets = values.size();
      patterns[i] = new ArrayList<TripletPower>(tripplets);
      for (int j = 0; j < tripplets; j++) {
        TripletPower t = new TripletPower();
        try {
          t.p = ((Double) ((DBObject) values.get(j)).get("p")).doubleValue();
        }
        catch (ClassCastException e) {
          t.p =
            (double) ((Integer) ((DBObject) values.get(j)).get("p")).intValue();
        }
        t.d = ((Integer) ((DBObject) values.get(j)).get("d")).intValue();
        patternDuration[i] += t.d;
        totalDuration += (n[i] * t.d);
        try {
          t.s = ((Double) ((DBObject) values.get(j)).get("s")).doubleValue();
        }
        catch (ClassCastException e) {
          t.s =
            (double) ((Integer) ((DBObject) values.get(j)).get("s")).intValue();
        }
        patterns[i].add(t);
      }
    }

  }

  /**
   * This is a getter function for the number of times the outer pattern is
   * repeated.
   * 
   * @return the outer pattern number of times.
   */
  public int getOuterN ()
  {
    return outerN;
  }

  /**
   * This is a getter function for the number of patterns of the active activeOnly
   * consumption model.
   * 
   * @return the number of patterns.
   */
  public int getPatternN ()
  {
    return patternN;
  }

  /**
   * This is a getter function for the number of times a certain pattern is
   * repeated.
   * 
   * @param i
   *          The pattern's index in the consumption model.
   * 
   * @return the ith pattern number of times.
   */
  public int getN (int i)
  {
    return n[i];
  }

  /**
   * This is a getter function for a certain pattern of the active activeOnly
   * consumption model.
   * 
   * @param i
   *          The index of the pattern in demand.
   * @return the ith pattern
   */
  public ArrayList<TripletPower> getPattern (int i)
  {
    return patterns[i];
  }

  /**
   * This function presents the main attributes of the active activeOnly consumption
   * model.
   */
  public void status ()
  {

    System.out.println("Outer N:" + outerN);
    System.out.println("Total Duration:" + totalDuration);
    System.out.println("Number of Patterns:" + patternN);
    System.out.println("Pattern Times: " + Arrays.toString(n));
    System.out
            .println("Pattern Durations: " + Arrays.toString(patternDuration));

  }

}
