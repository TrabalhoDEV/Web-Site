<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Editar turma</title>
</head>
<body>
<h3>Editar turma</h3>
<form action="${pageContext.request.contextPath}/admin/school-class/update" method="put">
  <label for="name">Nome da turma:</label>
  <input type="text" name="name" id="name" placeholder="Digite o nome da turma aqui">
  <%if (request.getAttribute("error") != null){%>
  <%=request.getAttribute("error")%>
  <%}%>
  <button type="submit">Enviar</button>
</form>
<a href="${pageContext.request.contextPath}/admin/school-class/find-many">Voltar</a>
</body>
</html>