package entidad;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import java.util.List;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import main.Juego;

public class EntidadFisica extends EntidadJava3D {

    /* F�sica */
    protected DiccionarioEntidades diccionarioEntidades = DiccionarioEntidades.getInstance();
    protected DiscreteDynamicsWorld mundoFisico;
    public RigidBody cuerpoRigido;
    protected CollisionObject ramaFisica;
    public float masa, elasticidad;
    protected float[] velocidades = new float[3];
    public float[] posiInicial;

    /* La velocidad lineal es relativa a la posicion del jugador, siendo hacia delante el eje x positivo */
    public Vector3f velocidad_lineal = new Vector3f(0f, 0f, 0f);
    public Vector3f velocidad_angular = new Vector3f(0f, 0f, 0f);

    public boolean esMDL;

    /* Constructor */
    public EntidadFisica(Juego juego, BranchGroup branchGroup) {
        super(juego, branchGroup);
        this.mundoFisico = juego.getMundoFisico();
        posiInicial=new float[3];
    }

    public void crearPropiedades(float masa, float elasticidad, float dampingLineal, Vector3f centro, Vector3f rotacion) {
        //Creaci�n de un cuerpoRigido (o RigidBody) con sus propiedades fisicas 
        this.masa = masa;
        Transform groundTransform = new Transform();
        groundTransform.setIdentity();
        groundTransform.origin.set(centro);
        posiInicial[0]=centro.x;
        posiInicial[1]=centro.y;
        posiInicial[2]=centro.z;
        /* Rotacion */
        float qy = (float) Math.sin(rotacion.y / 2);
        float qw = (float) Math.cos(rotacion.y / 2);
        Quat4f rot = new Quat4f();
        rot.y = qy;
        rot.w = qw;
        groundTransform.setRotation(rot);
        
        boolean isDynamic = (masa != 0f);
        Vector3f inerciaLocal = new Vector3f(0, 1, 0);
        if (isDynamic && !esMDL) { // 
            this.ramaFisica.getCollisionShape().calculateLocalInertia(masa, inerciaLocal);
        }
        DefaultMotionState EstadoDeMovimiento = new DefaultMotionState(groundTransform);
        RigidBodyConstructionInfo InformacionCuerpoR = new RigidBodyConstructionInfo(masa, EstadoDeMovimiento, this.ramaFisica.getCollisionShape(), inerciaLocal);
        InformacionCuerpoR.restitution = elasticidad;

        cuerpoRigido = new RigidBody(InformacionCuerpoR);
        cuerpoRigido.setActivationState(RigidBody.DISABLE_DEACTIVATION);
        cuerpoRigido.setDamping(dampingLineal, 0.1f);   //a�ade m�s (1) o menos  (0) "friccion del aire" al desplazarse/caer o rotar
        cuerpoRigido.setFriction(0.3f);
        //A�adiendo el cuerpoRigido al mundoFisico
        mundoFisico.addRigidBody(cuerpoRigido); // add the body to the dynamics world
        identificadorFisico = mundoFisico.getNumCollisionObjects() - 1;

        //A�adiendo objetoVisual asociado al grafo de escea y a la lista de objetos fisicos visibles y situandolo
        branchGroup.addChild(ramaVisible);
        diccionarioEntidades.a�adirEntidadFisica(this);

        //Presentaci�n inicial de la  figura visual asociada al cuerpo rigido
        Transform3D inip = new Transform3D();
        inip.set(centro);
        /* ToDo: Necesita testearse */

        inip.set(new Quat4f(rotacion.x, rotacion.y, rotacion.z, 1));
        desplazamiento.setTransform(inip);

        //Actualizacion de posicion. La rotacion se empezar� a actualizar en el primer movimiento (ver final del metodo mostrar(rigidBody))
        this.posiciones[0] = centro.x;
        this.posiciones[1] = centro.y;
        this.posiciones[2] = centro.z;
    }

    public void remover() {
        try {
            mundoFisico.getCollisionObjectArray().remove(identificadorFisico);
            mundoFisico.removeRigidBody(cuerpoRigido);
            branchGroup.removeChild(identificadorFigura);
            diccionarioEntidades.eliminarEntidadFisica(this);
        } catch (Exception e) {
            System.out.println("Ya eliminado");
        }
    }

    public void mostrar() {

        CollisionObject objeto = mundoFisico.getCollisionObjectArray().get(identificadorFisico); //
        RigidBody cuerpoRigido = RigidBody.upcast(objeto);

        if (cuerpoRigido != null && cuerpoRigido.getMotionState() != null) {
            Transform trans = new Transform();
            cuerpoRigido.getMotionState().getWorldTransform(trans);
            Quat4f orientacion = new Quat4f();
            cuerpoRigido.getOrientation(orientacion);
            Transform3D rot = new Transform3D(orientacion, new Vector3f((float) trans.origin.x, (float) trans.origin.y, (float) trans.origin.z), 1);
            desplazamiento.setTransform(rot);

            //Actualizacion de Matriz de rotaci�n y posiciones
//            rot.get(this.matrizRotacion);

            /* Posicion */
            this.posiciones[0] = trans.origin.x;
            this.posiciones[1] = trans.origin.y;
            this.posiciones[2] = trans.origin.z;
        }
    }

    public void actualizar() {
        /* Sistema Marl�nico */
        // Movimiento por fuerzas del jugador
//        float fuerzaHaciaAdelante = 0, fuerzaLateral = 0, fuerzaHaciaArriba = 0f;
//        if (adelante) {
//            fuerzaHaciaAdelante = masa * 100f * 2.5f;
//        }
//        if (atras) {
//            fuerzaHaciaAdelante = -masa * 100f * 2.5f;
//        }
//        if (derecha) {
//            fuerzaLateral = -masa * 40f;
//        }
//        if (izquierda) {
//            fuerzaLateral = masa * 40f;
//        }
//        if (arriba) {
//            fuerzaHaciaArriba = masa * 40f;
//        }

        Vector3d direccionFrente = direccionFrontal();

        /* Fuerza hacia delante */
        cuerpoRigido.applyCentralForce(new Vector3f((float) direccionFrente.x * velocidad_lineal.x * 0.1f,
                (float) direccionFrente.y,
                (float) direccionFrente.z * velocidad_lineal.x * 0.1f));

        /* Fuerza hacia arriba */
        cuerpoRigido.applyCentralForce(new Vector3f(0, velocidad_lineal.y, 0));

        /* Rotaci�n */
        cuerpoRigido.applyTorque(new Vector3f(0, velocidad_angular.y, 0));

        /* Damping */
        velocidad_lineal.scale(.7f);
        velocidad_angular.scale(.1f);
        if (velocidad_lineal.length() < .0001) {
            velocidad_lineal = new Vector3f(0, 0, 0);
        }
        if (velocidad_angular.length() < .0001) {
            velocidad_angular = new Vector3f(0, 0, 0);
        }

    }

}
