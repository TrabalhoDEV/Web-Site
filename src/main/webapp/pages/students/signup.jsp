<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Cadastro</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/css/img/Logo - Vértice.svg" type="image/x-icon">
</head>
<body>
<main class="form-container">
    <h2>Cadastro</h2>
    <p>Digite os seguintes dados:</p>
    <p>Sua matrícula é ${student.getEnrollment()}</p>

    <form action="${pageContext.request.contextPath}/student/register" method="post">
        <input hidden type="text" id="cpf" name="cpf" value="${student.cpf}">

        <div class="input-group">
        <label for="name">Name:</label>
        <input type="text" id="name" name="name" required>
        </div>

        <div class="input-group">
        <label for="email">Email:</label>
        <input type="text" id="email" name="email" value="${param.email}" required>
        </div>

        <div class="input-group">
        <label for="newPassword">Nova senha:</label>
        <input type="password" id="newPassword" name="newPassword" required>
        </div>

        <div class="input-group">
        <label for="password">Confirmar senha:</label>
        <input type="password" id="password" name="confirmPassword" required>
        </div>

        <button type="submit">Criar conta</button>

        <% if (request.getAttribute("error") != null) { %>
        <p style="color: #9b0404; text-align: start"><%= request.getAttribute("error") %></p>
        <% } %>
    </form>
</main>
</body>
</html>
