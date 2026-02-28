<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Nova Senha</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/styles/login.css">
</head>
<body>
<main class="form-container">
    <h2>Nova senha</h2>
    <p>Digite e confirme sua nova senha</p>

    <form action="${pageContext.request.contextPath}/auth/forgot-password/new-password" method="post">
        <div class="input-group">
            <label for="newPassword">Nova senha</label>
            <input name="newPassword" id="newPassword" type="text" placeholder="Digite a nova senha">
        </div>

        <div class="input-group">
            <label for="confirmPassword">Nova senha</label>
            <input name="confirmPassword" id="confirmPassword" type="password" placeholder="Confirme a nova senha">
            <%if (request.getAttribute("error") != null){%>
            <p id="error" style="color: #9b0404; text-align: start; margin: 0"><%= request.getAttribute("error")%></p>
            <%}%>
        </div>

        <button type="submit">Enviar</button>
    </form>

    <a style="text-align: center;" href="${pageContext.request.contextPath}">Cancelar operação</a>
</main>
</body>
</html>