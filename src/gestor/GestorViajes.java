package gestor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GestorViajes {

    private static FileWriter os;			// stream para escribir los datos en el fichero
    private static FileReader is;			// stream para leer los datos del fichero

    /**
     * 	Diccionario para manejar los datos en memoria.
     * 	La clave es el codigo único del viaje.
     */
    private static HashMap<String, Viaje> mapa;


    /**
     * Constructor del gestor de viajes
     * Crea o Lee un fichero con datos de prueba
     */
    public GestorViajes() {
        mapa =  new HashMap<String, Viaje>();
        File file = new File("viajes.json");
        try {
            if (!file.exists() ) {
                // Si no existe el fichero de datos, los genera y escribe
                os = new FileWriter(file);
                generaDatos();
                escribeFichero(os);
                os.close();
            }
            // Si existe el fichero o lo acaba de crear, lo lee y rellena el diccionario con los datos
            is = new FileReader(file);
            leeFichero(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Cuando cada cliente cierra su sesion volcamos los datos en el fichero para mantenerlos actualizados
     */
    public void guardaDatos(){
        File file = new File("viajes.json");
        try {
            os = new FileWriter(file);
            escribeFichero(os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * escribe en el fichero un array JSON con los datos de los viajes guardados en el diccionario
     *
     * @param os	stream de escritura asociado al fichero de datos
     */
    private void escribeFichero(FileWriter os) throws IOException {
        // TODO

        JSONArray array = new JSONArray();
        for (String k : mapa.keySet()) {
            JSONObject valor = mapa.get(k).toJSON();
            array.add(valor);
        }
        String f = array.toJSONString();
        try {
            os.write(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Genera los datos iniciales
     */
    private void generaDatos() {

        Viaje viaje = new Viaje("pedro", "Castellón", "Alicante", "28-05-2023", 16, 1);
        mapa.put(viaje.getCodviaje(), viaje);

        viaje = new Viaje("pedro", "Alicante", "Castellón", "29-05-2023", 16, 1);
        mapa.put(viaje.getCodviaje(), viaje);

        viaje = new Viaje("maria", "Madrid", "Valencia", "07-06-2023", 7, 2);
        mapa.put(viaje.getCodviaje(), viaje);

        viaje = new Viaje("carmen", "Sevilla", "Barcelona", "12-08-2023", 64, 1);
        mapa.put(viaje.getCodviaje(), viaje);

        viaje = new Viaje("juan", "Castellón", "Cordoba", "07-11-2023", 39, 3);
        mapa.put(viaje.getCodviaje(), viaje);

    }

    /**
     * Lee los datos del fichero en formato JSON y los añade al diccionario en memoria
     *
     * @param is	stream de lectura de los datos del fichero
     */
    private void leeFichero(FileReader is) {
        JSONParser parser = new JSONParser();
        try {
            // Leemos toda la información del fichero en un array de objetos JSON
            JSONArray array = (JSONArray) parser.parse(is);
            // Rellena los datos del diccionario en memoria a partir del JSONArray
            rellenaDiccionario(array);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * Rellena el diccionario a partir de los datos en un JSONArray
     *
     * @param array	JSONArray con los datos de los Viajes
     */
    private void rellenaDiccionario(JSONArray array) {
        // TODO


        for (Object o : array) {
            Viaje v = new Viaje((JSONObject) o);
            mapa.put(v.getCodviaje(), v);
        }

    }


    /**
     * Devuelve los viajes disponibles con un origen dado
     *
     * @param origen
     * @return JSONArray de viajes con un origen dado. Vacío si no hay viajes disponibles con ese origen
     */
    public JSONArray consultaViajes(String origen) {
        // TODO


        JSONArray arrayDatosViaje = new JSONArray();

        for(String c : mapa.keySet()){			//per cada viaje del diccionari
            Viaje v = mapa.get(c);				//creem el viaje en el constructor crear anteriorment
            if(v.getOrigen().equals(origen) && !v.finalizado()){	//comprovem que es el mateix origen
                JSONObject jv = v.toJSON();	//pasem a JSON
                arrayDatosViaje.add(jv);
            }
        }

        return arrayDatosViaje;

    }


    /**
     * El cliente codcli reserva el viaje codviaje
     *
     * @param codviaje
     * @param codcli
     * @return JSONObject con la información del viaje. Vacío si no existe o no está disponible
     */
    public JSONObject reservaViaje(String codviaje, String codcli) {

        // TODO
        JSONObject o = new JSONObject();

        if(!mapa.containsKey(codviaje) || mapa.get(codviaje).finalizado()) // si no está el viaje o está finalizado devuelve un obj vacío
            return o; //si pasa es pq el viaje existe.
        Viaje v = mapa.get(codviaje);
        if(!v.quedanPlazas()||v.getCodprop().equals(codcli))
            return o;
        //hemos pasado todos los filtros y vamos a modificar el objeto viaje.

        v.anyadePasajero(codcli);
        o = v.toJSON();
        return o;

    }

    /**
     * El cliente codcli anula su reserva del viaje codviaje
     *
     * @param codviaje	codigo del viaje a anular
     * @param codcli	codigo del cliente
     * @return	JSON del viaje en que se ha anulado la reserva. JSON vacio si no se ha anulado
     */
    public JSONObject anulaReserva(String codviaje, String codcli) {
        // TODO

        JSONObject o = new JSONObject();
        if (!mapa.containsKey(codviaje) || mapa.get(codviaje).finalizado())
            return o;

        Viaje v = mapa.get(codviaje);
        v.borraPasajero(codcli);
        o = v.toJSON();

        return o;



    }

    /**
     * Devuelve si una fecha es válida y futura
     * @param fecha
     * @return
     */
    private boolean es_fecha_valida(String fecha) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {
            LocalDate dia = LocalDate.parse(fecha, formatter);
            LocalDate hoy = LocalDate.now();

            return dia.isAfter(hoy);
        }
        catch (DateTimeParseException e) {
            System.out.println("Fecha invalida: " + fecha);
            return false;
        }

    }

    /**
     * El cliente codcli oferta un Viaje
     * @param codcli
     * @param origen
     * @param destino
     * @param fecha
     * @param precio
     * @param numplazas
     * @return	JSONObject con los datos del viaje ofertado
     */
    public JSONObject ofertaViaje(String codcli, String origen, String destino, String fecha, long precio, long numplazas) {
        // TODO

        Viaje v = new Viaje(codcli,origen,destino, fecha, precio, numplazas);
        mapa.put(v.getCodviaje(), v);	//añadimos el viaje al mapa para ofertarlo

        return v.toJSON();

    }



    /**
     * El cliente codcli borra un viaje que ha ofertado
     *
     * @param codviaje	codigo del viaje a borrar
     * @param codcli	codigo del cliente
     * @return	JSONObject del viaje borrado. JSON vacio si no se ha borrado
     */
    public JSONObject borraViaje(String codviaje, String codcli) {
        // TODO

        //comprobar si el viaje  existe, en el caso de que exista, comprobar que no ha ocurrido, luego ya modificar

        JSONObject obj = new JSONObject();

        if (mapa.containsKey(codviaje)) {
            if (codcli.equals(mapa.get(codviaje).getCodprop())) {	//si el que lo borra es el propietario
                if (!mapa.get(codviaje).finalizado()) {		//comprobamos si ha finalizado
                    obj = mapa.get(codviaje).toJSON();
                    mapa.remove(codviaje);					//borramos el viaje
                }
            }
        }
        return obj;
    }
    public Set<String> obtenerViajes(){
        return mapa.keySet();

    }

    public JSONArray consultaDestinos(String destino) {
        // TODO

        JSONArray arrayDatosViaje = new JSONArray();

        for(String c : mapa.keySet()){			//per cada viaje del diccionari
            Viaje v = mapa.get(c);				//creem el viaje en el constructor crear anteriorment
            if(v.getDestino().equals(destino) && !v.finalizado()){	//comprovem que es el mateix origen
                JSONObject jv = v.toJSON();	//pasem a JSON
                arrayDatosViaje.add(jv);
            }
        }

        return arrayDatosViaje;



    }
    public boolean existeViaje(String codviaje){
        return mapa.containsKey(codviaje);
    }
    public boolean getFechaOk (String fecha){
        //--> si devuelve true ok y en el caso de que no sea true pasa el de abajo
        //el print solo aparece cuando no es válida
        return es_fecha_valida(fecha);
    }
    public boolean getOrganizador(String codcli, String codviaje){
        return mapa.get(codviaje).getCodprop().equals(codcli);
    }

}

