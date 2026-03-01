<%@ page import="com.example.schoolservlet.models.SchoolClass" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Listar turmas</title>
</head>
<%
    Map<Integer, SchoolClass> schoolClassMap = (Map<Integer, SchoolClass>) request.getAttribute("schoolClassMap");
    int pageNumber = (Integer) request.getAttribute("page");
    int totalPages = (Integer) request.getAttribute("totalPages");
%>
<body>
<table>
    <thead>
    <tr>
        <th>Classe</th>
        <th>Ações</th>
    </tr>
    </thead>
    <%if (schoolClassMap != null && !schoolClassMap.isEmpty()) {%>
    <tbody>
    <%for (SchoolClass schoolClass: schoolClassMap.values()) {%>
    <tr>
        <td><%=schoolClass.getSchoolYear().toUpperCase()%></td>
        <td><a href="${pageContext.request.contextPath}/admin/school-class/update?id=<%=schoolClass.getId()%>">Editar</a></td>
        <td><a href="${pageContext.request.contextPath}/admin/school-class/delete?id=<%=schoolClass.getId()%>">Deletar</a></td>
    </tr>
    <%}
    }else {%>
    <tr>
    <td colspan="2">Nenhuma turma foi encontrada</td>
    </tr>
    <%}%>
    </tbody>
</table>
<div style="display: flex; gap: 10px; align-items: center">
<%if (pageNumber != 1){%>
    <a href="${pageContext.request.contextPath}/admin/school-class/find-many?page=<%=pageNumber - 1%>">⬅️</a>
<%}%>
<p><%=pageNumber%></p>
<%if (pageNumber < totalPages){%>
<a href="${pageContext.request.contextPath}/admin/school-class/find-many?page=<%=pageNumber + 1%>">➡️</a>
<%}%>
</div>

<% if (request.getAttribute("error") != null) {%>
<p><%=request.getAttribute("error")%></p>
<%}%>
<a href="${pageContext.request.contextPath}/admin/school-class/insert">Adicionar turma</a>
</body>
</html>
