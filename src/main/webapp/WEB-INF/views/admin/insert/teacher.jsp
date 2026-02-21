<%@ page import="java.util.List" %>
<%@ page import="com.example.schoolservlet.models.Subject" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Adicionar professor</title>
</head>
<%
  List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
%>
<body>
<form action="${pageContext.request.contextPath}/admin/teacher/insert" method="post">  <label for="name">Digite o nome do professor:</label>
  <input name="name" id="name" type="text">

  <label for="email">Digite o e-mail do professor:</label>
  <input name="email" id="email" type="email">

  <label for="username">Digite o username do professor:</label>
  <input name="username" id="username" type="text">

  <label for="password">Digite a senha do professor:</label>
  <input name="password" id="password" type="text">

  <%if (request.getAttribute("error") != null) {%>
  <p><%=request.getAttribute("error") %></p>
  <%}%>

  <button type="submit">Cadastrar professor</button>
</form>
</body>
</html>
