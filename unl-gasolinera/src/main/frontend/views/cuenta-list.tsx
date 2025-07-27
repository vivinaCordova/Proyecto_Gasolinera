import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import {
  Button,
  ComboBox,
  Dialog,
  EmailField,
  Grid,
  GridColumn,
  GridItemModel,
  GridSortColumn,
  HorizontalLayout,
  Icon,
  PasswordField,
  Select,
  TextField,
  VerticalLayout,
} from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { CuentaService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import Cuenta from 'Frontend/generated/org/unl/gasolinera/base/models/Cuenta';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect, useState } from 'react';
import { listaPersonaCombo } from 'Frontend/generated/CuentaService';
import { role } from 'Frontend/security/auth';
import { logout } from '@vaadin/hilla-frontend';

export const config: ViewConfig = {
  title: 'Cuentas',
  menu: {
    icon: 'vaadin:archive',
    order: 2,
    title: 'Cuentas',
  },
};

type CuentaEntryFormProps = {
  onCuentaCreated?: () => void;
};

//Guardar Cuenta
function CuentaEntryForm(props: CuentaEntryFormProps) {
  useEffect(() => {
    role().then(async (data) => {
      if (data?.rol != 'ROLE_admin') {
        await CuentaService.logout();
        await logout();
      }
    });
  }, []);
  const correo = useSignal('');
  const clave = useSignal('');
  const usuario = useSignal('');
  const estado = useSignal('');
  const createCuenta = async () => {
    try {
      if (correo.value.trim().length > 0 && clave.value.trim().length > 0, estado.value !== '') {
        const yaExiste = await CuentaService.isCreated(correo.value);
        if (yaExiste) {
          Notification.show('El correo electrónico ya está registrado.', {
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
        const id_usuario = parseInt(usuario.value) + 1;
        await CuentaService.createCuenta(correo.value, clave.value, estado.value, id_usuario);
        if (props.onCuentaCreated) {
          props.onCuentaCreated();
        }
        correo.value = '';
        clave.value = '';
        estado.value = '';
        usuario.value = '';

        dialogOpened.value = false;
        Notification.show('Cuenta creada', { duration: 3000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }
    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };

  let listaPersona = useSignal<String[]>([]);
  useEffect(() => {
    CuentaService.listaPersonaCombo().then((data) => (listaPersona.value = data));
  }, []);
  const dialogOpened = useSignal(false);
  return (
    <>
      <Dialog
        modeless
        headerTitle="Nueva Cuenta"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => {
          dialogOpened.value = detail.value;
        }}
        footer={
          <>
            <Button
              onClick={() => {
                dialogOpened.value = false;
              }}>
              Cancelar
            </Button>
            <Button onClick={createCuenta} theme="primary">
              Registrar
            </Button>
          </>
        }>
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <EmailField
            label="Correo"
            placeholder="Ingrese un correo"
            aria-label="Correo"
            value={correo.value}
            onValueChanged={(evt) => (correo.value = evt.detail.value)}
          />
          <PasswordField
            label="Clave"
            placeholder="Ingrese una clave"
            aria-label="Clave"
            value={clave.value}
            onValueChanged={(evt) => (clave.value = evt.detail.value)}
          />
          <ComboBox
            label="Estado"
            placeholder="Ingrese el estado"
            aria-label="Estado"
            items={[
              { label: 'Activo', value: true },
              { label: 'Inactivo', value: false },
            ]}
            value={estado.value}
            onValueChanged={(e) => (estado.value = e.detail.value)}
          />
          <ComboBox
            label="Usuario"
            items={listaPersona.value}
            placeholder="Seleccione una persona"
            aria-label="Seleccione una persona de la lista"
            value={usuario.value}
            onValueChanged={(evt) => (usuario.value = evt.detail.value)}
          />
        </VerticalLayout>
      </Dialog>
      <Button
        onClick={() => {
          dialogOpened.value = true;
        }}>
        Agregar
      </Button>
    </>
  );
}

function indexIndex({ model }: { model: GridItemModel<Cuenta> }) {
  return <span>{model.index + 1}</span>;
}

function renderEstado({ model }: { model: GridItemModel<Cuenta> }) {
  return <span>{model.item.estado ? 'Activo' : 'Inactivo'}</span>;
}

//Vista para las Cuentas
export default function CuentaView() {
  const [items, setItems] = useState([]);
  const callData = () => {
    CuentaService.listAll().then(function (data) {
      setItems(data);
    });
  };
  useEffect(() => {
    callData();
  }, []);

  const deleteCuenta = async (cuenta: Cuenta) => {
    // Mostrar confirmación antes de eliminar
    const confirmed = window.confirm(`¿Está seguro de que desea eliminar la cuenta con correo ${cuenta.correo}?`);

    if (confirmed) {
      try {
        await CuentaService.delete(cuenta.id);
        Notification.show('Cuenta eliminada exitosamente', {
          duration: 5000,
          position: 'bottom-end',
          theme: 'success'
        });
        // Recargar la lista después de eliminar
        callData();
      } catch (error) {
        console.error('Error al eliminar la cuenta:', error);
        Notification.show('Error al eliminar la cuenta', {
          duration: 5000,
          position: 'top-center',
          theme: 'error'
        });
      }
    }
  }

  const order = (event, columnId) => {
    console.log(event);
    const direction = event.detail.value;
    console.log(`Sort direction changed for column ${columnId} to ${direction}`);
    var dir = direction == 'asc' ? 1 : 2;
    CuentaService.order(columnId, dir).then(function (data) {
      setItems(data);
    });
  };

  const criterio = useSignal('');
  const texto = useSignal('');
  const itemSelect = [
    {
      label: 'Correo',
      value: 'correo',
    },
    {
      label: 'Usuario',
      value: 'usuario',
    },
    {
      label: 'Estado',
      value: 'estado',
    },
  ];
  const search = async () => {
    try {
      console.log(criterio.value + ' ' + texto.value);
      CuentaService.search(criterio.value, texto.value, 0).then(function (data) {
        setItems(data);
      });

      criterio.value = '';
      texto.value = '';

      Notification.show('Busqueda realizada', { duration: 5000, position: 'bottom-end', theme: 'success' });
    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Lista de cuentas">
        <Group>
          <CuentaEntryForm onCuentaCreated={callData} />
        </Group>
      </ViewToolbar>
      <HorizontalLayout theme="spacing">
        <Select
          items={itemSelect}
          value={criterio.value}
          onValueChanged={(evt) => (criterio.value = evt.detail.value)}
          placeholder="Selecione un criterio">
        </Select>
        {criterio.value === 'estado' ? (
          <Select
            items={[
              { label: 'Activo', value: 'true' },
              { label: 'Inactivo', value: 'false' },
            ]}
            value={texto.value}
            onValueChanged={(evt) => (texto.value = evt.detail.value)}
            placeholder="Seleccione el estado"
            style={{ width: '50%' }}
          />
        ) : (
          <TextField
            placeholder="Search"
            style={{ width: '50%' }}
            value={texto.value}
            onValueChanged={(evt) => (texto.value = evt.detail.value)}>
            <Icon slot="prefix" icon="vaadin:search" />
          </TextField>
        )}
        <Button onClick={search} theme="primary">
          BUSCAR
        </Button>
        <Button onClick={callData} theme="secondary">
          <Icon icon="vaadin:refresh" />
        </Button>
      </HorizontalLayout>
      <Grid items={items}>
        <GridColumn renderer={indexIndex} header="Nro" />
        <GridSortColumn path="correo" header="Correos" onDirectionChanged={(e) => order(e, 'correo')} />
        <GridSortColumn path="estado" onDirectionChanged={(e) => order(e, 'estado')} header="Estado" renderer={renderEstado} />
        <GridSortColumn path="usuario" header="Usuario" onDirectionChanged={(e) => order(e, 'usuario')} />
        {/* <GridColumn
          header="Eliminar"
          renderer={({ item }) => (
            <Button
              theme="error"
              onClick={() => deleteCuenta(item)}
            >
              Eliminar
            </Button>
          )}
        /> */}
      </Grid>
    </main>
  );
}
