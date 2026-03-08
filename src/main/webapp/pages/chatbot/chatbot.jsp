<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <title>Vértice - Apresentação</title>

  <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/layout/hero.css">

  <link rel="shortcut icon"
        href="<%=request.getContextPath()%>/assets/img/Logo%20-%20Vértice.svg"
        type="image/x-icon">

</head>
<%
    String errorMessage = request.getAttribute("error") != null? request.getAttribute("error").toString() : "";
    String aiResponse = request.getAttribute("response") != null? request.getAttribute("response").toString() : "";
    String userPrompt = request.getAttribute("userPrompt") != null? request.getAttribute("userPrompt").toString() : "";
%>
<body>

<nav class="navbar">
  <div class="nav-container">

    <div class="logo">
      <svg xmlns="http://www.w3.org/2000/svg" width="85" height="48" viewBox="0 0 85 48" fill="none">
        <mask id="mask0_1197_1043" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="14" y="14" width="21" height="32">
          <path d="M34.8545 14.9846H32.5893C28.2959 14.9846 24.4903 17.5554 23.1612 21.3535L14.8739 45.0349H19.9752C22.5772 45.0349 24.8836 43.4769 25.6892 41.175L34.8545 14.9846Z" fill="#D9D9D9"/>
        </mask>
        <g mask="url(#mask0_1197_1043)">
          <path d="M34.04 15.1978H31.7748C27.4815 15.1978 23.6759 17.7686 22.3467 21.5667L14.0594 45.2482H19.1607C21.7627 45.2482 24.0692 43.6901 24.8747 41.3882L34.04 15.1978Z" fill="url(#paint0_linear_1197_1043)"/>
        </g>
        <mask id="mask1_1197_1043" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="14" width="20" height="32">
          <path d="M0 14.9846C5.71965 15.3225 10.6461 18.8572 12.4507 23.918L19.9806 45.0349H14.8793C12.2773 45.0349 9.97088 43.4769 9.16533 41.175L0 14.9846Z" fill="#D9D9D9"/>
        </mask>
        <g mask="url(#mask1_1197_1043)">
          <mask id="mask2_1197_1043" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="-6" y="-1" width="20" height="31">
            <path d="M-5.33887 -0.112091C0.391919 0.226431 5.34131 3.73882 7.20721 8.79137L13.3134 25.3259L5.17725 29.9382L-5.33887 -0.112091Z" fill="url(#paint1_linear_1197_1043)"/>
          </mask>
          <g mask="url(#mask2_1197_1043)">
            <path d="M-5.33887 -0.112091C0.391919 0.226431 5.34131 3.73882 7.20721 8.79137L13.3134 25.3259L5.17725 29.9382L-5.33887 -0.112091Z" fill="url(#paint2_linear_1197_1043)"/>
          </g>
          <path d="M-5.46191 -0.0420837C0.268872 0.296438 5.21824 3.80888 7.08414 8.86143L13.1904 25.3958L10.6311 26.8466L12.1984 25.915C12.1952 25.9026 11.8515 24.5498 11.5501 23.7077C11.2578 22.8912 10.6857 21.6619 10.6857 21.6619C9.17293 18.0549 4.36274 15.8958 1.04469 15.6857C1.04469 15.6857 2.87986 19.6584 3.83307 22.2796C4.75865 24.8248 5.82154 28.7616 6.00983 29.4663L5.05416 30.0082L-5.46191 -0.0420837Z" fill="url(#paint3_linear_1197_1043)"/>
          <path d="M5.55859 31.4771L15.0231 26.3056L20.0421 45.0887H15.4528L13.3614 44.9806L11.5606 44.278L10.3987 43.3592L9.58542 42.2242L5.55859 31.4771Z" fill="url(#paint4_linear_1197_1043)"/>
        </g>
        <path d="M30.3256 29.2153L27.6079 41.6932L22.4037 46.6123L14.424 47.2122L8.75734 43.7329L5.3457 30.5951L6.21711 30.0299C8.0925 37.3005 11.5527 43.5184 16.5553 43.5184C19.5014 43.5184 22.7438 41.5163 24.5517 38.3416L27.1306 33.3035L30.0295 26.6977L30.3256 29.2153Z" fill="url(#paint5_linear_1197_1043)"/>
        <mask id="mask3_1197_1043" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="13" y="0" width="67" height="13">
          <path d="M79.9272 12.7634C79.9272 13.0862 64.7343 0 46.5063 0C28.2784 0 13.918 13.0862 13.918 12.7634C16.9149 12.7634 28.2784 3.7794 46.5063 3.7794C64.7343 3.7794 77.4352 12.8495 79.9272 12.7634Z" fill="url(#paint6_linear_1197_1043)"/>
        </mask>
        <g mask="url(#mask3_1197_1043)">
          <path d="M83.8549 14.524C83.8549 14.8617 66.9324 1.17386 46.6293 1.17386C26.3263 1.17386 10.3311 14.8617 10.3311 14.524C13.6691 14.524 26.3263 5.12702 46.6293 5.12702C66.9324 5.12702 81.0792 14.6142 83.8549 14.524Z" fill="url(#paint7_linear_1197_1043)"/>
          <path d="M79.9272 11.8103C79.9272 12.1573 64.7343 -1.90659 46.5063 -1.90659C28.2784 -1.90659 13.918 12.1573 13.918 11.8103C16.9149 11.8103 28.2784 2.15518 46.5063 2.15518C64.7343 2.15518 77.4352 11.903 79.9272 11.8103Z" fill="url(#paint8_linear_1197_1043)"/>
        </g>
        <path d="M84.3249 40.5689H75.5767C75.7672 41.5478 76.198 42.2646 76.869 42.7193C77.5483 43.1663 78.2856 43.3898 79.0809 43.3898C80.307 43.3898 81.3342 42.9158 82.1627 41.9678L83.7408 43.0777C82.5562 44.5036 80.9697 45.2165 78.9815 45.2165C77.4572 45.2165 76.1814 44.7309 75.1542 43.7598C74.1269 42.7887 73.6133 41.5825 73.6133 40.1412C73.6133 38.7924 74.131 37.6247 75.1666 36.6381C76.2104 35.6439 77.482 35.1468 78.9815 35.1468C80.4561 35.1468 81.7195 35.6516 82.7716 36.6613C83.8237 37.6632 84.3414 38.9658 84.3249 40.5689ZM75.8003 38.9619H82.0633C81.3674 37.6517 80.3401 36.9965 78.9815 36.9965C77.4986 36.9965 76.4382 37.6517 75.8003 38.9619Z" fill="#1B2559"/>
        <path d="M71.9874 42.0372V44.3379C70.8856 44.9236 69.8169 45.2165 68.7814 45.2165C67.3648 45.2165 66.1221 44.7232 65.0535 43.7367C63.9931 42.7424 63.4629 41.594 63.4629 40.2915C63.4629 38.8116 63.9931 37.5862 65.0535 36.615C66.1139 35.6362 67.4435 35.1468 69.0424 35.1468C69.9951 35.1468 70.9767 35.378 71.9874 35.8404V38.0833C70.9022 37.4359 69.8749 37.1122 68.9057 37.1122C68.011 37.1122 67.2281 37.405 66.5571 37.9908C65.886 38.5766 65.5505 39.2625 65.5505 40.0487C65.5505 40.935 65.8653 41.6827 66.4949 42.2915C67.1245 42.8927 67.895 43.1933 68.8063 43.1933C69.7341 43.1933 70.7945 42.8079 71.9874 42.0372Z" fill="#1B2559"/>
        <path d="M58.3047 32.2806C58.3047 31.8644 58.4704 31.506 58.8017 31.2055C59.1331 30.8972 59.5266 30.743 59.9823 30.743C60.4296 30.743 60.8148 30.8972 61.1379 31.2055C61.4693 31.506 61.635 31.8644 61.635 32.2806C61.635 32.7045 61.4693 33.0668 61.1379 33.3674C60.8148 33.668 60.4296 33.8183 59.9823 33.8183C59.5266 33.8183 59.1331 33.668 58.8017 33.3674C58.4704 33.0668 58.3047 32.7045 58.3047 32.2806ZM58.9136 35.4137H61.0509V44.9516H58.9136V35.4137Z" fill="#1B2559"/>
        <path d="M52.4895 32.6368H54.6517V35.4115H56.7891V37.2497H54.6517V44.9494H52.4895V37.2497H50.7871V35.3884H52.4895V32.6368Z" fill="#1B2559"/>
        <path d="M45.1945 35.4127V37.3781C45.675 36.5919 46.1431 36.0254 46.5987 35.6786C47.0626 35.324 47.6632 35.1468 48.4005 35.1468C48.6077 35.1468 48.9059 35.193 49.2953 35.2855L48.6367 37.2971C48.2224 37.2201 47.9491 37.1815 47.8165 37.1815C47.0875 37.1815 46.4745 37.4204 45.9774 37.8983C45.4886 38.3685 45.2442 38.9581 45.2442 39.6672V44.9506H43.082V35.4127H45.1945Z" fill="#1B2559"/>
        <path d="M40.3132 40.5698H31.5649C31.7555 41.5487 32.1863 42.2654 32.8573 42.7202C33.5366 43.1672 34.2739 43.3907 35.0692 43.3907C36.2953 43.3907 37.3225 42.9167 38.151 41.9687L39.7291 43.0786C38.5445 44.5044 36.958 45.2174 34.9698 45.2174C33.4455 45.2174 32.1697 44.7318 31.1424 43.7607C30.1152 42.7895 29.6016 41.5833 29.6016 40.142C29.6016 38.7933 30.1193 37.6256 31.1549 36.639C32.1987 35.6448 33.4703 35.1476 34.9698 35.1476C36.4444 35.1476 37.7077 35.6525 38.7598 36.6621C39.812 37.6641 40.3297 38.9667 40.3132 40.5698ZM31.7886 38.9628H38.0515C37.3557 37.6526 36.3284 36.9974 34.9698 36.9974C33.4869 36.9974 32.4265 37.6526 31.7886 38.9628ZM33.5407 33.6563L35.1935 30.4423H38.1261L35.3799 33.6563H33.5407Z" fill="#1B2559"/>
        <defs>
          <linearGradient id="paint0_linear_1197_1043" x1="38.8069" y1="4.8748" x2="11.3574" y2="54.9812" gradientUnits="userSpaceOnUse">
            <stop stop-color="white"/>
            <stop offset="0.474208" stop-color="#7C3AED"/>
            <stop offset="0.688674" stop-color="#2C18A3"/>
          </linearGradient>
          <linearGradient id="paint1_linear_1197_1043" x1="3.98725" y1="-0.112091" x2="-10.7042" y2="27.7303" gradientUnits="userSpaceOnUse">
            <stop stop-color="#4361EE"/>
            <stop offset="1" stop-color="#7C3AED"/>
          </linearGradient>
          <linearGradient id="paint2_linear_1197_1043" x1="3.98725" y1="-0.112091" x2="-10.7042" y2="27.7303" gradientUnits="userSpaceOnUse">
            <stop offset="1" stop-color="#7C3AED"/>
          </linearGradient>
          <linearGradient id="paint3_linear_1197_1043" x1="-26.6076" y1="27.2518" x2="-0.198951" y2="9.76046" gradientUnits="userSpaceOnUse">
            <stop offset="0.422047" stop-color="white"/>
            <stop offset="1" stop-color="white" stop-opacity="0"/>
          </linearGradient>
          <linearGradient id="paint4_linear_1197_1043" x1="6.44842" y1="28.7123" x2="15.4668" y2="49.373" gradientUnits="userSpaceOnUse">
            <stop stop-color="#7F96FD"/>
            <stop offset="1" stop-color="#4361EE"/>
          </linearGradient>
          <linearGradient id="paint5_linear_1197_1043" x1="19.927" y1="46.6174" x2="4.68368" y2="32.6081" gradientUnits="userSpaceOnUse">
            <stop stop-color="white" stop-opacity="0"/>
            <stop offset="1" stop-color="white" stop-opacity="0.4"/>
          </linearGradient>
          <linearGradient id="paint6_linear_1197_1043" x1="13.918" y1="6.38461" x2="79.9272" y2="6.38461" gradientUnits="userSpaceOnUse">
            <stop offset="0.0384615" stop-color="#FF5B5B"/>
            <stop offset="0.625" stop-color="#FF0080"/>
            <stop offset="0.75" stop-color="#FF1777"/>
            <stop offset="1" stop-color="#FF0080"/>
          </linearGradient>
          <linearGradient id="paint7_linear_1197_1043" x1="4.52904" y1="14.5302" x2="83.4004" y2="4.33526" gradientUnits="userSpaceOnUse">
            <stop offset="0.125" stop-color="#F980BF"/>
            <stop offset="0.274038" stop-color="#E962DA"/>
            <stop offset="0.456731" stop-color="#7C3AED"/>
            <stop offset="1" stop-color="#43AAEE"/>
          </linearGradient>
          <linearGradient id="paint8_linear_1197_1043" x1="12.9359" y1="11.8167" x2="79.6888" y2="3.14817" gradientUnits="userSpaceOnUse">
            <stop stop-color="#FFBCAA"/>
            <stop offset="0.168269" stop-color="#FF93DB"/>
            <stop offset="0.341346" stop-color="#AA5DED"/>
            <stop offset="0.788462" stop-color="#7CBFFA"/>
          </linearGradient>
        </defs>
      </svg>
    </div>

    <div class="nav-links">

      <a href="<%=request.getContextPath()%>/index.jsp"
         target="_blank"
         class="login-link">
        Login
      </a>

      <a href="<%=request.getContextPath()%>/pages/students/signupCpf.jsp"
         target="_blank"
         class="btn btn-primary btn-nav">
        Cadastro
      </a>

    </div>
  </div>
