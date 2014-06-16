package entidad;

import main.Juego;
import util.CapabilitiesMDL;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.linearmath.Transform;
import com.sun.j3d.loaders.Scene;
import entidad.Entidad;
import entidad.EntidadInteligente;
import eventos.Evento;
import java.net.URL;
import static java.rmi.server.LogStream.log;
import java.util.ArrayList;
import javax.media.j3d.*;
import javax.vecmath.*;
import net.sf.nwn.loader.AnimationBehavior;
import net.sf.nwn.loader.NWNLoader;
import util.Maths;
import static util.Maths.distancia;
import util.Sonido;

public class Personaje extends Entidad {

    /* Animaci�n */
    public Scene escenaPersonaje1;
    public AnimationBehavior ab = null;
    public String nombreAnimacionCorriendo, nombreAnimacionCaminando, nombreAnimacionQuieto, nombreAnimacionLuchando;
    public float radio, alturaP, alturaDeOjos;
    boolean esPersonaje;

    /* Control de animaciones */
    private float velocidad_giro = 50f;
    protected float velocidad_movimiento = 800;

    private String animacionActual = "";
    private boolean accionRealizada = false;

    /* Sistema vida */
    public int vida = 100;
    
    /* Booleano de tener el tesoro */
    public boolean tieneTesoro = false;

    /* Sistema de ataque */
    protected float distanciaAtaque = 7f;
    protected float da�oAtaque = 40;
    protected float intervaloAtaque = 15;
    protected float siguienteAtaque = 0;

    /* No tienes porque estar exactamente en la posici�n a la que vas */
    private float epsilonIr = 5f;

