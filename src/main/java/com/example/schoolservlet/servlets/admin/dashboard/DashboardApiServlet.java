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

/**
 * Servlet responsible for providing dashboard data through a JSON API endpoint.
 * <p>
 * This servlet handles HTTP GET requests and returns aggregated dashboard
 * information used to populate charts and KPI indicators in the administrative
 * dashboard. The data is retrieved through the {@code DashboardDAO} and
 * serialized to JSON using {@code Gson}.
 * </p>
 *
 * <p>
 * Access to this endpoint is restricted to administrators and validated
 * before the data is returned.
 * </p>
 */
@WebServlet(name = "admin-dashboard-api", value = "/admin/dashboard/api/all")
public class DashboardApiServlet extends HttpServlet {

    private final DashboardDAO dao  = new DashboardDAO();
    private final Gson gson = new Gson();

    /**
     * Handles HTTP GET requests to retrieve all dashboard data in JSON format.
     * <p>
     * The method validates whether the current user has administrative access,
     * retrieves all dashboard information, and writes the serialized JSON
     * response to the client.
     * </p>
     *
     * @param request the {@link HttpServletRequest} containing the client request
     * @param response the {@link HttpServletResponse} used to return the JSON response
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException if an input or output error occurs while writing the response
     */
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

    /**
     * Retrieves all dashboard datasets required by the administrative dashboard.
     * <p>
     * The method gathers multiple statistics and analytical datasets from the
     * {@code DashboardDAO}, including KPI metrics, class statistics, grade
     * averages, approval rates, and teacher workload information.
     * </p>
     *
     * @return a map containing all dashboard datasets organized by category
     * @throws Exception if an error occurs while retrieving the dashboard data
     */
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