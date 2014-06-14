package comportamiento;

import entidad.DiccionarioEntidades;
import entidad.Entidad;
import entidad.EntidadInteligente;
import figuras.Personaje;
import static util.Maths.*;

public class ComportamientoGuardian implements Comportamiento {

    private static DiccionarioEntidades diccionarioEntidades = DiccionarioEntidades.getInstance();

    private EntidadInteligente entidadControlada = null;
    private final float[] puntoDefendido;
    private float minimaDistanciaPerseguir = 30f;
    private float maximaDistanciaPerseguir = 50f;


    /* El supuesto enemigo */
    private Personaje objetivo = null;

    public ComportamientoGuardian(EntidadInteligente objetivo) {
        this.entidadControlada = objetivo;
        puntoDefendido = new float[]{
            objetivo.posiciones[0],
            objetivo.posiciones[1],
            objetivo.posiciones[2]};

    }

    public void actualizar() {
//        System.out.println("distancia al objetivo: " + (objetivo != null ? distancia(objetivo.posiciones, posicionInicial) : "NULL"));
        if (objetivo == null) {
            /* Si estamos a�n de vuelta */
            if (!entidadControlada.ir(puntoDefendido)) {
                System.out.println("Volviendo al punto defendido");
            } else {
                /* Estar alerta por si se acercan fuerzas hostiles */
                for (Personaje e : diccionarioEntidades.getPersonajesHostiles(entidadControlada)) {
                    if (distancia(e.posiciones, puntoDefendido) < maximaDistanciaPerseguir) {
                        objetivo = e; // Fusionaremos las clases y no habra que hacer casting
                        break;
                    }
                }
            }
        } else {
            if (distancia(objetivo.posiciones, puntoDefendido) > maximaDistanciaPerseguir) {
                /* Si el enemigo est� demasiado lejos del punto guardado lo olvidamos */
                objetivo = null;
                System.out.println("Objetivo fuera del per�metro");
            } else if (distancia(objetivo.posiciones, puntoDefendido) > minimaDistanciaPerseguir) {
                /* Si est� dentro del m�ximo le miramos */
                entidadControlada.mirarA(objetivo.posiciones);
                System.out.println("Objetivo dentro del per�metro exterior");
            } else {
                /* Si est� dentro del m�nimo le atacamos*/
                System.out.println("Atacando al objetivo");
                entidadControlada.atacar(objetivo);
            }
        }
    }
}
