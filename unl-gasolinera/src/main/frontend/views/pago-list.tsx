import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, GridSortColumn, HorizontalLayout, Icon, NumberField, Select, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { PagoService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
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

type ReciboDialogProps = {
  pago: Pago | null;
  opened: boolean;
  onClose: () => void;
};

function ReciboDialog({ pago, opened, onClose }: ReciboDialogProps) {
  const [recibo, setRecibo] = useState<any>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (opened && pago) {
      setLoading(true);
      PagoService.generarRecibo(pago.id)
        .then(data => {
          setRecibo(data);
          setLoading(false);
        })
        .catch(error => {
          console.error('Error al generar recibo:', error);
          Notification.show('Error al generar el recibo', {
            duration: 5000,
            theme: 'error',
            position: 'top-center'
          });
          setLoading(false);
          onClose();
        });
    }
  }, [opened, pago]);

  const imprimirRecibo = () => {
    if (!recibo) return;

    // Crear el contenido HTML para imprimir
    const contenidoImpresion = `
      <!DOCTYPE html>
      <html>
      <head>
        <title>Recibo de Pago - ${recibo.nroTransaccion}</title>
        <style>
          body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 10px auto;
            padding: 15px;
            line-height: 1.4;
            font-size: 14px;
          }
          .header {
            text-align: center;
            border-bottom: 2px solid #333;
            padding-bottom: 15px;
            margin-bottom: 15px;
          }
          .header h1 {
            margin: 0 0 5px 0;
            font-size: 1.5em;
          }
          .header p {
            margin: 0;
            font-size: 0.9em;
          }
          .section {
            margin-bottom: 15px;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            page-break-inside: avoid;
          }
          .section h3 {
            margin: 0 0 8px 0;
            color: #333;
            border-bottom: 1px solid #ccc;
            padding-bottom: 3px;
            font-size: 1.1em;
          }
          .field {
            display: flex;
            justify-content: space-between;
            margin-bottom: 6px;
            padding: 2px 0;
          }
          .label {
            font-weight: bold;
            color: #555;
            font-size: 0.9em;
          }
          .value {
            text-align: right;
            font-size: 0.9em;
          }
          .total {
            font-weight: bold;
            font-size: 1.1em;
            color: #2c5aa0;
          }
          .footer {
            text-align: center;
            margin-top: 15px;
            padding-top: 10px;
            border-top: 1px solid #ccc;
            font-size: 0.8em;
            color: #666;
            page-break-inside: avoid;
          }
          .footer p {
            margin: 3px 0;
          }
          @media print {
            body { 
              margin: 0; 
              padding: 10px;
              font-size: 12px;
            }
            .no-print { display: none; }
            .header {
              margin-bottom: 10px;
              padding-bottom: 10px;
            }
            .section {
              margin-bottom: 10px;
              padding: 8px;
            }
            .footer {
              margin-top: 10px;
              padding-top: 8px;
            }
          }
        </style>
      </head>
      <body>
        <div class="header">
          <h1>RECIBO DE PAGO</h1>
          <p>Sistema de Gestión de Gasolinera</p>
        </div>

        <div class="section">
          <h3>Información del Pago</h3>
          <div class="field">
            <span class="label">Número de Transacción:</span>
            <span class="value">${recibo.nroTransaccion || 'N/A'}</span>
          </div>
          <div class="field">
            <span class="label">Estado del Pago:</span>
            <span class="value">${recibo.estadoPago || 'N/A'}</span>
          </div>
          <div class="field">
            <span class="label">Fecha del Recibo:</span>
            <span class="value">${recibo.fechaRecibo || 'N/A'}</span>
          </div>
          <div class="field">
            <span class="label">Propietario del vehículo:</span>
            <span class="value">${recibo.propietarioVehiculo || 'N/A'}</span>
          </div>
          <div class="field">
            <span class="label">Cédula del propietario:</span>
            <span class="value">${recibo.cedulaPropietario || 'N/A'}</span>
          </div>
        </div>

        <div class="section">
          <h3>Detalles de la Orden</h3>
          <div class="field">
            <span class="label">Código:</span>
            <span class="value">${recibo.codigo || 'N/A'}</span>
          </div>
          <div class="field">
            <span class="label">Fecha de la Orden:</span>
            <span class="value">${recibo.fecha || 'N/A'}</span>
          </div>
          <div class="field">
            <span class="label">Placa del Vehículo:</span>
            <span class="value">${recibo.placa || 'N/A'}</span>
          </div>
          <div class="field">
            <span class="label">Estación:</span>
            <span class="value">${recibo.estacion || 'N/A'}</span>
          </div>
          <div class="field">
            <span class="label">Tipo de Gasolina:</span>
            <span class="value">${recibo.nombreGasolina || 'N/A'}</span>
          </div>
          <div class="field">
            <span class="label">Precio por Galón:</span>
            <span class="value">$${recibo.precio_establecido || 'N/A'}</span>
          </div>
          <div class="field">
            <span class="label">Galones:</span>
            <span class="value">${recibo.nroGalones || 'N/A'}</span>
          </div>
          <div class="field total">
            <span class="label">PRECIO TOTAL:</span>
            <span class="value">$${recibo.precioTotal || 'N/A'}</span>
          </div>
          <div class="field">
            <span class="label">Estado de la Orden:</span>
            <span class="value">${recibo.estado || 'N/A'}</span>
          </div>
        </div>

        <div class="footer">
          <p>Recibo generado automáticamente</p>
          <p>Fecha de impresión: ${new Date().toLocaleString('es-ES')}</p>
        </div>
      </body>
      </html>
    `;

    // Crear una ventana nueva para la impresión
    const ventanaImpresion = window.open('', '_blank');
    if (ventanaImpresion) {
      ventanaImpresion.document.write(contenidoImpresion);
      ventanaImpresion.document.close();

      // Esperar a que se cargue el contenido y luego imprimir
      ventanaImpresion.onload = () => {
        ventanaImpresion.print();
        ventanaImpresion.onafterprint = () => {
          ventanaImpresion.close();
        };
      };
    }
  };

  return (
    <Dialog
      headerTitle="Recibo de Pago"
      opened={opened}
      onOpenedChanged={({ detail }) => {
        if (!detail.value) onClose();
      }}
      footer={
        <>
          <Button onClick={onClose}>
            Cerrar
          </Button>
          <Button
            onClick={imprimirRecibo}
            theme="primary"
            disabled={loading || !recibo}
          >
            Imprimir
          </Button>
        </>
      }
    >
      <VerticalLayout style={{ alignItems: 'stretch', width: '25rem', maxWidth: '100%', gap: '1rem' }}>
        {loading ? (
          <div style={{ textAlign: 'center', padding: '2rem' }}>
            Generando recibo...
          </div>
        ) : recibo ? (
          <>
            <div style={{ textAlign: 'center', fontWeight: 'bold', fontSize: '1.2rem', marginBottom: '1rem' }}>
              RECIBO DE PAGO
            </div>

            <div style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '1rem', backgroundColor: '#f9f9f9' }}>
              <h4 style={{ margin: '0 0 0.5rem 0', color: '#333' }}>Información del Pago</h4>
              <TextField
                label="Número de Transacción"
                value={recibo.nroTransaccion?.toString() || 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
              <TextField
                label="Estado del Pago"
                value={recibo.estadoPago || 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
              <TextField
                label="Fecha del Recibo"
                value={recibo.fechaRecibo || 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
              <TextField
                label="Propietario del vehículo"
                value={recibo.propietarioVehiculo || 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
              <TextField
                label="Cédula del propietario"
                value={recibo.cedulaPropietario || 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
            </div>

            <div style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '1rem', backgroundColor: '#f0f8ff' }}>
              <h4 style={{ margin: '0 0 0.5rem 0', color: '#333' }}>Detalles de la Orden</h4>
              <TextField
                label="Código"
                value={recibo.codigo || 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
              <TextField
                label="Fecha de la Orden"
                value={recibo.fecha || 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
              <TextField
                label="Placa del Vehículo"
                value={recibo.placa || 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
              <TextField
                label="Estación"
                value={recibo.estacion || 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
              <TextField
                label="Tipo de Gasolina"
                value={recibo.nombreGasolina || 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
              <TextField
                label="Precio por Galón"
                value={recibo.precio_establecido ? `$${recibo.precio_establecido}` : 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
              <TextField
                label="Galones"
                value={recibo.nroGalones?.toString() || 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
              <TextField
                label="Precio Total"
                value={recibo.precioTotal ? `$${recibo.precioTotal}` : 'N/A'}
                readonly
                style={{ width: '100%', fontWeight: 'bold' }}
              />
              <TextField
                label="Estado de la Orden"
                value={recibo.estado || 'N/A'}
                readonly
                style={{ width: '100%' }}
              />
            </div>
          </>
        ) : (
          <div style={{ textAlign: 'center', padding: '2rem' }}>
            No se pudo cargar el recibo
          </div>
        )}
      </VerticalLayout>
    </Dialog>
  );
}

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
          <Select
            label="Estado del Pago"
            placeholder="Seleccione el estado del pago"
            aria-label="Estado del Pago"
            items={[
              { label: 'Completado', value: 'true' },
              { label: 'Error', value: 'false' },
            ]}
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
function renderEstado({ model }: { model: GridItemModel<Pago> }) {
  return <span>{model.item.estadoP ? 'Completado' : 'Error'}</span>;
}


export default function PagoView() {
  const [items, setItems] = useState([]);
  const [mensajePago, setMensajePago] = useState<string | null>(null);
  const [selectedPago, setSelectedPago] = useState<Pago | null>(null);
  const [reciboDialogOpened, setReciboDialogOpened] = useState(false);

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
      setItems(data);
    });
  }

  const deletePago = async (pago: Pago) => {
    // Mostrar confirmación antes de eliminar
    const confirmed = window.confirm(`¿Está seguro de que desea eliminar el pago con transacción ${pago.nroTransaccion}?`);

    if (confirmed) {
      try {
        await PagoService.delete(pago.id);
        Notification.show('Pago eliminado exitosamente', {
          duration: 5000,
          position: 'bottom-end',
          theme: 'success'
        });
        // Recargar la lista después de eliminar
        callData();
      } catch (error) {
        console.error('Error al eliminar pago:', error);
        Notification.show('Error al eliminar el pago', {
          duration: 5000,
          position: 'top-center',
          theme: 'error'
        });
      }
    }
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
          onValueChanged={(evt) => {
            criterio.value = evt.detail.value;
            texto.value = ''; 
          }}
          placeholder={'Seleccione un criterio'}>

        </Select>

        {criterio.value === 'estadoP' ? (
          <Select
            items={[
              { label: 'Completado', value: 'true' },
              { label: 'Error', value: 'false' },
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
            onValueChanged={(evt) => (texto.value = evt.detail.value)}
          >
            <Icon slot="prefix" icon="vaadin:search" />
          </TextField>
        )}
        
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
        <GridSortColumn onDirectionChanged={(e) => order(e, "estadoP")} path="estadoP" header="Estado" renderer={renderEstado} />

        <GridColumn
          header="Recibo"
          renderer={({ item }) => (
            <Button
              theme="success"
              onClick={() => {
                setSelectedPago(item);
                setReciboDialogOpened(true);
              }}
            >
              Recibo
            </Button>
          )}
        />
        <GridColumn
          header="Eliminar"
          renderer={({ item }) => (
            <Button
              theme="error"
              onClick={() => deletePago(item)}
            >
              Eliminar
            </Button>
          )}
        />
      </Grid>

      <ReciboDialog
        pago={selectedPago}
        opened={reciboDialogOpened}
        onClose={() => {
          setReciboDialogOpened(false);
          setSelectedPago(null);
        }}
      />
    </main>
  );
}
