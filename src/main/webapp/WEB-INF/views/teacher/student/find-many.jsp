<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.utils.records.TeacherStudentGrades" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        tbody tr:hover {
            background-color: #f9f9f9;
        }
        .pagination {
            margin-top: 20px;
        }
        .pagination a {
            margin-right: 10px;
            padding: 5px 10px;
            text-decoration: none;
            background-color: #007bff;
            color: white;
            border-radius: 4px;
        }
        .error {
            color: #d32f2f;
            margin-bottom: 20px;
        }
    </style>
</head>
<%
    Map<Integer, TeacherStudentGrades> studentMap = (Map<Integer, TeacherStudentGrades>) request.getAttribute("studentMap");
    int pageNumber = (Integer) request.getAttribute("currentPage");
    int totalPages = (Integer) request.getAttribute("totalPages");
%>
<body>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Ano escolar</th>
        <th>Nome</th>
        <th>Disciplina</th>
        <th>Nota 1</th>
        <th>Nota 2</th>
        <th>Acoes</th>
    </tr>
    </thead>
    <tbody>
    <%if (!studentMap.isEmpty()) {%>
    <%for (TeacherStudentGrades record : studentMap.values()) {%>
    <tr>
        <td><%=record.studentId()%></td>
        <td><%=record.schoolYear()%></td>
        <td><%=OutputFormatService.formatName(record.studentName())%></td>
        <td><%=record.subjectName()%></td>
        <td><%=record.grade1() != null ? record.grade1() : "-"%></td>
        <td><%=record.grade2() != null ? record.grade2() : "-"%></td>
        <td>
            <form action="${pageContext.request.contextPath}/teacher/students/grades/update" method="post">
                <input type="hidden" name="studentId" value="<%=record.studentId()%>">
                <input type="hidden" name="subjectName" value="<%=record.subjectName()%>">
                <button type="submit">Lançar notas</button>

            </form>
        </td>
    </tr>
    <%}%>
    <%} else {%>
    <tr>
        <td colspan="6" style="text-align: center;">Você não possui nenhum aluno</td>
    </tr>
    <%}%>
    </tbody>
</table>
<%if (request.getAttribute("error") != null) {%>
<div class="error"><%= request.getAttribute("error")%></div>
<%}%>
<div class="pagination">
    <form method="get" action="<%= request.getContextPath() %>/teacher/students" style="display:inline;">
        <input type="hidden" name="nextPage" value="<%=(int) request.getAttribute("currentPage") - 1%>">
        <button type="submit" <%= (int) request.getAttribute("currentPage") == 0?"disabled":""%>>Anterior</button>
    </form>

    <span>Página <%=pageNumber + 1%> de <%=totalPages + 1%></span>

    <form method="get" action="<%= request.getContextPath() %>/teacher/students" style="display:inline;">
        <input type="hidden" name="nextPage" value="<%=(int) request.getAttribute("currentPage") + 1%>">
        <button type="submit" <%= (int) request.getAttribute("totalPages") <= (int) request.getAttribute("currentPage")? "disabled":"" %>>Próxima</button>
    </form>
</div>
</body>
</html>