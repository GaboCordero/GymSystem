**Proyecto del curso Desarrollo de Aplicaciones Web y Patrones — Grupo 7**

## Descripción

GymSystem es una aplicación web multiempresa orientada a la administración de gimnasios. Su objetivo es centralizar la gestión de empresas, usuarios y clientes, junto con procesos de membresías, entrenamiento e inventario. El sistema contempla distintos roles de acceso para mantener separada la información de cada gimnasio.

## Tecnologías y requisitos

- **Java 21** y **Spring Boot 4.1.2**, con Maven.
- **Spring MVC, Spring Security, Spring Data JPA y Thymeleaf**.
- **Bootstrap** para las interfaces.
- **MySQL Server** y **MySQL Workbench** para la base de datos.
- **NetBeans** (el equipo ha trabajado con NetBeans 31) u otro IDE compatible con Maven.

Las dependencias utilizadas están declaradas en `pom.xml`.

## Configuración y ejecución local

1. Clonar el repositorio desde [GitHub](https://github.com/GaboCordero/GymSystem), utilizando la rama `main`, y abrir la carpeta que contiene `pom.xml` en NetBeans.
2. Iniciar MySQL Server y ejecutar en MySQL Workbench el script SQL del directorio `database/` para crear la base `gymsystem` y sus tablas. Verificar que se use la versión más reciente del script después de integrar los módulos del equipo.
3. Crear el archivo `src/main/resources/application-local.properties` con la configuración propia de cada computadora:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/gymsystem
   spring.datasource.username=TU_USUARIO_MYSQL
   spring.datasource.password=TU_CONTRASENA_MYSQL
   ```

4. Comprobar que `application.properties` active el perfil `local`. **No subir `application-local.properties` ni contraseñas a GitHub.**
5. En una base de datos nueva, inicializar la cuenta `SUPERADMIN` con `AdminInicialConfig`: configurar las variables de entorno `GYMSYSTEM_ADMIN_USERNAME` y `GYMSYSTEM_ADMIN_PASSWORD`, y habilitar temporalmente `gymsystem.admin.inicializar=true` en `application-local.properties`. Ejecutar la aplicación y luego cambiar esa propiedad a `false`. Si la cuenta ya existe, el inicializador no reemplaza sus credenciales.
6. Ejecutar **Clean and Build** y luego **Run Project**. Acceder a **http://localhost:8080/login** (puerto predeterminado, salvo configuración distinta).

El script SQL crea la estructura necesaria, pero no necesariamente incluye usuarios de prueba ni los datos locales de otros integrantes. Después de iniciar sesión como `SUPERADMIN` se pueden crear gimnasios y administradores para realizar las pruebas correspondientes.

## Historias de usuario y estado del avance

Durante este avance se trabajó por módulos, con desarrollo en ramas individuales e integración mediante Pull Requests a `main`.

| Historias | Trabajo realizado o abordado |
| --- | --- |
| **HU-01** | Registro y gestión de empresas (gimnasios). |
| **HU-02** | Inicio de sesión y control de acceso con Spring Security. |
| **HU-03** | Gestión de usuarios, roles y permisos por gimnasio. |
| **HU-04** | Gestión de planes de membresía. |
| **HU-05** | Registro de clientes y asignación de membresías. |
| **HU-06** | Gestión de pagos de membresía, dentro del módulo administrativo. |
| **HU-12 y HU-13** | Gestión de ejercicios/rutinas y asignación de rutinas a clientes. |
| **HU-18 a HU-20** | Gestión de productos, inventario, proveedores y ventas. |

**Estado del segundo avance:** todas las historias de usuario enumeradas en la tabla fueron implementadas y probadas satisfactoriamente por el equipo. Los módulos de administración, membresías, entrenamiento e inventario se encuentran funcionales y sus cambios fueron integrados en la rama `main` mediante Pull Requests. El proyecto puede continuar ampliándose con las historias previstas para etapas posteriores.
