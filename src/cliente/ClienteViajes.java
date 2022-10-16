package cliente;

import java.io.IOException;
import java.util.Scanner;

import gestor.GestorViajes;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;


public class ClienteViajes {


	// Sustituye esta clase por tu versión de la clase SubastasLocal de la práctica 1

	// Modifícala para que instancie un objeto de la clase AuxiliarClienteViajes

	// Modifica todas las llamadas al objeto de la clase GestorViajes
	// por llamadas al objeto de la clase AuxiliarClienteViajes.
	// Los métodos a llamar tendrán la misma signatura.

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
    public static void main(String[] args) throws IOException, ParseException {

        Scanner teclado = new Scanner(System.in);

        // Crea un gestorAux de viajes
        //GestorViajes gestor = new GestorViajes();
        AuxiliarClienteViajes gestorAux = new AuxiliarClienteViajes("localhost","12345");

        System.out.print("Introduce tu codigo de cliente: ");
        String codcli = teclado.nextLine();

        int opcion;
        do {
            opcion = menu(teclado);
            switch (opcion) {
                case 0: // Guardar los datos en el fichero y salir del programa

                    // TODO
                    gestorAux.cierraSesion();
                    //opcion = 0;

                    break;

                case 1: { // Consultar viajes con un origen dado

                    // TODO

                    System.out.print("Introduce el origen: ");
                    String origen = teclado.next();

                    if(gestorAux.consultaViajes(origen).isEmpty()) {
                        System.out.println("No hay viajes disponibles con ese origen");		//comprobar que no existe el origen
                    } else {

                        System.out.println("Viajes disponibles: ");
                        System.out.println(gestorAux.consultaViajes(origen).toJSONString());

                    }

                    break;
                }

                case 2: { // Reservar un viaje--> QUE NO EXISTA, QUE ESTÉ LLENO, FINALIZADO, QUE YA LO HAYAS RESERVADO TU MISMO

                    // TODO
                    System.out.print("Ahora se mostrarán los viajes que disponemos, introduzca el origen deseado: ");
                    String destino = teclado.next();

                    JSONArray o = gestorAux.consultaViajes(destino);
                    if(!o.isEmpty()) {

                        for (int i = 0; i < o.size(); i++) {    //comprobar si existe el destino

                            System.out.println(i + " --> " + o.get(i));
                        }
                        System.out.print("Introduce el codigo del viaje quiere reservar: ");
                        String codViaje = teclado.next();
                        JSONObject devolver = gestorAux.reservaViaje(codViaje, codcli);
                        if (devolver.isEmpty())
                                System.out.println("No es posible realizar la reserva");
                            else
                                System.out.println("Datos del viaje resevado: " + devolver.toJSONString());
                        }

                }
                break;


                case 3: { // Anular una reserva --> QUE NO EXISTISCA, PASSADA,

                    // TODO
                    System.out.print("Introduzca su código de viaje: ");
                    String codViaje = teclado.next();
                    //Comprobar código erróneo
                    JSONObject viaje = gestorAux.anulaReserva(codViaje, codcli);
                    if (viaje.isEmpty()) {
                        System.out.println("No existe ningún viaje con el código introducido o el viaje ha finalizado.");

                    } else {
                            System.out.println("Datos del viaje actualizados: " + viaje.toJSONString());
                        }
                    }
                    break;


                case 4: { // Ofertar un viaje, --> QUE LA FECHA CORRECTA, PRECIO >0, PLAZAS > 0,
                    // TODO
                    System.out.print("Introduzca el origen del viaje: ");
                    String origen = teclado.nextLine();
                    System.out.print("Introduzca un destino: ");
                    String destino = teclado.nextLine();


                    long precio = 0;
                    long numplazas = 0;
                    String fecha = null;
                    boolean f = false;
                    boolean p = false;
                    boolean pl = false;
                    while (!f) {
                        System.out.print("Introduzca una fecha con formato dd-mm-yyyy: ");
                        fecha = teclado.nextLine();
                        if (!gestorAux.fechaCorrecta(fecha)) {
                            System.out.print("Introduzca una fecha con formato dd-mm-yyyy: ");
                            fecha = teclado.nextLine();
                        } else f = true;
                    }
                    while (!p) {
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
                        if (numplazas <= 0)
                            System.out.println("Introduce un numero de plazas mayor que 0");
                        else
                            pl = true;

                        }
                        JSONObject v = gestorAux.ofertaViaje(codcli, origen, destino, fecha, precio, numplazas);
                        //if (v.isEmpty())
                           //System.out.println("No se puede ofertar el viaje. ");
                        //else
                           //System.out.println(v.toJSONString());
                        break;
                    }


                case 5: { // Borrar un viaje ofertado --> QUE SEA TUYO, CODIGO CORRECTO, QUE ESTE FECHA VALIDA

                    // TODO
                    System.out.print("Introduzca el código del viaje que desea borrar: ");
                    String codviaje = teclado.next();
                    JSONObject res = gestorAux.borraViaje(codviaje, codcli);
                    if(!res.isEmpty())
                        System.out.println("El viaje se ha borrado correctamente "+ res.toJSONString());
                    else System.out.println("Ha ocurrido un error borrando el viaje "+ codviaje);
                    break;
                }

            } // fin switch

        } while (opcion != 0);

    } // fin de main

} // fin class

