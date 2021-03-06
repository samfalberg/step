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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.google.gson.Gson;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private int maxComments = 5;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //Get input from load more button
    int loadMore = Integer.parseInt(getParameter(request, "load-more").orElse("0"));
    
    //Load 5 extra comments if pressed
    if (loadMore != 0) {
        maxComments += loadMore;
    }   

    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    ArrayList<Comment> comments = new ArrayList<Comment>();
    Iterator<Entity> iteration = results.asIterator();
    for (int count = 0; count < maxComments; count++) {
        //Check if it can iterate any more to avoid NoSuchElementException
        if (!iteration.hasNext()) {
            break;
        }
        Entity entity = iteration.next();
        
        long id = entity.getKey().getId();
        long timestamp = (long) entity.getProperty("timestamp");
        String name = (String) entity.getProperty("name");
        String email = (String) entity.getProperty("email");
        String message = (String) entity.getProperty("message");
        String mood = (String) entity.getProperty("mood");
        String blobKey = (String) entity.getProperty("blobKey");
        boolean myComment = isMyComment(id);

        Comment comment = new Comment.Builder()
                                    .setId(id)
                                    .setTimestamp(timestamp)
                                    .setName(name)
                                    .setEmail(email)
                                    .setMessage(message)
                                    .setMood(mood)
                                    .setBlobKey(blobKey)
                                    .setMyComment(myComment)
                                    .build();
        comments.add(comment);
    }
    
    String json = new Gson().toJson(comments);
    
    //Respond with message
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
     //Get input from num-comment form
    String numComments = getParameter(request, "num-comments").orElse(null);
    
    if (numComments != null) {
        // Convert the input to an int.
        int numCommentsToInt;
        try {
          numCommentsToInt = Integer.parseInt(numComments);
        } catch (NumberFormatException e) {
          response.setContentType("text/html");
          response.getWriter().println("Please enter one of the nonnegative integers from the dropdown list");
          return;
        }

        //Update maxComments if nonnegative
        if (numCommentsToInt > 0) {
            maxComments = numCommentsToInt;
        }
    }

    //Get input from comment form
    long timestamp = System.currentTimeMillis();
    String name = getParameter(request, "username").orElse("Anonymous User");
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    String userId = userService.getCurrentUser().getUserId();
    String message = getParameter(request, "text-input").orElse(null);
    String mood = getParameter(request, "cat-mood").orElse(null);
    String blobKey = getBlobKey(request, "image");
    
    if (message != null) {
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("timestamp", timestamp);
        commentEntity.setProperty("name", name);
        commentEntity.setProperty("email", email);
        commentEntity.setProperty("userId", userId);
        commentEntity.setProperty("message", message);
        commentEntity.setProperty("mood", mood);
        commentEntity.setProperty("blobKey", blobKey);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);
    }

    response.sendRedirect("/about.html");
  }

  /**
   * @return the Optional of the request parameter
   */
   private Optional<String> getParameter(HttpServletRequest request, String name) {
       String value = request.getParameter(name);
       //return empty Optional if string is null or empty
       if (value == null || value.isEmpty()) {
           return Optional.empty();
       }
       return Optional.ofNullable(value);
   }

   /** Returns the blob key for the user-uploaded file */
   private String getBlobKey(HttpServletRequest request, String name) {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
        List<BlobKey> blobKeys = blobs.get(name);

        // User submitted form without selecting a file, so we can't get a URL. (dev server)
        if (blobKeys == null || blobKeys.isEmpty()) {
            return null;
        } 

        // Form only contains a single file input, so get the first index.
        String blobKey = blobKeys.get(0).getKeyString();

        // User submitted form without selecting a file, so we can't get a URL. (live server)
        BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKeys.get(0));
        if (blobInfo.getSize() == 0) {
            blobstoreService.delete(blobKeys.get(0));
            return null;
        }

        return blobKey;        
   }

  /**
   * Says if a comment belongs to the currently signed-in user
   * @return boolean
   */
   private boolean isMyComment(long id) {
       UserService userService = UserServiceFactory.getUserService();

       if (!userService.isUserLoggedIn()) {
           return false;
       }
       
       String userId = userService.getCurrentUser().getUserId();
       Key commentEntityKey = KeyFactory.createKey("Comment", id);
       DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
       
       try {
            Entity commentEntity = datastore.get(commentEntityKey);

            String commentId = (String) commentEntity.getProperty("userId");

            //If the user id matches the comment's id, then it is the user's comment
            return commentId.equals(userId);

       } catch (EntityNotFoundException e) {
            System.out.println("Entity not found");
       }

       return false;
   }
}
