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

import java.io.FileNotFoundException;
import java.util.ArrayList;

import eu.cassandra.training.behaviour.BehaviourModel;
import eu.cassandra.training.response.ResponseModel;

public class Person
{
  private String installation;
  private String name;
  private ArrayList<BehaviourModel> behaviourModels;
  private ArrayList<ResponseModel> responseModels;

  public Person ()
  {
    name = "";
    installation = "";
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

  public String getInstallation ()
  {
    return installation;
  }

  public ArrayList<BehaviourModel> getBehaviourModels ()
  {

    return behaviourModels;

  }

  public ArrayList<ResponseModel> getResponseModels ()
  {

    return responseModels;

  }

  public BehaviourModel findBehaviour (String name)
  {

    BehaviourModel result = null;

    for (BehaviourModel behaviour: behaviourModels) {

      if (behaviour.getName().equalsIgnoreCase(name)) {
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
    throws FileNotFoundException
  {
    BehaviourModel behaviourModel = new BehaviourModel(appliance);
    behaviourModel.train(distributions);
    behaviourModels.add(behaviourModel);
  }
}
