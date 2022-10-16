package cliente;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import comun.MyStreamSocket;

/**
 * Esta clase es un modulo que proporciona la logica de aplicacion
 * para el Cliente del sevicio de viajes usando sockets de tipo stream
 */

public class AuxiliarClienteViajes {

	private final MyStreamSocket mySocket; // Socket de datos para comunicarse con el servidor
	JSONParser parser;

	/**
	 * Construye un objeto auxiliar asociado a un cliente del servicio.
	 * Crea un socket para conectar con el servidor.
	 *
	 * @param    hostName    nombre de la maquina que ejecuta el servidor
	 * @param    portNum        numero de puerto asociado al servicio en el servidor
	 */
	AuxiliarClienteViajes(String hostName, String portNum)
			throws SocketException, UnknownHostException, IOException {

		// IP del servidor
		InetAddress serverHost = InetAddress.getByName(hostName);
		// Puerto asociado al servicio en el servidor
		int serverPort = Integer.parseInt(portNum);
		//Instantiates a stream-mode socket and wait for a connection.
		this.mySocket = new MyStreamSocket(serverHost, serverPort);
		/**/
		System.out.println("Hecha peticion de conexion");
		parser = new JSONParser();
	} // end constructor

	/**
	 * Devuelve los viajes ofertados desde un origen dado
	 *
	 * @param origen origen del viaje ofertado
	 * @return array JSON de viajes desde un origen. array vacio si no hay ninguno
	 */
	public JSONArray consultaViajes(String origen) throws ParseException {
		// POR IMPLEMENTAR

		JSONObject obj = new JSONObject();
		obj.put("operacion", "1");
		obj.put("origen", origen);

		JSONArray array;
		try {
			mySocket.sendMessage(obj.toJSONString());
			String recepcion = mySocket.receiveMessage();

			array = (JSONArray) parser.parse(recepcion);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return array;
	} // end consultaViajes


	/**
	 * Un pasajero reserva un viaje
	 *
	 * @param codviaje   codigo del viaje
	 * @param codcliente codigo del pasajero
	 * @return objeto JSON con los datos del viaje. Vacio si no se ha podido reservar
	 */
	public JSONObject reservaViaje(String codviaje, String codcliente) throws IOException, ParseException {
		// POR IMPLEMENTAR

		JSONObject obj = new JSONObject();
		obj.put("operacion", "2");
		obj.put("codcli", codcliente);
		obj.put("codviaje", codviaje);

		return enviarMensaje(obj.toJSONString());
	} // end reservaViaje


	/**
	 * Un pasajero anula una reserva
	 *
	 * @param codviaje   codigo del viaje
	 * @param codcliente codigo del pasajero
	 * @return objeto JSON con los datos del viaje. Vacio si no se ha podido reservar
	 */
	public JSONObject anulaReserva(String codviaje, String codcliente) throws ParseException, IOException {

		JSONObject obj = new JSONObject();
		obj.put("operacion", "3");
		obj.put("codviaje", codviaje);
		obj.put("codcliente", codcliente);

		return enviarMensaje(obj.toJSONString());
	} // end anulaReserva

	/**
	 * Un cliente oferta un nuevo viaje
	 *
	 * @param codprop   codigo del cliente que hace la oferta
	 * @param origen    origen del viaje
	 * @param destino   destino del viaje
	 * @param fecha     fecha del viaje en formato dd/mm/aaaa
	 * @param precio    precio por plaza
	 * @param numplazas numero de plazas ofertadas
	 * @return viaje ofertado en formatoJSON. Vacio si no se ha podido ofertar
	 */
	public JSONObject ofertaViaje(String codprop, String origen, String destino, String fecha, long precio, long numplazas) throws ParseException {

		JSONObject obj = new JSONObject();
		obj.put("operacion", "4");
		obj.put("codprop", codprop);
		obj.put("origen", origen);
		obj.put("destino", destino);
		obj.put("fecha", fecha);
		obj.put("precio",precio);
		obj.put("numplazas", numplazas);

		return enviarMensaje(obj.toJSONString());
	} // end ofertaViaje

	/**
	 * Un cliente borra una oferta de viaje
	 *
	 * @param codviaje   codigo del viaje
	 * @param codcliente codigo del pasajero
	 * @return objeto JSON con los datos del viaje. Vacio si no se ha podido reservar
	 */
	public JSONObject borraViaje(String codviaje, String codcliente) throws ParseException {

		JSONObject obj = new JSONObject();
		obj.put("operacion", "5");
		obj.put("codviaje", codviaje);
		obj.put("codcliente", codcliente);

		return enviarMensaje(obj.toJSONString());
	} // end borraViaje


	/**
	 * Finaliza la conexion con el servidor
	 */

	public void cierraSesion() throws IOException {

		JSONObject obj = new JSONObject();
		obj.put("operacion", "0");

		try {
			mySocket.sendMessage(obj.toJSONString());
			System.out.println(mySocket.receiveMessage());
			mySocket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	} // end done
//end class

	public boolean fechaCorrecta(String fecha) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		try {
			LocalDate dia = LocalDate.parse(fecha, formatter);
			LocalDate hoy = LocalDate.now();

			return dia.isAfter(hoy);
		}
		catch (DateTimeParseException e) {
			System.out.println("Fecha no correcta: " + fecha);
			return false;
		}

	}
	private JSONObject enviarMensaje(String codificado) throws ParseException {
		JSONObject obj;
		try {
			mySocket.sendMessage(codificado);
			String recepcion = mySocket.receiveMessage();

			obj = (JSONObject) parser.parse(recepcion);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return obj;
	}
}
