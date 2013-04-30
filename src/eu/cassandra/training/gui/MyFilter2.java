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

/* ImageFilter.java is used by FileChooser.java. */
public class MyFilter2 extends FileFilter
{

  // Accept all directories and all arff , csv , xls files.
  public boolean accept (File f)
  {
    if (f.isDirectory()) {
      return true;
    }

    String extension = getExtension(f);
    if (extension != null) {
      if (extension.equals("csv") || extension.equals("xls")) {
        return true;
      }
      else {
        return false;
      }
    }

    return false;
  }

  // The description of this filter
  public String getDescription ()
  {
    return "*.xls,*.csv";
  }

  public static String getExtension (File f)
  {
    String ext = null;
    String s = f.getName();

    s = s.replace(".", ",");

    String[] par = s.split(",");

    if (par.length > 1)
      ext = par[1];
    else
      ext = "";
    return ext;
  }

}
