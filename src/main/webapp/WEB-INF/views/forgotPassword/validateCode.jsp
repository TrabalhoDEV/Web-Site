<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Validação do código</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
  <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/css/img/Logo - Vértice.svg" type="image/x-icon">
</head>
<body>
<main class="form-container">
  <h2>Verificação do código</h2>
  <p>Digite o código enviado para o seu e-mail</p>

  <form action="${pageContext.request.contextPath}/auth/forgot-password/validate-code" method="post">
    <div class="input-group">
      <label for="code">Código:</label>
      <input name="code" id="code" type="text" placeholder="000000" pattern="\d{6}">

      <%if (request.getAttribute("error") != null){ %>
      <p id="error" style="color: #9b0404; text-align: start; margin: 0"><%= request.getAttribute("error")%></p>
      <%}%>
    </div>

    <button type="submit">Enviar</button>
  </form>

  <p>Não recebeu, deseja reenviar? <a href="${pageContext.request.contextPath}/auth/forgot-password/send-code">Clique aqui</a></p>
</main>
</body>
</html>
