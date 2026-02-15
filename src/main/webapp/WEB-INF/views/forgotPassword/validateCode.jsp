<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Código</title>
</head>
<body>
  <form action="${pageContext.request.contextPath}/auth/forgot-password/validate-code" method="post">
    <label for="code">Digite o código:</label>
    <input name="code" id="code" type="text" placeholder="Digite o código aqui">
    <%if (request.getAttribute("error") != null){%>
    <p><%=request.getAttribute("error")%></p>
    <%}%>
    <button type="submit">Enviar</button>
  </form>
  <a href="${pageContext.request.contextPath}/auth/forgot-password/send-code">Enviar e-mail novamente</a>
</body>
</html>
