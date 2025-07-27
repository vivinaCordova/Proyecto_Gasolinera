import { Outlet } from 'react-router';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Suspense } from 'react';
import { ProgressBar } from '@vaadin/react-components/ProgressBar';

export const config: ViewConfig = {
  loginRequired: false, // Es público
};

export default function PublicLayout() {
  return (
    <Suspense fallback={<ProgressBar indeterminate className="m-0" />}>
      <Outlet />
    </Suspense>
  );
}
