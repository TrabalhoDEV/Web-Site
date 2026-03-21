const C = {
    violet: '#6d28d9',
    green:  '#059669',
    amber:  '#d97706',
    blue:   '#2563eb',
    rose:   '#dc2626',
    grid:   'rgba(109,40,217,.06)',
    text:   '#9ca3af',
};

function grad(ctx, color) {
    const g = ctx.createLinearGradient(0, 0, 0, 260);
    g.addColorStop(0, color + 'bb');
    g.addColorStop(1, color + '15');
    return g;
}

const charts = {};

// Chart.js defaults
Chart.defaults.font.family    = "'Poppins', sans-serif";
Chart.defaults.font.size      = 12;
Chart.defaults.color          = C.text;
Chart.defaults.plugins.legend.display = false;

const baseScales = {
    x: { grid: { color: C.grid, drawBorder: false }, ticks: { color: C.text } },
    y: { grid: { color: C.grid, drawBorder: false }, ticks: { color: C.text } },
};

async function loadAll() {
    showLoading(true);
    spinRefresh(true);

    // Pega o contextPath via <meta name="ctx"> no JSP
    const ctxPath = document.querySelector('meta[name="ctx"]')?.content ?? '';
    const url     = `${ctxPath}/admin/dashboard/api/all`;

    try {
        const res = await fetch(url);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const data = await res.json();
        if (data.error) throw new Error(data.error);

        renderKpis(data.kpis);
        renderSubjectGrades(data.avgGradesSubject);
        renderClassGrades(data.avgGradesClass);
        renderApproval(data.approvalRate);
        renderStudentsPerClass(data.studentsPerClass);
        renderSubjectsTeacher(data.subjectsPerTeacher);
        renderClassesTeacher(data.classesPerTeacher);

    } catch (err) {
        console.error('[Dashboard] Erro:', err);
        alert('Não foi possível carregar os dados.\n' + err.message);
    } finally {
        showLoading(false);
        spinRefresh(false);
    }
}

// ── KPIs ──────────────────────────────────────────────────────
function renderKpis(k) {
    if (!k) return;
    setText('kpi-students',     fmt(k.totalStudents));
    setText('kpi-teachers',     fmt(k.totalTeachers));
    setText('kpi-classes',      fmt(k.totalClasses));
    setText('kpi-subjects',     fmt(k.totalSubjects));
    setText('kpi-top-class',    k.classWithMostStudents ?? '—');
    setText('kpi-max-students', fmt(k.maxStudentsInClass));
    setText('kpi-avg',          fmtNum(k.avgStudentsPerClass));
}

function renderSubjectGrades(rows) {
    if (!rows?.length) return;
    buildChart('chartSubjectGrades', {
        type: 'bar',
        data: {
            labels: rows.map(r => r.subjectName),
            datasets: [{
                data: rows.map(r => +r.avgGrade),
                backgroundColor: ctx => grad(ctx.chart.ctx, C.violet),
                borderColor: C.violet, borderWidth: 0, borderRadius: 6,
            }]
        },
        options: {
            indexAxis: 'y', responsive: true, maintainAspectRatio: false,
            scales: {
                x: { ...baseScales.x, min: 0, max: 10,
                    ticks: { callback: v => v.toFixed(0) } },
                y: { ...baseScales.y }
            },
            plugins: { tooltip: { callbacks: {
                        label: c => `  Média: ${c.raw.toFixed(2)}`
                    }}}
        }
    });
}

