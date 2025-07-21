import { Outlet } from 'react-router';
import { Suspense } from 'react';
import { ProgressBar } from '@vaadin/react-components/ProgressBar';

export const config = {
  loginRequired: false
};

export default function UsuarioLayout() {
  return (
    <Suspense fallback={<ProgressBar indeterminate className="m-0" />}>
      <Outlet />
    </Suspense>
  );
}
