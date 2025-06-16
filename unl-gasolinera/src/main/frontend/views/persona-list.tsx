import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import {
  Button,
  ComboBox,
  DatePicker,
  Dialog,
  Grid,
  GridColumn,
  GridItemModel,
  GridSortColumn,
  HorizontalLayout,
  Icon,
  PasswordField,
  Select,
  TextField,
  VerticalLayout,
} from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { CuentaService, PersonaService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import Cuenta from 'Frontend/generated/org/unl/gasolinera/base/models/Cuenta';
import { useDataProvider } from '@vaadin/hilla-react-crud';
import { useEffect, useState } from 'react';
import { listaPersonaCombo } from 'Frontend/generated/CuentaService';
import Persona from 'Frontend/generated/org/unl/gasolinera/base/models/Persona';

export const config: ViewConfig = {
  title: 'Persona',
  menu: {
    icon: 'vaadin:clipboard-check',
    order: 1,
    title: 'Persona',
  },
};

type PersonaEntryFormProps = {
  onPersonaCreated?: () => void;
};

type PersonaEntryFormPropsUpdate = () => {
  onPersonaUpdated?: () => void;
};

//GUARDAR Persona
function PersonaEntryForm(props: PersonaEntryFormProps) {
  const usuario = useSignal('');
  const cedula = useSignal('');
  const rol = useSignal('');
  const createPersona = async () => {
    try {
      if (usuario.value.trim().length > 0 && cedula.value.trim().length > 0) {
        const id_rol = parseInt(rol.value) + 1;
        await PersonaService.createPersona(usuario.value, cedula.value, id_rol);
        if (props.onPersonaCreated) {
          props.onPersonaCreated();
        }
        usuario.value = '';
        cedula.value = '';
        rol.value = '';
        dialogOpened.value = false;
        Notification.show('Persona creada', { duration: 5000, position: 'bottom-end', theme: 'success' });
      } else {
        Notification.show('No se pudo crear, faltan datos', { duration: 5000, position: 'top-center', theme: 'error' });
      }
    } catch (error) {
      handleError(error);
    }
  };

  let listaRol = useSignal<String[]>([]);
  useEffect(() => {
    PersonaService.listRolCombo().then((data) => (listaRol.value = data));

    
  }, []);

  const dialogOpened = useSignal(false);
  return (
    <>
      <Dialog
        modeless
        headerTitle="Nueva Persona"
        opened={dialogOpened.value}
        onOpenedChanged={({ detail }) => {
          dialogOpened.value = detail.value;
        }}
        footer={
          <>
            <Button
              onClick={() => {
                dialogOpened.value = false;
              }}>
              Cancelar
            </Button>
            <Button onClick={createPersona} theme="primary">
              Registrar
            </Button>
          </>
        }>
        <VerticalLayout style={{ alignItems: 'stretch', width: '18rem', maxWidth: '100%' }}>
          <TextField
            label="Usuario"
            placeholder="Ingrese el nuevo usuario"
            aria-label="Usuario"
            value={usuario.value}
            onValueChanged={(evt) => (usuario.value = evt.detail.value)}
          />
          <TextField
            label="Cedula de la Persona"
            placeholder="Ingrese la cedula de la Persona"
            aria-label="Cedula de la Persona"
            value={cedula.value}
            onValueChanged={(evt) => (cedula.value = evt.detail.value)}
          />
          <ComboBox
            label="Rol"
            items={listaRol.value}
            placeholder="Seleccione un rol"
            aria-label="Seleccione un rol de la lista"
            value={rol.value}
            onValueChanged={(evt) => (rol.value = evt.detail.value)}
          />
        </VerticalLayout>
      </Dialog>
      <Button
        onClick={() => {
          dialogOpened.value = true;
        }}>
        Agregar
      </Button>
    </>
  );
}

function indexIndex({model}:{model:GridItemModel<Persona>}) {
  return (
    <span>
      {model.index + 1} 
    </span>
  );
}

//LISTA DE PersonaES
export default function PersonaView() {
  const [items, setItems] = useState([]);
  const callData = () => {
    PersonaService.listAll().then(function (data) {
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
    var dir = direction == 'asc' ? 1 : 2;
    PersonaService.order(columnId, dir).then(function (data) {
      setItems(data);
    });
  };

  const criterio = useSignal('');
  const texto = useSignal('');
  const itemSelect = [
    {
      label: 'Usuario',
      value: 'usuario',
    },
    {
      label: 'Cedula',
      value: 'cedula',
    },
    {
      label: 'Rol',
      value: 'rol',
    },
  ];
  const search = async () => {
    try {
      console.log(criterio.value + ' ' + texto.value);
      PersonaService.search(criterio.value, texto.value, 0).then(function (data) {
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
      <ViewToolbar title="Lista de Personaes">
        <Group>
          <PersonaEntryForm onPersonaCreated={callData} />
        </Group>
      </ViewToolbar>
      <HorizontalLayout theme="spacing">
        <Select
          items={itemSelect}
          value={criterio.value}
          onValueChanged={(evt) => (criterio.value = evt.detail.value)}
          placeholder="Selecione un criterio"></Select>

        <TextField
          placeholder="Search"
          style={{ width: '50%' }}
          value={texto.value}
          onValueChanged={(evt) => (texto.value = evt.detail.value)}>
          <Icon slot="prefix" icon="vaadin:search" />
        </TextField>
        <Button onClick={search} theme="primary">
          BUSCAR
        </Button>
      </HorizontalLayout>
      <Grid items={items}>
        <GridColumn renderer={indexIndex} header="Nro" />
        <GridSortColumn path="usuario" header="Usuario" onDirectionChanged={(e) => order(e, 'usuario')} />
        <GridSortColumn path="cedula" header="Cedula" onDirectionChanged={(e) => order(e, 'cedula')} />
        <GridSortColumn path="rol" header="Rol" onDirectionChanged={(e) => order(e, 'rol')} />
      </Grid>
    </main>
  );
}