function renderClassGrades(rows) {
    if (!rows?.length) return;
    buildChart('chartClassGrades', {
        type: 'bar',
        data: {
            labels: rows.map(r => r.schoolYear),
            datasets: [{
                data: rows.map(r => +r.avgGrade),
                backgroundColor: ctx => grad(ctx.chart.ctx, C.blue),
                borderColor: C.blue, borderWidth: 0, borderRadius: 6,
            }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            scales: { ...baseScales, y: { ...baseScales.y, min: 0, max: 10 } },
            plugins: { tooltip: { callbacks: {
                        label: c => `  Média: ${c.raw.toFixed(2)}`
                    }}}
        }
    });
}

function renderApproval(rows) {
    if (!rows?.length) return;
    buildChart('chartApproval', {
        type: 'line',
        data: {
            labels: rows.map(r => r.schoolYear),
            datasets: [{
                data: rows.map(r => +r.approvalRate),
                borderColor: C.green,
                backgroundColor: C.green + '18',
                pointBackgroundColor: C.green,
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointRadius: 5,
                tension: 0.4, fill: true,
            }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            scales: {
                ...baseScales,
                y: { ...baseScales.y, min: 0, max: 100,
                    ticks: { callback: v => v + '%' } }
            },
            plugins: { tooltip: { callbacks: {
                        label: c => `  Aprovação: ${c.raw.toFixed(1)}%`
                    }}}
        }
    });
}

function renderStudentsPerClass(rows) {
    if (!rows?.length) return;
    const palette = ['#6d28d9','#2563eb','#059669','#d97706','#dc2626',
        '#7c3aed','#0284c7','#16a34a','#b45309','#9f1239'];
    buildChart('chartStudentsPerClass', {
        type: 'doughnut',
        data: {
            labels: rows.map(r => r.schoolYear),
            datasets: [{
                data: rows.map(r => r.totalStudents),
                backgroundColor: palette.slice(0, rows.length),
                borderColor: '#fff', borderWidth: 3, hoverOffset: 8,
            }]
        },
        options: {
            responsive: true, maintainAspectRatio: false, cutout: '65%',
            plugins: {
                legend: {
                    display: true, position: 'right',
                    labels: { boxWidth: 10, borderRadius: 3, padding: 14,
                        color: '#374151', font: { size: 12 } }
                },
                tooltip: { callbacks: {
                        label: c => `  ${c.label}: ${c.raw} alunos`
                    }}
            }
        }
    });
}

function renderSubjectsTeacher(rows) {
    if (!rows?.length) return;
    buildChart('chartSubjectsTeacher', {
        type: 'bar',
        data: {
            labels: rows.map(r => r.name),
            datasets: [{
                data: rows.map(r => r.totalSubjects),
                backgroundColor: ctx => grad(ctx.chart.ctx, C.amber),
                borderColor: C.amber, borderWidth: 0, borderRadius: 6,
            }]
        },
        options: {
            indexAxis: 'y', responsive: true, maintainAspectRatio: false,
            scales: {
                x: { ...baseScales.x, ticks: { stepSize: 1 } },
                y: { ...baseScales.y, ticks: { font: { size: 11 } } }
            }
        }
    });
}

function renderClassesTeacher(rows) {
    if (!rows?.length) return;
    buildChart('chartClassesTeacher', {
        type: 'bar',
        data: {
            labels: rows.map(r => r.name),
            datasets: [{
                data: rows.map(r => r.totalClasses),
                backgroundColor: ctx => grad(ctx.chart.ctx, C.rose),
                borderColor: C.rose, borderWidth: 0, borderRadius: 6,
            }]
        },
        options: {
            indexAxis: 'y', responsive: true, maintainAspectRatio: false,
            scales: {
                x: { ...baseScales.x, ticks: { stepSize: 1 } },
                y: { ...baseScales.y, ticks: { font: { size: 11 } } }
            }
        }
    });
}

function buildChart(id, config) {
    if (charts[id]) charts[id].destroy();
    const el = document.getElementById(id);
    if (!el) return;
    charts[id] = new Chart(el, config);
}

function setText(id, val) {
    const el = document.getElementById(id);
    if (el) el.textContent = val ?? '—';
}

function fmt(n)    { return n != null ? Number(n).toLocaleString('pt-BR') : '—'; }
function fmtNum(n) { return n != null ? Number(n).toFixed(1) : '—'; }

function showLoading(on) {
    document.getElementById('loading-overlay')?.classList.toggle('active', on);
}

function spinRefresh(on) {
    document.getElementById('btn-refresh')?.classList.toggle('spinning', on);
}

loadAll();