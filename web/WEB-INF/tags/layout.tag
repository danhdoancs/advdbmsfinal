<%-- 
    Document   : layout
    Created on : Mar 14, 2015, 1:54:39 PM
    Author     : danh
--%>

<%@tag description="Page Layout" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags/"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="layoutTitle" required="true" %>
<%@attribute name="layoutHeader" required="true" %>
<%@attribute name="layoutFlagNoUserPanel"%>

<%-- any content can be specified here e.g.: --%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${layoutTitle}</title>
        <link href="./static/css/layout.css" rel="stylesheet" type="text/css"/>
        <link href="./static/css/query.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <header>

            <a class="noUnderline" href="./"><h1>Advance DBMS Homework 2B</h1></a>

            <h3>

                <div class="headerLeft">                
                    <div>${layoutHeader}</div>                                  
                </div>
                <div class="headerRight">
                </div>
            </h3>

        </header>

        <main>
            <c:choose>
                <c:when test="${message[0] == '1'}">
                    <div class="success_message">${message[1]}</div>
                </c:when>
                <c:otherwise>
                    <div class="error_message">${message[1]}</div>
                </c:otherwise>
            </c:choose>

            <jsp:doBody/>
        </main>

        <footer>
            <p>Copyright 2015, <a href="http://daviddoan.net" class="noUnderline">David Doan</a></p>
        </footer>
    </body>


    <script src="./static/js/jquery-1.11.2.min.js" type="text/javascript"></script>
    <script src="./static/js/query.js" type="text/javascript"></script>
</html>