


-- 1. Deshabilitar revisión de FKs y Eliminar tablas (para re-ejecución segura)
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `Reserva`;
DROP TABLE IF EXISTS `Socio_Alquiler`;
DROP TABLE IF EXISTS `Alquiler`;
DROP TABLE IF EXISTS `Socio`; 
DROP TABLE IF EXISTS `Inscripcion`;
DROP TABLE IF EXISTS `Embarcacion`;
DROP TABLE IF EXISTS `Patron`;

SET FOREIGN_KEY_CHECKS = 1;


-- 2. CREACIÓN DE TABLAS


-- Tabla 1: PATRON (Empleados del club)
CREATE TABLE IF NOT EXISTS `Patron` (
    `dni` VARCHAR(10) COLLATE utf8_unicode_ci NOT NULL,
    `nombre` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
    `apellidos` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
    `fecha_nacimiento` DATE NOT NULL,
    `fecha_expedicion_titulo` DATE NOT NULL,
    
    PRIMARY KEY (`dni`),
    UNIQUE KEY (`dni`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Tabla 2: SOCIO
CREATE TABLE IF NOT EXISTS `Socio` (
    `dni` VARCHAR(10) COLLATE utf8_unicode_ci NOT NULL,
    `nombre` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
    `apellidos` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
    `fecha_nacimiento` DATE NOT NULL,
    `direccion` VARCHAR(255) COLLATE utf8_unicode_ci,
    `fecha_inscripcion` DATE NOT NULL,
    `tiene_titulo_patron` BOOLEAN DEFAULT FALSE,
    `id_inscripcion_fk` INT NULL, 
    
    PRIMARY KEY (`dni`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Tabla 3: INSCRIPCION
CREATE TABLE IF NOT EXISTS `Inscripcion` (
    `id_inscripcion` INT NOT NULL AUTO_INCREMENT, 
    `dni_socio_titular_fk` VARCHAR(10) COLLATE utf8_unicode_ci NOT NULL,
    `cuota` DECIMAL(10, 2) NOT NULL,
    `fecha_creacion` DATE NOT NULL,
    `tipo` ENUM('INDIVIDUAL', 'FAMILIAR') NOT NULL, 
    
    PRIMARY KEY (`id_inscripcion`),
    UNIQUE KEY `UK_Inscripcion_Titular` (`dni_socio_titular_fk`) -- Un socio solo puede ser titular de una inscripción
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Tabla 4: EMBARCACION
CREATE TABLE IF NOT EXISTS `Embarcacion` (
    `matricula` VARCHAR(20) NOT NULL,
    `tipo` VARCHAR(50) COLLATE utf8_unicode_ci NOT NULL,
    `nombre` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
    `plazas` INT NOT NULL,
    `dimensiones` DECIMAL(10, 2),
    `dni_patron_asignado_fk` VARCHAR(10) COLLATE utf8_unicode_ci NULL,
    
    PRIMARY KEY (`matricula`),
    UNIQUE KEY (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Tabla 5: ALQUILER
CREATE TABLE IF NOT EXISTS `Alquiler` (
    `id_alquiler` INT NOT NULL AUTO_INCREMENT,
    `dni_socio_titular_fk` VARCHAR(10) COLLATE utf8_unicode_ci NOT NULL,
    `matricula_embarcacion_fk` VARCHAR(20) NOT NULL,
    `fecha_inicio` DATE NOT NULL,
    `fecha_fin` DATE NOT NULL,
    `precio_total` DECIMAL(10, 2) NOT NULL,
    
    PRIMARY KEY (`id_alquiler`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Tabla 6: RESERVA
CREATE TABLE IF NOT EXISTS `Reserva` (
    `id_reserva` INT NOT NULL AUTO_INCREMENT,
    `dni_socio_solicitante_fk` VARCHAR(10) COLLATE utf8_unicode_ci NOT NULL,
    `matricula_embarcacion_fk` VARCHAR(20) NOT NULL,
    `fecha_actividad` DATE NOT NULL,
    `plazas_solicitadas` INT NOT NULL,
    `descripcion` VARCHAR(255) COLLATE utf8_unicode_ci,
    `precio_total` DECIMAL(10, 2) NOT NULL,
    
    PRIMARY KEY (`id_reserva`),
    -- Una embarcación solo puede tener una reserva por día
    UNIQUE KEY `UK_Reserva_Embarcacion_Fecha` (`matricula_embarcacion_fk`, `fecha_actividad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Tabla 7: SOCIO_ALQUILER (Intermedia N:M)
CREATE TABLE IF NOT EXISTS `Socio_Alquiler` (
    `id_alquiler_fk` INT NOT NULL,
    `dni_socio_fk` VARCHAR(10) COLLATE utf8_unicode_ci NOT NULL,
    
    CONSTRAINT PK_Socio_Alquiler PRIMARY KEY (`id_alquiler_fk`, `dni_socio_fk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


-- 3. DEFINICIÓN DE CLAVES FORÁNEAS (Foreign Keys)


-- En Inscripcion: el titular debe existir en Socio
ALTER TABLE `Inscripcion`
ADD FOREIGN KEY (`dni_socio_titular_fk`) REFERENCES `Socio`(`dni`)
ON DELETE CASCADE ON UPDATE CASCADE; -- Si se borra el socio, se borra su inscripción

-- En Socio: la inscripción (si la tiene) debe existir en Inscripcion
ALTER TABLE `Socio`
ADD FOREIGN KEY (`id_inscripcion_fk`) REFERENCES `Inscripcion`(`id_inscripcion`)
ON DELETE SET NULL ON UPDATE CASCADE; -- Si se borra la inscripción, el socio queda 'suelto'

-- En Embarcacion: el patrón asignado debe existir en Patron
ALTER TABLE `Embarcacion`
ADD FOREIGN KEY (`dni_patron_asignado_fk`) REFERENCES `Patron`(`dni`)
ON DELETE SET NULL ON UPDATE CASCADE; -- Si se borra el patrón, la embarcación se queda sin patrón

-- En Alquiler: el socio titular y la embarcación deben existir
ALTER TABLE `Alquiler`
ADD FOREIGN KEY (`dni_socio_titular_fk`) REFERENCES `Socio`(`dni`)
ON DELETE CASCADE ON UPDATE CASCADE,
ADD FOREIGN KEY (`matricula_embarcacion_fk`) REFERENCES `Embarcacion`(`matricula`)
ON DELETE CASCADE ON UPDATE CASCADE;

-- En Reserva: el socio solicitante y la embarcación deben existir
ALTER TABLE `Reserva`
ADD FOREIGN KEY (`dni_socio_solicitante_fk`) REFERENCES `Socio`(`dni`)
ON DELETE CASCADE ON UPDATE CASCADE,
ADD FOREIGN KEY (`matricula_embarcacion_fk`) REFERENCES `Embarcacion`(`matricula`)
ON DELETE CASCADE ON UPDATE CASCADE;

-- En Socio_Alquiler: el alquiler y el socio deben existir
ALTER TABLE `Socio_Alquiler`
ADD FOREIGN KEY (`id_alquiler_fk`) REFERENCES `Alquiler`(`id_alquiler`)
ON DELETE CASCADE ON UPDATE CASCADE,
ADD FOREIGN KEY (`dni_socio_fk`) REFERENCES `Socio`(`dni`)
ON DELETE CASCADE ON UPDATE CASCADE;