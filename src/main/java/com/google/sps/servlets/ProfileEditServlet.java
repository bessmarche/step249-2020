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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/profile_edit")
public class ProfileEditServlet extends HttpServlet {
 /**
  * doPost checks if the user is currently logged in and returns the correct header
  */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String username = request.getParameter("username");
      String bio = request.getParameter("bio");
      
      HttpSession session = request.getSession();
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

      Entity userEntity = new Entity("User");
      userEntity.setProperty("email", session.getAttribute("userEmail"));
      userEntity.setProperty("name", username);
      userEntity.setProperty("bio", bio);
      userEntity.setProperty("imageURL", "images/default.png");
      datastore.put(userEntity);
      
      session.setAttribute("name", username);
      session.setAttribute("isLoggedIn", 1);

      response.sendRedirect("/user?id="+userEntity.getKey().getId()); 
  }
}
  