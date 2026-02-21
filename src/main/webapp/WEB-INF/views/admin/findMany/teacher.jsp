<%@ page import="com.example.schoolservlet.models.Teacher" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page import="com.example.schoolservlet.models.Subject" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.schoolservlet.models.SchoolClass" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Listar professores</title>
</head>
<%
  Map<Integer, Teacher> teacherMap = (Map<Integer, Teacher>) request.getAttribute("teacherMap");
  Map<Integer, List<Subject>> teacherSubjects = (Map<Integer, List<Subject>>) request.getAttribute("teacherSubjects");
  Map<Integer, List<SchoolClass>> teacherClasses = (Map<Integer, List<SchoolClass>>) request.getAttribute("teacherClasses");
  int pageNumber = (Integer) request.getAttribute("page");
%>
<body>
<% if (teacherMap != null && !teacherMap.isEmpty()) { %>

<table>
  <thead>
  <tr>
    <th>Nome</th>
    <th>Usuário</th>
    <th>Email</th>
    <th>Matérias</th>
    <th>Turmas</th>
    <th>Ações</th>
  </tr>
  </thead>
  <tbody>
  <% for (Teacher teacher : teacherMap.values()) { %>
  <tr>
    <td><%= OutputFormatService.formatName(teacher.getName()) %></td>
    <td><%= teacher.getUsername() %></td>
    <td><%= teacher.getEmail() %></td>
    <td>
      <% List<Subject> subjects = teacherSubjects.get(teacher.getId());
        if(subjects != null){
          for(Subject s : subjects){ %>
      <%= OutputFormatService.formatName(s.getName()) %><br>
      <%     }
      } %>
    </td>
    <td>
      <% List<SchoolClass> classes = teacherClasses.get(teacher.getId());
        if(classes != null){
          for(SchoolClass sc : classes){ %>
      <%= OutputFormatService.formatName(sc.getSchoolYear()) %><br>
      <%     }
      } %>
    </td>
    <td>
      <a href="${pageContext.request.contextPath}/admin/teacher/update?id=<%=teacher.getId()%>">
        Editar
      </a>
    </td>
  </tr>
  <% } %>
  </tbody>
</table>
<div>
  <% int totalPages = (Integer) request.getAttribute("totalPages"); %>
  <% int currentPage = (Integer) request.getAttribute("page"); %>

  <% if (totalPages > 1) { %>
  <% if (currentPage > 1) { %>
  <a href="?page=<%=currentPage-1%>">Anterior</a>
  <% } %>

  <% for(int i=1; i<=totalPages; i++) { %>
  <% if (i == currentPage) { %>
  <strong><%=i%></strong>
  <% } else { %>
  <a href="?page=<%=i%>"><%=i%></a>
  <% } %>
  <% } %>

  <% if (currentPage < totalPages) { %>
  <a href="?page=<%=currentPage+1%>">Próxima</a>
  <% } %>
  <% } %>
</div>

<% } else if (request.getAttribute("error") != null) { %>

<p><%= request.getAttribute("error") %></p>

<% } else { %>

<p>Nenhum professor foi encontrado</p>

<% } %>

<a href="${pageContext.request.contextPath}/admin/teacher/insert">
  Adicionar professor
</a>
</body>
</html>