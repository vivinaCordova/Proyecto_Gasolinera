import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, GridSortColumn, NumberField, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { CuentaService, EstacionService, TaskService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';

import { useDataProvider } from '@vaadin/hilla-react-crud';

import { useCallback, useEffect, useState } from 'react';
import { role } from 'Frontend/security/auth';
import { logout } from '@vaadin/hilla-frontend';

export const config: ViewConfig = {
  title: 'Estacion',
  menu: {
    icon: 'vaadin:building',
    order: 5,
    title: 'Estacion',
  },
};

type Estacion = {
  id: number;
  codigo: string;
  estado: string;

};

type EstacionEntryFormProps = {
  onEstacionCreated?: () => void;
};

type EstacionEntryFormPropsUpdate = {
  estacion: Estacion;
  onEstacionUpdated?: () => void;
};

//GUARDAR Estacion
function EstacionEntryForm(props: EstacionEntryFormProps) {
  useEffect(() => {
    role().then(async (data) => {
      if (data?.rol != 'ROLE_admin') {
        await CuentaService.logout();
        await logout();
      }
    });
  }, []);
  const codigo = useSignal('');
  const estado = useSignal('');
  const createEstacion = async () => {
    try {
      if (codigo.value.trim().length > 0 && estado.value.trim().length > 0) {
        await EstacionService.create(codigo.value, estado.value);
        if (props.onEstacionCreated) {
          props.onEstacionCreated();
        }
        codigo.value = '';
        estado.value = '';

        dialogOpened.value = false;
        Notification.show('Estacion creada', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }

    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };

  let listaestado = useSignal<String[]>([]);
  useEffect(() => {
    EstacionService.listEstadoE().then(data =>
      //console.log(data)
      listaestado.value = data
    );
  }, []);

  const dialogOpened = useSignal(false);
  return (
    <>
      <Dialog
        modeless
        headerTitle="Nueva Estacion"
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
            <Button onClick={createEstacion} theme="primary">
              Registrar
            </Button>

          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField label="codigo de la estacion"
            placeholder="Ingrese el codigo de la estacion"
            aria-label="codigo de la estacion"
            value={codigo.value}
            onValueChanged={(evt) => (codigo.value = evt.detail.value)}
          />
          <ComboBox label="estado"
            items={listaestado.value}
            placeholder='Seleccione un estado'
            aria-label='Seleccione un estado de la lista'
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

// ACTUALIZAR Estacion
function EstacionEntryFormUpdate(props: EstacionEntryFormPropsUpdate) {
  const dialogOpened = useSignal(false);

  // Inicializa los campos con los datos de la estacion
  const id = useSignal(props.estacion?.id?.toString() || '');
  const codigo = useSignal(props.estacion?.codigo || '');
  const estado = useSignal(props.estacion?.estado || '');

  // Cuando se abre el diálogo, actualiza los valores
  const openDialog = () => {
    id.value = props.estacion?.id?.toString() || '';
    codigo.value = props.estacion?.codigo || '';
    estado.value = props.estacion?.estado || '';
    dialogOpened.value = true;
  };

  const updateEstacion = async () => {
    try {
      if (codigo.value.trim().length > 0 && estado.value.trim().length > 0) {
        await EstacionService.update(
          parseInt(id.value),
          codigo.value,
          estado.value
        );
        if (props.onEstacionUpdated) {
          props.onEstacionUpdated();
        }
        dialogOpened.value = false;
        Notification.show('Estacion editada', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo editar, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }
    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };

  let listaestado = useSignal<String[]>([]);
  useEffect(() => {
    EstacionService.listEstadoE().then(data =>
      //console.log(data)
      listaestado.value = data
    );
  }, []);


  return (
    <>
      <Dialog
        modeless
        headerTitle="Editar estacion"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => {
          dialogOpened.value = detail.value;
        }}
        footer={
          <>
            <Button onClick={() => { dialogOpened.value = false; }}>Cancelar</Button>
            <Button onClick={updateEstacion} theme="primary">Registrar</Button>
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField label="codigo de la estacion"
            placeholder="Ingrese el codigo de la estacion"
            aria-label="codigo de la estacion"
            value={codigo.value}
            defaultValue={codigo.value}
            onValueChanged={(evt) => (codigo.value = evt.detail.value)}
          />
          <ComboBox label="estado"
            items={listaestado.value}
            placeholder='Seleccione un estado'
            aria-label='Seleccione un estado de la lista'
            value={estado.value}
            defaultValue={estado.value}
            onValueChanged={(evt) => (estado.value = evt.detail.value)}
          />
        </VerticalLayout>
      </Dialog>
      <Button onClick={openDialog}>Editar</Button>
    </>
  );
}


//Lista de Estaciones
export default function EstacionView() {
  const [items, setItems] = useState([]);
  useEffect(() => {
    EstacionService.listAll().then(function (data) {
      //console.log(data);
      setItems(data);
    });
  }, []);

  const deleteEstacion = async (estacion: Estacion) => {
    // Mostrar confirmación antes de eliminar
    const confirmed = window.confirm(`¿Está seguro de que desea eliminar la estación con el código ${estacion.codigo}?`);

    if (confirmed) {
      try {
        await EstacionService.delete(estacion.id);
        Notification.show('Estación eliminada exitosamente', {
          duration: 5000,
          position: 'bottom-end',
          theme: 'success'
        });
        // Recargar la lista después de eliminar
        callData();
      } catch (error) {
        console.error('Error al eliminar la estación:', error);
        Notification.show('Error al eliminar la estación', {
          duration: 5000,
          position: 'top-center',
          theme: 'error'
        });
      }
    }
  }

  const order = (event, columnId) => {
    const direction = event.detail.value;
    if (!direction) {
      // Sin orden, mostrar lista original
      EstacionService.listAll().then(setItems);
    } else {
      var dir = (direction == 'asc') ? 1 : 2;
      EstacionService.order(columnId, dir).then(setItems);
    }
  }

  const callData = () => {
    EstacionService.listAll().then(function (data) {
      //console.log(data);
      setItems(data);
    });
  }

  function indexLink({ model }: { model: GridItemModel<Estacion> }) {
    return (
      <span>
        <EstacionEntryFormUpdate estacion={model.item} onEstacionUpdated={callData} />
      </span>
    );
  }



  function indexIndex({ model }: { model: GridItemModel<Estacion> }) {
    return (
      <span>
        {model.index + 1}
      </span>
    );
  }

  return (

    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Lista de Estaciones">
        <Group>
          <EstacionEntryForm onEstacionCreated={callData} />
        </Group>
      </ViewToolbar>
      <Grid items={items}>
        <GridColumn renderer={indexIndex} header="Nro" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "codigo")} path="codigo" header="Estacion" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "estado")} path="estado" header="estado" />
        <GridColumn header="Acciones" renderer={indexLink} />
        <GridColumn
          header="Eliminar"
          renderer={({ item }) => (
            <Button
              theme="error"
              onClick={() => deleteEstacion(item)}
            >
              Eliminar
            </Button>
          )}
        />

      </Grid>
    </main>
  );
}
