import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridSortColumn, HorizontalLayout, Icon, NumberField, Select, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { CuentaService, OrdenCompraService, TaskService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import OrdenCompra from 'Frontend/generated/org/unl/gasolinera/taskmanagement/domain/Task';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect, useState } from 'react';
import { role } from 'Frontend/security/auth';
import { logout } from '@vaadin/hilla-frontend';

export const config: ViewConfig = {
  title: 'Orden Compra',
  menu: {
    icon: 'vaadin:cart',
    order: 1,
    title: 'Orden de Compra',
  },
};
type OrdenCompraEntryFormProps = {
  onOrdenCompraCreated?: () => void;
};
//GUARDAR OrdenCompra
function OrdenCompraEntryForm(props: OrdenCompraEntryFormProps) {
useEffect(() => {
    role().then(async (data) => {
      if (data?.rol != 'ROLE_admin') {
        await CuentaService.logout();
        await logout();
      }
    });
  }, []);
  const cantidad = useSignal('');
  const estado = useSignal('');
  const proveedor = useSignal('');
  const tanque = useSignal('');
  const createOrdenCompra = async () => {
    try {
      if (cantidad.value.trim().length > 0 && estado.value.trim().length > 0) {
        const idProveedor = parseInt(proveedor.value) + 1;
        const idTanque = parseInt(tanque.value) + 1;
        await OrdenCompraService.createOrdenCompra(parseInt(cantidad.value), idProveedor, idTanque, (estado.value));
        if (props.onOrdenCompraCreated) {
          props.onOrdenCompraCreated();
        }
        cantidad.value = '';
        estado.value = '';
        proveedor.value = '';
        tanque.value = '';
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
  let listaProveedor = useSignal<String[]>([]);
  useEffect(() => {
    OrdenCompraService.listProveedorCombo().then(data =>
      listaProveedor.value = data
    );
  }, []);
  let listaTanque = useSignal<String[]>([]);
  useEffect(() => {
    OrdenCompraService.listTanqueCombo().then(data =>
      listaTanque.value = data
    );
  }, []);
  let listaEstadoOrden = useSignal<String[]>([]);
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
              Cancelar
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
          <ComboBox label="Proveedor"
            items={listaProveedor.value}
            placeholder='Seleccione a el provedor'
            aria-label='Seleccione un tipo de combustible'
            value={proveedor.value}
            onValueChanged={(evt) => (proveedor.value = evt.detail.value)}
          />
          <ComboBox label="Tanque"
            items={listaProveedor.value}
            placeholder='Seleccione el Tanque'
            aria-label='Seleccione un tanque'
            value={tanque.value}
            onValueChanged={(evt) => (tanque.value = evt.detail.value)}
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
  const [items, setItems] = useState([]);
  //useEffect(() =>{
  const callData = () => {
    OrdenCompraService.listAll().then(function (data) {
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
    OrdenCompraService.order(columnId, dir).then(function (data) {
      setItems(data);
    });
  }
  function indexIndex({ model }: { model: GridItemModel<OrdenCompra> }) {
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
      label: 'Cantidad de pedido',
      value: 'cantidad',
    },
    {

      label: 'Proveedor',
      value: 'proveedor',
    },

  ]
  const search = async () => {
    try {
      OrdenCompraService.search(criterio.value, texto.value, 0).then(function (data) {
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

      <ViewToolbar title="Lista de Ordenes de Compra">
        <Group>
          <OrdenCompraEntryForm onOrdenCompraCreated={callData} />
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
        <GridColumn renderer={indexIndex} header="Nro" />
        <GridSortColumn path="cantidad" header="Cantidad" onDirectionChanged={(e) => order(e, 'nombre')} />
        <GridSortColumn path="proveedor" header="Proveedor" onDirectionChanged={(e) => order(e, 'nombre')} />
        <GridSortColumn path="tanque" header="Tanque" onDirectionChanged={(e) => order(e, 'nombre')} />
        <GridColumn path="estado" header="Estado">

        </GridColumn>
      </Grid>
    </main>
  );
}
