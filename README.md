# OptiFlow Platform API

API REST de OptiFlow, la app de Logix para ópticas: reserva de citas, atención optométrica, ventas con Mercado Pago, seguimiento de lentes, inventario y notificaciones push.

## Tecnologías

Java 21, Spring Boot 3.5, PostgreSQL 16 con Flyway, H2 para local y tests, JWT, Swagger (springdoc) y JUnit 5.

## Bounded contexts

- `searchbooking`: pacientes, personal, ópticas, horarios y citas
- `clinical`: fichas, recetas, cotizaciones, ventas y pagos
- `production`: órdenes de trabajo y entrega
- `inventory`: monturas, stock y proveedores
- `notification`: avisos, push y fidelización

Cada contexto se divide en `domain`, `application`, `infrastructure` e `interfaces`. Lo común está en `shared`.

## Ejecutar en local

```bash
mvn spring-boot:run
```

Usa H2 en memoria (perfil `local`). Para usar PostgreSQL: `docker compose up --build`.

Swagger queda en `http://localhost:8080/swagger-ui.html`. El token se obtiene con `POST /api/v1/auth/tokens` y se pega en **Authorize**.

## Usuarios de prueba

Contraseña `secret12`: `admin@optiflow.test`, `optometrist@optiflow.test`, `advisor@optiflow.test`, `technician@optiflow.test` y `patient@optiflow.test`.

## Variables de entorno

| Variable | Uso |
| :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | `local` (por defecto) o `postgres` |
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | Conexión a PostgreSQL |
| `JWT_SECRET` | Clave para firmar los tokens (mínimo 32 caracteres) |
| `MERCADOPAGO_ACCESS_TOKEN`, `MERCADOPAGO_WEBHOOK_SECRET` | Pagos con Mercado Pago |
| `FIREBASE_CREDENTIALS` | JSON de la cuenta de servicio de Firebase |

## Tests

```bash
mvn test
```

GitHub Actions los corre en cada push y pull request.
