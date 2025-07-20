import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, GridSortColumn, HorizontalLayout, Icon, Select, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { CuentaService, ProveedorService, TaskService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import Proveedor from 'Frontend/generated/org/unl/gasolinera/taskmanagement/domain/Task';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect, useState } from 'react';
import { role } from 'Frontend/security/auth';
import { logout } from '@vaadin/hilla-frontend';

export const config: ViewConfig = {
  title: 'Proveedor',
  menu: {
    icon: 'vaadin:truck',
    order: 1,
    title: 'Proveedor',
  },
};

type ProveedorEntryFormProps = {
  onProveedorCreated?: () => void;
};
//GUARDAR Proveedor
function ProveedorEntryForm(props: ProveedorEntryFormProps) {
  useEffect(() => {
      role().then(async (data) => {
        if (data?.rol != 'ROLE_admin') {
          await CuentaService.logout();
          await logout();
        }
      });
    }, []);
  const nombre = useSignal('');
  const correoElectronico = useSignal('');
  const tipoCombustible = useSignal('');
  const createProveedor = async () => {
    try {
      if (nombre.value.trim().length > 0 && correoElectronico.value.trim().length > 0 && tipoCombustible.value.trim().length > 0){
        await ProveedorService.createProveedor((nombre.value),(correoElectronico.value),(tipoCombustible.value));
        if (props.onProveedorCreated) {
          props.onProveedorCreated();
        }
        nombre.value = '';
        correoElectronico.value = '';
        tipoCombustible.value = '';
        dialogOpened.value = false;
        Notification.show('Proveedor creado', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }

    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };
  
  let listaTipoCombustible= useSignal<String[]>([]);
  useEffect(() => {
  ProveedorService.listTipo().then(data =>
      listaTipoCombustible.value = data
    );
  }, []);

  const dialogOpened = useSignal(false);
  return (
    <>
      <Dialog
        modeless
        headerTitle="Nuevo Proveedor"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => {
          dialogOpened.value = detail.value;
        }}
        footer={
          <>
            <Button
              onClick={() => {
                dialogOpened.value = false;
              }}
            >
              Cancelar
            </Button>
            <Button onClick={createProveedor} theme="primary">
              Registrar
            </Button>
            
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField label="Nombre del Proveedor" 
            placeholder="Ingrese el nombre del Proveedor"
            aria-label="Nombre del Proveedor"
            value={nombre.value}
            onValueChanged={(evt) => (nombre.value = evt.detail.value)}
            />
          <TextField label="Correo Electronico" 
            placeholder='Ingrese Correo Electronico'
            aria-label='Ingrese Correo Electronico'
            value={correoElectronico.value}
            onValueChanged={(evt) => (correoElectronico.value = evt.detail.value)}
            />
            <ComboBox label="Tipo" 
            items={listaTipoCombustible.value}
            placeholder='Seleccione un tipo'
            aria-label='Seleccione un tipo de combustible'
            value={tipoCombustible.value}
            onValueChanged={(evt) => (tipoCombustible.value = evt.detail.value)}
            /> 
        </VerticalLayout>
      </Dialog>
      <Button
            onClick={() => {
              dialogOpened.value = true;
            }}
          >
            Agregar
          </Button>
    </>
  );
}

//LISTA DE ProveedorS
export default function ProveedorView() {
  const [items, setItems] = useState([]);
  //useEffect(() =>{
  const callData = () => {
    ProveedorService.listAll().then(function (data) {
      //items.values = data;
      setItems(data);
    });
  };
  useEffect(() => {
    callData();
  }, []);
  const order = (event, columnId) => {
    console.log(event);
    const direction = event.detail.value;
    console.log(`Sort direction changed for column ${columnId} to ${direction}`);

    var dir = (direction == 'asc') ? 1 : 2;
    ProveedorService.order(columnId, dir).then(function (data) {
      setItems(data);
    });
  }
  function indexIndex({ model }: { model: GridItemModel<Proveedor> }) {
    return (
      <span>
        {model.index + 1}
      </span>
    );
  }
  const criterio = useSignal('');
  const texto = useSignal('');
  const itemSelect = [
    {
      label: 'Nombre',
      value: 'nombre',
    },
    {

      label: 'Correo Electronico',
      value: 'correoElectronico',
    },
    {

      label: 'Tipo de Combustible',
      value: 'tipo',
    },

  ]
  const search = async () => {
    try {
      ProveedorService.search(criterio.value, texto.value, 0).then(function (data) { 
        setItems(data);
      });
      criterio.value = '';
      texto.value = '';
      Notification.show('busqueda realizada', { duration: 5000, position: 'bottom-end', theme: 'success' });

    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };
  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Lista de Proveedores">
        <Group>
          <ProveedorEntryForm onProveedorCreated={callData}/>
        </Group>
      </ViewToolbar>
      <HorizontalLayout theme="spacing">
        <Select items={itemSelect}
          value={criterio.value}
          onValueChanged={(evt) => (criterio.value = evt.detail.value)}
          placeholder="seleccione criterio">

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
          Buscar
        </Button>
      </HorizontalLayout>
      <Grid items={items}>
        <GridColumn  renderer={indexIndex} header="Nro" />
        <GridSortColumn path="nombre" header="Nombre" onDirectionChanged={(e) => order(e, 'nombre')}/>
        <GridSortColumn path="tipoCombustible" header="Tipo"onDirectionChanged={(e) => order(e, 'nombre')}/>
        <GridColumn path="correoElectronico" header="Correo Electronico">
        

        </GridColumn>
      </Grid>
    </main>
  );
}
