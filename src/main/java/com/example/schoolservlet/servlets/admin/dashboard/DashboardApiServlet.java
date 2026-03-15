package com.example.schoolservlet.servlets.admin.dashboard;

import com.example.schoolservlet.daos.DashboardDAO;
import com.example.schoolservlet.utils.AccessValidation;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet(name = "admin-dashboard-api", value = "/admin/dashboard/api/all")
public class DashboardApiServlet extends HttpServlet {

    private final DashboardDAO dao  = new DashboardDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        if (!AccessValidation.isAdmin(request, response)) return;

        PrintWriter out = response.getWriter();

        try {
            out.print(gson.toJson(fetchAll()));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", e.getMessage())));
        }
    }

    private Map<String, Object> fetchAll() throws Exception {
        Map<String, Object> all = new LinkedHashMap<>();
        all.put("kpis",dao.getKpis());
        all.put("studentsPerClass",dao.getStudentsPerClass());
        all.put("avgGradesSubject",dao.getAvgGradesSubject());
        all.put("avgGradesClass",dao.getAvgGradesClass());
        all.put("approvalRate",dao.getApprovalRate());
        all.put("subjectsPerTeacher",dao.getSubjectsPerTeacher());
        all.put("classesPerTeacher",dao.getClassesPerTeacher());
        return all;
    }
}