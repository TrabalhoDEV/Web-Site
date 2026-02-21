<%@ page import="com.example.schoolservlet.models.Teacher" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Atualizar Professor</title>
</head>
<body>


<%
    Teacher teacher = (Teacher) request.getAttribute("teacher");
%>

<h2>Atualizar Professor</h2>
<form action="${pageContext.request.contextPath}/admin/teacher/update" method="post">
    <input type="hidden" name="id" value="<%=teacher.getId()%>" />
    <div class="form-group">
        <label for="name">Nome</label>
        <input
                type="text"
                id="name"
                name="name"
                value="<%=OutputFormatService.formatName(teacher.getName())%>"
                placeholder="Ex: João Silva"
                required>
    </div>
    <div class="form-group">
        <label for="email">Email</label>
        <input
                type="email"
                id="email"
                name="email"
                value="<%=teacher.getEmail()%>"
                placeholder="Ex: joao@email.com"
                required>
    </div>
    <div class="form-group">
    <label for="username">Usuário</label>
    <input
            type="text"
            id="username"
            name="username"
            value="<%=teacher.getUsername()%>"
            placeholder="Ex: joaosilva"
            required>
    </div>

    <% if (request.getAttribute("error") != null) { %>
    <p>
        <%= request.getAttribute("error") %>
    </p>
    <% } %>

    <button type="submit">Atualizar Professor</button>

</form>

</body>
</html>