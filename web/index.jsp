<%-- 
    Document   : home
    Created on : Mar 14, 2015, 2:28:07 PM
    Author     : danh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<t:layout layoutTitle="Home" layoutHeader="Home">
    <jsp:body>  
        <form class="query7Form" action="" method="GET" accept-charset="utf-8">
            <ul>
                <li>
                    <label></label>
                    <input class="os" type="text" name="os" required="true">
                </li>
                <li>
                    <label></label>
                    <input type="submit" class="query7Btn" value="Query"> 
                </li>
                </li>
                <div  class="customerTable" hidden="true">
                </div>

            </ul>
        </form>
    </jsp:body>
</t:layout>