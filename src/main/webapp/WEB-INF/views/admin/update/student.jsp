<%@ page import="com.example.schoolservlet.models.Student" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Update Student</title>
</head>
<body>
    <h1>Update Student</h1>

    <%
        // Load Student object from request attributes for form pre-population
        Student student = (Student) request.getAttribute("student");
        String errorMessage = (String) request.getAttribute("error");
        String schoolYear = (String) request.getAttribute("schoolYear");
        
        // Verify that student object exists before rendering form
        if (student == null) {
    %>
        <div class="error-message">
            <strong>Error:</strong> Student data could not be loaded. Please try again.
        </div>
        <a href="${pageContext.request.contextPath}/admin/student/find-many">Back to Student List</a>
    <%
            return;
        }
        
        // Display error message if validation errors occurred
        if (errorMessage != null && !errorMessage.trim().isEmpty()) {
    %>
        <div class="error-message">
            <strong>Error:</strong> <%= errorMessage %>
        </div>
    <% } %>
    
    <!-- Student update form: POST to update servlet -->
    <form method="post" action="${pageContext.request.contextPath}/admin/student/update">
        <!-- Enrollment field (read-only identifier) -->
        <div class="form-group">
            <label for="enrollment">Enrollment</label>
            <input type="text" id="enrollment" name="enrollment" 
                   value="<%= String.format("%06d", student.getId()) %>" readonly />
        </div>
        
        <!-- Name field (editable) -->
        <div class="form-group">
            <label for="name">Name</label>
            <input type="text" id="name" name="name" 
                   value="<%= student.getName() != null ? student.getName() : "" %>" required />
        </div>
        
        <!-- Email field (editable) -->
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" 
                   value="<%= student.getEmail() != null ? student.getEmail() : "" %>" required />
        </div>
        
        <!-- CPF field (read-only, for validation purposes) -->
        <div class="form-group">
            <label for="cpf">CPF</label>
            <input type="text" id="cpf" name="cpf" 
                   value="<%= student.getCpf() != null ? student.getCpf() : "" %>" readonly />
        </div>
        
        <!-- School year field (read-only) -->
        <div class="form-group">
            <label for="schoolClass">School Year</label>
            <input type="text" id="schoolClass" name="schoolClass" 
                   value="<%= schoolYear != null ? schoolYear : "" %>" readonly />
        </div>
        
        <!-- Submit button -->
        <button type="submit">Update</button>
    </form>
</body>
</html>