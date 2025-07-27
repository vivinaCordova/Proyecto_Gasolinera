import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, Dialog, EmailField, PasswordField, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { CuentaService, PersonaService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import { useAuth, AuthProvider, isLogin } from 'Frontend/security/auth';
import { useNavigate, useSearchParams } from 'react-router';
import { useEffect, useState } from 'react';
import handleError from './_ErrorHandler';
import { Email } from '@vaadin/hilla-lit-form';
import { login } from 'Frontend/generated/CuentaService';
import { Link } from 'react-router-dom';

export const config: ViewConfig = {
  skipLayouts: true,
  menu: {
    exclude: true,
  },
};

export default function CuentaView() {
  useEffect(() => {
    isLogin().then(data => {
      if (data === true) {
        navigate('/');
      }
      console.log(data + " -- ");
    });
  }, []);

  const usuario = useSignal('');
  const cedula = useSignal('');
  const correo = useSignal('');
  const clave = useSignal('');
  const navigate = useNavigate();

  const crearCuenta = async () => {
    try {
      if (usuario.value.trim() && cedula.value.trim() && correo.value.trim() && clave.value.trim()) {
        const isCreated = await PersonaService.isCreated(cedula.value);
        const isUser = await PersonaService.isUser(usuario.value);
        const existEmail = await CuentaService.isCreated(correo.value);

        if (isCreated) {
          Notification.show('La cédula ya está registrada', {
            duration: 5000,
            position: 'top-center',
            theme: 'error',
          });
          return;
        }
        if (existEmail) {
          Notification.show('Este correo ya está registrado', {
            duration: 5000,
            position: 'top-center',
            theme: 'error',
          });
          return;
        }
        if (isUser) {
          Notification.show('Este usuario ya está registrado', {
            duration: 5000,
            position: 'top-center',
            theme: 'error',
          });
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

        await PersonaService.createRegistro(usuario.value, cedula.value, correo.value, clave.value);

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
    <main className="flex items-center justify-center h-screen">
      <VerticalLayout
        theme="spacing"
        style={{
          width: '22rem',
          padding: '2rem',
          border: '1px solid #ccc',
          borderRadius: '8px',
          boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
        }}>
        <h2>
        Crea una Cuenta
        </h2>

        <TextField
          label="Usuario"
          placeholder="Ingrese su usuario"
          value={usuario.value}
          onValueChanged={(e) => (usuario.value = e.detail.value)}
          style={{ width: '100%' }} 
        />

        <TextField
          label="Cédula"
          placeholder="Ingrese su número de cédula"
          value={cedula.value}
          onValueChanged={(e) => (cedula.value = e.detail.value)}
          style={{ width: '100%' }}
        />

        <EmailField
          label="Correo electrónico"
          placeholder="ejemplo@correo.com"
          value={correo.value}
          onValueChanged={(e) => (correo.value = e.detail.value)}
          style={{ width: '100%' }}
        />

        <PasswordField
          label="Contraseña"
          placeholder="Cree una contraseña segura"
          value={clave.value}
          onValueChanged={(e) => (clave.value = e.detail.value)}
          style={{ width: '100%' }}
        />

        <Button theme="primary" onClick={crearCuenta}style={{ display: 'block', margin: '0 auto' }}>
          Registrar Cuenta
        </Button>
        <p style={{ marginTop: '1rem', textAlign: 'center' }}>
          ¿Ya tienes una cuenta? <Link to="/login">Inicia sesión aquí</Link>
        </p>
      </VerticalLayout>
    </main>
  );
}
