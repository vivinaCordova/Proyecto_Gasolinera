import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, NumberField, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { OrdenCompraService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import OrdenCompra from 'Frontend/generated/org/unl/gasolinera/taskmanagement/domain/Task';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect } from 'react';

export const config: ViewConfig = {
  title: 'Orden Compra',
  menu: {
    icon: 'vaadin:clipboard-check',
    order: 1,
    title: 'Orden Compra',
  },
};
type OrdenCompraEntryFormProps = {
  onOrdenCompraCreated?: () => void;
};
//GUARDAR OrdenCompra
function OrdenCompraEntryForm(props: OrdenCompraEntryFormProps) {
  const cantidad = useSignal('');
  const estado = useSignal('');
  const proveedor = useSignal('');
  const createOrdenCompra = async () => {
    try {
      if (cantidad.value.trim().length > 0 && estado.value.trim().length > 0 && proveedor.value.trim().length > 0){
        const idProveedor = parseInt(proveedor.value)+1;
        //const id_OrdenDespacho = parseInt(ordenDespacho.value)+1;
        await OrdenCompraService.createOrdenCompra(parseInt(cantidad.value), idProveedor,(estado.value));
        if (props.onOrdenCompraCreated) {
          props.onOrdenCompraCreated();
        }
        cantidad.value = '';
        estado.value = '';
        proveedor.value = '';
        dialogOpened.value = false;
        Notification.show('OrdenCompra creada', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }

    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };
  let listaProveedor= useSignal<String[]>([]);
  useEffect(() => {
    OrdenCompraService.listProveedorCombo().then(data =>
      listaProveedor.value = data
    );
  }, []);
  let listaEstadoOrden= useSignal<String[]>([]);
  useEffect(() => {
    OrdenCompraService.listEstado().then(data =>
      listaEstadoOrden.value = data
    );
  }, []);

  const dialogOpened = useSignal(false);
  return (
    <>
      <Dialog
        modeless
        headerTitle="Nueva OrdenCompra"
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
            <Button onClick={createOrdenCompra} theme="primary">
              Registrar
            </Button>
            
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          < NumberField label="Cantidad" 
            placeholder="Ingrese la cantidad"
            aria-label="Ingrese la cantidad"
            value={cantidad.value}
            onValueChanged={(evt) => (cantidad.value = evt.detail.value)}
            />
          <ComboBox label="Tipo" 
            items={listaProveedor.value}
            placeholder='Seleccione un tipo'
            aria-label='Seleccione un tipo de combustible'
            value={proveedor.value}
            onValueChanged={(evt) => (proveedor.value = evt.detail.value)}
            />
            <ComboBox label="Tipo" 
            items={listaEstadoOrden.value}
            placeholder='Seleccione un tipo'
            aria-label='Seleccione un tipo de combustible'
            value={estado.value}
            onValueChanged={(evt) => (estado.value = evt.detail.value)}
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

//LISTA DE OrdenCompraS
export default function OrdenCompraView() {
  
  const dataProvider = useDataProvider<OrdenCompra>({ 
    list: () => OrdenCompraService.listOrdenCompra(),
  });

  function indexIndex({model}:{model:GridItemModel<OrdenCompra>}) {
    return (
      <span>
        {model.index + 1} 
      </span>
    );
  }

  return (

    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Lista de OrdenCompraes">
        <Group>
          <OrdenCompraEntryForm onOrdenCompraCreated={dataProvider.refresh}/>
        </Group>
      </ViewToolbar>
      <Grid dataProvider={dataProvider.dataProvider}>
        <GridColumn  renderer={indexIndex} header="Nro" />
        <GridColumn path="cantidad" header="Cantidad" />
        <GridColumn path="proveedor" header="Proveedor"/>
        <GridColumn path="estado" header="Estado">

        </GridColumn>
      </Grid>
    </main>
  );
}