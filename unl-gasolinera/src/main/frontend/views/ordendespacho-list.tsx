import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, NumberField, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { OrdenDespachoService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect, useState } from 'react';
import { PagoService } from 'Frontend/generated/endpoints';

// export async function crearPago(idOrdenDespacho: number, estado: boolean): Promise<void>

export const config: ViewConfig = {
  title: 'Orden de Despacho',
  menu: {
    icon: 'vaadin:form',
    order: 2,
    title: 'Orden de Despacho',
  },
};

type OrdenDespachoEntryFormProps = {
  onOrdenCreated?: () => void;
};

function OrdenDespachoEntryForm(props: OrdenDespachoEntryFormProps) {
  const codigo = useSignal('');
  const nroGalones = useSignal('');
  const fecha = useSignal('');
  const precioTotal = useSignal('');
  const estado = useSignal('');
  const idVehiculo = useSignal('');
  const idPrecioGalon = useSignal('');

  const listaEstados = useSignal<string[]>([]);
  useEffect(() => {
    OrdenDespachoService.listEstadoOrdenDespacho().then((data) => {
      listaEstados.value = data;
    });
  }, []);

  const createOrden = async () => {
    try {
      if (
        codigo.value.trim() &&
        nroGalones.value &&
        fecha.value &&
        precioTotal.value &&
        estado.value &&
        idVehiculo.value &&
        idPrecioGalon.value
      ) {
        await OrdenDespachoService.create(
          codigo.value,
          parseFloat(nroGalones.value),
          new Date(fecha.value),
          parseFloat(precioTotal.value),
          estado.value,
          parseInt(idPrecioGalon.value),
          parseInt(idVehiculo.value)
        );

        if (props.onOrdenCreated) {
          props.onOrdenCreated();
        }

        codigo.value = '';
        nroGalones.value = '';
        fecha.value = '';
        precioTotal.value = '';
        estado.value = '';
        idVehiculo.value = '';
        idPrecioGalon.value = '';

        dialogOpened.value = false;
        Notification.show('Orden de despacho creada', {
          duration: 5000,
          position: 'bottom-end',
          theme: 'success',
        });
      } else {
        Notification.show('Faltan datos obligatorios', {
          duration: 5000,
          position: 'top-center',
          theme: 'error',
        });
      }
    } catch (error) {
      handleError(error);
    }
  };

  const dialogOpened = useSignal(false);

  return (
    <>
      <Dialog
        modeless
        headerTitle="Nueva Orden de Despacho"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => {
          dialogOpened.value = detail.value;
        }}
        footer={
          <>
            <Button onClick={() => (dialogOpened.value = false)}>Cancelar</Button>
            <Button onClick={createOrden} theme="primary">
              Registrar
            </Button>
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField
            label="Código"
            placeholder="Ingrese el código"
            value={codigo.value}
            onValueChanged={(e) => (codigo.value = e.detail.value)}
          />
          <NumberField
            label="Nro Galones"
            placeholder="Ingrese el número de galones"
            value={nroGalones.value}
            onValueChanged={(e) => (nroGalones.value = e.detail.value)}
          />
          <DatePicker
            label="Fecha"
            placeholder="Seleccione la fecha"
            value={fecha.value}
            onValueChanged={(e) => (fecha.value = e.detail.value)}
          />
          <NumberField
            label="Precio Total"
            placeholder="Ingrese el precio total"
            value={precioTotal.value}
            onValueChanged={(e) => (precioTotal.value = e.detail.value)}
          />
          <ComboBox
            label="Estado"
            items={listaEstados.value}
            placeholder="Seleccione un estado"
            value={estado.value}
            onValueChanged={(e) => (estado.value = e.detail.value)}
          />
          <TextField
            label="ID Vehículo"
            placeholder="Ingrese el ID del vehículo"
            value={idVehiculo.value}
            onValueChanged={(e) => (idVehiculo.value = e.detail.value)}
          />
          <TextField
            label="ID Precio Galón"
            placeholder="Ingrese el ID del precio por galón"
            value={idPrecioGalon.value}
            onValueChanged={(e) => (idPrecioGalon.value = e.detail.value)}
          />
        </VerticalLayout>
      </Dialog>

      <Button onClick={() => (dialogOpened.value = true)}>Agregar</Button>
    </>
  );
}

