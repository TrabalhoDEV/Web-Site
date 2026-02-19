<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.models.Student" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %><%--
  Created by IntelliJ IDEA.
  User: caiomaciel-ieg
  Date: 18/02/2026
  Time: 21:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<%
  Map<Integer, Student> studentMap = (Map<Integer, Student>) request.getAttribute("studentMap");
  int pageNumber = (Integer) request.getAttribute("page");
%>
<body>
<table>
  <thead>
  <tr>
    <th>Matrícula</th>
    <th>Nome</th>
    <th>CPF</th>
    <th>Email</th>
    <th>Status</th>
  </tr>
  </thead>
  <tbody>
  <%for (Student student: studentMap.values()) {%>
  <tr>
    <td><%=student.getEnrollment()%></td>
    <td><%=OutputFormatService.formatName(student.getName())%>
    </td>
    <td><%=student.getCpf()%>
    </td>
    <td><%=student.getEmail() != null ? student.getEmail() : "Não informado"%>
    </td>
    <td><%=student.getStatus()%>
    </td>
  </tr>
  <%}%>
  </tbody>
</table>
<a href="admin/add-student">Cadastrar alunos</a>
</body>
</html>
