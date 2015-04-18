<%-- 
    Document   : computerList
    Created on : Mar 15, 2015, 10:05:27 PM
    Author     : danh
--%>

<%@tag description="put the tag description here" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="productss" type="java.lang.Object" required="true"%>

<%-- any content can be specified here e.g.: --%>
<c:choose>
    <c:when test="${message[0] == '1'}">
        <div class="success_message">${message[1]}</div>
    </c:when>
    <c:otherwise>
        <div class="error_message">${message[1]}</div>
    </c:otherwise>
</c:choose>
        
<table>
    <thead>
    <th>Rank</th>
    <th>Name</th>
</thead>
<tbody>
    <c:set var="idx" value="1" scope="page"/>
    <c:forEach var="tupple" items="${productss}">
        <tr>
            <td>${idx}</td>
            <td>${tupple.productName}</td>
        </tr>
        <c:set var="idx" value="${idx+1}" scope="page"/>
    </c:forEach>
</tbody>
</table>