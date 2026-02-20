<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Listar matérias</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/admin/subject/insert" method="post">

    <div class="form-group">
        <label for="name">Nome da disciplina</label>
        <input
                type="text"
                id="name"
                name="name"
                placeholder="Ex: Matemática"
                required>
    </div>

    <div class="form-group">
        <label for="deadline">Prazo final da disciplina</label>
        <input
                type="date"
                id="deadline"
                name="deadline"
                required
        >
    </div>

    <%if (request.getAttribute("error") != null) {%>
    <p><%=request.getAttribute("error") %></p>
    <%}%>

    <button type="submit">Cadastrar disciplina</button>
</form>
</body>
</html>
