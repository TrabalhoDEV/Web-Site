<%@ page import="java.util.List" %>
<%@ page import="com.example.schoolservlet.models.Subject" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page import="com.example.schoolservlet.models.SchoolClass" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Adicionar professor</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/admin/teacher/insert" method="post">
  <label for="name">Digite o nome do professor:</label>
  <input name="name" id="name" type="text">

  <label for="email">Digite o e-mail do professor:</label>
  <input name="email" id="email" type="email">

  <label for="username">Digite o username do professor:</label>
  <input name="username" id="username" type="text">

  <label for="password">Digite a senha do professor:</label>
  <input name="password" id="password" type="password">

  <fieldset>
    <legend>Selecione as matérias:</legend>
    <%
      List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
      if (subjects != null) {
        for (Subject subject : subjects) {
    %>
    <input type="checkbox" name="subjectIds" value="<%= subject.getId() %>">
    <%= OutputFormatService.formatName(subject.getName()) %><br>
    <%
        }
      }
    %>
  </fieldset><br>

  <fieldset>
    <legend>Selecione as turmas:</legend>
    <%
      List<SchoolClass> classes = (List<SchoolClass>) request.getAttribute("classes");
      if (classes != null) {
        for (SchoolClass sc : classes) {
    %>
    <input type="checkbox" name="schoolClassIds" value="<%= sc.getId() %>">
    <%= OutputFormatService.formatName(sc.getSchoolYear()) %><br>
    <%
        }
      }
    %>
  </fieldset><br>

  <% if (request.getAttribute("error") != null) { %>
  <p style="color:red;"><%= request.getAttribute("error") %></p>
  <% } %>

  <button type="submit">Cadastrar professor</button>
</form>
</body>
</html>
