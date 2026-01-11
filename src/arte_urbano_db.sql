
CREATE DATABASE IF NOT EXISTS arte_urbano;
USE arte_urbano;

CREATE TABLE IF NOT EXISTS obra(
    idObra INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    idArtista INT NOT NULL,
    tecnica VARCHAR(50) NOT NULL,           -- Spray, Stencil, Mural, etc.
    idGaleria INT NOT NULL,                 -- Galería que representa la obra
    colaboradores INT NOT NULL,             -- Número de artistas colaboradores
    dimensiones FLOAT NOT NULL,             -- Dimensiones en metros cuadrados
    ubicacion VARCHAR(100) NOT NULL,        -- Ubicación física de la obra
    valoracion INT NOT NULL,                -- Valoración de 1 a 10
    idExposicion INT NOT NULL,              -- Exposición a la que pertenece
    UNIQUE (idExposicion, idArtista, titulo)
);

CREATE TABLE IF NOT EXISTS artista(
    idArtista INT AUTO_INCREMENT PRIMARY KEY,
    nombreArtistico VARCHAR(50) NOT NULL UNIQUE,    -- Nombre artístico/tag
    nombreReal VARCHAR(50) NOT NULL,
    edad INT NOT NULL,
    pais VARCHAR(30) NOT NULL,
    fechaPrimeraObra DATE,                          -- Primera obra publicada
    exposicionActiva BOOLEAN NOT NULL               -- Si tiene exposición activa
);

CREATE TABLE IF NOT EXISTS galeria(
    idGaleria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    localizacion VARCHAR(50) NOT NULL,
    empleados INT NOT NULL,
    fechaFundacion DATE,
    director VARCHAR(40)                            -- Director de la galería
);

CREATE TABLE IF NOT EXISTS exposicion(
    idExposicion INT AUTO_INCREMENT PRIMARY KEY,
    idArtista INT NOT NULL,
    titulo VARCHAR(100) NOT NULL,
    numeroObras INT NOT NULL,
    duracionDias INT NOT NULL,                      -- Duración en días
    fechaInicio DATE NOT NULL,
    idGaleria INT NOT NULL,
    UNIQUE (idArtista, titulo)
);

ALTER TABLE obra
    ADD FOREIGN KEY (idArtista) REFERENCES artista(idArtista),
    ADD FOREIGN KEY (idExposicion) REFERENCES exposicion(idExposicion),
    ADD FOREIGN KEY (idGaleria) REFERENCES galeria(idGaleria);

ALTER TABLE exposicion
    ADD FOREIGN KEY (idArtista) REFERENCES artista(idArtista),
    ADD FOREIGN KEY (idGaleria) REFERENCES galeria(idGaleria);

-- Función 1: Verificar si existe un artista por nombre artístico
DELIMITER ||
CREATE FUNCTION existeArtista(f_nombreArtistico VARCHAR(50))
RETURNS BIT
BEGIN
    IF EXISTS (
        SELECT 1
        FROM artista
        WHERE nombreArtistico = f_nombreArtistico
    ) THEN
        RETURN 1;
    END IF;
    RETURN 0;
END; ||
DELIMITER ;

-- Función 2: Verificar si existe una galería
DELIMITER ||
CREATE FUNCTION existeGaleria(f_nombreGaleria VARCHAR(50))
RETURNS BIT
BEGIN
    IF EXISTS (
        SELECT 1
        FROM galeria
        WHERE nombre = f_nombreGaleria
    ) THEN
        RETURN 1;
    END IF;
    RETURN 0;
END; ||
DELIMITER ;

-- Función 3: Verificar si existe una exposición de un artista
DELIMITER ||
CREATE FUNCTION existeExposicionArtista(f_idArtista INT, f_titulo VARCHAR(100))
RETURNS BIT
BEGIN
    IF EXISTS (
        SELECT 1
        FROM exposicion
        WHERE idArtista = f_idArtista
          AND titulo = f_titulo
    ) THEN
        RETURN 1;
    END IF;
    RETURN 0;
END; ||
DELIMITER ;

-- Función 4: Verificar si existe una obra en una exposición
DELIMITER ||
CREATE FUNCTION existeObraExposicion(f_idExposicion INT, f_titulo VARCHAR(100))
RETURNS BIT
BEGIN
    IF EXISTS (
        SELECT 1
        FROM obra
        WHERE idExposicion = f_idExposicion
          AND titulo = f_titulo
    ) THEN
        RETURN 1;
    END IF;
    RETURN 0;
END; ||
DELIMITER ;

-- Procedimiento 1: Obtener obras mejor valoradas
DELIMITER ||
CREATE PROCEDURE obtenerObrasMejorValoradas(IN limite INT)
BEGIN
    SELECT o.*, a.nombreArtistico, g.nombre as galeria
    FROM obra o
    JOIN artista a ON o.idArtista = a.idArtista
    JOIN galeria g ON o.idGaleria = g.idGaleria
    ORDER BY o.valoracion DESC
    LIMIT limite;
END; ||
DELIMITER ;

