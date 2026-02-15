<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Nova senha</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/auth/forgot-password/new-password" method="post">
    <label for="newPassword">Nova senha:</label>
    <input name="newPassword" id="newPassword" type="password" placeholder="Digite sua nova senha aqui">
    <label for="confirmPassword">Nova senha:</label>
    <input name="confirmPassword" id="confirmPassword" type="password" placeholder="Confirme sua nova senha aqui">
    <%if (request.getAttribute("error") != null){%>
    <p><%=request.getAttribute("error")%></p>
    <%}%>
    <button type="submit">Enviar</button>
</form>
</body>
</html>
