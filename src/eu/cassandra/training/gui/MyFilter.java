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
package eu.cassandra.training.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * This class is used as a filter for the acceptable file types of the browsing
 * capabilities of the Training Module.In this case it accepts only .json and
 * .txt files since it is a filter for the consumption models imported from the
 * user.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */
public class MyFilter extends FileFilter
{

  /**
   * This function is the acceptance filter for the file types selected.
   * 
   * @param file
   *          the selected file from the user under investigation for
   *          acceptance.
   * @return the acceptance or not of the file.
   */
  public boolean accept (File file)
  {
    if (file.isDirectory()) {
      return true;
    }

    String extension = getExtension(file);
    if (extension != null) {
      if (extension.equals("txt") || extension.equals("json")) {
        return true;
      }
      else {
        return false;
      }
    }

    return false;
  }

  /**
   * This function what will be written as the filter's description on the
   * browsing window.
   * 
   * @return a string with the description.
   */
  public String getDescription ()
  {
    return "*.txt,*.json";
  }

  /**
   * This function is used in order to extract the extension of the file
   * selected from the user.
   * 
   * @param file
   *          the selected file from the user.
   * @return the extension of the file.
   */
  public static String getExtension (File file)
  {
    String ext = null;
    String s = file.getName();

    s = s.replace(".", ",");

    String[] par = s.split(",");

    if (par.length > 1)
      ext = par[1];
    else
      ext = "";

    return ext;
  }

}
