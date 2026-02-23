<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page import="com.example.schoolservlet.models.StudentSubject" %>
<%@ page import="com.example.schoolservlet.utils.records.StudentsPerformanceCount" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.schoolservlet.models.Teacher" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<%
  Map<Integer, List<StudentSubject>> studentSubjectMap = (Map<Integer, List<StudentSubject>>) request.getAttribute("studentSubjectMap");
  StudentsPerformanceCount studentsPerformanceCount = (StudentsPerformanceCount) request.getAttribute("studentsPerformanceCount");
  boolean hasFilter = request.getAttribute("hasFilter") != null;
  Teacher teacher = (Teacher) request.getAttribute("teacher");
  int pageNumber = (Integer) request.getAttribute("page");
  int totalPages = (Integer) request.getAttribute("totalPages");
%>
<body>
<section>
  <h1><%=teacher != null ? OutputFormatService.formatName(teacher.getName()) : "Professor"%></h1>
</section>
<section style="display: flex; gap: 20px">
  <div>
    <h3>Aprovados</h3>
    <div style="display: flex; gap: 10px">
      <p style="font-size: 20px"><%=studentsPerformanceCount.approved()%></p>
      <p>alunos</p>
    </div>
  </div>
  <div>
    <h3>Pendentes</h3>
    <div style="display: flex; gap: 10px">
      <p style="font-size: 20px"><%=studentsPerformanceCount.pending()%></p>
      <p>alunos</p>
    </div>
  </div>
  <div>
    <h3>Reprovados</h3>
    <div style="display: flex; gap: 10px">
      <p style="font-size: 20px"><%=studentsPerformanceCount.failed()%></p>
      <p>alunos</p>
    </div>
  </div>
</section>
<form style="display:flex; gap: 12px" action="${pageContext.request.contextPath}/teacher/students" method="get">
  <input name="enrollment" type="text" placeholder="Buscar aluno por matrícula">
  <button type="submit">Buscar</button>
</form>

<%if (request.getAttribute("error") != null){ %>
<p><%= request.getAttribute("error")%></p>
<%}%>
<table>
  <thead>
  <tr>
    <th>Matrícula</th>
    <th>Nome</th>
    <th>Matéria</th>
    <th>Nota 1</th>
    <th>Nota 2</th>
    <th>Média</th>
    <th>Situação de nota</th>
    <th>Ação</th>
  </tr>
  </thead>
  <tbody>
  <%if (!studentSubjectMap.isEmpty()){%>
  <%for (List<StudentSubject> studentSubjects: studentSubjectMap.values()) {%>
  <%for (StudentSubject studentSubject: studentSubjects){%>
  <tr>
    <td><%=studentSubject.getStudent().getEnrollment()%></td>
    <td><%=OutputFormatService.formatName(studentSubject.getStudent().getName())%></td>
    <td><%=OutputFormatService.formatName(studentSubject.getSubject().getName())%></td>
    <td style="text-align: center"><%=studentSubject.getGrade1() != null ? studentSubject.getGrade1() : "-"%></td>
    <td style="text-align: center"><%=studentSubject.getGrade2() != null ? studentSubject.getGrade2() : "-"%></td>
    <td style="text-align: center"><%=studentSubject.getAverage() != null ? studentSubject.getAverage() : "-"%></td>
    <td><%=studentSubject.getStatus()%></td>
    <td><a href="#">Ver detalhes</a></td>
  </tr>
  <%}%>
  <%}%>
  <%} else {%>
  <td colspan="7" style="text-align: center"><%= hasFilter ? "Nenhum aluno foi encontrado" : "Você não possui nenhum aluno no momento"%></td>
  <%}%>
  </tbody>
</table>
</body>
</html>
