package comportamiento;

import entidad.DiccionarioEntidades;
import entidad.EntidadInteligente;
import entidad.Entidad;
import util.Maths;
import static util.Maths.*;

public class ComportamientoPerseguir implements Comportamiento {

    private static DiccionarioEntidades diccionarioEntidades = DiccionarioEntidades.getInstance();

    private EntidadInteligente entidadControlada = null;
    private final float[] puntoDefendido;
    private float distanciaMinima = 30f;
//    float[] posi; //posicion inicial de la entidad

    /* No tienes porque estar exactamente en la posici�n inicial */
    private float epsilon = 5f;

    private Entidad objetivo = null;

    public ComportamientoPerseguir(EntidadInteligente objetivo) {
        this.entidadControlada = objetivo;
        puntoDefendido = new float[]{
            objetivo.posiciones[0],
            objetivo.posiciones[1],
            objetivo.posiciones[2]};

//        posi = entidadControlada.posiInicial;

        /* Ponemos al jugador como enemigo fijo */
        this.objetivo = diccionarioEntidades.buscarEntidades("JUGADOR").get(0);
//        System.out.println("Objetivo:  " + objetivo);
    }

    public void actualizar() {
//        System.out.println("Distancia jugador al punto defendido:" + distancia(objetivo.posiciones, puntoDefendido));

        /* Si el enemigo est� mu cerca vamos hacia �l */
        if (distanciaHorizontal(objetivo.posiciones, puntoDefendido) < distanciaMinima) {
            entidadControlada.ir(objetivo.posiciones);
        } else {
            /* Si est� lejos, pasamos de �l y volvemos al punto defendido */
            if (distanciaHorizontal(entidadControlada.posiciones, puntoDefendido) > epsilon) {
//            entidadControlada.ir(posi);
                entidadControlada.ir(puntoDefendido);
            }
        }
        
//        System.out.println("El jugador esta mirando al perro: " + objetivo.estaMirando(entidadControlada.posiciones));
    }
}
