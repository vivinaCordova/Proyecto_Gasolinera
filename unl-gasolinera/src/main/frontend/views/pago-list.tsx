import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, DatePicker, Grid, GridColumn, GridItemModel, GridSortColumn, TextField } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { TaskService } from 'Frontend/generated/endpoints';
import { PagoService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import Task from 'Frontend/generated/org/unl/gasolinera/taskmanagement/domain/Task';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect } from 'react';
import { useState } from 'react';

export const config: ViewConfig = {
  title: 'Pagos',
  menu: {
    icon: 'vaadin:clipboard-check',
    order: 1,
    title: 'Pagos',
  },
};

type PagoEntryFormProps = {
  onPagoCreated?: () => void;
};

function PagoEntryForm(props: PagoEntryFormProps) {
  const nroTransaccion = useSignal('');
  const orden_despacho = useSignal('');
  const estadoP = useSignal('');
  const createPago = async () => {
    try {
      if (nroTransaccion.value.trim().length > 0 && orden_despacho.value.trim().length > 0) {
        await PagoService.create(
          parseInt(nroTransaccion.value),
          estadoP.value === 'true', // boolean as second argument
          parseInt(orden_despacho.value)
        );
        if (props.onPagoCreated) {
          props.onPagoCreated();
        }
        
        nroTransaccion.value = '';
        orden_despacho.value = '';
        estadoP.value = '';

        // dialogOpened.value = false;
        Notification.show('Pago creado', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }

    } catch (error) {
      console.log(error);
      handleError(error);
    }
  };

  
  
}

export default function PagoView() {
  const [items, setItems] = useState([]);
  useEffect(() => {
    PagoService.listAll().then(function (data) {
      console.log(data);
      setItems(data);
    });
  }, []);

  const order = (event, columnId) => {
    const direction = event.detail.value;
    if (!direction) {
      // Sin orden, mostrar lista original
      PagoService.listAll().then(setItems);
    } else {
      var dir = (direction == 'asc') ? 1 : 2;
      PagoService.order(columnId, dir).then(setItems);
    }
  }

  const callData = () => {
    PagoService.listAll().then(function (data) {
      //console.log(data);
      setItems(data);
    });
  }

  /*function indexLink({ model }: { model: GridItemModel<Pago> }) {
    return (
      <span>
        <PagoEntryFormUpdate Pago={model.item} onPagoUpdated={callData} />
      </span>
    );
  }*/


  function indexIndex({ model }: { model: GridItemModel<Pago> }) {
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
      label: 'Nro Transaccion',
      value: 'nroTransaccion',
    },
    {
      label: 'Orden Despacho',
      value: 'orden_despacho',
    },
    {
      label: 'Estado',
      value: 'estadoP',
    }
  ];

  const search = async () => {
    try {
      PagoService.search(criterio.value, texto.value, 0).then(function (data) {
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

      <ViewToolbar title="Lista de Pagos">
        <Group>
        
        </Group>
      </ViewToolbar>
      
      <Grid items={items}>
        <GridColumn renderer={indexIndex} header="Nro" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "nroTransaccion")} path="nroTransaccion" header="nroTransaccion" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "orden_despacho")} path="orden_despacho" header="Orden Despacho" />
        <GridSortColumn onDirectionChanged={(e) => order(e, "estadoP")} path="estadoP" header="Estado" />
        
        

      </Grid>
    </main>
  );
}

