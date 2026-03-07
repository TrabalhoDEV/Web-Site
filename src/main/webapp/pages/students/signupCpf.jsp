<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Validar cpf</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
    <link rel="shortcut icon" href="<%= request.getContextPath() %>/assets/img/Logo%20-%20Vértice.svg" type="image/x-icon">
</head>
<body>
<main class="form-container">
    <h2>Seja bem vindo</h2>
    <p>Para se cadastrar, informe seu cpf</p>

    <form action="${pageContext.request.contextPath}/student/validate/cpf" method="post">

        <div class="input-group">
            <label for="cpf">CPF:</label>
            <input type="text" id="cpf" name="cpf" placeholder="Digite seu cpf" required>
        </div>

    <% if (request.getAttribute("error") != null) { %>
    <p style="color: #9b0404; text-align: start"><%= request.getAttribute("error") %></p>
    <% } %>
    <button type="submit">Enviar</button>
    <p>Já tem conta? <a href="${pageContext.request.contextPath}/index.jsp">Clique aqui</a></p>
</form>
</main>
</body>
</html>
