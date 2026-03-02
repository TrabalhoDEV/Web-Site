<%@ page import="com.example.schoolservlet.models.SchoolClass" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page import="com.example.schoolservlet.models.Subject" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashSet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Editar turma</title>
</head>
<body>
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

  <button type="submit">Enviar</button>
</form>
<a href="${pageContext.request.contextPath}/admin/school-class/find-many">Voltar</a>
</body>
</html>