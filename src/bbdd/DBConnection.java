package bbdd;

import java.io.*;
import java.sql.*;
import java.util.Properties;

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

    private DBConnection() {
        loadProperties();
        connect();
    }

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
                setDefaultProperties();
                saveProperties();
            }
        } catch (IOException e) {
            System.err.println("Error cargando propiedades: " + e.getMessage());
            setDefaultProperties();
        }
    }

    private void setDefaultProperties() {
        this.ip = DEFAULT_IP;
        this.port = DEFAULT_PORT;
        this.database = DEFAULT_DATABASE;
        this.user = DEFAULT_USER;
        this.password = DEFAULT_PASSWORD;
        this.adminPassword = DEFAULT_ADMIN_PASSWORD;
    }

    private void saveProperties() {
        Properties props = new Properties();
        props.setProperty("db.ip", this.ip);
        props.setProperty("db.port", this.port);
        props.setProperty("db.database", this.database);
        props.setProperty("db.user", this.user);
        props.setProperty("db.password", this.password);
        props.setProperty("admin.password", this.adminPassword);

        try (FileOutputStream fos = new FileOutputStream(PROPERTIES_FILE)) {
            props.store(fos, "Configuracion de Base de Datos");
        } catch (IOException e) {
            System.err.println("Error guardando propiedades: " + e.getMessage());
        }
    }

    public void connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            // Conectar SIN especificar la base de datos
            String urlWithoutDB = "jdbc:mysql://" + ip + ":" + port +
                    "/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(urlWithoutDB, user, password);

            System.out.println("Conexión exitosa al servidor MySQL");

            // Crear la base de datos si no existe
            createDatabaseIfNotExists();

            // Cerrar y reconectar a la base de datos específica
            connection.close();

            String urlWithDB = "jdbc:mysql://" + ip + ":" + port + "/" + database +
                    "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

            connection = DriverManager.getConnection(urlWithDB, user, password);
            isConnected = true;

            System.out.println("Conectado y usando base de datos: " + database);

        } catch (SQLException e) {
            isConnected = false;
            System.err.println("Error de conexión SQL: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Modo offline activado");
        } catch (ClassNotFoundException e) {
            isConnected = false;
            System.err.println("Driver MySQL no encontrado: " + e.getMessage());
        }
    }

    private void createDatabaseIfNotExists() {
        try {
            Statement stmt = connection.createStatement();

            // PASO 1: Verificar si la BD existe
            ResultSet rs = stmt.executeQuery("SHOW DATABASES LIKE '" + database + "'");
            boolean dbExists = rs.next();
            rs.close();

            if (!dbExists) {
                // La BD no existe, crearla
                stmt.executeUpdate("CREATE DATABASE " + database);
                System.out.println("✓ Base de datos creada: " + database);
            } else {
                System.out.println("✓ Base de datos ya existe: " + database);
            }

            // PASO 2: Usar la base de datos
            stmt.executeUpdate("USE " + database);

            // PASO 3: Verificar si las tablas existen
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rsTables = meta.getTables(null, database, "galeria", new String[]{"TABLE"});
            boolean tablesExist = rsTables.next();
            rsTables.close();

            if (!tablesExist) {
                System.out.println("→ Creando tablas...");
                createTablesWithCleanup();
            } else {
                System.out.println("✓ Las tablas ya existen");
                // Verificar funciones
                createFunctions();
            }

            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error verificando/creando base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTablesWithCleanup() {
        try {
            Statement stmt = connection.createStatement();

            // LIMPIAR: Eliminar tablas si existen (en orden inverso por FK)
            String[] dropTables = {
                    "DROP TABLE IF EXISTS obra",
                    "DROP TABLE IF EXISTS exposicion",
                    "DROP TABLE IF EXISTS artista",
                    "DROP TABLE IF EXISTS galeria"
            };

            for (String drop : dropTables) {
                try {
                    stmt.executeUpdate(drop);
                } catch (SQLException e) {
                    // Ignorar errores de DROP
                }
            }

            System.out.println("→ Limpieza completada");

            // CREAR tablas limpias
            createTables();

            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error en limpieza de tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() {
        try {
            Statement stmt = connection.createStatement();

            // Tabla galeria (SIN IF NOT EXISTS porque ya limpiamos)
            String createGaleria = "CREATE TABLE galeria(" +
                    "idGaleria INT AUTO_INCREMENT PRIMARY KEY," +
                    "nombre VARCHAR(50) NOT NULL UNIQUE," +
                    "localizacion VARCHAR(50) NOT NULL," +
                    "empleados INT NOT NULL," +
                    "fechaFundacion DATE," +
                    "director VARCHAR(40)," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createGaleria);
            System.out.println("✓ Tabla galeria creada");

            // Tabla artista
            String createArtista = "CREATE TABLE artista(" +
                    "idArtista INT AUTO_INCREMENT PRIMARY KEY," +
                    "nombreArtistico VARCHAR(50) NOT NULL UNIQUE," +
                    "nombreReal VARCHAR(50) NOT NULL," +
                    "edad INT NOT NULL," +
                    "pais VARCHAR(30) NOT NULL," +
                    "fechaPrimeraObra DATE," +
                    "exposicionActiva BOOLEAN NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createArtista);
            System.out.println("✓ Tabla artista creada");

            // Tabla exposicion
            String createExposicion = "CREATE TABLE exposicion(" +
                    "idExposicion INT AUTO_INCREMENT PRIMARY KEY," +
                    "idArtista INT NOT NULL," +
                    "titulo VARCHAR(100) NOT NULL," +
                    "numeroObras INT NOT NULL," +
                    "duracionDias INT NOT NULL," +
                    "fechaInicio DATE NOT NULL," +
                    "idGaleria INT NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "UNIQUE (idArtista, titulo)," +
                    "FOREIGN KEY (idArtista) REFERENCES artista(idArtista) ON DELETE CASCADE," +
                    "FOREIGN KEY (idGaleria) REFERENCES galeria(idGaleria) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createExposicion);
            System.out.println("✓ Tabla exposicion creada");

            // Tabla obra
            String createObra = "CREATE TABLE obra(" +
                    "idObra INT AUTO_INCREMENT PRIMARY KEY," +
                    "titulo VARCHAR(100) NOT NULL," +
                    "idArtista INT NOT NULL," +
                    "tecnica VARCHAR(50) NOT NULL," +
                    "idGaleria INT NOT NULL," +
                    "colaboradores INT NOT NULL DEFAULT 0," +
                    "dimensiones FLOAT NOT NULL," +
                    "ubicacion VARCHAR(100) NOT NULL," +
                    "valoracion INT NOT NULL CHECK (valoracion BETWEEN 1 AND 10)," +
                    "idExposicion INT," + // NULLABLE
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (idArtista) REFERENCES artista(idArtista) ON DELETE CASCADE," +
                    "FOREIGN KEY (idExposicion) REFERENCES exposicion(idExposicion) ON DELETE SET NULL," +
                    "FOREIGN KEY (idGaleria) REFERENCES galeria(idGaleria) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createObra);
            System.out.println("✓ Tabla obra creada");

            System.out.println("✓ Todas las tablas creadas exitosamente");

            // Crear funciones
            createFunctions();

            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error creando tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createFunctions() {
        try {
            Statement stmt = connection.createStatement();

            // Eliminar funciones si existen
            try { stmt.executeUpdate("DROP FUNCTION IF EXISTS existeArtista"); } catch (SQLException e) {}
            try { stmt.executeUpdate("DROP FUNCTION IF EXISTS existeGaleria"); } catch (SQLException e) {}
            try { stmt.executeUpdate("DROP FUNCTION IF EXISTS existeExposicionArtista"); } catch (SQLException e) {}
            try { stmt.executeUpdate("DROP FUNCTION IF EXISTS existeObraExposicion"); } catch (SQLException e) {}

            // Crear funciones (sin IF NOT EXISTS)
            String funcExisteArtista =
                    "CREATE FUNCTION existeArtista(f_nombreArtistico VARCHAR(50)) " +
                            "RETURNS BIT " +
                            "DETERMINISTIC " +
                            "BEGIN " +
                            "  IF EXISTS (SELECT 1 FROM artista WHERE nombreArtistico = f_nombreArtistico) THEN " +
                            "    RETURN 1; " +
                            "  END IF; " +
                            "  RETURN 0; " +
                            "END";
            stmt.executeUpdate(funcExisteArtista);
            System.out.println("✓ Función existeArtista creada");

            String funcExisteGaleria =
                    "CREATE FUNCTION existeGaleria(f_nombreGaleria VARCHAR(50)) " +
                            "RETURNS BIT " +
                            "DETERMINISTIC " +
                            "BEGIN " +
                            "  IF EXISTS (SELECT 1 FROM galeria WHERE nombre = f_nombreGaleria) THEN " +
                            "    RETURN 1; " +
                            "  END IF; " +
                            "  RETURN 0; " +
                            "END";
            stmt.executeUpdate(funcExisteGaleria);
            System.out.println("✓ Función existeGaleria creada");

            String funcExisteExposicion =
                    "CREATE FUNCTION existeExposicionArtista(f_idArtista INT, f_titulo VARCHAR(100)) " +
                            "RETURNS BIT " +
                            "DETERMINISTIC " +
                            "BEGIN " +
                            "  IF EXISTS (SELECT 1 FROM exposicion WHERE idArtista = f_idArtista AND titulo = f_titulo) THEN " +
                            "    RETURN 1; " +
                            "  END IF; " +
                            "  RETURN 0; " +
                            "END";
            stmt.executeUpdate(funcExisteExposicion);
            System.out.println("✓ Función existeExposicionArtista creada");

            String funcExisteObra =
                    "CREATE FUNCTION existeObraExposicion(f_idExposicion INT, f_titulo VARCHAR(100)) " +
                            "RETURNS BIT " +
                            "DETERMINISTIC " +
                            "BEGIN " +
                            "  IF EXISTS (SELECT 1 FROM obra WHERE idExposicion = f_idExposicion AND titulo = f_titulo) THEN " +
                            "    RETURN 1; " +
                            "  END IF; " +
                            "  RETURN 0; " +
                            "END";
            stmt.executeUpdate(funcExisteObra);
            System.out.println("✓ Función existeObraExposicion creada");

            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error creando funciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && isConnected;
        } catch (SQLException e) {
            return false;
        }
    }

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
            System.err.println("Error llamando funcion: " + e.getMessage());
        }
        return null;
    }
}
