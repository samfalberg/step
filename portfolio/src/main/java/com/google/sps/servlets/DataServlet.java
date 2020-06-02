// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import com.google.gson.Gson;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    doPost(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<String> comments = new ArrayList<String>();

    //Get input from comment form
    String comment = getParameter(request, "text-input", "");
    comments.add(comment);

    String json = convertToJson(comments);
    
    //Respond with message
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  /**
   * Converts messages ArrayList to JSON string using Gson
   */
  private String convertToJson(ArrayList<String> messages) {
      Gson gson = new Gson();
      String json = gson.toJson(messages);
      return json;
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
   private String getParameter(HttpServletRequest request, String name, String defaultValue) {
       String value = request.getParameter(name);
       if (value == null) {
           return defaultValue;
       }
       return value;
   }
}
