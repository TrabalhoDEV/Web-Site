<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h1>Login</h1>
<br/>
<a href="${pageContext.request.contextPath}/admin/auth">Admin</a>
<br>
<form action="${pageContext.request.contextPath}/auth" method="post">
    <label for="identifier">Matrícula/usuário:</label>
    <input name="identifier" id="identifier" type="text" placeholder="Digite sua matrícula ou usuário aqui">
    <br>
    <label for="password">Senha:</label>
    <input name="password" id="password" type="password" placeholder="Digite sua senha aqui">
    <%if (request.getAttribute("error") != null){%>
    <p><%=request.getAttribute("error")%></p>
    <%}%>
    <br>
    <button type="submit">Login</button>
</form>
<a href="${pageContext.request.contextPath}/auth/forgot-password/send-code">Esqueci minha senha</a>
</body>
</html>