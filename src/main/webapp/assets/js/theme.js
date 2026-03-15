document.addEventListener('DOMContentLoaded', () => {
    const themeBtn = document.getElementById('theme-button');
    const body = document.body;

    if (localStorage.getItem('system-theme') === 'dark') {
        body.classList.add('dark-mode');
    }

    if (themeBtn) {
        themeBtn.addEventListener('click', () => {

            body.classList.toggle('dark-mode');
            if (body.classList.contains('dark-mode')) {
                localStorage.setItem('system-theme', 'dark');
            } else {
                localStorage.setItem('system-theme', 'light');
            }
        });
    } else {
        console.error("The 'theme-button' was not found in HTML");
    }
});