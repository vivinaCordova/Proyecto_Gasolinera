import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { ProveedorService} from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import Proveedor from 'Frontend/generated/org/unl/gasolinera/taskmanagement/domain/Task';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect } from 'react';

export const config: ViewConfig = {
  title: 'Proveedor',
  menu: {
    icon: 'vaadin:clipboard-check',
    order: 1,
    title: 'Proveedor',
  },
};
type ProveedorEntryFormProps = {
  onProveedorCreated?: () => void;
};
//GUARDAR Proveedor
function ProveedorEntryForm(props: ProveedorEntryFormProps) {
  const nombre = useSignal('');
  const correoElectronico = useSignal('');
  const tipoCombustible = useSignal('');
  const createProveedor = async () => {
    try {
      if (nombre.value.trim().length > 0 && correoElectronico.value.trim().length > 0 && tipoCombustible.value.trim().length > 0){
        //const id_ordenCompra = parseInt(ordenCompra.value)+1;
        //const id_OrdenDespacho = parseInt(ordenDespacho.value)+1;
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
              Candelar
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
          <ComboBox label="Correo Electronico" 
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
  
  const dataProvider = useDataProvider<Proveedor>({ 
    list: () => ProveedorService.listProveedor(),
  });

  function indexIndex({model}:{model:GridItemModel<Proveedor>}) {
    return (
      <span>
        {model.index + 1} 
      </span>
    );
  }

  return (

    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Lista de Proveedores">
        <Group>
          <ProveedorEntryForm onProveedorCreated={dataProvider.refresh}/>
        </Group>
      </ViewToolbar>
      <Grid dataProvider={dataProvider.dataProvider}>
        <GridColumn  renderer={indexIndex} header="Nro" />
        <GridColumn path="nombre" header="Nombre" />
        <GridColumn path="correoElectronico" header="Correo Electronico"/>
        <GridColumn path="tipo" header="Tipo">

        </GridColumn>
      </Grid>
    </main>
  );
}