<%@ page import="com.example.schoolservlet.models.Subject" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Listar matérias</title>
</head>
<body>
<%
    Subject subject = (Subject) request.getAttribute("subject");
%>
<form action="${pageContext.request.contextPath}/admin/subject/update" method="post">

    <div class="form-group">
        <label for="name">Nome da disciplina</label>
        <input
                value="<%=OutputFormatService.formatName(subject.getName())%>"
                type="text"
                id="name"
                name="name"
                placeholder="Ex: Matemática"
                required>
    </div>

    <div class="form-group">
        <label for="deadline">Prazo final da disciplina</label>
        <input
                value="<%=subject.getDeadline()%>"
                type="date"
                id="deadline"
                name="deadline"
                required
        >
    </div>

    <%if (request.getAttribute("error") != null) {%>
    <p><%=request.getAttribute("error") %></p>
    <%}%>

    <button type="submit">Atualizar disciplina</button>
</form>
</body>
</html>
