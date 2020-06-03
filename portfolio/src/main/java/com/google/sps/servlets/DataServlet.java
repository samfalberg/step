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
import java.util.Iterator;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private int maxComments = 10;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Task").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    ArrayList<Comment> comments = new ArrayList<Comment>();
    Iterator<Entity> iteration = results.asIterator();
    for (int count = 0; count < maxComments; count++) {
        //Check if it can iterate any more to avoid NoSuchElementException
        if (!iteration.hasNext())
            break;
        Entity entity = iteration.next();
        
        long id = entity.getKey().getId();
        String message = (String) entity.getProperty("message");
        long timestamp = (long) entity.getProperty("timestamp");

        Comment comment = new Comment(id, message, timestamp);
        comments.add(comment);
    }
    
    String json = new Gson().toJson(comments);
    
    //Respond with message
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //Get input from comment form
    String message = getParameter(request, "text-input", "");
    long timestamp = System.currentTimeMillis();

    //Get input from num-comment form
    String numComments = getParameter(request, "num-comments", "10");
    
    // Convert the input to an int.
    int numCommentsToInt;
    try {
      numCommentsToInt = Integer.parseInt(numComments);
    } catch (NumberFormatException e) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter one of the nonnegative integers from the dropdown list");
      return;
    }
    
    //Check that input is nonnegative
    if (numCommentsToInt < 0) {
        numCommentsToInt = maxComments;
    }

    //Change maxComments to user input
    maxComments = numCommentsToInt;

    Entity taskEntity = new Entity("Task");
    taskEntity.setProperty("message", message);
    taskEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    response.sendRedirect("/about.html");
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