    public Personaje(String ficheroMDL,
            float radio,
            BranchGroup conjunto,
            Juego juego,
            boolean esPersonaje) {

        super(juego, conjunto);

        /* Sistema de log */
        logEnabled = false;

        this.esPersonaje = esPersonaje;
        //Creando una apariencia
        Appearance apariencia = new Appearance();
        this.radio = radio;
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);
        esMDL = true;
        //Creacion de la forma visual MDL
        //nombre = "figura_MDL_" + identificador;
        //Sphere figuraVisual = new Sphere(radio);
        TransformGroup figuraVisual = crearObjetoMDL(ficheroMDL, radio * 2);
        SphereShape figuraFisica = new SphereShape(radio);
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(figuraFisica);
        ramaVisible.addChild(desplazamiento);
        desplazamiento.addChild(figuraVisual);
    }

    public TransformGroup crearObjetoMDL(String archivo, float multiplicadorEscala) {
        BranchGroup RamaMDL = new BranchGroup();
        float rotacionX = 0;
        float rotacionY = 0;
        float rotacionZ = 0;
        float escalaTamano = 1f;
        float desplazamientoY = 0;
        try {
            NWNLoader nwn2 = new NWNLoader();
            nwn2.enableModelCache(true);
            escenaPersonaje1 = nwn2.load(new URL("file://localhost/" + System.getProperty("user.dir") + "/" + archivo));
            RamaMDL = escenaPersonaje1.getSceneGroup();
            //Recorrido por los- objetos para darle capacidades a sus Shapes3D
            CapabilitiesMDL.setCapabilities(RamaMDL, this.identificadorFigura);
            //Para cada Objeto MDL dar nombre las animaciones de la figura. Dar rotaciones a la figuraMDL (suelen venir giradas)
            ab = (AnimationBehavior) escenaPersonaje1.getNamedObjects().get("AnimationBehavior");
            if (archivo.equals("objetosMDL/Iron_Golem.mdl")) {
                nombreAnimacionCorriendo = "iron_golem:cwalk";
                nombreAnimacionCaminando = "iron_golem:cwalk";
                nombreAnimacionQuieto = "iron_golem:cpause1";
                nombreAnimacionLuchando = "iron_golem:ca1slashl";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                escalaTamano = 0.65f;
                desplazamientoY = -1f;
                alturaP = (float) 3f * escalaTamano;
                alturaDeOjos = alturaP;
            }
            if (archivo.equals("objetosMDL/Doomknight.mdl")) {
                nombreAnimacionCaminando = "Doomknight:crun";
                nombreAnimacionCaminando = "Doomknight:cwalk";
                nombreAnimacionQuieto = "Doomknight:cpause1";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 2f;
                escalaTamano = 0.8f;
                alturaDeOjos = 1.5f * escalaTamano;
                desplazamientoY = -1f;
            }
            if (archivo.equals("objetosMDL/Dire_Cat.mdl")) {
                nombreAnimacionCaminando = "dire_cat:crun";
                nombreAnimacionCaminando = "dire_cat:cwalk";
                nombreAnimacionQuieto = "dire_cat:cpause1";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 2f;
                escalaTamano = 1f;
                alturaDeOjos = alturaP * escalaTamano;
            }
            if (archivo.equals("objetosMDL/pixie.mdl")) {
                nombreAnimacionCaminando = "pixie:crun";
                nombreAnimacionCaminando = "pixie:cwalk";
                nombreAnimacionQuieto = "pixie:cpause1";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 0.4f;
                escalaTamano = 12.0f;
                desplazamientoY = -11.5f;
                alturaDeOjos = alturaP * escalaTamano;
            }
            if (archivo.equals("objetosMDL/Intellect_Devour.mdl")) {
                nombreAnimacionCaminando = "intellect_devour:crun";
                nombreAnimacionCaminando = "intellect_devour:cwalk";
                nombreAnimacionQuieto = "intellect_devour:cpause1";
                nombreAnimacionLuchando = "intellect_devour:ca1slashl";

                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 1f;
                escalaTamano = 6.0f;
                desplazamientoY = 0f;
                alturaDeOjos = alturaP * escalaTamano;
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            System.out.println("Error during load Dire_Cat.mdl");
        }

        //Ajustando rotacion inicial de la figura MLD y aplicando tamano
        Transform3D rotacionCombinada = new Transform3D();
        rotacionCombinada.set(new Vector3f(0, desplazamientoY, 0));
        Transform3D correcionTemp = new Transform3D();
        correcionTemp.rotX(rotacionX);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.rotZ(rotacionZ);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.rotY(rotacionY);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.setScale(escalaTamano * multiplicadorEscala);
        rotacionCombinada.mul(correcionTemp);
        TransformGroup rotadorDeFIguraMDL = new TransformGroup(rotacionCombinada);
        rotadorDeFIguraMDL.addChild(RamaMDL);
        return rotadorDeFIguraMDL;
    }

    public void realizarAccion(Evento e) {

//        /* No podemos hacer acciones si no tenemos puntos de vida */
//        if (vida <= 0) {
//            return;
//        }
        accionRealizada = true;

        /* Velocidad por defecto de la animaci�n */
        ab.setAnimationTimeScale(.5f);

        try {
            String command = e.getComando();
            ArrayList<String> params = e.getParams();
            switch (command) {
                case "mover": {
                    String option = params.get(0);
                    switch (option) {
                        case "adelante":
                            log("Ir adelante");
                            if (!animacionActual.equals(nombreAnimacionCaminando)) {
                                ab.playAnimation(nombreAnimacionCaminando, true);
                                ab.setAnimationTimeScale(.5f);
                                animacionActual = nombreAnimacionCaminando;
                                Sonido.reproducirSonido("walk");
                            }
                            velocidad_lineal.x += velocidad_movimiento;
                            break;
                        case "atras":
                            if (!animacionActual.equals(nombreAnimacionCaminando)) {
                                ab.playAnimation(nombreAnimacionCaminando, true);
                                ab.setAnimationTimeScale(.5f);
                                animacionActual = nombreAnimacionCaminando;
                                Sonido.reproducirSonido("walk");

                            }
                            log("Ir atras");
                            velocidad_lineal.x -= velocidad_movimiento;
                            break;
                    }
                    break;
                }
                case "girar": {
                    String option = params.get(0);
                    switch (option) {
                        case "izquierda": {
                            log("Girar izquierda");
                            velocidad_angular.y += velocidad_giro;
//                            Transform trans = new Transform();
//                            Quat4f rotacion = new Quat4f();
//                            cuerpoRigido.getCenterOfMassTransform(trans);
//                            trans.getRotation(rotacion);
//                            rotacion.y += velocidad_giro;
////                            System.out.println(rotacion);
//                            trans.setRotation(rotacion);
//                            cuerpoRigido.setCenterOfMassTransform(trans);
                        }
                        break;
                        case "derecha": {
                            log("Girar derecha");
                            velocidad_angular.y -= velocidad_giro;
//                            Transform trans = new Transform();
//                            Quat4f rotacion = new Quat4f();
//                            cuerpoRigido.getCenterOfMassTransform(trans);
//                            trans.getRotation(rotacion);
//                            rotacion.y -= velocidad_giro;
////                            System.out.println(rotacion);
//                            trans.setRotation(rotacion);
//                            cuerpoRigido.setCenterOfMassTransform(trans);
                        }
                        break;
                    }
                    break;
                }
                case "atacar": {
                    log("Atacando");
                    if (!animacionActual.equals(nombreAnimacionLuchando)) {
                        ab.playAnimation(nombreAnimacionLuchando, true);
                        ab.setAnimationTimeScale(.5f);
                        animacionActual = nombreAnimacionLuchando;
                        Sonido.reproducirSonido("attack");
                    }
                    for (Entidad ent : diccionarioEntidades.getEntidades()) {
                        /* ToDo: Reparar esto que no funciona. El m�todo de colisi�n siempre da true */
                        if (!getId().equals(ent.getId())) {
                            ent.velocidad_lineal.y += 50000;
//                             System.out.println("este: " + cuerpoRigido.toString() + "\nOtro: " + ef.cuerpoRigido.toString() + "\n");
                        }
                    }
                    break;
                }
                /* Porque puedo */
                case "volar": {
                    log("Volando");
                    velocidad_lineal.y += 200;
                    break;
                }
            }
        } catch (Exception ex) {
            log("Error al procesar evento");
        }
    }

    @Override
    public void actualizar() {
        super.actualizar();

        /* Resetear rotacion */
        Transform trans = new Transform();
        Quat4f rotacion = new Quat4f();
        cuerpoRigido.getCenterOfMassTransform(trans);
        trans.getRotation(rotacion);
        rotacion.x = 0;
        rotacion.z = 0;
        trans.setRotation(rotacion);
        cuerpoRigido.setCenterOfMassTransform(trans);

        /* Si no estamos haciendo nada ponemos la animacion por defecto */
        if (!accionRealizada && !animacionActual.equals(nombreAnimacionQuieto)) {
            log("Animacion quieto");
            ab.playAnimation(nombreAnimacionQuieto, true);
            animacionActual = nombreAnimacionQuieto;
        }
        accionRealizada = false;

        /* Sistema de ataque */
        if (siguienteAtaque > 0) {
            siguienteAtaque--;
        }
        
        if(etiquetas.contains("JUGADOR")){
            System.out.println("Jugador (x,z) = (" + posiciones[0] + ", " + posiciones[2] + ")");
        }
    }

    /* S�lo tiene en cuenta la direcci�n en el plano XZ */
    public boolean ir(float[] p) {
        if (distancia(posiciones, p) < epsilonIr) {
            /* Nos paramos */
            System.out.println("Punto alcanzado");
            cuerpoRigido.setLinearVelocity(new Vector3f(0, 0, 0));
            return true;
        }

        /* Si no estamos mirando en la direcci�n giramos */
        if (!estaMirando(p)) {
            mirarA(p);
        } else {
            /* Cogemos la direcci�n y nos movemos a nuestra velocidad */
            Vector3f dir = new Vector3f(p[0] - posiciones[0], p[1] - posiciones[1], p[2] - posiciones[2]);
            if (dir.x > 0 || dir.y > 0 || dir.z > 0) {
                dir.normalize();
                dir.y = 0; // Para no aplicar fuerzas verticales
                dir.scale(velocidad_movimiento);
                cuerpoRigido.applyCentralForce(dir);
            }
        }
        /* Devolvemos false si a�n no estabamos ah� */
        return false;
    }

    public void atacar(Personaje objetivo) {
        /* Si el objetivo est� lejos nos acercamos */
        if (ir(objetivo.posiciones) && mirarA(objetivo.posiciones)) {
            System.out.println("[Atacar] Esperando a que se pueda atacar");
            /* Si hemos atacado recientemente nos esperamos */
            if (siguienteAtaque <= 0) {
                Evento e = new Evento();
                e.setComando("da�ar");
                e.setValor(da�oAtaque);
                objetivo.procesarEvento(e);
                System.out.println("Ataque realizado!");
            }
            cuerpoRigido.setLinearVelocity(new Vector3f());
        } else {
            System.out.println("[Atacar] Mirando al objetivo");
        }
    }

    public void procesarEvento(Evento e) {
        switch (e.getComando()) {
            case "da�ar":
                vida -= e.getValor();
                if (vida <= 0) {
                    remover();
                }
                System.out.println("La entidad con etiquetas: " + etiquetas.toString() + " ha perdido todos sus puntos de vida");
                break;
        }
    }
}
