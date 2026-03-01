<%@ page import="com.example.schoolservlet.models.Teacher" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.example.schoolservlet.utils.OutputFormatService" %>
<%@ page import="java.util.TreeMap" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Map<Integer, Teacher> teacherMap = (Map<Integer, Teacher>) request.getAttribute("teacherMap");
    teacherMap = new TreeMap<>(teacherMap);
    int totalPages = (Integer) request.getAttribute("totalPages");
    int currentPage = (Integer) request.getAttribute("page");
%>
<%
    String error = (String) session.getAttribute("error");
    if (error != null) {
%>
<p style="color:red;"><%= error %></p>
<%
        session.removeAttribute("error");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Listar professores</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/list.css" />
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/css/img/Logo - Vértice.svg" type="image/x-icon">
</head>
<body>

<div class="app-layout">

    <!-- SIDEBAR -->

    <aside class="sidebar">

        <div class="sidebar-logo">
            <svg class="l" xmlns="http://www.w3.org/2000/svg" width="101" height="51" viewBox="0 0 101 51" fill="none">
                <mask id="mask0_1031_100" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="22" y="16" width="23" height="35">
                    <path d="M44.8174 16.743H42.2769C37.4619 16.743 33.1938 19.5943 31.7032 23.8067L22.4088 50.0716H28.13C31.0482 50.0716 33.6349 48.3436 34.5383 45.7906L44.8174 16.743Z" fill="#D9D9D9"/>
                </mask>
                <g mask="url(#mask0_1031_100)">
                    <path d="M43.9043 16.9796H41.3638C36.5488 16.9796 32.2808 19.8308 30.7901 24.0433L21.4957 50.3082H27.2169C30.1351 50.3082 32.7218 48.5802 33.6252 46.0272L43.9043 16.9796Z" fill="url(#paint0_linear_1031_100)"/>
                </g>
                <mask id="mask1_1031_100" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="5" y="16" width="24" height="35">
                    <path d="M5.72852 16.743C12.1432 17.1178 17.6682 21.0381 19.6921 26.651L28.1371 50.0716H22.4159C19.4977 50.0716 16.911 48.3436 16.0076 45.7906L5.72852 16.743Z" fill="#D9D9D9"/>
                </mask>
                <g mask="url(#mask1_1031_100)">
                    <mask id="mask2_1031_100" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="-1" y="-1" width="22" height="35">
                        <path d="M-0.260742 -0.000549316C6.16642 0.374903 11.7172 4.27047 13.8099 9.87421L20.658 28.2125L11.5332 33.328L-0.260742 -0.000549316Z" fill="url(#paint1_linear_1031_100)"/>
                    </mask>
                    <g mask="url(#mask2_1031_100)">
                        <path d="M-0.260742 -0.000549316C6.16642 0.374903 11.7172 4.27047 13.8099 9.87421L20.658 28.2125L11.5332 33.328L-0.260742 -0.000549316Z" fill="url(#paint2_linear_1031_100)"/>
                    </g>
                    <path d="M-0.395996 0.0770874C6.03116 0.45254 11.5819 4.34816 13.6746 9.9519L20.5228 28.2901L17.6526 29.8991L19.4103 28.8659C19.4068 28.8521 19.0213 27.3518 18.6832 26.4178C18.3554 25.5123 17.7137 24.1489 17.7137 24.1489C16.0172 20.1483 10.6225 17.7536 6.90125 17.5207C6.90125 17.5207 8.95942 21.9267 10.0285 24.8338C11.0665 27.6568 12.2586 32.023 12.4697 32.8045L11.3979 33.4056L-0.395996 0.0770874Z" fill="url(#paint3_linear_1031_100)"/>
                    <path d="M11.9624 35.0347L22.577 29.2991L28.2059 50.1312H23.0589L20.7134 50.0113L18.6937 49.232L17.3907 48.213L16.4786 46.9542L11.9624 35.0347Z" fill="url(#paint4_linear_1031_100)"/>
                </g>
                <mask id="mask3_1031_100" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="21" y="0" width="75" height="15">
                    <path d="M95.3682 14.2795C95.3682 14.6376 78.3291 0.123779 57.8862 0.123779C37.4433 0.123779 21.3379 14.6376 21.3379 14.2795C24.699 14.2795 37.4433 4.31548 57.8862 4.31548C78.3291 4.31548 92.5734 14.3751 95.3682 14.2795Z" fill="url(#paint5_linear_1031_100)"/>
                </mask>
                <g mask="url(#mask3_1031_100)">
                    <path d="M99.7736 16.2322C99.7736 16.6068 80.7947 1.42566 58.0245 1.42566C35.2543 1.42566 17.3154 16.6068 17.3154 16.2322C21.0591 16.2322 35.2543 5.81008 58.0245 5.81008C80.7947 5.81008 96.6605 16.3322 99.7736 16.2322Z" fill="url(#paint6_linear_1031_100)"/>
                    <path d="M95.3682 13.2226C95.3682 13.6074 78.3291 -1.99078 57.8862 -1.99078C37.4433 -1.99078 21.3379 13.6074 21.3379 13.2226C24.699 13.2226 37.4433 2.51409 57.8862 2.51409C78.3291 2.51409 92.5734 13.3253 95.3682 13.2226Z" fill="url(#paint7_linear_1031_100)"/>
                </g>
                <path d="M100.301 45.1184H90.4896C90.7032 46.204 91.1864 46.999 91.9389 47.5034C92.7008 47.9992 93.5277 48.2471 94.4196 48.2471C95.7947 48.2471 96.9468 47.7214 97.8759 46.6699L99.6458 47.9009C98.3172 49.4823 96.538 50.273 94.3081 50.273C92.5986 50.273 91.1678 49.7345 90.0157 48.6574C88.8636 47.5803 88.2876 46.2425 88.2876 44.644C88.2876 43.1481 88.8683 41.853 90.0297 40.7588C91.2003 39.6561 92.6265 39.1047 94.3081 39.1047C95.9619 39.1047 97.3788 39.6646 98.5587 40.7845C99.7387 41.8957 100.319 43.3404 100.301 45.1184ZM90.7404 43.3361H97.7644C96.9839 41.8829 95.8319 41.1563 94.3081 41.1563C92.6451 41.1563 91.4558 41.8829 90.7404 43.3361Z" fill="var(--text-color)"/>
                <path d="M86.4618 46.7469V49.2985C85.2261 49.9482 84.0275 50.273 82.8662 50.273C81.2774 50.273 79.8838 49.7259 78.6852 48.6317C77.496 47.529 76.9014 46.2553 76.9014 44.8107C76.9014 43.1694 77.496 41.8103 78.6852 40.7332C79.8745 39.6475 81.3657 39.1047 83.1588 39.1047C84.2273 39.1047 85.3283 39.3612 86.4618 39.8741V42.3616C85.2446 41.6436 84.0926 41.2845 83.0055 41.2845C82.0021 41.2845 81.1241 41.6094 80.3715 42.259C79.619 42.9087 79.2427 43.6695 79.2427 44.5414C79.2427 45.5245 79.5957 46.3536 80.3019 47.029C81.008 47.6957 81.872 48.0291 82.894 48.0291C83.9346 48.0291 85.1239 47.6017 86.4618 46.7469Z" fill="var(--text-color)"/>
                <path d="M71.1182 35.926C71.1182 35.4643 71.304 35.0669 71.6756 34.7335C72.0473 34.3915 72.4886 34.2206 72.9996 34.2206C73.5013 34.2206 73.9333 34.3915 74.2957 34.7335C74.6673 35.0669 74.8531 35.4643 74.8531 35.926C74.8531 36.3961 74.6673 36.7979 74.2957 37.1313C73.9333 37.4646 73.5013 37.6313 72.9996 37.6313C72.4886 37.6313 72.0473 37.4646 71.6756 37.1313C71.304 36.7979 71.1182 36.3961 71.1182 35.926ZM71.801 39.4008H74.1981V49.9793H71.801V39.4008Z" fill="var(--text-color)"/>
                <path d="M64.5958 36.321H67.0208V39.3984H69.4178V41.4372H67.0208V49.9769H64.5958V41.4372H62.6865V39.3728H64.5958V36.321Z" fill="var(--text-color)"/>
                <path d="M56.4151 39.3997V41.5795C56.954 40.7075 57.4789 40.0792 57.9899 39.6946C58.5102 39.3013 59.1838 39.1047 60.0107 39.1047C60.243 39.1047 60.5774 39.156 61.0141 39.2586L60.2755 41.4897C59.8109 41.4042 59.5043 41.3615 59.3557 41.3615C58.5381 41.3615 57.8505 41.6265 57.2931 42.1565C56.7449 42.6779 56.4708 43.3318 56.4708 44.1183V49.9781H54.0459V39.3997H56.4151Z" fill="var(--text-color)"/>
                <path d="M50.9385 45.1195H41.1272C41.3409 46.2051 41.8241 47.0001 42.5766 47.5044C43.3385 48.0002 44.1654 48.2481 45.0573 48.2481C46.4324 48.2481 47.5845 47.7224 48.5136 46.671L50.2835 47.9019C48.9549 49.4833 47.1757 50.2741 44.9458 50.2741C43.2363 50.2741 41.8055 49.7355 40.6534 48.6584C39.5013 47.5814 38.9253 46.2436 38.9253 44.645C38.9253 43.1491 39.506 41.854 40.6673 40.7599C41.838 39.6571 43.2642 39.1058 44.9458 39.1058C46.5996 39.1058 48.0165 39.6657 49.1964 40.7855C50.3764 41.8968 50.9571 43.3414 50.9385 45.1195ZM41.3781 43.3372H48.4021C47.6216 41.884 46.4695 41.1574 44.9458 41.1574C43.2828 41.1574 42.0935 41.884 41.3781 43.3372ZM43.3431 37.4517L45.1967 33.8871H48.4857L45.4057 37.4517H43.3431Z" fill="var(--text-color)"/>
                <defs>
                    <linearGradient id="paint0_linear_1031_100" x1="49.2504" y1="5.53041" x2="18.9897" y2="61.3871" gradientUnits="userSpaceOnUse">
                        <stop stop-color="white"/>
                        <stop offset="0.474208" stop-color="#7C3AED"/>
                        <stop offset="0.688674" stop-color="#2C18A3"/>
                    </linearGradient>
                    <linearGradient id="paint1_linear_1031_100" x1="10.1986" y1="-0.000549608" x2="-5.99272" y2="31.0281" gradientUnits="userSpaceOnUse">
                        <stop stop-color="#4361EE"/>
                        <stop offset="1" stop-color="#7C3AED"/>
                    </linearGradient>
                    <linearGradient id="paint2_linear_1031_100" x1="10.1986" y1="-0.000549608" x2="-5.99272" y2="31.0281" gradientUnits="userSpaceOnUse">
                        <stop offset="1" stop-color="#7C3AED"/>
                    </linearGradient>
                    <linearGradient id="paint3_linear_1031_100" x1="-24.1112" y1="30.3485" x2="5.30444" y2="10.6473" gradientUnits="userSpaceOnUse">
                        <stop offset="0.422047" stop-color="white"/>
                        <stop offset="1" stop-color="white" stop-opacity="0"/>
                    </linearGradient>
                    <linearGradient id="paint4_linear_1031_100" x1="12.9604" y1="31.9683" x2="22.8868" y2="54.964" gradientUnits="userSpaceOnUse">
                        <stop stop-color="#7F96FD"/>
                        <stop offset="1" stop-color="#4361EE"/>
                    </linearGradient>
                    <linearGradient id="paint5_linear_1031_100" x1="21.3379" y1="7.2049" x2="95.3682" y2="7.2049" gradientUnits="userSpaceOnUse">
                        <stop offset="0.0384615" stop-color="#FF5B5B"/>
                        <stop offset="0.625" stop-color="#FF0080"/>
                        <stop offset="0.75" stop-color="#FF1777"/>
                        <stop offset="1" stop-color="#FF0080"/>
                    </linearGradient>
                    <linearGradient id="paint6_linear_1031_100" x1="10.8084" y1="16.239" x2="99.231" y2="4.68151" gradientUnits="userSpaceOnUse">
                        <stop offset="0.125" stop-color="#F980BF"/>
                        <stop offset="0.274038" stop-color="#E962DA"/>
                        <stop offset="0.456731" stop-color="#7C3AED"/>
                        <stop offset="1" stop-color="#43AAEE"/>
                    </linearGradient>
                    <linearGradient id="paint7_linear_1031_100" x1="20.2365" y1="13.2296" x2="95.0729" y2="3.4025" gradientUnits="userSpaceOnUse">
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
                <li class="sidebar-item non-active">
                    <a href="${pageContext.request.contextPath}/admin/student/find-many">
              <span class="icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="21" height="22" viewBox="0 0 21 22" fill="none">
                  <path d="M20.487 9.26216L11.737 0.512158C11.4089 0.184217 10.9639 0 10.5 0C10.0361 0 9.59113 0.184217 9.26298 0.512158L0.51298 9.26216C0.349688 9.42424 0.220247 9.61716 0.132184 9.82972C0.0441203 10.0423 -0.000807906 10.2702 1.0996e-05 10.5003V21.0003C1.0996e-05 21.2323 0.0921984 21.4549 0.256293 21.619C0.420387 21.7831 0.642946 21.8753 0.875011 21.8753H20.125C20.3571 21.8753 20.5796 21.7831 20.7437 21.619C20.9078 21.4549 21 21.2323 21 21.0003V10.5003C21.0008 10.2702 20.9559 10.0423 20.8678 9.82972C20.7798 9.61716 20.6503 9.42424 20.487 9.26216ZM19.25 20.1253H1.75001V10.5003L10.5 1.75028L19.25 10.5003V20.1253Z" fill="currentColor"/>
                  <rect x="9" y="15" width="3" height="3" rx="1" fill="currentColor"/>
                  </svg>
              </span>
                        <span>Aluno</span>
                    </a>
                </li>

                <li class="sidebar-item active">
                    <a href="${pageContext.request.contextPath}/admin/teacher/find-many">
                  <span class="icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="28" height="18" viewBox="0 0 28 18" fill="none">
                      <path d="M12.5872 12.0252C13.7599 11.2444 14.6502 10.107 15.1265 8.78116C15.6028 7.45528 15.6398 6.01132 15.232 4.66281C14.8241 3.31429 13.9932 2.13282 12.8619 1.2931C11.7307 0.453382 10.3593 0 8.95043 0C7.5416 0 6.17016 0.453382 5.03893 1.2931C3.9077 2.13282 3.07673 3.31429 2.66891 4.66281C2.26108 6.01132 2.29805 7.45528 2.77435 8.78116C3.25065 10.107 4.14099 11.2444 5.31371 12.0252C3.19238 12.807 1.3807 14.2538 0.149026 16.1497C0.0842937 16.2459 0.0393302 16.354 0.0167497 16.4678C-0.00583087 16.5815 -0.00557806 16.6986 0.0174934 16.8122C0.0405649 16.9259 0.0859948 17.0338 0.151142 17.1297C0.21629 17.2257 0.299856 17.3077 0.396982 17.371C0.494109 17.4344 0.602859 17.4778 0.71691 17.4987C0.830962 17.5197 0.948041 17.5178 1.06134 17.4931C1.17464 17.4684 1.2819 17.4214 1.37689 17.3549C1.47188 17.2884 1.5527 17.2037 1.61465 17.1056C2.40914 15.8837 3.49629 14.8795 4.77738 14.1844C6.05847 13.4893 7.49291 13.1252 8.95043 13.1252C10.408 13.1252 11.8424 13.4893 13.1235 14.1844C14.4046 14.8795 15.4917 15.8837 16.2862 17.1056C16.4146 17.2964 16.6128 17.4289 16.8381 17.4747C17.0634 17.5204 17.2976 17.4757 17.4902 17.3501C17.6828 17.2245 17.8182 17.0281 17.8671 16.8035C17.9161 16.5789 17.8747 16.344 17.7518 16.1497C16.5202 14.2538 14.7085 12.807 12.5872 12.0252ZM4.13793 6.56516C4.13793 5.61334 4.42018 4.68289 4.94899 3.89148C5.47779 3.10007 6.2294 2.48324 7.10877 2.11899C7.98814 1.75475 8.95577 1.65944 9.88931 1.84513C10.8228 2.03083 11.6803 2.48917 12.3534 3.16221C13.0264 3.83525 13.4848 4.69276 13.6705 5.62629C13.8562 6.55982 13.7608 7.52746 13.3966 8.40683C13.0324 9.2862 12.4155 10.0378 11.6241 10.5666C10.8327 11.0954 9.90226 11.3777 8.95043 11.3777C7.67452 11.3762 6.45128 10.8687 5.54908 9.96652C4.64687 9.06431 4.13938 7.84107 4.13793 6.56516ZM27.122 17.3605C26.9276 17.4872 26.6909 17.5316 26.4638 17.4838C26.2368 17.436 26.038 17.3 25.9112 17.1056C25.1177 15.8829 24.0307 14.8784 22.7493 14.1835C21.468 13.4887 20.0331 13.1257 18.5754 13.1277C18.3434 13.1277 18.1208 13.0355 17.9567 12.8714C17.7926 12.7073 17.7004 12.4847 17.7004 12.2527C17.7004 12.0206 17.7926 11.798 17.9567 11.6339C18.1208 11.4698 18.3434 11.3777 18.5754 11.3777C19.2841 11.377 19.984 11.2198 20.6249 10.9173C21.2658 10.6148 21.832 10.1745 22.2831 9.62785C22.7341 9.08118 23.0588 8.44164 23.234 7.75492C23.4092 7.0682 23.4306 6.35126 23.2966 5.65532C23.1626 4.95939 22.8766 4.30164 22.4589 3.72906C22.0413 3.15649 21.5023 2.68323 20.8805 2.34311C20.2588 2.00298 19.5695 1.80437 18.8621 1.76148C18.1547 1.7186 17.4465 1.83248 16.7882 2.09501C16.6809 2.14141 16.5654 2.16582 16.4484 2.16681C16.3315 2.1678 16.2156 2.14534 16.1075 2.10076C15.9994 2.05618 15.9013 1.99039 15.819 1.90726C15.7368 1.82414 15.6721 1.72538 15.6286 1.61681C15.5852 1.50824 15.564 1.39206 15.5662 1.27515C15.5684 1.15824 15.5941 1.04296 15.6416 0.936126C15.6891 0.829291 15.7576 0.733068 15.843 0.653143C15.9283 0.573218 16.0288 0.511211 16.1386 0.470788C17.6452 -0.130063 19.3209 -0.151685 20.8425 0.410092C22.3641 0.971869 23.6237 2.07724 24.3784 3.51296C25.1331 4.94868 25.3294 6.61303 24.9294 8.18492C24.5293 9.7568 23.5613 11.1249 22.2122 12.0252C24.3335 12.807 26.1452 14.2538 27.3768 16.1497C27.5036 16.3441 27.5479 16.5808 27.5001 16.8079C27.4524 17.0349 27.3163 17.2337 27.122 17.3605Z" fill="currentColor"/>
                      </svg>
                  </span>
                        <span>Professor</span>
                    </a>
                </li>

                <li class="sidebar-item non-active">
                    <a href="${pageContext.request.contextPath}/admin/school-class/find-many">
                        <span class="icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-school-icon lucide-school"><path d="M14 21v-3a2 2 0 0 0-4 0v3"/><path d="M18 5v16"/><path d="m4 6 7.106-3.79a2 2 0 0 1 1.788 0L20 6"/><path d="m6 11-3.52 2.147a1 1 0 0 0-.48.854V19a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-5a1 1 0 0 0-.48-.853L18 11"/><path d="M6 5v16"/><circle cx="12" cy="9" r="2"/></svg>
                        </span>
                        <span>Turmas</span>
                    </a>
                </li>

                <li class="sidebar-item non-active">
                    <a href="${pageContext.request.contextPath}/admin/subject/find-many">
                        <span class="icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-captions-icon lucide-captions"><rect width="18" height="14" x="3" y="5" rx="2" ry="2"/><path d="M7 15h4M15 15h2M7 11h2M13 11h4"/></svg>
                        </span>
                        <span>Matérias</span>
                    </a>
                </li>

                <li class="sidebar-item non-active">
                    <a href="">
              <span class="icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="23" height="26" viewBox="0 0 23 26" fill="none">
                  <path d="M8.73192 3.47167C8.7962 2.7954 9.11031 2.16739 9.61287 1.71033C10.1154 1.25327 10.7703 1 11.4497 1C12.129 1 12.7839 1.25327 13.2865 1.71033C13.789 2.16739 14.1031 2.7954 14.1674 3.47167C14.2061 3.90853 14.3494 4.32966 14.5852 4.6994C14.8211 5.06914 15.1426 5.37661 15.5225 5.59579C15.9023 5.81497 16.3294 5.9394 16.7676 5.95856C17.2057 5.97771 17.642 5.89103 18.0396 5.70584C18.6569 5.42558 19.3564 5.38502 20.0019 5.59207C20.6475 5.79911 21.1929 6.23894 21.532 6.82596C21.8712 7.41297 21.9798 8.10517 21.8367 8.76784C21.6936 9.4305 21.3091 10.0162 20.7579 10.411C20.399 10.6628 20.1061 10.9974 19.9038 11.3864C19.7016 11.7754 19.596 12.2073 19.596 12.6458C19.596 13.0842 19.7016 13.5162 19.9038 13.9051C20.1061 14.2941 20.399 14.6287 20.7579 14.8805C21.3091 15.2753 21.6936 15.861 21.8367 16.5237C21.9798 17.1863 21.8712 17.8785 21.532 18.4655C21.1929 19.0526 20.6475 19.4924 20.0019 19.6994C19.3564 19.9065 18.6569 19.8659 18.0396 19.5857C17.642 19.4005 17.2057 19.3138 16.7676 19.3329C16.3294 19.3521 15.9023 19.4765 15.5225 19.6957C15.1426 19.9149 14.8211 20.2224 14.5852 20.5921C14.3494 20.9618 14.2061 21.383 14.1674 21.8198C14.1031 22.4961 13.789 23.1241 13.2865 23.5812C12.7839 24.0382 12.129 24.2915 11.4497 24.2915C10.7703 24.2915 10.1154 24.0382 9.61287 23.5812C9.11031 23.1241 8.7962 22.4961 8.73192 21.8198C8.69335 21.3828 8.55003 20.9615 8.3141 20.5917C8.07816 20.2218 7.75655 19.9142 7.37652 19.695C6.99649 19.4758 6.56923 19.3514 6.13093 19.3324C5.69262 19.3134 5.25619 19.4002 4.85859 19.5857C4.24129 19.8659 3.5418 19.9065 2.89625 19.6994C2.25071 19.4924 1.70529 19.0526 1.36615 18.4655C1.02701 17.8785 0.918419 17.1863 1.06151 16.5237C1.20459 15.861 1.58912 15.2753 2.14025 14.8805C2.49914 14.6287 2.7921 14.2941 2.99436 13.9051C3.19661 13.5162 3.3022 13.0842 3.3022 12.6458C3.3022 12.2073 3.19661 11.7754 2.99436 11.3864C2.7921 10.9974 2.49914 10.6628 2.14025 10.411C1.58989 10.016 1.20604 9.43053 1.0633 8.76831C0.920563 8.1061 1.02913 7.41447 1.3679 6.82783C1.70667 6.2412 2.25145 5.80148 2.89636 5.59412C3.54127 5.38676 4.24023 5.42658 4.85742 5.70584C5.25497 5.89103 5.69129 5.97771 6.12944 5.95856C6.56759 5.9394 6.99467 5.81497 7.37455 5.59579C7.75442 5.37661 8.07589 5.06914 8.31176 4.6994C8.54763 4.32966 8.69095 3.90853 8.72959 3.47167" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M11.4492 16.1464C13.3822 16.1464 14.9492 14.5794 14.9492 12.6464C14.9492 10.7134 13.3822 9.14636 11.4492 9.14636C9.51622 9.14636 7.94922 10.7134 7.94922 12.6464C7.94922 14.5794 9.51622 16.1464 11.4492 16.1464Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
              </span>
                        <span>Configurações</span>
                    </a>
                </li>
            </ul>
        </nav>

        <div class="sidebar-footer">
            <a class="logout-button" href="${pageContext.request.contextPath}/logout">
                <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 28 28" fill="none">
                    <path d="M18.6665 19.8334L24.4998 14L18.6665 8.16669" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M24.5 14H10.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M10.5 24.5H5.83333C5.21449 24.5 4.621 24.2542 4.18342 23.8166C3.74583 23.379 3.5 22.7855 3.5 22.1667V5.83333C3.5 5.21449 3.74583 4.621 4.18342 4.18342C4.621 3.74583 5.21449 3.5 5.83333 3.5H10.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                Sair</a>
        </div>
    </aside>


    <!-- MAIN CONTENT -->

    <main class="main-content">

        <section>
            <div class="boletim-grid">
                <div class="header-grid">
                    <h1>Lista de Professores</h1>
                    <a href="${pageContext.request.contextPath}/admin/teacher/insert"><button class="btn-add">Adicionar Professor</button></a>
                </div>

                <hr>
                <table class="grade-table" style="--cols: 4">
                    <thead>
                    <tr>
                        <th>Nome</th>
                        <th>Usuário</th>
                        <th>Email</th>
                        <th>Ações</th>
                    </tr>
                    </thead>

                    <tbody>
                    <% if (teacherMap != null && !teacherMap.isEmpty()) { %>
                    <% for (Teacher teacher : teacherMap.values()) { %>
                    <tr>
                        <td><%= OutputFormatService.formatName(teacher.getName()) %></td>
                        <td><%= teacher.getUsername() %></td>
                        <td><%= teacher.getEmail() %></td>
                        <td class="actions">
                            <div class="btn-action">
                                <a href="${pageContext.request.contextPath}/admin/teacher/details?id=<%=teacher.getId()%>"><button class="btn-details">Ver detalhes</button></a>
                                <a href="${pageContext.request.contextPath}/admin/teacher/update?id=<%=teacher.getId()%>"><button class="btn-edit">Modificar</button></a>
                                <a><button class="btn-delete">Deletar</button></a>
                            </div>
                        </td>
                    </tr>
                    <% } } else if (request.getAttribute("error") != null) { %>

                    <tr>
                    <td colspan="4" style="text-align: center"><%= request.getAttribute("error") %></td>
                    </tr>

                    <% } else { %>

                    <tr>
                    <td colspan="4" style="text-align: center">Nenhum professor foi encontrado</td>
                    </tr>

                    <% } %>

                    </tr>
                    </tbody>
                </table>
            </div>
        </section>
        <dialog id="deleteDialog">
            <div class="modal-cardD">
                <h3>Deseja deletar este professor?</h3>
                <p id="deleteText"></p>

                <div class="modal-actions">
                    <button id="closeDelete">Cancelar</button>
                    <button id="confirmDelete">Confirmar</button>
                </div>
            </div>
        </dialog>

        <div class="pagination">

            <% if (totalPages > 1) { %>

            <% if (currentPage > 1) { %>
            <a href="?page=<%=currentPage-1%>">Anterior</a>
            <a href="?page=<%= currentPage - 1 %>"><%= currentPage - 1 %></a>
            <% } %>


            <strong><%= currentPage %></strong>

            <% if (currentPage < totalPages) { %>
            <a href="?page=<%= currentPage + 1 %>"><%= currentPage + 1 %></a>
            <a href="?page=<%=currentPage+1%>">Próxima</a>
            <% } %>

            <% } %>

        </div>
    </main>

    <script>
        document.addEventListener("DOMContentLoaded", () => {

            const deleteDialog = document.getElementById("deleteDialog");

            // Abrir modal ao clicar em Delete
            document.querySelectorAll(".btn-delete").forEach(button => {
                button.addEventListener("click", () => {
                    deleteDialog.showModal();
                });
            });

            // Botão Cancelar
            document.getElementById("closeDelete").addEventListener("click", () => {
                window.location.href = window.location.href;
            });

            // Botão Confirmar Delete
            document.getElementById("confirmDelete").addEventListener("click", () => {
                window.location.href = window.location.href;
            });

        });
    </script>
</body>
</html>