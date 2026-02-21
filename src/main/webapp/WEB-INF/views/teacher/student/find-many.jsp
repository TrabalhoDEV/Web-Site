<%@ page import="com.example.schoolservlet.models.Teacher" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.models.Student" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<%
    Map<Integer, Student> studentMap = (Map<Integer, Student>) request.getAttribute("studentMap");
    int pageNumber = (Integer) request.getAttribute("page");
    int totalPages = (Integer) request.getAttribute("totalPages");
%>
<body>
<%if (!studentMap.isEmpty()) {%>
<table>
    <thead>
    <tr>
        <th>Matrícula</th>
        <th>Nome</th>
        <th>CPF</th>
        <th>Email</th>
        <th>Status</th>
        <th>Ações</th>
    </tr>
    </thead>
    <tbody>
    <%for (Student student : studentMap.values()) {%>
    <tr>
        <td><%=student.getEnrollment()%>
        </td>
        <td><%=OutputFormatService.formatName(student.getName())%>
        </td>
        <td><%=student.getCpf()%>
        </td>
        <td><%=student.getEmail() != null ? student.getEmail() : "Não informado"%>
        </td>
        <td><%=student.getStatus()%>
        </td>
        <td></td>
    </tr>
    <%}%>
    </tbody>
</table>
<%} else {%>
<p>Você nã possui nenhum aluno</p>
<%}%>
<%if (request.getAttribute("error") != null) {%>
<p><%= request.getAttribute("error")%>
</p>
<%}%>
<div style="display: flex; gap: 10px">
    <%if (pageNumber != 1) {%>
    <a href="${pageContext.request.contextPath}/teacher/students?page=<%=pageNumber - 1%>">⬅️</a>
    <%}%>
    <p><%=pageNumber%>
    </p>
    <%if (pageNumber != totalPages) {%>
    <a href="${pageContext.request.contextPath}/teacher/students?page=<%=pageNumber + 1%>">➡️</a>
    <%}%>
</div>
</body>
</html>
