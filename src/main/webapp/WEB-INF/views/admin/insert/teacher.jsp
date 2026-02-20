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
<form action="admin/teacher/insert" method="post">
  <label for="name">Digite o nome do professor:</label>
  <input name="name" id="name" type="text">

  <label for="email">Digite o e-mail do professor:</label>
  <input name="email" id="email" type="email">

  <label for="anoEscolar">Ano Escolar:</label>
  <select id="anoEscolar" name="anoEscolar" required>
    <option value="" selected disabled>-- Selecione um ano --</option>
    <%for (Subject subject: subjects) {%>
    <option value="<%=subject.getId()%>"><%=OutputFormatService.formatName(subject.getName())%></option>
    <%}%>
  </select>
</form>
</body>
</html>
