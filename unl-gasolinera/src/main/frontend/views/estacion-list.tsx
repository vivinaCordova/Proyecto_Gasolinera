import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, GridSortColumn, HorizontalLayout, Icon, NumberField, Select, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { EstacionService, TaskService} from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useCallback, useEffect, useState } from 'react';
import { search } from 'Frontend/generated/EstacionService';

export const config: ViewConfig = {
  title: 'Estación de Vehículos',
  menu: {
    icon: 'vaadin:building',
    order: 4,
    title: 'Estacion',
  },
};

type Estacion = {
  id: number;
  codigo: string;
  estadoE: string;
};

type EstacionEntryFormProps = {
  onEstacionCreated?: () => void;
};

type EstacionEntryFormPropsUpdate = {
  estacion: Estacion;
  onEstacionUpdated?: () => void;
};

// Guardar Estacion
function EstacionEntryForm(props: EstacionEntryFormProps) {
  const codigo = useSignal('');
  const estadoE = useSignal('');
  
  const dialogOpened = useSignal(false);

  const createEstacion = async () => {
    try {
      if (codigo.value.trim().length > 0 && estadoE.value.trim().length > 0) {
        await EstacionService.create(codigo.value, estadoE.value);
        if (props.onEstacionCreated) {
          props.onEstacionCreated();
        }
        codigo.value = '';
        estadoE.value = '';
        
        dialogOpened.value = false;
        Notification.show('Estacion creado', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }
    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };

  let listaEstado = useSignal<String[]>([]);
    useEffect(() => {
      EstacionService.listEstadoE().then(data =>
        //console.log(data)
        listaEstado.value = data
      );
  }, []);

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
            <Button onClick={() => { dialogOpened.value = false; }}>Cancelar</Button>
            <Button onClick={createEstacion} theme="primary">Registrar</Button>
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField
            label="Codigo"
            placeholder="Ingrese el codigo de la estacion"
            aria-label="Codigo de la estacion"
            value={codigo.value}
            defaultValue={codigo.value}
            onValueChanged={(evt) => (codigo.value = evt.detail.value)}
          />
          <ComboBox
            label="Estado de Uso"
            items={listaEstado.value}
            placeholder="Seleccione un propietario"
            aria-label="Seleccione un propietario de la lista"
            value={estadoE.value}
            defaultValue={estadoE.value}
            onValueChanged={(evt) => (estadoE.value = evt.detail.value)}
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

// Actualizar Estacion
function EstacionEntryFormUpdate(props: EstacionEntryFormPropsUpdate) {
  const dialogOpened = useSignal(false);

  const id = useSignal(props.estacion?.id?.toString() || '');
  const codigo = useSignal(props.estacion?.codigo || '');
  const estadoE = useSignal(props.estacion?.estadoE || '');
  

  const openDialog = () => {
    id.value = props.estacion?.id?.toString() || '';
    codigo.value = props.estacion?.codigo || '';
    estadoE.value = props.estacion?.estadoE || '';
    dialogOpened.value = true;
  };

  const updateEstacion = async () => {
    try {
      if (codigo.value.trim().length > 0 && estadoE.value.trim().length > 0) {
        await EstacionService.update(
          parseInt(id.value), 
          codigo.value, 
          estadoE.value, 
          
        );

        if (props.onEstacionUpdated) props.onEstacionUpdated();
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

  let listaEstado = useSignal<String[]>([]);
  useEffect(() => {
    EstacionService.listEstadoE().then(data =>
      //console.log(data)
      listaEstado.value = data
    );
}, []);

  return (
    <>
      <Dialog modeless headerTitle="Editar Estacion" 
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => (dialogOpened.value = detail.value)}
        footer={
          <>
            <Button onClick={() => (dialogOpened.value = false)}>Cancelar</Button>
            <Button onClick={updateEstacion} theme="primary">Registrar</Button>
          </>
        }>
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField label="Codigo" 
            value={codigo.value} 
            defaultValue={codigo.value} 
            onValueChanged={(evt) => (codigo.value = evt.detail.value)} 
          />

          <ComboBox label="Estado de Uso" items={listaEstado.value}
            value={estadoE.value}
            defaultValue={estadoE.value}
            onValueChanged={(evt) => (estadoE.value = evt.detail.value)}
          />
        </VerticalLayout>
      </Dialog>
      <Button onClick={openDialog}>Editar</Button>
    </>
  );
}

// Lista de Estacions
export default function EstacionView() {
  const [items, setItems] = useState([]);
  useEffect(() => {
    EstacionService.listAll().then(function (data) {
      //console.log(data);
      setItems(data);
    });
  }, []);


  const order = (event, columnId) => {
    const direction = event.detail.value;
    if (!direction) {
      
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

  

  //BUSCAR
  const criterio = useSignal('');
  const texto = useSignal('');

  const itemSelect = [
    {
      label: 'Codigo',
      value: 'Codigo',
    }
  ];


  const search = async () => {
    try {
      EstacionService.search(criterio.value, texto.value, 0).then(function (data) {
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

      <ViewToolbar title="Lista de Estaciones">
        <Group>
          <EstacionEntryForm onEstacionCreated={callData} />
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
        <GridSortColumn path="estado" header="Estado" onDirectionChanged={(e) => order(e, "estado")} />
        <GridColumn header="Acciones" renderer={indexLink} />
      </Grid>
    </main>
  );
}
