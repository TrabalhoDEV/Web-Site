<%@ page import="com.example.schoolservlet.models.SchoolClass" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Deletar professor</title>
</head>

<body>
<h1>Deletar professor</h1>
<p>Esse professor possui pendências, informe um professor para substituí-lo</p>
<form action="${pageContext.request.contextPath}/admin/school-class/delete" method="post">
  <input type="hidden" name="id" value="<%= request.getAttribute("id")%>">

  <label for="username">Usuário:</label>
  <input name="username" id="username" type="text" placeholder="Digite o usuário aqui">

  <%if (request.getAttribute("error") != null){%>
  <p><%=request.getAttribute("error")%></p>
  <%}%>

  <button type="submit">Enviar</button>
</form>
</body>
</html>
