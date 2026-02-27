<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.models.StudentSubject" %>
<%@ page import="com.example.schoolservlet.utils.Constants" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Student Home</title>
</head>
<body>

<h1>Seja bem vindo:)</h1>

<aside>
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/student/bulletin">Minhas matérias</a></li>
        </ul>
    </nav>
</aside>
<h2>Feedbacks de seus professores: </h2>

<%
    String error = (String) request.getAttribute("error");
    if (error != null) {
%>
<script>alert(<%= error %>)</script>
<%
    }
%>

<table border="1" cellpadding="8" cellspacing="0">
    <thead>
    <tr>
        <th>Feedbacks</th>
    </tr>
    </thead>
    <tbody>
    <%
        boolean showNextPage = false;
        List<String> observationsList = (List<String>) request.getAttribute("observationsList");


        if (observationsList != null && !observationsList.isEmpty()) {
            for (String obs : observationsList) {
    %>
    <tr>
        <td><%=obs%></td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="5">No feedbacks found.</td>
    </tr>
    <%
        }
    %>
    </tbody>
</table>

<br>

<%
    Integer currentPage = 0;
    Integer totalPages = 0;
    try{
        currentPage = request.getAttribute("currentPage") != null? (Integer) request.getAttribute("currentPage"):1;
        totalPages =  request.getAttribute("totalPages") != null? (Integer) request.getAttribute("totalPages"):1;
    } catch (ClassCastException e){
        ;
    }

%>

<a href="${pageContext.request.contextPath}/student/home?nextPage=<%= currentPage - 1 %>"
        <%= currentPage <= 0 ? "style='pointer-events:none; color: gray;'" : "" %>>
    Anterior
</a>

<a href="${pageContext.request.contextPath}/student/home?nextPage=<%= currentPage + 1 %>"
        <%= (int) totalPages <= currentPage ? "style='pointer-events:none; color: gray;'" : "" %>>
    Próxima
</a>

</body>
</html>
