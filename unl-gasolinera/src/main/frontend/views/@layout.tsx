import { Outlet, useLocation, useNavigate } from 'react-router';
import {
  AppLayout,
  Avatar,
  Icon,
  MenuBar,
  MenuBarItemSelectedEvent,
  ProgressBar,
  Scroller,
  SideNav,
  SideNavItem,
} from '@vaadin/react-components';
import { Suspense, useEffect, useState } from 'react';
import { createMenuItems } from '@vaadin/hilla-file-router/runtime.js';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { useAuth } from 'Frontend/security/auth';
import { CuentaService } from 'Frontend/generated/endpoints';

function Header() {
  // TODO Replace with real application logo and name
  return (
    <div className="flex p-m gap-m items-center" slot="drawer">
      <Icon icon="vaadin:cubes" className="text-primary icon-l" />
      <span className="font-semibold text-l">Unl Gasolinera</span>
    </div>
  );
}

function MainMenu({ username, setUsername }: { username: string | null; setUsername: React.Dispatch<React.SetStateAction<string | null>> }) {
  const navigate = useNavigate();
  const location = useLocation();
  const [role, setRole] = useState<string | null>(null);

  useEffect(() => {
    CuentaService.view_rol().then((response) => {
      const userRole = response?.rol?.trim() ?? null;
      const userName = response?.usuario?.trim() ?? null; 
      console.log('Rol obtenido:', userRole); 
      console.log('Usuario obtenido:', userName); 
      setRole(userRole);
      setUsername(userName);
    }).catch((error) => {
      console.error('Error al obtener el rol y usuario:', error);
    });
  }, [setUsername]);

  const adminMenuItems = [
    { to: '/cuenta-list', icon: 'vaadin:users', title: 'Cuentas' },
    { to: '/estacion-list', icon: 'vaadin:map-marker', title: 'Estaciones' },
    { to: '/ordenCompra-list', icon: 'vaadin:clipboard', title: 'Orden de Compra' },
    { to: '/ordendespacho-list', icon: 'vaadin:clipboard-check', title: 'Orden de Despacho' },
    { to: '/pago-list', icon: 'vaadin:list', title: 'Lista de Pagos' },
    { to: '/persona-list', icon: 'vaadin:user', title: 'Personas' },
    { to: '/precioestablecido-list', icon: 'vaadin:money', title: 'Precios Establecidos' },
    { to: '/Proveedor-list', icon: 'vaadin:truck', title: 'Proveedores' },
    { to: '/tanque-list', icon: 'vaadin:bar-chart', title: 'Tanques' },
    { to: '/vehiculo-list', icon: 'vaadin:car', title: 'Vehículos' },
    {to: '/bienvenida-list', icon: 'vaadin:home', title: 'Bienvenida' },

  ];

  const userMenuItems = [
    { to: '/ordendespacho-list', icon: 'vaadin:clipboard-check', title: 'Orden de Despacho' },
    { to: '/pago-form', icon: 'vaadin:credit-card', title: 'Formulario de Pago' },
    { to: '/pago-list', icon: 'vaadin:list', title: 'Lista de Pagos' },
    {to: '/bienvenida-list', icon: 'vaadin:home', title: 'Bienvenida' },


  ];

  const menuItems = role === 'ROLE_admin' ? adminMenuItems : userMenuItems;

  return (
    <SideNav className="mx-m" onNavigate={({ path }) => path != null && navigate(path)} location={location}>
      {menuItems.map(({ to, icon, title }) => (
        <SideNavItem path={to} key={to}>
          {icon && <Icon icon={icon} slot="prefix" />}
          {title}
        </SideNavItem>
      ))}
    </SideNav>
  );
}

function UserMenu({ username }: { username: string | null }) {
  const { logout } = useAuth();
  const items = [
    {
      component: (
        <>
          <Avatar theme="xsmall" name={username || 'Usuario'} colorIndex={5} className="mr-s" /> {username || 'Usuario'}
        </>
      ),
      children: [
        { text: 'Perfil', action: () => console.log('Perfil') },
        { text: 'Configuracion', action: () => console.log('Configuracion') },
        { text: 'Cerrar sesion', action: () => (async () => CuentaService.logout().then(async function() {
           await logout();
        }))()},
      ],
    },
  ];
  const onItemSelected = (event: MenuBarItemSelectedEvent) => {
    const action = (event.detail.value as any).action;
    if (action) {
      action();
    }
  };
  return (
    <MenuBar theme="tertiary-inline" items={items} onItemSelected={onItemSelected} className="m-m" slot="drawer" />
  );
}

//MIO
export const config: ViewConfig = {
  loginRequired: true
}


export default function MainLayout() {
  const [username, setUsername] = useState<string | null>(null);

  return (
    <AppLayout primarySection="drawer">
      <Header />
      <Scroller slot="drawer">
        <MainMenu username={username} setUsername={setUsername} />
      </Scroller>
      <UserMenu username={username} />
      <Suspense fallback={<ProgressBar indeterminate={true} className="m-0" />}>
        <Outlet />
      </Suspense>
    </AppLayout>
  );
}
