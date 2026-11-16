const API_URL = '/students';

const form = document.getElementById('student-form');
const formTitle = document.getElementById('form-title');
const idInput = document.getElementById('student-id');
const nombreInput = document.getElementById('nombre');
const correoInput = document.getElementById('correo');
const telefonoInput = document.getElementById('telefono');
const idiomaInput = document.getElementById('idioma');

const btnSubmit = document.getElementById('btn-submit');
const btnPatch = document.getElementById('btn-patch');
const btnCancel = document.getElementById('btn-cancel');
const tbody = document.getElementById('students-tbody');
const alertBox = document.getElementById('alert-box');

let originalStudent = null;

document.addEventListener('DOMContentLoaded', loadStudents);

function showAlert(message, type = 'success') {
    alertBox.textContent = message;
    alertBox.className = `alert alert-${type}`;
    setTimeout(() => alertBox.classList.add('hidden'), 4500);
}

function getFormData() {
    return {
        nombre: nombreInput.value.trim(),
        correo: correoInput.value.trim(),
        numero_telefono: telefonoInput.value.trim(),
        idioma: idiomaInput.value
    };
}

async function loadStudents() {
    try {
        const res = await fetch(API_URL);
        const students = await res.json();
        renderTable(students);
    } catch (err) {
        showAlert('Error al conectar con el servidor.', 'error');
    }
}

function renderTable(students) {
    if (!students || students.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-msg">No hay estudiantes registrados.</td></tr>';
        return;
    }

    tbody.innerHTML = students.map(s => {
        const tel = s.numero_telefono || s.numeroTelefono || '';
        return `
            <tr>
                <td>${s.id}</td>
                <td>${s.nombre}</td>
                <td>${s.correo}</td>
                <td>${tel}</td>
                <td>${s.idioma}</td>
                <td>
                    <button class="btn btn-warning btn-sm" onclick="editStudent(${s.id})">Editar</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteStudent(${s.id})">Eliminar</button>
                </td>
            </tr>
        `;
    }).join('');
}

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = idInput.value;
    const payload = getFormData();
    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_URL}/${id}` : API_URL;

    try {
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (!res.ok) {
            throw new Error(extractErrorMessage(data));
        }

        showAlert(id ? 'Estudiante actualizado (PUT).' : 'Estudiante registrado correctamente.');
        resetForm();
        loadStudents();
    } catch (err) {
        showAlert(err.message, 'error');
    }
});

btnPatch.addEventListener('click', async () => {
    const id = idInput.value;
    if (!id || !originalStudent) return;

    const current = getFormData();
    const patchPayload = {};

    if (current.nombre !== originalStudent.nombre) patchPayload.nombre = current.nombre;
    if (current.correo !== originalStudent.correo) patchPayload.correo = current.correo;
    const origTel = originalStudent.numero_telefono || originalStudent.numeroTelefono;
    if (current.numero_telefono !== origTel) patchPayload.numero_telefono = current.numero_telefono;
    if (current.idioma !== originalStudent.idioma) patchPayload.idioma = current.idioma;

    if (Object.keys(patchPayload).length === 0) {
        showAlert('No modificaste ningún campo para aplicar PATCH.', 'error');
        return;
    }

    try {
        const res = await fetch(`${API_URL}/${id}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(patchPayload)
        });

        const data = await res.json();
        if (!res.ok) throw new Error(extractErrorMessage(data));

        showAlert('Actualización parcial (PATCH) aplicada.');
        resetForm();
        loadStudents();
    } catch (err) {
        showAlert(err.message, 'error');
    }
});

async function editStudent(id) {
    try {
        const res = await fetch(`${API_URL}/${id}`);
        const data = await res.json();
        if (!res.ok) throw new Error(extractErrorMessage(data));

        originalStudent = data;
        idInput.value = data.id;
        nombreInput.value = data.nombre;
        correoInput.value = data.correo;
        telefonoInput.value = data.numero_telefono || data.numeroTelefono || '';
        idiomaInput.value = data.idioma;

        formTitle.textContent = `Editando Estudiante #${data.id}`;
        btnSubmit.textContent = 'Actualizar Todo (PUT)';
        btnPatch.classList.remove('hidden');
        btnCancel.classList.remove('hidden');
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteStudent(id) {
    if (!confirm(`¿Seguro que deseas eliminar al estudiante #${id}?`)) return;

    try {
        const res = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        if (!res.ok) {
            const data = await res.json();
            throw new Error(extractErrorMessage(data));
        }
        showAlert(`Estudiante #${id} eliminado.`);
        loadStudents();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

document.getElementById('btn-search').addEventListener('click', async () => {
    const id = document.getElementById('search-id').value;
    if (!id) return loadStudents();

    try {
        const res = await fetch(`${API_URL}/${id}`);
        const data = await res.json();
        if (!res.ok) throw new Error(extractErrorMessage(data));
        renderTable([data]);
    } catch (err) {
        showAlert(err.message, 'error');
        renderTable([]);
    }
});

document.getElementById('btn-reset-search').addEventListener('click', () => {
    document.getElementById('search-id').value = '';
    loadStudents();
});

btnCancel.addEventListener('click', resetForm);

function resetForm() {
    form.reset();
    idInput.value = '';
    originalStudent = null;
    formTitle.textContent = 'Registrar Nuevo Estudiante';
    btnSubmit.textContent = 'Guardar Estudiante';
    btnPatch.classList.add('hidden');
    btnCancel.classList.add('hidden');
}

function extractErrorMessage(data) {
    if (typeof data === 'string') return data;
    if (data.message) return data.message;
    if (data.error) return data.error;
    return Object.values(data).join(' | ');
}