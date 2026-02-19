<%@ page import="com.example.schoolservlet.models.SchoolClass" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin - Cadastro de Estudante</title>
</head>
<%
    List<SchoolClass> schoolClasses= (List<SchoolClass>) request.getAttribute("schoolClasses");
%>
<body>
    <!-- JavaScript alert for error or success messages -->
    <%
        if (request.getAttribute("error") != null) {
    %>
    <script>
        // Display error alert with error message only
        alert("<%= request.getAttribute("error") %>");
    </script>
    <%
        } else if (request.getAttribute("success") != null){
    %>
    <script>
        // Display error alert with error message only
        alert("Estudante cadastrado com sucesso");
    </script>
    <%}%>

    <!-- Title of the form -->
    <h2>Cadastro de Estudante</h2>

    <!-- Form to submit student registration
         Action: sends data to admin/sign-student servlet
         Method: POST (more secure than GET for form submission) -->
    <form action="${pageContext.request.contextPath}/admin/add-student" method="post">

        <!-- CPF field: expects numeric input -->
        <label for="cpf">CPF:</label>
        <input type="number" id="cpf" name="cpf" placeholder="Digite o CPF do estudante" required>
        <br><br>

        <!-- Academic year selection dropdown -->
        <label for="anoEscolar">Ano Escolar:</label>
        <select id="anoEscolar" name="anoEscolar" required>
            <option value="" selected disabled>-- Selecione um ano --</option>
            <%for (SchoolClass schoolClass: schoolClasses) {%>
                <option value="<%=schoolClass.getId()%>"><%= schoolClass.getSchoolYear()%></option>
            <%}%>
        </select>
        <br><br>

        <!-- Submit button to send the form -->
        <input type="submit" value="Enviar">
    </form>
<a href="${pageContext.request.contextPath}/admin/students">Ver alunos</a>
</body>
</html>
