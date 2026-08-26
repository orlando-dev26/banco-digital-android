# Informe del Proyecto — Plataforma de Banca Digital Simulada

**Equipo:** 5 integrantes · **Duración:** 17 semanas (la semana 1 ya concluyó) · **Naturaleza:** proyecto académico

---

## Índice

- [1. Introducción](#1-introducción)
  - [1.1. Contexto general](#11-contexto-general)
  - [1.2. Motivación del proyecto](#12-motivación-del-proyecto)
  - [1.3. Problema y necesidad que se busca resolver](#13-problema-y-necesidad-que-se-busca-resolver)
  - [1.4. Propósito general de la aplicación](#14-propósito-general-de-la-aplicación)
- [2. Decisiones tomadas dentro del sistema](#2-decisiones-tomadas-dentro-del-sistema)
  - [2.1. Representación de valores monetarios](#21-representación-de-valores-monetarios)
  - [2.2. Propiedad exclusiva del saldo](#22-propiedad-exclusiva-del-saldo)
  - [2.3. Transacción ACID local en lugar de saga distribuida](#23-transacción-acid-local-en-lugar-de-saga-distribuida)
  - [2.4. Patrón Transactional Outbox](#24-patrón-transactional-outbox)
  - [2.5. Idempotencia en tres capas](#25-idempotencia-en-tres-capas)
  - [2.6. Bloqueo pesimista en orden determinista](#26-bloqueo-pesimista-en-orden-determinista)
  - [2.7. Fuente de verdad operativa y fuente de verdad contable](#27-fuente-de-verdad-operativa-y-fuente-de-verdad-contable)
  - [2.8. Partida doble con cuentas de sistema](#28-partida-doble-con-cuentas-de-sistema)
  - [2.9. Inmutabilidad del libro contable forzada por permisos](#29-inmutabilidad-del-libro-contable-forzada-por-permisos)
  - [2.10. Evaluación de fraude síncrona con degradación](#210-evaluación-de-fraude-síncrona-con-degradación)
  - [2.11. Auth0 como proveedor de identidad y paso de token entre servicios](#211-auth0-como-proveedor-de-identidad-y-paso-de-token-entre-servicios)
  - [2.12. Validación del token en cada microservicio](#212-validación-del-token-en-cada-microservicio)
  - [2.13. Verificación de propiedad del recurso](#213-verificación-de-propiedad-del-recurso)
  - [2.14. Biometría como desbloqueo local](#214-biometría-como-desbloqueo-local)
  - [2.15. REST para decidir, Kafka para informar](#215-rest-para-decidir-kafka-para-informar)
  - [2.16. Topics organizados por dominio](#216-topics-organizados-por-dominio)
  - [2.17. Contratos de eventos sin módulo compartido](#217-contratos-de-eventos-sin-módulo-compartido)
  - [2.18. DNS de Docker en lugar de Eureka](#218-dns-de-docker-en-lugar-de-eureka)
  - [2.19. Una instancia PostgreSQL con base y rol por servicio](#219-una-instancia-postgresql-con-base-y-rol-por-servicio)
  - [2.20. Redis compartido con prefijos y ACL](#220-redis-compartido-con-prefijos-y-acl)
  - [2.21. Convenciones de modelado de datos](#221-convenciones-de-modelado-de-datos)
  - [2.22. Monorepo con desarrollo basado en tronco](#222-monorepo-con-desarrollo-basado-en-tronco)
  - [2.23. Vinculación de dispositivo y cambio verificado](#223-vinculación-de-dispositivo-y-cambio-verificado)
  - [2.24. Identificación del cliente y numeración de productos](#224-identificación-del-cliente-y-numeración-de-productos)
  - [2.25. Decisiones aún pendientes](#225-decisiones-aún-pendientes)
- [3. Arquitectura de microservicios](#3-arquitectura-de-microservicios)
  - [3.1. Visión general](#31-visión-general)
  - [3.2. API Gateway](#32-api-gateway)
  - [3.3. Identity Service](#33-identity-service)
  - [3.4. Account Service](#34-account-service)
  - [3.5. Transaction Service](#35-transaction-service)
  - [3.6. Ledger Service](#36-ledger-service)
  - [3.7. Fraud Service](#37-fraud-service)
  - [3.8. Notification Service](#38-notification-service)
  - [3.9. Matriz de comunicación](#39-matriz-de-comunicación)
- [4. Base de datos y funcionalidades de cada microservicio](#4-base-de-datos-y-funcionalidades-de-cada-microservicio)
  - [4.1. API Gateway](#41-api-gateway)
  - [4.2. Identity Service](#42-identity-service)
  - [4.3. Account Service](#43-account-service)
  - [4.4. Transaction Service](#44-transaction-service)
  - [4.5. Ledger Service](#45-ledger-service)
  - [4.6. Fraud Service](#46-fraud-service)
  - [4.7. Notification Service](#47-notification-service)
- [5. Sprints](#5-sprints)
  - [5.1. Marco temporal](#51-marco-temporal)
  - [5.2. Sprint 1 — Fundaciones y seguridad extremo a extremo](#52-sprint-1--fundaciones-y-seguridad-extremo-a-extremo)
  - [5.3. Sprint 2 — Identidad y cuentas](#53-sprint-2--identidad-y-cuentas)
  - [5.4. Sprint 3 — Transferencia funcional](#54-sprint-3--transferencia-funcional)
  - [5.5. Sprint 4 — Eventos y contabilidad](#55-sprint-4--eventos-y-contabilidad)
  - [5.6. Sprint 5 — Notificaciones y administración](#56-sprint-5--notificaciones-y-administración)
  - [5.7. Sprint 6 — Detección de fraude](#57-sprint-6--detección-de-fraude)
  - [5.8. Sprint 7 — Endurecimiento y consistencia](#58-sprint-7--endurecimiento-y-consistencia)
  - [5.9. Sprint 8 — Cierre y entrega](#59-sprint-8--cierre-y-entrega)
  - [5.10. Dependencias y puntos de control](#510-dependencias-y-puntos-de-control)
- [6. Backlog](#6-backlog)
  - [6.1. Épicas y convenciones](#61-épicas-y-convenciones)
  - [6.2. EP-01 Infraestructura y plataforma](#62-ep-01-infraestructura-y-plataforma)
  - [6.3. EP-02 Identidad y autenticación](#63-ep-02-identidad-y-autenticación)
  - [6.4. EP-03 Cuentas](#64-ep-03-cuentas)
  - [6.5. EP-04 Transferencias](#65-ep-04-transferencias)
  - [6.6. EP-05 Ledger](#66-ep-05-ledger)
  - [6.7. EP-06 Fraude](#67-ep-06-fraude)
  - [6.8. EP-07 Notificaciones](#68-ep-07-notificaciones)
  - [6.9. EP-08 Administración](#69-ep-08-administración)
  - [6.10. EP-09 Aplicación móvil](#610-ep-09-aplicación-móvil)
  - [6.11. EP-10 Calidad, seguridad y observabilidad](#611-ep-10-calidad-seguridad-y-observabilidad)
- [7. Parte técnica](#7-parte-técnica)
  - [7.1. Microservicios](#71-microservicios)
  - [7.2. Aplicación móvil](#72-aplicación-móvil)
  - [7.3. Panel de administración](#73-panel-de-administración)
  - [7.4. Identidad y seguridad](#74-identidad-y-seguridad)
  - [7.5. Persistencia](#75-persistencia)
  - [7.6. Mensajería](#76-mensajería)
  - [7.7. Almacenamiento auxiliar](#77-almacenamiento-auxiliar)
  - [7.8. Comunicación entre componentes](#78-comunicación-entre-componentes)
  - [7.9. Infraestructura y ejecución](#79-infraestructura-y-ejecución)
  - [7.10. Calidad y observabilidad](#710-calidad-y-observabilidad)
  - [7.11. Resumen de tecnologías por componente](#711-resumen-de-tecnologías-por-componente)
- [8. Diccionario de términos](#8-diccionario-de-términos)
  - [8.1. Términos de negocio bancario](#81-términos-de-negocio-bancario)
  - [8.2. Términos de arquitectura y sistemas distribuidos](#82-términos-de-arquitectura-y-sistemas-distribuidos)
  - [8.3. Términos de seguridad e identidad](#83-términos-de-seguridad-e-identidad)
  - [8.4. Términos de mensajería y Kafka](#84-términos-de-mensajería-y-kafka)
  - [8.5. Términos de base de datos](#85-términos-de-base-de-datos)
  - [8.6. Términos de desarrollo y calidad](#86-términos-de-desarrollo-y-calidad)

---

# 1. Introducción

## 1.1. Contexto general

El proyecto consiste en el diseño y construcción de una **plataforma de banca digital simulada**, compuesta por un backend distribuido en siete microservicios, una aplicación móvil Android nativa y un panel de administración web.

El sistema permite que una persona se registre, cree cuentas bancarias, consulte su saldo, transfiera dinero a otro usuario, revise su historial de operaciones y reciba notificaciones de todo lo que ocurre en su cuenta. Toda operación queda registrada en un libro contable y es evaluada previamente por un motor de detección de fraude.

Se trata de un proyecto **académico y simulado**: no se maneja dinero real, no existe integración con entidades financieras ni con pasarelas de pago, y todo el dinero del sistema se origina en saldos iniciales o en acreditaciones realizadas por un administrador.

El desarrollo lo lleva a cabo un equipo de cinco integrantes en un plazo de diecisiete semanas.

## 1.2. Motivación del proyecto

El dominio bancario se eligió deliberadamente porque **obliga a resolver bien problemas que en otros dominios pueden evitarse o disimularse**. En una aplicación de notas o un catálogo de productos, un registro duplicado es una molestia; en una transferencia, un registro duplicado significa dinero creado de la nada.

Esa exigencia hace que el proyecto sirva como vehículo para trabajar de forma realista una serie de temas que forman parte del núcleo de la ingeniería de software moderna:

- Arquitectura de microservicios con propiedad estricta de los datos.
- Autenticación y autorización basadas en estándares abiertos, en dos niveles distintos.
- Comunicación síncrona y asíncrona, y el criterio para elegir entre ambas.
- Garantías de consistencia cuando no existe una transacción global.
- Idempotencia y tolerancia a fallos parciales.
- Arquitectura dirigida por eventos.
- Contabilidad de partida doble.
- Detección de fraude mediante reglas.

La motivación adicional es de tipo formativo: llegar a un sistema donde **la corrección no dependa de que nadie se equivoque**, sino de restricciones verificables en la base de datos, pruebas automatizadas y procesos de reconciliación que demuestran que el sistema cuadra.

## 1.3. Problema y necesidad que se busca resolver

El problema técnico central que aborda el proyecto es el siguiente:

> **¿Cómo se garantiza que una transferencia de dinero se ejecute exactamente una vez, en un sistema distribuido donde la red falla, los procesos mueren a mitad de una operación y los mensajes pueden entregarse por duplicado?**

De este problema se derivan las necesidades concretas que el sistema debe resolver:

| Necesidad | Manifestación concreta |
|---|---|
| Evitar la duplicación de operaciones | El usuario pulsa dos veces, o la respuesta se pierde y la aplicación reintenta |
| Evitar la pérdida de registros contables | El proceso muere entre confirmar la transferencia y publicar el evento |
| Garantizar la corrección bajo concurrencia | Dos transferencias simultáneas desde la misma cuenta, o transferencias cruzadas entre dos cuentas |
| Impedir el acceso a recursos ajenos | Un usuario autenticado modifica un identificador y opera sobre la cuenta de otro |
| Impedir la suplantación entre servicios | Alguien alcanza la red interna y llama directamente a un microservicio |
| Detectar operaciones sospechosas antes de ejecutarlas | Una evaluación que llega después del movimiento no previene nada |
| Verificar que la contabilidad cuadra | Una divergencia silenciosa entre saldos y asientos es indetectable sin reconciliación |

## 1.4. Propósito general de la aplicación

El propósito de la aplicación es **permitir que un usuario autenticado transfiera dinero a otro usuario de forma segura, con garantía de ejecución única, con registro contable completo y con evaluación previa de riesgo**, ofreciendo además las funcionalidades de soporte necesarias para que esa operación tenga sentido: identidad, cuentas, historial, notificaciones y administración.

El sistema se construye siguiendo un criterio de prioridad estricto:

> **MVP funcional → seguridad → consistencia → pruebas → mejoras → funcionalidades opcionales.**

No se inicia ninguna funcionalidad avanzada antes de que exista una transferencia completa, segura, idempotente y correctamente asentada en el libro contable.

---

# 2. Decisiones tomadas dentro del sistema

Cada decisión se documenta indicando qué se decidió, cómo se implementará, por qué se tomó, en qué parte del sistema aplica y qué relación tiene con el desarrollo de las funcionalidades.

## 2.1. Representación de valores monetarios

**Qué se decidió.** Todo valor monetario se representa con `BigDecimal` en Java y `NUMERIC(19,4)` en PostgreSQL. Queda prohibido el uso de `double`, `float` o `REAL` en cualquier capa, incluidos los DTO de entrada y salida y los payloads de los eventos.

**Cómo se implementará.** Las columnas de importe se declaran como `NUMERIC(19,4)` en las migraciones de Flyway. Los atributos de las entidades JPA se declaran como `BigDecimal`. En los contratos JSON los importes viajan como cadena decimal, no como número de coma flotante. Las comparaciones se realizan con `compareTo`, nunca con `equals`, porque `equals` en `BigDecimal` distingue la escala y considera `10.0` distinto de `10.00`.

**Por qué se tomó.** Los tipos de coma flotante no pueden representar exactamente valores decimales. La suma de `0.1` y `0.2` no produce `0.3`. En un sistema financiero, esos errores se acumulan operación tras operación y terminan descuadrando el libro contable de forma irreparable. Es un fallo trivialmente detectable y de los que más credibilidad restan a un proyecto.

**Dónde aplica.** Account Service (saldo y movimientos), Transaction Service (importe de la transferencia), Ledger Service (líneas de asiento), Fraud Service (umbrales y acumulados), y los payloads de todos los eventos que transportan importes.

**Relación con las funcionalidades.** Afecta directamente a la consulta de saldo, a la transferencia, al cálculo de acumulados en las reglas de velocidad y a la reconciliación contable: si los tipos no son exactos, la reconciliación detectaría discrepancias permanentes de céntimos que no corresponden a ningún error real.

## 2.2. Propiedad exclusiva del saldo

**Qué se decidió.** Únicamente **Account Service** puede leer y modificar saldos. Ningún otro microservicio accede a la información financiera de las cuentas ni la altera.

**Cómo se implementará.** Account Service es el propietario exclusivo de la base de datos `account_db`. Expone un endpoint interno `POST /internal/transfers`, no enrutado por el Gateway y protegido por rol, que ejecuta el movimiento. Transaction Service invoca ese endpoint, pero no dispone de ninguna vía para modificar un saldo por su cuenta. El rol de base de datos `transaction_user` no tiene permiso alguno sobre `account_db`.

**Por qué se tomó.** En cuanto un segundo componente puede escribir dinero, la corrección del sistema deja de ser demostrable: cualquier descuadre exige investigar dos rutas de escritura, y las garantías de concurrencia se multiplican. Concentrar la escritura en un único punto permite que las restricciones de la base de datos y el bloqueo pesimista sean suficientes.

**Dónde aplica.** Account Service y Transaction Service, y la frontera entre ambos.

**Relación con las funcionalidades.** Es la base de las funcionalidades de transferencia, consulta de saldo, historial de movimientos y acreditación administrativa.

## 2.3. Transacción ACID local en lugar de saga distribuida

**Qué se decidió.** El débito de la cuenta origen y el crédito de la cuenta destino se ejecutan en **una única transacción ACID** dentro de Account Service. No se implementa una saga con compensaciones.

**Cómo se implementará.** El endpoint `POST /internal/transfers` abre una transacción, bloquea ambas cuentas, valida estado y fondos, inserta los dos movimientos, actualiza los dos saldos y confirma. Si algo falla, la transacción se revierte por completo y no queda ningún efecto parcial.

**Por qué se tomó.** Ambas cuentas viven en el mismo microservicio y en la misma base de datos. Una saga distribuida resuelve el problema de operar sobre recursos que no comparten transacción; aquí ese problema no existe. Introducirla añadiría estados intermedios, compensaciones, reversiones y varias semanas de desarrollo sin ganancia alguna en corrección.

**Dónde aplica.** Account Service.

**Relación con las funcionalidades.** Simplifica radicalmente la funcionalidad de transferencia: no existen estados de "débito realizado, crédito pendiente", no hay que revertir movimientos y no hace falta un proceso que repare operaciones a medias.

**Nota.** Si en el futuro las cuentas se repartieran entre bases de datos distintas, la decisión debería revisarse. Queda documentada como decisión consciente, no como desconocimiento del patrón saga.

## 2.4. Patrón Transactional Outbox

**Qué se decidió.** Ningún servicio publica directamente en Kafka desde el código de negocio. Los eventos se escriben en una tabla `outbox_event` dentro de la misma transacción que produce el cambio de estado, y un proceso programado los publica después.

**Cómo se implementará.** La transacción que marca una transferencia como completada inserta también la fila del evento. Un método anotado con `@Scheduled` se ejecuta cada uno o dos segundos, selecciona las filas pendientes con `FOR UPDATE SKIP LOCKED`, publica en Kafka y marca la fila como publicada. El identificador de la fila del outbox se reutiliza como identificador del evento.

**Por qué se tomó.** Confirmar en PostgreSQL y publicar en Kafka son dos operaciones que no pueden hacerse de forma atómica. Si el proceso muere entre ambas, la transferencia existe pero el evento nunca se emite: el Ledger jamás registra el asiento y el dinero se mueve sin rastro contable. Con el outbox, si Kafka está caído los eventos se acumulan y se publican al recuperarse. No se pierde ninguno.

**Dónde aplica.** Transaction Service, Account Service e Identity Service, es decir, todos los productores de eventos.

**Relación con las funcionalidades.** Es lo que permite responder a la pregunta "¿qué ocurre si Kafka falla durante una transferencia?": la transferencia se completa igual, el usuario recibe su respuesta, y la contabilidad y las notificaciones llegan cuando el servicio de mensajería vuelve.

## 2.5. Idempotencia en tres capas

**Qué se decidió.** La protección contra la duplicación de operaciones se implementa en tres niveles independientes, de modo que si uno falla el siguiente sigue cubriendo.

| Capa | Mecanismo | Dónde |
|---|---|---|
| 1 | Cabecera `Idempotency-Key` generada por el cliente | Transaction Service |
| 2 | `transaction_id` con restricción única sobre los movimientos | Account Service |
| 3 | `event_id` registrado en una tabla de eventos procesados | Ledger, Notification y Fraud |

**Cómo se implementará.** La aplicación móvil genera un identificador único antes de enviar la petición y lo transmite en la cabecera. Transaction Service reserva esa clave en Redis para responder rápido y la persiste en PostgreSQL con restricción única, guardando la respuesta original para poder devolverla idéntica ante un reintento. Account Service impone `UNIQUE (operation_id, account_id, direction)` sobre la tabla de movimientos. Cada consumidor de Kafka inserta el identificador del evento en su tabla `processed_event` dentro de la misma transacción en que aplica el efecto.

**Por qué se tomó.** El escenario es cotidiano: el usuario pulsa "Transferir", la operación se ejecuta correctamente y la respuesta se pierde por un corte de red. La aplicación muestra error y el usuario vuelve a pulsar. Sin protección, se transfiere dos veces. Y como la entrega de Kafka es *at-least-once*, los eventos duplicados no son una anomalía sino el comportamiento esperado.

**Dónde aplica.** Toda la ruta de la transferencia y todos los consumidores de eventos.

**Relación con las funcionalidades.** Es un requisito directo de la funcionalidad de transferencia, y de la escritura de asientos contables y del envío de notificaciones, que no deben duplicarse ante un reproceso.

## 2.6. Bloqueo pesimista en orden determinista

**Qué se decidió.** Al ejecutar una transferencia, Account Service bloquea ambas cuentas con `SELECT ... FOR UPDATE`, adquiriendo los bloqueos siempre en el mismo orden, determinado por el identificador de cuenta y no por cuál es origen y cuál destino.

**Cómo se implementará.** Antes de bloquear, se ordenan los dos identificadores de cuenta de forma ascendente y se adquieren los bloqueos en esa secuencia. La validación autoritativa de fondos y estado se realiza **después** de tener los bloqueos, no antes.

**Por qué se tomó.** Sin orden determinista, una transferencia de A hacia B que bloquea primero A, ejecutándose simultáneamente con una de B hacia A que bloquea primero B, produce un interbloqueo. PostgreSQL lo detecta y aborta una de las dos transacciones, pero es un fallo evitable con una línea de código. Además, la validación de fondos solo es fiable si se realiza bajo el bloqueo: comprobarla antes deja una ventana en la que otra operación puede consumir el saldo.

**Dónde aplica.** Account Service, en la ejecución del movimiento.

**Relación con las funcionalidades.** Garantiza que la funcionalidad de transferencia sea correcta bajo carga concurrente. Se valida con una prueba automatizada que lanza cien transferencias cruzadas simultáneas.

## 2.7. Fuente de verdad operativa y fuente de verdad contable

**Qué se decidió.** Account Service mantiene el saldo materializado y es la **fuente de verdad operativa**: es quien autoriza si hay fondos y quien responde al usuario. Ledger Service es la **fuente de verdad contable**: registro histórico inmutable. La coherencia entre ambos se verifica mediante un proceso de reconciliación.

**Cómo se implementará.** Account Service actualiza el saldo dentro de la transacción del movimiento y nunca consulta al Ledger. Ledger Service construye los asientos consumiendo eventos. Un proceso programado y un endpoint bajo demanda comparan, cuenta por cuenta, el saldo reportado por Account con la suma de las líneas del Ledger, y registran toda discrepancia como incidente.

**Por qué se tomó.** El modelo puro de contabilidad, en el que el saldo es una proyección calculada desde los asientos, es más elegante y es como funciona la banca real, pero obliga a poner el Ledger en el camino crítico o a aceptar latencia en el saldo. Para el plazo disponible, el modelo elegido conserva la simplicidad sin renunciar al rigor, siempre que la coherencia se **verifique** en lugar de asumirse.

**Dónde aplica.** Account Service y Ledger Service.

**Relación con las funcionalidades.** Determina que la consulta de saldo sea inmediata y que el registro contable sea eventualmente consistente. Añade la funcionalidad de reconciliación, disponible en el panel de administración.

## 2.8. Partida doble con cuentas de sistema

**Qué se decidió.** El libro contable emplea partida doble, e incorpora cuentas de sistema para las operaciones cuyo dinero no procede de otra cuenta de usuario.

**Cómo se implementará.** Cada movimiento financiero genera un asiento con al menos dos líneas, una al debe y otra al haber, cuya suma debe coincidir. Se precargan dos cuentas de sistema: `SYSTEM_CASH`, contrapartida de los saldos iniciales y de las acreditaciones administrativas, y `SYSTEM_SUSPENSE`, contrapartida de las correcciones.

**Por qué se tomó.** Sin cuentas de sistema, una acreditación de saldo no tendría contrapartida y rompería la invariante de la partida doble, porque el dinero entra desde fuera del sistema. La decisión fue consecuencia directa de habilitar la funcionalidad de acreditación administrativa.

**Dónde aplica.** Ledger Service.

**Relación con las funcionalidades.** Permite que las funcionalidades de saldo inicial y acreditación administrativa convivan con un libro contable formalmente correcto. Además, el modelo admite N líneas por asiento, de modo que incorporar comisiones más adelante no exigiría ningún cambio estructural.

## 2.9. Inmutabilidad del libro contable forzada por permisos

**Qué se decidió.** Los asientos contables no pueden modificarse ni eliminarse. La restricción se impone a nivel de base de datos, no por convención en el código.

**Cómo se implementará.** El rol `ledger_user` de PostgreSQL recibe únicamente permisos `SELECT` e `INSERT` sobre las tablas de asientos y líneas. No se le concede `UPDATE` ni `DELETE`. El repositorio de la aplicación no expone métodos de modificación. Una corrección se registra siempre como contra-asiento que referencia al original.

**Por qué se tomó.** Una nota en el documento que dice "los asientos son inmutables" no impide nada. Si el permiso no existe, la base de datos rechaza la operación aunque el código contenga un error o alguien añada un método de actualización por descuido.

**Dónde aplica.** Ledger Service.

**Relación con las funcionalidades.** Es lo que da valor a la funcionalidad de auditoría contable: un historial que puede reescribirse no sirve como evidencia.

## 2.10. Evaluación de fraude síncrona con degradación

**Qué se decidió.** Fraud Service se invoca de forma **síncrona antes de mover el dinero**, con un tiempo límite estricto y una política de degradación explícita. Adicionalmente consume eventos de forma asíncrona para mantener su historial.

**Cómo se implementará.** Transaction Service llama a `POST /internal/fraud/evaluations` con un tiempo límite de 500 ms. La respuesta contiene puntuación, nivel de riesgo y decisión. Si la decisión es aprobar, el flujo continúa; si es revisar, la transferencia queda retenida; si es rechazar, se marca como fallida. Si el servicio no responde a tiempo, se aplica la política de degradación: por debajo del umbral de importe se aprueba, por encima se retiene.

**Por qué se tomó.** Una evaluación asíncrona no puede impedir nada: cuando el veredicto llega por Kafka, el dinero ya se movió. Solo quedaría revertir o alertar, y ninguna de las dos cosas es prevención. Si el requisito es poder bloquear una operación, la evaluación debe ocurrir antes de ejecutarla.

**Dónde aplica.** Transaction Service y Fraud Service.

**Relación con las funcionalidades.** Habilita la funcionalidad de retención de operaciones sospechosas y la cola de revisión del panel de administración. La política de degradación es lo que impide que una caída de Fraud Service deje el sistema inoperativo.

## 2.11. Auth0 como proveedor de identidad y paso de token entre servicios

**Qué se decidió.** La autenticación de la persona se delega en **Auth0**, que emite y firma los tokens. La comunicación entre microservicios **no** utiliza un flujo de autenticación propio: se reenvía el mismo token del usuario que originó la petición (paso de token).

| | Autenticación de usuario | Comunicación entre servicios |
|---|---|---|
| Mecanismo | Auth0 con validación de PIN delegada al Identity Service | Reenvío del token del usuario |
| Sujeto | Persona física | La misma persona física |
| Autorización | Rol y propiedad del recurso | Rol y propiedad del recurso |
| Rutas | `/api/v1/**` | `/internal/**` |

**Cómo se implementará.** La aplicación móvil envía las credenciales del usuario (identificador, PIN y datos del dispositivo) hacia Auth0, que resuelve la autenticación consultando al Identity Service mediante una **conexión de base de datos personalizada**: Auth0 no valida el PIN por sí mismo, sino que invoca un endpoint del Identity Service que comprueba el `pin_hash` almacenado y el dispositivo vinculado. Superada la validación, Auth0 emite el token de acceso y el token de renovación. El rol viaja como afirmación personalizada inyectada mediante una acción de Auth0. Cuando un microservicio llama a otro, adjunta el token que recibió, sin solicitar uno propio.

**Por qué se tomó.** Delegar la emisión y la firma de tokens en un proveedor gestionado evita construir y custodiar el material criptográfico dentro del proyecto. El paso de token elimina un segundo mecanismo de autenticación completo: como toda operación interna se origina en una petición de un usuario concreto, el sujeto es el mismo en toda la cadena y la identidad no se pierde entre saltos.

**Consecuencia asumida.** Auth0 es un servicio en la nube y el resto de la plataforma se ejecuta localmente mediante Docker Compose. Para que la conexión personalizada pueda alcanzar el Identity Service durante el desarrollo es necesario exponer ese endpoint mediante un túnel. Es una dependencia externa que el diseño anterior no tenía, y queda registrada como riesgo en 2.25.

**Dónde aplica.** Todos los microservicios, la aplicación móvil y la configuración del inquilino de Auth0.

**Relación con las funcionalidades.** Condiciona la implementación de cada endpoint: define si se protege por rol o requiere comprobación adicional de propiedad del recurso.

## 2.12. Validación del token en cada microservicio

**Qué se decidió.** Cada microservicio valida el token de forma independiente. El API Gateway también lo valida, pero los servicios no confían en esa validación.

**Cómo se implementará.** Cada servicio se configura como Resource Server de Spring Security, apuntando al conjunto de claves públicas que publica Auth0 en su punto de descubrimiento. Verifica firma, emisor, expiración y audiencia. El proyecto no gestiona claves de firma propias: la rotación es responsabilidad del proveedor y los servicios obtienen la clave vigente a partir del identificador de clave que viaja en la cabecera del token.

**Por qué se tomó.** Si el Gateway fuese el único punto de validación, alcanzar la red interna equivaldría a tener acceso total al sistema. La validación en el Gateway existe para rechazar cuanto antes las peticiones inválidas, no para eximir a los servicios de comprobar.

**Dónde aplica.** Los siete componentes.

**Relación con las funcionalidades.** Es transversal a todas las funcionalidades expuestas por API.

## 2.13. Verificación de propiedad del recurso

**Qué se decidió.** Antes de cualquier operación sobre una cuenta, se verifica que esa cuenta pertenece al usuario identificado en el token. El identificador de usuario nunca se toma del cuerpo de la petición.

**Cómo se implementará.** El identificador se extrae siempre del campo `sub` del token validado. Transaction Service solicita a Account Service la validación de la transferencia incluyendo ese identificador, y Account Service comprueba la titularidad de la cuenta origen antes de responder. Existe una prueba automatizada específica que verifica que un usuario no puede operar sobre una cuenta ajena.

**Por qué se tomó.** Es la vulnerabilidad más explotada en interfaces de programación financieras: un usuario legítimamente autenticado cambia un identificador en el cuerpo de la petición y opera sobre recursos de otro. Sin esta comprobación, toda la arquitectura de seguridad resulta decorativa.

**Dónde aplica.** Account Service, Transaction Service, Notification Service y todo endpoint que devuelva recursos de un usuario.

**Relación con las funcionalidades.** Afecta a consulta de cuentas, consulta de saldo, historial de movimientos, transferencia, historial de transferencias y bandeja de notificaciones.

## 2.14. Biometría como desbloqueo local

**Qué se decidió.** La autenticación biométrica se emplea exclusivamente como mecanismo de desbloqueo local en el dispositivo. **El backend no recibe, procesa ni almacena ningún dato biométrico.**

**Cómo se implementará.** Tras el primer inicio de sesión con PIN, la aplicación ofrece activar el acceso biométrico. El token de renovación se cifra con una clave generada en el Android Keystore, configurada para exigir autenticación del usuario y para invalidarse si se registra una nueva huella o rostro en el dispositivo. En cada apertura, la API `BiometricPrompt` desbloquea la clave, la aplicación descifra el token de renovación y lo canjea en Auth0 por un token de acceso nuevo, sin necesidad de reintroducir el PIN.

**Por qué se tomó.** El dato biométrico es información sensible de categoría especial. Enviarlo al servidor sería innecesario, jurídicamente delicado y técnicamente inferior: el modelo estándar de la industria consiste en que el sistema operativo custodie la plantilla biométrica y la aplicación solo obtenga acceso a material criptográfico local.

**Nota terminológica.** «Face ID» es la denominación comercial de Apple. En Android, que es la plataforma elegida, el mecanismo equivalente es `BiometricPrompt` combinado con el Android Keystore, y admite rostro o huella según el hardware del dispositivo.

**Dónde aplica.** Aplicación móvil e Identity Service.

**Relación con las funcionalidades.** Sostiene la funcionalidad de acceso biométrico. La biometría desbloquea el dispositivo, mientras que el PIN es la credencial que se valida en el servidor: son mecanismos complementarios y no intercambiables.

## 2.15. REST para decidir, Kafka para informar

**Qué se decidió.** Se utiliza REST cuando se necesita una respuesta para poder continuar, y Kafka cuando algo ya ocurrió y otros componentes pueden reaccionar después.

**Cómo se implementará.** Las validaciones de cuenta, la evaluación de riesgo y la ejecución del movimiento se realizan por REST, de forma síncrona. El registro contable, el envío de notificaciones y la actualización del historial de fraude se realizan por Kafka, de forma asíncrona.

**Por qué se tomó.** Kafka no puede utilizarse para validar un saldo: cuando llegara la respuesta, la decisión ya estaría tomada. A la inversa, obligar a que una transferencia espere a que se envíe una notificación acopla el resultado de una operación financiera a un componente accesorio.

**Dónde aplica.** Todas las comunicaciones entre servicios.

**Relación con las funcionalidades.** Define qué pasos son visibles para el usuario y cuáles ocurren después de que reciba su respuesta.

## 2.16. Topics organizados por dominio

**Qué se decidió.** Se emplean cuatro topics de dominio: `transaction-events`, `account-events`, `fraud-events` e `identity-events`, más un topic de mensajes fallidos por cada uno.

**Cómo se implementará.** Cada topic pertenece a un único productor. Notification Service se suscribe a los cuatro y decide por sí mismo qué merece notificación. La clave de partición de los eventos de transacción es el identificador de la transferencia, lo que garantiza que los eventos de una misma operación se procesen en orden.

**Por qué se tomó.** Un topic nombrado por su consumidor acopla la topología de mensajería a ese consumidor y anula la razón de ser de Kafka. Con topics de dominio, añadir un nuevo tipo de notificación no obliga a modificar ningún productor.

**Dónde aplica.** Todos los productores y consumidores de eventos.

**Relación con las funcionalidades.** Determina cómo se alimentan las funcionalidades de contabilidad, notificación e historial de fraude.

## 2.17. Contratos de eventos sin módulo compartido

**Qué se decidió.** No existe un módulo de código compartido con las clases de los eventos. Cada servicio define sus propios objetos de transferencia de datos.

**Cómo se implementará.** Se mantiene una carpeta `contracts/` en el repositorio con un ejemplo en JSON, versionado, por cada tipo de evento. Es documentación, no una dependencia compilada. Los consumidores se configuran para ignorar propiedades desconocidas, de modo que un productor puede añadir campos sin romper a nadie. Cada consumidor incorpora una prueba que deserializa el ejemplo correspondiente; si un productor rompe el contrato, esa prueba falla en la integración continua.

**Por qué se tomó.** Un módulo compartido termina acumulando lógica y convierte el conjunto en un monolito distribuido con pasos adicionales. El riesgo asumido es la deriva de contratos, que se mitiga con las tres medidas anteriores.

**Dónde aplica.** Todos los servicios que producen o consumen eventos.

**Relación con las funcionalidades.** Permite que los equipos avancen de forma independiente sin coordinar versiones de una biblioteca común.

## 2.18. DNS de Docker en lugar de Eureka

**Qué se decidió.** El descubrimiento de servicios se resuelve con el DNS interno de Docker. No se implementa Eureka.

**Cómo se implementará.** Cada servicio se dirige a otro por su nombre de contenedor, tomado de una variable de entorno. Se configuran comprobaciones de salud en Docker Compose y se utiliza Resilience4j para tiempos límite, reintentos y cortocircuito.

**Por qué se tomó.** Eureka resuelve el problema de instancias efímeras con direcciones impredecibles, propio del autoescalado en la nube. En Docker Compose los nombres de servicio son estables y el DNS interno resuelve y balancea entre réplicas. Añadirlo supondría un punto de fallo adicional y varios días de configuración para resolver un problema inexistente. Además, Kubernetes resuelve el descubrimiento con DNS del mismo modo, por lo que la decisión no es un atajo sino el camino hacia el que evolucionó la industria.

**Dónde aplica.** Todas las comunicaciones internas y el enrutamiento del Gateway.

**Relación con las funcionalidades.** Es una decisión de infraestructura sin impacto funcional directo, pero libera tiempo de desarrollo hacia funcionalidades del núcleo.

## 2.19. Una instancia PostgreSQL con base y rol por servicio

**Qué se decidió.** Se ejecuta una única instancia de PostgreSQL que aloja seis bases de datos independientes, cada una con su propio rol y sin permisos cruzados.

**Cómo se implementará.** Un script de inicialización crea `identity_db`, `account_db`, `transaction_db`, `ledger_db`, `fraud_db` y `notification_db`, junto con un rol por servicio al que solo se conceden permisos sobre su base. Las migraciones se gestionan con Flyway por servicio y la validación de esquema se configura en modo `validate`.

**Por qué se tomó.** Seis instancias separadas serían más realistas pero consumirían memoria innecesaria en los equipos del equipo de desarrollo. Con una instancia y bases separadas se conserva íntegramente la propiedad de datos, y **el aislamiento lo garantizan los permisos, no la disciplina del equipo**: si un rol carece de acceso a otra base, la regla no puede saltarse ni por descuido ni por urgencia.

**Dónde aplica.** Los seis servicios con persistencia.

**Relación con las funcionalidades.** Sostiene el principio de que ningún servicio consulta datos de otro directamente; toda información ajena se obtiene por REST o llega por evento.

## 2.20. Redis compartido con prefijos y ACL

**Qué se decidió.** Se utiliza una única instancia de Redis compartida, con separación lógica mediante prefijos y separación de acceso mediante listas de control de acceso.

**Cómo se implementará.** Cada servicio emplea su propio prefijo de claves y se autentica con un usuario de Redis restringido a ese patrón y a un conjunto limitado de comandos. Toda clave lleva tiempo de expiración.

**Por qué se tomó.** Varias instancias añadirían contenedores y complejidad operativa sin beneficio proporcional para el plazo disponible. La desventaja real de compartir, que es la ausencia de aislamiento, se mitiga con un archivo de configuración de una decena de líneas que impide que un servicio lea claves de otro.

**Dónde aplica.** API Gateway, Identity, Transaction, Fraud y Account.

**Relación con las funcionalidades.** Soporta la limitación de tasa, la respuesta rápida de idempotencia y los contadores de velocidad del motor de fraude.

**Restricción asociada.** Redis no es fuente de verdad de nada financiero. Si se vaciara por completo, el sistema debe seguir siendo correcto: más lento, con la idempotencia apoyada únicamente en PostgreSQL, pero sin dinero perdido ni duplicado.

## 2.21. Convenciones de modelado de datos

**Qué se decidió.** Un conjunto de convenciones obligatorias en todas las bases de datos del sistema.

| Convención | Motivo |
|---|---|
| Clave primaria de tipo `UUID` | Evita coordinar secuencias entre servicios y no filtra el volumen de operaciones |
| Marcas de tiempo con zona horaria, en UTC | Una marca sin zona horaria produce errores imposibles de depurar |
| Enumerados como texto con restricción `CHECK`, nunca el tipo nativo de PostgreSQL | Añadir un valor a un tipo enumerado nativo exige una migración con bloqueo |
| En Java, enumerados persistidos por nombre, nunca por posición | Reordenar los valores del enumerado corrompería los datos históricos |
| Importes siempre positivos, con el signo expresado en un campo de dirección | Guardar importes negativos convierte cada consulta agregada en una fuente de errores |
| Copias congeladas en lugar de referencias vivas para datos históricos | Un extracto debe seguir siendo legible aunque la contraparte cambie |
| Sin claves foráneas que crucen la frontera de un servicio | La integridad referencial entre bases distintas rompería la independencia de los servicios |
| Sin borrado físico en entidades financieras | Se usa estado o contra-asiento |

**Dónde aplica.** Todas las bases de datos.

**Relación con las funcionalidades.** Condiciona el diseño de todas las entidades descritas en la sección 4.

## 2.22. Monorepo con desarrollo basado en tronco

**Qué se decidió.** Todo el backend reside en un único repositorio desde el primer día, con desarrollo en ramas de vida corta. La aplicación móvil reside en un repositorio independiente.

**Cómo se implementará.** La rama principal está protegida y solo admite cambios mediante solicitudes de incorporación revisadas. Las ramas de funcionalidad tienen una vida máxima de dos o tres días. La integración continua compila únicamente los módulos afectados. Un archivo de propietarios de código asigna revisor automáticamente según el directorio modificado.

**Por qué se tomó.** Como cada persona trabaja en un directorio distinto, los conflictos de fusión son prácticamente inexistentes. Se descartó explícitamente la alternativa de mantener repositorios separados y fusionarlos al final del proyecto: esa fusión exigiría reescribir historiales, rehacer la integración continua y reorganizar dependencias precisamente en la semana destinada a preparar la entrega.

**Regla asociada, no negociable.** La rama principal siempre debe arrancar con un único comando de composición. Si se rompe, repararlo tiene prioridad sobre cualquier otra tarea.

**Dónde aplica.** Organización del trabajo de todo el equipo.

**Relación con las funcionalidades.** Evita la integración tardía, que es el modo de fallo más frecuente en proyectos de esta envergadura.

## 2.23. Vinculación de dispositivo y cambio verificado

**Qué se decidió.** Cada usuario queda vinculado a **un único dispositivo de confianza**. Un intento de inicio de sesión desde un dispositivo distinto no se rechaza sin más: activa un procedimiento de cambio de dispositivo verificado mediante un código enviado por SMS.

**Cómo se implementará.** Durante el registro, la aplicación extrae el identificador del dispositivo y lo almacena en la tabla de perfil de usuario, junto con el modelo. En cada inicio de sesión, la validación del PIN comprueba además que el identificador recibido coincide con el vinculado.

Si no coincide, el acceso se bloquea y se inicia el flujo de cambio:

1. El sistema envía un código numérico al teléfono registrado y crea un registro de verificación con el dispositivo solicitante en estado pendiente.
2. El usuario introduce correo, PIN y el código recibido.
3. Si los tres son correctos, el dispositivo pendiente pasa a ser el dispositivo vinculado, se revocan los tokens de renovación del dispositivo anterior y se publica el evento correspondiente.

El código caduca a los **quince minutos** y admite un máximo de **cinco intentos**; superado ese número, el registro queda inutilizado y debe solicitarse uno nuevo. Cada intento fallido y cada bloqueo por dispositivo no coincidente quedan registrados como evento de inicio de sesión.

**Por qué se tomó.** Sin vinculación, quien conociera el PIN de una persona podría operar desde cualquier terminal, y un PIN de pocos dígitos es una credencial débil frente a una contraseña. La vinculación convierte la posesión del dispositivo en un segundo factor implícito, que es el modelo que emplean las aplicaciones de pago móvil de uso masivo. El cambio de dispositivo no puede prohibirse porque los usuarios cambian de teléfono legítimamente, de modo que la solución es hacerlo posible pero costoso de suplantar.

**Reversión de decisión previa.** La vinculación de dispositivo y la autenticación reforzada figuraban inicialmente fuera del alcance del proyecto. Se reincorporan de forma deliberada al adoptarse el inicio de sesión con PIN, que sin ellas resultaría insuficiente.

**Dónde aplica.** Identity Service, aplicación móvil y Notification Service, que es quien realiza el envío del código.

**Relación con las funcionalidades.** Añade dos funcionalidades nuevas —solicitud de cambio de dispositivo y confirmación del cambio— y modifica la de inicio de sesión, que pasa a tener un tercer resultado posible además de éxito y fallo: dispositivo no coincidente. Habilita además la regla de fraude basada en dispositivo, que anteriormente quedaba fuera de alcance.

## 2.24. Identificación del cliente y numeración de productos

**Qué se decidió.** La persona se identifica mediante **documento de identidad**, con dos tipos admitidos: DNI y pasaporte. La cuenta bancaria se genera automáticamente al darse de alta el usuario, y el backend genera con ella tres identificadores: número de cuenta interno, **CIP** y número de tarjeta. Las transferencias se realizan **por CIP**; el número de tarjeta es un elemento puramente visual.

**Cómo se implementará.** El registro incorpora tipo y número de documento al perfil de usuario, con unicidad sobre el par. Identity Service publica el evento de alta y **Account Service crea la cuenta al consumirlo**, generando en la misma transacción el número de cuenta de catorce dígitos, el CIP de veinte derivado de él, y el número de tarjeta de dieciséis con dígito de verificación de Luhn. El número de tarjeta se devuelve siempre enmascarado.

**Por qué se tomó.** Un correo electrónico es un dato de contacto, no una identidad: cambia y no es adecuado como identificador de un cliente bancario. En cuanto a la numeración, separar los tres identificadores refleja el modelo real, en el que el número interno, el código para recibir transferencias y el plástico son cosas distintas. Que la cuenta se cree por evento y no dentro del registro mantiene la separación de responsabilidades: Identity conoce personas, Account conoce productos financieros.

**Consecuencia sobre el alcance.** La apertura de cuenta deja de ser una acción explícita del usuario y pasa a ser consecuencia del alta. La historia correspondiente del backlog se reinterpreta como consumo del evento de alta, conservando su criterio de aceptación: cuenta creada con saldo cero y asociada al usuario.

**Dónde aplica.** Identity Service, Account Service, Transaction Service y aplicación móvil.

## 2.25. Decisiones aún pendientes

| Decisión | Estado | Plazo |
|---|---|---|
| Versión de Spring Boot | Java 25 confirmado. Falta elegir entre la rama 3.5, con mayor volumen de material de referencia, y la rama 4.1, que es la vigente. Los trenes de Spring Cloud no son intercambiables entre ambas ramas | **Antes del Sprint 1** |
| Exposición del Identity Service hacia Auth0 | La conexión de base de datos personalizada exige que Auth0, que se ejecuta en la nube, alcance por HTTPS un endpoint que se ejecuta localmente. Falta decidir el mecanismo de túnel y quién lo mantiene operativo durante el desarrollo | **Antes del Sprint 1** |
| Proveedor de envío de SMS | El código de cambio de dispositivo requiere un canal de SMS. Falta decidir si se simula en local, de forma equivalente a lo que Mailhog hace con el correo, o se integra un proveedor real | Antes del Sprint 2 |
| Reparto de la lógica de autenticación | La validación del PIN, la comprobación del dispositivo y el flujo de cambio residen en el Identity Service, no en Auth0. Falta confirmar con el equipo hasta dónde llega la responsabilidad de cada uno para evitar lógica duplicada | Antes del Sprint 1 |
| Tecnología del panel de administración | Vista renderizada en servidor frente a aplicación de página única con capa intermedia. Los endpoints son idénticos en ambos casos | Antes del Sprint 5 |

---

# 3. Arquitectura de microservicios

## 3.1. Visión general

El sistema se compone de siete microservicios. PostgreSQL, Kafka, Redis, Mailhog y Docker son componentes de infraestructura y no se contabilizan como microservicios de negocio. Auth0 es un proveedor de identidad externo en la nube, ajeno a la composición local.

```
        ┌──────────────────────┐        ┌────────────────────┐
        │  Aplicación Android  │        │  Panel de admin.   │
        └──────────┬───────────┘        └─────────┬──────────┘
                   │            HTTPS             │
                   └──────────────┬───────────────┘
                                  ▼
                    ┌─────────────────────────────┐
                    │        API GATEWAY          │
                    └──────────────┬──────────────┘
                                   │  REST
        ┌──────────────────┬───────┴────────┬──────────────────┐
        ▼                  ▼                ▼                  ▼
  ┌───────────┐     ┌───────────┐    ┌────────────┐    ┌────────────┐
  │ IDENTITY  │     │  ACCOUNT  │◄───│TRANSACTION │───►│   FRAUD    │
  │           │     │           │REST│            │REST│            │
  └─────┬─────┘     └─────┬─────┘    └─────┬──────┘    └─────┬──────┘
        │  outbox         │  outbox        │  outbox          │
        └─────────────────┴────────────────┴──────────────────┘
                                   ▼
                    ┌──────────────────────────────────────┐
                    │              KAFKA                   │
                    └──────┬──────────────────────┬────────┘
                           ▼                      ▼
                    ┌────────────┐        ┌───────────────┐
                    │   LEDGER   │        │ NOTIFICATION  │
                    └────────────┘        └───────────────┘
```

## 3.2. API Gateway

**Nombre del microservicio.** API Gateway.

**Responsabilidad principal.** Ser el único punto de entrada del sistema desde el exterior.

**Función dentro del sistema.** Recibe todas las peticiones de la aplicación móvil y del panel de administración, valida el token, aplica limitación de tasa, genera el identificador de correlación, unifica el formato de los errores y enruta hacia el microservicio correspondiente. Oculta la topología interna: ni la aplicación móvil ni el panel conocen la existencia ni la dirección de los servicios internos.

No contiene lógica bancaria, no accede a ninguna base de datos y no toma decisiones de negocio.

**Con qué microservicios se comunica y cómo.**

| Destino | Mecanismo | Contenido |
|---|---|---|
| Identity Service | HTTP/REST | Reenvío de peticiones de registro y perfil |
| Account Service | HTTP/REST | Reenvío de peticiones de cuentas y saldo |
| Transaction Service | HTTP/REST | Reenvío de peticiones de transferencia e historial |
| Fraud Service | HTTP/REST | Reenvío de consultas administrativas |
| Ledger Service | HTTP/REST | Reenvío de consultas contables administrativas |
| Notification Service | HTTP/REST | Reenvío de consultas de la bandeja de notificaciones |

El Gateway reenvía el token del usuario tal cual lo recibe. No solicita tokens propios ni participa en la comunicación entre servicios internos. **No se comunica mediante Kafka.**

## 3.3. Identity Service

**Nombre del microservicio.** Identity Service.

**Responsabilidad principal.** Gestionar los datos de identidad **de negocio** del usuario y **validar la credencial de acceso** —PIN y dispositivo vinculado— cuando Auth0 se lo solicita. La emisión y la firma de los tokens pertenecen a Auth0.

**Función dentro del sistema.** Registra nuevos usuarios creándolos primero en Auth0 y a continuación en su propia base de datos, guardando el PIN cifrado y el dispositivo desde el que se realizó el alta. Expone el endpoint que la conexión de base de datos personalizada de Auth0 invoca en cada inicio de sesión para comprobar el PIN y la coincidencia del dispositivo. Custodia el perfil, el documento de identidad, el estado de verificación de cliente y la fecha de alta como cliente bancario. Conduce el flujo de cambio de dispositivo, registra los inicios de sesión y publica los eventos correspondientes.

**Con qué microservicios se comunica y cómo.**

| Contraparte | Mecanismo | Dirección | Contenido |
|---|---|---|---|
| Auth0 | HTTP/REST (Management API) | Saliente | Creación del usuario durante el registro y revocación de sesiones al cambiar de dispositivo |
| Auth0 | HTTP/REST (conexión personalizada) | Entrante | Validación del PIN y del dispositivo vinculado en cada inicio de sesión |
| Account Service | HTTP/REST | Entrante | Consulta de datos del titular durante la creación de cuenta |
| Fraud Service | HTTP/REST | Entrante | Consulta de la fecha de alta como cliente, del estado de verificación y del dispositivo vinculado |
| Notification Service | **Kafka** | Saliente | Publica `UserRegistered`, `UserKycVerified`, `UserLoggedIn`, `DeviceChangeRequested` y `DeviceChanged` en el topic `identity-events` |

## 3.4. Account Service

**Nombre del microservicio.** Account Service.

**Responsabilidad principal.** Ser el propietario exclusivo de las cuentas bancarias y del saldo. Es el único componente autorizado a modificar dinero.

**Función dentro del sistema.** Crea cuentas generando su número interno, su CIP y los datos de su tarjeta, con saldo inicial, responde consultas de cuentas y saldo, valida las condiciones previas de una transferencia incluida la titularidad, ejecuta el movimiento de forma atómica, mantiene el historial de movimientos, y gestiona el bloqueo, el desbloqueo y la acreditación administrativa.

**Con qué microservicios se comunica y cómo.**

| Contraparte | Mecanismo | Dirección | Contenido |
|---|---|---|---|
| Transaction Service | HTTP/REST | Entrante | Validación de la transferencia y ejecución del movimiento |
| Fraud Service | HTTP/REST | Entrante | Consulta de contexto de la cuenta |
| Identity Service | HTTP/REST | Saliente | Validación opcional del titular al crear una cuenta |
| Ledger Service | **Kafka** | Saliente | Publica `AccountCreated` y `AccountCredited` en `account-events`, que originan asientos contables |
| Notification Service | **Kafka** | Saliente | Publica `AccountBlocked` y `AccountUnblocked` en `account-events` |

## 3.5. Transaction Service

**Nombre del microservicio.** Transaction Service.

**Responsabilidad principal.** Orquestar la transferencia de dinero entre cuentas. Es el servicio central del sistema y no es un CRUD.

**Función dentro del sistema.** Recibe la solicitud de transferencia, garantiza la idempotencia, valida el contenido, coordina la validación de cuentas y la evaluación de riesgo, solicita la ejecución del movimiento, gestiona la máquina de estados, publica los eventos resultantes mediante la tabla de salida y responde al usuario. **No modifica saldos ni escribe asientos contables.**

**Con qué microservicios se comunica y cómo.**

| Contraparte | Mecanismo | Dirección | Contenido |
|---|---|---|---|
| Account Service | HTTP/REST | Saliente | Validación de cuentas y titularidad; ejecución del movimiento |
| Fraud Service | HTTP/REST | Saliente | Evaluación de riesgo antes de mover el dinero |
| Fraud Service | HTTP/REST | Entrante | Consulta del historial reciente del usuario |
| Ledger Service | **Kafka** | Saliente | Publica `TransactionCompleted` en `transaction-events`, que origina el asiento contable |
| Notification Service | **Kafka** | Saliente | Publica `TransactionCompleted`, `TransactionFailed` y `TransactionFlagged` |
| Fraud Service | **Kafka** | Saliente | Los mismos eventos, consumidos para actualizar el historial |

## 3.6. Ledger Service

**Nombre del microservicio.** Ledger Service.

**Responsabilidad principal.** Mantener el libro contable inmutable de partida doble y verificar que la contabilidad concuerda con los saldos operativos.

**Función dentro del sistema.** Consume eventos de negocio y genera el asiento contable correspondiente, con sus líneas al debe y al haber. Expone consultas contables de solo lectura y ejecuta la reconciliación contra Account Service, registrando las discrepancias detectadas.

**Es un consumidor puro: no expone escritura por HTTP y no participa en el camino crítico de la transferencia.**

**Con qué microservicios se comunica y cómo.**

| Contraparte | Mecanismo | Dirección | Contenido |
|---|---|---|---|
| Transaction Service | **Kafka** | Entrante | Consume `TransactionCompleted` del topic `transaction-events` |
| Account Service | **Kafka** | Entrante | Consume `AccountCreated` y `AccountCredited` del topic `account-events` |
| Account Service | HTTP/REST | Saliente | Consulta de saldos durante la reconciliación |
| API Gateway | HTTP/REST | Entrante | Consultas contables administrativas |

## 3.7. Fraud Service

**Nombre del microservicio.** Fraud Service.

**Responsabilidad principal.** Evaluar el riesgo de una operación **antes** de que el dinero se mueva, mediante un motor de reglas configurable.

**Función dentro del sistema.** Recibe la solicitud de evaluación, ejecuta las reglas activas, calcula una puntuación ponderada, determina el nivel de riesgo y devuelve una decisión. Persiste toda evaluación, incluidas las aprobadas, y genera incidentes cuando el riesgo es alto. En paralelo consume eventos para mantener el historial que alimentan las reglas de velocidad.

**Con qué microservicios se comunica y cómo.**

| Contraparte | Mecanismo | Dirección | Contenido |
|---|---|---|---|
| Transaction Service | HTTP/REST | Entrante | Solicitud de evaluación, con límite de 500 ms |
| Account Service | HTTP/REST | Saliente | Contexto de la cuenta implicada |
| Identity Service | HTTP/REST | Saliente | Fecha de alta como cliente y estado de verificación |
| Transaction Service | HTTP/REST | Saliente | Historial reciente de operaciones |
| Transaction Service | **Kafka** | Entrante | Consume eventos de transacción para actualizar el historial |
| Notification Service | **Kafka** | Saliente | Publica `FraudIncidentRaised` en `fraud-events` |

## 3.8. Notification Service

**Nombre del microservicio.** Notification Service.

**Responsabilidad principal.** Comunicar al usuario lo que ocurre en su cuenta, a través de la bandeja de la aplicación y del correo electrónico.

**Función dentro del sistema.** Consume los eventos de los cuatro topics del sistema, decide cuáles merecen notificación, genera el contenido, lo persiste para su consulta en la aplicación y lo envía por correo. Expone la consulta de la bandeja y el marcado como leída.

**Es un consumidor puro.** Ningún servicio lo invoca por REST para pedirle que envíe algo: se entera por los eventos.

**Con qué microservicios se comunica y cómo.**

| Contraparte | Mecanismo | Dirección | Contenido |
|---|---|---|---|
| Transaction Service | **Kafka** | Entrante | Transferencia completada, fallida o retenida |
| Account Service | **Kafka** | Entrante | Cuenta creada, acreditada, bloqueada o desbloqueada |
| Fraud Service | **Kafka** | Entrante | Incidente de riesgo elevado |
| Identity Service | **Kafka** | Entrante | Alta de usuario, verificación e inicio de sesión |
| API Gateway | HTTP/REST | Entrante | Consulta de la bandeja y marcado como leída |
| Mailhog | SMTP | Saliente | Envío del correo |

**Propiedad de diseño relevante.** Transaction Service nunca espera a Notification Service. Un fallo en el envío de una notificación no puede afectar a una transferencia ya completada.

## 3.9. Matriz de comunicación

**Comunicación mediante HTTP/REST**

| Origen | Destino | Motivo |
|---|---|---|
| Gateway | Los seis servicios de negocio | Enrutamiento de peticiones de usuario |
| Transaction | Account | Validar cuentas y ejecutar el movimiento |
| Transaction | Fraud | Evaluar el riesgo |
| Fraud | Account | Contexto de la cuenta |
| Fraud | Identity | Antigüedad del cliente y dispositivo vinculado |
| Fraud | Transaction | Historial reciente |
| Account | Identity | Validación del titular |
| Ledger | Account | Saldos para la reconciliación |
| Identity | Auth0 | Creación del usuario y revocación de sesiones |
| Auth0 | Identity | Validación del PIN y del dispositivo vinculado |

**Comunicación mediante Kafka**

| Productor | Topic | Consumidores |
|---|---|---|
| Transaction | `transaction-events` | Ledger, Notification, Fraud |
| Account | `account-events` | Ledger, Notification |
| Fraud | `fraud-events` | Notification |
| Identity | `identity-events` | Notification |

---

# 4. Base de datos y funcionalidades de cada microservicio

## 4.1. API Gateway

### Tablas

**No tiene base de datos.** Es una decisión deliberada: si el Gateway persistiera estado, dejaría de ser un componente de borde reemplazable y se convertiría en un punto de fallo con datos que recuperar.

Su único almacenamiento son claves efímeras en Redis:

| Clave | Contenido | Expiración |
|---|---|---|
| `gateway:ratelimit:{userId}` | Contador de peticiones del usuario | 1 minuto |
| `gateway:ratelimit:ip:{ip}` | Contador por dirección de origen | 1 minuto |

### Funcionalidades

- Enrutar las peticiones hacia el microservicio correspondiente.
- Validar el token: firma, emisor y expiración.
- Aplicar limitación de tasa por usuario y por dirección de origen.
- Generar y propagar el identificador de correlación.
- Aplicar la política de origen cruzado.
- Unificar el formato de todos los errores del sistema.
- Impedir el acceso externo a los endpoints internos.

---

## 4.2. Identity Service

Base de datos: `identity_db`.

### Tablas y entidades

**Tabla `user_profile`** — entidad *Perfil de usuario*. Representa a la persona como cliente del banco y custodia su credencial de acceso.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del usuario dentro del sistema |
| `auth0_user_id` | Texto, único | Vínculo con el usuario de Auth0; es el valor que llega en el campo `sub` del token |
| `document_type` | Texto | Tipo de documento: DNI o pasaporte |
| `document_number` | Texto | Número de documento |
| `first_name`, `last_name` | Texto | Nombre y apellidos |
| `email` | Texto, único | Correo electrónico |
| `phone` | Texto, único | Teléfono; destino del código de cambio de dispositivo |
| `pin_hash` | Texto | Resumen criptográfico del PIN de acceso |
| `device_id` | Texto | Identificador del dispositivo vinculado |
| `device_model` | Texto | Modelo del dispositivo vinculado, a título informativo |
| `birth_date` | Fecha | Fecha de nacimiento |
| `customer_since` | Marca de tiempo | Fecha de alta como cliente bancario |
| `status` | Texto | Activo, suspendido o cerrado |
| `created_at`, `updated_at` | Marca de tiempo | Auditoría |
| `version` | Entero | Control de concurrencia optimista |

Restricción compuesta: el par tipo y número de documento es único. Restricción de validación: el tipo solo admite los valores `DNI` y `PASSPORT`.

**Tabla `verification_code`** — entidad *Código de verificación*. Sostiene el estado intermedio del cambio de dispositivo.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del registro |
| `user_id` | UUID | Usuario que solicita el cambio |
| `code` | Texto | Código enviado por SMS |
| `purpose` | Texto | Motivo de la verificación; actualmente cambio de dispositivo |
| `pending_device_id` | Texto | Dispositivo solicitante, todavía no confirmado |
| `pending_device_model` | Texto | Modelo del dispositivo solicitante |
| `attempts` | Entero | Número de intentos fallidos acumulados |
| `max_attempts` | Entero | Límite de intentos; su valor por defecto es cinco |
| `expires_at` | Marca de tiempo | Momento de caducidad; quince minutos después de la creación |
| `used_at` | Marca de tiempo | Momento en que el código se consumió correctamente |
| `created_at` | Marca de tiempo | Auditoría |

**Tabla `refresh_token`** — entidad *Token de renovación*. Registra las sesiones vivas para poder revocarlas al cambiar de dispositivo.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del registro |
| `user_id` | UUID | Titular de la sesión |
| `auth0_token_id` | Texto | Referencia a la sesión emitida por Auth0 |
| `issued_at` | Marca de tiempo | Momento de emisión |
| `revoked_at` | Marca de tiempo | Momento de revocación, si la hubo |
| `revoked_reason` | Texto | Motivo: cierre de sesión, nuevo inicio o cambio de dispositivo |

**Tabla `role`** — entidad *Rol*. Catálogo local de roles, poblado por migración. Contiene únicamente usuario y administrador, y se mantiene sincronizado con los roles definidos en Auth0.

**Tabla `login_event`** — entidad *Evento de inicio de sesión*. Registra cada intento de acceso.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del registro |
| `user_id` | UUID | Usuario que intentó acceder |
| `result` | Texto | Éxito, fallo o dispositivo no coincidente |
| `reason` | Texto | Detalle del resultado |
| `ip_address` | Dirección IP | Origen de la conexión |
| `user_agent` | Texto | Identificación del cliente que realizó la petición |
| `device_id` | Texto | Dispositivo desde el que se intentó el acceso |
| `device_model` | Texto | Modelo del dispositivo |
| `occurred_at` | Marca de tiempo | Momento del intento |

**Tabla `outbox_event`** — entidad *Evento pendiente de publicación*. Descrita en detalle en 4.4.

### Relaciones entre entidades

- `user_profile` **1 → N** `login_event`: un usuario acumula muchos intentos de acceso.
- `user_profile` **1 → N** `verification_code`: un usuario puede solicitar el cambio de dispositivo varias veces, aunque solo un código permanece vigente.
- `user_profile` **1 → N** `refresh_token`: se conservan también las sesiones ya revocadas, como evidencia de auditoría.
- `user_profile` **N → 1** `role`: cada usuario tiene exactamente un rol.
- `outbox_event` no tiene relación estructural con las demás; almacena eventos serializados listos para publicar.

### Funcionalidades

- Registrar un usuario nuevo, creándolo primero en Auth0 y después en la base local, guardando el PIN cifrado y el dispositivo de alta.
- Compensar la creación en Auth0 si falla la creación del perfil, para no dejar usuarios huérfanos.
- Validar el PIN y la coincidencia del dispositivo cuando Auth0 lo solicita durante el inicio de sesión.
- Iniciar el cambio de dispositivo generando y enviando el código de verificación.
- Confirmar el cambio de dispositivo, actualizar el dispositivo vinculado y revocar las sesiones anteriores.
- Consultar y actualizar el perfil del usuario autenticado.
- Consultar usuarios desde el panel de administración.
- Registrar los inicios de sesión y publicar el evento correspondiente.
- Exponer un endpoint interno para que Fraud Service consulte la antigüedad del cliente y el dispositivo vinculado.

### Detalles de diseño

**`auth0_user_id`.** Es el identificador que Auth0 asigna al usuario, y es el mismo valor que llega en el campo `sub` del token. Existe porque Identity Service necesita saber a qué usuario del proveedor de identidad corresponde cada perfil. Se utiliza al resolver el perfil a partir del token recibido.

> **Regla importante:** el resto del sistema (cuentas, transferencias, notificaciones) referencia el campo `id` de esta tabla, **no** el `auth0_user_id`. De ese modo, si algún día se cambiara de proveedor de identidad, el cambio afectaría a un único servicio. Esta regla es la que ha permitido sustituir el proveedor sin propagar el cambio al resto de servicios.

**`pin_hash`.** El PIN nunca se almacena en claro ni viaja fuera de la petición de validación. Se guarda su resumen criptográfico con una función de derivación lenta y con sal. Un PIN de pocos dígitos tiene un espacio de búsqueda reducido, de modo que su seguridad no descansa en el secreto en sí, sino en la combinación con el dispositivo vinculado y con el límite de intentos.

**`device_id` en el perfil frente a `device_id` en el evento de acceso.** El primero es el dispositivo **autorizado**, y es la referencia contra la que se compara. El segundo es el dispositivo **desde el que se intentó** el acceso, y se conserva aunque el intento haya sido rechazado, precisamente porque los intentos desde dispositivos ajenos son la evidencia más valiosa para el motor de fraude.

**`attempts` y `max_attempts`.** El contador se incrementa con cada código introducido incorrectamente. Al alcanzar el límite, el registro deja de ser utilizable y debe solicitarse uno nuevo. Sin este límite, un código de seis dígitos sería enumerable en cuestión de minutos.

**`document_type` y `document_number`.** Identifican a la persona con independencia del correo, que es un dato de contacto y puede cambiar. Se admiten dos valores: `DNI`, con exactamente ocho dígitos numéricos, y `PASSPORT`, con entre seis y doce caracteres alfanuméricos. La validación de formato por tipo se realiza en la capa de aplicación; la base de datos se limita a la restricción de valores admitidos y a la unicidad del par. Se optó por una restricción de validación sobre texto en lugar de un tipo enumerado nativo porque las migraciones de Flyway manejan mejor la ampliación del primero.

> **Regla importante:** el identificador de acceso frente a Auth0 sigue siendo el **correo electrónico**. El documento es un dato de negocio, no una credencial. Se separan porque cambiar el identificador canónico una vez creados los usuarios en Auth0 obligaría a migrarlos.

**`customer_since`.** Fecha en la que la persona pasó a ser cliente del banco. No es lo mismo que `created_at`, que indica cuándo se creó la fila. Se separan porque `customer_since` es un dato de negocio consultado por Fraud Service para aplicar la regla "cliente reciente con importe elevado", mientras que `created_at` es un dato técnico de auditoría.

**Ausencia de verificación de identidad (KYC).** Un banco real está obligado a verificar la identidad de sus clientes antes de permitirles operar. Este proyecto **no modela ese proceso**: no existe integración con RENIEC ni con ningún servicio de verificación, y una aprobación automática no aportaría más que dos columnas siempre con el mismo valor. La decisión es deliberada y queda registrada aquí, no omitida por descuido: el modelo asume que todo usuario registrado está habilitado para operar. Incorporarlo en el futuro requeriría un campo de estado en esta tabla y una comprobación previa en Transaction Service.

**`ip_address` y `user_agent`.** La dirección de origen de la conexión y la cadena que identifica al cliente que realizó la petición. Existen para poder mostrar al usuario avisos del tipo "se ha iniciado sesión desde un dispositivo distinto" y como evidencia de auditoría ante un acceso sospechoso.

**Ausencia de tabla de claves de firma.** El proyecto no custodia material criptográfico propio: Auth0 firma los tokens y publica sus claves públicas, y los servicios las obtienen a partir del identificador de clave que viaja en la cabecera del token. La rotación es responsabilidad del proveedor.

**`version`.** Contador que se incrementa en cada actualización de la fila. Si dos procesos leen el mismo registro y ambos intentan guardarlo, el segundo detecta que la versión cambió y falla en lugar de sobrescribir silenciosamente el cambio del primero. Es el mecanismo de bloqueo optimista de JPA.

---

## 4.3. Account Service

Base de datos: `account_db`. Es el modelo más delicado del sistema: aquí vive el dinero.

### Tablas y entidades

**Tabla `account`** — entidad *Cuenta bancaria*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador de la cuenta |
| `account_number` | Texto de 14, único | Número de cuenta interno de la plataforma |
| `cip` | Texto de 20, único | Código interbancario de la cuenta; **es el dato que se usa para transferir** |
| `card_number` | Texto de 16, único | Número de tarjeta **generado por el backend**; dato visual, sin funcionalidad |
| `card_expiry` | Texto de 5 | Vencimiento generado, en formato MM/AA |
| `card_holder_name` | Texto | Nombre que se muestra en la tarjeta |
| `user_id` | UUID | Titular; referencia a Identity Service |
| `alias` | Texto | Nombre que el usuario da a la cuenta |
| `type` | Texto | Tipo de cuenta |
| `currency` | Texto de 3 | Moneda; siempre PEN |
| `status` | Texto | Activa, bloqueada o cerrada |
| `balance` | Numérico (19,4) | **Saldo actual** |
| `opened_at` | Marca de tiempo | Fecha de apertura |
| `created_at`, `updated_at` | Marca de tiempo | Auditoría |
| `version` | Entero | Control de concurrencia optimista |

Restricción de validación: el saldo nunca puede ser negativo.

**Tabla `account_movement`** — entidad *Movimiento de cuenta*. Registro histórico que nunca se modifica ni se elimina.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del movimiento |
| `account_id` | UUID | Cuenta afectada |
| `operation_id` | UUID | Operación que lo originó |
| `operation_type` | Texto | Transferencia, depósito, saldo inicial o ajuste |
| `direction` | Texto | Debe o haber |
| `amount` | Numérico (19,4) | Importe, siempre positivo |
| `balance_after` | Numérico (19,4) | Saldo resultante tras el movimiento |
| `counterparty_cip` | Texto de 20 | CIP de la contraparte, tal como se introdujo |
| `counterparty_account_number` | Texto de 14 | Número de cuenta de la contraparte |
| `description` | Texto | Concepto |
| `created_at` | Marca de tiempo | Momento del movimiento |

Restricción de unicidad: la combinación de operación, cuenta y dirección no puede repetirse.

**Tabla `account_status_history`** — entidad *Historial de estado de la cuenta*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del registro |
| `account_id` | UUID | Cuenta afectada |
| `previous_status`, `new_status` | Texto | Estado anterior y nuevo |
| `reason` | Texto | Motivo del cambio |
| `changed_by` | UUID | Quién realizó el cambio |
| `changed_at` | Marca de tiempo | Momento del cambio |

**Tabla `outbox_event`** — entidad *Evento pendiente de publicación*.

### Relaciones entre entidades

- `account` **1 → N** `account_movement`: una cuenta acumula muchos movimientos.
- `account` **1 → N** `account_status_history`: una cuenta acumula muchos cambios de estado.
- Invariante que relaciona ambas entidades principales: la suma algebraica de los movimientos de una cuenta debe coincidir siempre con su saldo.

### Funcionalidades

- Crear una cuenta **generando** su número interno, su CIP y los datos de su tarjeta, con saldo inicial en cero, a partir del evento de alta de usuario publicado por Identity Service.
- Consultar las cuentas del usuario autenticado, devolviendo el número de tarjeta siempre enmascarado.
- Resolver un CIP para obtener la cuenta destino y el nombre del titular durante la validación de una transferencia.
- Consultar el saldo de una cuenta propia.
- Consultar el historial de movimientos paginado.
- Validar una transferencia: existencia de ambas cuentas, estado activo, coincidencia de moneda, **titularidad de la cuenta origen** y disponibilidad de fondos.
- **Ejecutar el movimiento**: débito y crédito en una única transacción, con bloqueo de ambas cuentas en orden determinista, de forma idempotente.
- Acreditar saldo por parte de un administrador.
- Bloquear y desbloquear cuentas.
- Publicar los eventos de cuenta mediante la tabla de salida.

### Detalles de diseño

**`account_number`, `cip` y `card_number`: tres identificadores con tres propósitos.** El número de cuenta es el identificador interno de la plataforma y no se pide nunca al usuario. El CIP es el único dato que se introduce para transferir, y es el que se muestra para recibir dinero. El número de tarjeta es **exclusivamente visual**: existe para que la pantalla de inicio de la aplicación muestre una tarjeta con aspecto realista, y no participa en ninguna operación.

**Generación en el momento de la apertura.** Los tres valores los **produce el backend** dentro de la misma transacción que crea la cuenta. Ninguno se solicita al usuario, ninguno se importa de un sistema externo y ninguno corresponde a un producto real preexistente: no hay emisor de tarjetas, ni plástico, ni proveedor de numeración. Se trata de una banca virtual, de modo que la cuenta, el CIP y la tarjeta **nacen con el alta**.

- **Número de cuenta**, catorce dígitos: código de entidad ficticio, tres dígitos de agencia y diez dígitos aleatorios.
- **CIP**, veinte dígitos: incorpora los catorce del número de cuenta más dígitos de control. Se deriva del número de cuenta en lugar de generarse de forma independiente para que sea verificable y para que un CIP mal tecleado se detecte antes de consultar la base de datos.
- **Número de tarjeta**, dieciséis dígitos: prefijo de emisor ficticio, nueve dígitos aleatorios y un dígito de verificación calculado con el algoritmo de Luhn. El vencimiento se fija a cinco años desde la apertura.

La colisión se resuelve por reintento apoyado en la restricción de unicidad de la base de datos, no comprobando previamente la existencia del valor, que sería susceptible de condición de carrera.

**Enmascaramiento del número de tarjeta.** El valor se persiste completo, pero **ninguna respuesta de la API lo devuelve íntegro**: se expone siempre con los seis primeros y los cuatro últimos dígitos visibles. Aunque los números sean ficticios, tratarlos como dato sensible desde el principio evita normalizar una práctica que en un sistema real constituiría un incumplimiento.

> **Nota de nomenclatura:** en el sistema financiero peruano el código de veinte dígitos que identifica una cuenta para transferencias interbancarias se denomina **CCI**; las siglas *CIP* corresponden a un código de pago distinto. En este proyecto se adopta **CIP** como nombre propio de la plataforma simulada, con la equivalencia funcional del CCI. La decisión es deliberada y queda registrada aquí para evitar que se interprete como un error de dominio.

**`counterparty_cip` junto a `counterparty_account_number`.** Se conservan ambos, congelados en el momento del movimiento. El CIP es el dato que el usuario reconoce y el que debe aparecer en el extracto; el número de cuenta permite relacionar el movimiento con la cuenta interna aunque el CIP se regenerara en el futuro.

**`balance` frente a la suma de `account_movement`.** El saldo se almacena de forma materializada, es decir, precalculado en una columna, en lugar de recalcularse sumando todos los movimientos en cada consulta. Se hace por rendimiento: una cuenta con miles de movimientos haría muy lenta cada consulta de saldo. La coherencia se garantiza actualizando ambos en la misma transacción, y se verifica con una prueba automatizada.

**Restricción `balance >= 0`.** Es una validación a nivel de base de datos que impide dejar una cuenta en negativo. **No sustituye a la validación de fondos, la respalda.** Aunque un error de lógica intentara aplicar un débito excesivo, la base de datos aborta la transacción completa. Es la red de seguridad más valiosa del sistema.

**`operation_id`.** Identificador de la operación de negocio que originó el movimiento. En una transferencia contiene el identificador de la transferencia; en una acreditación, el de la acreditación. Existe para poder relacionar los movimientos con la operación que los produjo y, sobre todo, para hacer posible la restricción de unicidad descrita a continuación.

**Restricción única sobre `operation_id`, `account_id` y `direction`.** Es la garantía última de que una transferencia no puede aplicarse dos veces. Si un reintento intentara insertar de nuevo el mismo movimiento, PostgreSQL rechazaría la operación completa. Aunque fallaran todas las capas anteriores de idempotencia, esta restricción impide la duplicación de dinero.

**`direction` y el signo del importe.** El campo `amount` almacena **siempre un valor positivo**; el sentido del movimiento lo indica `direction`, que vale debe o haber. Se hizo así porque guardar importes negativos parece cómodo pero convierte cada consulta agregada en una fuente de errores de signo difíciles de detectar.

**`balance_after`.** Saldo que quedó en la cuenta inmediatamente después de aplicar ese movimiento. Existe para poder reconstruir un extracto bancario sin recalcular, para depurar sin ambigüedad cuándo se produjo una discrepancia, y para verificar la coherencia entre movimientos y saldo.

**`counterparty_account_number`.** Número de cuenta de la otra parte de la operación, guardado como **copia congelada** en el momento del movimiento, no como referencia viva a la otra cuenta. Existe porque un extracto histórico debe seguir siendo legible aunque la cuenta contraria cambie de estado, de titular o desaparezca del sistema.

**`changed_by`.** Identificador del administrador que realizó un cambio de estado sobre la cuenta. Existe para poder responder a la pregunta "¿quién bloqueó esta cuenta y por qué?". Sin este campo, la funcionalidad de bloqueo carecería de trazabilidad y no sería defendible como funcionalidad de un sistema financiero.

---

## 4.4. Transaction Service

Base de datos: `transaction_db`.

### Tablas y entidades

**Tabla `transfer`** — entidad *Transferencia*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador de la transferencia |
| `user_id` | UUID | Ordenante, tomado del token |
| `source_account_id` | UUID | Cuenta origen |
| `source_account_number` | Texto de 14 | Número de la cuenta origen |
| `destination_cip` | Texto de 20 | CIP introducido por el usuario |
| `destination_account_id` | UUID | Cuenta destino, resuelta durante la validación a partir del CIP |
| `destination_account_number` | Texto de 14 | Número interno de la cuenta destino, resuelto |
| `destination_holder_name` | Texto | Nombre del titular destino |
| `amount` | Numérico (19,4) | Importe |
| `currency` | Texto de 3 | Moneda |
| `status` | Texto | Estado dentro de la máquina de estados |
| `failure_code` | Texto | Código estable del motivo de fallo |
| `failure_reason` | Texto | Explicación legible del fallo |
| `risk_score` | Entero | Puntuación de riesgo, de 0 a 100 |
| `risk_level` | Texto | Nivel de riesgo |
| `description` | Texto | Concepto introducido por el usuario |
| `correlation_id` | UUID | Traza de la petición original |
| `created_at` | Marca de tiempo | Creación |
| `processing_at`, `completed_at` | Marca de tiempo | Transiciones de estado |
| `version` | Entero | Control de concurrencia optimista |

**Tabla `idempotency_record`** — entidad *Registro de idempotencia*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del registro |
| `user_id` | UUID | Usuario que envió la petición |
| `idempotency_key` | Texto | Clave enviada por el cliente |
| `request_hash` | Texto de 64 | Huella del cuerpo de la petición |
| `transfer_id` | UUID | Transferencia asociada |
| `status` | Texto | En curso, completada o fallida |
| `response_status` | Entero | Código de respuesta devuelto |
| `response_body` | JSON | Respuesta original completa |
| `created_at`, `expires_at` | Marca de tiempo | Vigencia del registro |

Restricción de unicidad: la combinación de usuario y clave no puede repetirse.

**Tabla `transfer_status_history`** — entidad *Historial de estados de la transferencia*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del registro |
| `transfer_id` | UUID | Transferencia afectada |
| `previous_status`, `new_status` | Texto | Estado anterior y nuevo |
| `reason` | Texto | Motivo de la transición |
| `changed_by` | UUID | Administrador, o vacío si fue el sistema |
| `changed_at` | Marca de tiempo | Momento de la transición |

**Tabla `outbox_event`** — entidad *Evento pendiente de publicación*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del evento |
| `aggregate_type` | Texto | Tipo de entidad que originó el evento |
| `aggregate_id` | UUID | Identificador de esa entidad |
| `event_type` | Texto | Nombre del evento |
| `event_version` | Entero | Versión del contrato |
| `payload` | JSON | Contenido del evento |
| `headers` | JSON | Identificadores de correlación y causalidad |
| `created_at` | Marca de tiempo | Creación |
| `published_at` | Marca de tiempo | Vacío mientras esté pendiente |
| `attempts` | Entero | Intentos de publicación |
| `last_error` | Texto | Último error de publicación |

### Relaciones entre entidades

- `transfer` **1 → N** `transfer_status_history`: una transferencia acumula sus transiciones.
- `idempotency_record` **1 → 1** `transfer`: cada registro de idempotencia apunta a la transferencia que produjo, o a ninguna si aún está en curso.
- `outbox_event` se relaciona lógicamente con `transfer` a través del campo de identificador de entidad, sin clave foránea, para que la tabla pueda albergar eventos de cualquier entidad.

### Funcionalidades

- Crear una transferencia con clave de idempotencia obligatoria.
- Garantizar que una petición repetida no genera una segunda transferencia.
- Validar el contenido: importe positivo, cuentas distintas, y formato y dígitos de control del CIP destino correctos.
- Coordinar con Account Service la resolución del CIP destino y la validación de ambas cuentas.
- Coordinar la evaluación de riesgo con Fraud Service y aplicar la política de degradación.
- Solicitar la ejecución del movimiento a Account Service.
- Gestionar la máquina de estados y registrar cada transición.
- Publicar los eventos mediante la tabla de salida.
- Consultar una transferencia y su estado.
- Consultar el historial paginado y filtrable.
- Aprobar o rechazar transferencias retenidas, desde el panel de administración.
- Exponer un endpoint interno de historial para Fraud Service.

### Detalles de diseño

**`idempotency_key`.** Identificador único que la aplicación móvil genera **antes** de enviar la petición y transmite en una cabecera. Su propósito es que el servidor pueda reconocer un reintento. El escenario que resuelve es cotidiano: la transferencia se ejecuta correctamente, la respuesta se pierde por un corte de red, la aplicación muestra error y el usuario vuelve a pulsar. Sin esta clave, se transferiría dos veces.

> **Detalle de implementación en la aplicación móvil:** la clave debe generarse **una sola vez**, al construir la pantalla de confirmación, y conservarse en el `ViewModel`. Si se generara dentro del manejador del botón, cada pulsación produciría una clave nueva y la protección no serviría de nada.

**`request_hash`.** Huella criptográfica del cuerpo de la petición. Existe para detectar el caso en que llega la misma clave de idempotencia con un contenido distinto. Sin este campo, un cliente con un error podría recibir como respuesta el resultado de una transferencia que nunca pidió. Cuando se detecta, se devuelve un error de contenido no procesable.

**`response_body`.** Copia completa de la respuesta que se envió la primera vez. Existe para poder devolver ante un reintento **exactamente** la misma respuesta, y no una reconstruida que podría diferir. Es lo que hace que el reintento sea transparente para el cliente.

**Ámbito de la clave de idempotencia.** La restricción de unicidad combina usuario y clave, no la clave por sí sola. Se hizo así para que dos usuarios distintos que generaran por casualidad el mismo identificador no se bloquearan mutuamente.

**Relación entre Redis y esta tabla.** Redis guarda una copia de la clave para responder con rapidez, pero **la fuente de verdad es esta tabla**. Si Redis se vaciara por completo, la protección contra duplicados seguiría en pie. Ese escenario debe probarse.

**`failure_code` frente a `failure_reason`.** Se separan a propósito. El código es estable y programable: la aplicación móvil decide qué pantalla mostrar en función de él. El texto es para la persona y puede cambiarse o traducirse sin romper el cliente.

**`correlation_id`.** Identificador que se genera en el Gateway al recibir la petición y acompaña a esa operación a través de todos los servicios, tanto en las llamadas REST como en las cabeceras de los mensajes de Kafka. Existe para poder reconstruir el recorrido completo de una transferencia buscando un único valor en los registros de todos los servicios.

**`aggregate_type` y `aggregate_id`.** Indican qué entidad originó el evento y cuál es su identificador. El segundo se emplea además como clave de partición al publicar en Kafka, lo que garantiza que todos los eventos de una misma transferencia se procesen en orden.

**El identificador de la fila del outbox se reutiliza como identificador del evento.** Es un detalle pequeño con consecuencias grandes: si se generara un identificador nuevo en cada intento de publicación, un reenvío producido por un reintento llegaría al consumidor con un identificador distinto y la deduplicación no lo reconocería como repetido. Reutilizando el identificador de la fila, toda la cadena de idempotencia funciona.

**Índice parcial sobre las filas pendientes.** El proceso que publica los eventos consulta únicamente las filas cuyo campo de publicación está vacío. Se crea un índice restringido a esa condición para que la consulta siga siendo instantánea por mucho que crezca la tabla histórica.

---

## 4.5. Ledger Service

Base de datos: `ledger_db`.

### Tablas y entidades

**Tabla `ledger_account`** — entidad *Cuenta contable*. Contiene tanto las cuentas de los usuarios como las cuentas de sistema.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador de la cuenta contable |
| `code` | Texto, único | Código: número de cuenta del usuario o código de sistema |
| `type` | Texto | De usuario o de sistema |
| `external_ref` | UUID | Identificador de la cuenta en Account Service |
| `name` | Texto | Denominación |
| `normal_balance` | Texto | Naturaleza deudora o acreedora |
| `currency` | Texto de 3 | Moneda |
| `created_at` | Marca de tiempo | Alta |

**Tabla `journal_entry`** — entidad *Asiento contable*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del asiento |
| `reference_type` | Texto | Tipo de hecho que lo originó |
| `reference_id` | UUID | Identificador de ese hecho |
| `description` | Texto | Descripción |
| `occurred_at` | Marca de tiempo | Momento del hecho económico |
| `recorded_at` | Marca de tiempo | Momento del registro contable |
| `reverses_entry_id` | UUID | Asiento que este corrige, si aplica |
| `source_event_id` | UUID | Evento que lo originó |

Restricción de unicidad: la combinación de tipo e identificador de referencia no puede repetirse.

**Tabla `journal_line`** — entidad *Línea de asiento*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador de la línea |
| `entry_id` | UUID | Asiento al que pertenece |
| `line_number` | Entero | Orden dentro del asiento |
| `ledger_account_id` | UUID | Cuenta contable afectada |
| `direction` | Texto | Debe o haber |
| `amount` | Numérico (19,4) | Importe, siempre positivo |
| `currency` | Texto de 3 | Moneda |

**Tabla `processed_event`** — entidad *Evento procesado*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `event_id` | UUID | Identificador del evento consumido |
| `consumer_group` | Texto | Grupo consumidor que lo procesó |
| `event_type` | Texto | Tipo de evento |
| `processed_at` | Marca de tiempo | Momento del procesamiento |

**Tabla `reconciliation_run`** — entidad *Ejecución de reconciliación*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador de la ejecución |
| `started_at`, `finished_at` | Marca de tiempo | Inicio y fin |
| `accounts_checked` | Entero | Cuentas comprobadas |
| `discrepancies_found` | Entero | Discrepancias detectadas |
| `triggered_by` | Texto | Programada o manual |

**Tabla `reconciliation_incident`** — entidad *Discrepancia detectada*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del incidente |
| `run_id` | UUID | Ejecución que lo detectó |
| `account_id`, `account_number` | UUID / Texto | Cuenta afectada |
| `account_balance` | Numérico (19,4) | Saldo según Account Service |
| `ledger_balance` | Numérico (19,4) | Saldo según los asientos |
| `difference` | Numérico (19,4) | Diferencia |
| `status` | Texto | Abierto, resuelto o ignorado |
| `detected_at`, `resolved_at` | Marca de tiempo | Detección y resolución |
| `notes` | Texto | Observaciones |

### Relaciones entre entidades

- `journal_entry` **1 → N** `journal_line`: un asiento contiene varias líneas.
- `ledger_account` **1 → N** `journal_line`: una cuenta contable aparece en muchas líneas.
- `journal_entry` **1 → 1** `journal_entry` de forma reflexiva: un contra-asiento apunta al asiento que corrige.
- `reconciliation_run` **1 → N** `reconciliation_incident`: una ejecución puede detectar varias discrepancias.

### Funcionalidades

- Consumir los eventos de transacción y de cuenta y generar el asiento correspondiente.
- Dar de alta la cuenta contable espejo cuando se crea una cuenta bancaria.
- Garantizar que un mismo hecho no genere dos asientos.
- Validar en cada asiento que la suma del debe coincide con la del haber.
- Consultar asientos por cuenta y por transferencia.
- Calcular el saldo de una cuenta a partir de sus asientos.
- Ejecutar la reconciliación de forma programada y bajo demanda.
- Registrar y consultar las discrepancias detectadas.
- Registrar contra-asientos de corrección.

### Detalles de diseño

**Por qué existe `ledger_account` en lugar de referenciar directamente las cuentas de Account Service.** El libro contable necesita operar con dos clases de cuentas: las de los usuarios y las de sistema, que no existen en Account Service. Al mantener un catálogo propio que contiene ambas, cada línea de asiento tiene una única clave foránea y las consultas son uniformes. Cuando llega el evento de creación de una cuenta bancaria, el Ledger da de alta su propia cuenta contable espejo.

**Cuentas de sistema.** Son cuentas que no pertenecen a ningún usuario y sirven de contrapartida a las operaciones cuyo dinero entra o sale del sistema. Se precargan dos:

| Código | Uso |
|---|---|
| `SYSTEM_CASH` | Contrapartida de los saldos iniciales y de las acreditaciones administrativas |
| `SYSTEM_SUSPENSE` | Contrapartida de las correcciones contables |

Sin ellas, una acreditación de saldo no tendría origen y rompería la partida doble, porque el dinero entra desde fuera del sistema.

**`normal_balance`.** Indica si la naturaleza de la cuenta es deudora o acreedora, es decir, en qué sentido aumenta su saldo. Es un concepto contable estándar. Existe para poder calcular e interpretar correctamente el saldo de cada cuenta contable.

**`occurred_at` frente a `recorded_at`.** El primero indica cuándo ocurrió el hecho económico; el segundo, cuándo se registró el asiento. Son distintos y la diferencia importa: si la publicación del evento se retrasa una hora, el hecho ocurrió a las 10:00 y se asentó a las 11:00. La contabilidad se ordena por el primero; la depuración del sistema, por el segundo.

**`reference_type` y `reference_id`.** Identifican el hecho de negocio que originó el asiento: una transferencia, un depósito, un saldo inicial o una corrección. Su combinación es única, lo que impide que un mismo hecho genere dos asientos. Junto con la tabla de eventos procesados, constituyen la doble garantía de idempotencia del Ledger.

**`reverses_entry_id`.** Referencia al asiento que este corrige. Existe porque **los asientos no se modifican nunca**: un error se corrige registrando un nuevo asiento de sentido contrario que anula el efecto del original, dejando ambos visibles. Es exactamente como funciona la contabilidad real.

**Inmutabilidad forzada por permisos.** El rol de base de datos de este servicio recibe únicamente permisos de lectura e inserción sobre las tablas de asientos y líneas. No se le concede modificación ni borrado. Una nota en el documento que dijera "los asientos son inmutables" no impediría nada; la ausencia del permiso sí.

**`processed_event`.** Tabla que registra qué eventos ya se procesaron. Existe porque la entrega de mensajes garantiza que un evento llega **al menos una vez**, lo que significa que los duplicados son el comportamiento esperado y no una anomalía. Antes de aplicar el efecto, el consumidor intenta insertar el identificador del evento; si ya existe, descarta el mensaje. La inserción y el efecto ocurren en la misma transacción.

**Por qué la reconciliación se divide en dos tablas.** Separar la ejecución del incidente permite responder no solo "¿qué está descuadrado?" sino también "¿cuándo se comprobó por última vez y salió limpio?". Una comprobación que no deja registro de haberse ejecutado no permite demostrar que el sistema cuadra.

---

## 4.6. Fraud Service

Base de datos: `fraud_db`.

### Tablas y entidades

**Tabla `fraud_rule`** — entidad *Regla de fraude*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador de la regla |
| `code` | Texto, único | Código de la regla |
| `name`, `description` | Texto | Denominación y explicación |
| `rule_type` | Texto | Tipo de regla, determina qué evaluador la ejecuta |
| `threshold_value` | Numérico (19,4) | Umbral: importe o número de operaciones |
| `window_minutes` | Entero | Ventana temporal, solo en reglas de velocidad |
| `weight` | Entero | Peso de la regla en la puntuación total |
| `enabled` | Booleano | Si está activa |
| `updated_by`, `updated_at` | UUID / Marca de tiempo | Auditoría de cambios |

**Tabla `fraud_evaluation`** — entidad *Evaluación de riesgo*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador de la evaluación |
| `transaction_id` | UUID, único | Transferencia evaluada |
| `user_id` | UUID | Usuario ordenante |
| `source_account_id` | UUID | Cuenta origen |
| `destination_account_number` | Texto | Cuenta destino |
| `amount` | Numérico (19,4) | Importe evaluado |
| `score` | Entero | Puntuación resultante, de 0 a 100 |
| `risk_level` | Texto | Bajo, medio o alto |
| `decision` | Texto | Aprobar, revisar o rechazar |
| `degraded` | Booleano | Si se aplicó la política de degradación |
| `duration_ms` | Entero | Tiempo empleado en evaluar |
| `evaluated_at` | Marca de tiempo | Momento de la evaluación |

**Tabla `fraud_evaluation_rule_hit`** — entidad *Regla disparada*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del registro |
| `evaluation_id` | UUID | Evaluación a la que pertenece |
| `rule_id` | UUID | Regla que se disparó |
| `rule_code` | Texto | Código de la regla, copia congelada |
| `weight_applied` | Entero | Peso aplicado, copia congelada |
| `threshold_applied` | Numérico (19,4) | Umbral aplicado, copia congelada |
| `detail` | JSON | Valores observados |

**Tabla `fraud_incident`** — entidad *Incidente de fraude*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del incidente |
| `evaluation_id` | UUID | Evaluación que lo originó |
| `user_id`, `transaction_id` | UUID | Usuario y transferencia |
| `severity` | Texto | Gravedad |
| `status` | Texto | Abierto, revisado o descartado |
| `notes` | Texto | Observaciones del revisor |
| `created_at` | Marca de tiempo | Creación |
| `reviewed_by`, `reviewed_at` | UUID / Marca de tiempo | Revisión |

**Tabla `known_beneficiary`** — entidad *Destinatario conocido*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador del registro |
| `user_id` | UUID | Usuario ordenante |
| `destination_account_number` | Texto | Cuenta destino |
| `first_transfer_at`, `last_transfer_at` | Marca de tiempo | Primera y última transferencia |
| `transfer_count` | Entero | Número de transferencias realizadas |

Restricción de unicidad: la combinación de usuario y cuenta destino no puede repetirse.

**Tabla `processed_event`** — entidad *Evento procesado*.

### Relaciones entre entidades

- `fraud_evaluation` **1 → N** `fraud_evaluation_rule_hit`: una evaluación puede disparar varias reglas.
- `fraud_rule` **1 → N** `fraud_evaluation_rule_hit`: una regla puede dispararse en muchas evaluaciones.
- `fraud_evaluation` **1 → 0..1** `fraud_incident`: una evaluación de riesgo alto genera un incidente.
- `known_beneficiary` no tiene relación estructural con las demás; se actualiza consumiendo eventos.

### Funcionalidades

- Evaluar una transferencia ejecutando las reglas activas.
- Calcular una puntuación ponderada y determinar el nivel de riesgo.
- Devolver una decisión: aprobar, revisar o rechazar.
- Persistir toda evaluación, incluidas las aprobadas.
- Registrar qué reglas se dispararon y con qué valores.
- Generar incidentes cuando el riesgo es alto.
- Consultar y modificar las reglas y sus umbrales desde el panel de administración.
- Mantener los contadores de velocidad en Redis.
- Consumir eventos de transacción para actualizar los destinatarios conocidos.

### Detalles de diseño

**Por qué las reglas viven en base de datos y no en el código.** Almacenarlas como filas permite activar, desactivar y ajustar umbrales sin desplegar ni reiniciar nada. Esa capacidad tiene un valor práctico directo durante la demostración del proyecto: se puede reducir un umbral en vivo, realizar una transferencia pequeña y mostrar cómo el sistema la retiene.

**`weight`.** Peso que aporta cada regla a la puntuación total cuando se dispara. La puntuación final es la suma ponderada de las reglas disparadas, acotada entre 0 y 100. Existe para que las reglas puedan calibrarse de forma independiente: un importe elevado debe pesar más que una operación en horario poco habitual.

**`score` y `risk_level`.** La puntuación es un número entre 0 y 100; el nivel es la banda a la que pertenece. Bajo por debajo de 30, medio entre 30 y 69, alto a partir de 70. Se guardan ambos porque el número permite calibrar y comparar, mientras que la banda es lo que se muestra y lo que determina la decisión.

**Copias congeladas en `fraud_evaluation_rule_hit`.** Es el punto de diseño más importante de este servicio. Como las reglas son editables desde el panel, si solo se guardara el identificador de la regla, una evaluación de hace un mes resultaría inexplicable: se vería que se disparó la regla de importe, pero con el umbral vigente hoy, no con el que había entonces. Guardar código, peso y umbral tal como estaban en el momento de la evaluación hace que **toda decisión pasada siga siendo auditable y reproducible**. Es exactamente lo que exige un sistema antifraude real.

**`degraded` y `duration_ms`.** El primero indica si la evaluación se resolvió aplicando la política de degradación por no haber respuesta a tiempo. El segundo mide cuánto tardó. No son campos decorativos: permiten demostrar con datos que la política de degradación funcionó y que la latencia se mantuvo por debajo del objetivo de 500 milisegundos.

**`known_beneficiary`.** Registra a qué cuentas ha transferido antes cada usuario. Alimenta la regla que eleva el riesgo cuando se transfiere por primera vez a un destinatario nuevo. Se actualiza consumiendo eventos, no durante la evaluación síncrona, para no añadir latencia al camino crítico.

**Por qué los contadores de velocidad no son una tabla.** Las reglas que cuentan operaciones en una ventana de tiempo se apoyan en contadores almacenados en Redis con expiración automática. Son datos efímeros de alta frecuencia de escritura; persistirlos en PostgreSQL sería utilizar la herramienta equivocada y añadiría carga innecesaria a la base de datos.

---

## 4.7. Notification Service

Base de datos: `notification_db`.

### Tablas y entidades

**Tabla `notification`** — entidad *Notificación*.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador de la notificación |
| `user_id` | UUID | Destinatario |
| `source_event_id` | UUID | Evento que la originó |
| `type` | Texto | Tipo de notificación |
| `channel` | Texto | Canal: en la aplicación o correo |
| `title` | Texto | Título |
| `body` | Texto | Contenido |
| `metadata` | JSON | Datos auxiliares para la navegación en la aplicación |
| `status` | Texto | Pendiente, enviada o fallida |
| `attempts` | Entero | Intentos de envío |
| `read_at` | Marca de tiempo | Momento de lectura |
| `created_at`, `sent_at` | Marca de tiempo | Creación y envío |

Restricción de unicidad: la combinación de evento origen, usuario y canal no puede repetirse.

**Tabla `notification_template`** — entidad *Plantilla de notificación*. Opcional.

| Campo | Tipo | Información que almacena |
|---|---|---|
| `id` | UUID | Identificador |
| `code` | Texto, único | Código de la plantilla |
| `channel` | Texto | Canal al que aplica |
| `subject` | Texto | Asunto |
| `body_template` | Texto | Cuerpo con marcadores de sustitución |
| `enabled` | Booleano | Si está activa |

**Tabla `processed_event`** — entidad *Evento procesado*.

### Relaciones entre entidades

- `notification_template` **1 → N** `notification` de forma lógica, a través del tipo y el canal.
- Un mismo evento origina **varias filas** en `notification`: una por cada combinación de destinatario y canal. Una transferencia completada genera cuatro: dos para el emisor y dos para el receptor, en aplicación y correo.

### Funcionalidades

- Consumir los eventos de los cuatro topics del sistema.
- Decidir qué eventos merecen notificación y a quién.
- Generar el contenido a partir de plantillas.
- Persistir la notificación para su consulta en la aplicación.
- Enviar el correo electrónico.
- Consultar la bandeja del usuario autenticado de forma paginada.
- Devolver el número de notificaciones no leídas.
- Marcar una notificación como leída.
- Reintentar los envíos fallidos y derivar a la cola de mensajes fallidos los que no se recuperan.

### Detalles de diseño

**`source_event_id`.** Identificador del evento de Kafka que originó la notificación. Existe para garantizar que un mismo hecho no se notifique dos veces, incluso si el evento se procesa de nuevo tras un reinicio.

**Restricción única sobre evento origen, usuario y canal.** Es la idempotencia expresada como clave natural del modelo. Aunque el evento se procesara dos veces, la segunda inserción fallaría y no habría duplicado. Incluir el usuario en la clave es imprescindible, porque **un mismo evento genera notificaciones para dos personas distintas**: el emisor y el receptor de la transferencia. Incluir el canal permite que la misma persona reciba la notificación en la aplicación y por correo sin que una bloquee a la otra.

**`status` y `read_at` son ejes independientes.** El primero describe el envío: pendiente, enviada o fallida. El segundo describe la lectura por parte del usuario. Se mantienen separados porque mezclar el estado de lectura dentro del estado de envío confundiría dos conceptos distintos y complicaría las consultas. Además, la lectura solo tiene sentido en el canal de la aplicación, no en el correo.

**`metadata`.** Campo de contenido flexible donde se guardan los datos que la aplicación necesita para navegar al detalle: el identificador de la transferencia, el importe y la contraparte. Existe para que al tocar una notificación la aplicación pueda abrir directamente la pantalla correspondiente sin realizar consultas adicionales.

**`attempts`.** Número de intentos de envío realizados. Existe para poder aplicar reintentos con espera creciente y para identificar las notificaciones que nunca llegaron a enviarse.

---

# 5. Sprints

## 5.1. Marco temporal

El proyecto se planifica en **17 semanas**, de las cuales **la primera ya concluyó** y se dedicó al análisis, la definición de la arquitectura y las decisiones documentadas en la sección 2. Restan por tanto **16 semanas**, que se organizan en **ocho sprints de dos semanas**.

| Sprint | Semanas del proyecto | Objetivo |
|---|---|---|
| 1 | 2–3 | Fundaciones y seguridad extremo a extremo |
| 2 | 4–5 | Identidad y cuentas |
| 3 | 6–7 | Transferencia funcional |
| 4 | 8–9 | Eventos y contabilidad |
| 5 | 10–11 | Notificaciones y administración |
| 6 | 12–13 | Detección de fraude |
| 7 | 14–15 | Endurecimiento y consistencia |
| 8 | 16–17 | Cierre y entrega |

**Regla transversal.** Al final de cada sprint, el sistema completo debe arrancar con un único comando de composición desde la rama principal. Sin excepciones.

## 5.2. Sprint 1 — Fundaciones y seguridad extremo a extremo

**Semanas 2 y 3.**

**Objetivo.** Disponer de un esqueleto ejecutable con autenticación real funcionando de extremo a extremo.

**Trabajo a realizar.**
- Crear el monorepo, el proyecto padre y las convenciones de código.
- Componer el entorno con PostgreSQL, Kafka en modo sin coordinador externo, Redis y Mailhog.
- Crear las seis bases de datos y sus roles sin permisos cruzados.
- Configurar las listas de control de acceso de Redis.
- Configurar el inquilino de Auth0 con su aplicación, sus roles y la acción que inyecta el rol en el token, y **versionar esa configuración en el repositorio**.
- Implementar en el Identity Service el endpoint de validación de PIN y dispositivo, y conectarlo a Auth0 mediante la conexión de base de datos personalizada.
- Resolver el túnel que permite a Auth0 alcanzar el entorno local.
- Levantar los esqueletos de los siete servicios con comprobaciones de salud y control de migraciones.
- Configurar el Gateway con enrutamiento y validación de token contra las claves públicas de Auth0.
- Configurar la integración continua con filtrado por rutas.
- Iniciar el proyecto Android y resolver el flujo de inicio de sesión con PIN.

**Entregable.** Desde la aplicación móvil se inicia sesión con PIN, Auth0 valida la credencial contra el Identity Service, se obtiene un token y con él se accede a un endpoint protegido a través del Gateway.

**Advertencia.** Este sprint parece infraestructura pura y es el que más condiciona el resto del proyecto. Dos riesgos concretos merecen atención desde el primer día. El primero es la dependencia de un proveedor en la nube que debe alcanzar un entorno local: si el túnel no está resuelto, **nadie del equipo puede iniciar sesión**, y el trabajo del resto de servicios queda bloqueado. El segundo es que la configuración del inquilino se realice manualmente y no quede versionada: reconstruirla de memoria es un contratiempo que ocurre en prácticamente todos los proyectos que no toman esta precaución.

## 5.3. Sprint 2 — Identidad y cuentas

**Semanas 4 y 5.**

**Objetivo.** Usuarios reales con cuentas reales y saldo consultable.

**Trabajo a realizar.**
- Registro de usuario creándolo en Auth0 y después en la base local, con compensación ante fallo, guardando el PIN cifrado y el dispositivo de alta.
- Perfil del usuario y estado de verificación de cliente.
- Renovación de token con rotación.
- Flujo de cambio de dispositivo con código por SMS, caducidad y límite de intentos.
- Modelo de cuenta y de movimientos, con importes en tipo numérico exacto.
- Generación del número de cuenta, del CIP y de los datos de la tarjeta.
- Saldo inicial registrado también como movimiento.
- Verificación de titularidad en todas las consultas.
- Identificador de correlación generado y propagado.
- Formato unificado de errores.
- En la aplicación móvil: registro, activación biométrica y pantalla de cuentas y saldo.

**Entregable.** Un usuario se registra desde el móvil, activa el acceso biométrico, cierra la aplicación, la reabre con biometría y consulta su saldo. Un intento de acceso desde un dispositivo distinto queda bloqueado y ofrece el flujo de cambio verificado.

**Criterio de finalización.** Ningún usuario puede consultar datos de otro, y existen pruebas automatizadas que lo demuestran.

## 5.4. Sprint 3 — Transferencia funcional

**Semanas 6 y 7.**

**Objetivo.** El hito central del proyecto: mover dinero de forma correcta, segura e idempotente. **Todavía sin mensajería.**

**Trabajo a realizar.**
- Máquina de estados de la transferencia y registro de transiciones.
- Idempotencia con respaldo en Redis y en PostgreSQL, incluida la reproducción de la respuesta original.
- Validación del contenido de la petición.
- Validación de cuentas con verificación de titularidad.
- **Ejecución atómica del débito y el crédito, con bloqueo de ambas cuentas en orden determinista.**
- Autenticación entre servicios con validación de audiencia y de permisos.
- Consulta de transferencia e historial paginado.
- En la aplicación móvil: formulario de transferencia con clave de idempotencia generada una sola vez, pantalla de confirmación e historial.

**Entregable.** Transferencia completa desde el móvil, con los saldos de ambas cuentas actualizados correctamente.

**Criterio de finalización.**
- Cien transferencias cruzadas concurrentes no producen interbloqueos ni descuadres.
- Dos peticiones con la misma clave de idempotencia generan un solo movimiento y devuelven la misma respuesta.
- Un usuario que intenta transferir desde una cuenta ajena recibe un error de autorización.

**Este es el punto de control del proyecto.** Si al final de la semana 7 la transferencia no funciona con estas pruebas superadas, debe replanificarse el alcance antes de continuar.

## 5.5. Sprint 4 — Eventos y contabilidad

**Semanas 8 y 9.**

**Objetivo.** Cerrar el mínimo producto viable con contabilidad de partida doble alimentada por eventos.

**Trabajo a realizar.**
- Configuración de los topics con sus particiones y sus colas de mensajes fallidos.
- Sobre común de eventos y carpeta de contratos.
- Tabla de salida y proceso programado de publicación.
- Consumidor del libro contable con idempotencia por identificador de evento.
- Modelo de partida doble con cuentas de sistema.
- Permisos de base de datos que impiden modificar o eliminar asientos.
- Consultas contables.
- En la aplicación móvil: detalle de la transferencia.

**Entregable.** Una transferencia genera automáticamente su asiento contable. Con la mensajería detenida, la transferencia se completa igual y el asiento aparece al restaurar el servicio.

**Criterio de finalización.** Reprocesar un evento no duplica el asiento, y las pruebas de contrato están superadas.

**Fin del mínimo producto viable. Semana 9 de 17: queda algo menos de la mitad del tiempo para el resto.**

## 5.6. Sprint 5 — Notificaciones y administración

**Semanas 10 y 11.**

**Objetivo.** Cerrar el ciclo de comunicación con el usuario y disponer de herramientas de operación.

**Trabajo a realizar.**
- Consumo de los cuatro topics con idempotencia.
- Canal de notificación dentro de la aplicación, con consulta paginada y marcado como leída.
- Canal de correo con plantillas, dirigido al servidor de captura.
- Notificación tanto al emisor como al receptor.
- Acreditación de saldo por parte del administrador, con su asiento contable contra la cuenta de caja del sistema.
- Bloqueo y desbloqueo de cuentas.
- Panel de administración: usuarios, cuentas, acreditación y transferencias.
- Cierre de sesión y aviso de inicio de sesión.
- Registros estructurados con el identificador de correlación.
- En la aplicación móvil: bandeja de notificaciones.

**Entregable.** Una transferencia genera notificación en la aplicación y correo visible en el servidor de captura. Un administrador acredita saldo desde el panel y el asiento correspondiente aparece en el libro contable.

**Nota.** Al inicio de este sprint debe estar decidida la tecnología del panel de administración.

## 5.7. Sprint 6 — Detección de fraude

**Semanas 12 y 13.**

**Objetivo.** Incorporar la evaluación de riesgo al camino crítico sin comprometer la disponibilidad.

**Trabajo a realizar.**
- Modelo de reglas, evaluaciones e incidentes.
- Motor de reglas con puntuación ponderada y registro de las reglas disparadas con sus valores congelados.
- Contadores de velocidad en Redis.
- Endpoint interno de evaluación con objetivo de latencia inferior a medio segundo.
- Integración síncrona desde el servicio de transacciones, con política de degradación por importe.
- Estado de retención y su liberación por parte del administrador.
- Cola de revisión y ajuste de reglas en el panel.
- Consumo de eventos para actualizar los destinatarios conocidos.
- En la aplicación móvil: presentación del estado de retención.

**Entregable.** Una transferencia de importe elevado queda retenida, se notifica al usuario, y el administrador la libera desde el panel.

**Criterio de finalización.** Con el servicio de fraude detenido, el sistema sigue operando conforme a la política de degradación documentada.

## 5.8. Sprint 7 — Endurecimiento y consistencia

**Semanas 14 y 15.**

**Objetivo.** Convertir el sistema en algo defendible: seguridad completa, resiliencia y consistencia verificada.

**Trabajo a realizar.**
- Listas de control de acceso de la mensajería.
- Reintentos con espera creciente y cola de mensajes fallidos en todos los consumidores.
- Limitación de tasa en el Gateway.
- Cifrado del tráfico con certificado autofirmado.
- Tiempos límite, reintentos y cortocircuito en todas las llamadas internas.
- **Proceso de reconciliación programado y bajo demanda.**
- Límites de importe por operación y diarios.
- Bloqueo temporal tras intentos fallidos de acceso.
- Métricas básicas y trazabilidad completa de una transferencia.
- Pruebas de seguridad.
- Panel de estado del sistema y de mensajes fallidos.
- En la aplicación móvil: pulido del manejo de errores y estados de carga.

**Entregable.** Demostración de resiliencia: se detiene el consumidor del libro contable, se realiza una transferencia, la reconciliación detecta el desfase, se restablece el consumidor y la reconciliación vuelve a cuadrar.

## 5.9. Sprint 8 — Cierre y entrega

**Semanas 16 y 17.**

**Objetivo.** Consolidar, documentar y ensayar la presentación.

**Trabajo a realizar.**
- Documentación de interfaces agregada en el Gateway.
- Registros de decisión de arquitectura.
- Diagramas de contexto y de contenedores.
- Memoria final y evidencias de prueba.
- Colección de peticiones para prueba manual.
- Contra-asientos de corrección.
- Pruebas de carga ligeras.
- Corrección de defectos pendientes.
- **Ensayo completo de la demostración, al menos dos veces.**

**Se reserva holgura deliberadamente en este sprint.** Un proyecto de esta envergadura acumula retrasos, y planificar el último sprint al límite garantiza entregar mal.

**Guion sugerido para la demostración final.**

1. Registro desde el móvil y activación del acceso biométrico.
2. Reapertura con biometría y consulta de saldo.
3. Transferencia correcta, con notificación en el móvil y correo en el servidor de captura.
4. Asiento de partida doble visible en el panel de administración.
5. Doble pulsación deliberada: la idempotencia impide el duplicado.
6. Reducción del umbral de fraude en vivo y transferencia retenida; liberación desde el panel.
7. Detención del consumidor contable, transferencia, reconciliación con discrepancia, restauración y nueva reconciliación correcta.
8. Intento de transferir desde una cuenta ajena, rechazado por falta de autorización.

Los puntos 5, 6 y 7 son los que distinguen este proyecto de una aplicación de gestión convencional.

## 5.10. Dependencias y puntos de control

**Cadena crítica de dependencias.**

```
Auth0 configurado y alcanzable desde el entorno local
    ↓
Identity Service con validación de PIN y dispositivo
    ↓
API Gateway con validación de token
    ↓
Identity Service
    ↓
Account Service con saldo
    ↓
Transaction Service con idempotencia y ejecución atómica    ← ruta crítica
    ↓
Kafka con tabla de salida
    ↓
Ledger Service
    ↓
    ├──► Notification Service
    ├──► Fraud Service
    └──► Reconciliación
```

**Puntos de control.**

| Semana | Hito | Consecuencia si no se cumple |
|---|---|---|
| 3 | Inicio de sesión real de extremo a extremo, con PIN validado y dispositivo comprobado | Retraso en cadena de todo el proyecto |
| 7 | **Transferencia correcta con pruebas de concurrencia superadas** | **Replanificación obligatoria del alcance** |
| 9 | Mínimo producto viable cerrado | Se sacrifican el panel de administración y el servicio de fraude |
| 13 | Fraude integrado | Se degrada a reglas embebidas en el servicio de transacciones |
| 15 | Endurecimiento y reconciliación | Se recorta el alcance del endurecimiento |
| 17 | Entrega | — |

---

# 6. Backlog

## 6.1. Épicas y convenciones

| Épica | Nombre |
|---|---|
| EP-01 | Infraestructura y plataforma |
| EP-02 | Identidad y autenticación |
| EP-03 | Cuentas |
| EP-04 | Transferencias |
| EP-05 | Ledger |
| EP-06 | Fraude |
| EP-07 | Notificaciones |
| EP-08 | Administración |
| EP-09 | Aplicación móvil |
| EP-10 | Calidad, seguridad y observabilidad |

**Prioridades:** `MUST` (imprescindible), `SHOULD` (importante, no crítico), `COULD` (opcional).
**Estado inicial de todos los elementos:** pendiente.

## 6.2. EP-01 Infraestructura y plataforma

| ID | Tarea | Componente | Prior. | Depende de | Sprint |
|---|---|---|---|---|---|
| INF-01 | Monorepo, proyecto padre, convenciones, propietarios de código | — | MUST | — | 1 |
| INF-02 | Composición con PostgreSQL, Kafka, Redis y Mailhog | — | MUST | INF-01 | 1 |
| INF-03 | Seis bases de datos con rol propio y sin permisos cruzados | — | MUST | INF-02 | 1 |
| INF-04 | Listas de control de acceso de Redis por servicio | — | MUST | INF-02 | 1 |
| INF-05 | Inquilino de Auth0 configurado y versionado, con túnel hacia el entorno local | — | MUST | INF-02 | 1 |
| INF-06 | Esqueleto de los siete servicios con salud y migraciones | Todos | MUST | INF-01 | 1 |
| INF-07 | Gateway con enrutamiento y validación de token | Gateway | MUST | INF-05 | 1 |
| INF-08 | Integración continua con filtrado por rutas | — | MUST | INF-01 | 1 |
| INF-09 | Identificador de correlación generado y propagado | Gateway | MUST | INF-07 | 2 |
| INF-10 | Formato uniforme de errores | Gateway | SHOULD | INF-07 | 2 |
| INF-11 | Limitación de tasa con Redis | Gateway | SHOULD | INF-04 | 7 |
| INF-12 | Política de origen cruzado configurada | Gateway | SHOULD | INF-07 | 5 |
| INF-13 | Cifrado del tráfico con certificado autofirmado | Gateway | SHOULD | INF-07 | 7 |
| INF-14 | Gestión de secretos mediante archivo de entorno | — | MUST | INF-02 | 1 |
| INF-15 | Tiempos límite, reintentos y cortocircuito | Transaction, Fraud | SHOULD | TRX-05 | 7 |

## 6.3. EP-02 Identidad y autenticación

| ID | Tarea | Componente | Prior. | Depende de | Sprint |
|---|---|---|---|---|---|
| IDN-01 | Aplicación, roles y acción de inyección de rol en el inquilino | Auth0 | MUST | INF-05 | 1 |
| IDN-02 | Conexión de base de datos personalizada apuntando al Identity Service | Auth0 | MUST | IDN-01 | 1 |
| IDN-03 | Endpoint de validación de PIN y dispositivo vinculado | Identity | MUST | IDN-02 | 1 |
| IDN-04 | Registro de usuario mediante la API de administración, con PIN cifrado | Identity | MUST | IDN-01 | 2 |
| IDN-05 | Renovación de token con rotación | Auth0 | MUST | IDN-03 | 2 |
| IDN-06 | Solicitud de cambio de dispositivo con envío de código por SMS | Identity | MUST | IDN-04 | 2 |
| IDN-07 | Confirmación de cambio de dispositivo con caducidad y límite de intentos | Identity | MUST | IDN-06 | 2 |
| IDN-08 | Revocación de sesiones al confirmarse el cambio de dispositivo | Identity | MUST | IDN-07 | 2 |
| IDN-09 | Cierre de sesión con revocación | Identity | SHOULD | IDN-03 | 5 |
| IDN-10 | Consulta y actualización de perfil | Identity | MUST | IDN-04 | 2 |
| IDN-11 | Estado de verificación de cliente | Identity | SHOULD | IDN-04 | 2 |
| IDN-12 | Aviso de inicio de sesión y publicación del evento | Identity | SHOULD | KAF-01 | 5 |
| IDN-13 | Bloqueo temporal por intentos fallidos de PIN | Identity | MUST | INF-04 | 2 |
| IDN-14 | Endpoint interno de consulta de usuario y dispositivo vinculado | Identity | SHOULD | SEC-02 | 6 |

## 6.4. EP-03 Cuentas

| ID | Tarea | Componente | Prior. | Depende de | Sprint |
|---|---|---|---|---|---|
| ACC-01 | Modelo de cuenta y movimientos con tipo numérico exacto | Account | MUST | INF-03 | 2 |
| ACC-02 | Generación de cuenta con número interno, CIP y tarjeta, y saldo inicial | Account | MUST | ACC-01 | 2 |
| ACC-03 | Consulta de cuentas propias | Account | MUST | ACC-02 | 2 |
| ACC-04 | Consulta de saldo | Account | MUST | ACC-02 | 2 |
| ACC-05 | Historial de movimientos paginado | Account | MUST | ACC-02 | 3 |
| ACC-06 | Validación de transferencia con verificación de titularidad | Account | MUST | ACC-02, SEC-02 | 3 |
| ACC-07 | **Ejecución atómica de débito y crédito con bloqueo ordenado** | Account | MUST | ACC-06 | 3 |
| ACC-08 | Acreditación de saldo por administrador | Account | MUST | ACC-02 | 5 |
| ACC-09 | Bloqueo y desbloqueo de cuenta con historial | Account | SHOULD | ACC-02 | 5 |
| ACC-10 | Publicación de eventos de cuenta mediante tabla de salida | Account | SHOULD | KAF-01 | 5 |

## 6.5. EP-04 Transferencias

| ID | Tarea | Componente | Prior. | Depende de | Sprint |
|---|---|---|---|---|---|
| TRX-01 | Modelo de transferencia y máquina de estados | Transaction | MUST | INF-03 | 3 |
| TRX-02 | Idempotencia con Redis y PostgreSQL | Transaction | MUST | INF-04 | 3 |
| TRX-03 | Validación del contenido de la petición | Transaction | MUST | TRX-01 | 3 |
| TRX-04 | Coordinación de la validación con Account | Transaction | MUST | ACC-06, SEC-02 | 3 |
| TRX-05 | Solicitud de ejecución del movimiento | Transaction | MUST | ACC-07 | 3 |
| TRX-06 | Consulta de transferencia y estado | Transaction | MUST | TRX-01 | 3 |
| TRX-07 | Historial paginado y filtrable | Transaction | MUST | TRX-01 | 3 |
| TRX-08 | Tabla de salida y proceso de publicación | Transaction | MUST | KAF-01 | 4 |
| TRX-09 | Integración con fraude y política de degradación | Transaction | MUST | FRD-03 | 6 |
| TRX-10 | Aprobación y rechazo de transferencias retenidas | Transaction | SHOULD | TRX-09 | 6 |
| TRX-11 | Endpoint interno de historial para fraude | Transaction | SHOULD | SEC-02 | 6 |
| TRX-12 | Límites por operación y diarios | Transaction | COULD | TRX-05 | 7 |

## 6.6. EP-05 Ledger

| ID | Tarea | Componente | Prior. | Depende de | Sprint |
|---|---|---|---|---|---|
| LED-01 | Modelo de cuentas contables, asientos y líneas | Ledger | MUST | INF-03 | 4 |
| LED-02 | Inmutabilidad forzada por permisos de base de datos | Ledger | MUST | LED-01 | 4 |
| LED-03 | Consumo de transferencias completadas con idempotencia | Ledger | MUST | KAF-02 | 4 |
| LED-04 | Consumo de eventos de cuenta y asientos con cuentas de sistema | Ledger | MUST | ACC-10 | 5 |
| LED-05 | Consulta por cuenta y por transferencia | Ledger | MUST | LED-03 | 4 |
| LED-06 | Cálculo del saldo a partir de los asientos | Ledger | SHOULD | LED-03 | 5 |
| LED-07 | **Reconciliación programada y bajo demanda** | Ledger | MUST | LED-06 | 7 |
| LED-08 | Contra-asientos de corrección | Ledger | COULD | LED-02 | 8 |

## 6.7. EP-06 Fraude

| ID | Tarea | Componente | Prior. | Depende de | Sprint |
|---|---|---|---|---|---|
| FRD-01 | Modelo de reglas, evaluaciones e incidentes | Fraud | MUST | INF-03 | 6 |
| FRD-02 | Motor de reglas con puntuación ponderada y valores congelados | Fraud | MUST | FRD-01 | 6 |
| FRD-03 | Endpoint interno de evaluación | Fraud | MUST | FRD-02, SEC-02 | 6 |
| FRD-04 | Reglas de velocidad con contadores en Redis | Fraud | MUST | INF-04 | 6 |
| FRD-05 | Consumo de eventos para destinatarios conocidos | Fraud | SHOULD | KAF-02 | 6 |
| FRD-06 | Consulta y ajuste de reglas | Fraud | SHOULD | FRD-01 | 6 |
| FRD-07 | Registro y consulta de incidentes | Fraud | SHOULD | FRD-02 | 6 |
| FRD-08 | Publicación del evento de incidente | Fraud | SHOULD | KAF-01 | 6 |

## 6.8. EP-07 Notificaciones

| ID | Tarea | Componente | Prior. | Depende de | Sprint |
|---|---|---|---|---|---|
| NOT-01 | Modelo de notificación con canal y estado | Notification | MUST | INF-03 | 5 |
| NOT-02 | Consumo de los cuatro topics con idempotencia | Notification | MUST | KAF-02 | 5 |
| NOT-03 | Canal en aplicación y consulta paginada | Notification | MUST | NOT-01 | 5 |
| NOT-04 | Marcado como leída y contador de no leídas | Notification | MUST | NOT-03 | 5 |
| NOT-05 | Canal de correo con plantillas | Notification | SHOULD | NOT-01 | 5 |
| NOT-06 | Notificación al emisor y al receptor | Notification | SHOULD | NOT-02 | 5 |
| NOT-07 | Reintentos y cola de mensajes fallidos | Notification | SHOULD | KAF-03 | 7 |

## 6.9. EP-08 Administración

| ID | Tarea | Componente | Prior. | Depende de | Sprint |
|---|---|---|---|---|---|
| ADM-01 | Autenticación del panel con rol de administrador | Admin | MUST | IDN-01 | 5 |
| ADM-02 | Listado y detalle de usuarios | Admin | MUST | IDN-06 | 5 |
| ADM-03 | Consulta de cuentas y acreditación de saldo | Admin | MUST | ACC-08 | 5 |
| ADM-04 | Consulta de transferencias con filtros y traza | Admin | MUST | TRX-07 | 5 |
| ADM-05 | Bloqueo y desbloqueo de cuentas | Admin | SHOULD | ACC-09 | 5 |
| ADM-06 | Cola de revisión de transferencias retenidas | Admin | SHOULD | TRX-10 | 6 |
| ADM-07 | Panel de fraude con ajuste de reglas | Admin | SHOULD | FRD-06 | 6 |
| ADM-08 | Consulta contable y ejecución de la reconciliación | Admin | SHOULD | LED-07 | 7 |
| ADM-09 | Estado de los servicios y mensajes fallidos | Admin | COULD | KAF-03 | 7 |

## 6.10. EP-09 Aplicación móvil

| ID | Tarea | Prior. | Depende de | Sprint |
|---|---|---|---|---|
| APP-01 | Proyecto base, navegación, tema y cliente de red | MUST | — | 1 |
| APP-02 | Inicio de sesión con PIN y envío del identificador de dispositivo | MUST | IDN-03 | 1 |
| APP-03 | Registro de usuario con definición de PIN | MUST | IDN-04 | 2 |
| APP-03b | Pantallas de cambio de dispositivo y confirmación por código | MUST | IDN-07 | 2 |
| APP-04 | Activación biométrica con almacén de claves | MUST | APP-02 | 2 |
| APP-05 | Renovación automática del token | MUST | APP-02 | 2 |
| APP-06 | Listado de cuentas y saldo | MUST | ACC-03 | 2 |
| APP-07 | Formulario de transferencia con clave de idempotencia | MUST | TRX-02 | 3 |
| APP-08 | Confirmación y resultado de la transferencia | MUST | TRX-05 | 3 |
| APP-09 | Historial de transferencias | MUST | TRX-07 | 3 |
| APP-10 | Detalle de transferencia | SHOULD | TRX-06 | 4 |
| APP-11 | Bandeja de notificaciones | SHOULD | NOT-03 | 5 |
| APP-12 | Presentación del estado de retención | SHOULD | TRX-09 | 6 |
| APP-13 | Manejo de errores y estados de carga | SHOULD | APP-08 | 7 |

## 6.11. EP-10 Calidad, seguridad y observabilidad

| ID | Tarea | Prior. | Depende de | Sprint |
|---|---|---|---|---|
| SEC-01 | Validación del token en cada servicio | MUST | IDN-01 | 1 |
| SEC-02 | Paso de token entre servicios con validación de audiencia | MUST | IDN-01 | 3 |
| SEC-03 | Prueba de verificación de titularidad | MUST | ACC-06 | 3 |
| SEC-04 | Listas de control de acceso de la mensajería | SHOULD | KAF-01 | 7 |
| KAF-01 | Configuración de topics y productores | MUST | INF-02 | 4 |
| KAF-02 | Consumidores con confirmación manual e idempotencia | MUST | KAF-01 | 4 |
| KAF-03 | Reintentos con espera creciente y cola de mensajes fallidos | SHOULD | KAF-02 | 7 |
| OBS-01 | Registros estructurados con contexto de diagnóstico | MUST | INF-09 | 5 |
| OBS-02 | Comprobaciones de salud y sondas de disponibilidad | MUST | INF-06 | 1 |
| OBS-03 | Métricas básicas | SHOULD | INF-06 | 7 |
| OBS-04 | Trazabilidad completa de una transferencia | SHOULD | OBS-01 | 7 |
| QA-01 | Pruebas unitarias de la lógica de negocio | MUST | — | Continuo |
| QA-02 | Pruebas de integración con contenedores efímeros | MUST | ACC-07 | 3 |
| QA-03 | Prueba de transferencias concurrentes | MUST | ACC-07 | 3 |
| QA-04 | Prueba de idempotencia | MUST | TRX-02 | 3 |
| QA-05 | Prueba de evento duplicado | MUST | KAF-02 | 4 |
| QA-06 | Pruebas de contrato de eventos | SHOULD | KAF-01 | 4 |
| QA-07 | Pruebas de seguridad | MUST | SEC-03 | 7 |
| QA-09 | Prueba de acceso desde dispositivo no vinculado | MUST | IDN-03 | 2 |
| QA-08 | Colección de peticiones para prueba manual | SHOULD | INF-07 | 5 |
| DOC-01 | Documentación de interfaces en todos los servicios | MUST | INF-06 | Continuo |
| DOC-02 | Registros de decisión de arquitectura | MUST | — | Continuo |
| DOC-03 | Diagramas de contexto y contenedores | SHOULD | — | 8 |
| DOC-04 | Memoria final y evidencias de prueba | MUST | — | 8 |

---

# 7. Parte técnica

## 7.1. Microservicios

**Tecnología:** Java sobre **Spring Boot**.

| Componente de Spring | Función dentro de la arquitectura |
|---|---|
| Spring Boot | Base de los siete microservicios: configuración, arranque y empaquetado |
| Spring Web | Exposición de los endpoints REST de cada servicio |
| Spring Security | Validación de tokens y aplicación de las reglas de autorización por rol y por permiso |
| Spring Security OAuth2 Resource Server | Configuración de cada servicio como consumidor de tokens emitidos por el proveedor de identidad |
| Spring Security OAuth2 Client | Obtención y renovación automática de los tokens de servicio para las llamadas internas |
| Spring Cloud Gateway | Enrutamiento, filtros y limitación de tasa en el punto de entrada |
| Spring Data JPA | Acceso a la base de datos de cada servicio mediante entidades y repositorios |
| Spring for Apache Kafka | Producción y consumo de eventos, gestión de reintentos y derivación a colas de mensajes fallidos |
| Spring Data Redis | Acceso a los contadores y claves temporales |
| Spring Boot Mail | Envío de las notificaciones por correo |
| Spring Scheduling | Ejecución periódica del publicador de la tabla de salida y del proceso de reconciliación |
| Spring Boot Actuator | Comprobaciones de salud, sondas de disponibilidad y exposición de métricas |
| Resilience4j | Tiempos límite, reintentos y cortocircuito en las llamadas entre servicios |
| Flyway | Control de versiones del esquema de base de datos de cada servicio |
| springdoc-openapi | Generación automática de la documentación de las interfaces |

**Versión de Java:** 25. **Versión de Spring Boot:** pendiente de decidir entre la rama 3.5 y la rama 4.1, según se indica en 2.23.

## 7.2. Aplicación móvil

**Tecnología:** desarrollo **nativo de Android en Kotlin, con Jetpack Compose**.

| Elemento | Función |
|---|---|
| Kotlin | Lenguaje de la aplicación |
| Jetpack Compose | Construcción de la interfaz de usuario de forma declarativa |
| SDK de Auth0 para Android | Comunicación con el proveedor de identidad para el inicio de sesión y la renovación de tokens |
| Identificador de instalación del dispositivo | Valor que la aplicación extrae automáticamente y envía en cada inicio de sesión para comprobar la vinculación |
| BiometricPrompt | Presentación del diálogo de autenticación biométrica del sistema, equivalente en Android a lo que en dispositivos de Apple se denomina Face ID |
| Android Keystore | Custodia de la clave criptográfica que protege el token de renovación, con exigencia de autenticación del usuario |
| EncryptedSharedPreferences | Almacenamiento cifrado del token de renovación |
| Retrofit y OkHttp | Cliente de red hacia el punto de entrada del sistema |
| ViewModel | Conservación del estado de la pantalla, incluida la clave de idempotencia de la transferencia |

**Función dentro de la arquitectura.** Es el cliente principal del sistema. Se comunica exclusivamente con el punto de entrada mediante HTTP/REST sobre tráfico cifrado, y con el proveedor de identidad para obtener y renovar tokens. No accede nunca a un microservicio interno.

## 7.3. Panel de administración

**Tecnología:** pendiente de decidir entre dos alternativas, con los mismos endpoints en ambos casos.

| Alternativa | Descripción |
|---|---|
| Vista renderizada en servidor | Aplicación Spring Boot con motor de plantillas, que consume directamente los endpoints administrativos |
| Aplicación de página única con capa intermedia | Interfaz construida con un framework de JavaScript, servida junto a una capa intermedia que adapta las respuestas del backend a las necesidades de la pantalla |

**Función dentro de la arquitectura.** Herramienta de operación y de demostración. Consume los endpoints protegidos por rol de administrador a través del punto de entrada. Permite consultar usuarios, cuentas y transferencias, acreditar saldo, bloquear cuentas, revisar operaciones retenidas, ajustar las reglas de detección de fraude, consultar el libro contable y ejecutar la reconciliación.

## 7.4. Identidad y seguridad

**Tecnología:** **Auth0** como proveedor de identidad, con los estándares **OAuth 2.0**, **OpenID Connect** y **JWT**. La validación de la credencial reside en el Identity Service.

| Elemento | Función |
|---|---|
| Auth0 | Emisión, firma y renovación de tokens, gestión de roles y custodia de las claves de firma |
| Conexión de base de datos personalizada | Mecanismo por el que Auth0 delega en el Identity Service la comprobación del PIN y del dispositivo vinculado |
| Acción de Auth0 | Inyección del rol del usuario como afirmación personalizada del token |
| PIN con resumen criptográfico y sal | Credencial de acceso del usuario, verificada siempre en el servidor y nunca almacenada en claro |
| Vinculación de dispositivo | Segundo factor implícito: el acceso solo se concede desde el dispositivo registrado |
| Código de verificación por SMS | Autorización del cambio de dispositivo, con caducidad de quince minutos y cinco intentos |
| OpenID Connect | Capa de identidad sobre OAuth 2.0, que aporta el token de identidad y el punto de descubrimiento de la configuración |
| JWT firmado con algoritmo asimétrico | Formato de los tokens; los servicios validan la firma con la clave pública publicada por el proveedor |
| Paso de token entre servicios | Reenvío del token del usuario en las llamadas internas, en lugar de un segundo mecanismo de autenticación |
| Audiencia del token | Indicación de a qué destinatario va dirigido, para impedir su reutilización indebida |
| Android Keystore y BiometricPrompt | Desbloqueo local del token de renovación, sin que ningún dato biométrico salga del dispositivo |
| Cifrado del tráfico con certificado autofirmado | Protección del canal entre los clientes y el punto de entrada |

## 7.5. Persistencia

**Tecnología:** **PostgreSQL**.

| Elemento | Función |
|---|---|
| PostgreSQL | Base de datos de los seis servicios con persistencia |
| Una base de datos y un rol por servicio | Garantía de propiedad de datos mediante permisos, no mediante convención |
| Tipo numérico exacto de precisión fija | Representación de todos los importes monetarios |
| Bloqueo pesimista de filas | Control de concurrencia durante la ejecución de una transferencia |
| Restricciones de validación y de unicidad | Garantías de corrección a nivel de motor de base de datos |
| Permisos restringidos sin modificación ni borrado | Inmutabilidad del libro contable |
| Tipo de dato de documento estructurado | Almacenamiento del contenido de eventos y de datos auxiliares de notificaciones |
| Flyway | Versionado y aplicación de las migraciones de esquema |

## 7.6. Mensajería

**Tecnología:** **Apache Kafka** en modo autónomo sin coordinador externo.

| Elemento | Función |
|---|---|
| Apache Kafka | Transporte de los eventos de dominio entre servicios |
| Cuatro topics de dominio | Separación de los eventos por servicio productor |
| Colas de mensajes fallidos | Aislamiento de los mensajes que no pueden procesarse |
| Grupos de consumidores | Un grupo por servicio consumidor |
| Clave de partición | Garantía de orden de los eventos de una misma operación |
| Confirmación manual del consumo | Confirmación únicamente después de haber persistido el efecto |
| Formato JSON con sobre común | Contrato de los eventos, sin registro de esquemas ni módulo compartido |

**Comunicaciones que utilizan Kafka:** Transaction hacia Ledger, Notification y Fraud; Account hacia Ledger y Notification; Fraud hacia Notification; Identity hacia Notification.

## 7.7. Almacenamiento auxiliar

**Tecnología:** **Redis**.

| Uso | Servicio | Contenido |
|---|---|---|
| Limitación de tasa | Gateway | Contadores por usuario y por origen |
| Idempotencia | Transaction | Reserva rápida de la clave, con respaldo persistente en base de datos |
| Contadores de velocidad | Fraud | Número e importe acumulado de operaciones en una ventana temporal |
| Intentos de acceso | Identity | Contador de intentos fallidos y bloqueo temporal |
| Caché opcional | Account | Datos no financieros |

**Restricción:** Redis no es fuente de verdad de ningún dato financiero.

## 7.8. Comunicación entre componentes

| Tramo | Mecanismo | Motivo |
|---|---|---|
| Aplicación móvil → Punto de entrada | HTTP/REST sobre tráfico cifrado | Es la interfaz pública del sistema |
| Panel de administración → Punto de entrada | HTTP/REST sobre tráfico cifrado | Idéntico motivo |
| Aplicación móvil → Proveedor de identidad | HTTP/REST | Obtención y renovación de tokens |
| Punto de entrada → Microservicios | HTTP/REST | Enrutamiento de peticiones de usuario |
| Transaction → Account | HTTP/REST | Se necesita la respuesta para continuar |
| Transaction → Fraud | HTTP/REST | Se necesita el veredicto antes de mover el dinero |
| Fraud → Account, Identity y Transaction | HTTP/REST | Consulta de contexto durante la evaluación |
| Ledger → Account | HTTP/REST | Consulta de saldos durante la reconciliación |
| Identity → Proveedor de identidad | HTTP/REST | Creación del usuario durante el registro |
| Transaction, Account, Identity, Fraud → Consumidores | **Kafka** | El hecho ya ocurrió; los consumidores reaccionan después |
| Notification → Servidor de correo | SMTP | Envío de las notificaciones por correo |

## 7.9. Infraestructura y ejecución

| Elemento | Tecnología | Función |
|---|---|---|
| Contenedores | Docker | Empaquetado de cada componente |
| Orquestación local | Docker Compose | Ejecución del sistema completo con un único comando |
| Descubrimiento de servicios | DNS interno de Docker | Resolución de los nombres de los servicios |
| Captura de correo | Mailhog | Recepción y visualización de las notificaciones por correo, sin envío real |
| Repositorio | Monorepo con desarrollo basado en tronco | Organización del código del backend |
| Integración continua | GitHub Actions | Compilación y pruebas en cada solicitud de incorporación |

## 7.10. Calidad y observabilidad

| Elemento | Tecnología | Función |
|---|---|---|
| Pruebas unitarias | JUnit 5, Mockito, AssertJ | Verificación de la lógica de dominio |
| Pruebas de integración | Spring Boot Test y Testcontainers | Verificación contra bases de datos y mensajería reales |
| Pruebas de interfaz y seguridad | MockMvc y Spring Security Test | Verificación de contratos y de reglas de autorización |
| Registros | Logback con salida estructurada | Registro con identificador de correlación en cada línea |
| Contexto de diagnóstico | MDC de SLF4J | Propagación del identificador de correlación a todas las líneas de una operación |
| Métricas | Micrometer con Actuator | Transferencias por estado, latencia de la evaluación de riesgo, tamaño de la tabla de salida |
| Documentación de interfaces | OpenAPI mediante springdoc | Especificación consultable, agregada en el punto de entrada |

## 7.11. Resumen de tecnologías por componente

| Componente | Tecnología principal | Almacenamiento | Comunicación |
|---|---|---|---|
| Aplicación móvil | Kotlin y Jetpack Compose | Almacén de claves cifrado del dispositivo | REST hacia el punto de entrada |
| Panel de administración | Pendiente de decidir | Ninguno | REST hacia el punto de entrada |
| API Gateway | Spring Cloud Gateway | Redis | REST |
| Identity Service | Spring Boot | PostgreSQL | REST y Kafka como productor |
| Account Service | Spring Boot | PostgreSQL y Redis | REST y Kafka como productor |
| Transaction Service | Spring Boot | PostgreSQL y Redis | REST y Kafka como productor |
| Ledger Service | Spring Boot | PostgreSQL | Kafka como consumidor y REST de solo lectura |
| Fraud Service | Spring Boot | PostgreSQL y Redis | REST y Kafka en ambos sentidos |
| Notification Service | Spring Boot | PostgreSQL | Kafka como consumidor, REST y SMTP |
| Proveedor de identidad | Auth0 (servicio externo en la nube) | Propio del proveedor | REST |
| Mensajería | Apache Kafka | Registro propio | Protocolo propio |
| Correo de desarrollo | Mailhog | Memoria | SMTP |

---

# 8. Diccionario de términos

## 8.1. Términos de negocio bancario

**KYC (Know Your Customer, «conoce a tu cliente»).**
Conjunto de procedimientos por los que una entidad financiera verifica la identidad de sus clientes antes de permitirles operar. Existe para prevenir el blanqueo de capitales y en Perú lo regula la Superintendencia de Banca, Seguros y AFP. *Relación con el sistema:* **queda fuera de alcance**. El término se recoge aquí porque forma parte del vocabulario del dominio y porque la exclusión es una decisión consciente, no un olvido: el sistema asume que todo usuario registrado está habilitado para operar.

**Ledger (libro contable o libro mayor).**
Registro histórico e inmutable de todos los movimientos financieros del sistema. No es un simple historial: cada movimiento se registra como un asiento formalmente correcto. *Relación con el sistema:* es la responsabilidad de Ledger Service, que lo construye consumiendo eventos y no participa en el camino crítico de la transferencia.

**Partida doble.**
Método contable según el cual toda operación se registra al menos dos veces, una al debe y otra al haber, de forma que la suma de ambas coincida siempre. Es el fundamento de la contabilidad desde el siglo XV. *Relación con el sistema:* una transferencia de cien soles genera un asiento con dos líneas, un cargo de cien en la cuenta origen y un abono de cien en la cuenta destino. Ledger Service valida esta igualdad en cada asiento.

**Asiento contable.**
Registro individual de una operación en el libro contable. Está compuesto por varias líneas cuya suma al debe iguala la suma al haber. *Relación con el sistema:* es la entidad `journal_entry` de Ledger Service, y cada transferencia completada genera exactamente uno.

**Debe y haber (débito y crédito).**
Los dos sentidos en que puede registrarse un movimiento contable. En una cuenta de cliente, el debe reduce el saldo disponible y el haber lo aumenta. *Relación con el sistema:* corresponde al campo `direction` de las líneas de asiento y de los movimientos de cuenta.

**Cuenta de sistema.**
Cuenta contable que no pertenece a ningún usuario y actúa como contrapartida de las operaciones cuyo dinero entra o sale del sistema. *Relación con el sistema:* `SYSTEM_CASH` es la contrapartida de los saldos iniciales y de las acreditaciones administrativas; sin ella, esas operaciones romperían la partida doble porque el dinero no procedería de ninguna cuenta de cliente.

**Contra-asiento.**
Asiento de sentido contrario que anula el efecto de otro anterior. *Relación con el sistema:* es la única forma admitida de corregir un error contable, porque los asientos originales no pueden modificarse.

**Saldo materializado.**
Saldo almacenado en una columna, precalculado, en lugar de obtenerse sumando todos los movimientos en cada consulta. *Relación con el sistema:* es la columna `balance` de Account Service. Se emplea por rendimiento, y su coherencia con los movimientos se verifica mediante pruebas y mediante la reconciliación.

**Reconciliación.**
Proceso que compara dos fuentes de información que deberían coincidir y detecta las discrepancias. *Relación con el sistema:* compara el saldo que Account Service declara para cada cuenta con la suma de los asientos que el libro contable tiene registrados. Es lo que convierte la consistencia eventual en una propiedad verificable en lugar de una afirmación.

**Score o puntuación de riesgo.**
Valor numérico, entre cero y cien, que expresa la probabilidad de que una operación sea fraudulenta. Se obtiene sumando los pesos de las reglas que se dispararon. *Relación con el sistema:* Fraud Service lo calcula en cada evaluación y lo traduce a un nivel bajo, medio o alto, que determina si la operación se aprueba, se retiene o se rechaza.

**Regla de velocidad.**
Regla de detección de fraude que cuenta cuántas operaciones, o qué importe acumulado, ha realizado un usuario dentro de una ventana temporal. *Relación con el sistema:* se apoya en contadores almacenados en Redis con expiración automática, porque son datos efímeros de alta frecuencia de escritura.

## 8.2. Términos de arquitectura y sistemas distribuidos

**Microservicio.**
Componente autónomo que implementa una capacidad de negocio concreta, es propietario exclusivo de sus datos y se despliega de forma independiente. *Relación con el sistema:* el proyecto se compone de siete.

**Idempotencia.**
Propiedad por la cual ejecutar una operación varias veces produce el mismo resultado que ejecutarla una sola. *Relación con el sistema:* es la garantía de que una transferencia no se duplica aunque el usuario pulse dos veces, la red falle o un evento se entregue repetido. Se implementa en tres capas independientes.

**Clave de idempotencia.**
Identificador único que el cliente genera antes de enviar una petición y envía en una cabecera, para que el servidor pueda reconocer un reintento y devolver la respuesta original en lugar de ejecutar de nuevo la operación. *Relación con el sistema:* la genera la aplicación móvil y es obligatoria al crear una transferencia. Debe generarse una sola vez y conservarse en el estado de la pantalla.

**Transactional Outbox (tabla de salida transaccional).**
Patrón que resuelve la imposibilidad de escribir en una base de datos y publicar en un sistema de mensajería de forma atómica. El evento se guarda en una tabla dentro de la misma transacción que el cambio de estado, y un proceso independiente lo publica después. *Relación con el sistema:* lo aplican Transaction, Account e Identity. Es lo que garantiza que ningún asiento contable se pierda si la mensajería está caída.

**Saga.**
Patrón que coordina una operación que abarca varios servicios mediante una secuencia de pasos con acciones de compensación para deshacer los ya realizados si uno falla. *Relación con el sistema:* se evaluó y se **descartó explícitamente**, porque ambas cuentas de una transferencia viven en el mismo servicio y en la misma base de datos, de modo que basta una transacción local.

**ACID.**
Conjunto de propiedades de una transacción de base de datos: atomicidad, consistencia, aislamiento y durabilidad. Atomicidad significa que la operación se aplica entera o no se aplica en absoluto. *Relación con el sistema:* el débito y el crédito de una transferencia se ejecutan dentro de una única transacción con estas propiedades.

**Consistencia eventual.**
Situación en la que dos fuentes de información no coinciden en un instante dado pero convergen con el tiempo. *Relación con el sistema:* describe la relación entre el saldo de Account Service y los asientos del libro contable. Se acepta como diseño, y se verifica mediante la reconciliación.

**Entrega al menos una vez (at-least-once).**
Garantía de un sistema de mensajería según la cual un mensaje llega al menos una vez, pudiendo llegar repetido. *Relación con el sistema:* implica que los eventos duplicados son el comportamiento esperado, no una anomalía, y obliga a que todos los consumidores sean idempotentes.

**Cortocircuito (circuit breaker).**
Mecanismo que deja de invocar a un servicio que está fallando de forma sostenida, evitando acumular esperas inútiles, y reintenta pasado un tiempo. *Relación con el sistema:* protege las llamadas de Transaction hacia Account y Fraud.

**Espera creciente (backoff exponencial).**
Estrategia de reintento en la que el tiempo de espera se duplica en cada intento sucesivo. *Relación con el sistema:* se aplica a los consumidores de eventos y a las llamadas internas que pueden reintentarse.

**Fail-open y fail-closed.**
Dos políticas opuestas para actuar cuando un control de seguridad no puede evaluarse. La primera permite la operación priorizando la disponibilidad; la segunda la bloquea priorizando la seguridad. *Relación con el sistema:* si Fraud Service no responde, se aplica la primera por debajo de un umbral de importe y la segunda por encima.

**Identificador de correlación.**
Valor único que se genera al recibir una petición y acompaña a esa operación a través de todos los servicios, tanto en las llamadas como en los mensajes. *Relación con el sistema:* permite reconstruir el recorrido completo de una transferencia buscando un único valor en los registros de todos los servicios.

**Copia congelada (snapshot).**
Valor copiado en el momento en que ocurre un hecho, en lugar de una referencia que se resolvería en el futuro. *Relación con el sistema:* el número de cuenta de la contraparte en un movimiento, y el peso y umbral de una regla de fraude en una evaluación. Existen porque un registro histórico debe seguir siendo interpretable aunque los datos originales cambien.

## 8.3. Términos de seguridad e identidad

**OAuth 2.0.**
Estándar que permite a una aplicación obtener acceso limitado a un recurso en nombre de un usuario, sin manejar sus credenciales. *Relación con el sistema:* es la base del mecanismo de autenticación del proyecto.

**OpenID Connect.**
Capa de identidad construida sobre OAuth 2.0, que añade el token de identidad y el punto de descubrimiento de la configuración del proveedor. *Relación con el sistema:* es lo que permite que los microservicios obtengan automáticamente las claves públicas para validar las firmas.

**Proveedor de identidad.**
Servicio responsable de autenticar a los usuarios y emitir los tokens que acreditan esa autenticación. *Relación con el sistema:* el proyecto emplea Auth0, un proveedor gestionado en la nube; la plataforma no custodia claves de firma propias.

**JWT (JSON Web Token).**
Formato de token compuesto por una cabecera, un cuerpo con afirmaciones y una firma. Al estar firmado, cualquier servicio puede verificar su autenticidad sin consultar al emisor. *Relación con el sistema:* es el formato de todos los tokens que circulan por la plataforma.

**Claim (afirmación).**
Cada uno de los datos contenidos en el cuerpo de un token: quién es el sujeto, quién lo emitió, cuándo expira, a quién va dirigido y qué rol tiene. *Relación con el sistema:* el sistema utiliza el sujeto, el emisor, la expiración, la audiencia y el rol.

**`sub` (subject, sujeto).**
Afirmación del token que identifica de forma única al titular. *Relación con el sistema:* es el único origen admitido del identificador de usuario. **Nunca se toma del cuerpo de la petición.**

**PIN.**
Credencial numérica corta que el usuario introduce para autenticarse. *Relación con el sistema:* sustituye a la contraseña como credencial de acceso. Se valida siempre en el servidor y se almacena únicamente como resumen criptográfico con sal. Su brevedad obliga a acompañarlo de la vinculación de dispositivo y de un límite de intentos.

**Conexión de base de datos personalizada.**
Mecanismo del proveedor de identidad que le permite delegar la comprobación de credenciales en un sistema externo, en lugar de mantenerlas él mismo. *Relación con el sistema:* es la pieza que hace posible que Auth0 emita los tokens mientras el PIN y el dispositivo se verifican en el Identity Service. Implica que el proveedor, alojado en la nube, debe poder alcanzar el entorno local.

**Vinculación de dispositivo (device binding).**
Asociación de una cuenta a un terminal concreto, de modo que el acceso desde cualquier otro requiera una verificación adicional. *Relación con el sistema:* cada usuario tiene un único dispositivo vinculado, y su posesión actúa como segundo factor implícito frente a la debilidad de un PIN corto.

**Autenticación reforzada (step-up).**
Exigencia de una comprobación adicional cuando una operación presenta más riesgo que la actividad habitual del usuario. *Relación con el sistema:* se aplica en el cambio de dispositivo, que exige correo, PIN y un código recibido por SMS.

**Código de un solo uso.**
Valor de corta vigencia que se envía por un canal distinto del habitual y se consume en un único intento válido. *Relación con el sistema:* autoriza el cambio de dispositivo, caduca a los quince minutos y admite cinco intentos antes de invalidarse.

**Token de acceso y token de renovación.**
El primero es de vida corta y se envía en cada petición; el segundo es de vida larga y sirve para obtener uno nuevo cuando el primero expira. *Relación con el sistema:* el token de renovación es el que se guarda cifrado en el dispositivo y se desbloquea con biometría.

**Paso de token (token passthrough).**
Práctica consistente en que un servicio reenvíe a otro el mismo token que recibió, en lugar de obtener uno propio. *Relación con el sistema:* es el mecanismo de autenticación entre microservicios, y hace que la identidad del usuario que originó la petición se conserve a lo largo de toda la cadena.

**Rol.**
Atributo del sujeto que expresa **qué es** un usuario dentro del sistema. *Relación con el sistema:* se emplean dos, usuario y administrador. Se definen en el proveedor de identidad, viajan en el token como afirmación personalizada y se replican en una tabla local a efectos de consulta.

**Audiencia (`aud`).**
Afirmación del token que indica a qué destinatario va dirigido. *Relación con el sistema:* cada servicio comprueba que su propio nombre figura en ese campo, de modo que un token no pueda reutilizarse fuera del ámbito para el que se emitió.

**Resource Server (servidor de recursos).**
Componente que recibe peticiones acompañadas de un token, lo valida y decide si autoriza la operación. *Relación con el sistema:* cada uno de los siete componentes está configurado como tal, incluso los internos.

**JWKS (conjunto de claves web en formato JSON).**
Documento publicado por el proveedor de identidad que contiene las claves públicas con las que verificar las firmas de los tokens. *Relación con el sistema:* permite que cada servicio valide los tokens por sí mismo, sin consultar al proveedor en cada petición. Lo publica Auth0, que es también quien gestiona la rotación de claves.

**`kid` (identificador de clave).**
Valor de la cabecera del token que indica con qué clave se firmó. *Relación con el sistema:* permite que un servicio seleccione la clave pública correcta cuando el proveedor mantiene varias vigentes durante una rotación.

**Principio de mínimo privilegio.**
Regla según la cual cada componente recibe únicamente los permisos que necesita, y ninguno más. *Relación con el sistema:* se aplica en las listas de control de acceso de la mensajería, en las del almacenamiento auxiliar y en los permisos de base de datos de cada servicio.

**Defensa en profundidad.**
Estrategia consistente en establecer varias capas de control, de modo que la superación de una no comprometa el sistema. *Relación con el sistema:* el punto de entrada valida el token y los servicios lo vuelven a validar; la red interna nunca se considera una credencial.

**Autorización por propiedad del recurso.**
Comprobación de que el recurso solicitado pertenece efectivamente al usuario identificado en el token. Su ausencia constituye la vulnerabilidad más explotada en interfaces de programación financieras. *Relación con el sistema:* antes de cualquier operación se verifica que la cuenta origen pertenece al sujeto del token.

**BiometricPrompt.**
Interfaz del sistema operativo Android que presenta el diálogo estándar de autenticación biométrica. Devuelve a la aplicación únicamente un resultado afirmativo o negativo; la plantilla biométrica nunca sale del hardware seguro del dispositivo. *Relación con el sistema:* es el equivalente en Android de lo que en dispositivos de Apple se denomina Face ID, y es el mecanismo que desbloquea el token de renovación guardado en el dispositivo.

**Android Keystore.**
Almacén de claves criptográficas respaldado por hardware seguro del dispositivo. Las claves pueden configurarse para exigir autenticación del usuario antes de ser utilizables. *Relación con el sistema:* custodia la clave que cifra el token de renovación, y se configura para invalidarse si se registra una nueva huella o rostro en el dispositivo.

## 8.4. Términos de mensajería y Kafka

**Evento.**
Registro de que algo ya ocurrió. Se expresa en pasado y no admite rechazo por parte de quien lo recibe. *Relación con el sistema:* se distinguen de las llamadas REST, que solicitan una acción y pueden fallar.

**Topic.**
Canal con nombre al que se publican los mensajes y del que se suscriben los consumidores. *Relación con el sistema:* se emplean cuatro, uno por servicio productor, más una cola de mensajes fallidos por cada uno.

**Productor y consumidor.**
Quien publica mensajes en un topic y quien los lee, respectivamente. *Relación con el sistema:* Transaction, Account, Identity y Fraud producen; Ledger, Notification y Fraud consumen.

**Grupo de consumidores.**
Conjunto de instancias que se reparten la lectura de un topic, de forma que cada mensaje lo procesa una sola de ellas. *Relación con el sistema:* cada servicio consumidor tiene su propio grupo, lo que permite que varios servicios reciban el mismo evento de forma independiente.

**Partición.**
Subdivisión de un topic. El orden de los mensajes solo está garantizado dentro de cada partición, no en el conjunto del topic. *Relación con el sistema:* cada topic se divide en tres particiones.

**Clave de partición.**
Valor que determina a qué partición va cada mensaje. Todos los mensajes con la misma clave acaban en la misma partición y por tanto se procesan en orden. *Relación con el sistema:* la clave de los eventos de transacción es el identificador de la transferencia, lo que garantiza que sus eventos se procesen en el orden en que se produjeron.

**Offset (desplazamiento).**
Posición de un mensaje dentro de una partición. Cada grupo de consumidores lleva la cuenta de hasta dónde ha leído. *Relación con el sistema:* la confirmación del desplazamiento se realiza manualmente, únicamente después de haber persistido el efecto del mensaje.

**Dead Letter Topic (cola de mensajes fallidos).**
Topic al que se envían los mensajes que no han podido procesarse tras agotar los reintentos. *Relación con el sistema:* existe uno por cada topic principal, y su contenido se muestra en el panel de administración. Una cola de mensajes fallidos que nadie revisa no cumple ninguna función.

**KRaft.**
Modo de funcionamiento de Kafka que prescinde del coordinador externo que se utilizaba tradicionalmente, reduciendo el número de componentes necesarios. *Relación con el sistema:* es el modo elegido, y permite ejecutar la mensajería con un único contenedor.

**Sobre del evento.**
Estructura común que envuelve el contenido de todos los eventos e incluye identificador, tipo, versión, momento, identificadores de correlación y de causalidad, y servicio productor. *Relación con el sistema:* permite tratar de forma uniforme la deduplicación, la trazabilidad y la evolución de los contratos.

**Lectura tolerante.**
Práctica consistente en que un consumidor ignore los campos que no conoce, de modo que un productor pueda añadir información sin romper a nadie. *Relación con el sistema:* es una de las tres medidas que sustituyen al módulo de código compartido.

## 8.5. Términos de base de datos

**Tipo numérico exacto de precisión fija.**
Tipo de dato que representa valores decimales sin error de redondeo, a diferencia de los tipos de coma flotante. *Relación con el sistema:* todos los importes monetarios lo utilizan, con diecinueve dígitos de precisión y cuatro decimales.

**Bloqueo pesimista.**
Técnica que reserva una fila en exclusiva durante una transacción, de modo que ninguna otra pueda modificarla hasta que la primera termine. *Relación con el sistema:* se aplica sobre las dos cuentas implicadas en una transferencia, y es lo que garantiza la corrección bajo concurrencia.

**Bloqueo optimista.**
Técnica alternativa que no reserva la fila, sino que detecta al guardar si alguien la modificó entre tanto, mediante un contador de versión. *Relación con el sistema:* se emplea en las entidades que no manejan dinero.

**Interbloqueo (deadlock).**
Situación en la que dos transacciones se bloquean mutuamente porque cada una espera un recurso que la otra retiene. *Relación con el sistema:* se evita adquiriendo siempre los bloqueos en el mismo orden, determinado por el identificador de cuenta y no por cuál es origen y cuál destino.

**Restricción de validación (`CHECK`).**
Condición que la base de datos verifica antes de aceptar una fila. *Relación con el sistema:* impide que el saldo de una cuenta quede en negativo, incluso ante un error de lógica en la aplicación.

**Restricción de unicidad.**
Condición que impide que se repita un valor o una combinación de valores. *Relación con el sistema:* es el mecanismo que sostiene la corrección en cuatro puntos distintos: movimientos de cuenta, registros de idempotencia, asientos contables y notificaciones.

**Índice parcial.**
Índice que solo cubre las filas que cumplen una condición. *Relación con el sistema:* el índice sobre las filas pendientes de publicación de la tabla de salida, que mantiene instantánea la consulta del publicador por mucho que crezca la tabla histórica.

**Migración de esquema.**
Cambio versionado en la estructura de la base de datos, aplicado de forma controlada y reproducible. *Relación con el sistema:* cada servicio gestiona las suyas con Flyway, y la validación del esquema se configura en modo estricto para que la aplicación no modifique tablas por su cuenta.

**GRANT (concesión de permisos).**
Instrucción que otorga a un rol de base de datos la capacidad de realizar determinadas operaciones. *Relación con el sistema:* es lo que garantiza materialmente la propiedad de datos entre servicios y la inmutabilidad del libro contable, en lugar de dejarlo a la disciplina del equipo.

## 8.6. Términos de desarrollo y calidad

**Monorepo.**
Repositorio único que contiene el código de varios componentes. *Relación con el sistema:* aloja el backend completo. La aplicación móvil reside en un repositorio aparte.

**Desarrollo basado en tronco (trunk-based development).**
Práctica en la que todo el trabajo se integra frecuentemente en una única rama principal, mediante ramas de vida muy corta. *Relación con el sistema:* las ramas tienen una vida máxima de dos o tres días, para evitar la integración tardía.

**Testcontainers.**
Biblioteca que levanta contenedores reales de base de datos o mensajería durante la ejecución de las pruebas. *Relación con el sistema:* permite verificar el comportamiento contra PostgreSQL y Kafka reales en lugar de contra sustitutos en memoria, lo que resulta imprescindible para probar el bloqueo de filas y las restricciones de la base de datos.

**Prueba de contrato.**
Prueba que verifica que un consumidor sigue siendo capaz de interpretar los mensajes que produce un productor. *Relación con el sistema:* cada consumidor deserializa un ejemplo versionado guardado en la carpeta de contratos; si un productor rompe el formato, la prueba falla en la integración continua.

**ADR (registro de decisión de arquitectura).**
Documento breve que recoge una decisión estructural, su contexto, las alternativas consideradas y sus consecuencias. *Relación con el sistema:* se redacta uno por cada decisión de la sección 2, en el momento en que se toma y no al final del proyecto.

**BFF (backend for frontend, capa intermedia para el cliente).**
Componente que adapta las respuestas del backend a las necesidades específicas de una interfaz concreta, agrupando varias llamadas en una. *Relación con el sistema:* es una de las dos alternativas consideradas para el panel de administración.

**Actuator.**
Módulo de Spring Boot que expone información operativa de la aplicación: estado de salud, métricas y sondas de disponibilidad. *Relación con el sistema:* sus comprobaciones de salud las utiliza la orquestación de contenedores para no arrancar un servicio antes de que sus dependencias estén listas.

**MDC (contexto de diagnóstico asignado).**
Mecanismo del sistema de registro que asocia datos al hilo de ejecución para que aparezcan automáticamente en todas las líneas emitidas durante una operación. *Relación con el sistema:* es lo que hace que el identificador de correlación aparezca en cada línea de registro sin necesidad de pasarlo explícitamente.

**Vertical slice (corte vertical).**
Forma de organizar el trabajo consistente en completar una funcionalidad de extremo a extremo, atravesando todas las capas, en lugar de construir una capa completa antes de pasar a la siguiente. *Relación con el sistema:* es el criterio de planificación de los sprints, y el motivo de que el primer entregable sea un inicio de sesión funcionando de extremo a extremo.
