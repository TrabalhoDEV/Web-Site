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

<header class="navbar" style="color: black">
  <div class="nav-container">

    <a href="#" class="logo">
      <svg xmlns="http://www.w3.org/2000/svg" width="101" height="51" viewBox="0 0 101 51" fill="none">
        <mask id="mask0_1217_584" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="22" y="16" width="23" height="35">
          <path d="M44.8174 16.7429H42.2769C37.4619 16.7429 33.1938 19.5941 31.7032 23.8066L22.4088 50.0715H28.13C31.0482 50.0715 33.6349 48.3435 34.5383 45.7905L44.8174 16.7429Z" fill="#D9D9D9"/>
        </mask>
        <g mask="url(#mask0_1217_584)">
          <path d="M43.9043 16.9795H41.3638C36.5488 16.9795 32.2808 19.8307 30.7901 24.0432L21.4957 50.3081H27.2169C30.1351 50.3081 32.7218 48.5801 33.6252 46.027L43.9043 16.9795Z" fill="url(#paint0_linear_1217_584)"/>
        </g>
        <mask id="mask1_1217_584" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="5" y="16" width="24" height="35">
          <path d="M5.72852 16.7429C12.1432 17.1176 17.6682 21.038 19.6921 26.6509L28.1371 50.0715H22.4159C19.4977 50.0715 16.911 48.3435 16.0076 45.7905L5.72852 16.7429Z" fill="#D9D9D9"/>
        </mask>
        <g mask="url(#mask1_1217_584)">
          <mask id="mask2_1217_584" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="-1" y="-1" width="22" height="35">
            <path d="M-0.260742 -0.000732422C6.16642 0.37472 11.7172 4.27028 13.8099 9.87403L20.658 28.2123L11.5332 33.3279L-0.260742 -0.000732422Z" fill="url(#paint1_linear_1217_584)"/>
          </mask>
          <g mask="url(#mask2_1217_584)">
            <path d="M-0.260742 -0.000732422C6.16642 0.37472 11.7172 4.27028 13.8099 9.87403L20.658 28.2123L11.5332 33.3279L-0.260742 -0.000732422Z" fill="url(#paint2_linear_1217_584)"/>
          </g>
          <path d="M-0.395996 0.0769043C6.03116 0.452356 11.5819 4.34798 13.6746 9.95172L20.5228 28.2899L17.6526 29.8989L19.4103 28.8657C19.4068 28.8519 19.0213 27.3516 18.6832 26.4176C18.3554 25.5121 17.7137 24.1487 17.7137 24.1487C16.0172 20.1481 10.6225 17.7534 6.90125 17.5205C6.90125 17.5205 8.95942 21.9265 10.0285 24.8337C11.0665 27.6566 12.2586 32.0228 12.4697 32.8044L11.3979 33.4054L-0.395996 0.0769043Z" fill="url(#paint3_linear_1217_584)"/>
          <path d="M11.9624 35.0347L22.577 29.2991L28.2059 50.1312H23.0589L20.7134 50.0113L18.6937 49.232L17.3907 48.213L16.4786 46.9542L11.9624 35.0347Z" fill="url(#paint4_linear_1217_584)"/>
        </g>
        <mask id="mask3_1217_584" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="44" height="51">
          <mask id="mask4_1217_584" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="22" y="16" width="23" height="35">
            <path d="M44.8174 16.7427H42.2769C37.4619 16.7427 33.1938 19.5939 31.7032 23.8064L22.4088 50.0713H28.13C31.0482 50.0713 33.6349 48.3432 34.5383 45.7902L44.8174 16.7427Z" fill="#D9D9D9"/>
          </mask>
          <g mask="url(#mask4_1217_584)">
            <path d="M43.9043 16.9792H41.3638C36.5488 16.9792 32.2808 19.8305 30.7901 24.0429L21.4957 50.3078H27.2169C30.1351 50.3078 32.7218 48.5798 33.6252 46.0268L43.9043 16.9792Z" fill="url(#paint5_linear_1217_584)"/>
          </g>
          <path d="M0 0C6.42716 0.375452 11.978 4.27102 14.0706 9.87476L20.9188 28.213L11.794 33.3286L0 0Z" fill="url(#paint6_linear_1217_584)"/>
          <path d="M11.9624 35.0344L22.577 29.2988L28.2059 50.131H23.0589L20.7134 50.0111L18.6937 49.2318L17.3907 48.2128L16.4786 46.9539L11.9624 35.0344Z" fill="url(#paint7_linear_1217_584)"/>
        </mask>
        <g mask="url(#mask3_1217_584)">
          <path d="M39.7375 32.5262L36.6895 46.3653L30.853 51.821L21.9036 52.4864L15.5484 48.6274L11.7222 34.0564L12.6995 33.4295C14.8027 41.4934 18.6834 48.3896 24.2939 48.3896C27.598 48.3896 31.2344 46.169 33.262 42.648L36.1543 37.0603L39.4054 29.7339L39.7375 32.5262Z" fill="url(#paint8_linear_1217_584)"/>
        </g>
        <mask id="mask5_1217_584" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="21" y="0" width="75" height="15">
          <path d="M95.3682 14.2795C95.3682 14.6376 78.3291 0.123779 57.8862 0.123779C37.4433 0.123779 21.3379 14.6376 21.3379 14.2795C24.699 14.2795 37.4433 4.31548 57.8862 4.31548C78.3291 4.31548 92.5734 14.3751 95.3682 14.2795Z" fill="url(#paint9_linear_1217_584)"/>
        </mask>
        <g mask="url(#mask5_1217_584)">
          <path d="M99.7736 16.2324C99.7736 16.6069 80.7947 1.42578 58.0245 1.42578C35.2543 1.42578 17.3154 16.6069 17.3154 16.2324C21.0591 16.2324 35.2543 5.8102 58.0245 5.8102C80.7947 5.8102 96.6605 16.3323 99.7736 16.2324Z" fill="url(#paint10_linear_1217_584)"/>
          <path d="M95.3682 13.2226C95.3682 13.6075 78.3291 -1.99072 57.8862 -1.99072C37.4433 -1.99072 21.3379 13.6075 21.3379 13.2226C24.699 13.2226 37.4433 2.51415 57.8862 2.51415C78.3291 2.51415 92.5734 13.3253 95.3682 13.2226Z" fill="url(#paint11_linear_1217_584)"/>
        </g>
        <path d="M100.301 45.1184H90.4896C90.7032 46.204 91.1864 46.999 91.9389 47.5034C92.7008 47.9992 93.5277 48.2471 94.4196 48.2471C95.7947 48.2471 96.9468 47.7214 97.8759 46.6699L99.6458 47.9009C98.3172 49.4823 96.538 50.273 94.3081 50.273C92.5986 50.273 91.1678 49.7345 90.0157 48.6574C88.8636 47.5803 88.2876 46.2425 88.2876 44.644C88.2876 43.1481 88.8683 41.853 90.0297 40.7588C91.2003 39.6561 92.6265 39.1047 94.3081 39.1047C95.9619 39.1047 97.3788 39.6646 98.5587 40.7845C99.7387 41.8957 100.319 43.3404 100.301 45.1184ZM90.7404 43.3361H97.7644C96.9839 41.8829 95.8319 41.1563 94.3081 41.1563C92.6451 41.1563 91.4558 41.8829 90.7404 43.3361Z" fill="#1B2559"/>
        <path d="M86.4618 46.7469V49.2985C85.2261 49.9482 84.0275 50.273 82.8662 50.273C81.2774 50.273 79.8838 49.7259 78.6852 48.6317C77.496 47.529 76.9014 46.2553 76.9014 44.8107C76.9014 43.1694 77.496 41.8103 78.6852 40.7332C79.8745 39.6475 81.3657 39.1047 83.1588 39.1047C84.2273 39.1047 85.3283 39.3612 86.4618 39.8741V42.3616C85.2446 41.6436 84.0926 41.2845 83.0055 41.2845C82.0021 41.2845 81.1241 41.6094 80.3715 42.259C79.619 42.9087 79.2427 43.6695 79.2427 44.5414C79.2427 45.5245 79.5957 46.3536 80.3019 47.029C81.008 47.6957 81.872 48.0291 82.894 48.0291C83.9346 48.0291 85.1239 47.6017 86.4618 46.7469Z" fill="#1B2559"/>
        <path d="M71.1182 35.9258C71.1182 35.4642 71.304 35.0667 71.6756 34.7334C72.0473 34.3914 72.4886 34.2205 72.9996 34.2205C73.5013 34.2205 73.9333 34.3914 74.2957 34.7334C74.6673 35.0667 74.8531 35.4642 74.8531 35.9258C74.8531 36.396 74.6673 36.7978 74.2957 37.1311C73.9333 37.4645 73.5013 37.6312 72.9996 37.6312C72.4886 37.6312 72.0473 37.4645 71.6756 37.1311C71.304 36.7978 71.1182 36.396 71.1182 35.9258ZM71.801 39.4007H74.1981V49.9791H71.801V39.4007Z" fill="#1B2559"/>
        <path d="M64.5958 36.321H67.0208V39.3984H69.4178V41.4372H67.0208V49.9769H64.5958V41.4372H62.6865V39.3728H64.5958V36.321Z" fill="#1B2559"/>
        <path d="M56.4151 39.3997V41.5795C56.954 40.7075 57.4789 40.0792 57.9899 39.6946C58.5102 39.3013 59.1838 39.1047 60.0107 39.1047C60.243 39.1047 60.5774 39.156 61.0141 39.2586L60.2755 41.4897C59.8109 41.4042 59.5043 41.3615 59.3557 41.3615C58.5381 41.3615 57.8505 41.6265 57.2931 42.1565C56.7449 42.6779 56.4708 43.3318 56.4708 44.1183V49.9781H54.0459V39.3997H56.4151Z" fill="#1B2559"/>
        <path d="M50.9385 45.1193H41.1272C41.3409 46.205 41.8241 47 42.5766 47.5043C43.3385 48.0001 44.1654 48.248 45.0573 48.248C46.4324 48.248 47.5845 47.7223 48.5136 46.6709L50.2835 47.9018C48.9549 49.4832 47.1757 50.2739 44.9458 50.2739C43.2363 50.2739 41.8055 49.7354 40.6534 48.6583C39.5013 47.5812 38.9253 46.2434 38.9253 44.6449C38.9253 43.149 39.506 41.8539 40.6673 40.7597C41.838 39.657 43.2642 39.1057 44.9458 39.1057C46.5996 39.1057 48.0165 39.6656 49.1964 40.7854C50.3764 41.8967 50.9571 43.3413 50.9385 45.1193ZM41.3781 43.337H48.4021C47.6216 41.8838 46.4695 41.1572 44.9458 41.1572C43.2828 41.1572 42.0935 41.8838 41.3781 43.337ZM43.3431 37.4516L45.1967 33.887H48.4857L45.4057 37.4516H43.3431Z" fill="#1B2559"/>
        <defs>
          <linearGradient id="paint0_linear_1217_584" x1="49.2504" y1="5.53028" x2="18.9897" y2="61.387" gradientUnits="userSpaceOnUse">
            <stop stop-color="white"/>
            <stop offset="0.474208" stop-color="#7C3AED"/>
            <stop offset="0.688674" stop-color="#2C18A3"/>
          </linearGradient>
          <linearGradient id="paint1_linear_1217_584" x1="10.1986" y1="-0.000732713" x2="-5.99272" y2="31.0279" gradientUnits="userSpaceOnUse">
            <stop stop-color="#4361EE"/>
            <stop offset="1" stop-color="#7C3AED"/>
          </linearGradient>
          <linearGradient id="paint2_linear_1217_584" x1="10.1986" y1="-0.000732713" x2="-5.99272" y2="31.0279" gradientUnits="userSpaceOnUse">
            <stop offset="1" stop-color="#7C3AED"/>
          </linearGradient>
          <linearGradient id="paint3_linear_1217_584" x1="-24.1112" y1="30.3483" x2="5.30444" y2="10.6472" gradientUnits="userSpaceOnUse">
            <stop offset="0.422047" stop-color="white"/>
            <stop offset="1" stop-color="white" stop-opacity="0"/>
          </linearGradient>
          <linearGradient id="paint4_linear_1217_584" x1="12.9604" y1="31.9683" x2="22.8868" y2="54.964" gradientUnits="userSpaceOnUse">
            <stop stop-color="#7F96FD"/>
            <stop offset="1" stop-color="#4361EE"/>
          </linearGradient>
          <linearGradient id="paint5_linear_1217_584" x1="49.2504" y1="5.53004" x2="18.9897" y2="61.3867" gradientUnits="userSpaceOnUse">
            <stop stop-color="white"/>
            <stop offset="0.474208" stop-color="#7C3AED"/>
            <stop offset="0.688674" stop-color="#2C18A3"/>
          </linearGradient>
          <linearGradient id="paint6_linear_1217_584" x1="10.4594" y1="-2.91553e-07" x2="-5.73198" y2="31.0287" gradientUnits="userSpaceOnUse">
            <stop stop-color="#4361EE"/>
            <stop offset="1" stop-color="#7C3AED"/>
          </linearGradient>
          <linearGradient id="paint7_linear_1217_584" x1="12.9604" y1="31.9681" x2="22.8868" y2="54.9638" gradientUnits="userSpaceOnUse">
            <stop stop-color="#7F96FD"/>
            <stop offset="1" stop-color="#4361EE"/>
          </linearGradient>
          <linearGradient id="paint8_linear_1217_584" x1="28.0753" y1="51.8266" x2="11.1542" y2="36.1013" gradientUnits="userSpaceOnUse">
            <stop stop-color="white" stop-opacity="0"/>
            <stop offset="1" stop-color="white" stop-opacity="0.4"/>
          </linearGradient>
          <linearGradient id="paint9_linear_1217_584" x1="21.3379" y1="7.2049" x2="95.3682" y2="7.2049" gradientUnits="userSpaceOnUse">
            <stop offset="0.0384615" stop-color="#FF5B5B"/>
            <stop offset="0.625" stop-color="#FF0080"/>
            <stop offset="0.75" stop-color="#FF1777"/>
            <stop offset="1" stop-color="#FF0080"/>
          </linearGradient>
          <linearGradient id="paint10_linear_1217_584" x1="10.8084" y1="16.2392" x2="99.231" y2="4.68163" gradientUnits="userSpaceOnUse">
            <stop offset="0.125" stop-color="#F980BF"/>
            <stop offset="0.274038" stop-color="#E962DA"/>
            <stop offset="0.456731" stop-color="#7C3AED"/>
            <stop offset="1" stop-color="#43AAEE"/>
          </linearGradient>
          <linearGradient id="paint11_linear_1217_584" x1="20.2365" y1="13.2296" x2="95.0729" y2="3.40256" gradientUnits="userSpaceOnUse">
            <stop stop-color="#FFBCAA"/>
            <stop offset="0.168269" stop-color="#FF93DB"/>
            <stop offset="0.341346" stop-color="#AA5DED"/>
            <stop offset="0.788462" stop-color="#7CBFFA"/>
          </linearGradient>
        </defs>
      </svg>
    </a>

    <nav class="nav-links">
      <a href="<%= request.getContextPath() %>/index.html#hero">Início</a>
      <a href="">Detalhes</a>
      <a href="<%= request.getContextPath() %>/index.html#recursos">Recursos</a>
      <a href="<%= request.getContextPath() %>/index.html#beneficios">Benefícios</a>
    </nav>

    <div class="nav-actions">
      <a href="<%= request.getContextPath() %>/index.jsp" class="login-link">Login</a>
      <a href="<%= request.getContextPath() %>/pages/students/signupCpf.jsp" class="btn btn-primary btn-nav">Cadastrar</a>
    </div>

  </div>
</header>


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