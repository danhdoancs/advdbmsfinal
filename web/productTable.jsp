<%-- 
    Document   : computerTable
    Created on : Mar 14, 2015, 3:08:58 PM
    Author     : danh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib tagdir="/WEB-INF/tags/" prefix="t" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<li>
    <label>Result:</label>
    <t:productList productss="${products}"/>
</li>
