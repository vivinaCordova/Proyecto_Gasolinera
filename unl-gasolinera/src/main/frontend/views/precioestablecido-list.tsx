import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, GridSortColumn, HorizontalLayout, Icon, NumberField, Select, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { OrdenDespachoService, PrecioEstablecidoService, TaskService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import Task from 'Frontend/generated/org/unl/gasolinera/taskmanagement/domain/Task';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect, useState } from 'react';

export const config: ViewConfig = {
  title: 'Precio Establecido',
  menu: {
    icon: 'vaadin:money',
    order: 3,
    title: 'Precio Establecido',
  },
};

type PrecioEstablecido = {
  id: number;
  fecha: Date | string;
  fechaFin: Date | string;
  estado: string;
  precio: number;
  tipoCombustible: string;
};

type PrecioEstablecidoEntryFormProps = {
  onPrecioEstablecidoCreated?: () => void;
};

type PrecioEstablecidoEntryFormPropsUpdate = {
  precioEstablecido: PrecioEstablecido;
  onPrecioEstablecidoUpdated?: () => void;
};


// Guardar Precio Establecido
function PrecioEstablecidoForm(props: PrecioEstablecidoEntryFormProps) {
  const fecha = useSignal('');
  const fechaFin = useSignal('');
  const estado = useSignal('');
  const precio = useSignal('');
  const tipoCombustible = useSignal('');


  const createPrecioEstablecido = async () => {
    try {
      if (
        fecha.value.trim().length > 0 &&
        fechaFin.value.trim().length > 0 &&
        precio.value.trim().length > 0 &&
        tipoCombustible.value.trim().length > 0
      ) {
        await PrecioEstablecidoService.create(
          fecha.value,
          fechaFin.value,
          (estado.value == "ACTIVO" ? true : false),
          parseFloat(precio.value),
          tipoCombustible.value
        );

        if (props.onPrecioEstablecidoCreated) props.onPrecioEstablecidoCreated();

        fecha.value = '';
        fechaFin.value = '';
        estado.value = '';
        precio.value = '';
        tipoCombustible.value = '';

        dialogOpened.value = false;
        Notification.show('Precio establecido creado', {
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

  let listaTipoCombustible = useSignal<string[]>([]);
  useEffect(() => {
    PrecioEstablecidoService.listTipoCombustible().then(data => {
      if (data) {
        listaTipoCombustible.value = data.filter((item): item is string => item !== undefined);
      }
    });
  }, []);

  const dialogOpened = useSignal(false);

  return (
    <>
      <Dialog
        modeless
        headerTitle="Nuevo Precio Establecido"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => {
          dialogOpened.value = detail.value;
        }}
        footer={
          <>
            <Button onClick={() => (dialogOpened.value = false)}>Cancelar</Button>
            <Button onClick={createPrecioEstablecido} theme="primary">Registrar</Button>
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <DatePicker
            label="Fecha de Inicio"
            placeholder="Ingrese la fecha de inicio"
            aria-label="Fecha de Inicio"
            value={fecha.value}
            onValueChanged={(e) => (fecha.value = e.detail.value)}
          />
          <DatePicker
            label="Fecha de Fin"
            placeholder="Ingrese la fecha de fin"
            aria-label="Fecha de Fin"
            value={fechaFin.value}
            onValueChanged={(e) => (fechaFin.value = e.detail.value)}
          />
          <ComboBox
            label="Estado"
            placeholder="Ingrese el estado"
            aria-label="Estado"
            items={['Activo', 'Inactivo']}
            value={estado.value}
            onValueChanged={(e) => (estado.value = e.detail.value)}
          />
          <NumberField
            label="Precio"
            placeholder="Ingrese el precio"
            aria-label="Precio"
            value={precio.value}
            onValueChanged={(e) => (precio.value = e.detail.value)}
          />
          <ComboBox
            label="Tipo de Combustible"
            placeholder="Ingrese el tipo de combustible"
            aria-label="Tipo de Combustible"
            items={listaTipoCombustible.value}
            value={tipoCombustible.value}
            onValueChanged={(e) => (tipoCombustible.value = e.detail.value)}
          />
        </VerticalLayout>
      </Dialog>
      <Button onClick={() => (dialogOpened.value = true)}>Agregar</Button>
    </>
  );
}

// ACTUALIZAR PrecioEstablecido
function PrecioEstablecidoEntryFormUpdate(props: PrecioEstablecidoEntryFormPropsUpdate) {
  const dialogOpened = useSignal(false);
  const id = useSignal(props.precioEstablecido.id.toString());

  // Helper function to convert date to string
  const dateToString = (date: any): string => {
    if (date instanceof Date) {
      return date.toISOString().split('T')[0];
    }
    if (typeof date === 'string') {
      return date.split('T')[0]; // In case it's already an ISO string
    }
    return date ? date.toString() : '';
  };

  const fecha = useSignal(dateToString(props.precioEstablecido.fecha));
  const fechaFin = useSignal(dateToString(props.precioEstablecido.fechaFin));
  const estado = useSignal(props.precioEstablecido.estado);
  const precio = useSignal(props.precioEstablecido.precio.toString());
  const tipoCombustible = useSignal(props.precioEstablecido.tipoCombustible);

  let listaTipoCombustible = useSignal<string[]>([]);
  useEffect(() => {
    PrecioEstablecidoService.listTipoCombustible().then(data => {
      if (data) {
        listaTipoCombustible.value = data.filter((item): item is string => item !== undefined);
      }
    });
  }, []);

  const openDialog = () => {
    id.value = props.precioEstablecido.id.toString();
    fecha.value = dateToString(props.precioEstablecido.fecha);
    fechaFin.value = dateToString(props.precioEstablecido.fechaFin);
    estado.value = props.precioEstablecido.estado;
    precio.value = props.precioEstablecido.precio.toString();
    tipoCombustible.value = props.precioEstablecido.tipoCombustible;
    dialogOpened.value = true;
  };

  const updatePrecioEstablecido = async () => {
    try {
      if (fecha.value && fechaFin.value && estado.value && precio.value && tipoCombustible.value) {
        await PrecioEstablecidoService.update(
          parseInt(id.value),
          fecha.value,
          fechaFin.value,
          estado.value === 'Activo',
          parseFloat(precio.value),
          tipoCombustible.value
        );
        props.onPrecioEstablecidoUpdated?.();
        dialogOpened.value = false;
        Notification.show('Precio Establecido actualizado', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('Faltan datos para actualizar', { duration: 5000, position: 'top-center', theme: 'error' });
      }
    } catch (error) {
      handleError(error);
    }
  };

  return (
    <>
      <Dialog modeless headerTitle="Editar Precio Establecido" opened={dialogOpened.value} onOpenedChanged={({ detail }) => dialogOpened.value = detail.value}
        footer={<><Button onClick={() => dialogOpened.value = false}>Cancelar</Button><Button onClick={updatePrecioEstablecido} theme="primary">Registrar</Button></>}>
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <DatePicker label="Fecha de Inicio" value={fecha.value} onValueChanged={(e) => fecha.value = e.detail.value} />
          <DatePicker label="Fecha de Fin" value={fechaFin.value} onValueChanged={(e) => fechaFin.value = e.detail.value} />
          <ComboBox label="Estado" items={['Activo', 'Inactivo']} value={estado.value} onValueChanged={(e) => estado.value = e.detail.value} />
          <NumberField label="Precio" value={precio.value} onValueChanged={(e) => precio.value = e.detail.value} />
          <ComboBox label="Tipo de Combustible" items={listaTipoCombustible.value} value={tipoCombustible.value} onValueChanged={(e) => tipoCombustible.value = e.detail.value} />
        </VerticalLayout>
      </Dialog>
      <Button onClick={openDialog}>Actualizar</Button>
    </>
  );
}

export default function PrecioEstablecidoView() {
  const [items, setItems] = useState<PrecioEstablecido[]>([]);
  const callData = () => PrecioEstablecidoService.listAll().then(data => {
    if (data) {
      setItems(data as PrecioEstablecido[]);
    }
  });

  useEffect(() => {
    callData();
  }, []);

  const order = (event: any, columnId: string) => {
    const direction = event.detail.value;
    if (!direction) callData();
    else PrecioEstablecidoService.order(columnId, direction === 'asc' ? 1 : 2).then(data => {
      if (data) {
        setItems(data as PrecioEstablecido[]);
      }
    });
  };

  const indexLink = ({ model }: { model: GridItemModel<PrecioEstablecido> }) => (
    <PrecioEstablecidoEntryFormUpdate precioEstablecido={model.item} onPrecioEstablecidoUpdated={callData} />
  );

  const indexIndex = ({ model }: { model: GridItemModel<PrecioEstablecido> }) => (
    <span>{model.index + 1}</span>
  );

  function renderEstado({ model }: { model: GridItemModel<PrecioEstablecido> }) {
    return <span>{model.item.estado ? 'Activo' : 'Inactivo'}</span>;
  }

  const criterio = useSignal('');
  const texto = useSignal('');

  const itemSelect = [
    {
      label: 'Fecha Inicio',
      value: 'fecha',
    },
    {
      label: 'Fecha Fin',
      value: 'fechaFin',
    },
    {
      label: 'Estado',
      value: 'estado',
    }
  ];

  const search = async () => {
    try {
      PrecioEstablecidoService.search(criterio.value, texto.value, 0).then(function (data) {
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

  function precioRenderer({ model }: { model: GridItemModel<PrecioEstablecido> }) {
      const precio = model.item.precio;
      return <span>${precio ? Number(precio).toFixed(2) : '0.00'}</span>;
    }

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Lista de Precio Establecidos">
        <Group>
          <PrecioEstablecidoForm onPrecioEstablecidoCreated={callData} />
        </Group>
      </ViewToolbar>
      <HorizontalLayout theme="spacing">
        <Select items={itemSelect}
          value={criterio.value}
          onValueChanged={(evt) => {
            criterio.value = evt.detail.value;
            texto.value = '';
          }}
          placeholder={'Seleccione un criterio'}>

        </Select>

        {criterio.value === 'estado' ? (
          <Select
            items={[
              { label: 'Activo', value: 'true' },
              { label: 'Inactivo', value: 'false' },
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
          <Icon icon="vaadin:refresh" />
        </Button>

      </HorizontalLayout>
      <Grid items={items}>
        <GridColumn renderer={indexIndex} header="Nro" />
        <GridSortColumn path="fecha" onDirectionChanged={(e) => order(e, 'fecha')} header="Fecha Inicio" width="320px" flexGrow={0} />
        <GridSortColumn path="fechaFin" onDirectionChanged={(e) => order(e, 'fechaFin')} header="Fecha Fin" width="320px" flexGrow={0} />
        <GridSortColumn path="estado" onDirectionChanged={(e) => order(e, 'estado')} header="Estado" renderer={renderEstado} />
        <GridSortColumn path="precio" onDirectionChanged={(e) => order(e, 'precio')} header="Precio" renderer={precioRenderer} />
        <GridSortColumn path="tipoCombustible" onDirectionChanged={(e) => order(e, 'tipoCombustible')} header="Tipo de Combustible" width="200px" flexGrow={0} />
        <GridColumn header="Actualizar" renderer={indexLink} />
      </Grid>
    </main>
  );
}