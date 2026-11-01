# GymSystem

Sistema web de gestión de gimnasios desarrollado como proyecto del curso **Desarrollo de Aplicaciones Web y Patrones**.

## 1. Tecnologías y requisitos

- Java 21 y Maven.
- Spring Boot, Spring MVC, Spring Security, Spring Data JPA y Thymeleaf.
- Bootstrap para las vistas.
- MySQL Server y MySQL Workbench.
- NetBeans (el proyecto se ha trabajado en NetBeans 31).
- Git y acceso al repositorio grupal en GitHub.

Las versiones concretas de las dependencias se encuentran en `pom.xml`. No es necesario instalar Maven por separado si NetBeans ya lo proporciona.

## 2. Qué está implementado

| Historia | Funcionalidad disponible |
| --- | --- |
| HU-01 | Gestión de empresas: registro, edición, listado y activación/desactivación. |
| HU-02 | Inicio de sesión, roles y cierre de sesión mediante Spring Security. |
| HU-03 | Registro, consulta, edición, roles y activación/desactivación de usuarios, con controles por gimnasio. |
| HU-05 (parte del integrante 1) | Registro y listado de clientes asociados a cuentas con rol `CLIENTE`. |

**Pendiente de integración:** la asociación efectiva entre clientes y membresías depende del módulo del integrante 2. No se ha implementado todavía el registro/asignación de membresías.

## 3. Descargar y abrir el proyecto

1. Obtener el repositorio desde GitHub y abrir su carpeta en NetBeans como proyecto Maven existente.
2. Comprobar que se está usando JDK 21.
3. Dejar que Maven descargue las dependencias definidas en `pom.xml`.
4. No copiar ni subir credenciales personales o archivos de configuración de otra computadora.

Repositorio grupal: `https://github.com/GaboCordero/GymSystem`.

## 4. Configurar MySQL Workbench

1. Iniciar MySQL Server y conectarse desde MySQL Workbench.
2. Abrir `database/gymsystem.sql` desde **File > Open SQL Script**.
3. Ejecutar el script completo. Este crea `gymsystem`, selecciona la base de datos, crea las tablas y carga los cuatro roles iniciales.
4. Actualizar **Schemas** y verificar que aparezcan las tablas `empresa`, `rol`, `usuario`, `usuario_rol` y `cliente`.

Orden de dependencia de tablas: `empresa` y `rol` → `usuario` → `usuario_rol` y `cliente`.

**Importante:** el script crea la estructura y los roles, **no** las empresas, usuarios ni contraseñas de prueba. `CREATE TABLE IF NOT EXISTS` no actualiza una tabla existente si su estructura cambia; coordinar futuras modificaciones del script con el equipo. La instalación desde una base de datos totalmente nueva aún debe verificarse en la computadora receptora.

## 5. Configuración local de Spring Boot

Crear `src/main/resources/application-local.properties` en la computadora de cada integrante. Este archivo contiene valores locales y **no debe subirse a GitHub**.

Ejemplo (reemplazar los valores con los propios):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gymsystem?useSSL=false&serverTimezone=America/Costa_Rica
spring.datasource.username=TU_USUARIO_MYSQL
spring.datasource.password=TU_PASSWORD_MYSQL
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

En `application.properties` el proyecto utiliza el perfil `local` (`spring.profiles.active=local`). Verificar ese valor antes de iniciar. La configuración existente incluye `spring.jpa.hibernate.ddl-auto=update`; **no usar `create` ni `create-drop`**, porque podrían borrar datos. La estructura compartida debe mantenerse coordinada mediante `database/gymsystem.sql`.

Comprobar que `.gitignore` excluya `application-local.properties`, secretos y archivos generados.

## 6. Crear el primer SUPERADMIN

Una base de datos recién instalada no contiene cuentas. El proyecto incorpora `AdminInicialConfig.java` para crear **una cuenta inicial de manera opcional**.

