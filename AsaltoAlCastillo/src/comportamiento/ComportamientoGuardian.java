package comportamiento;

import entidad.DiccionarioEntidades;
import entidad.EntidadFisica;
import entidad.EntidadInteligente;
import entidad.EntidadJava3D;
import static util.Maths.distancia;

public class ComportamientoGuardian implements Comportamiento {

    private static DiccionarioEntidades diccionarioEntidades = DiccionarioEntidades.getInstance();

    private EntidadInteligente entidadControlada = null;
    private final float[] posicionInicial;
    private float minimaDistanciaPerseguir = 5f;
    private float maximaDistanciaPerseguir = 15f;

    /* No tienes porque estar exactamente en la posici�n inicial */
    private float epsilon = 1f;

    /* El supuesto enemigo */
    private EntidadFisica objetivo = null;

    public ComportamientoGuardian(EntidadInteligente objetivo) {
        this.entidadControlada = objetivo;
        posicionInicial = objetivo.posiciones;
    }

    public void actualizar() {
        if (objetivo == null) {
            /* Si estamos a�n de vuelta */
            if (distancia(entidadControlada.posiciones, posicionInicial) > epsilon) {
                entidadControlada.ir(posicionInicial);
            } else {
                /* Estar alerta por si se acercan fuerzas hostiles */
                for (EntidadFisica e : diccionarioEntidades.getEntidadesHostiles(entidadControlada)) {
                    if (distancia(e.posiciones, posicionInicial) < minimaDistanciaPerseguir) {
                        objetivo = e;
                        break;
                    }
                }
            }
        } else {
            if (distancia(objetivo.posiciones, posicionInicial) > maximaDistanciaPerseguir) {
                /* Si el enemigo est� demasiado lejos del punto guardado: volver */
                objetivo = null;
            } else {
                /* Si est� al alcance: atacar */
                entidadControlada.atacar(objetivo);
            }
        }

    }
}
