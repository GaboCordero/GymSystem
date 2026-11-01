-- Base de datos GymSystem

CREATE DATABASE IF NOT EXISTS gymsystem
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE gymsystem;

-- Tabla de empresas
CREATE TABLE IF NOT EXISTS empresa (
    id_empresa INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(25),
    correo VARCHAR(100),
    direccion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id_empresa),
    UNIQUE (nombre)
) ENGINE = InnoDB;

-- Tabla de roles
CREATE TABLE IF NOT EXISTS rol (
    id_rol INT NOT NULL AUTO_INCREMENT,
    rol VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_rol),
    UNIQUE (rol)
) ENGINE = InnoDB;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    id_empresa INT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(75) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    telefono VARCHAR(25),
    activo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id_usuario),
    UNIQUE (username),
    UNIQUE (correo),
    FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa)
) ENGINE = InnoDB;

-- Tabla de relación entre usuarios y roles
CREATE TABLE IF NOT EXISTS usuario_rol (
    id_usuario INT NOT NULL,
    id_rol INT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
) ENGINE = InnoDB;

-- Tabla de clientes
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente INT NOT NULL AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    fecha_registro DATE NOT NULL,
    observaciones VARCHAR(255),
    PRIMARY KEY (id_cliente),
    UNIQUE KEY uk_cliente_usuario (id_usuario),
    CONSTRAINT fk_cliente_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;


-- Inserción de roles
INSERT IGNORE INTO rol (rol) VALUES
('SUPERADMIN'),
('ADMIN'),
('ENTRENADOR'),
('CLIENTE');
