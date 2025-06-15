import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, GridSortColumn, HorizontalLayout, Icon, NumberField, Select, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { OrdenDespachoService, TaskService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useCallback, useEffect, useState } from 'react';
import { search } from 'Frontend/generated/OrdenDespachoService';
import { data } from 'react-router';

export const config: ViewConfig = {
  title: 'Orden de Despacho',
  menu: {
    icon: 'vaadin:form',
    order: 2,
    title: 'Orden de Despacho',
  },
};

type OrdenDespacho = {
  estacion: string;
  vehiculo: string;
  id: number;
  codigo: string;
  nroGalones: number;
  fecha: Date;
  precioTotal: number;
  estado: string;
  ordenDespacho: string;
  precioEstablecido: string;
}


type OrdenDespachoEntryFormProps = {
  onOrdenDespachoreated?: () => void;
};

type OrdenDespachoEntryFormPropsUpdate = {
  ordenDespacho: OrdenDespacho;
  onOrdenDespachopdated?: () => void;
};

// Guardar Despacho
function OrdenDespachoEntryForm(props: OrdenDespachoEntryFormProps) {
  const nroGalones = useSignal('');
  const codigo = useSignal('');
  const fecha = useSignal('');
  const precioTotal = useSignal('');
  const estado = useSignal('');
  const precioEstablecido = useSignal('');
  const vehiculo = useSignal('');
  const estacion = useSignal('');

  const dialogOpened = useSignal(false);

  const createOrdenDespacho = async () => {
      try {
        if (nroGalones.value.trim().length > 0 && codigo.value.trim().length > 0 
          && fecha.value.trim().length > 0 && precioTotal.value.trim().length > 0 
          && estado.value.trim().length > 0) {

          const idPrecioEstablecido = parseInt(precioEstablecido.value)
          const idEstacion = parseInt(estacion.value);
          const idVehiculo = parseInt(vehiculo.value);
          


          await OrdenDespachoService.create( codigo.value, fecha.value, parseInt(nroGalones.value), estado.value, parseFloat(precioTotal.value), idPrecioEstablecido, idEstacion, idVehiculo);
          if (props.onOrdenDespachoreated) {
            props.onOrdenDespachoreated();
          }
          nroGalones.value = '';
          codigo.value = '';
          fecha.value = '';
          precioTotal.value = '';
          estado.value = '';
          precioEstablecido.value = '';
          vehiculo.value = '';
          estacion.value = '';
          dialogOpened.value = false;
          Notification.show('Orden Despacho creada', { duration: 5000, position: 'bottom-end', theme: 'success' });
        } else {
          Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
        }
      } catch (error) {
        console.log(error);
        handleError(error);
      }
    };


  let ListaVehiculo = useSignal<String[]>([]);
  useEffect(() => {
    OrdenDespachoService.listaVehiculoCombo().then(data =>
      //console.log(data)
      ListaVehiculo.value = data
    );
  }, []);

  let listaEstaciones = useSignal<String[]>([]);
  useEffect(() => {
    OrdenDespachoService.listaEstacionCombo().then(data =>
      //console.log(data)
      listaEstaciones.value = data
    );
  }, []);

  let listaPreciosEstablecidos = useSignal<String[]>([]);
  useEffect(() => {
    OrdenDespachoService.listaPrecioEstablecidosCombo().then(data =>
      //console.log(data)
      listaPreciosEstablecidos.value = data
    );
  }, []);

  let listaEstados = useSignal<String[]>([]);
  useEffect(() => {
    OrdenDespachoService.listEstadoOrdenDespacho().then(data =>
      //console.log(data)
      listaEstados.value = data
    );
  }, []);

 
  return (
    <>
      <Dialog
        modeless
        headerTitle="Nueva Orden de Despacho"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => dialogOpened.value = detail.value}
        footer={
          <>
            <Button onClick={() => dialogOpened.value = false}>Cancelar</Button>
            <Button onClick={createOrdenDespacho} theme="primary">Registrar</Button>
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField label="Codigo"
            placeholder="Ingrese el codigo"
            aria-label="Codigo"
            value={codigo.value}
            onValueChanged={(evt) => (codigo.value = evt.detail.value)}
          />
          <NumberField label="Nro Galones"
            placeholder="Ingrese el Nro Galones"
            aria-label="Nro de Galones"
            value={nroGalones.value}
            onValueChanged={(evt) => (nroGalones.value = evt.detail.value)}
          />
          <DatePicker label="Fecha"
            placeholder="Ingrese la fecha"
            aria-label="Fecha"
            value={fecha.value}
            onValueChanged={(evt) => (fecha.value = evt.detail.value)} />

          <NumberField label="Precio Total"
            placeholder="Ingrese el Precio Total"
            aria-label="Precio Total"
            value={precioTotal.value}
            onValueChanged={(e) => (precioTotal.value = e.detail.value)} />

          <ComboBox label="Estado"
            placeholder="Ingrese el Estado"
            aria-label="Estado"
            items={listaEstados.value}
            value={estado.value}
            onValueChanged={(e) => (estado.value = e.detail.value)} />

          <ComboBox label="Vehículo"
            placeholder="Ingrese el Vehículo"
            aria-label="Vehículo"
            items={ListaVehiculo.value}
            value={vehiculo.value}
            onValueChanged={(e) => (vehiculo.value = e.detail.value)} />

          <ComboBox label="Precio por Galón"
            placeholder="Ingrese el Precio por Galón"
            aria-label="Precio por Galón"
            items={listaPreciosEstablecidos.value}
            value={precioEstablecido.value}
            onValueChanged={(e) => (precioEstablecido.value = e.detail.value)} />

          <ComboBox label="Estación"
            placeholder="Ingrese la Estación"
            aria-label="Estación"
            items={listaEstaciones.value}
            value={estacion.value}
            onValueChanged={(e) => (estacion.value = e.detail.value)} />
        </VerticalLayout>
      </Dialog>
      <Button onClick={() => dialogOpened.value = true}>Agregar</Button>
    </>
  );
}

