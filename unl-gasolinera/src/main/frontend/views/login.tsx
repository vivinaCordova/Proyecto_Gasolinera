import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, DatePicker, Grid, GridColumn, LoginForm, LoginOverlay, TextField } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { CuentaService, TaskService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import {  useAuth, AuthProvider, isLogin} from "Frontend/security/auth";
import { useNavigate, useSearchParams } from 'react-router';
import { useEffect } from 'react';

export const config: ViewConfig = {
    skipLayouts: true,
    menu: {
      exclude: true
    }
}

export default function LoginView() {
  console.log("LOGIN");
  const navigate = useNavigate();

  useEffect(() => {
    isLogin().then(data => {
      if (data === true) {
        navigate('/');
      }
      console.log(data + " -- ");
    });
  }, []);

  const { state, login } = useAuth();
  const  [searchParams]  = useSearchParams();
  const hasError = useSignal(false);
  const errores = searchParams.has("error");

  const i18n = {
    header: {
      title: 'Gasolinera UNL',
      description: 'Servicio más rápido y eficiente',
    },
    form: {
      title: 'Inicio de sesión',
      username: 'Correo electrónico',
      password: 'Clave',
      submit: 'Ingresar',
      forgotPassword: '¿Olvidaste tu clave?',
    },
    errorMessage: {
      title: 'Error',
      message: 'Usuario o clave incorrecta',
      username: 'Usuario incorrecto',
      password: 'Clave incorrecta',
    },
    additionalInformation: 'Obtén un mejor servicio',
  };

  useEffect(() => {
    CuentaService.createRoles().then(() => {
      hasError.value = false;
    });
  }, []);

  return (
    <main className="flex justify-center items-center w-full h-full">
      <LoginOverlay opened i18n={i18n} error={errores} noForgotPassword
        onErrorChanged={(event) => {
          console.log(event);
          hasError.value = event.detail.value;
        }}
        onLogin={
          async ({ detail: { username, password } }) => {
          console.log(username + " " + password);
          CuentaService.login(username, password).then(async function(data) {
            console.log(data);
            if (data?.estado === 'false') {
              
              hasError.value = Boolean("true");
              navigate('/login?error');
            } else {
              const { error } = await login(username, password);
              hasError.value = Boolean(error);
              const dato = await CuentaService.isLogin();
              console.log(dato);
              window.location.reload();
              navigate("/", { replace: true });
            }
          });
        }}
      />
    </main>
  );
}