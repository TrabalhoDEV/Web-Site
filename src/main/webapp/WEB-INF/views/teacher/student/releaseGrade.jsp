<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Liberar Notas</title>
</head>
<body>
    <div>
        <h1>Liberar Notas</h1>

        <%
            Object studentSubjectObj = request.getAttribute("studentSubject");
            if (studentSubjectObj == null) {
        %>
            <p>Erro: Informações do aluno-matéria não disponíveis.</p>
        <%
            } else {
                com.example.schoolservlet.models.StudentSubject ss =
                    (com.example.schoolservlet.models.StudentSubject) studentSubjectObj;
        %>

        <div>
            <p><strong>Aluno:</strong> <%= ss.getStudent().getName() %></p>
            <p><strong>Matrícula:</strong> <%= String.format("%06d", ss.getStudent().getId()) %></p>
            <p><strong>Disciplina:</strong> <%= ss.getSubject().getName() %></p>
            <p><strong>Status:</strong> <%= ss.getStatus() %></p>
        </div>

        <form method="POST" action="<%= request.getContextPath() %>/teacher/students/grades/release">
            <input type="hidden" name="studentSubjectId" value="<%= ss.getId() %>">

            <div>
                <label for="grade1">Nota 1ª Avaliação:</label>
                <input type="number" id="grade1" name="grade1" step="0.1" min="0" max="10"
                       value="<%= ss.getGrade1() != null ? ss.getGrade1() : "" %>" required>
            </div>

            <div>
                <label for="grade2">Nota 2ª Avaliação:</label>
                <input type="number" id="grade2" name="grade2" step="0.1" min="0" max="10"
                       value="<%= ss.getGrade2() != null ? ss.getGrade2() : "" %>" required>
            </div>

            <div>
                <label for="obs">Observações:</label>
                <textarea id="obs" name="obs" rows="4"><%= ss.getObs() != null ? ss.getObs() : "" %></textarea>
            </div>

            <div>
                Média: <span id="mediaValue">-</span>
            </div>

            <div>
                <button type="submit">Salvar Notas</button>
            </div>
        </form>

        <script>
            function calcularMedia() {
                const grade1 = parseFloat(document.getElementById("grade1").value) || 0;
                const grade2 = parseFloat(document.getElementById("grade2").value) || 0;

                if (grade1 > 0 && grade2 > 0) {
                    const media = ((grade1 + grade2) / 2).toFixed(2);
                    document.getElementById("mediaValue").textContent = media;
                } else {
                    document.getElementById("mediaValue").textContent = "-";
                }
            }

            document.getElementById("grade1").addEventListener("input", calcularMedia);
            document.getElementById("grade2").addEventListener("input", calcularMedia);

            calcularMedia();
        </script>

        <% } %>
    </div>
</body>
</html>
