// URL de tu API en Render
const API_URL = 'https://tienda-app-backend-habm.onrender.com/api/v1/tienda';

let isLogin = true;

const form = document.getElementById('auth-form');
const formTitle = document.getElementById('form-title');
const submitBtn = document.getElementById('submit-btn');
const toggleLink = document.getElementById('toggle-link');
const toggleMsg = document.getElementById('toggle-msg');
const messageDiv = document.getElementById('message');

// Cambiar entre Login y Registro
toggleLink.addEventListener('click', (e) => {
    e.preventDefault();
    isLogin = !isLogin;
    
    if (isLogin) {
        formTitle.textContent = 'Iniciar Sesión';
        submitBtn.textContent = 'Entrar';
        toggleMsg.textContent = '¿No tienes cuenta?';
        toggleLink.textContent = 'Regístrate aquí';
    } else {
        formTitle.textContent = 'Registrarse';
        submitBtn.textContent = 'Crear Cuenta';
        toggleMsg.textContent = '¿Ya tienes cuenta?';
        toggleLink.textContent = 'Inicia sesión aquí';
    }
    messageDiv.className = 'message hidden';
});

// Enviar el formulario
form.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const user = document.getElementById('user').value;
    const password = document.getElementById('password').value;
    
    const endpoint = isLogin ? '/auth/login' : '/auth/register';
    
    // Mostrar mensaje de "Cargando..."
    messageDiv.textContent = 'Conectando con el servidor... (puede tardar 50s si está dormido)';
    messageDiv.className = 'message';
    messageDiv.style.background = '#fff3cd';
    messageDiv.style.color = '#856404';

    try {
        const response = await fetch(`${API_URL}${endpoint}`, {
            method: 'POST',
            mode: 'cors', // <--- IMPORTANTE: Modo CORS para permitir peticiones desde el navegador
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ user, password })
        });

        const data = await response.json();

        if (response.ok) {
            messageDiv.textContent = isLogin ? 'Login exitoso. Redirigiendo...' : 'Registro exitoso. Redirigiendo...';
            messageDiv.className = 'message success';
            messageDiv.style.background = '#d4edda';
            messageDiv.style.color = '#155724';
            
            if (data.data && data.data.jwt) {
                localStorage.setItem('token', data.data.jwt);
                localStorage.setItem('username', data.data.username);
            }

            setTimeout(() => {
                window.location.href = 'dashboard.html';
            }, 1500);

        } else {
            messageDiv.textContent = data.message || 'Error en la petición';
            messageDiv.className = 'message error';
            messageDiv.style.background = '#f8d7da';
            messageDiv.style.color = '#721c24';
        }
    } catch (error) {
        // Si hay un error de CORS (porque el servidor no tiene los headers configurados)
        messageDiv.textContent = 'Error de conexión. El servidor puede estar dormido (espera 50s) o hay un problema de CORS.';
        messageDiv.className = 'message error';
        messageDiv.style.background = '#f8d7da';
        messageDiv.style.color = '#721c24';
        console.error('Error detallado:', error);
    }
});