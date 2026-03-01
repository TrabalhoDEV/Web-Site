<%@ page import="java.util.List" %>
<%@ page import="com.example.schoolservlet.models.Subject" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Adicionar turma</title>
</head>
<%
    List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
%>
<body>
    <h3>Adicionar turma</h3>
    <form action="${pageContext.request.contextPath}/admin/school-class/insert" method="post">
        <label for="name">Nome da turma:</label>
        <input type="text" name="name" id="name" placeholder="Digite o nome da turma aqui">

        <fieldset>
            <legend>Selecione as matérias:</legend>
            <%
                if (subjects != null && !subjects.isEmpty()) {
                    for (Subject subject : subjects) {
            %>
            <input type="checkbox" name="subjectIds" value="<%= subject.getId() %>">
            <%= OutputFormatService.formatName(subject.getName()) %><br>
            <%
                    }
                }
            %>
        </fieldset>
        <%if (request.getAttribute("error") != null){%>
            <%=request.getAttribute("error")%>
        <%}%>
        <button type="submit">Enviar</button>
    </form>
    <a href="${pageContext.request.contextPath}/admin/school-class/find-many">Voltar</a>
</body>
</html>
