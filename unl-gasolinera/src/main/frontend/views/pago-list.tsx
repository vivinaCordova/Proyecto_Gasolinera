import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, GridSortColumn, HorizontalLayout, Icon, NumberField, Select, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { TaskService } from 'Frontend/generated/endpoints';
import { PagoService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import Task from 'Frontend/generated/org/unl/gasolinera/taskmanagement/domain/Task';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect } from 'react';
import { useState } from 'react';

export const config: ViewConfig = {
  title: 'Pagos',
  menu: {
    icon: 'vaadin:credit-card',
    order: 1,
    title: 'Pagos',
  },
};

type Pago = {
  id: number;
  nroTransaccion: number;
  orden_despacho: number;
  estadoP: boolean;
};

type PagoEntryFormProps = {
  onPagoCreated?: () => void;
};

function PagoEntryForm(props: PagoEntryFormProps) {
  const nroTransaccion = useSignal('');
  const orden_despacho = useSignal('');
  const estadoP = useSignal('');
  const dialogOpened = useSignal(false);
  const createPago = async () => {
    try {
      if (nroTransaccion.value.trim().length > 0 && orden_despacho.value.trim().length > 0 && estadoP.value.trim().length > 0) {
        const idOrdenDespacho = parseInt(orden_despacho.value);
        await PagoService.create(
          Number(nroTransaccion.value),
          estadoP.value.trim().toLowerCase() === 'true',
          idOrdenDespacho
        );
        if (props.onPagoCreated) {
          props.onPagoCreated();
        }

        nroTransaccion.value = '';
        orden_despacho.value = '';
        estadoP.value = '';

        dialogOpened.value = false;
        Notification.show('Pago creado', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }

    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };
  let listaPagoOrdenDespacho = useSignal<String[]>([]);
  useEffect(() => {
    PagoService.listaPagoOrdenDespacho().then(data =>
      //console.log(data)
      listaPagoOrdenDespacho.value = data
    );
  }, []);
  return (
    <>
      <Dialog
        modeless
        headerTitle="Nuevo Pago"
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
            <Button onClick={createPago} theme="primary">
              Registrar
            </Button>

          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>


          <NumberField label="Nro de Transacción"
            placeholder="Ingrese el Nro de Transacción"
            aria-label="Nro de Transacción"
            value={nroTransaccion.value}
            onValueChanged={(evt) => (nroTransaccion.value = evt.detail.value)}
          />

          <ComboBox label="Orden Despacho"
            items={listaPagoOrdenDespacho.value}
            placeholder='Seleccione una orden de despacho'
            aria-label='Seleccione una orden de despacho'
            value={orden_despacho.value}
            onValueChanged={(evt) => (orden_despacho.value = evt.detail.value)}
          />
          <TextField label="Estado del Pago"
            placeholder="Ingrese el estado del pago (true/false)"
            aria-label="Estado del Pago"
            value={estadoP.value}
            onValueChanged={(evt) => (estadoP.value = evt.detail.value)}
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


export default function PagoView() {
  const [items, setItems] = useState([]);
  const [mensajePago, setMensajePago] = useState<string | null>(null);

  // Manejar redirección de OPPWA después del pago
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    if (id) {
      console.log('Procesando resultado de pago desde OPPWA, checkoutId:', id);
      PagoService.consultarEstadoPago(id).then(resultado => {
        console.log('Resultado de consultarEstadoPago:', resultado);
        if (resultado && resultado.estado === "true") {
          
          Notification.show('Pago realizado con éxito', {
            duration: 5000,
            position: 'bottom-end',
            theme: 'success',
          });
        } else if (resultado && resultado.estado === "false") {
          setMensajePago("Pago rechazado");
          Notification.show('Pago rechazado', {
            duration: 5000,
            position: 'bottom-end',
            theme: 'error',
          });
        }
        // Limpiar la URL y recargar los datos
        window.history.replaceState({}, '', window.location.pathname);
        callData(); // Recargar la lista de pagos
      }).catch(error => {
        console.error('Error en consultarEstadoPago:', error);
        setMensajePago("Error al procesar el pago");
        Notification.show('Error al procesar el pago', {
          duration: 5000,
          position: 'bottom-end',
          theme: 'error',
        });
      });
    }
  }, []);

  useEffect(() => {
    PagoService.listAll().then(function (data) {
      console.log(data);
      setItems(data);
    });
  }, []);

  const order = (event, columnId) => {
    const direction = event.detail.value;
    if (!direction) {
      // Sin orden, mostrar lista original
      PagoService.listAll().then(setItems);
    } else {
      var dir = (direction == 'asc') ? 1 : 2;
      PagoService.order(columnId, dir).then(setItems);
    }
  }

  const callData = () => {
    PagoService.listAll().then(function (data) {
      //console.log(data);
      setItems(data);
    });
  }

  /*function indexLink({ model }: { model: GridItemModel<Pago> }) {
    return (
      <span>
        <PagoEntryFormUpdate Pago={model.item} onPagoUpdated={callData} />
      </span>
    );
  }*/


  function indexIndex({ model }: { model: GridItemModel<Pago> }) {
    return (
      <span>
        {model.index + 1}
      </span>
    );
  }

  //BUSCAR
  const criterio = useSignal('');
  const texto = useSignal('');

  const itemSelect = [
    {
      label: 'Nro Transaccion',
      value: 'nroTransaccion',
    },
    {
      label: 'Orden Despacho',
      value: 'orden_despacho',
    },
    {
      label: 'Estado',
      value: 'estadoP',
    }
  ];

  const search = async () => {
    try {
      PagoService.search(criterio.value, texto.value, 0).then(function (data) {
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

      <ViewToolbar title="Lista de Pagos">
        <Group>
          <PagoEntryForm onPagoCreated={callData} />
        </Group>
      </ViewToolbar>
      
      {mensajePago && (
        <div style={{ 
          padding: '1rem', 
          marginBottom: '1rem',
          backgroundColor: mensajePago.includes('éxito') ? '#d4edda' : '#f8d7da',
          color: mensajePago.includes('éxito') ? '#155724' : '#721c24',
          border: `1px solid ${mensajePago.includes('éxito') ? '#c3e6cb' : '#f5c6cb'}`,
          borderRadius: '0.25rem'
        }}>
          {mensajePago}
        </div>
      )}
      <HorizontalLayout theme="spacing">
        <Select items={itemSelect}
          value={criterio.value}
          onValueChanged={(evt) => (criterio.value = evt.detail.value)}
          placeholder={'Seleccione un criterio'}>

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
        <Button onClick={callData} theme="secondary">
          REFRESCAR
        </Button>

      </HorizontalLayout>
      <Grid items={items}>
        <GridColumn renderer={indexIndex} header="Nro" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "nroTransaccion")} path="nroTransaccion" header="nroTransaccion" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "orden_despacho")} path="orden_despacho" header="Orden Despacho" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "estadoP")} path="estadoP" header="Estado" />



      </Grid>
    </main>
  );
}