</nav>


<main class="hero-section">

  <div class="hero-container">

    <div class="hero-text">

      <h1 class="title">

        Tudo o que acontece na escola num clique. Para todos que fazem a educação acontecer.

        <span class="wave">
<svg xmlns="http://www.w3.org/2000/svg" width="487" height="34" viewBox="0 0 487 34" fill="none">
<path d="M4.00098 30C73.6316 10.3798 266.915 -17.0885 483.001 30"
      stroke="#4361EE"
      stroke-width="8"
      stroke-linecap="round"/>
</svg>
</span>

      </h1>
      <p class="description">

        Centralize notas, observações e relatórios em um ambiente seguro.
        Acompanhamento em tempo real para pais e ferramentas de produtividade de ponta para educadores.
      </p>
      <a href="<%=request.getContextPath()%>/index.jsp"
         target="_blank"
         class="btn btn-primary btn-large">
        Acesse o painel
      </a>
    </div>

    <div class="hero-chat">
      <h2 class="chat-title">Pergunte sobre nós</h2>
      <div class="chat-window">

        <%
          if (!userPrompt.isEmpty() && !aiResponse.isEmpty()) {
        %>
        <div class="message sent">
          <p><%= userPrompt %></p>
        </div>
        <div class="message received">
          <p><%= aiResponse %></p>
        </div>
        <% } else { %>
        <div class="message received">
          <p>Faça uma pergunta sobre a escola e eu te respondo!</p>
        </div>
        <% } %>
      </div>

      <form class="chat-input-box" id="chatForm" method="post" action="${pageContext.request.contextPath}/chatbot">
        <button type="button" class="icon-btn clip">
        </button>
        <input
                type="text"
                id="messageInput"
                name="userPrompt"
                placeholder="Mande uma mensagem"
                class="chat-input"
                required
                autocomplete="off">
        <button type="submit" class="btn-send">
          Enviar
        </button>
        <p style="color: #ff0066"><%= errorMessage %></p>
      </form>
    </div>
  </div>
</main>


<div class="fab-profile">

  <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28"
       viewBox="0 0 24 24" fill="#3A56E4">

    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
    <circle cx="12" cy="7" r="4"></circle>
  </svg>
  <div class="lock-badge">
    <svg xmlns="http://www.w3.org/2000/svg"
         width="10" height="10" viewBox="0 0 24 24"
         fill="#fff">
      <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
      <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
    </svg>
  </div>
</div>
</body>
</html>