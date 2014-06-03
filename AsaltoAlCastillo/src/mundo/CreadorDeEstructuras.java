package mundo;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import figuras.Bloque;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3f;
import main.Juego;

public class CreadorDeEstructuras {

    /* Se han de crear entidades fisicas y meterlas al branchgroup */
    /* Los tama�os se especifican en mitades */
    private static float masa = 1f;
    private static float elasticidad = 0.5f;
    private static float dampingLineal = 0.5f;
    private static float dampingAngular = 0.9f;

    public static void crearBloque(
            Vector3f centro,
            Vector3f tama�os,
            Vector3f rotacion,
            BranchGroup conjunto,
            Juego juego) {

        Bloque b = new Bloque(tama�os, "res//texturas//muro.jpg", conjunto, juego);
        b.crearPropiedades(masa, elasticidad, dampingLineal, centro, rotacion);
    }

    /* Los muros tienen un ancho prefijado */
    public static void crearMuro(
            Vector3f comienzo,
            Vector3f fin,
            float altura,
            int numeroBloques,
            BranchGroup conjunto,
            Juego juego) {
        /* ToDo: Implementar */
    }

    /* Creaci�n de una torre cil�ndrica */
    public static void crearTorre(
            Vector3f pos,
            float radio,
            int altura,
            int numeroBloques,
            BranchGroup conjunto) 
    {
        for(int i = 0; i < altura; i++){
            float angDesplazamiento = (float) (Math.PI/numeroBloques); 
            for(int j = 0; j < numeroBloques; j++){
                 float angulo = (float) (j==0?0:(2*Math.PI/j + (j&1)==0?0:angDesplazamiento));
                 Vector3f posCentro = new Vector3f((float) (pos.x+radio*(Math.sin(angulo))), pos.y + i, (float) (pos.x+radio*(Math.cos(angulo))));
                 crearBloque(posCentro, new Vector3f(2,1,1), new Vector3f(0,angulo,0), conjunto);
            }
        }
    }

}
