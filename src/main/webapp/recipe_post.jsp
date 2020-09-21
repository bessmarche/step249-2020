<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
   String uploadUrl = blobstoreService.createUploadUrl("/recipe_post"); %>

<c:set var = "upload"  value = "<%= uploadUrl %>" />

<t:genericpage>
    <jsp:body>
       <div class="content">
        <c:choose>
        <c:when test="${edit}">
            <c:set var="welcome_message" value="Edit your recipe!"/>
        </c:when>
        <c:otherwise>
            <c:set var="welcome_message" value="Post your own recipe!"/>
            <c:set var="title_placeholder" value="Type your recipe title"/>
            <c:set var="descriprion_placeholder" value="Add a short description for your recipe"/>
            <c:set var="img" value="required"/>
        </c:otherwise>
        </c:choose>
        <c:choose>
        <c:when test="${isLoggedIn == 1}">
            <h2>${welcome_message}</h2>
            <form action="${upload}" enctype="multipart/form-data" method="POST">
            <c:if test="${edit}">
                <input type="hidden" name="edited" value="true"/>
                <input type="hidden" name="recipeId" value="${recipeId}"/>
            </c:if>
             <div class="form-group">
                <label for="image">Upload Main Recipe Image</label>
                <input type="file" class="form-control-file" name="image" ${img}>
             </div>
             <div class="form-group">
                <label for="title">Recipe Title</label>
                <input type="text" class="form-control" name="title" placeholder="${title_placeholder}" value="${title}" maxlength="50" required>
             </div>
             <div class="form-group">
                <label for="description">Overview</label>
                <textarea class="form-control" name="description" rows="3" placeholder="${description_placeholder}" maxlength="500" required>${description}</textarea>
             </div>
             <fieldset class="form-group">
                <label>Select the difficulty</label>
                         <div class="form-check">
                             <label class="radio-inline" for="easy">
                             <input class="form-check-input" type="radio" name="difficulty" id="easy" value="easy" ${easyChecked} required>
                             Easy</label>
                         </div>
                         <div class="form-check">
                             <label class="radio-inline" for="medium">
                             <input class="form-check-input" type="radio" name="difficulty" id="medium" value="medium" ${mediumChecked} required>
                             Medium</label>
                         </div>
                         <div class="form-check">
                             <label class="radio-inline" for="hard">
                             <input class="form-check-input" type="radio" name="difficulty" id="hard" value="hard" ${hardChecked} required>
                             Hard</label>
                         </div>
             </fieldset>
             <div class="form-group">
                 <label>Select the prearation time for your recipe</label>
                 <div class="form-row">
                      <label class="form-inline" for="time">
                       <input type="number" class="form-control" name="time" min="15" value="${time}"  placeholder="input prep time in minutes" required>
                      </label>
                 </div>
             </div>
             <div class="form-group">
                <label>Ingredients</label>
                <div class="ingredients-wrapper">
                <c:choose>
                  <c:when test="${edit}">
                      <c:forEach items="${ingredients}" var="ingredient">
                        <div class ="ingredient-form">
                           <input type="text" class="form-control" name="ingredients[]" maxlength="50" value="${ingredient}" required>
                           <a href="javascript:void(0);" class="ingredient-remove-button">REMOVE</a>
                        </div>
                      </c:forEach>
                  </c:when>
                  <c:otherwise>
                       <div class="ingredient-form">
                                <input type="text" class="form-control" name="ingredients[]" placeholder="qunatity, unti of measure and ingredient" maxlength="50" required>
                                <a href="javascript:void(0);" class="ingredient-remove-button">REMOVE</a>
                        </div>
                  </c:otherwise>
                  </c:choose>
                </div>
                <a href="javascript:void(0);" class="ingredient-add-button" title="add-step">ADD INGREDIENT</a>
                </div>
             <div class="form-group">
                <label for="step">Steps</label>
                <div class="steps-wrapper">
                  <c:choose>
                  <c:when test="${edit}">
                     <c:forEach items="${steps}" var="step">
                       <div class ="step-form">
                            <textarea class="form-control" name="step[]" rows="3" maxlength="500" required>${step}</textarea>
                            <a href="javascript:void(0);" class="step-remove-button">REMOVE</a>
                        </div>
                      </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <div class="step-form">
                        <textarea class="form-control" name="step[]" rows="3" maxlength="500" required></textarea>
                        <a href="javascript:void(0);" class="step-remove-button">REMOVE</a>
                    </div>
                  </c:otherwise>
                  </c:choose>
                </div>
                <a href="javascript:void(0);" class="step-add-button" title="add-step">ADD STEP</a>
             </div>
             <button type="submit" class="btn btn-secondary my-3">submit</button>
            </form>
            <div class="step-clone" style="display:none">
                   <textarea class="form-control" name="step[]" rows="3" maxlength="500" ></textarea>
                   <a href="javascript:void(0);" class="step-remove-button">REMOVE</a>
            </div>
            <div class="ingredient-clone" style="display:none">
                      <input type="text" class="form-control" name="ingredients[]" placeholder="ingredient" maxlength="50" >
                      <a href="javascript:void(0);" class="ingredient-remove-button">REMOVE</a></div>
            </div>
        </c:when>
        <c:otherwise>
            <h2>You have to be logged in to acess this page!</h2>
        </c:otherwise>
      </c:choose>
       </div>
    </jsp:body>
</t:genericpage>