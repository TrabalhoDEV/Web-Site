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
            <li>
                <form>
                    <button type="submit" formaction="<%= request.getContextPath() %>/student/bulletin">Minhas matérias</button>
                </form>
            </li>
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
        Object attr = request.getAttribute("observationsList");
        List<String> observationsList = attr instanceof List<?>
                ? (List<String>) attr
                : Collections.emptyList();

        if (!observationsList.isEmpty()) {
            if (observationsList.size() >= Constants.DEFAULT_TAKE) showNextPage = true;
            System.out.println(observationsList.size());
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
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    if (currentPage == null) currentPage = 0;
%>

<form method="get" action="<%= request.getContextPath() %>/student/home">
    <input type="hidden" name="nextPage" value="<%= currentPage- 1%>">
    <button type="submit" <%= currentPage <= 0 ? "disabled" : "" %>>Previous</button>
</form>

<form method="get" action="<%= request.getContextPath() %>/student/home">
    <input type="hidden" name="nextPage" value="<%= currentPage+ 1%>">
    <button type="submit" <%=!showNextPage ? "disabled": ""%>>Next</button>
</form>

</body>
</html>
