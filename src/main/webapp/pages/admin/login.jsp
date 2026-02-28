<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/styles/login.css">
</head>
<body>

<main class="form-container">
    <h2>Acesso Administrativo</h2>

    <form action="${pageContext.request.contextPath}/admin/auth" method="post">

        <!-- Matrícula -->
        <div class="input-group">
            <label for="cpf">CPF</label>
            <input
                    id="cpf"
                    type="text"
                    name="cpf"
                    required
                    pattern="\d{3}\.?\d{3}\.?\d{3}-?\d{2}"
                    title="Por favor digite no formato: 000.000.000-00"
                    placeholder="000.000.000-00">
        </div>

        <!-- Senha -->
        <div class="input-group">
            <label for="senha">Senha</label>
            <input
                    type="password"
                    id="senha"
                    name="password"
                    required
                    placeholder="Digite a Senha">

            <%if (request.getAttribute("error") != null){ %>
            <p id="error" style="color: #9b0404; text-align: start; margin: 0"><%= request.getAttribute("error")%></p>
            <%}%>
        </div>

        <button type="submit">Entrar</button>

    </form>

    <p style="font-size: 16px">Esqueceu sua senha? <a href="${pageContext.request.contextPath}/auth/forgot-password/send-code">Clique aqui</a></p>
    <p>Não é administrador? <a href="${pageContext.request.contextPath}/index.jsp">Volte por aqui</a></p>
</main>

</body>
</html>