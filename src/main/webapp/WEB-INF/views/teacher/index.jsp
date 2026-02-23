<%@ page import="com.example.schoolservlet.models.StudentSubject" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.models.Teacher" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page import="com.example.schoolservlet.utils.records.StudentsPerformance" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.schoolservlet.utils.records.TeacherPendency" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Home professor</title>
</head>
<%
  Map<Integer, StudentSubject> studentSubjectMap = (Map<Integer, StudentSubject>) request.getAttribute("studentsToHelpMap");
  StudentsPerformance studentsPerformance = (StudentsPerformance) request.getAttribute("studentsPerformance");
  List<TeacherPendency> pendencies = (List<TeacherPendency>) request.getAttribute("pendencies");
  Teacher teacher = (Teacher) request.getAttribute("teacher");
%>
<body>
<section>
  <h1>Olá, <%=teacher != null ? OutputFormatService.formatName(teacher.getName()) : "professor"%>!</h1>
</section>
<section>
  <h3>Desempenho</h3>
  <p>Aprovados: <%= studentsPerformance.approved()%>%</p>
  <p>Reprovados: <%= studentsPerformance.failed()%>%</p>
  <p>Pendentes: <%= studentsPerformance.pending()%>%</p>
</section>
<section>
  <h3>Pendências</h3>
  <%if (!pendencies.isEmpty()){%>
    <%for (TeacherPendency pendency: pendencies){%>
      <div>
        <h4><%=pendency.grade1() == null && pendency.grade2() == null ? String.format("Lançar notas - %s", OutputFormatService.formatName(pendency.studentName()))
                : (pendency.grade1() == null ? String.format("Lançar N1- %s", OutputFormatService.formatName(pendency.studentName()))
                : String.format("Lançar N2 - %s", OutputFormatService.formatName(pendency.studentName())))
        %></h4>
        <h5><%= OutputFormatService.formatName(pendency.subjectName())%></h5>
        <p>Prazo: <%=OutputFormatService.formatDate(pendency.deadline())%></p>
        <p><%=pendency.status()%></p>
      </div>
    <%}%>
  <%} else {%>
  <p>Nenhuma pendência foi encontrada</p>
  <%}%>
</section>
<section>
  <div>
    <h3>Alunos em atenção</h3>
    <a href="${pageContext.request.contextPath}/teacher/students">Ver alunos</a>
  </div>
  <%if (studentSubjectMap != null && !studentSubjectMap.isEmpty()){%>
    <%for(StudentSubject studentSubject: studentSubjectMap.values()){%>
      <div>
        <p><%=OutputFormatService.formatName(studentSubject.getStudent().getName())%></p>
        <p>Média: <%=studentSubject.getAverage()%></p>
      </div>
    <%}%>
  <%} else {%>
    <p>Nenhum aluno foi encontrado nessa situação</p>
  <%}%>

  <%if (request.getAttribute("error") != null) {%>
    <p><%=request.getAttribute("error")%></p>
  <%}%>
</section>
</body>
</html>