1. Configurar dos variables de entorno en el sistema donde se inicia NetBeans:
   - `GYMSYSTEM_ADMIN_USERNAME`: nombre de usuario inicial.
   - `GYMSYSTEM_ADMIN_PASSWORD`: contraseña inicial privada y suficientemente robusta.
2. En `application-local.properties`, agregar temporalmente:

   ```properties
   gymsystem.admin.inicializar=true
   ```
3. Reiniciar NetBeans si fue necesario para que reconozca las variables de entorno, iniciar la aplicación y verificar que no haya errores.
4. **Desactivar la inicialización** eliminando esa propiedad o cambiándola a `false`; reiniciar la aplicación.
5. Iniciar sesión con las credenciales que se definieron localmente.

En Windows se pueden configurar las variables de entorno desde **Configuración avanzada del sistema > Variables de entorno**, en las variables de usuario. Cerrar y reabrir NetBeans después de configurarlas. **No introducir estas credenciales en el repositorio, SQL, capturas ni README.**

El inicializador comprueba si ya existe el `username` indicado y no crea otro usuario con el mismo nombre. Si el arranque falla, comprobar primero que se ejecutó la carga de roles del script y que ambas variables están disponibles para el proceso de Java. El correo fijo usado por la cuenta inicial está definido en `AdminInicialConfig.java`; no crear manualmente otra cuenta con ese correo.

## 7. Ejecutar y acceder

1. Abrir el proyecto en NetBeans.
2. Ejecutar **Clean and Build** y después **Run Project**.
3. Abrir `http://localhost:8080/login`.

El puerto predeterminado es **8080**, salvo que se configure otro mediante `server.port`. Si el puerto está ocupado, configurar otro en el perfil local y ajustar las URLs. La aplicación se ejecuta en la computadora de cada integrante, no en un servidor compartido.

### Rutas principales

| Ruta | Acceso / uso |
| --- | --- |
| `/login` | Inicio de sesión. |
| `/` | Redirección después de iniciar sesión según el rol. |
| `/empresa/listado` | SUPERADMIN: empresas. |
| `/usuario/listado` | SUPERADMIN y ADMIN: usuarios (con permisos diferentes). |
| `/usuario/nuevo` | Registro de cuentas según permisos. |
| `/cliente/listado` | ADMIN: clientes de su gimnasio. |
| `/cliente/nuevo` | ADMIN: asociar una cuenta `CLIENTE` a un registro de cliente. |
| `/inicio` | Pantalla provisional para roles sin módulo propio. |
| `/logout` | Cierre de sesión mediante formulario **POST**. |

Hay otras rutas internas para guardar, editar y cambiar estados; los formularios las invocan directamente. Para `ADMIN`, la página inicial debe ser `/usuario/listado`; para `SUPERADMIN`, `/empresa/listado`.

## 8. Usuarios, roles y flujo de prueba

| Rol | Qué puede hacer en lo implementado |
| --- | --- |
| `SUPERADMIN` | Gestionar empresas y cuentas administradoras. |
| `ADMIN` | Gestionar cuentas `ENTRENADOR` y `CLIENTE` de su propio gimnasio, y registrar clientes. |
| `ENTRENADOR` | Puede autenticarse; su módulo específico queda para otra parte del proyecto. |
| `CLIENTE` | Puede autenticarse; su módulo específico queda para otra parte del proyecto. |

**No existen usuarios predefinidos en el SQL.** Los usuarios utilizados durante el desarrollo en otra computadora no se transfieren automáticamente. Cada integrante debe crear sus propias cuentas de prueba:

1. Iniciar sesión como `SUPERADMIN` inicial.
2. Registrar una empresa activa desde `/empresa/listado`.
3. Registrar un usuario `ADMIN` para esa empresa desde `/usuario/nuevo`.
4. Cerrar sesión e ingresar como ese `ADMIN`.
5. Desde `/usuario/nuevo`, registrar una cuenta con rol `CLIENTE`.
6. Desde `/cliente/nuevo`, seleccionar esa cuenta y registrarla como cliente.
7. Verificar el resultado en `/cliente/listado`.

