import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, Dialog, EmailField, PasswordField, TextField } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { CuentaService, PersonaService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import { useAuth, isLogin } from 'Frontend/security/auth';
import { useNavigate, useSearchParams } from 'react-router';
import { useEffect } from 'react';
import handleError from './_ErrorHandler';
import { login } from 'Frontend/generated/CuentaService';
import { Link } from 'react-router-dom';
import '../styles/registro.css';

export const config: ViewConfig = {
  skipLayouts: true,
  menu: {
    exclude: true,
  },
};

export default function CuentaView() {
  const usuario = useSignal('');
  const cedula = useSignal('');
  const correo = useSignal('');
  const clave = useSignal('');
  const placa = useSignal('');
  const modelo = useSignal('');
  const marca = useSignal('');
  const navigate = useNavigate();

  useEffect(() => {
    isLogin().then((data) => {
      if (data === true) {
        navigate('/');
      }
    });
  }, []);

  const crearCuenta = async () => {
    try {
      if (
        usuario.value.trim() &&
        cedula.value.trim() &&
        correo.value.trim() &&
        clave.value.trim() &&
        placa.value.trim() &&
        modelo.value.trim() &&
        marca.value.trim()
      ) {
        const isCreated = await PersonaService.isCreated(cedula.value);
        const isUser = await PersonaService.isUser(usuario.value);
        const existEmail = await CuentaService.isCreated(correo.value);

        if (isCreated || isUser || existEmail) {
          Notification.show(
            isCreated
              ? 'La cédula ya está registrada'
              : isUser
                ? 'Este usuario ya está registrado'
                : 'Este correo ya está registrado',
            { duration: 5000, position: 'top-center', theme: 'error' }
          );
          return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(correo.value)) {
          Notification.show('El correo no tiene un formato válido.', {
            duration: 5000,
            position: 'top-center',
            theme: 'error',
          });
          return;
        }

        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
        if (!passwordRegex.test(clave.value)) {
          Notification.show(
            'La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas y un número.',
            {
              duration: 6000,
              position: 'top-center',
              theme: 'error',
            }
          );
          return;
        }

        await PersonaService.createRegistro(
          usuario.value,
          cedula.value,
          correo.value,
          clave.value,
          placa.value,
          modelo.value,
          marca.value
        );

        const loginResult = await login(correo.value, clave.value);
        if (loginResult && !loginResult.error) {
          window.location.reload();
          navigate('/');
        } else {
          Notification.show('Error al iniciar sesión automáticamente', {
            duration: 5000,
            position: 'top-center',
            theme: 'error',
          });
        }
      } else {
        Notification.show('Todos los campos son obligatorios', {
          duration: 5000,
          position: 'top-center',
          theme: 'error',
        });
      }
    } catch (error) {
      handleError(error);
    }
  };

  return (
    <main className="registro-fondo">
      <div className="registro-overlay"></div>

      <div className="registro-container">
        <h2>Crea una Cuenta</h2>
        <div className="registro-formulario">
          <div className="columna">
            <TextField
              label="Usuario"
              placeholder="Ingrese su usuario"
              value={usuario.value}
              onValueChanged={(e) => (usuario.value = e.detail.value)}
            />
            <EmailField
              label="Correo electrónico"
              placeholder="ejemplo@correo.com"
              value={correo.value}
              onValueChanged={(e) => (correo.value = e.detail.value)}
            />

            <PasswordField
              label="Contraseña"
              placeholder="Cree una contraseña segura"
              value={clave.value}
              onValueChanged={(e) => (clave.value = e.detail.value)}
            />

          </div>
          <div className="columna">
            <TextField
              label="Cédula"
              placeholder="Ingrese su número de cédula"
              value={cedula.value}
              onValueChanged={(e) => (cedula.value = e.detail.value)}
            />
            <TextField
              label="Placa del Vehículo"
              placeholder="Ingrese la placa de su vehículo"
              value={placa.value}
              onValueChanged={(e) => (placa.value = e.detail.value)}
            />
            <TextField
              label="Modelo del Vehículo"
              placeholder="Ingrese el modelo de su vehículo"
              value={modelo.value}
              onValueChanged={(e) => (modelo.value = e.detail.value)}
            />
            <TextField
              label="Marca del Vehículo"
              placeholder="Ingrese la marca de su vehículo"
              value={marca.value}
              onValueChanged={(e) => (marca.value = e.detail.value)}
            />
          </div>
        </div>

        <Button theme="primary" onClick={crearCuenta} style={{ display: 'block', margin: '2rem auto 0 auto' }}>
          Registrar Cuenta
        </Button>

        <p>¿Ya tienes una cuenta? <Link to="/login">Inicia sesión aquí</Link></p>
      </div>
    </main>
  );
}