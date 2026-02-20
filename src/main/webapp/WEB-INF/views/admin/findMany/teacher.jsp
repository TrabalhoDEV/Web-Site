<%@ page import="com.example.schoolservlet.models.Teacher" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Listar professores</title>
</head>
<%
  Map<Integer, Teacher> teacherMap = (Map<Integer, Teacher>) request.getAttribute("teacherMap");
  int pageNumber = (Integer) request.getAttribute("page");
%>
<body>
<%if (!teacherMap.isEmpty()){%>
<table>
  <thead>
  <tr>
    <th>Nome</th>
    <th>Usuário</th>
    <th>Email</th>
  </tr>
  </thead>
  <tbody>
  <%for (Teacher teacher: teacherMap.values()) {%>
  <tr>
    <td><%=OutputFormatService.formatName(teacher.getName())%></td>
    <td><%=teacher.getUsername()%>
    </td>
    <td><%=teacher.getEmail()%>
    </td>
  </tr>
  <%}%>
  </tbody>
</table>
<%} else if (request.getAttribute("error") != null){%>
<p><%=request.getAttribute("error")%></p>
<%} else {%>
<p>Nenhum professor foi encontrado</p>
<%}%>
<a href="admin/teacher/insert">Adicionar professor</a>
</body>
</html>
