import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, NumberField, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { OrdenDespachoService, PrecioEstablecidoService, TaskService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import Task from 'Frontend/generated/org/unl/gasolinera/taskmanagement/domain/Task';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect } from 'react';

export const config: ViewConfig = {
  title: 'Precio Establecido',
  menu: {
    icon: 'vaadin:money',
    order: 3,
    title: 'Precio Establecido',
  },
};

type PrecioEstablecidoFormProps = {
  onPrecioCreated?: () => void;
};

function PrecioEstablecidoForm(props: PrecioEstablecidoFormProps) {
  const fecha = useSignal('');
  const fechaFin = useSignal('');
  const estado = useSignal('Activo');
  const precio = useSignal('');
  const tipoCombustible = useSignal('');
  const listaTipo = useSignal<string[]>([]);

  useEffect(() => {
    PrecioEstablecidoService.listTipoCombustible().then(data => {
      listaTipo.value = data;
    });
  }, []);

  const createPrecio = async () => {
    try {
      if (
        fecha.value &&
        fechaFin.value &&
        precio.value &&
        tipoCombustible.value
      ) {
        await PrecioEstablecidoService.create(
          new Date(fecha.value),
          new Date(fechaFin.value),
          estado.value === 'Activo',
          parseFloat(precio.value),
          tipoCombustible.value
        );

        if (props.onPrecioCreated) props.onPrecioCreated();

        fecha.value = '';
        fechaFin.value = '';
        estado.value = 'Activo';
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
            <Button onClick={createPrecio} theme="primary">Registrar</Button>
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <DatePicker
            label="Fecha de Inicio"
            value={fecha.value}
            onValueChanged={(e) => (fecha.value = e.detail.value)}
          />
          <DatePicker
            label="Fecha de Fin"
            value={fechaFin.value}
            onValueChanged={(e) => (fechaFin.value = e.detail.value)}
          />
          <ComboBox
            label="Estado"
            items={['Activo', 'Inactivo']}
            value={estado.value}
            onValueChanged={(e) => (estado.value = e.detail.value)}
          />
          <NumberField
            label="Precio"
            placeholder="Ingrese el precio"
            value={precio.value}
            onValueChanged={(e) => (precio.value = e.detail.value)}
          />
          <ComboBox
            label="Tipo de Combustible"
            items={listaTipo.value}
            value={tipoCombustible.value}
            onValueChanged={(e) => (tipoCombustible.value = e.detail.value)}
          />
        </VerticalLayout>
      </Dialog>
      <Button onClick={() => (dialogOpened.value = true)}>Agregar</Button>
    </>
  );
}

export default function PrecioEstablecidoView() {
  const dataProvider = useDataProvider<any>({
    list: () => PrecioEstablecidoService.listPrecioEstablecido(),
  });

  function indexIndex({ model }: { model: GridItemModel<any> }) {
    return <span>{model.index + 1}</span>;
  }

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Lista de Precios Establecidos">
        <Group>
          <PrecioEstablecidoForm onPrecioCreated={dataProvider.refresh} />
        </Group>
      </ViewToolbar>

      <Grid dataProvider={dataProvider.dataProvider}>
        <GridColumn renderer={indexIndex} header="Nro" />
        <GridColumn path="fecha" header="Fecha Inicio" />
        <GridColumn path="fechaFin" header="Fecha Fin" />
        <GridColumn path="estado" header="Estado" />
        <GridColumn path="precio" header="Precio" />
        <GridColumn path="tipoCombustible" header="Tipo de Combustible" />
      </Grid>
    </main>
  );
}