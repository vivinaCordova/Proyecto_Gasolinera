import {configureAuth} from '@vaadin/hilla-react-auth';
import { CuentaService, UserInfoService } from 'Frontend/generated/endpoints';
import UserInfo from 'Frontend/generated/org/unl/gasolinera/base/controller/service/UserInfoService/UserInfo';

/*const auth = configureAuth(CuentaService.getAuthentication, {
    getRoles:(user) => user.authorities?.map((v) => v ?? '')
});*/

const auth = configureAuth(CuentaService.getAuthentication, {
    getRoles: (user) => { 
        return user.authorities?.map(s => s ?? ''); 
    }
});


export const useAuth = auth.useAuth;
export const isLogin = CuentaService.isLogin;
export const role = CuentaService.view_rol;
export const AuthProvider = auth.AuthProvider;