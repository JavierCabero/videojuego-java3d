package mundo;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Vector3d;

public class CreadorDeEstructuras {

    /* Se han de crear entidades fisicas y meterlas al branchgroup */
    /* Los tama�os se especifican en mitades */
    public static void crearBloque(Vector3d centro, Vector3d tama�os, Vector3d rotacion, BranchGroup conjunto) {
        /* ToDo: Implementar */
    }
    
    /* Los muros tienen un ancho prefijado */
    public static void crearMuro(
            Vector3d comienzo, 
            Vector3d fin,
            float altura,
            int numeroBloques,
            Vector3d rotacion,
            BranchGroup conjunto) 
    {
        /* ToDo: Implementar */
    }
    
    /* Creaci�n de una torre cil�ndrica */
    public static void crearTorre(
            Vector3d pos,
            float altura,
            int numeroBloques,
            Vector3d rotacion,
            BranchGroup conjunto) 
    {
        /* ToDo: Implementar */
    }
    
}
