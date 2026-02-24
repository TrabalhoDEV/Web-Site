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
%>
<body>
<%if (!studentMap.isEmpty()){%>
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
    <td>
      <form action="${pageContext.request.contextPath}/admin/student/update" method="get">
        <input type="hidden" name="enrollment" value="<%= student.getId() %>">
        <input type="submit" value="Atualizar estudante">
      </form>
    </td>
  </tr>
  <%}%>
  </tbody>
</table>
<%} else if (request.getAttribute("error") != null){%>
<p><%=request.getAttribute("error")%></p>
<%} else {%>
<p>Nenhum aluno foi encontrado</p>
<%}%>
<a href="${pageContext.request.contextPath}/admin/add-student">Cadastrar alunos</a>
</body>
</html>
