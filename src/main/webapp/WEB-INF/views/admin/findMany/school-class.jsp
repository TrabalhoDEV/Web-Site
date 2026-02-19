<%@ page import="com.example.schoolservlet.models.SchoolClass" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<%
    Map<Integer, SchoolClass> schoolClassMap = (Map<Integer, SchoolClass>) request.getAttribute("schoolClassMap");
    int pageNumber = (Integer) request.getAttribute("page");
%>
<body>
<%if (!schoolClassMap.isEmpty()) {%>
<table>
    <thead>
    <tr>
        <th>Classe</th>
    </tr>
    </thead>
    <tbody>
    <%for (SchoolClass schoolClass: schoolClassMap.values()) {%>
    <tr>
        <td><%=schoolClass.getSchoolYear()%></td>
    </tr>
    <%}%>
    </tbody>
</table>
<%} else if (request.getAttribute("error") != null) {%>
<p><%=request.getAttribute("error")%></p>
<%} else {%>
<p>Nenhuma turma foi encontrada</p>
<%}%>
</body>
</html>
