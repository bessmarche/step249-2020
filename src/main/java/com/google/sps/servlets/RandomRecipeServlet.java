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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.search.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns a random recipe*/
@WebServlet("/random")
public class RandomRecipeServlet extends HttpServlet {
  // Name of the index used.
  private static final String INDEX_NAME = "recipes_index";

  /**
   * This function creates and redirects to an url from a random recipe's id.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String url = "/recipe?id=" + returnRandomId(datastore);

    request.getRequestDispatcher(url).forward(request, response);
  }

  /**
   * This function returns a random recipe's id.
   */
  private long returnRandomId(DatastoreService datastore) {
    int position = getRandomPosition(datastore);
    Results<ScoredDocument> recipes = getRecipeDocumentFromPosition(
      datastore,
      position
    );
    Iterator iter = recipes.iterator();
    ScoredDocument document = (ScoredDocument) iter.next();
    return Long.parseLong(document.getId());
  }

  /**
   * This function generates and returns an integer in the range [0, number of recipes).
   */
  private int getRandomPosition(DatastoreService datastore) {
    int recipesNumber = countRecipes();
    Random randomGenerator = new Random();
    int randomPosition = randomGenerator.nextInt(recipesNumber);

    return randomPosition;
  }

  private Results<ScoredDocument> getRecipeDocumentFromPosition(
    DatastoreService datastore,
    int position
  ) {
    QueryOptions options = QueryOptions
      .newBuilder()
      .setLimit(1)
      .setOffset(position)
      .build();
    Query query = Query.newBuilder().setOptions(options).build("");
    Index index = getIndex(INDEX_NAME);

    return index.search(query);
  }

  /**
   * Returns the index that stores the recipes documents
   */
  private Index getIndex(String indexName) {
    IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
    Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
    return index;
  }

  /**
   * Count the number of Recipe entities inside the datastore.
   */
  public static int countRecipes() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    com.google.appengine.api.datastore.Query query = new com.google.appengine.api.datastore.Query(
      "Recipe"
    )
    .addSort("title", SortDirection.ASCENDING);
    PreparedQuery preparedQuery = datastore.prepare(query);
    int lengthOfQuery = preparedQuery.countEntities();

    return lengthOfQuery;
  }
}
