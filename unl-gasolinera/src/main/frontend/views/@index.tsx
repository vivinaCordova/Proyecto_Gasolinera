// TODO Replace with your own main view.

import {ViewConfig} from "@vaadin/hilla-file-router/types.js";
import { useAuth,role } from "Frontend/security/auth";

export const config: ViewConfig = {
    menu: {
        exclude: true
    },
};

export default function MainView() {
    
        
    return (
        <main className="p-m">
            Dashboard
        </main>
    );
}