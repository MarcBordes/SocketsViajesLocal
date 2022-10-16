package servidor;

import java.io.IOException;
import java.net.SocketException;

import gestor.GestorViajes;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import comun.MyStreamSocket;

/**
 * Clase ejecutada por cada hebra encargada de servir a un cliente del servicio de viajes.
 * El metodo run contiene la logica para gestionar una sesion con un cliente.
 */

class HiloServidorViajes implements Runnable {


	private MyStreamSocket myDataSocket;
	private GestorViajes gestor;
	private JSONParser parse;

	/**
	 * Construye el objeto a ejecutar por la hebra para servir a un cliente
	 * @param	myDataSocket	socket stream para comunicarse con el cliente
	 * @param	unGestor		gestor de viajes
	 */
	HiloServidorViajes(MyStreamSocket myDataSocket, GestorViajes unGestor) {
		// POR IMPLEMENTAR
		this.myDataSocket = myDataSocket;
		this.gestor = unGestor;
		parse = new JSONParser();

	}

	/**
	 * Gestiona una sesion con un cliente	
	 */
	public void run( ) {
		JSONObject object;
		String operacion = "0";
		boolean done = false;


		try {

			while (!done) {
				// Recibe una petición del cliente

				object = (JSONObject) parse.parse(myDataSocket.receiveMessage());
				System.out.println(object.toJSONString());

				// Extrae la operación y sus parámetros
				operacion = (String) object.get("operacion");

				switch (operacion) {
				case "0":
					gestor.guardaDatos();
					myDataSocket.sendMessage("Cerrando sesion..");
					myDataSocket.close();
					done = true;
					break;

				case "1": { // Consulta los viajes con un origen dado
					String origen = (String) object.get("origen");
					String res = gestor.consultaViajes(origen).toJSONString();
					myDataSocket.sendMessage(res);
					break;
				} 
				case "2": { // Reserva una plaza en un viaje
					String codcli = (String) object.get("codcli");
					String codviaje = (String) object.get("codviaje");
					String res = gestor.reservaViaje(codviaje,codcli).toJSONString();
					myDataSocket.sendMessage(res);
					break;
				}             
				case "3": { // Anular reserva
					String codviaje = (String) object.get("codviaje");
					String codcliente = (String) object.get("codcliente");
					String res = gestor.anulaReserva(codviaje, codcliente).toJSONString();
					myDataSocket.sendMessage(res);
					break;
				}
				case "4": { // Oferta un viaje
					String codprop = (String) object.get("codprop");
					String origen = (String)object.get("origen");
					String destino = (String) object.get("destino");
					String fecha = (String) object.get("fecha");
					long precio = (long) object.get("precio");
					long numplazas = (long) object.get("numplazas");
					String res = gestor.ofertaViaje(codprop, origen, destino, fecha, precio, numplazas).toJSONString();
					myDataSocket.sendMessage(res);
					break;
				}
				case "5": { // Borra un viaje

					String codviaje = (String) object.get("codviaje");
					String codcli = (String) object.get("codcliente");
					String res = gestor.borraViaje(codviaje, codcli).toJSONString();
					myDataSocket.sendMessage(res);

					break;
				}

				} // fin switch
			} // fin while   
		} // fin try
		catch (SocketException ex) {
			System.out.println("Capturada SocketException");
		}

		catch (IOException ex) {
			System.out.println("Capturada IOException");
		}

		catch (Exception ex) {
			System.out.println("Exception caught in thread: " + ex);
		} // fin catch
	} //fin run

} //fin class 
