import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, GridSortColumn, HorizontalLayout, Icon, NumberField, Select, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { OrdenDespachoService, PagoService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect, useState } from 'react';
import OrdenDespacho from 'Frontend/generated/org/unl/gasolinera/base/models/OrdenDespacho';

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

type OrdenDespachoEntryFormUpdate = {
  orden_despacho: OrdenDespacho;
  onOrdenUpdated?: () => void;
};

function OrdenDespachoEntryForm(props: OrdenDespachoEntryFormProps) {
  const codigo = useSignal('');
  const nroGalones = useSignal('');
  const fecha = useSignal('');
  const precioTotal = useSignal('');
  const estado = useSignal('');
  const idVehiculo = useSignal('');
  const idPrecioEstablecido = useSignal('');
  const idEstacion = useSignal('');

  const listaEstados = useSignal<any[]>([]);
  useEffect(() => {
    OrdenDespachoService.listEstadoOrdenDespacho().then((data) => {
      listaEstados.value = data || [];
    });
  }, []);

  const listaPrecios = useSignal<any[]>([]);
  useEffect(() => {
    OrdenDespachoService.listPrecioGalonCombo().then((data) => {
      listaPrecios.value = data || [];
    });
  }, []);

  const listaVehiculos = useSignal<any[]>([]);
  useEffect(() => {
    OrdenDespachoService.listVehiculoCombo().then((data) => {
      listaVehiculos.value = data || [];
    });
  }, []);

  const listaEstaciones = useSignal<any[]>([]);
  useEffect(() => {
    OrdenDespachoService.listEstacionCombo().then((data) => {
      listaEstaciones.value = data || [];
    });
  }, []);

  useEffect(() => {
    if (nroGalones.value && idPrecioEstablecido.value) {
      const precioSeleccionado = listaPrecios.value.find(
        (p) => p.value === idPrecioEstablecido.value
      );
      if (precioSeleccionado) {
        const precio = parseFloat(precioSeleccionado.precio);
        const galones = parseFloat(String(nroGalones.value));
        if (!isNaN(precio) && !isNaN(galones)) {
          precioTotal.value = (precio * galones).toFixed(2);
        }
      }
    } else {
      precioTotal.value = '';
    }
  }, [nroGalones.value, idPrecioEstablecido.value, listaPrecios.value]);

  const createOrden = async () => {
    try {
      if (
        codigo.value.trim() &&
        nroGalones.value &&
        fecha.value &&
        precioTotal.value &&
        estado.value &&
        idVehiculo.value &&
        idPrecioEstablecido.value &&
        idEstacion.value
      ) {
       
        await OrdenDespachoService.create(
          codigo.value,
          parseFloat(nroGalones.value),
          new Date(fecha.value),
          estado.value,
          parseInt(idPrecioEstablecido.value),
          parseInt(idVehiculo.value),
          parseInt(idEstacion.value)
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
        idPrecioEstablecido.value = '';
        idEstacion.value = '';

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
          <DatePicker
            label="Fecha"
            placeholder="Seleccione la fecha"
            value={fecha.value}
            onValueChanged={(e) => (fecha.value = e.detail.value)}
          />
          <NumberField
            label="Nro Galones"
            placeholder="Ingrese el número de galones"
            value={nroGalones.value?.toString() ?? ''}
            onValueChanged={(e) => (nroGalones.value = e.detail.value)}
          />
          <ComboBox
            label="Tipo de Gasolina"
            items={listaPrecios.value}
            placeholder="Seleccione el tipo"
            itemLabelPath="label"
            itemValuePath="value"
            value={idPrecioEstablecido.value}
            onValueChanged={(e) => {
              idPrecioEstablecido.value = e.detail.value;
            }}
          />
          <TextField
            label="Precio Total (automático)"
            value={precioTotal.value?.toString() ?? ''}
            readonly
          />
          <ComboBox
            label="Estado"
            items={listaEstados.value}
            placeholder="Seleccione un estado"
            value={estado.value}
            onValueChanged={(e) => (estado.value = e.detail.value)}
          />
          <ComboBox
            label="Matrícula del Vehículo"
            items={listaVehiculos.value}
            placeholder="Seleccione una matrícula"
            itemLabelPath="label"
            itemValuePath="value"
            value={idVehiculo.value}
            onValueChanged={(e) => (idVehiculo.value = e.detail.value)}
          />
          <ComboBox
            label="Estación"
            items={listaEstaciones.value}
            placeholder="Seleccione una estación"
            itemLabelPath="label"
            itemValuePath="value"
            value={idEstacion.value}
            onValueChanged={(e) => (idEstacion.value = e.detail.value)}
          />
        </VerticalLayout>
      </Dialog>

      <Button onClick={() => (dialogOpened.value = true)}>Agregar</Button>
    </>
  );
}

function OrdenDespachoUpdateForm(props: OrdenDespachoEntryFormUpdate) {
  const dialogOpened = useSignal(false);

  const id = useSignal(props.orden_despacho?.id?.toString() || '');
  const codigo = useSignal(props.orden_despacho?.codigo || '');
  const nroGalones = useSignal(props.orden_despacho?.nroGalones || '');
  const fecha = useSignal(props.orden_despacho?.fecha || '');
  const precioTotal = useSignal(props.orden_despacho?.precioTotal || '');
  const estado = useSignal(props.orden_despacho?.estado || '');
  const placa = useSignal(props.orden_despacho?.placa || '');
  const precioEstablecido = useSignal(props.orden_despacho?.precio_establecido || '');
  const estacion = useSignal(props.orden_despacho?.estacion || '');

  const openDialog = () => {
    id.value = props.orden_despacho?.id?.toString() || '';
    codigo.value = props.orden_despacho?.codigo || '';
    nroGalones.value = props.orden_despacho?.nroGalones || '';
    fecha.value = props.orden_despacho?.fecha || '';
    precioTotal.value = props.orden_despacho?.precioTotal || '';
    estado.value = props.orden_despacho?.estado || '';
    
    // Cargar valores para los ComboBox con setTimeout para asegurar que las listas estén cargadas
    setTimeout(() => {
      // Para el vehículo, necesitamos encontrar el ID basado en la placa
      if (props.orden_despacho?.placa && listaVehiculos.value.length > 0) {
        const vehiculoEncontrado = listaVehiculos.value.find(v => v.label === props.orden_despacho.placa);
        if (vehiculoEncontrado) {
          idVehiculo.value = vehiculoEncontrado.value;
        }
      }
      
      // Para el precio establecido, basado en precio_establecido
      if (props.orden_despacho?.precio_establecido && listaPrecios.value.length > 0) {
        const precioEncontrado = listaPrecios.value.find(p => p.precio == props.orden_despacho.precio_establecido);
        if (precioEncontrado) {
          idPrecioEstablecido.value = precioEncontrado.value;
        }
      }
      
      // Para la estación, basado en el nombre de la estación
      if (props.orden_despacho?.estacion && listaEstaciones.value.length > 0) {
        const estacionEncontrada = listaEstaciones.value.find(e => e.label === props.orden_despacho.estacion);
        if (estacionEncontrada) {
          idEstacion.value = estacionEncontrada.value;
        }
      }
    }, 100);

    dialogOpened.value = true;
  };

  const updateOrdenDespacho = async () => {
    try {
      if (
        codigo.value.trim().length > 0 &&
        Number(nroGalones.value) > 0 &&
        fecha.value.trim().length > 0 &&
        Number(precioTotal.value) > 0 &&
        estado.value.trim().length > 0 &&
        idVehiculo.value.trim().length > 0 &&
        idPrecioEstablecido.value.trim().length > 0 &&
        idEstacion.value.trim().length > 0
      ) {
        await OrdenDespachoService.update(
          parseInt(id.value), 
          codigo.value, 
          parseFloat(String(nroGalones.value)), 
          new Date(fecha.value), 
          estado.value, 
          parseInt(idPrecioEstablecido.value), 
          parseInt(idVehiculo.value), 
          parseInt(idEstacion.value)
        );
        if (props.onOrdenUpdated) {
          props.onOrdenUpdated();
        }
        codigo.value = '';
        nroGalones.value = '';
        fecha.value = '';
        precioTotal.value = '';
        estado.value = '';
        placa.value = '';
        precioEstablecido.value = '';
        estacion.value = '';


        dialogOpened.value = false;
        Notification.show('Orden de Despacho editada', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo editar, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }

    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };


  let listaEstados = useSignal<any[]>([]);
  useEffect(() => {
    OrdenDespachoService.listEstadoOrdenDespacho().then((data) => {
      listaEstados.value = data || [];
    });
  }, []);

  let listaPrecios = useSignal<any[]>([]);
  useEffect(() => {
    OrdenDespachoService.listPrecioGalonCombo().then((data) => {
      listaPrecios.value = data || [];
    });
  }, []);

  let listaVehiculos = useSignal<any[]>([]);
  useEffect(() => {
    OrdenDespachoService.listVehiculoCombo().then((data) => {
      listaVehiculos.value = data || [];
    });
  }, []);

  let listaEstaciones = useSignal<any[]>([]);
  useEffect(() => {
    OrdenDespachoService.listEstacionCombo().then((data) => {
      listaEstaciones.value = data || [];
    });
  }, []);

  const idVehiculo = useSignal('');
  const idPrecioEstablecido = useSignal('');
  const idEstacion = useSignal('');

 useEffect(() => {
    if (nroGalones.value && idPrecioEstablecido.value) {
      const precioSeleccionado = listaPrecios.value.find(
        (p) => p.value === idPrecioEstablecido.value
      );
      if (precioSeleccionado) {
        const precio = parseFloat(precioSeleccionado.precio);
        const galones = parseFloat(String(nroGalones.value));
        if (!isNaN(precio) && !isNaN(galones)) {
          precioTotal.value = (precio * galones).toFixed(2);
        }
      }
    } else {
      precioTotal.value = '';
    }
  }, [nroGalones.value, idPrecioEstablecido.value, listaPrecios.value]);

  return (
    <>
      <Dialog
        modeless
        headerTitle="Editar Orden Despacho"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => {
          dialogOpened.value = detail.value;
        }}
        footer={
          <>
            <Button onClick={() => { dialogOpened.value = false; }}>Cancelar</Button>
            <Button onClick={updateOrdenDespacho} theme="primary">Registrar</Button>
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
          <DatePicker
            label="Fecha"
            placeholder="Seleccione la fecha"
            value={fecha.value}
            onValueChanged={(e) => (fecha.value = e.detail.value)}
          />
          <NumberField
            label="Nro Galones"
            placeholder="Ingrese el número de galones"
            value={nroGalones.value}
            onValueChanged={(e) => (nroGalones.value = e.detail.value)}
          />
          <ComboBox
            label="Tipo de Gasolina"
            items={listaPrecios.value}
            placeholder="Seleccione el tipo"
            itemLabelPath="label"
            itemValuePath="value"
            value={idPrecioEstablecido.value}
            onValueChanged={(e) => {
              idPrecioEstablecido.value = e.detail.value;
            }}
          />
          <TextField
            label="Precio Total (automático)"
            value={precioTotal.value?.toString() ?? ''}
            readonly
          />
          <ComboBox
            label="Estado"
            items={listaEstados.value}
            placeholder="Seleccione un estado"
            value={estado.value}
            onValueChanged={(e) => (estado.value = e.detail.value)}
          />
          <ComboBox
            label="Matrícula del Vehículo"
            items={listaVehiculos.value}
            placeholder="Seleccione una matrícula"
            itemLabelPath="label"
            itemValuePath="value"
            value={idVehiculo.value}
            onValueChanged={(e) => (idVehiculo.value = e.detail.value)}
          />
          <ComboBox
            label="Estación"
            items={listaEstaciones.value}
            placeholder="Seleccione una estación"
            itemLabelPath="label"
            itemValuePath="value"
            value={idEstacion.value}
            onValueChanged={(e) => (idEstacion.value = e.detail.value)}
          />
        </VerticalLayout>
      </Dialog>
      <Button onClick={openDialog}>Editar</Button>
    </>
  );

}

export default function OrdenDespachoView() {
  const dataProvider = useDataProvider<any>({
    list: () => OrdenDespachoService.listOrdenDespacho().then(data => data || [])
  });
  const [items, setItems] = useState<any[]>([]);
  const [ordenPago, setOrdenPago] = useState<any | null>(null);
  const [checkoutId, setCheckoutId] = useState<string | null>(null);
  const [mensajePago, setMensajePago] = useState<string | null>(null);

  useEffect(() => {
    OrdenDespachoService.listAll().then(function (data) {
      setItems(data || []);
    });
  }, []);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    const ordenPagoId = ordenPago?.id || localStorage.getItem('ordenPagoId');
    if (id) {
      console.log('Llamando a consultarEstadoPago solo con checkoutId:', id);
      PagoService.consultarEstadoPago(id).then(async resultado => {
        console.log('Resultado de consultarEstadoPago:', resultado);
        if (resultado && resultado.estado === "true") {
          setMensajePago("Pago realizado con éxito");

        } else if (resultado && resultado.estado === "false") {
          setMensajePago("Pago rechazado");

        }
        setOrdenPago(null);
        setCheckoutId(null);
        localStorage.removeItem('ordenPagoId');
        window.history.replaceState({}, '', window.location.pathname);
        callData(); // Usar callData en lugar de dataProvider.refresh()
      }).catch(error => {
        console.error('Error en consultarEstadoPago:', error);
        setMensajePago("Error al procesar el pago");
      });
    }
  }, [window.location.search]);

  const order = (event: any, columnId: any) => {
    const direction = event.detail.value;
    if (!direction) {
      // Sin orden, mostrar lista original
      OrdenDespachoService.listAll().then((data: any) => setItems(data));
    } else {
      var dir = (direction == 'asc') ? 1 : 2;
      OrdenDespachoService.order(columnId, dir).then((data: any) => setItems(data));
    }
  }
  const callData = () => {
    OrdenDespachoService.listAll().then(function (data: any) {
      setItems(data || []);
    });
  }

  const deleteOrdenDespacho = async (orden_despacho: OrdenDespacho) => {
    // Mostrar confirmación antes de eliminar
    const confirmed = window.confirm(`¿Está seguro de que desea eliminar el despacho con código ${orden_despacho.codigo}?`);

    if (confirmed) {
      try {
        await OrdenDespachoService.delete(orden_despacho.id);
        Notification.show('Despacho eliminado exitosamente', {
          duration: 5000,
          position: 'bottom-end',
          theme: 'success'
        });
        // Recargar la lista después de eliminar
        callData();
      } catch (error) {
        console.error('Error al eliminar la orden de despacho:', error);
        Notification.show('Error al eliminar la orden de despacho', {
          duration: 5000,
          position: 'top-center',
          theme: 'error'
        });
      }
    }
  }

  //BUSCAR
  const criterio = useSignal('');
  const texto = useSignal('');

  const itemSelect = [
    {
      label: 'Código',
      value: 'codigo',
    },
    {
      label: 'Fecha',
      value: 'fecha',
    },
    {
      label: 'Placa',
      value: 'placa',
    },
    {
      label: 'Estacion',
      value: 'estacion'
    },
    {
      label: 'Tipo Combustible',
      value: 'nombreGasolina'
    },
    {
      label: 'Estado',
      value: 'estado'
    }
  ];

  const searchCri = async () => {
    try {
      OrdenDespachoService.search(criterio.value, texto.value, 0).then(function (data) {
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

  function indexLink({ model }: { model: GridItemModel<OrdenDespacho> }) {
    return (
      <span>
        <OrdenDespachoUpdateForm orden_despacho={model.item} onOrdenUpdated={callData} />
      </span>
    );
  }

  function precioRenderer({ model }: { model: GridItemModel<any> }) {
    const precio = model.item.precio_establecido;
    return <span>${precio ? Number(precio).toFixed(2) : '0.00'}</span>;
  }

  function precioTotalRenderer({ model }: { model: GridItemModel<any> }) {
    const precioTotal = model.item.precioTotal;
    return <span>${precioTotal ? Number(precioTotal).toFixed(2) : '0.00'}</span>;
  }


  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Lista de Órdenes de Despacho">
        <Group>
          <OrdenDespachoEntryForm onOrdenCreated={callData} />
        </Group>
      </ViewToolbar>
      <HorizontalLayout theme="spacing">
        <Select items={itemSelect}
          value={criterio.value}
          onValueChanged={(evt) => (criterio.value = evt.detail.value)}
          placeholder={'Seleccione un criterio'}>

        </Select>
        
        {criterio.value === 'estado' ? (
          <Select
            items={[
              { label: 'En Proceso', value: 'EN_PROCESO' },
              { label: 'Completado', value: 'COMPLETADO' },
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

        <Button onClick={searchCri} theme="primary">
          BUSCAR
        </Button>
        <Button onClick={callData} theme="secondary">
          REFRESCAR
        </Button>

      </HorizontalLayout>
      <Grid items={items}>
        <GridColumn renderer={indexIndex} header="Nro" width="70px" flexGrow={0} />
        <GridSortColumn onDirectionChanged={(e) => order(e, "codigo")} path="codigo" header="Código" width="140px" flexGrow={0} />
        <GridSortColumn onDirectionChanged={(e) => order(e, "fecha")} path="fecha" header="Fecha" width="320px" flexGrow={0} />
        <GridSortColumn onDirectionChanged={(e) => order(e, "placa")} path="placa" header="Placa del Vehículo" width="200px" flexGrow={0} />
        <GridSortColumn onDirectionChanged={(e) => order(e, "estacion")} path="estacion" header="Estación" width="150px" flexGrow={0} />
        <GridSortColumn onDirectionChanged={(e) => order(e, "nombreGasolina")} path="nombreGasolina" header="Tipo de Gasolina" width="150px" flexGrow={0} />
        <GridSortColumn onDirectionChanged={(e) => order(e, "precio_establecido")} path="precio_establecido" header="Precio por Galón" width="170px" flexGrow={0} renderer={precioRenderer} />
        <GridSortColumn onDirectionChanged={(e) => order(e, "nroGalones")} path="nroGalones" header="Galones" width="150px" flexGrow={0} />
        <GridSortColumn onDirectionChanged={(e) => order(e, "precioTotal")} path="precioTotal" header="Precio Total" width="120px" flexGrow={0} renderer={precioTotalRenderer} />
        <GridSortColumn onDirectionChanged={(e) => order(e, "estado")} path="estado" header="Estado" width="230px" flexGrow={0} />
        <GridColumn header="Editar" renderer={indexLink} />
        <GridColumn
          header="Pagar"
          renderer={({ item }) => (
            <Button
              theme="primary"
              onClick={() => {
                setOrdenPago(item);
                setMensajePago(null);
                setCheckoutId(null);
                localStorage.setItem('ordenPagoId', item.id);
              }}
              disabled={!!checkoutId || item.estado === 'COMPLETADO'}
            >
              {item.estado === 'COMPLETADO' ? 'Pagado' : 'Pagar'}
            </Button>
          )}
        />

        <GridColumn
          header="Eliminar"
          renderer={({ item }) => (
            <Button
              theme="error"
              onClick={() => deleteOrdenDespacho(item)}
            >
              Eliminar
            </Button>
          )}
        />
      </Grid>

      {ordenPago && !checkoutId && (
        <div style={{ marginTop: '1rem' }}>
          <h4>Pago para orden: {ordenPago.codigo} (${Number(ordenPago.precioTotal).toFixed(2)})</h4>
          <Button
            theme="primary"
            onClick={async () => {
              console.log('Iniciando checkout con ordenPago.id:', ordenPago.id);
              const resp = await PagoService.checkout(ordenPago.precioTotal, 'USD', ordenPago.id);
              console.log('Respuesta del checkout:', resp);
              if (resp && resp.id) {
                setCheckoutId(String(resp.id));
                console.log('CheckoutId establecido:', resp.id);
              } else {
                setMensajePago('No se pudo iniciar el pago');
                console.error('Error en checkout:', resp);
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
