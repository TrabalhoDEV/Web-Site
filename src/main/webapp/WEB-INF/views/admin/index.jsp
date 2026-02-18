<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin - Cadastro de Estudante</title>
</head>
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
        } else if (request.getAttribute("success") != null && (boolean) request.getAttribute("success")) {
    %>
    <script>
        // Display success alert when student registration is completed successfully
        alert("Estudante cadastrado com sucesso!");
    </script>
    <%
        }
    %>

    <!-- Title of the form -->
    <h2>Cadastro de Estudante</h2>

    <!-- Form to submit student registration
         Action: sends data to admin/sign-student servlet
         Method: POST (more secure than GET for form submission) -->
    <form action="${pageContext.request.contextPath}/admin/sign-student" method="post">

        <!-- CPF field: expects numeric input -->
        <label for="cpf">CPF:</label>
        <input type="number" id="cpf" name="cpf" placeholder="Digite o CPF do estudante" required>
        <br><br>

        <!-- Academic year selection dropdown -->
        <label for="anoEscolar">Ano Escolar:</label>
        <select id="anoEscolar" name="anoEscolar" required>
            <option value="">-- Selecione um ano --</option>
            <!-- Elementary school years (1-9) -->
            <option value="1">1º ano Fundamental</option>
            <option value="2">2º ano Fundamental</option>
            <option value="3">3º ano Fundamental</option>
            <option value="4">4º ano Fundamental</option>
            <option value="5">5º ano Fundamental</option>
            <option value="6">6º ano Fundamental</option>
            <option value="7">7º ano Fundamental</option>
            <option value="8">8º ano Fundamental</option>
            <option value="9">9º ano Fundamental</option>
        </select>
        <br><br>

        <!-- Submit button to send the form -->
        <input type="submit" value="Enviar">
    </form>
</body>
</html>
