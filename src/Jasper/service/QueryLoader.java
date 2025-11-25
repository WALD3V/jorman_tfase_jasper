package Jasper.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Cargador dinámico de queries SQL desde archivos externos.
 * Lee los archivos .sql desde la carpeta resources/sql/ y los mantiene en memoria.
 */
public class QueryLoader {

    private static final Map<String, String> QUERIES = new HashMap<>();
    private static final String SQL_PATH = "/Jasper/resources/sql/";

    static {
        loadAllQueries();
    }

    /**
     * Carga todos los archivos SQL disponibles en la carpeta resources/sql/
     */
    private static void loadAllQueries() {
        String[] queryNames = {
            "QUERY_ROLES_PRINCIPALES",
            "QUERY_RUBROS",
            "QUERY_DET_HAB_DES",
            "QUERY_INGRESOS",
            "QUERY_EGRESOS",
            "QUERY_HAB_TOTSAL_DES",
            "QUERY_DESCUENTO_TOTAL",
            "QUERY_NETO"
        };

        for (String queryName : queryNames) {
            String query = loadQuery(queryName);
            if (query != null) {
                QUERIES.put(queryName, query);
            }
        }
    }

    /**
     * Carga un archivo SQL específico
     * @param queryName Nombre del archivo sin extensión (ej: QUERY_ROLES_PRINCIPALES)
     * @return Contenido del archivo SQL o null si no existe
     */
    private static String loadQuery(String queryName) {
        try {
            String fileName = SQL_PATH + queryName + ".sql";
            InputStream inputStream = QueryLoader.class.getResourceAsStream(fileName);

            if (inputStream == null) {
                System.err.println("Advertencia: No se encontró el archivo SQL: " + fileName);
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder query = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                query.append(line).append("\n");
            }

            reader.close();
            return query.toString().trim();

        } catch (Exception e) {
            System.err.println("Error cargando query " + queryName + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene una query cargada por su nombre
     * @param queryName Nombre de la query
     * @return La query SQL o una cadena vacía si no existe
     */
    public static String getQuery(String queryName) {
        return QUERIES.getOrDefault(queryName, "");
    }

    /**
     * Verifica si una query existe en memoria
     * @param queryName Nombre de la query
     * @return true si existe, false si no
     */
    public static boolean hasQuery(String queryName) {
        return QUERIES.containsKey(queryName);
    }

    /**
     * Obtiene todas las queries cargadas
     * @return Mapa de nombres y queries
     */
    public static Map<String, String> getAllQueries() {
        return new HashMap<>(QUERIES);
    }

    /**
     * Recarga todas las queries (útil para desarrollo)
     */
    public static void reload() {
        QUERIES.clear();
        loadAllQueries();
    }
}
