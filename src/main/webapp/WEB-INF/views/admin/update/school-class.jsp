<<<<<<< HEAD
<%@ page import="com.example.schoolservlet.models.SchoolClass" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page import="com.example.schoolservlet.models.Subject" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashSet" %>
=======
>>>>>>> 987874a (feat: creating routes to create or update school_class)
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Editar turma</title>
</head>
<body>
<<<<<<< HEAD
<%
  SchoolClass schoolClass = (SchoolClass) request.getAttribute("schoolClass");
%>

<%
  List<Subject> schoolClassSubjects = (List<Subject>) request.getAttribute("schoolClassSubjects");
  List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");

  Set<Integer> subjectsIds = new HashSet<>();
  if (schoolClassSubjects != null) {
    for (Subject ts : schoolClassSubjects) {
      subjectsIds.add(ts.getId());
    }
  }
%>
<h3>Editar turma</h3>
<form action="${pageContext.request.contextPath}/admin/school-class/update" method="post">
  <%if (schoolClass!= null){%>
    <label for="schoolYear">Nome da turma:</label>
    <input type="text" name="schoolYear" id="schoolYear" placeholder="Digite o nome da turma aqui" value="<%=schoolClass.getSchoolYear().toUpperCase()%>">
    <input type="hidden" name="id" value="<%=schoolClass.getId()%>">
  <%}%>
  <fieldset>
    <legend>Selecione as matérias:</legend>
  <% for (Subject subject : subjects) { %>
  <input type="checkbox"
         name="subjectIds"
         value="<%=subject.getId()%>"
    <%= subjectsIds.contains(subject.getId()) ? "checked" : "" %>>
  <%= OutputFormatService.formatName(subject.getName()) %><br>
  <% } %>
  </fieldset>

  <%if (request.getAttribute("error") != null){%>
  <p><%=request.getAttribute("error")%></p>
  <%}%>

=======
<h3>Editar turma</h3>
<form action="${pageContext.request.contextPath}/admin/school-class/update" method="put">
  <label for="name">Nome da turma:</label>
  <input type="text" name="name" id="name" placeholder="Digite o nome da turma aqui">
  <%if (request.getAttribute("error") != null){%>
  <%=request.getAttribute("error")%>
  <%}%>
>>>>>>> 987874a (feat: creating routes to create or update school_class)
  <button type="submit">Enviar</button>
</form>
<a href="${pageContext.request.contextPath}/admin/school-class/find-many">Voltar</a>
</body>
</html>