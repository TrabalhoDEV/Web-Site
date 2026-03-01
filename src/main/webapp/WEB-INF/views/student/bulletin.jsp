<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.models.StudentSubject" %>
<%@ page import="com.example.schoolservlet.utils.records.AuthenticatedUser" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Aluno | Vértice - Boletim</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/layout/tokens.css" />
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/layout/topbar.css" />
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/layout/navbar.css" />
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/student/boletim.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/assets/css/img/logo_pequena.svg" type="image/x-icon">
</head>

<body>

<%
    AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");
    Map<Integer, StudentSubject> studentSubjectMap =
            (Map<Integer, StudentSubject>) request.getAttribute("studentSubjectMap");

    int approved = 0;
    int reproved = 0;
    int pending = 0;

    if (studentSubjectMap != null && !studentSubjectMap.isEmpty()) {
        for (StudentSubject ss : studentSubjectMap.values()) {
            Double avg = ss.getAverage();
            if (ss.getGrade1() == null || ss.getGrade2() == null) {
                pending++;
            } else if (avg >= 7) {
                approved++;
            } else {
                reproved++;
            }
        }
    }
%>

<div class="app-layout">

  <!-- SIDEBAR -->

  <aside class="sidebar">

    <div class="sidebar-logo">
      <img src="<%= request.getContextPath() %>/assets/css/img/logo_pequena.svg" alt="Vertice Logo" />
    </div>

    <nav class="sidebar-nav">
      <ul>
        <li class="sidebar-item non-active">
          <a href="<%= request.getContextPath() %>/student/home">
              <span class="icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="21" height="22" viewBox="0 0 21 22" fill="none">
                  <path d="M20.487 9.26216L11.737 0.512158C11.4089 0.184217 10.9639 0 10.5 0C10.0361 0 9.59113 0.184217 9.26298 0.512158L0.51298 9.26216C0.349688 9.42424 0.220247 9.61716 0.132184 9.82972C0.0441203 10.0423 -0.000807906 10.2702 1.0996e-05 10.5003V21.0003C1.0996e-05 21.2323 0.0921984 21.4549 0.256293 21.619C0.420387 21.7831 0.642946 21.8753 0.875011 21.8753H20.125C20.3571 21.8753 20.5796 21.7831 20.7437 21.619C20.9078 21.4549 21 21.2323 21 21.0003V10.5003C21.0008 10.2702 20.9559 10.0423 20.8678 9.82972C20.7798 9.61716 20.6503 9.42424 20.487 9.26216ZM19.25 20.1253H1.75001V10.5003L10.5 1.75028L19.25 10.5003V20.1253Z" fill="currentColor"></path>
                  <rect x="9" y="15" width="3" height="3" rx="1" fill="currentColor"></rect>
                  </svg>
              </span>
            <span>Início</span>
          </a>
        </li>

        <li class="sidebar-item active">
          <a href="<%= request.getContextPath() %>/student/bulletin">
              <span class="icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="28" height="18" viewBox="0 0 28 18" fill="none">
                  <path d="M12.5872 12.0252C13.7599 11.2444 14.6502 10.107 15.1265 8.78116C15.6028 7.45528 15.6398 6.01132 15.232 4.66281C14.8241 3.31429 13.9932 2.13282 12.8619 1.2931C11.7307 0.453382 10.3593 0 8.95043 0C7.5416 0 6.17016 0.453382 5.03893 1.2931C3.9077 2.13282 3.07673 3.31429 2.66891 4.66281C2.26108 6.01132 2.29805 7.45528 2.77435 8.78116C3.25065 10.107 4.14099 11.2444 5.31371 12.0252C3.19238 12.807 1.3807 14.2538 0.149026 16.1497C0.0842937 16.2459 0.0393302 16.354 0.0167497 16.4678C-0.00583087 16.5815 -0.00557806 16.6986 0.0174934 16.8122C0.0405649 16.9259 0.0859948 17.0338 0.151142 17.1297C0.21629 17.2257 0.299856 17.3077 0.396982 17.371C0.494109 17.4344 0.602859 17.4778 0.71691 17.4987C0.830962 17.5197 0.948041 17.5178 1.06134 17.4931C1.17464 17.4684 1.2819 17.4214 1.37689 17.3549C1.47188 17.2884 1.5527 17.2037 1.61465 17.1056C2.40914 15.8837 3.49629 14.8795 4.77738 14.1844C6.05847 13.4893 7.49291 13.1252 8.95043 13.1252C10.408 13.1252 11.8424 13.4893 13.1235 14.1844C14.4046 14.8795 15.4917 15.8837 16.2862 17.1056C16.4146 17.2964 16.6128 17.4289 16.8381 17.4747C17.0634 17.5204 17.2976 17.4757 17.4902 17.3501C17.6828 17.2245 17.8182 17.0281 17.8671 16.8035C17.9161 16.5789 17.8747 16.344 17.7518 16.1497C16.5202 14.2538 14.7085 12.807 12.5872 12.0252ZM4.13793 6.56516C4.13793 5.61334 4.42018 4.68289 4.94899 3.89148C5.47779 3.10007 6.2294 2.48324 7.10877 2.11899C7.98814 1.75475 8.95577 1.65944 9.88931 1.84513C10.8228 2.03083 11.6803 2.48917 12.3534 3.16221C13.0264 3.83525 13.4848 4.69276 13.6705 5.62629C13.8562 6.55982 13.7608 7.52746 13.3966 8.40683C13.0324 9.2862 12.4155 10.0378 11.6241 10.5666C10.8327 11.0954 9.90226 11.3777 8.95043 11.3777C7.67452 11.3762 6.45128 10.8687 5.54908 9.96652C4.64687 9.06431 4.13938 7.84107 4.13793 6.56516ZM27.122 17.3605C26.9276 17.4872 26.6909 17.5316 26.4638 17.4838C26.2368 17.436 26.038 17.3 25.9112 17.1056C25.1177 15.8829 24.0307 14.8784 22.7493 14.1835C21.468 13.4887 20.0331 13.1257 18.5754 13.1277C18.3434 13.1277 18.1208 13.0355 17.9567 12.8714C17.7926 12.7073 17.7004 12.4847 17.7004 12.2527C17.7004 12.0206 17.7926 11.798 17.9567 11.6339C18.1208 11.4698 18.3434 11.3777 18.5754 11.3777C19.2841 11.377 19.984 11.2198 20.6249 10.9173C21.2658 10.6148 21.832 10.1745 22.2831 9.62785C22.7341 9.08118 23.0588 8.44164 23.234 7.75492C23.4092 7.0682 23.4306 6.35126 23.2966 5.65532C23.1626 4.95939 22.8766 4.30164 22.4589 3.72906C22.0413 3.15649 21.5023 2.68323 20.8805 2.34311C20.2588 2.00298 19.5695 1.80437 18.8621 1.76148C18.1547 1.7186 17.4465 1.83248 16.7882 2.09501C16.6809 2.14141 16.5654 2.16582 16.4484 2.16681C16.3315 2.1678 16.2156 2.14534 16.1075 2.10076C15.9994 2.05618 15.9013 1.99039 15.819 1.90726C15.7368 1.82414 15.6721 1.72538 15.6286 1.61681C15.5852 1.50824 15.564 1.39206 15.5662 1.27515C15.5684 1.15824 15.5941 1.04296 15.6416 0.936126C15.6891 0.829291 15.7576 0.733068 15.843 0.653143C15.9283 0.573218 16.0288 0.511211 16.1386 0.470788C17.6452 -0.130063 19.3209 -0.151685 20.8425 0.410092C22.3641 0.971869 23.6237 2.07724 24.3784 3.51296C25.1331 4.94868 25.3294 6.61303 24.9294 8.18492C24.5293 9.7568 23.5613 11.1249 22.2122 12.0252C24.3335 12.807 26.1452 14.2538 27.3768 16.1497C27.5036 16.3441 27.5479 16.5808 27.5001 16.8079C27.4524 17.0349 27.3163 17.2337 27.122 17.3605Z" fill="currentColor"></path>
                  </svg>
              </span>
            <span>Boletim</span>
          </a>
        </li>
      </ul>
    </nav>
  </aside>


  <!-- MAIN CONTENT -->

  <main class="main-content">
    <!-- TOPBAR -->

    <header class="topbar">
      <div class="topbar-left"></div>
      <div class="topbar-right">
        <div class="topbar-actions">
          <button class="icon-button">
            <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" viewBox="0 0 25 25" fill="currentColor">
              <path d="M24.7145 14.9754C24.5888 14.8493 24.431 14.7599 24.2583 14.7167C24.0855 14.6735 23.9043 14.6782 23.734 14.7302C21.8643 15.2956 19.8763 15.343 17.9817 14.8674C16.0872 14.3918 14.3572 13.411 12.976 12.0295C11.5949 10.6481 10.6143 8.9177 10.1388 7.0228C9.66328 5.12789 9.71068 3.13946 10.2759 1.26937C10.3284 1.09897 10.3334 0.917484 10.2904 0.744438C10.2475 0.571393 10.1582 0.413333 10.0321 0.287256C9.90609 0.16118 9.74806 0.0718578 9.57505 0.0288961C9.40204 -0.0140655 9.22059 -0.00904108 9.05022 0.0434289C6.46528 0.835443 4.19593 2.42273 2.5651 4.57942C1.13891 6.47332 0.268965 8.72748 0.0530111 11.0886C-0.162943 13.4498 0.283649 15.8244 1.34261 17.9457C2.40158 20.067 4.03096 21.8509 6.04769 23.0971C8.06443 24.3432 10.3886 25.0022 12.7592 24.9999C15.5247 25.0085 18.2167 24.1092 20.4222 22.4402C22.5784 20.809 24.1654 18.5392 24.9572 15.9537C25.0091 15.7839 25.0139 15.6033 24.9711 15.431C24.9284 15.2587 24.8397 15.1012 24.7145 14.9754ZM19.2431 20.8734C17.1662 22.438 14.5941 23.1996 12.0004 23.018C9.40666 22.8363 6.96582 21.7237 5.12721 19.8848C3.2886 18.046 2.17597 15.6047 1.99414 13.0105C1.81232 10.4162 2.57353 7.84356 4.13766 5.76613C5.15671 4.42012 6.47415 3.32901 7.98633 2.57868C7.90018 3.18334 7.85677 3.79333 7.8564 4.40411C7.85997 7.78447 9.20412 11.0254 11.5939 13.4156C13.9837 15.8059 17.2239 17.1503 20.6036 17.1539C21.2154 17.1537 21.8265 17.1103 22.4323 17.024C21.6814 18.5367 20.5897 19.8544 19.2431 20.8734Z" fill="currentColor"></path>
            </svg>
          </button>
          <button class="icon-button notification-button">
            <svg xmlns="http://www.w3.org/2000/svg" width="22" height="26" viewBox="0 0 22 26" fill="currentColor">
              <path d="M21.3396 18.5758C20.7156 17.407 19.788 14.0999 19.788 9.78058C19.788 7.18661 18.8403 4.69888 17.1534 2.86467C15.4665 1.03045 13.1785 0 10.7929 0C8.4072 0 6.11925 1.03045 4.43233 2.86467C2.74542 4.69888 1.79772 7.18661 1.79772 9.78058C1.79772 14.1012 0.868975 17.407 0.244938 18.5758C0.085579 18.8729 0.00109661 19.2105 1.06041e-05 19.5544C-0.0010754 19.8984 0.0812736 20.2366 0.238753 20.5349C0.396232 20.8333 0.623273 21.0811 0.896979 21.2536C1.17068 21.4261 1.48138 21.517 1.79772 21.5173H6.38636C6.59389 22.6215 7.1458 23.6138 7.94874 24.3265C8.75168 25.0392 9.75636 25.4284 10.7929 25.4284C11.8293 25.4284 12.834 25.0392 13.637 24.3265C14.4399 23.6138 14.9918 22.6215 15.1993 21.5173H19.788C20.1042 21.5168 20.4148 21.4257 20.6883 21.2532C20.9619 21.0806 21.1888 20.8327 21.3461 20.5344C21.5035 20.2361 21.5857 19.898 21.5846 19.5541C21.5834 19.2103 21.499 18.8728 21.3396 18.5758ZM10.7929 23.4734C10.235 23.4732 9.69082 23.285 9.23534 22.9347C8.77987 22.5844 8.43545 22.0892 8.24948 21.5173H13.3362C13.1503 22.0892 12.8058 22.5844 12.3504 22.9347C11.8949 23.285 11.3508 23.4732 10.7929 23.4734ZM1.79772 19.5612C2.6635 17.9425 3.59675 14.1916 3.59675 9.78058C3.59675 7.7054 4.35491 5.71522 5.70444 4.24785C7.05397 2.78048 8.88433 1.95612 10.7929 1.95612C12.7014 1.95612 14.5317 2.78048 15.8813 4.24785C17.2308 5.71522 17.989 7.7054 17.989 9.78058C17.989 14.188 18.92 17.9388 19.788 19.5612H1.79772Z" fill="currentColor"></path>
            </svg>
          </button>
        </div>
        <div class="topbar-profile">
          <div class="topbar-profile-info">
            <span><%= user.email() %></span>
          </div>
        </div>
      </div>
    </header>

    <!-- NOTE CARDS SECTION -->
    <section class="note-grid">
      <div class="card-note">
        <div class="pending-note">
          <h1>Pendente</h1>
          <p><span><%= pending %></span> Matéria(s)</p>
        </div>

        <div class="reproved-note">
          <h1>Reprovado</h1>
          <p><span><%= reproved %></span> Matéria(s)</p>
        </div>

        <div class="approved-note">
          <h1>Aprovado</h1>
          <p><span><%= approved %></span> Matéria(s)</p>
        </div>
      </div>
    </section>

    <!-- GRADES TABLE SECTION -->
    <section>
      <div class="boletim-grid">
        <% if (studentSubjectMap == null || studentSubjectMap.isEmpty()) { %>
        <p>Nenhuma disciplina encontrada.</p>
        <% } else { %>
        <table class="grade-table">
          <thead>
          <tr>
            <th>Matéria</th>
            <th>Nota 1</th>
            <th>Nota 2</th>
            <th>Média</th>
            <th>Situação</th>
          </tr>
          </thead>

          <tbody>
          <%
              for (StudentSubject ss : studentSubjectMap.values()) {
                  Double avg = ss.getAverage();
                  String statusClass = "pending";
                  String statusText = "Pendente";

                  if (ss.getGrade1() != null && ss.getGrade2() != null) {
                      if (avg >= 7) {
                          statusClass = "approved";
                          statusText = "Aprovado";
                      } else {
                          statusClass = "reproved";
                          statusText = "Reprovado";
                      }
                  }
          %>
          <tr>
            <td class="subject"><%= (ss.getSubject() != null && ss.getSubject().getName() != null)
                    ? OutputFormatService.formatName(ss.getSubject().getName())
                    : "—" %></td>

            <td><%= (ss.getGrade1() != null) ? ss.getGrade1() : "—" %></td>

            <td><%= (ss.getGrade2() != null) ? ss.getGrade2() : "—" %></td>

            <td class="bold">
              <%= avg != null ? String.format("%.2f", avg) : "-"%>
            </td>

            <td><span class="status <%= statusClass %>"><%= statusText %></span></td>
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
      </div>
    </section>
  </main>
</div>
</body>
</html>