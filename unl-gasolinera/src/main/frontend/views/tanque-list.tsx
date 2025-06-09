import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, ComboBox, DatePicker, Dialog, Grid, GridColumn, GridItemModel, NumberField, TextField, VerticalLayout } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { TanqueService, TaskService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';

import { useDataProvider } from '@vaadin/hilla-react-crud';
import Tanque from 'Frontend/generated/org/unl/gasolinera/taskmanagement/domain/Tanque';
import { useCallback, useEffect, useState } from 'react';

export const config: ViewConfig = {
  title: 'Tanque',
  menu: {
    icon: 'vaadin:clipboard-check',
    order: 1,
    title: 'Tanque',
  },
};
type TanqueEntryFormProps = {
  onTanqueCreated?: () => void;
};
//GUARDAR Tanque
function TanqueEntryForm(props: TanqueEntryFormProps) {
  const capacidad = useSignal('');
  const capacidadMinima = useSignal('');
  const capacidadTotal = useSignal('');
  const ordenCompra = useSignal('');
  const ordenDespacho = useSignal('');
  const tipo = useSignal('');
  const createTanque = async () => {
    try {
      if (capacidad.value.trim().length > 0 && capacidadMinima.value.trim().length > 0 && capacidadTotal.value.trim().length > 0 && ordenCompra.value.trim().length > 0 && ordenDespacho.value.trim().length > 0 && tipo.value.trim().length > 0){
        const id_ordenCompra = parseInt(ordenCompra.value)+1;
        const id_OrdenDespacho = parseInt(ordenDespacho.value)+1;
        await TanqueService.createTanque(parseInt(capacidad.value), parseInt(capacidadMinima.value),parseInt(capacidadTotal.value), tipo.value, id_OrdenDespacho, id_ordenCompra);
        if (props.onTanqueCreated) {
          props.onTanqueCreated();
        }
        capacidad.value = '';
        capacidadMinima.value = '';
        capacidadTotal.value = '';
        tipo.value = '';
        dialogOpened.value = false;
        Notification.show('Tanque creado', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }

    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };
  
  let listaOrdenCompra = useSignal<String[]>([]);
  useEffect(() => {
    TanqueService.listOrdenCopraCombo().then(data =>
      listaOrdenCompra.value = data
    );
  }, []);

  let listaOrdenDespacho = useSignal<String[]>([]);
  useEffect(() => {
    TanqueService.listOrdenDespachoCombo().then(data =>
      listaOrdenDespacho.value = data
    );
  }, []);

  let listaTipo = useSignal<String[]>([]);
  useEffect(() => {
    TanqueService.listTipo().then(data =>
      listaTipo.value = data
    );
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
              Candelar
            </Button>
            <Button onClick={createTanque} theme="primary">
              Registrar
            </Button>
            
          </>
        }
      >
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <NumberField  label="Capacidad" 
            placeholder="Ingrese el nombre de la Tanque"
            aria-label="Nombre del tanque"
            value={capacidad.value}
            onValueChanged={(evt) => (capacidad.value = evt.detail.value)}
            />
          <ComboBox label="Orden" 
            items={listaOrdenCompra.value}
            placeholder='Seleccione una orden'
            aria-label='Seleccione un orden'
            value={ordenCompra.value}
            onValueChanged={(evt) => (ordenCompra.value = evt.detail.value)}
            />
            <ComboBox label="Orden" 
            items={listaOrdenDespacho.value}
            placeholder='Seleccione una orden'
            aria-label='Seleccione una orden'
            value={ordenDespacho.value}
            onValueChanged={(evt) => (ordenDespacho.value = evt.detail.value)}
            />
            <ComboBox label="Tipo" 
            items={listaTipo.value}
            placeholder='Seleccione un tipo'
            aria-label='Seleccione un tipo de la lista'
            value={tipo.value}
            onValueChanged={(evt) => (tipo.value = evt.detail.value)}
            /> 
            <NumberField label="" 
            placeholder='Ingrese la capacidad minima'
            aria-label='Ingrese la capacidad minima '
            value={capacidadMinima.value}
            onValueChanged={(evt) => (capacidadMinima.value = evt.detail.value)}
            />
            <NumberField  label="capacidad total" 
            placeholder='Inserte capacidad total'
            aria-label='INserte capacidad total'
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

//LISTA DE TanqueS
export default function TanqueView() {
  
  const dataProvider = useDataProvider<Tanque>({ 
    list: () => TanqueService.listTanque(),
  });

  function indexIndex({model}:{model:GridItemModel<Tanque>}) {
    return (
      <span>
        {model.index + 1} 
      </span>
    );
  }

  return (

    <main className="w-full h-full flex flex-col box-border gap-s p-m">

      <ViewToolbar title="Lista de Tanquees">
        <Group>
          <TanqueEntryForm onTanqueCreated={dataProvider.refresh}/>
        </Group>
      </ViewToolbar>
      <Grid dataProvider={dataProvider.dataProvider}>
        <GridColumn  renderer={indexIndex} header="Nro" />
        <GridColumn path="capacidad" header="Capacidad" />
        <GridColumn path="capacidadMinima" header="Capacidad Minima"/>
        <GridColumn path="capacidadTotal" header="Capacidad Maxima"/>
        <GridColumn path="ordenCompra" header="Orden Compra"/>
        <GridColumn path="ordenDespacho" header="Orden Despacho"/>
        <GridColumn path="tipo" header="Tipo">

        </GridColumn>
      </Grid>
    </main>
  );
}