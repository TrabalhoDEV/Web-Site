<%--
  Created by IntelliJ IDEA.
  User: caiomachado-ieg
  Date: 23/02/2026
  Time: 09:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sign Up Student</title>
</head>
<body>
    <%
        String register = request.getParameter("register");
        if ("sucess".equals(register)) {
    %>
    <div style="background-color: #d4edda; color: #155724; padding: 15px; border: 1px solid #c3e6cb; border-radius: 5px; margin-bottom: 20px;">
        <strong>Sucesso!</strong> O cadastro foi realizado com sucesso.
    </div>
    <% } %>

    <form action="${pageContext.request.contextPath}/pages/students/register" method="post">
        <input type="hidden" name="enrollment" value="<%= request.getParameter("enrollment") != null ? request.getParameter("enrollment") : request.getAttribute("enrollment") %>">

        <label for="name">Name:</label>
        <input type="text" id="name" name="name" value="${param.name}" required>

        <label for="email">Email:</label>
        <input type="text" id="email" name="email" value="${param.email}" required>

        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">Register</button>

        <% if (request.getAttribute("error") != null) { %>
        <span style="color: red;"><%= request.getAttribute("error") %></span>
        <% } %>
    </form>
</body>
</html>
