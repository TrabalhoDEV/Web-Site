<%@ page import="com.example.schoolservlet.models.SchoolClass" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Deletar turma</title>
</head>

<%
  List<SchoolClass> schoolClasses= (List<SchoolClass>) request.getAttribute("schoolClasses");
%>
<body>
    <h1>Deletar turma</h1>
    <p>Essa turma possui alunos, escolha a turma que quer redirecioná-los</p>
    <form action="${pageContext.request.contextPath}/admin/school-class/delete" method="post">
      <input type="hidden" name="id" value="<%= request.getAttribute("id")%>">

      <label for="anoEscolar">Ano Escolar:</label>
      <select id="anoEscolar" name="newId" required>
        <option value="" selected disabled>-- Selecione um ano --</option>
        <%for (SchoolClass schoolClass: schoolClasses) {%>
          <%if (schoolClass.getId() != (int) request.getAttribute("id")) {%>
            <option value="<%=schoolClass.getId()%>"><%= schoolClass.getSchoolYear().toUpperCase()%></option>
          <%}%>
        <%}%>
      </select>

      <%if (request.getAttribute("error") != null){%>
        <p><%=request.getAttribute("error")%></p>
      <%}%>

      <button type="submit">Enviar</button>
</form>
</body>
</html>
