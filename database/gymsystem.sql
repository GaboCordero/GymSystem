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

CREATE TABLE IF NOT EXISTS plan_membresia (
    id_plan INT NOT NULL AUTO_INCREMENT,
    id_empresa INT NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255),
    duracion_dias INT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id_plan),
    UNIQUE KEY uk_plan_empresa_nombre (id_empresa, nombre),
    CONSTRAINT fk_plan_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES empresa(id_empresa)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS membresia (
    id_membresia INT NOT NULL AUTO_INCREMENT,
    id_cliente INT NOT NULL,
    id_plan INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_registro DATE NOT NULL,
    PRIMARY KEY (id_membresia),
    CONSTRAINT fk_membresia_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente),
    CONSTRAINT fk_membresia_plan
        FOREIGN KEY (id_plan)
        REFERENCES plan_membresia(id_plan)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pago (
    id_pago INT NOT NULL AUTO_INCREMENT,
    id_membresia INT NOT NULL,
    id_usuario_registro INT NOT NULL,
    fecha_pago DATETIME NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(20) NOT NULL,
    referencia VARCHAR(50),
    PRIMARY KEY (id_pago),
    CONSTRAINT fk_pago_membresia
        FOREIGN KEY (id_membresia)
        REFERENCES membresia(id_membresia),
    CONSTRAINT fk_pago_usuario
        FOREIGN KEY (id_usuario_registro)
        REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ejercicio (
    id_ejercicio INT NOT NULL AUTO_INCREMENT,
    id_empresa INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    grupo_muscular VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id_ejercicio),
    UNIQUE KEY uk_ejercicio_empresa_nombre (id_empresa, nombre),
    CONSTRAINT fk_ejercicio_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES empresa(id_empresa)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS rutina (
    id_rutina INT NOT NULL AUTO_INCREMENT,
    id_empresa INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    objetivo VARCHAR(100),
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id_rutina),
    UNIQUE KEY uk_rutina_empresa_nombre (id_empresa, nombre),
    CONSTRAINT fk_rutina_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES empresa(id_empresa)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS rutina_ejercicio (
    id_rutina_ejercicio INT NOT NULL AUTO_INCREMENT,
    id_rutina INT NOT NULL,
    id_ejercicio INT NOT NULL,
    orden INT NOT NULL,
    series INT NOT NULL,
    repeticiones VARCHAR(20) NOT NULL,
    descanso_segundos INT,
    indicaciones VARCHAR(255),
    PRIMARY KEY (id_rutina_ejercicio),
    UNIQUE KEY uk_rutina_ejercicio (id_rutina, id_ejercicio),
    CONSTRAINT fk_rutina_ejercicio_rutina
        FOREIGN KEY (id_rutina)
        REFERENCES rutina(id_rutina),
    CONSTRAINT fk_rutina_ejercicio_ejercicio
        FOREIGN KEY (id_ejercicio)
        REFERENCES ejercicio(id_ejercicio)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS rutina_cliente (
    id_rutina_cliente INT NOT NULL AUTO_INCREMENT,
    id_rutina INT NOT NULL,
    id_cliente INT NOT NULL,
    id_entrenador INT NOT NULL,
    fecha_asignacion DATE NOT NULL,
    observaciones VARCHAR(255),
    activa BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id_rutina_cliente),
    CONSTRAINT fk_rutina_cliente_rutina
        FOREIGN KEY (id_rutina)
        REFERENCES rutina(id_rutina),
    CONSTRAINT fk_rutina_cliente_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente),
    CONSTRAINT fk_rutina_cliente_entrenador
        FOREIGN KEY (id_entrenador)
        REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS producto (
    id_producto INT NOT NULL AUTO_INCREMENT,
    id_empresa INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL,
    existencias INT NOT NULL DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id_producto),
    CONSTRAINT fk_producto_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES empresa(id_empresa)
) ENGINE=InnoDB;