// Actualizar ordenDespacho
function OrdenDespachoEntryFormPropsUpdate(props: OrdenDespachoEntryFormPropsUpdate) {
  const dialogOpened = useSignal(false);

  const id = useSignal(props.ordenDespacho?.id?.toString() || '');
  const codigo = useSignal(props.ordenDespacho?.codigo || '');
  const nroGalones = useSignal(props.ordenDespacho?.nroGalones || '');
  const fecha = useSignal(props.ordenDespacho?.fecha || '');
  const precioTotal = useSignal(props.ordenDespacho?.precioTotal?.toString() || '');
  const estado = useSignal(props.ordenDespacho?.estado || '');
  const vehiculo = useSignal(props.ordenDespacho?.vehiculo || '');
  const precioEstablecido = useSignal(props.ordenDespacho?.precioEstablecido || '');
  const estacion = useSignal(props.ordenDespacho?.estacion || '');

  const openDialog = () => {
    id.value = props.ordenDespacho?.id?.toString() || '';
    codigo.value = props.ordenDespacho?.codigo || '';
    nroGalones.value = props.ordenDespacho?.nroGalones?.toString() || '';
    fecha.value = props.ordenDespacho?.fecha || '';
    precioTotal.value = props.ordenDespacho?.precioTotal?.toString() || '';
    estado.value = props.ordenDespacho?.estado || '';
    vehiculo.value = props.ordenDespacho?.vehiculo || '';
    precioEstablecido.value = props.ordenDespacho?.precioEstablecido || '';
    estacion.value = props.ordenDespacho?.estacion || '';
    dialogOpened.value = true;
  };

  const updateordenDespacho = async () => {
    try {
      if (codigo.value.trim().length > 0 && fecha.value.trim().length > 0 && estado.value.trim().length > 0) {
        await OrdenDespachoService.update(
          parseInt(id.value),
          codigo.value,
          nroGalones.value,
          fecha.value,
          parseInt(precioTotal.value)
        );

        if (props.onOrdenDespachopdated) props.onOrdenDespachopdated();
        dialogOpened.value = false;
        Notification.show('Vehículo editado', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo editar, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }
    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };

  const listaVehiculo = useSignal<string[]>([]);
  useEffect(() => {
    OrdenDespachoService.listaVehiculoCombo().then(data =>
      listaVehiculo.value = data
    );
  }, []);

  const listaPreciosEstablecidos = useSignal<string[]>([]);
  useEffect(() => {
    OrdenDespachoService.listaPrecioEstablecidosCombo.then(data =>
      listaPreciosEstablecidos.value = data
    );
  }, []);


  const listaEstacion = useSignal<string[]>([]);
  useEffect(() => {
    OrdenDespachoService.listaEstacionCombo.then(data =>
      listaEstacion.value = data
    );
  }, []);


  return (
    <>
      <Dialog modeless headerTitle="Editar OrdenDespacho"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => (dialogOpened.value = detail.value)}
        footer={
          <>
            <Button onClick={() => (dialogOpened.value = false)}>Cancelar</Button>
            <Button onClick={updateordenDespacho} theme="primary">Registrar</Button>
          </>
        }>
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField label="codigo"
            value={codigo.value}
            defaultValue={codigo.value}
            onValueChanged={(evt) => (codigo.value = evt.detail.value)}
          />

          <TextField label="nroGalones"
            value={nroGalones.value}
            defaultValue={nroGalones.value}
            onValueChanged={(evt) => (nroGalones.value = evt.detail.value)}
          />

          <TextField label="fecha"
            value={fecha.value} 
            defaultValue={fecha.value}
            onValueChanged={(evt) => (fecha.value = evt.detail.value)}
          />

          <ComboBox label="precioTotal" items={precioTotal.value}
            value={precioTotal.value}
            defaultValue={precioTotal.value}
            onValueChanged={(evt) => (precioTotal.value = evt.detail.value)}
          />
          <ComboBox label="estado"
            value={estado.value}
            defaultValue={estado.value}
            onValueChanged={(evt) => (estado.value = evt.detail.value)}
          />
          <ComboBox label="Vehículo"

            value={vehiculo.value}
            defaultValue={vehiculo.value}
            onValueChanged={(evt) => (vehiculo.value = evt.detail.value)}
          />
          <ComboBox label="Estación"
            value={estacion.value}
            defaultValue={estacion.value}
            onValueChanged={(evt) => (estacion.value = evt.detail.value)}
          />
        </VerticalLayout>
      </Dialog>
      <Button onClick={openDialog}>Editar</Button>
    </>
  );
}


// Lista de Orden
export default function OrdenDespachoView() {
  const [items, setItems] = useState([]);
  useEffect(() => {
    OrdenDespachoService.listAll().then(function (data) {
      //console.log(data);
      setItems(data);
    });
  }, []);


  const order = (event, columnId) => {
    const direction = event.detail.value;
    if (!direction) {

      OrdenDespachoService.listAll().then(setItems);
    } else {
      var dir = (direction == 'asc') ? 1 : 2;
      OrdenDespachoService.order(columnId, dir).then(setItems);
    }
  }

  const callData = () => {
    OrdenDespachoService.listAll().then(function (data) {
      //console.log(data);
      setItems(data);
    });
  }

  function indexLink({ model }: { model: GridItemModel<OrdenDespacho> }) {
    return (
      <span>
        <OrdenDespachoEntryFormPropsUpdate ordenDespacho={model.item} onOrdenDespachopdated={callData} />
      </span>
    );
  }


  function indexIndex({ model }: { model: GridItemModel<OrdenDespacho> }) {
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
        label: 'Codigo',
        value: 'codigo',
      }
    ];
  
  
    const search = async () => {
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

    return (

      <main className="w-full h-full flex flex-col box-border gap-s p-m">
  
        <ViewToolbar title="Lista Orden Despacho">
          <Group>
            <OrdenDespachoEntryForm onOrdenDespachoreated={callData} />
          </Group>
        </ViewToolbar>
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
  
        </HorizontalLayout>
        <Grid items={items}>
          <GridColumn renderer={indexIndex} header="Nro" />
          <GridSortColumn onDirectionChanged={(e) => order(e, "codigo")} path="codigo" header="Codigo" />
          <GridSortColumn onDirectionChanged={(e) => order(e, "nroGalones")} path="nroGalones" header="Nro Galones" />
          <GridSortColumn onDirectionChanged={(e) => order(e, "fecha")} path="fecha" header="Fecha" />
          <GridSortColumn onDirectionChanged={(e) => order(e, "precioTotal")} path="precioTotal" header="Precio Total" />
          <GridSortColumn onDirectionChanged={(e) => order(e, "estado")} path="estado" header="Estado" />
          <GridSortColumn onDirectionChanged={(e) => order(e, "vehiculo")} path="vehiculo" header="Vehículo" />
          <GridSortColumn onDirectionChanged={(e) => order(e, "precioEstablecido")} path="precioEstablecido" header="Precio Establecido" />
          <GridSortColumn onDirectionChanged={(e) => order(e, "estacion")} path="estacion" header="Estación" />
          <GridColumn header="Acciones" renderer={indexLink} />
        </Grid>
      </main>
    );

}
