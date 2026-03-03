function openModal(id) {
    fetch(`/admin/SeuServlet?action=get&id=${id}`, {
        method: 'GET'
    })
        .then(response => response.json())
        .then(dados => {
            // Preenche o modal com os dados recebidos
            document.getElementById('editId').value = dados.id;
            document.getElementById('editNome').value = dados.nome;
            document.getElementById('editIdade').value = dados.idade;
            document.getElementById('editEmail').value = dados.email;

            // Abre o modal
            document.getElementById('modalEdit').showModal();
        })
        .catch(error => console.error('Erro ao buscar dados:', error));
}

// UPDATE - Atualizar
function atualizar() {
    const id = document.getElementById('editId').value;
    const dados = {
        nome: document.getElementById('editNome').value,
        idade: document.getElementById('editIdade').value,
        email: document.getElementById('editEmail').value
    };

    fetch('/seuapp/SeuServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `action=update&id=${id}&nome=${dados.nome}&idade=${dados.idade}&email=${dados.email}`
    })
        .then(response => response.text())
        .then(data => {
            alert('Atualizado com sucesso!');
            location.reload();
        });
}