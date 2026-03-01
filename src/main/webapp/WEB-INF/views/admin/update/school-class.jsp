<%@ page import="com.example.schoolservlet.models.SchoolClass" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Editar turma</title>
</head>
<body>
<%
  SchoolClass schoolClass = (SchoolClass) request.getAttribute("schoolClass");
%>
<h3>Editar turma</h3>
<form action="${pageContext.request.contextPath}/admin/school-class/update" method="post">
  <%if (schoolClass!= null){%>
    <label for="schoolYear">Nome da turma:</label>
    <input type="text" name="schoolYear" id="schoolYear" placeholder="Digite o nome da turma aqui" value="<%=schoolClass.getSchoolYear().toUpperCase()%>">
    <input type="hidden" name="id" value="<%=schoolClass.getId()%>">
  <%}%>
  <%if (request.getAttribute("error") != null){%>
  <%=request.getAttribute("error")%>
  <%}%>
  <button type="submit">Enviar</button>
</form>
<a href="${pageContext.request.contextPath}/admin/school-class/find-many">Voltar</a>
</body>
</html>