import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { isLogin } from 'Frontend/security/auth';

export const config = {
  skipLayouts: true,
  loginRequired: false,
  menu: {
    exclude: true
  }
};

export default function IndexRedirect() {
  const navigate = useNavigate();

  useEffect(() => {
    isLogin().then((logueado) => {
      if (logueado) {
        navigate('/ordendespacho-list');
      } else {
        navigate('/bienvenida-list');
      }
    });
  }, []);

  return null;
}
