<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/auth/forgot-password/send-code" method="post">
        <label for="input">Digite seu identificador:</label>
        <input name="input" id="input" type="text" placeholder="Digite aqui">
        <%if (request.getAttribute("error") != null){%>
            <p><%=request.getAttribute("error")%></p>
        <%}%>
        <button type="submit">Enviar</button>
    </form>
</body>
</html>
