<%@ page import="com.example.schoolservlet.models.Teacher" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page import="com.example.schoolservlet.models.Subject" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.schoolservlet.models.SchoolClass" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Atualizar Professor</title>
</head>
<body>


<%
    Teacher teacher = (Teacher) request.getAttribute("teacher");
    List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
    List<Subject> teacherSubjects = (List<Subject>) request.getAttribute("teacherSubjects");

    Set<Integer> teacherSubjectIds = new HashSet<>();
    if (teacherSubjects != null) {
        for (Subject ts : teacherSubjects) {
            teacherSubjectIds.add(ts.getId());
        }
    }

    List<SchoolClass> schoolClasses = (List<SchoolClass>) request.getAttribute("schoolClasses");
    List<SchoolClass> teacherSchoolClasses = (List<SchoolClass>) request.getAttribute("teacherSchoolClasses");

    Set<Integer> teacherSchoolClassesId = new HashSet<>();
    if (teacherSchoolClasses != null) {
        for (SchoolClass sc : teacherSchoolClasses) {
            teacherSchoolClassesId.add(sc.getId());
        }
    }
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

    <% for (Subject subject : subjects) { %>
    <input type="checkbox"
           name="subjectIds"
           value="<%=subject.getId()%>"
        <%= teacherSubjectIds.contains(subject.getId()) ? "checked" : "" %>>
    <%= OutputFormatService.formatName(subject.getName()) %><br>
    <% } %>

    <% for (SchoolClass schoolClass : schoolClasses) { %>
    <input type="checkbox"
           name="schoolClassIds"
           value="<%=schoolClass.getId()%>"
        <%= teacherSchoolClassesId.contains(schoolClass.getId()) ? "checked" : "" %>>
    <%= OutputFormatService.formatName(schoolClass.getSchoolYear()) %><br>
    <% } %>

    <% if (request.getAttribute("error") != null) { %>
    <p>
        <%= request.getAttribute("error") %>
    </p>
    <% } %>

    <button type="submit">Atualizar Professor</button>

</form>

</body>
</html>