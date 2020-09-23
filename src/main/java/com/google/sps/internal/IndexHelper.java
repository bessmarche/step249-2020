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
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.search.*;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class IndexHelper {
  // Name of the index used.
  private static final String INDEX_NAME = "recipes_index";

  /**
   * Returns the index that stores the recipes documents
   */
  public static Index getIndex() {
    IndexSpec indexSpec = IndexSpec.newBuilder().setName(INDEX_NAME).build();
    Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
    return index;
  }
  
  /**
  * Adds a Recipe to the index.
  */
  public static void addRecipe(Entity recipeEntity, ArrayList<String> ingredients){
    Index index = getIndex();
    Document recipeDocument = buildRecipeDocumentForIndexing(recipeEntity, ingredients);
    index.put(recipeDocument);
  }

  /**
  * Deletes a Recipe from the index.
  */
  public static void deleteRecipe(Entity recipeEntity){
    Index index = getIndex();
    index.delete(String.valueOf(recipeEntity.getKey().getId()));
  }

  /**
  * Search and returns a list of Recipe Documents that matches the criterias from query.
  */
  public static Results<ScoredDocument> searchRecipes(Query query) {
    Index index = getIndex();   
    return index.search(query);
  }

  /**
  * Builds a Recipe Document to be added to the index.
  */
  private static Document buildRecipeDocumentForIndexing(
    Entity recipeEntity,
    ArrayList<String> ingredients
  ) {
    Document recipeDocument = Document
      .newBuilder()
      .setId(String.valueOf(recipeEntity.getKey().getId()))
      .addField(
        Field
          .newBuilder()
          .setName("title")
          .setText((String) recipeEntity.getProperty("index_title"))
      )
      .addField(
        Field
          .newBuilder()
          .setName("ingredients")
          .setText(String.join(" ", ingredients))
      )
      .addField(
        Field
          .newBuilder()
          .setName("prep_time")
          .setNumber((Integer) recipeEntity.getProperty("prep_time"))
      )
      .addField(
        Field
          .newBuilder()
          .setName("difficulty")
          .setText((String) recipeEntity.getProperty("difficulty"))
      )
      .build();

    return recipeDocument;
  }
}