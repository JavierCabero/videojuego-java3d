package eventos;

import entidad.DiccionarioEntidades;
import entidad.EntidadJava3D;
import entidad.TipoEntidad;

import java.util.PriorityQueue;

public class ManejadorEventos {

    private DiccionarioEntidades diccionarioEntidades = DiccionarioEntidades.getInstance();

    /**
     * ************ On demand holder initialization *************
     */
    private ManejadorEventos() {
    }

    private static class EventHandlerHolder {

        private static ManejadorEventos INSTANCE = new ManejadorEventos();
    }

    public static ManejadorEventos getInstance() {
        return EventHandlerHolder.INSTANCE;
    }
    /**
     * **********************************************************
     */

    // Events
    private PriorityQueue<Evento> queue = new PriorityQueue<Evento>();

    public void a�adirEvento(Evento evento) {
        queue.add(evento);
    }

    public void poll() {
        while (queue.peek() != null && queue.peek().getTiempo() <= System.currentTimeMillis()) {
            manejarEvento(queue.remove());
        }
    }

    private void manejarEvento(Evento evento) {

        TipoEntidad objetivo = evento.getTipoObjetivo();
        if (!evento.getTargets().isEmpty()) {
            evento.setTargets(diccionarioEntidades.buscarEntidades(objetivo));
        }

        EntidadJava3D entidad = null;
        for (Integer id : evento.getTargets()) {
            entidad = diccionarioEntidades.getEntidad(id);
            if (entidad != null) {
                entidad.recibirEvento(evento);
            }
        }
    }

}
