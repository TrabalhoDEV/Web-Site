<%@ page import="com.example.schoolservlet.models.Teacher" %>
<%@ page import="com.example.schoolservlet.models.Subject" %>
<%@ page import="com.example.schoolservlet.models.SchoolClass" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Detalhes do Professor</title>
</head>

<%
    Teacher teacher = (Teacher) request.getAttribute("teacher");
    List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
    List<SchoolClass> classes = (List<SchoolClass>) request.getAttribute("classes");
%>

<body>
<% if (request.getAttribute("error") != null) { %>
<p><%= request.getAttribute("error") %></p>
<% } %>
<% if (teacher != null) { %>

<h2>Detalhes do Professor</h2>

<hr>

<h3>Informações Básicas</h3>
<p><strong>Nome:</strong> <%= OutputFormatService.formatName(teacher.getName()) %></p>
<p><strong>Usuário:</strong> <%= teacher.getUsername() %></p>
<p><strong>Email:</strong> <%= teacher.getEmail() %></p>

<hr>

<h3>Matérias</h3>
<% if (subjects != null && !subjects.isEmpty()) { %>
<ul>
    <% for (Subject s : subjects) { %>
    <li><%= OutputFormatService.formatName(s.getName()) %></li>
    <% } %>
</ul>
<% } else { %>
<p>Este professor não possui matérias vinculadas.</p>
<% } %>

<hr>

<h3>Turmas</h3>

<% if (classes != null && !classes.isEmpty()) { %>
<ul>
    <% for (SchoolClass sc : classes) { %>
    <li><%= OutputFormatService.formatName(sc.getSchoolYear()) %></li>
    <% } %>
</ul>
<% } else { %>
<p>Este professor não possui turmas vinculadas.</p>
<% } %>

<hr>

<br>

<a href="${pageContext.request.contextPath}/admin/teacher/find-many">
    ← Voltar para lista
</a>

|

<a href="${pageContext.request.contextPath}/admin/teacher/update?id=<%=teacher.getId()%>">
    Editar
</a>

<% } else { %>

<p>Professor não encontrado.</p>

<% } %>

</body>
</html>
