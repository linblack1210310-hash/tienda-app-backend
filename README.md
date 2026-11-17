# TiendaApp - API REST con Spring Boot y JWT

Proyecto de una tienda en línea con autenticación JWT, roles (ADMIN, CLIENTE, VENDEDOR) y gestión de productos y pedidos.

## Tecnologías
- Java 17+
- Spring Boot
- MySQL
- JWT (jjwt)
- Lombok

## Configuración

1. Clona el repositorio.
2. Crea una base de datos en MySQL llamada `tienda_db`.
3. Configura las credenciales en `src/main/resources/application.yaml`.
4. Ejecuta el proyecto con: `mvnw spring-boot:run`

## Credenciales del ADMIN
- **Usuario:** admin
- **Contraseña:** admin123

## Endpoints principales
- `POST /api/v1/tienda/auth/login`
- `POST /api/v1/tienda/auth/register`
- `GET /api/v1/tienda/products` (público)
- `POST /api/v1/tienda/products` (VENDEDOR)
- `POST /api/v1/tienda/orders` (CLIENTE)