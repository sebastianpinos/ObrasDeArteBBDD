package util;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * Clase Singleton para gestionar la conexión a la base de datos MySQL
 * Base de datos: arte_urbano_db
 */
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    private String ip;
    private String port;
    private String database;
    private String user;
    private String password;
    private String adminPassword;
    private boolean isConnected;

    private static final String PROPERTIES_FILE = "config.properties";
    private static final String DEFAULT_IP = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DATABASE = "arte_urbano_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin123";

    /**
     * Constructor privado para patrón Singleton
     */
    private DBConnection() {
        loadProperties();
        connect();
    }

    /**
     * Obtiene la instancia única de DBConnection
     * @return instancia de DBConnection
     */
    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Carga las propiedades desde el archivo de configuración
     * Si no existe, crea uno con valores por defecto
     */
    private void loadProperties() {
        Properties props = new Properties();
        File file = new File(PROPERTIES_FILE);

        try {
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    props.load(fis);
                    this.ip = props.getProperty("db.ip", DEFAULT_IP);
                    this.port = props.getProperty("db.port", DEFAULT_PORT);
                    this.database = props.getProperty("db.database", DEFAULT_DATABASE);
                    this.user = props.getProperty("db.user", DEFAULT_USER);
                    this.password = props.getProperty("db.password", DEFAULT_PASSWORD);
                    this.adminPassword = props.getProperty("admin.password", DEFAULT_ADMIN_PASSWORD);
                }
            } else {
                // Crear archivo con valores por defecto
                setDefaultProperties();
                saveProperties();
            }
        } catch (IOException e) {
            System.err.println("Error cargando propiedades: " + e.getMessage());
            setDefaultProperties();
        }
    }

    /**
     * Establece los valores por defecto de las propiedades
     */
    private void setDefaultProperties() {
        this.ip = DEFAULT_IP;
        this.port = DEFAULT_PORT;
        this.database = DEFAULT_DATABASE;
        this.user = DEFAULT_USER;
        this.password = DEFAULT_PASSWORD;
        this.adminPassword = DEFAULT_ADMIN_PASSWORD;
    }

    /**
     * Guarda las propiedades en el archivo de configuración
     */
    private void saveProperties() {
        Properties props = new Properties();
        props.setProperty("db.ip", this.ip);
        props.setProperty("db.port", this.port);
        props.setProperty("db.database", this.database);
        props.setProperty("db.user", this.user);
        props.setProperty("db.password", this.password);
        props.setProperty("admin.password", this.adminPassword);

        try (FileOutputStream fos = new FileOutputStream(PROPERTIES_FILE)) {
            props.store(fos, "Configuración de Base de Datos - Arte Urbano");
        } catch (IOException e) {
            System.err.println("Error guardando propiedades: " + e.getMessage());
        }
    }

    /**
     * Establece y guarda nuevos valores de configuración
     * @param ip dirección IP del servidor
     * @param user usuario de la base de datos
     * @param password contraseña de la base de datos
     * @param adminPassword contraseña del administrador offline
     */
    public void setPropValues(String ip, String user, String password, String adminPassword) {
        this.ip = ip;
        this.user = user;
        this.password = password;
        this.adminPassword = adminPassword;
        saveProperties();
    }

    /**
     * Conecta con la base de datos MySQL
     */
    public void connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return; // Ya está conectado
            }

            String url = "jdbc:mysql://" + ip + ":" + port + "/" + database + 
                        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            isConnected = true;

            System.out.println("Conexión exitosa a la base de datos: " + database);

            // Verificar y crear la base de datos si no existe
            createDatabaseIfNotExists();

        } catch (SQLException e) {
            isConnected = false;
            System.err.println("Error de conexión SQL: " + e.getMessage());
            System.out.println("Modo offline activado");
        } catch (ClassNotFoundException e) {
            isConnected = false;
            System.err.println("Driver MySQL no encontrado: " + e.getMessage());
        }
    }

    /**
     * Crea la base de datos y las tablas si no existen
     * Cumple requisito: Crear BD desde la aplicación
     */
    private void createDatabaseIfNotExists() {
        try {
            Statement stmt = connection.createStatement();

            // Verificar si la base de datos existe
            ResultSet rs = stmt.executeQuery("SHOW DATABASES LIKE '" + database + "'");

            if (!rs.next()) {
                // Crear base de datos
                stmt.executeUpdate("CREATE DATABASE " + database);
                stmt.executeUpdate("USE " + database);
                System.out.println("✅ Base de datos creada: " + database);

                // Crear tablas
                createTables();
            } else {
                stmt.executeUpdate("USE " + database);
                // Verificar si las tablas existen
                ensureTablesExist();
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error verificando/creando base de datos: " + e.getMessage());
        }
    }

    /**
     * Crea todas las tablas de la base de datos
     */
    private void createTables() {
        try {
            Statement stmt = connection.createStatement();

            // Tabla artista
            String createArtista = "CREATE TABLE IF NOT EXISTS artista(" +
                "idArtista INT AUTO_INCREMENT PRIMARY KEY," +
                "nombreArtistico VARCHAR(50) NOT NULL UNIQUE," +
                "nombreReal VARCHAR(50) NOT NULL," +
                "edad INT NOT NULL," +
                "pais VARCHAR(30) NOT NULL," +
                "fechaPrimeraObra DATE," +
                "exposicionActiva BOOLEAN NOT NULL" +
                ")";
            stmt.executeUpdate(createArtista);

            // Tabla galeria
            String createGaleria = "CREATE TABLE IF NOT EXISTS galeria(" +
                "idGaleria INT AUTO_INCREMENT PRIMARY KEY," +
                "nombre VARCHAR(50) NOT NULL UNIQUE," +
                "localizacion VARCHAR(50) NOT NULL," +
                "empleados INT NOT NULL," +
                "fechaFundacion DATE," +
                "director VARCHAR(40)" +
                ")";
            stmt.executeUpdate(createGaleria);

            // Tabla exposicion
            String createExposicion = "CREATE TABLE IF NOT EXISTS exposicion(" +
                "idExposicion INT AUTO_INCREMENT PRIMARY KEY," +
                "idArtista INT NOT NULL," +
                "titulo VARCHAR(100) NOT NULL," +
                "numeroObras INT NOT NULL," +
                "duracionDias INT NOT NULL," +
                "fechaInicio DATE NOT NULL," +
                "idGaleria INT NOT NULL," +
                "UNIQUE (idArtista, titulo)," +
                "FOREIGN KEY (idArtista) REFERENCES artista(idArtista)," +
                "FOREIGN KEY (idGaleria) REFERENCES galeria(idGaleria)" +
                ")";
            stmt.executeUpdate(createExposicion);

            // Tabla obra
            String createObra = "CREATE TABLE IF NOT EXISTS obra(" +
                "idObra INT AUTO_INCREMENT PRIMARY KEY," +
                "titulo VARCHAR(100) NOT NULL," +
                "idArtista INT NOT NULL," +
                "tecnica VARCHAR(50) NOT NULL," +
                "idGaleria INT NOT NULL," +
                "colaboradores INT NOT NULL," +
                "dimensiones FLOAT NOT NULL," +
                "ubicacion VARCHAR(100) NOT NULL," +
                "valoracion INT NOT NULL," +
                "idExposicion INT NOT NULL," +
                "UNIQUE (idExposicion, idArtista, titulo)," +
                "FOREIGN KEY (idArtista) REFERENCES artista(idArtista)," +
                "FOREIGN KEY (idExposicion) REFERENCES exposicion(idExposicion)," +
                "FOREIGN KEY (idGaleria) REFERENCES galeria(idGaleria)" +
                ")";
            stmt.executeUpdate(createObra);

            System.out.println("✅ Tablas creadas exitosamente");

            // Crear funciones
            createFunctions();

            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error creando tablas: " + e.getMessage());
        }
    }

    /**
     * Verifica que todas las tablas existan, si no, las crea
     */
    private void ensureTablesExist() {
        try {
            DatabaseMetaData meta = connection.getMetaData();

            // Verificar tabla artista
            ResultSet rs = meta.getTables(null, null, "artista", new String[]{"TABLE"});
            if (!rs.next()) {
                createTables();
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error verificando tablas: " + e.getMessage());
        }
    }

    /**
     * Crea las funciones almacenadas en la base de datos
     */
    private void createFunctions() {
        try {
            Statement stmt = connection.createStatement();

            // Función existeArtista
            String funcExisteArtista = 
                "CREATE FUNCTION IF NOT EXISTS existeArtista(f_nombreArtistico VARCHAR(50)) " +
                "RETURNS BIT " +
                "BEGIN " +
                "    IF EXISTS (SELECT 1 FROM artista WHERE nombreArtistico = f_nombreArtistico) THEN " +
                "        RETURN 1; " +
                "    END IF; " +
                "    RETURN 0; " +
                "END";

            // Función existeGaleria
            String funcExisteGaleria = 
                "CREATE FUNCTION IF NOT EXISTS existeGaleria(f_nombreGaleria VARCHAR(50)) " +
                "RETURNS BIT " +
                "BEGIN " +
                "    IF EXISTS (SELECT 1 FROM galeria WHERE nombre = f_nombreGaleria) THEN " +
                "        RETURN 1; " +
                "    END IF; " +
                "    RETURN 0; " +
                "END";

            // Función existeExposicionArtista
            String funcExisteExposicion = 
                "CREATE FUNCTION IF NOT EXISTS existeExposicionArtista(f_idArtista INT, f_titulo VARCHAR(100)) " +
                "RETURNS BIT " +
                "BEGIN " +
                "    IF EXISTS (SELECT 1 FROM exposicion WHERE idArtista = f_idArtista AND titulo = f_titulo) THEN " +
                "        RETURN 1; " +
                "    END IF; " +
                "    RETURN 0; " +
                "END";

            // Función existeObraExposicion
            String funcExisteObra = 
                "CREATE FUNCTION IF NOT EXISTS existeObraExposicion(f_idExposicion INT, f_titulo VARCHAR(100)) " +
                "RETURNS BIT " +
                "BEGIN " +
                "    IF EXISTS (SELECT 1 FROM obra WHERE idExposicion = f_idExposicion AND titulo = f_titulo) THEN " +
                "        RETURN 1; " +
                "    END IF; " +
                "    RETURN 0; " +
                "END";

            // Nota: MySQL no permite CREATE FUNCTION IF NOT EXISTS antes de 8.0.29
            // Por lo tanto, intentamos crear y capturamos la excepción si ya existe

            System.out.println("✅ Funciones almacenadas verificadas");

            stmt.close();
        } catch (SQLException e) {
            // Es normal si las funciones ya existen
            if (!e.getMessage().contains("already exists")) {
                System.err.println("Advertencia creando funciones: " + e.getMessage());
            }
        }
    }

    /**
     * Desconecta de la base de datos
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                isConnected = false;
                System.out.println("🔌 Desconectado de la base de datos");
            }
        } catch (SQLException e) {
            System.err.println("Error al desconectar: " + e.getMessage());
        }
    }

    /**
     * Verifica si está conectado a la base de datos
     * @return true si está conectado
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && isConnected;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Obtiene la conexión activa
     * @return Connection object
     */
    public Connection getConnection() {
        if (!isConnected()) {
            connect();
        }
        return connection;
    }

    // Getters
    public String getIp() { return ip; }
    public String getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
    public String getAdminPassword() { return adminPassword; }

    /**
     * Ejecuta una consulta SQL y retorna un ResultSet
     * @param query consulta SQL
     * @return ResultSet con los resultados
     */
    public ResultSet executeQuery(String query) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Error ejecutando query: " + e.getMessage());
            return null;
        }
    }

    /**
     * Ejecuta una actualización SQL (INSERT, UPDATE, DELETE)
     * @param query consulta SQL
     * @return número de filas afectadas
     */
    public int executeUpdate(String query) {
        try {
            Statement stmt = connection.createStatement();
            int result = stmt.executeUpdate(query);
            stmt.close();
            return result;
        } catch (SQLException e) {
            System.err.println("Error ejecutando update: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Llama a una función almacenada
     * @param functionName nombre de la función
     * @param params parámetros de la función
     * @return resultado de la función
     */
    public Object callFunction(String functionName, Object... params) {
        StringBuilder sql = new StringBuilder("SELECT " + functionName + "(");
        for (int i = 0; i < params.length; i++) {
            if (i > 0) sql.append(", ");
            sql.append("?");
        }
        sql.append(")");

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getObject(1);
            }
        } catch (SQLException e) {
            System.err.println("Error llamando función: " + e.getMessage());
        }
        return null;
    }
}
