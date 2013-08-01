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

/**
 * This class is used for the definition of a reactive activeOnly consumption
 * triplet.
 * 
 * @author Kyriakos Chatzidimitriou
 * 
 */
public class TripletReactive
{

  /** The reactive activeOnly of the triplet in VAR. */
  public double q;

  /** The slope of the triplet. */
  public double s;

  /** The duration of the triplet. */
  public int d;

  /** A simple constructor of a triplet */
  public TripletReactive ()
  {
    q = s = 0;
    d = 0;
  }
}
