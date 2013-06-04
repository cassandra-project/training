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

import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartPanel;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.cassandra.training.behaviour.BehaviourModel;
import eu.cassandra.training.response.ResponseModel;
import eu.cassandra.training.utils.ChartUtils;

public class Person
{
  private String installation = "";
  private String name = "";
  private String type = "";
  private String personID = "";
  private ArrayList<BehaviourModel> behaviourModels;
  private ArrayList<ResponseModel> responseModels;

  public Person ()
  {
    behaviourModels = new ArrayList<BehaviourModel>();
    responseModels = new ArrayList<ResponseModel>();
  }

  public Person (String name, String installation)
  {
    this.name = name;
    this.installation = installation;
    behaviourModels = new ArrayList<BehaviourModel>();
    responseModels = new ArrayList<ResponseModel>();
  }

  public String getName ()
  {
    return name;
  }

  public String getType ()
  {
    return type;
  }

  public String getInstallation ()
  {
    return installation;
  }

  public String getPersonID ()
  {
    return personID;
  }

  public ArrayList<BehaviourModel> getBehaviourModels ()
  {

    return behaviourModels;

  }

  public ArrayList<ResponseModel> getResponseModels ()
  {

    return responseModels;

  }

  public void setPersonID (String id)
  {
    personID = id;
  }

  public void addBehaviour (BehaviourModel behaviour)
  {

    behaviourModels.add(behaviour);

  }

  public BehaviourModel findBehaviour (Appliance appliance)
  {

    BehaviourModel result = null;
    String temp = this.name + " " + appliance.getName() + " Behaviour Model";
    // System.out.println("Name:" + name);
    for (BehaviourModel behaviour: behaviourModels) {
      // System.out.println(behaviour.getName());
      if (behaviour.getName().equalsIgnoreCase(temp)) {
        result = behaviour;
        break;
      }
    }
    return result;
  }

  public BehaviourModel findBehaviour (String name)
  {

    BehaviourModel result = null;
    String temp = this.name + " " + name + " Behaviour Model";
    for (BehaviourModel behaviour: behaviourModels) {
      if (behaviour.getName().equalsIgnoreCase(temp)) {
        result = behaviour;
        break;
      }
    }
    return result;
  }

  public ResponseModel findResponse (String name)
  {

    ResponseModel result = null;

    for (ResponseModel response: responseModels) {

      if (response.getName().equalsIgnoreCase(name)) {
        result = response;
        break;
      }
    }
    return result;
  }

  public void train (Appliance appliance, String[] distributions)
    throws IOException
  {
    BehaviourModel exists =
      findBehaviour(name + " " + appliance.getName() + " Behaviour Model");

    if (exists != null)
      behaviourModels.remove(exists);

    BehaviourModel behaviourModel = new BehaviourModel(appliance, name);
    behaviourModel.train(distributions);
    behaviourModels.add(behaviourModel);
  }

  public void train (ActivityTemp activity, String[] distributions)
    throws IOException
  {
    BehaviourModel exists =
      findBehaviour(activity.getName() + " Behaviour Model");

    if (exists != null)
      behaviourModels.remove(exists);

    BehaviourModel behaviourModel = activity.toBehaviourModel(name);
    behaviourModel.train(distributions);
    behaviourModels.add(behaviourModel);
  }

  public ChartPanel previewResponse (BehaviourModel behaviour, int response,
                                     double[] basicScheme, double[] newScheme)
  {
    return ResponseModel.previewResponseModel(behaviour, response, basicScheme,
                                              newScheme);
  }

  public String createResponse (BehaviourModel behaviour, int responseType,
                                double[] basicScheme, double[] newScheme)
    throws IOException
  {
    String responseTemp = "";

    switch (responseType) {

    case 0:
      responseTemp = "Best";
      break;
    case 1:
      responseTemp = "Normal";
      break;
    case 2:
      responseTemp = "Worst";
    }

    String temp =
      name + " " + behaviour.getAppliancesOf()[0] + " Response Model ("
              + responseTemp + ")";

    ResponseModel exists = findResponse(temp);

    if (exists != null)
      responseModels.remove(exists);

    String result = "";

    ResponseModel response = new ResponseModel(behaviour, name, responseType);

    response.respond(responseType, basicScheme, newScheme);

    responseModels.add(response);

    result = response.toString();

    return result;
  }

  public String toString ()
  {
    return name;
  }

  public DBObject toJSON (String installationID)
  {

    DBObject temp = new BasicDBObject();

    temp.put("name", name);
    temp.put("type", type);
    temp.put("description", name + " " + type);
    temp.put("inst_id", installationID);

    return temp;

  }

  public void status ()
  {
    System.out.println("Name: " + name);
    System.out.println("Person Of Installation: " + installation);
    System.out.println("Behaviour Models:" + behaviourModels.toString());
    System.out.println("Response Models:" + responseModels.toString());
  }

  public ChartPanel statisticGraphs ()
  {
    String title = name + " Statistics";

    return ChartUtils.createPieChart(title, this);
  }
}
