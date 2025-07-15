import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, GridSortColumn, NumberField, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { VehiculoService, TaskService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';

import { useDataProvider } from '@vaadin/hilla-react-crud';

import { useCallback, useEffect, useState } from 'react';

export const config: ViewConfig = {
  title: 'Vehiculo',
  menu: {
    icon: 'vaadin:car',
    order: 6,
    title: 'Vehiculo',
  },
};

type Vehiculo = {
  id: number;
  placa: string;
  modelo: string;
  marca: string;
  propietario: string;
  
};


type VehiculoEntryFormProps = {
  onVehiculoCreated?: () => void;
};

type VehiculoEntryFormPropsUpdate = {
  vehiculo: Vehiculo;
  onVehiculoUpdated?: () => void;
};

//GUARDAR Vehiculo
function VehiculoEntryForm(props: VehiculoEntryFormProps) {
  const placa = useSignal('');
  const modelo = useSignal('');
  const marca = useSignal('');
  const propietario = useSignal('');

  const createVehiculo = async () => {
    try {
      if (placa.value.trim().length > 0 && modelo.value.trim().length > 0 && marca.value.trim().length > 0 && propietario.value.trim().length > 0) {
        const id_propietario = parseInt(propietario.value);
        await VehiculoService.create(placa.value, modelo.value, marca.value, id_propietario);
        if (props.onVehiculoCreated) {
          props.onVehiculoCreated();
        }
        placa.value = '';
        modelo.value = '';
        marca.value = '';
        propietario.value = '';
        

        dialogOpened.value = false;
        Notification.show('Vehiculo creada', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }

    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };

  let listaPersona = useSignal<String[]>([]);
  useEffect(() => {
    VehiculoService.listPersonaCombo().then(data =>
      //console.log(data)
      listaPersona.value = data
    );
  }, []);
  
  const dialogOpened = useSignal(false);
  return (
    <>
      <Dialog
        modeless
        headerTitle="Nuevo Vehiculo"
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
            <Button onClick={createVehiculo} theme="primary">
              Registrar
            </Button>

          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField label="Placa de la Vehiculo"
            placeholder="Ingrese el placa de la Vehiculo"
            aria-label="placa de la Vehiculo"
            value={placa.value}
            onValueChanged={(evt) => (placa.value = evt.detail.value)}
          />
          <TextField label="Modelo del Vehiculo"
            placeholder="Ingrese el modelo del Vehiculo"
            aria-label="Modelo del Vehiculo"
            value={modelo.value}
            onValueChanged={(evt) => (modelo.value = evt.detail.value)}
          />
           <TextField label="Marca del Vehiculo"
            placeholder="Ingrese la Marca del Vehiculo"
            aria-label="Marca del Vehiculo"
            value={marca.value}
            onValueChanged={(evt) => (marca.value = evt.detail.value)}
          />
          <ComboBox label="Propietario"
            items={listaPersona.value}
            placeholder='Seleccione un Propietario'
            aria-label='Seleccione un Propietario de la lista'
            value={propietario.value}
            onValueChanged={(evt) => (propietario.value = evt.detail.value)}
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

// ACTUALIZAR Vehiculo
function VehiculoEntryFormUpdate(props: VehiculoEntryFormPropsUpdate) {
  const dialogOpened = useSignal(false);

  // Inicializa los campos con los datos de la Vehiculo
  const id = useSignal(props.vehiculo?.id?.toString() || '');
  const placa = useSignal(props.vehiculo?.placa || '');
  const modelo = useSignal(props.vehiculo?.modelo || '');
  const marca = useSignal(props.vehiculo?.marca || '');
  const propietario = useSignal(props.vehiculo?.propietario || '');

  // Cuando se abre el diálogo, actualiza los valores
  const openDialog = () => {
    id.value = props.vehiculo?.id?.toString() || '';
    placa.value = props.vehiculo?.placa || '';
    modelo.value = props.vehiculo?.modelo || '';
    marca.value = props.vehiculo?.marca || '';
    propietario.value = props.vehiculo?.propietario || '';
    dialogOpened.value = true;
  };

  const updateVehiculo = async () => {
    try {
      if (placa.value.trim().length > 0 && modelo.value.trim().length > 0 && marca.value.trim().length > 0 && propietario.value.trim().length > 0) {
        const id_propietario = parseInt(propietario.value);
        await VehiculoService.update(parseInt(id.value),placa.value, modelo.value, marca.value, id_propietario);
        if (props.onVehiculoUpdated) {
          props.onVehiculoUpdated();
        }
        placa.value = '';
        modelo.value = '';
        marca.value = '';
        propietario.value = '';
        

        dialogOpened.value = false;
        Notification.show('Vehiculo creado', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }

    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };

  let listaPersona = useSignal<String[]>([]);
  useEffect(() => {
    VehiculoService.listPersonaCombo().then(data =>
      //console.log(data)
      listaPersona.value = data
    );
  }, []);
  

  return (
    <>
      <Dialog
        modeless
        headerTitle="Editar Vehiculo"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => {
          dialogOpened.value = detail.value;
        }}
        footer={
          <>
            <Button onClick={() => { dialogOpened.value = false; }}>Cancelar</Button>
            <Button onClick={updateVehiculo} theme="primary">Registrar</Button>
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField label="Placa de la Vehiculo"
            placeholder="Ingrese el placa de la Vehiculo"
            aria-label="placa de la Vehiculo"
            value={placa.value}
            onValueChanged={(evt) => (placa.value = evt.detail.value)}
          />
          <TextField label="Modelo del Vehiculo"
            placeholder="Ingrese el modelo del Vehiculo"
            aria-label="Modelo del Vehiculo"
            value={modelo.value}
            onValueChanged={(evt) => (modelo.value = evt.detail.value)}
          />
           <TextField label="Marca del Vehiculo"
            placeholder="Ingrese la Marca del Vehiculo"
            aria-label="Marca del Vehiculo"
            value={marca.value}
            onValueChanged={(evt) => (marca.value = evt.detail.value)}
          />
          <ComboBox label="Propietario"
            items={listaPersona.value}
            placeholder='Seleccione un Propietario'
            aria-label='Seleccione un Propietario de la lista'
            value={propietario.value}
            onValueChanged={(evt) => (propietario.value = evt.detail.value)}
          />
        </VerticalLayout>
      </Dialog>
      <Button onClick={openDialog}>Editar</Button>
    </>
  );
}


//Lista de Vehiculos
export default function VehiculoView() {
  const [items, setItems] = useState([]);
  useEffect(() => {
    VehiculoService.listAll().then(function (data) {
      //console.log(data);
      setItems(data);
    });
  }, []);

  const order = (event, columnId) => {
    const direction = event.detail.value;
    if (!direction) {
      // Sin orden, mostrar lista original
      VehiculoService.listAll().then(setItems);
    } else {
      var dir = (direction == 'asc') ? 1 : 2;
      VehiculoService.order(columnId, dir).then(setItems);
    }
  }

  const callData = () => {
    VehiculoService.listAll().then(function (data) {
      //console.log(data);
      setItems(data);
    });
  }

  function indexLink({ model }: { model: GridItemModel<Vehiculo> }) {
    return (
      <span>
        <VehiculoEntryFormUpdate vehiculo={model.item} onVehiculoUpdated={callData} />
      </span>
    );
  }



  function indexIndex({ model }: { model: GridItemModel<Vehiculo> }) {
    return (
      <span>
        {model.index + 1}
      </span>
    );
  }

  return (

    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Lista de Vehiculos">
        <Group>
          <VehiculoEntryForm onVehiculoCreated={callData} />
        </Group>
      </ViewToolbar>
      <Grid items={items}>
        <GridColumn renderer={indexIndex} header="Nro" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "placa")} path="placa" header="Vehiculo" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "modelo")} path="modelo" header="modelo" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "marca")} path="marca" header="marca" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "propietario")} path="propietario" header="Propietario" />
        <GridColumn header="Acciones" renderer={indexLink} />

      </Grid>
    </main>
  );
}
