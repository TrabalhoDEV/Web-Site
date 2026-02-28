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
    <h2>Login</h2>
    <p>Bem-vindo de volta! Faça seu <a class="linkAdm" style="font-size: var(--text-sm); color: var(--muted-text-color);" href="${pageContext.request.contextPath}/admin/auth">login</a>.</p>

    <form action="${pageContext.request.contextPath}/auth" method="post">

        <!-- Matrícula -->
        <div class="input-group">
            <label for="login">Usuário ou Matrícula</label>
            <input
                    type="text"
                    id="login"
                    name="identifier"
                    required
                    placeholder="Digite seu usuário ou matrícula">
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
            <%if (request.getAttribute("error") != null) {%>
                <p id="error" style="color: #9b0404; text-align: start; margin: 0"><%= request.getAttribute("error")%></p>
            <%}%>
        </div>

        <button type="submit">Entrar</button>

    </form>

    <p>Esqueceu sua senha? <a href="${pageContext.request.contextPath}/auth/forgot-password/send-code">Clique aqui</a></p>
</main>

</body>
</html>