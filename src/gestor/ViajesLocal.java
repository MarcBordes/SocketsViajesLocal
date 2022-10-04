package gestor;

import org.json.simple.JSONObject;
import com.sun.security.jgss.GSSUtil;
import org.json.simple.JSONArray;

import java.util.Scanner;

public class ViajesLocal {

    /**
     * Muestra el menu de opciones y lee repetidamente de teclado hasta obtener una opcion valida
     * @param teclado	stream para leer la opción elegida de teclado
     * @return			opción elegida
     */
    public static int menu(Scanner teclado) {
        int opcion;
        System.out.println("\n\n");
        System.out.println("=====================================================");
        System.out.println("============            MENU        =================");
        System.out.println("=====================================================");
        System.out.println("0. Salir");
        System.out.println("1. Consultar viajes con un origen dado");
        System.out.println("2. Reservar un viaje");
        System.out.println("3. Anular una reserva");
        System.out.println("4. Ofertar un viaje");
        System.out.println("5. Borrar un viaje");
        do {
            System.out.print("\nElige una opcion (0..5): ");
            opcion = teclado.nextInt();
        } while ( (opcion<0) || (opcion>5) );
        teclado.nextLine(); // Elimina retorno de carro del buffer de entrada
        return opcion;
    }


    /**
     * Programa principal. Muestra el menú repetidamente y atiende las peticiones del cliente.
     *
     * @param args	no se usan argumentos de entrada al programa principal
     */
    public static void main(String[] args)  {

        Scanner teclado = new Scanner(System.in);

        // Crea un gestor de viajes
        GestorViajes gestor = new GestorViajes();

        System.out.print("Introduce tu codigo de cliente: ");
        String codcli = teclado.nextLine();

        int opcion;
        do {
            opcion = menu(teclado);
            switch (opcion) {
                case 0: // Guardar los datos en el fichero y salir del programa

                    // TODO
                    gestor.guardaDatos();
                    opcion = 0;

                    break;

                case 1: { // Consultar viajes con un origen dado

                    // TODO

                    System.out.print("Introduce el origen: ");
                    String origen = teclado.next();

                    if(gestor.consultaViajes(origen).isEmpty()) {
                        System.out.println("No hay viajes disponibles con ese origen");		//comprobar que no existe el origen
                    } else {

                        System.out.println("Viajes disponibles: ");
                        System.out.println(gestor.consultaViajes(origen));

                    }

                    break;
                }

                case 2: { // Reservar un viaje--> QUE NO EXISTA, QUE ESTÉ LLENO, FINALIZADO, QUE YA LO HAYAS RESERVADO TU MISMO

                    // TODO
                    System.out.print("Ahora se mostrarán los viajes que disponemos, introduzca el destino deseado: ");
                    String destino = teclado.next();

                    if(!gestor.consultaDestinos(destino).isEmpty()) {

                        for (int i = 0; i < gestor.consultaDestinos(destino).size(); i++) {    //comprobar si existe el destino

                            System.out.println(i + " --> " + gestor.consultaDestinos(destino).get(i));
                        }
                        System.out.print("Introduce el codigo del viaje quiere reservar: ");
                        String codViaje = teclado.next();

                        if (!gestor.existeViaje(codViaje)) {

                            System.out.println("No existe ningún viaje con el código introducido.");
                        } else {

                            JSONObject devolver = gestor.reservaViaje(codViaje, codcli);

                            if (devolver.isEmpty())
                                System.out.println("No es posible realizar la reserva");
                            else
                                System.out.println("Datos del viaje resevado: " + devolver.toJSONString());
                        }

                    }else
                        System.out.println("El destino no esta disponible");
                }
                break;


                case 3: { // Anular una reserva --> QUE NO EXISTISCA, PASSADA,

                    // TODO
                    System.out.print("Introduzca su código de viaje: ");
                    String codViaje = teclado.next();
                    //Comprobar código erróneo
                    if (!gestor.existeViaje(codViaje)) {
                        System.out.println("No existe ningún viaje con el código introducido.");

                    } else {
                        JSONObject viaje = gestor.anulaReserva(codViaje, codcli);
                        if(viaje.isEmpty()) {
                            System.out.println("El viaje ha finalizado.");
                        } else {
                            System.out.println("Datos del viaje actualizados: " + viaje.toJSONString());
                        }
                    }
                    break;
                }

                case 4: { // Ofertar un viaje, --> QUE LA FECHA CORRECTA, PRECIO >0, PLAZAS > 0,
                    // TODO
                    System.out.print("Introduzca el origen del viaje: ");
                    String origen = teclado.next();
                    System.out.print("Introduzca un destino: ");
                    String destino = teclado.next();

                    long precio = 0;
                    long numplazas = 0;
                    String fecha = null;
                    boolean f = false;
                    boolean p = false;
                    boolean pl = false;
                    while(!f){
                        System.out.print("Introduzca una fecha con formato dd-mm-yyyy: ");
                        fecha = teclado.next();
                        if(!gestor.getFechaOk(fecha)) {
                            System.out.print("Introduzca una fecha con formato dd-mm-yyyy: ");
                            fecha = teclado.next();
                        } else f = true;
                    } while (!p) {
                        System.out.print("Introduzca un precio: ");
                        precio = teclado.nextLong();
                        if (precio <= 0)
                            System.out.println("Introduce un precio mayor que 0");
                        else
                            p = true;
                    }
                    while (!pl) {
                        System.out.print("Introduce las plazas disponibles: ");
                        numplazas = teclado.nextLong();
                        if(numplazas <= 0)
                            System.out.println("Introduce un numero de plazas mayor que 0");
                        else
                            pl = true;

                    }
                    JSONObject v = gestor.ofertaViaje(codcli, origen, destino, fecha, precio, numplazas);
                    System.out.println(v.toJSONString());
                    break;
                }

                case 5: { // Borrar un viaje ofertado --> QUE SEA TUYO, CODIGO CORRECTO, QUE ESTE FECHA VALIDA

                    // TODO
                    System.out.print("Introduzca el código del viaje que desea borrar: ");
                    String codviaje = teclado.next();
                    if(!gestor.existeViaje(codviaje))
                        System.out.println("El viaje no existe");
                    else {
                        if (!gestor.getOrganizador(codcli, codviaje))
                            System.out.println("No eres el propietario de este viaje, no puedes borralo");
                        else if (gestor.borraViaje(codviaje, codcli).isEmpty())
                            System.out.println("El viaje ha finalizado");
                    }
                    break;
                }

            } // fin switch

        } while (opcion != 0);

    } // fin de main

} // fin class

