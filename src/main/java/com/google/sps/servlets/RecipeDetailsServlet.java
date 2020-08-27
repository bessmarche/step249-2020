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
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/recipe")
public class RecipeDetailsServlet extends HttpServlet {
  /*
  * doGet receives the request and returns the message sent as parameter 
  */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    /**
      Assume the request has a parameter containing the id of the recipe
     **/

// create a new recipe object

    Recipe recipe = new Recipe();
    recipe.setId(Long.parseLong(entity.getId()));
    recipe.setName(name);
    recipe.setImage(entity.getOnlyField("imgURL").getText());
    recipe.setIngredients(ingredients);
    recipe.setSteps(steps);

// send all the parameters to the request

    request.setAttribute("title", recipe.getName());
    request.setAttribute("imgURL", recipe.getId());
    request.setAttribute("ingredient", recipe.getIngredients());
    request.setAttribute("steps", recipe.getSteps());

    request.getRequestDispatcher("/recipe.jsp").forward(request, response);
  }
}
