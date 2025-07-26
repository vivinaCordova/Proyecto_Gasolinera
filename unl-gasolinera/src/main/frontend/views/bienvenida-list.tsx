import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/bienvenida.css';

export const config: ViewConfig = {
  loginRequired: false,
  skipLayouts: true,
  menu: { exclude: true },
};

export default function BienvenidaView() {
  const navigate = useNavigate();

  useEffect(() => {
    const audio = new Audio('/audio/Bienvenida.mp3');
    audio.play().catch(() => console.warn("Bloqueo de reproducción automática."));
  }, []);

  const handleIniciar = () => navigate('/login');
  const handleRegister = () => navigate('/register');

  return (
    <div className="bienvenida-fondo">
      <div className="overlay"></div>

      <header className="barra-superior">
        <span className="emoji-inicio">⛽</span>
        <span className="titulo-barra">Gasolinera Universidad Nacional de Loja</span>
      </header>

      <div className="contenido">
        <h1 className="titulo">¡Bienvenido!</h1>
        <p className="descripcion">
          Gracias por elegir nuestra gasolinera UNL. Aquí encontrarás eficiencia, confianza
          y el mejor servicio para tu vehículo.
        </p>
        <button className="boton-registrarse" onClick={handleRegister}>
          Registrarse
        </button>
        <button className="boton-iniciar" onClick={handleIniciar}>
          Iniciar Sesión
        </button>
      </div>
    </div>
  );
}
