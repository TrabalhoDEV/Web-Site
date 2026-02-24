<%--
  Created by IntelliJ IDEA.
  User: Eduardo
  Date: 23/02/2026
  Time: 20:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.schoolservlet.models.StudentSubject" %>
<%
    StudentSubject studentSubject = (StudentSubject) request.getAttribute("studentSubject");
    String error = (String) request.getAttribute("error");
%>
<html>
<head>
    <title>Release Grade</title>
</head>
<body>
    <h1>Release Grade</h1>
    
    <% if (error != null) { %>
        <div class="error"><%= error %></div>
    <% } %>
    
    <% if (studentSubject != null) { %>
        <div class="info">
            <p><strong>Student:</strong> <%= studentSubject.getStudent().getName() %> (ID: <%= studentSubject.getStudent().getId() %>)</p>
            <p><strong>Subject:</strong> <%= studentSubject.getSubject().getName() %> (ID: <%= studentSubject.getSubject().getId() %>)</p>
            <p><strong>Grade 1:</strong> <%= studentSubject.getGrade1() != null ? studentSubject.getGrade1() : "Not set" %></p>
            <p><strong>Grade 2:</strong> <%= studentSubject.getGrade2() != null ? studentSubject.getGrade2() : "Not set" %></p>
            <p><strong>Average:</strong> <%= studentSubject.getAverage() != null ? String.format("%.2f", studentSubject.getAverage()) : "Not available" %></p>
        </div>
        
        <form method="post" action="${pageContext.request.contextPath}/teacher/students/grades/release">
            <input type="hidden" name="studentId" value="<%= studentSubject.getStudent().getId() %>">
            <input type="hidden" name="courseId" value="<%= studentSubject.getSubject().getId() %>">

            <div class="form-group">
                <label for="grade1">Grade 1 (0-10):</label>
                <input type="number" id="grade1" name="grade1" min="0" max="100" step="0.01"
                       value="<%= studentSubject.getGrade1() != null ? studentSubject.getGrade1() : "" %>">
            </div>

            <div class="form-group">
                <label for="grade2">Grade 2 (0-10):</label>
                <input type="number" id="grade2" name="grade2" min="0" max="100" step="0.01"
                       value="<%= studentSubject.getGrade2() != null ? studentSubject.getGrade2() : "" %>">
            </div>
            
            <div class="form-group">
                <label for="obs">Observations:</label>
                <textarea id="obs" name="obs" rows="4"><%= studentSubject.getObs() != null ? studentSubject.getObs() : "" %></textarea>
            </div>
            
            <div class="form-group">
                <button type="submit" class="btn">Release Grade</button>
            </div>
        </form>
    <% } %>
</body>
</html>