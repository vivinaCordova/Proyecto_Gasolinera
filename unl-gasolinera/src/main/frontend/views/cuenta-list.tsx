import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import {
  Button,
  ComboBox,
  DatePicker,
  Dialog,
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



type PrecioEstablecido = {
  estado: string;
};

type CuentaEntryFormProps = {
  onCuentaCreated?: () => void;
};

//GUARDAR CUENTA
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
      if (correo.value.trim().length > 0 && clave.value.trim().length > 0) {
        const id_usuario = parseInt(usuario.value) + 1;
        await CuentaService.createCuenta(correo.value, clave.value,  estado.value, id_usuario);
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
    CuentaService.listaPersonaCombo().then(data => 
      listaPersona.value = data
    );
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
          <TextField
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

function indexIndex({model}:{model:GridItemModel<Cuenta>}) {
  return (
    <span>
      {model.index + 1} 
    </span>
  );
}

function renderEstado({ model }: { model: GridItemModel<Cuenta> }) {
  return <span>{model.item.estado ? 'Activo' : 'Inactivo'}</span>;
}

//LISTA DE CUENTAS
export default function CuentaView() {
  const [items, setItems] = useState([]);
  const callData= () => {
    CuentaService.listAll().then(function(data){
      setItems(data);
    });
  };
  useEffect(() => {
    callData();
  },[]);

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
        <Select items={itemSelect}
          value={criterio.value}
          onValueChanged={(evt) => (criterio.value = evt.detail.value)}
          placeholder="Selecione un criterio">


        </Select>

        <TextField
          placeholder="Search"
          style={{ width: '50%' }}
          value={texto.value}
          onValueChanged={(evt) => (texto.value = evt.detail.value)}
        >
          <Icon slot="prefix" icon="vaadin:search" />
        </TextField>
        <Button onClick={search} theme="primary">
          BUSCAR
        </Button>
      </HorizontalLayout>
      <Grid items={items}>
        <GridColumn renderer={indexIndex} header="Nro" />
        <GridSortColumn path="correo" header="Correos" onDirectionChanged={(e) => order(e, "correo")}/>
            <GridSortColumn path="estado" onDirectionChanged={(e) => order(e, 'estado')} header="Estado" renderer={renderEstado} />
        <GridSortColumn path="usuario" header="Usuario"onDirectionChanged={(e) => order(e, "usuario")}/>
      </Grid>
    </main>
  );
  
}
