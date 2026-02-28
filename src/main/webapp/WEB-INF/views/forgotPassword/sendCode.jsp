<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Recuperação de senha</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/styles/login.css">
</head>
<body>
<main class="form-container">
    <h2>Recuperação de senha</h2>
    <p>Esqueceu sua senha? Sem problemas, aqui você pode recuperar</p>

    <form action="${pageContext.request.contextPath}/auth/forgot-password/send-code" method="post">
        <div class="input-group">
            <label for="input">Identificador:</label>
            <input
                    name="input"
                    id="input"
                    type="text"
                    required
                    placeholder="Digite seu identificador aqui">

            <%if (request.getAttribute("error") != null){ %>
            <p id="error" style="color: #9b0404; text-align: start; margin: 0"><%= request.getAttribute("error")%></p>
            <%}%>
        </div>

        <button type="submit">Enviar</button>
    </form>

    <p>Se lembrou e quer voltar? <a href="${pageContext.request.contextPath}/index.jsp">Clique aqui</a></p>
</main>
</body>
</html>