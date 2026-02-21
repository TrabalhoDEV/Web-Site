<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.models.StudentSubject" %>
<%@ page import="com.example.schoolservlet.utils.Constants" %>

<html>
<head>
    <title>Boletim</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background: #f4f4f4; }
    </style>
</head>
<body>

<aside>
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/student/home">Home</a></li>
        </ul>
    </nav>
</aside>

<h1>Boletim</h1>

<%

    Map<Integer, StudentSubject> studentSubjectMap =
            (Map<Integer, StudentSubject>) request.getAttribute("studentSubjectMap");
%>

<% if (studentSubjectMap == null || studentSubjectMap.isEmpty()) { %>
<p>Nenhuma disciplina encontrada.</p>
<% } else { %>

<table>
    <thead>
    <tr>
        <th>Disciplina</th>
        <th>Nota 1</th>
        <th>Nota 2</th>
        <th>Média</th>
        <th>Status Final</th>
    </tr>
    </thead>
    <tbody>

    <%
        for (StudentSubject ss : studentSubjectMap.values()) {
    %>
    <tr>
        <td><%= (ss.getSubject() != null && ss.getSubject().getName() != null)
                ? ss.getSubject().getName()
                : "—" %></td>

        <td><%= (ss.getGrade1() != null) ? ss.getGrade1() : "—" %></td>

        <td><%= (ss.getGrade2() != null) ? ss.getGrade2() : "—" %></td>

        <td>
            <%
                Double avg = ss.getAverage();
                if (avg != null) {
                    out.print(String.format("%.2f", avg));
                } else {
                    out.print("—");
                }
            %>
        </td>

        <td><%= avg >= 7? "Aprovado" : "Reprovado"%></td>
    </tr>
    <%
        }
    %>

    </tbody>
</table>

<div style="margin-top:12px;">
    <form method="get" action="<%= request.getContextPath() %>/student/bulletin" style="display:inline;">
        <input type="hidden" name="nextPage" value="<%=(int) request.getAttribute("currentPage") - 1%>">
        <button type="submit" <%= (int) request.getAttribute("currentPage") == 0?"disabled":""%>>Anterior</button>
    </form>

    &nbsp;

    <form method="get" action="<%= request.getContextPath() %>/student/bulletin" style="display:inline;">
        <input type="hidden" name="nextPage" value="<%=(int) request.getAttribute("currentPage") + 1%>">
        <button type="submit" <%= (int) request.getAttribute("totalPages") <= (int) request.getAttribute("currentPage")? "disabled":"" %>>Próxima</button>
    </form>
</div>

<% } %>
</body>
</html>