**Atención:** crear una cuenta con rol `CLIENTE` **no** crea automáticamente una fila en `cliente`; por ahora son dos pasos diferentes. No compartir contraseñas reales. Las contraseñas se guardan con BCrypt, no en texto plano.

## 9. Modelo de datos para la integración con membresías

Tablas compartidas hasta este punto:

- `empresa`: gimnasios registrados.
- `rol`: `SUPERADMIN`, `ADMIN`, `ENTRENADOR`, `CLIENTE`.
- `usuario`: datos personales, inicio de sesión, estado y `id_empresa`.
- `usuario_rol`: relación entre cuentas y roles.
- `cliente`: `id_cliente`, `id_usuario` (FK única), `fecha_registro` y `observaciones`.

La tabla `cliente` mantiene una relación **uno a uno** con `usuario` mediante `id_usuario`. El gimnasio de un cliente se consulta desde `cliente → usuario → empresa`. No duplicar nombres, correos ni identificadores de empresa en `cliente`.

**Para el integrante 2:** usar el identificador `cliente.id_cliente` al diseñar la asociación con las membresías; definir en conjunto si habrá historial de membresías, fechas de inicio/fin y estados, según las historias de usuario asignadas. No asumir que cada cliente puede tener una sola membresía histórica. Al consultar o asignar membresías, verificar siempre que el cliente pertenezca a la empresa del administrador autenticado. No modificar los roles, tablas de seguridad ni contraseñas para implementar membresías.

El módulo de membresías y la asociación completa **no están implementados** en este punto.

## 10. Git y entrega entre integrantes

**No comenzar desde una versión antigua de `main`.** Antes de crear la rama del integrante 2, el integrante 1 debe subir sus cambios a `feature/usuarios-clientes` y abrir un **Pull Request (PR)** hacia `main`; el grupo debe revisar y fusionar ese PR. Luego el integrante 2 actualizará `main` y creará su propia rama.

Secuencia sugerida para el integrante 2, después de la fusión:

```bash
git switch main
git pull origin main
git switch -c feature/membresias-rutinas
git push -u origin feature/membresias-rutinas
```

Si no conoce Git Bash, puede realizar estas operaciones mediante la interfaz Git de NetBeans. La rama debe crearse **a partir de `main` actualizado**, no a partir de una rama desactualizada.

Mientras desarrolla:

1. Guardar cambios con **Commit** en su rama, revisando los archivos seleccionados.
2. Usar **Push** para enviarlos a GitHub.
3. Abrir un **Pull Request** desde `feature/membresias-rutinas` hacia `main` al completar su módulo.
4. Avisar al integrante 3 antes de que cree su rama, para que parta de la versión integrada más reciente.
5. No subir `application-local.properties`, claves, contraseñas, carpetas `target/` ni configuraciones personales innecesarias.

No trabajar directamente en `main` y no fusionar cambios sin verificar que el proyecto compile.

## 11. Comprobaciones antes de continuar

- [ ] El proyecto compila con **Clean and Build**.
- [ ] MySQL muestra las cinco tablas y cuatro roles.
- [ ] `application-local.properties` funciona y no está versionado.
- [ ] Se puede crear el primer `SUPERADMIN` en una instalación nueva.
- [ ] SUPERADMIN puede crear empresa y ADMIN.
- [ ] ADMIN puede crear usuarios CLIENTE y asociarlos a `cliente`.
- [ ] ADMIN no accede a las empresas ni a los usuarios de otro gimnasio.
- [ ] El flujo de cierre de sesión funciona.
- [ ] El PR del integrante 1 fue fusionado a `main` antes de crear la siguiente rama.

Si hay diferencias entre el SQL, las entidades y la base de datos local, comunicarlas al equipo antes de cambiar el esquema. Este README documenta el estado de entrega del integrante 1; completar las secciones de membresías cuando el integrante 2 implemente y pruebe su módulo.
