<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Página não encontrada | Vértice</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/error404.css" />
    <link rel="shortcut icon" href="<%= request.getContextPath() %>/assets/img/Logo%20-%20Vértice.svg" type="image/x-icon">
  </head>
</head>
<body>
    <div class="action-container">
        <h1 class="title">Oops!</h1>
        <h2 class="sub-title">Page not found</h2>
        <p class="description">
            A página que você está procurando não existe ou foi movida. Por favor, verifique o URL ou aperte no botão abaixo.
        </p>
        <a href="${pageContext.request.contextPath}/index.jsp" class="return">
            <button>Voltar para a página de login</button>
        </a>
    </div>
    <div class="img-container">
        <img src="${pageContext.request.contextPath}/assets/img/error-img.svg" alt="Error Image" class="error-image">
    </div>
</body>
</html>