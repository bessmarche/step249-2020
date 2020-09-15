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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class AuthServlet extends HttpServlet {

  /**
   * doGet checks if the user is currently logged in, and if the user has already a profile. It then returns the correct header
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    UserService userService = UserServiceFactory.getUserService();
    HttpSession session = request.getSession();

    if (userService.isUserLoggedIn()) {
      handleLogIn(response, session, userService);
    } else {
      session.invalidate();
      response.sendRedirect("/");
    }
  }

  public void handleLogIn(
    HttpServletResponse response,
    HttpSession session,
    UserService userService
  )
    throws IOException {
    String urlToRedirectToAfterUserLogsOut = "/login";

    Entity currentUser = queryForUser(userService);
    if (Objects.isNull(currentUser)) {
      setRegistrationAttributes(
        userService,
        urlToRedirectToAfterUserLogsOut,
        session
      );
      response.sendRedirect("/profile_creation.jsp");
    } else {
      setUserAttributes(
        session,
        urlToRedirectToAfterUserLogsOut,
        userService,
        currentUser
      );
      response.sendRedirect("/user?id=" + currentUser.getKey().getId());
    }
  }

  public Entity queryForUser(UserService userService) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity currentUser = null;
    String userEmail = userService.getCurrentUser().getEmail();
    Filter propertyFilter = new FilterPredicate(
      "email",
      FilterOperator.EQUAL,
      userEmail
    );
    Query q = new Query("User").setFilter(propertyFilter);
    PreparedQuery pq = datastore.prepare(q);
    List<Entity> userEntityList = pq.asList(FetchOptions.Builder.withLimit(1));
    if (userEntityList.size() >= 1) {
      currentUser = userEntityList.get(0);
    }
    return currentUser;
  }

  public void setUserAttributes(
    HttpSession session,
    String url,
    UserService userService,
    Entity userEntity
  )
    throws IOException {
    String userEmail = userService.getCurrentUser().getEmail();
    String logoutUrl = userService.createLogoutURL(url);

    session.setAttribute("isLoggedIn", 1);
    session.setAttribute("userEmail", userEmail);
    session.setAttribute("logoutURL", logoutUrl);
    session.setAttribute("name", userEntity.getProperty("name"));
    session.setAttribute("bio", userEntity.getProperty("bio"));
    session.setAttribute("id", userEntity.getKey().getId());
  }

  public void setRegistrationAttributes(
    UserService userService,
    String url,
    HttpSession session
  )
    throws IOException {
    String userEmail = userService.getCurrentUser().getEmail();
    String logoutUrl = userService.createLogoutURL(url);

    session.setAttribute("uregisteredUserEmail", userEmail);
    session.setAttribute("logoutURL", logoutUrl);
  }
}
