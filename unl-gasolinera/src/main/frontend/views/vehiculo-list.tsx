import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, GridSortColumn, HorizontalLayout, Icon, NumberField, Select, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { VehiculoService, TaskService} from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useCallback, useEffect, useState } from 'react';
import { search } from 'Frontend/generated/VehiculoService';

export const config: ViewConfig = {
  title: 'Vehículo',
  menu: {
    icon: 'vaadin:car',
    order: 4,
    title: 'Vehículo',
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

// Guardar Vehiculo
function VehiculoEntryForm(props: VehiculoEntryFormProps) {
  const placa = useSignal('');
  const modelo = useSignal('');
  const marca = useSignal('');
  const propietario = useSignal('');

  const dialogOpened = useSignal(false);

  const createVehiculo = async () => {
    try {
      if (placa.value.trim().length > 0 && modelo.value.trim().length > 0 && marca.value.trim().length > 0) {
        const id_Propietario = parseInt(propietario.value)
        await VehiculoService.create(placa.value, modelo.value, marca.value, id_Propietario);
        if (props.onVehiculoCreated) {
          props.onVehiculoCreated();
        }
        placa.value = '';
        modelo.value = '';
        marca.value = '';
        propietario.value = '';
        dialogOpened.value = false;
        Notification.show('Vehículo creado', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }
    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };

  let listaPersona = useSignal<string[]>([]);
  useEffect(() => {
    VehiculoService.listaPersonaCombo().then(data => 
      listaPersona.value = data
    );
  }, []);

  return (
    <>
      <Dialog
        modeless
        headerTitle="Nuevo Vehículo"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => {
          dialogOpened.value = detail.value;
        }}
        footer={
          <>
            <Button onClick={() => { dialogOpened.value = false; }}>Cancelar</Button>
            <Button onClick={createVehiculo} theme="primary">Registrar</Button>
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField
            label="Placa"
            placeholder="Ingrese la placa del vehículo"
            aria-label="Placa del vehículo"
            value={placa.value}
            defaultValue={placa.value}
            onValueChanged={(evt) => (placa.value = evt.detail.value)}
          />
          <TextField
            label="Modelo"
            placeholder="Ingrese el modelo del vehículo"
            aria-label="Modelo del vehículo"
            value={modelo.value}
            defaultValue={modelo.value}
            onValueChanged={(evt) => (modelo.value = evt.detail.value)}
          />
          <TextField
            label="Marca"
            placeholder="Ingrese la marca del vehículo"
            aria-label="Marca del vehículo"
            value={marca.value}
            defaultValue={marca.value}
            onValueChanged={(evt) => (marca.value = evt.detail.value)}
          />
          <ComboBox
            label="Propietario"
            items={listaPersona.value}
            placeholder="Seleccione un propietario"
            aria-label="Seleccione un propietario de la lista"
            value={propietario.value}
            defaultValue={propietario.value}
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

// Actualizar Vehiculo
function VehiculoEntryFormUpdate(props: VehiculoEntryFormPropsUpdate) {
  const dialogOpened = useSignal(false);

  const id = useSignal(props.vehiculo?.id?.toString() || '');
  const placa = useSignal(props.vehiculo?.placa || '');
  const modelo = useSignal(props.vehiculo?.modelo || '');
  const marca = useSignal(props.vehiculo?.marca || '');
  const propietario = useSignal(props.vehiculo?.propietario?.toString() || '');

  const openDialog = () => {
    id.value = props.vehiculo?.id?.toString() || '';
    placa.value = props.vehiculo?.placa || '';
    modelo.value = props.vehiculo?.modelo || '';
    marca.value = props.vehiculo?.marca || '';
    propietario.value = props.vehiculo?.propietario?.toString() || '';
    dialogOpened.value = true;
  };

  const updateVehiculo = async () => {
    try {
      if (placa.value.trim().length > 0 && modelo.value.trim().length > 0 && marca.value.trim().length > 0) {
        await VehiculoService.update(
          parseInt(id.value), 
          placa.value, 
          modelo.value, 
          marca.value, 
          parseInt(propietario.value)
        );

        if (props.onVehiculoUpdated) props.onVehiculoUpdated();
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

  const listaPersona = useSignal<string[]>([]);
  useEffect(() => {
    VehiculoService.listaPersonaCombo().then(data => 
      listaPersona.value = data
    );
  }, []);

  return (
    <>
      <Dialog modeless headerTitle="Editar Vehículo" 
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => (dialogOpened.value = detail.value)}
        footer={
          <>
            <Button onClick={() => (dialogOpened.value = false)}>Cancelar</Button>
            <Button onClick={updateVehiculo} theme="primary">Registrar</Button>
          </>
        }>
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField label="Placa" 
            value={placa.value} 
            defaultValue={placa.value} 
            onValueChanged={(evt) => (placa.value = evt.detail.value)} 
          />

          <TextField label="Modelo" 
            value={modelo.value} 
            defaultValue={modelo.value} 
            onValueChanged={(evt) => (modelo.value = evt.detail.value)} 
          />

          <TextField label="Marca" 
            value={marca.value} defaultValue={marca.value} 
            onValueChanged={(evt) => (marca.value = evt.detail.value)} 
          />

          <ComboBox label="Propietario" items={listaPersona.value}
            value={propietario.value}
            defaultValue={propietario.value}
            onValueChanged={(evt) => (propietario.value = evt.detail.value)}
          />
        </VerticalLayout>
      </Dialog>
      <Button onClick={openDialog}>Editar</Button>
    </>
  );
}

// Lista de Vehiculos
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

  

  //BUSCAR
  const criterio = useSignal('');
  const texto = useSignal('');

  const itemSelect = [
    {
      label: 'Placa',
      value: 'placa',
    }
  ];


  const search = async () => {
    try {
      VehiculoService.search(criterio.value, texto.value, 0).then(function (data) {
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

      <ViewToolbar title="Lista de Vehiculos">
        <Group>
          <VehiculoEntryForm onVehiculoCreated={callData} />
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
        <GridSortColumn onDirectionChanged={(e) => order(e, "placa")} path="placa" header="Placa" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "modelo")} path="modelo" header="Modelo" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "marca")} path="marca" header="Marca" />
        <GridSortColumn path="propietario" header="Propietario" onDirectionChanged={(e) => order(e, "propietario")} />
        <GridColumn header="Acciones" renderer={indexLink} />
      </Grid>
    </main>
  );
}
