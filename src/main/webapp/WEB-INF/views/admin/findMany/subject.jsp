<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.models.Subject" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page import="com.example.schoolservlet.utils.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Adicionar matéria</title>
</head>
<%
    Map<Integer, Subject> subjectMap = (Map<Integer, Subject>) request.getAttribute("subjectMap");
    int totalPages = (Integer) request.getAttribute("totalPages");
    int pageNumber = (Integer) request.getAttribute("page");
%>
<body>
<%if (!subjectMap.isEmpty()){%>
<table>
    <thead>
    <tr>
        <th>Nome</th>
        <th>Data limite para notas</th>
        <th>Ações</th>
    </tr>
    </thead>
    <tbody>
    <%for (Subject subject: subjectMap.values()) {%>
    <tr>
        <td><%=OutputFormatService.formatName(subject.getName())%>
        </td>
        <td><%=subject.getDeadline()%></td>
        <td><a href="${pageContext.request.contextPath}/admin/subject/update?id=<%=subject.getId()%>">Editar</a></td>
    </tr>
    <%}%>
    </tbody>
</table>
<%} else if (request.getAttribute("error") != null){%>
<p><%=request.getAttribute("error")%></p>
<%} else {%>
<p>Nenhuma matéria foi encontrada</p>
<%}%>
<a href="${pageContext.request.contextPath}/admin/subject/insert">Adicionar matéria</a>
<div style="display: flex; gap: 10px">
    <%if (pageNumber != 1){%>
    <a href="${pageContext.request.contextPath}/admin/subject/find-many?page=<%= pageNumber - 1%>">⬅️</a>
    <%}%>
    <p><%=pageNumber%></p>
    <%if (pageNumber < totalPages){%>
    <a href="${pageContext.request.contextPath}/admin/subject/find-many?page=<%= pageNumber + 1%>">➡️</a>
    <%}%>
</div>
</body>
</html>