//LISTA DE ORDENES
export default function OrdenDespachoView() {
  const dataProvider = useDataProvider<any>({
    list: () => OrdenDespachoService.listOrdenDespacho(),
  });

  const [ordenPago, setOrdenPago] = useState<any | null>(null);
  const [checkoutId, setCheckoutId] = useState<string | null>(null);
  const [mensajePago, setMensajePago] = useState<string | null>(null);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    const ordenPagoId = ordenPago?.id || localStorage.getItem('ordenPagoId');
    if (id && ordenPagoId) {
      PagoService.consultarEstadoPago(id).then(async resultado => {
        if (resultado && resultado.estado === "true") {
          setMensajePago("Pago realizado con éxito");
          // Llama correctamente a crearPago con parámetros individuales
          console.log('Llamando crearPago', Number(ordenPagoId), true);
          await PagoService.crearPago(
            Number(ordenPagoId),
            true
          );
        } else if (resultado && resultado.estado === "false") {
          setMensajePago("Pago rechazado");
          console.log('Llamando crearPago', Number(ordenPagoId), false);
          await PagoService.crearPago(
            Number(ordenPagoId),
            false
          );
        }
        setOrdenPago(null);
        setCheckoutId(null);
        localStorage.removeItem('ordenPagoId');
        window.history.replaceState({}, '', window.location.pathname);
        dataProvider.refresh();
      });
    }
    // eslint-disable-next-line
  }, [window.location.search]);

  useEffect(() => {
    if (checkoutId) {
      const script = document.createElement('script');
      script.src = `https://eu-test.oppwa.com/v1/paymentWidgets.js?checkoutId=${checkoutId}`;
      script.async = true;
      document.body.appendChild(script);

      return () => {
        document.body.removeChild(script);
      };
    }
  }, [checkoutId]);

  function indexIndex({ model }: { model: GridItemModel<any> }) {
    return <span>{model.index + 1}</span>;
  }

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Lista de Órdenes de Despacho">
        <Group>
          <OrdenDespachoEntryForm onOrdenCreated={dataProvider.refresh} />
        </Group>
      </ViewToolbar>

      <Grid dataProvider={dataProvider.dataProvider}>
        <GridColumn renderer={indexIndex} header="Nro" />
        <GridColumn path="codigo" header="Código" />
        <GridColumn path="nroGalones" header="Galones" />
        <GridColumn path="fecha" header="Fecha" />
        <GridColumn path="precioTotal" header="Precio Total" />
        <GridColumn path="estado" header="Estado" />
        <GridColumn path="idVehiculo" header="ID Vehículo" />
        <GridColumn path="idPrecioGalon" header="ID Precio Galón" />
        <GridColumn path="idEstacion" header="ID Estación" />
        <GridColumn
          header="Acción"
          renderer={({ item }) => (
            <Button
              theme="primary"
              onClick={() => {
                setOrdenPago(item);
                setMensajePago(null);
                setCheckoutId(null);
                localStorage.setItem('ordenPagoId', item.id);
              }}
              disabled={!!checkoutId}
            >
              Pagar
            </Button>
          )}
        />
      </Grid>
      {ordenPago && !checkoutId && (
        <div style={{ marginTop: '1rem' }}>
          <h4>Pago para orden: {ordenPago.codigo} (${ordenPago.precioTotal})</h4>
          <Button
            theme="primary"
            onClick={async () => {
              const resp = await PagoService.checkout(ordenPago.precioTotal, 'USD');
              if (resp && resp.id) {
                setCheckoutId(String(resp.id));
              } else {
                setMensajePago('No se pudo iniciar el pago');
              }
            }}
          >
            Iniciar Pago
          </Button>
        </div>
      )}
      {checkoutId && (
        <form
          className="paymentWidgets"
          data-brands="VISA MASTER AMEX"
          style={{ marginTop: '1rem' }}
        ></form>
      )}
    </main>
  );
}