-- Procedimiento 2: Obtener exposiciones activas
DELIMITER ||
CREATE PROCEDURE obtenerExposicionesActivas()
BEGIN
    SELECT e.*, a.nombreArtistico, g.nombre as galeria
    FROM exposicion e
    JOIN artista a ON e.idArtista = a.idArtista
    JOIN galeria g ON e.idGaleria = g.idGaleria
    WHERE DATE_ADD(e.fechaInicio, INTERVAL e.duracionDias DAY) >= CURDATE()
    ORDER BY e.fechaInicio DESC;
END; ||
DELIMITER ;

-- Procedimiento 3: Estadísticas de artista
DELIMITER ||
CREATE PROCEDURE estadisticasArtista(IN f_idArtista INT)
BEGIN
    SELECT 
        a.nombreArtistico,
        COUNT(DISTINCT e.idExposicion) as totalExposiciones,
        COUNT(o.idObra) as totalObras,
        AVG(o.valoracion) as valoracionPromedio,
        SUM(o.dimensiones) as metrosCuadradosTotales
    FROM artista a
    LEFT JOIN exposicion e ON a.idArtista = e.idArtista
    LEFT JOIN obra o ON a.idArtista = o.idArtista
    WHERE a.idArtista = f_idArtista
    GROUP BY a.idArtista;
END; ||
DELIMITER ;

-- Insertar galerías
INSERT INTO galeria (nombre, localizacion, empleados, fechaFundacion, director) VALUES
('Galería Urban Canvas', 'Barcelona, España', 15, '2015-03-20', 'María García'),
('Street Art Gallery', 'Madrid, España', 12, '2018-06-10', 'Carlos Ruiz'),
('Modern Walls', 'Valencia, España', 8, '2020-01-15', 'Ana Martínez');

-- Insertar artistas
INSERT INTO artista (nombreArtistico, nombreReal, edad, pais, fechaPrimeraObra, exposicionActiva) VALUES
('El Niño de las Pinturas', 'Miguel Ángel Santos', 32, 'España', '2010-05-15', true),
('Banksy BCN', 'Anónimo', 35, 'España', '2012-08-20', true),
('ColorVivo', 'Laura Fernández', 28, 'España', '2015-11-03', false),
('StreetDreamer', 'Pablo Jiménez', 40, 'España', '2008-02-28', true);

-- Insertar exposiciones
INSERT INTO exposicion (idArtista, titulo, numeroObras, duracionDias, fechaInicio, idGaleria) VALUES
(1, 'Sueños Urbanos', 12, 90, '2026-01-15', 1),
(2, 'Voces del Asfalto', 8, 60, '2025-12-01', 2),
(3, 'Colores de la Ciudad', 15, 120, '2026-02-01', 1),
(4, 'Retrospectiva 2000-2025', 25, 180, '2025-11-15', 3);

-- Insertar obras
INSERT INTO obra (titulo, idArtista, tecnica, idGaleria, colaboradores, dimensiones, ubicacion, valoracion, idExposicion) VALUES
('El grito silencioso', 1, 'Spray y Stencil', 1, 1, 25.5, 'Calle Mayor 45, Barcelona', 9, 1),
('Libertad encarcelada', 2, 'Mural', 2, 2, 45.0, 'Plaza del Sol, Madrid', 10, 2),
('Niños del futuro', 1, 'Aerosol', 1, 1, 15.0, 'Barrio Gótico, Barcelona', 8, 1),
('Geometría urbana', 3, 'Pintura acrílica', 1, 3, 20.0, 'Rambla Catalunya, Barcelona', 7, 3),
('Retrato de ciudad', 4, 'Mixta', 3, 1, 30.0, 'Gran Vía, Valencia', 9, 4),
('Esperanza', 2, 'Stencil', 2, 1, 12.5, 'Malasaña, Madrid', 10, 2),
('Naturaleza artificial', 3, 'Spray', 1, 2, 18.0, 'Poblenou, Barcelona', 8, 3),
('Tiempo detenido', 4, 'Mural', 3, 1, 50.0, 'Ruzafa, Valencia', 9, 4);

-- Vista: Catálogo completo de obras
CREATE VIEW vista_catalogo_obras AS
SELECT 
    o.idObra,
    o.titulo as tituloObra,
    a.nombreArtistico,
    o.tecnica,
    o.dimensiones,
    o.ubicacion,
    o.valoracion,
    g.nombre as galeria,
    e.titulo as exposicion,
    e.fechaInicio
FROM obra o
JOIN artista a ON o.idArtista = a.idArtista
JOIN galeria g ON o.idGaleria = g.idGaleria
JOIN exposicion e ON o.idExposicion = e.idExposicion;

-- Vista: Artistas con exposiciones activas
CREATE VIEW vista_artistas_activos AS
SELECT 
    a.idArtista,
    a.nombreArtistico,
    a.nombreReal,
    a.pais,
    COUNT(e.idExposicion) as totalExposiciones,
    AVG(o.valoracion) as valoracionPromedio
FROM artista a
JOIN exposicion e ON a.idArtista = e.idArtista
JOIN obra o ON a.idArtista = o.idArtista
WHERE a.exposicionActiva = true
GROUP BY a.idArtista;
