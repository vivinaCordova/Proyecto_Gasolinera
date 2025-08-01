import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, Card, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, GridSortColumn, HorizontalLayout, Icon, NumberField, Select, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { CuentaService, TanqueService, TaskService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import Tanque from 'Frontend/generated/org/unl/gasolinera/taskmanagement/domain/Task';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect, useState } from 'react';
import { role } from 'Frontend/security/auth';
import { logout } from '@vaadin/hilla-frontend';

export const config: ViewConfig = {
  title: 'Tanque',
  menu: {
    icon: 'vaadin:cube',
    order: 1,
    title: 'Tanque',
  },
};

type TanqueEntryFormProps = {
  onTanqueCreated?: () => void;
};

function TanqueEntryForm(props: TanqueEntryFormProps) {
  useEffect(() => {
    role().then(async (data) => {
      if (data?.rol != 'ROLE_admin') {
        await CuentaService.logout();
        await logout();
      }
    });
  }, []);
  const codigo = useSignal('');
  const capacidad = useSignal('');
  const capacidadMinima = useSignal('');
  const capacidadTotal = useSignal('');
  const tipoCombustible = useSignal('');
  const createTanque = async () => {
    try {
      if (capacidad.value.trim().length > 0 && capacidadMinima.value.trim().length > 0 && capacidadTotal.value.trim().length > 0 && tipoCombustible.value.trim().length > 0 && codigo.value.trim().length > 0) {

        await TanqueService.createTanque(
          parseFloat(capacidad.value),
          parseFloat(capacidadTotal.value),
          parseFloat(capacidadMinima.value),
          parseInt(tipoCombustible.value),
          codigo.value
        );
        if (props.onTanqueCreated) {
          props.onTanqueCreated();
        }
        codigo.value = '';
        capacidad.value = '';
        capacidadMinima.value = '';
        capacidadTotal.value = '';
        tipoCombustible.value = '';
        dialogOpened.value = false;
        Notification.show('Tanque creado', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos obligatorios', { duration: 5000, position: 'top-center', theme: 'error' });
      }

    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };


  let listaTipo = useSignal<any[]>([]);
  useEffect(() => {
    TanqueService.listTipo().then(data => {
      if (data) {
        listaTipo.value = data;
      }
    });
  }, []);


  const dialogOpened = useSignal(false);
  return (
    <>
      <Dialog
        modeless
        headerTitle="Nuevo Tanque"
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
            <Button onClick={createTanque} theme="primary">
              Registrar
            </Button>

          </>
        }
      >

        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField label="Codigo"
            placeholder="Ingrese el codigo del Tanque"
            aria-label="Nombre del codigo"
            value={codigo.value}
            onValueChanged={(evt) => (codigo.value = evt.detail.value)}
          />
          <NumberField label="Capacidad"
            placeholder="Ingrese la capacidad actual del Tanque"
            aria-label="Nombre del tanque"
            value={capacidad.value}
            onValueChanged={(evt) => (capacidad.value = evt.detail.value)}
          />
          <ComboBox label="Tipo"
            items={listaTipo.value}
            itemLabelPath="label"
            itemValuePath="value"
            placeholder='Seleccione un tipo'
            aria-label='Seleccione un tipo de la lista'
            value={tipoCombustible.value}
            onValueChanged={(evt) => (tipoCombustible.value = evt.detail.value)}
          />
          <NumberField label="Capacidad Minima"
            placeholder='Ingrese la capacidad minima'
            aria-label='Ingrese la capacidad minima '
            value={capacidadMinima.value}
            onValueChanged={(evt) => (capacidadMinima.value = evt.detail.value)}
          />
          <NumberField label="Capacidad Maxima"
            placeholder='Ingrese capacidad la capacidad maxima'
            aria-label='Ingrese capacidad la capacidad maxima'
            value={capacidadTotal.value}
            onValueChanged={(evt) => (capacidadTotal.value = evt.detail.value)}
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

//LiSTA TANQUES
export default function TanqueView() {
  const [items, setItems] = useState([]);
  //useEffect(() =>{
  const callData = () => {
    TanqueService.listAll().then(function (data) {
      //items.values = data;
      setItems(data);
    });
  };
  useEffect(() => {
    callData();
  }, []);
  const order = (event, columnId) => {
    console.log(event);
    const direction = event.detail.value;
    console.log(`Sort direction changed for column ${columnId} to ${direction}`);

    var dir = (direction == 'asc') ? 1 : 2;
    TanqueService.order(columnId, dir).then(function (data) {
      setItems(data);
    });
  }
  function indexIndex({ model }: { model: GridItemModel<Tanque> }) {
    return (
      <span>
        {model.index + 1}
      </span>
    );
  }

  function capacidad({ item }: { item: Tanque }) {
    return (
      <span>
        {item.capacidad} Gl
      </span>
    );
  }

  function capacidadMinima({ item }: { item: Tanque }) {
    return (
      <span>
        {item.capacidadMinima} Gl
      </span>
    );
  }

  function capacidadTotal({ item }: { item: Tanque }) {
    return (
      <span>
        {item.capacidadTotal} Gl
      </span>
    );
  }

  const criterio = useSignal('');
  const texto = useSignal('');
  const itemSelect = [
    {

      label: 'Codigo',
      value: 'codigo',
    },
    {

      label: 'Tipo de Combustible',
      value: 'tipoCombustible',
    }

  ]
  const search = async () => {
    try {
      TanqueService.search(criterio.value, texto.value, 0).then(function (data) {
        setItems(data);
      });
      criterio.value = '';
      texto.value = '';
      Notification.show('busqueda realizada', { duration: 5000, position: 'bottom-end', theme: 'success' });

    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };
  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Lista de Tanques">
        <Group>
          <TanqueEntryForm onTanqueCreated={callData} />
        </Group>
      </ViewToolbar>
      <HorizontalLayout theme="spacing">
        <Select items={itemSelect}
          value={criterio.value}
          onValueChanged={(evt) => (criterio.value = evt.detail.value)}
          placeholder="Seleccione un criterio">

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
        <Button onClick={callData} theme="secondary">
          <Icon icon="vaadin:refresh" />
        </Button>
      </HorizontalLayout>

      <Grid items={items}>
        <GridColumn renderer={indexIndex} header="Nro" />
        <GridSortColumn path="codigo" header="Codigo" onDirectionChanged={(e) => order(e, 'nombre')} />
        <GridSortColumn renderer={capacidad} header="Capacidad" onDirectionChanged={(e) => order(e, 'nombre')} />
        <GridSortColumn renderer={capacidadMinima} header="Capacidad Minima" onDirectionChanged={(e) => order(e, 'nombre')} />
        <GridSortColumn renderer={capacidadTotal} header="Capacidad Maxima" onDirectionChanged={(e) => order(e, 'nombre')} />
        <GridColumn path="tipoCombustible" header="Tipo" />
        <GridColumn
          header="Acciones"
          renderer={({ item }: { item: Tanque }) => (
            <Button
              theme="primary"
              onClick={async () => {
                try {
                  // Llamar al método del servicio para aumentar el stock
                  const resultado = await TanqueService.aumentarStock(item.codigo);

                  // Mostrar notificaciones según el resultado
                  if (resultado) {
                    Notification.show(
                      `El stock del tanque ${item.codigo} se ha aumentado exitosamente.`,
                      { duration: 5000, position: 'bottom-end', theme: 'success' }
                    );
                  } else {
                    Notification.show(
                      `El tanque ${item.codigo} no requiere reposición.`,
                      { duration: 5000, position: 'top-center', theme: 'warning' }
                    );
                  }

                  // Refrescar la lista de tanques después de la operación
                  await callData();
                } catch (error) {
                  console.error('Error al intentar aumentar el stock:', error);

                  // Mostrar notificación de error
                  Notification.show(
                    `Error al intentar aumentar el stock del tanque ${item.codigo}. Por favor, inténtelo de nuevo.`,
                    { duration: 5000, position: 'top-center', theme: 'error' }
                  );
                }
              }}
            >
              Aumentar Stock
            </Button>
          )}
        />
      </Grid>
    </main>
  );
}