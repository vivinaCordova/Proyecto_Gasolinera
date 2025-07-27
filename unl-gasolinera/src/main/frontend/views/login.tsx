import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { useSignal } from '@vaadin/hilla-react-signals';
import { useAuth, isLogin } from "Frontend/security/auth";
import { CuentaService } from 'Frontend/generated/endpoints';
import { useNavigate, useSearchParams } from 'react-router';
import { useEffect, useState } from 'react';
import '../styles/login.css'; // El estilo lo definiremos igual que en otras vistas

export const config: ViewConfig = {
  skipLayouts: true,
  menu: {
    exclude: true
  }
};

export default function LoginView() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [searchParams] = useSearchParams();
  const hasError = useSignal(false);

  const [email, setEmail] = useState('');
  const [clave, setClave] = useState('');

  useEffect(() => {
    isLogin().then((data) => {
      if (data === true) {
        navigate('/');
      }
    });
  }, []);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    CuentaService.login(email, clave).then(async (data) => {
      if (data?.estado === 'false') {
        hasError.value = true;
        return;
      } else {
        const { error } = await login(email, clave);
        hasError.value = Boolean(error);
        const dato = await CuentaService.isLogin();
        console.log(dato);
        window.location.reload();
        navigate("/", { replace: true });
      }
    });
  };

  return (
    <div className="login-fondo">
      <div className="login-overlay"></div>

      <div className="login-container">
        <h2>Inicio de sesión</h2>
        <form onSubmit={handleLogin}>
          <input
            type="email"
            placeholder="Correo electrónico"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <input
            type="password"
            placeholder="Clave"
            value={clave}
            onChange={(e) => setClave(e.target.value)}
            required
          />

          {hasError.value && <p className="error">Usuario o clave incorrecta</p>}

          <button type="submit">Ingresar</button>
        </form>

        <p>¿No tienes cuenta? <a href="/register">Regístrate aquí</a></p>
      </div>
    </div>
  );
}
