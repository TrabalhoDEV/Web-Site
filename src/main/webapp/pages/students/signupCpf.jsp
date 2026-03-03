<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Verify your CPF</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/pages/students/verifyCpf" method="post">
    <label for="cpf">CPF:</label>
    <input type="text" id="cpf" name="cpf" value="${param.cpf}" required>

    <button type="submit">Verify</button>

    <% if (request.getAttribute("error") != null) { %>
    <span style="color: red;"><%= request.getAttribute("error") %></span>
    <% } %>
</form>
</body>
</html>
