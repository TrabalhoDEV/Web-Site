<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.schoolservlet.utils.records.AuthenticatedUser" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.models.StudentSubject" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Aluno | Vértice</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/layout/tokens.css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/layout/topbar.css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/layout/navbar.css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/student/home.css"/>

    <link rel="shortcut icon" href="<%= request.getContextPath() %>/assets/img/Logo%20-%20Vértice.svg"
          type="image/x-icon">

    <script src="${pageContext.request.contextPath}/assets/js/theme.js"></script>
</head>
<body>

<div class="app-layout">
    <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
    %>
    <script>alert(<%= error %>)</script>
    <%
        }
    %>

    <%
        Map<Integer, StudentSubject> studentSubjectMap = (Map<Integer, StudentSubject>) request.getAttribute("studentSubjectMap");
        AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

        Integer currentPage = (Integer) request.getAttribute("currentPage");
        if (currentPage == null) currentPage = 0;
        Integer totalPages = (Integer) request.getAttribute("totalPages");
        if (totalPages == null) totalPages = 1;
    %>
    <!-- SIDEBAR -->
    <aside class="sidebar">
        <div class="sidebar-logo">
            <svg class="l" xmlns="http://www.w3.org/2000/svg" width="101" height="51" viewBox="0 0 101 51" fill="none">
                <mask id="mask0_1031_100" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="22" y="16" width="23"
                      height="35">
                    <path d="M44.8174 16.743H42.2769C37.4619 16.743 33.1938 19.5943 31.7032 23.8067L22.4088 50.0716H28.13C31.0482 50.0716 33.6349 48.3436 34.5383 45.7906L44.8174 16.743Z"
                          fill="#D9D9D9"/>
                </mask>
                <g mask="url(#mask0_1031_100)">
                    <path d="M43.9043 16.9796H41.3638C36.5488 16.9796 32.2808 19.8308 30.7901 24.0433L21.4957 50.3082H27.2169C30.1351 50.3082 32.7218 48.5802 33.6252 46.0272L43.9043 16.9796Z"
                          fill="url(#paint0_linear_1031_100)"/>
                </g>
                <mask id="mask1_1031_100" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="5" y="16" width="24"
                      height="35">
                    <path d="M5.72852 16.743C12.1432 17.1178 17.6682 21.0381 19.6921 26.651L28.1371 50.0716H22.4159C19.4977 50.0716 16.911 48.3436 16.0076 45.7906L5.72852 16.743Z"
                          fill="#D9D9D9"/>
                </mask>
                <g mask="url(#mask1_1031_100)">
                    <mask id="mask2_1031_100" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="-1" y="-1"
                          width="22" height="35">
                        <path d="M-0.260742 -0.000549316C6.16642 0.374903 11.7172 4.27047 13.8099 9.87421L20.658 28.2125L11.5332 33.328L-0.260742 -0.000549316Z"
                              fill="url(#paint1_linear_1031_100)"/>
                    </mask>
                    <g mask="url(#mask2_1031_100)">
                        <path d="M-0.260742 -0.000549316C6.16642 0.374903 11.7172 4.27047 13.8099 9.87421L20.658 28.2125L11.5332 33.328L-0.260742 -0.000549316Z"
                              fill="url(#paint2_linear_1031_100)"/>
                    </g>
                    <path d="M-0.395996 0.0770874C6.03116 0.45254 11.5819 4.34816 13.6746 9.9519L20.5228 28.2901L17.6526 29.8991L19.4103 28.8659C19.4068 28.8521 19.0213 27.3518 18.6832 26.4178C18.3554 25.5123 17.7137 24.1489 17.7137 24.1489C16.0172 20.1483 10.6225 17.7536 6.90125 17.5207C6.90125 17.5207 8.95942 21.9267 10.0285 24.8338C11.0665 27.6568 12.2586 32.023 12.4697 32.8045L11.3979 33.4056L-0.395996 0.0770874Z"
                          fill="url(#paint3_linear_1031_100)"/>
                    <path d="M11.9624 35.0347L22.577 29.2991L28.2059 50.1312H23.0589L20.7134 50.0113L18.6937 49.232L17.3907 48.213L16.4786 46.9542L11.9624 35.0347Z"
                          fill="url(#paint4_linear_1031_100)"/>
                </g>
                <mask id="mask3_1031_100" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="21" y="0" width="75"
                      height="15">
                    <path d="M95.3682 14.2795C95.3682 14.6376 78.3291 0.123779 57.8862 0.123779C37.4433 0.123779 21.3379 14.6376 21.3379 14.2795C24.699 14.2795 37.4433 4.31548 57.8862 4.31548C78.3291 4.31548 92.5734 14.3751 95.3682 14.2795Z"
                          fill="url(#paint5_linear_1031_100)"/>
                </mask>
                <g mask="url(#mask3_1031_100)">
                    <path d="M99.7736 16.2322C99.7736 16.6068 80.7947 1.42566 58.0245 1.42566C35.2543 1.42566 17.3154 16.6068 17.3154 16.2322C21.0591 16.2322 35.2543 5.81008 58.0245 5.81008C80.7947 5.81008 96.6605 16.3322 99.7736 16.2322Z"
                          fill="url(#paint6_linear_1031_100)"/>
                    <path d="M95.3682 13.2226C95.3682 13.6074 78.3291 -1.99078 57.8862 -1.99078C37.4433 -1.99078 21.3379 13.6074 21.3379 13.2226C24.699 13.2226 37.4433 2.51409 57.8862 2.51409C78.3291 2.51409 92.5734 13.3253 95.3682 13.2226Z"
                          fill="url(#paint7_linear_1031_100)"/>
                </g>
                <path d="M100.301 45.1184H90.4896C90.7032 46.204 91.1864 46.999 91.9389 47.5034C92.7008 47.9992 93.5277 48.2471 94.4196 48.2471C95.7947 48.2471 96.9468 47.7214 97.8759 46.6699L99.6458 47.9009C98.3172 49.4823 96.538 50.273 94.3081 50.273C92.5986 50.273 91.1678 49.7345 90.0157 48.6574C88.8636 47.5803 88.2876 46.2425 88.2876 44.644C88.2876 43.1481 88.8683 41.853 90.0297 40.7588C91.2003 39.6561 92.6265 39.1047 94.3081 39.1047C95.9619 39.1047 97.3788 39.6646 98.5587 40.7845C99.7387 41.8957 100.319 43.3404 100.301 45.1184ZM90.7404 43.3361H97.7644C96.9839 41.8829 95.8319 41.1563 94.3081 41.1563C92.6451 41.1563 91.4558 41.8829 90.7404 43.3361Z"
                      fill="var(--text-color)"/>
                <path d="M86.4618 46.7469V49.2985C85.2261 49.9482 84.0275 50.273 82.8662 50.273C81.2774 50.273 79.8838 49.7259 78.6852 48.6317C77.496 47.529 76.9014 46.2553 76.9014 44.8107C76.9014 43.1694 77.496 41.8103 78.6852 40.7332C79.8745 39.6475 81.3657 39.1047 83.1588 39.1047C84.2273 39.1047 85.3283 39.3612 86.4618 39.8741V42.3616C85.2446 41.6436 84.0926 41.2845 83.0055 41.2845C82.0021 41.2845 81.1241 41.6094 80.3715 42.259C79.619 42.9087 79.2427 43.6695 79.2427 44.5414C79.2427 45.5245 79.5957 46.3536 80.3019 47.029C81.008 47.6957 81.872 48.0291 82.894 48.0291C83.9346 48.0291 85.1239 47.6017 86.4618 46.7469Z"
                      fill="var(--text-color)"/>
                <path d="M71.1182 35.926C71.1182 35.4643 71.304 35.0669 71.6756 34.7335C72.0473 34.3915 72.4886 34.2206 72.9996 34.2206C73.5013 34.2206 73.9333 34.3915 74.2957 34.7335C74.6673 35.0669 74.8531 35.4643 74.8531 35.926C74.8531 36.3961 74.6673 36.7979 74.2957 37.1313C73.9333 37.4646 73.5013 37.6313 72.9996 37.6313C72.4886 37.6313 72.0473 37.4646 71.6756 37.1313C71.304 36.7979 71.1182 36.3961 71.1182 35.926ZM71.801 39.4008H74.1981V49.9793H71.801V39.4008Z"
                      fill="var(--text-color)"/>
                <path d="M64.5958 36.321H67.0208V39.3984H69.4178V41.4372H67.0208V49.9769H64.5958V41.4372H62.6865V39.3728H64.5958V36.321Z"
                      fill="var(--text-color)"/>
                <path d="M56.4151 39.3997V41.5795C56.954 40.7075 57.4789 40.0792 57.9899 39.6946C58.5102 39.3013 59.1838 39.1047 60.0107 39.1047C60.243 39.1047 60.5774 39.156 61.0141 39.2586L60.2755 41.4897C59.8109 41.4042 59.5043 41.3615 59.3557 41.3615C58.5381 41.3615 57.8505 41.6265 57.2931 42.1565C56.7449 42.6779 56.4708 43.3318 56.4708 44.1183V49.9781H54.0459V39.3997H56.4151Z"
                      fill="var(--text-color)"/>
                <path d="M50.9385 45.1195H41.1272C41.3409 46.2051 41.8241 47.0001 42.5766 47.5044C43.3385 48.0002 44.1654 48.2481 45.0573 48.2481C46.4324 48.2481 47.5845 47.7224 48.5136 46.671L50.2835 47.9019C48.9549 49.4833 47.1757 50.2741 44.9458 50.2741C43.2363 50.2741 41.8055 49.7355 40.6534 48.6584C39.5013 47.5814 38.9253 46.2436 38.9253 44.645C38.9253 43.1491 39.506 41.854 40.6673 40.7599C41.838 39.6571 43.2642 39.1058 44.9458 39.1058C46.5996 39.1058 48.0165 39.6657 49.1964 40.7855C50.3764 41.8968 50.9571 43.3414 50.9385 45.1195ZM41.3781 43.3372H48.4021C47.6216 41.884 46.4695 41.1574 44.9458 41.1574C43.2828 41.1574 42.0935 41.884 41.3781 43.3372ZM43.3431 37.4517L45.1967 33.8871H48.4857L45.4057 37.4517H43.3431Z"
                      fill="var(--text-color)"/>
                <defs>
                    <linearGradient id="paint0_linear_1031_100" x1="49.2504" y1="5.53041" x2="18.9897" y2="61.3871"
                                    gradientUnits="userSpaceOnUse">
                        <stop stop-color="white"/>
                        <stop offset="0.474208" stop-color="#7C3AED"/>
                        <stop offset="0.688674" stop-color="#2C18A3"/>
                    </linearGradient>
                    <linearGradient id="paint1_linear_1031_100" x1="10.1986" y1="-0.000549608" x2="-5.99272"
                                    y2="31.0281" gradientUnits="userSpaceOnUse">
                        <stop stop-color="#4361EE"/>
                        <stop offset="1" stop-color="#7C3AED"/>
                    </linearGradient>
                    <linearGradient id="paint2_linear_1031_100" x1="10.1986" y1="-0.000549608" x2="-5.99272"
                                    y2="31.0281" gradientUnits="userSpaceOnUse">
                        <stop offset="1" stop-color="#7C3AED"/>
                    </linearGradient>
                    <linearGradient id="paint3_linear_1031_100" x1="-24.1112" y1="30.3485" x2="5.30444" y2="10.6473"
                                    gradientUnits="userSpaceOnUse">
                        <stop offset="0.422047" stop-color="white"/>
                        <stop offset="1" stop-color="white" stop-opacity="0"/>
                    </linearGradient>
                    <linearGradient id="paint4_linear_1031_100" x1="12.9604" y1="31.9683" x2="22.8868" y2="54.964"
                                    gradientUnits="userSpaceOnUse">
                        <stop stop-color="#7F96FD"/>
                        <stop offset="1" stop-color="#4361EE"/>
                    </linearGradient>
                    <linearGradient id="paint5_linear_1031_100" x1="21.3379" y1="7.2049" x2="95.3682" y2="7.2049"
                                    gradientUnits="userSpaceOnUse">
                        <stop offset="0.0384615" stop-color="#FF5B5B"/>
                        <stop offset="0.625" stop-color="#FF0080"/>
                        <stop offset="0.75" stop-color="#FF1777"/>
                        <stop offset="1" stop-color="#FF0080"/>
                    </linearGradient>
                    <linearGradient id="paint6_linear_1031_100" x1="10.8084" y1="16.239" x2="99.231" y2="4.68151"
                                    gradientUnits="userSpaceOnUse">
                        <stop offset="0.125" stop-color="#F980BF"/>
                        <stop offset="0.274038" stop-color="#E962DA"/>
                        <stop offset="0.456731" stop-color="#7C3AED"/>
                        <stop offset="1" stop-color="#43AAEE"/>
                    </linearGradient>
                    <linearGradient id="paint7_linear_1031_100" x1="20.2365" y1="13.2296" x2="95.0729" y2="3.4025"
                                    gradientUnits="userSpaceOnUse">
                        <stop stop-color="#FFBCAA"/>
                        <stop offset="0.168269" stop-color="#FF93DB"/>
                        <stop offset="0.341346" stop-color="#AA5DED"/>
                        <stop offset="0.788462" stop-color="#7CBFFA"/>
                    </linearGradient>
                </defs>
            </svg>
        </div>


        <nav class="sidebar-nav">
            <ul>
                <li class="sidebar-item active">
                    <a href="<%= request.getContextPath() %>/student/home">
                        <span class="icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="21" height="22" viewBox="0 0 21 22"
                                 fill="none">
                                <path d="M20.487 9.26216L11.737 0.512158C11.4089 0.184217 10.9639 0 10.5 0C10.0361 0 9.59113 0.184217 9.26298 0.512158L0.51298 9.26216C0.349688 9.42424 0.220247 9.61716 0.132184 9.82972C0.0441203 10.0423 -0.000807906 10.2702 1.0996e-05 10.5003V21.0003C1.0996e-05 21.2323 0.0921984 21.4549 0.256293 21.619C0.420387 21.7831 0.642946 21.8753 0.875011 21.8753H20.125C20.3571 21.8753 20.5796 21.7831 20.7437 21.619C20.9078 21.4549 21 21.2323 21 21.0003V10.5003C21.0008 10.2702 20.9559 10.0423 20.8678 9.82972C20.7798 9.61716 20.6503 9.42424 20.487 9.26216ZM19.25 20.1253H1.75001V10.5003L10.5 1.75028L19.25 10.5003V20.1253Z"
                                      fill="currentColor"></path>
                                <rect x="9" y="15" width="3" height="3" rx="1" fill="currentColor"></rect>
                            </svg>
                        </span>
                        <span>Início</span>
                    </a>
                </li>

                <li class="sidebar-item non-active">
                    <a href="<%= request.getContextPath() %>/student/bulletin">
                        <span class="icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                             stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                             class="lucide lucide-captions-icon lucide-captions"><rect width="18" height="14" x="3"
                                                                                       y="5" rx="2" ry="2"/><path
                                d="M7 15h4M15 15h2M7 11h2M13 11h4"/></svg>
                        </span>
                        <span>Boletim</span>
                    </a>
                </li>
            </ul>
        </nav>

        <div class="sidebar-footer">
            <a class="logout-button" href="${pageContext.request.contextPath}/logout">
                <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 28 28" fill="none">
                    <path d="M18.6665 19.8334L24.4998 14L18.6665 8.16669" stroke="currentColor" stroke-width="2"
                          stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M24.5 14H10.5" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                          stroke-linejoin="round"/>
                    <path d="M10.5 24.5H5.83333C5.21449 24.5 4.621 24.2542 4.18342 23.8166C3.74583 23.379 3.5 22.7855 3.5 22.1667V5.83333C3.5 5.21449 3.74583 4.621 4.18342 4.18342C4.621 3.74583 5.21449 3.5 5.83333 3.5H10.5"
                          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                Sair</a>
        </div>
    </aside>

    <!-- MAIN CONTENT -->
    <main class="main-content">
        <!-- TOPBAR -->
        <header class="topbar">
            <div class="topbar-right">
                <div class="topbar-actions">
                    <button type="button" class="icon-button" id="theme-button">
                        <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" viewBox="0 0 25 25"
                             fill="currentColor">
                            <path d="M24.7145 14.9754C24.5888 14.8493 24.431 14.7599 24.2583 14.7167C24.0855 14.6735 23.9043 14.6782 23.734 14.7302C21.8643 15.2956 19.8763 15.343 17.9817 14.8674C16.0872 14.3918 14.3572 13.411 12.976 12.0295C11.5949 10.6481 10.6143 8.9177 10.1388 7.0228C9.66328 5.12789 9.71068 3.13946 10.2759 1.26937C10.3284 1.09897 10.3334 0.917484 10.2904 0.744438C10.2475 0.571393 10.1582 0.413333 10.0321 0.287256C9.90609 0.16118 9.74806 0.0718578 9.57505 0.0288961C9.40204 -0.0140655 9.22059 -0.00904108 9.05022 0.0434289C6.46528 0.835443 4.19593 2.42273 2.5651 4.57942C1.13891 6.47332 0.268965 8.72748 0.0530111 11.0886C-0.162943 13.4498 0.283649 15.8244 1.34261 17.9457C2.40158 20.067 4.03096 21.8509 6.04769 23.0971C8.06443 24.3432 10.3886 25.0022 12.7592 24.9999C15.5247 25.0085 18.2167 24.1092 20.4222 22.4402C22.5784 20.809 24.1654 18.5392 24.9572 15.9537C25.0091 15.7839 25.0139 15.6033 24.9711 15.431C24.9284 15.2587 24.8397 15.1012 24.7145 14.9754ZM19.2431 20.8734C17.1662 22.438 14.5941 23.1996 12.0004 23.018C9.40666 22.8363 6.96582 21.7237 5.12721 19.8848C3.2886 18.046 2.17597 15.6047 1.99414 13.0105C1.81232 10.4162 2.57353 7.84356 4.13766 5.76613C5.15671 4.42012 6.47415 3.32901 7.98633 2.57868C7.90018 3.18334 7.85677 3.79333 7.8564 4.40411C7.85997 7.78447 9.20412 11.0254 11.5939 13.4156C13.9837 15.8059 17.2239 17.1503 20.6036 17.1539C21.2154 17.1537 21.8265 17.1103 22.4323 17.024C21.6814 18.5367 20.5897 19.8544 19.2431 20.8734Z"
                                  fill="currentColor"></path>
                        </svg>
                    </button>
                    <button class="icon-button notification-button">
                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="26" viewBox="0 0 22 26"
                             fill="currentColor">
                            <path d="M21.3396 18.5758C20.7156 17.407 19.788 14.0999 19.788 9.78058C19.788 7.18661 18.8403 4.69888 17.1534 2.86467C15.4665 1.03045 13.1785 0 10.7929 0C8.4072 0 6.11925 1.03045 4.43233 2.86467C2.74542 4.69888 1.79772 7.18661 1.79772 9.78058C1.79772 14.1012 0.868975 17.407 0.244938 18.5758C0.085579 18.8729 0.00109661 19.2105 1.06041e-05 19.5544C-0.0010754 19.8984 0.0812736 20.2366 0.238753 20.5349C0.396232 20.8333 0.623273 21.0811 0.896979 21.2536C1.17068 21.4261 1.48138 21.517 1.79772 21.5173H6.38636C6.59389 22.6215 7.1458 23.6138 7.94874 24.3265C8.75168 25.0392 9.75636 25.4284 10.7929 25.4284C11.8293 25.4284 12.834 25.0392 13.637 24.3265C14.4399 23.6138 14.9918 22.6215 15.1993 21.5173H19.788C20.1042 21.5168 20.4148 21.4257 20.6883 21.2532C20.9619 21.0806 21.1888 20.8327 21.3461 20.5344C21.5035 20.2361 21.5857 19.898 21.5846 19.5541C21.5834 19.2103 21.499 18.8728 21.3396 18.5758ZM10.7929 23.4734C10.235 23.4732 9.69082 23.285 9.23534 22.9347C8.77987 22.5844 8.43545 22.0892 8.24948 21.5173H13.3362C13.1503 22.0892 12.8058 22.5844 12.3504 22.9347C11.8949 23.285 11.3508 23.4732 10.7929 23.4734ZM1.79772 19.5612C2.6635 17.9425 3.59675 14.1916 3.59675 9.78058C3.59675 7.7054 4.35491 5.71522 5.70444 4.24785C7.05397 2.78048 8.88433 1.95612 10.7929 1.95612C12.7014 1.95612 14.5317 2.78048 15.8813 4.24785C17.2308 5.71522 17.989 7.7054 17.989 9.78058C17.989 14.188 18.92 17.9388 19.788 19.5612H1.79772Z"
                                  fill="currentColor"></path>
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

        <!-- HERO -->
        <section class="hero">
            <div class="hero-content">
                <h1>Olá! </h1>
                <p>Veja os feedbacks dos seus professores.</p>
            </div>
            <div class="hero-illustration">
                <img src="<%= request.getContextPath() %>/assets/img/gato.svg" alt="Ilustração gato"/>
            </div>
        </section>

        <section class="obs">
            <section class="observations-grid">
                <%
                    if (studentSubjectMap != null && !studentSubjectMap.isEmpty()) {
                        for (StudentSubject studentSubject : studentSubjectMap.values()) {
                %>
                <div class="card-tasks">
                    <p class="materia"><%= OutputFormatService.formatName(studentSubject.getSubject().getName())%>
                    </p>
                    <p class="descricao"><%= studentSubject.getObs() %>
                    </p>
                </div>
                <%
                    }
                } else {
                %>
                <div class="card-tasks">
                    <p>Nenhuma observação encontrada.</p>
                </div>
                <%
                    }
                %>
            </section>

            <!-- PAGINAÇÃO -->
            <div class="pagination">

                <% if (currentPage > 1) { %>
                <a href="?page=<%=currentPage-1%>">Anterior</a>
                <% } %>


                <strong><%= currentPage != 0 ? currentPage : 1 %>/<%= totalPages != 0 ? totalPages : 1%>
                </strong>

                <% if (currentPage < totalPages) { %>
                <a href="?page=<%=currentPage+1%>">Próxima</a>
                <% } %>
            </div>
        </section>
    </main>
</div>

<script>
    const dialog = document.querySelector("#modalDialog");
    const openButtons = document.querySelectorAll(".openButton");
    const closeButton = document.querySelector("#closeButton");

    document.addEventListener("DOMContentLoaded", () => {
        openButtons.forEach(button => {
            button.addEventListener("click", () => {
                dialog.showModal();
            });
        });

        closeButton.addEventListener("click", () => {
            dialog.close();
        });
    });

    dialog.addEventListener("click", (e) => {
        if (e.target === dialog) {
            dialog.close();
        }
    });
</script>

</body>
</html>
