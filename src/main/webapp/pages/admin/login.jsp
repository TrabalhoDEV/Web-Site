<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h1>Login de administrador</h1>
<br/>
<form action="${pageContext.request.contextPath}/admin/auth" method="post">
    <label for="cpf">Cpf:</label>
    <input name="cpf" id="cpf" type="text" placeholder="Digite seu cpf aqui">
    <label for="password">Senha:</label>
    <input name="password" id="password" type="password" placeholder="Digite sua senha aqui">
    <%if (request.getAttribute("error") != null){
        if (request.getAttribute("error").equals("Acesso negado. Faça login como administrador.")){
            %> <script>
                alert("Acesso negado. Faça login como administrador.");
            </script>
        <%
        } else {
    %>
        <p><%=request.getAttribute("error")%></p>
    <%}}%>
    <button type="submit">Login</button>
</form>
<a href="${pageContext.request.contextPath}/auth/forgot-password/send-code">Esqueci minha senha</a>
</body>
</html>