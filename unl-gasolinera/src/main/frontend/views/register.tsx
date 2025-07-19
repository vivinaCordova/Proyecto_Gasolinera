import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, LoginForm, LoginOverlay, TextField, PasswordField } from '@vaadin/react-components';
import { CuentaService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import {  useAuth, AuthProvider, isLogin} from "Frontend/security/auth";
import { useNavigate, useSearchParams } from 'react-router';
import { useEffect, useState } from 'react';


/*export const config: ViewConfig = {
    skipLayouts: true,
    menu: {
      exclude: true
    }
}

export default function RegisterView() {
   console.log("REGISTER");

  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handleRegister = async () => {
    if (password !== confirmPassword) {
      alert('Las contraseñas no coinciden');
      return;
    }

    try {
      await CuentaService.createCuenta(email, password, true, 1); // Ajusta los parámetros según tu lógica
      alert('Cuenta creada exitosamente');
      navigate('/login');
    } catch (error) {
      console.error('Error al crear la cuenta:', error);
      alert('No se pudo crear la cuenta');
    }
  };

  return (
    <main className="flex flex-col justify-center items-center w-full h-full gap-m">
      <h1>Registro</h1>
      <TextField label="Correo electrónico" value={email} onChange={(e) => setEmail(e.target.value)} />
      <PasswordField label="Contraseña" value={password} onChange={(e) => setPassword(e.target.value)} />
      <PasswordField
        label="Confirmar contraseña"
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.target.value)}
      />
      <Button theme="primary" onClick={handleRegister}>
        Crear cuenta
      </Button>
    </main>
  );
}*/