package entidad;

import comportamiento.Comportamiento;
import comportamiento.ComportamientoGuardian;
import eventos.Evento;
import figuras.EsferaMDL;
import javax.media.j3d.BranchGroup;
import javax.vecmath.Vector3f;
import main.Juego;
import util.Maths;

/* Funcionalidad: vida, sistema de ataque y comportamiento */
public class EntidadInteligente extends EsferaMDL {

    private Comportamiento comportamiento = null;

    /* Sistema vida */
    private int vida = 100;

    /* Sistema de ataque */
    private float distanciaAtaque = 1.5f;
    private float da�oAtaque = 40;
    private float intervaloAtaque = 15;
    private float siguienteAtaque = 0;

    public EntidadInteligente(String ficheroMDL, float radio, BranchGroup conjunto, Juego juego, boolean esPersonaje) {
        super(ficheroMDL, radio, conjunto, juego, esPersonaje);
        /* Comportamiento por defecto */
        comportamiento = new ComportamientoGuardian(this);
    }

    public void actualizar() {
        super.actualizar();

        /* Comprobar si hemos muerto */
        if (vida <= 0) {
            //ToDo: Implementar
            // Eliminar del juego o dejar quieto
        }
        
        comportamiento.actualizar();

        /* Sistema de ataque */
        if (siguienteAtaque > 0) {
            siguienteAtaque--;
        }

    }

    public void setComportamiento(Comportamiento comportamiento) {
        this.comportamiento = comportamiento;
    }

    public Comportamiento getComportamiento() {
        return comportamiento;
    }

    /* Cuando este m�todo est� hecho se discutir� si debe ir aqu� o en EntidadFisica */
    public void ir(float[] p) {

        /* Provisional (es demasiado fuerte) */
        Vector3f dir = new Vector3f(p[0] - posiciones[0], p[1] - posiciones[1], p[2] - posiciones[2]);
        dir.normalize();
        dir.scale(velocidad_movimiento);
        cuerpoRigido.applyCentralForce(dir);
    }

    public void atacar(EntidadInteligente objetivo) {
        /* Si el objetivo est� lejos nos acercamos */
        if (Maths.distancia(posiciones, objetivo.posiciones) > distanciaAtaque) {
            ir(objetivo.posiciones);
        } else {
            /* Si hemos atacado recientemente nos esperamos */
            if (siguienteAtaque <= 0) {
                Evento e = new Evento();
                e.setCommando("da�ar");
                e.setValor(da�oAtaque);
                objetivo.procesarEvento(e);
            }
        }
    }

    public void procesarEvento(Evento e) {
        switch (e.getCommando()) {
            case "da�ar":
                vida -= e.getValor();
                break;
        }
    }
}
