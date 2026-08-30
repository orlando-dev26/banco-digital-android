<!--
Sync Impact Report
- Version change: unratified scaffold -> 1.0.0
- Modified principles:
  - Placeholder Principle 1 -> I. Desarrollo guiado por especificaciones y trazabilidad
  - Placeholder Principle 2 -> II. Arquitectura Android MVVM y flujo unidireccional
  - Placeholder Principle 3 -> III. Seguridad, privacidad y autenticación
  - Placeholder Principle 4 -> IV. Sistema de diseño, UX y accesibilidad
  - Placeholder Principle 5 -> V. Contratos, datos y resiliencia
- Added principles:
  - VI. Calidad y pruebas
  - VII. Git, revisión y alcance
- Added sections:
  - Restricciones del proyecto y límites de decisión
  - Flujo de desarrollo y puertas de calidad
- Removed sections: None; placeholder sections were resolved into project-specific sections.
- Template synchronization:
  - .specify/templates/spec-template.md: inspected; no update required.
  - .specify/templates/plan-template.md: inspected; no update required because Constitution Check
    gates are resolved from this file at runtime.
  - .specify/templates/tasks-template.md: inspected; no update required.
  - .specify/templates/checklist-template.md: inspected; no update required.
- Follow-up TODOs: None. Team-dependent decisions remain explicitly marked NEEDS CLARIFICATION.
-->

# Banca Digital Android Constitution

## Core Principles

### I. Desarrollo guiado por especificaciones y trazabilidad

1. Todo comportamiento nuevo de producción MUST partir de una especificación aprobada que defina
   qué se necesita, por qué se necesita, historias de usuario, criterios de aceptación, casos límite
   y exclusiones.
2. El flujo normal MUST ser: constitution -> specify -> clarify -> plan -> tasks -> analyze ->
   implement -> converge.
3. La especificación MUST definir el qué y el porqué. Las tecnologías, clases, dependencias y
   detalles de implementación MUST pertenecer al plan técnico.
4. Cada cambio MUST ser trazable entre Jira, su directorio de especificación, tareas, commits y
   Pull Request.
5. Toda inconsistencia entre Jira, Figma, contratos del backend, documentos o código MUST
   registrarse como NEEDS CLARIFICATION. El agente MUST NOT resolverla inventando comportamiento.
6. Las fuentes aprobadas y más recientes MUST prevalecer sobre documentos obsoletos. Keycloak
   MUST NOT tratarse como parte de la solución actual; el proveedor confirmado es Auth0.
7. El código existente MAY aportar contexto del sistema, pero MUST NOT considerarse automáticamente
   una especificación válida.

Rationale: este principio evita comportamiento no documentado, separa la intención del producto de
la implementación y permite auditar cada cambio de producción de extremo a extremo.

### II. Arquitectura Android MVVM y flujo unidireccional

1. La aplicación MUST usar Kotlin, Jetpack Compose, MVVM y flujo unidireccional de datos.
2. Cada pantalla no trivial MUST tener un contrato explícito compuesto por estado inmutable, eventos
   o acciones tipadas de UI y efectos de una sola ejecución cuando correspondan.
3. El ViewModel MUST exponer un `UiState` inmutable mediante `StateFlow` y MUST recibir eventos
   tipados desde la UI.
4. Los composables MUST limitarse a renderizar estado y emitir eventos. MUST NOT contener llamadas
   de red, persistencia, autenticación ni reglas de negocio.
5. El estado MUST elevarse al propietario apropiado y MUST existir una única fuente de verdad para
   cada dato.
6. La recolección de flujos en Compose MUST respetar el ciclo de vida de Android.
7. La estructura inicial MUST ser package-by-feature dentro del módulo `app`.
8. El proyecto MUST permanecer en un único módulo Gradle mientras su tamaño no justifique la
   modularización. Toda modularización futura MUST contar con un plan o ADR que explique límites,
   beneficios y coste.
9. El acceso remoto o local MUST estar detrás de repositorios. Los modelos DTO, de dominio y de UI
   MUST NOT mezclarse sin una justificación explícita en el plan técnico.
10. La capa de dominio y los casos de uso SHOULD introducirse cuando exista lógica reutilizable o
    compleja; MUST NOT introducirse como abstracciones vacías.

Rationale: estos límites mantienen la UI predecible y testeable y permiten evolucionar el proyecto
brownfield gradualmente sin imponer modularización o abstracciones prematuras.

### III. Seguridad, privacidad y autenticación

1. Auth0 MUST integrarse como aplicación nativa mediante Universal Login y Authorization Code Flow
   con PKCE.
2. La aplicación Android es un cliente público y MUST NOT contener un client secret.
3. Las credenciales de Auth0 MUST introducirse únicamente en Universal Login. La aplicación
   MUST NOT capturar ni enviar directamente la contraseña de Auth0.
4. Si se conserva el PIN local de seis dígitos, este MUST considerarse únicamente un mecanismo local
   de desbloqueo para un usuario y dispositivo previamente vinculados. MUST NOT tratarse como
   contraseña de Auth0 ni enviarse al proveedor de identidad sin un contrato aprobado.
5. La biometría MUST mostrarse solamente a usuarios existentes que hayan vinculado previamente su
   cuenta y habilitado biometría en ese dispositivo.
6. La biometría MUST actuar únicamente como compuerta local de reautenticación y MUST NOT sustituir
   la identidad o autorización del backend.
7. Tokens, PIN, credenciales, datos personales y datos bancarios MUST NOT almacenarse en el
   repositorio, imprimirse en logs ni incluirse en fixtures o capturas de prueba.
8. Las credenciales reutilizables MUST almacenarse usando protección respaldada por Android
   Keystore. La librería concreta MUST decidirse en el plan técnico.
9. Los accesos al backend MUST utilizar HTTPS y MUST pasar por el API Gateway aprobado. La
   aplicación MUST NOT asumir acceso directo a microservicios.
10. Los datos bancarios sensibles SHOULD aparecer enmascarados por defecto para reducir su
    exposición accidental.
11. Scopes, audience, redirect URI, duración de tokens, rotación, revocación, `device_id` y contratos
    de registro MUST permanecer como NEEDS CLARIFICATION hasta recibir definición y aprobación del
    equipo.
12. Un flujo real de autenticación mediante DNI y PIN MUST NOT implementarse hasta que el backend y
    el equipo de seguridad aprueben expresamente ese contrato. La interfaz actual con DNI MAY
    mantenerse como diseño o desbloqueo local, pero MUST NOT presentarse como evidencia de un
    contrato de autenticación.

Rationale: minimizar la exposición de credenciales, preservar la autoridad del backend y rechazar
decisiones de identidad no aprobadas evita comportamiento inseguro de producción.

### IV. Sistema de diseño, UX y accesibilidad

1. Los frames de Figma marcados explícitamente como listos para desarrollo MUST ser la fuente visual
   aprobada.
2. Toda diferencia entre Figma y el código MUST resolverse documentando cuál versión fue aprobada.
   Un colaborador o agente MUST NOT elegir una silenciosamente.
3. Colores, tipografía, formas y espaciado recurrente MUST centralizarse como tokens del tema y
   componentes reutilizables.
4. MUST NOT agregarse valores visuales hardcodeados cuando exista un token aprobado equivalente.
5. Cada pantalla asíncrona MUST diseñar, cuando sean aplicables, los estados inicial, loading,
   contenido, vacío, error recuperable, error no recuperable, sin conexión y sesión expirada.
6. Los flujos transaccionales MUST contemplar prevención de doble envío, operación en procesamiento,
   éxito, rechazo y estado incierto.
7. Los componentes interactivos MUST cumplir objetivos táctiles de al menos 48 dp, descripciones
   accesibles, contraste suficiente y escalado de fuente.
8. Las pantallas y componentes reutilizables SHOULD disponer de Compose Preview para sus estados
   representativos, de forma que la revisión visual sea rápida y repetible.
9. La navegación MUST separar el destino o Route con estado y ViewModel del composable visual
   reutilizable y testeable.

Rationale: una fuente visual aprobada, tokens centralizados, estados explícitos y requisitos de
accesibilidad producen una experiencia coherente y utilizable en dispositivos y fallos reales.

### V. Contratos, datos y resiliencia

1. Los contratos aprobados del backend u OpenAPI MUST ser la fuente de verdad para endpoints,
   payloads, tipos y códigos de error.
2. La aplicación MUST NOT inventar endpoints o respuestas para desbloquear implementación.
3. Mocks y fakes MAY utilizarse para desarrollo, pero MUST estar detrás de interfaces y MUST estar
   claramente identificados como datos no productivos.
4. Los errores técnicos MUST mapearse a errores de dominio o UI comprensibles. Las pantallas
   MUST NOT interpretar directamente excepciones de red.
5. Las operaciones asíncronas MUST soportar cancelación y MUST respetar el ciclo de vida.
6. Los reintentos automáticos MUST limitarse a operaciones seguras o idempotentes.
7. Los reintentos de una misma transferencia MUST conservar una clave de idempotencia estable,
   conforme al contrato aprobado del backend.
8. Los montos monetarios MUST usar representaciones exactas. `Float` y `Double` MUST NOT utilizarse
   para cálculos financieros.
9. Fechas, zonas horarias, moneda y formato de DNI MUST seguir contratos explícitos y MUST NOT
   inferirse desde el dispositivo cuando afecten reglas del negocio.

Rationale: la fidelidad contractual y el manejo explícito de fallos impiden que suposiciones del
cliente corrompan el comportamiento financiero durante reintentos o cambios de ciclo de vida.

### VI. Calidad y pruebas

1. Todo cambio destinado a un Pull Request MUST superar los siguientes comandos:

   ```text
   ./gradlew :app:assembleDebug
   ./gradlew :app:testDebugUnitTest
   ./gradlew :app:lintDebug
   ```

2. El código nuevo MUST NOT introducir errores de compilación, pruebas fallidas ni nuevos errores
   de lint.
3. ViewModels, validadores, reductores y reglas de negocio MUST tener pruebas unitarias cuando
   contengan comportamiento.
4. Los flujos críticos de autenticación, navegación y transferencias MUST tener pruebas de UI o
   integración en el nivel apropiado.
5. Cada criterio de aceptación de la especificación MUST relacionarse con una prueba, una
   verificación manual documentada o una tarea explícita.
6. Una prueba boilerplate que no verifique comportamiento real MUST NOT satisfacer el requisito de
   calidad.
7. Una feature MUST NOT considerarse terminada únicamente porque compile. MUST satisfacer también
   accesibilidad, estados de UI, criterios de aceptación, seguridad y documentación.
8. Las dependencias nuevas MUST justificarse en `research.md` o `plan.md` y MUST centralizar su
   versión en el catálogo de versiones.
9. Toda actualización no relacionada de herramientas o dependencias SHOULD realizarse en un Pull
   Request separado para reducir el alcance de revisión y regresión.

Rationale: puertas reproducibles y pruebas orientadas a comportamiento aportan evidencia de que un
cambio es seguro, completo y consistente con su especificación aprobada.

### VII. Git, revisión y alcance

1. El desarrollo MUST NOT realizarse directamente sobre `main`.
2. Cada feature o cambio acotado MUST usar una rama propia y MUST asociarse con una incidencia Jira
   y una especificación.
3. Los commits SHOULD ser pequeños y coherentes y SHOULD usar Conventional Commits para mantener un
   historial revisable.
4. Todo Pull Request MUST incluir objetivo, Jira, ruta de la especificación, capturas cuando cambie
   la UI, pruebas ejecutadas, decisiones relevantes y exclusiones.
5. Las refactorizaciones ajenas MUST NOT mezclarse con una feature salvo que sean imprescindibles y
   estén justificadas en el plan.
6. Todo código producido con ayuda de IA MUST ser revisado por un desarrollador. El agente MUST NOT
   constituir aprobación técnica.
7. Los secretos, archivos locales del IDE y configuraciones personales MUST NOT entrar al control
   de versiones.

Rationale: ramas acotadas, revisiones enfocadas y responsabilidad humana preservan el aislamiento
de cambios y evitan ampliaciones accidentales de alcance o confianza.

## Restricciones del proyecto y límites de decisión

- Este es un proyecto brownfield: existen pantallas visuales Android, pero todavía no existe una
  arquitectura completa. La UI y el código existentes MUST inspeccionarse antes de planificar
  cambios, pero MUST NOT prevalecer sobre especificaciones o contratos aprobados.
- Esta constitución MUST contener reglas duraderas y verificables y MUST NOT incluir tareas
  temporales del Sprint 1 ni estados transitorios de entrega.
- Toda decisión que dependa del backend, seguridad o aprobación del equipo MUST permanecer como
  NEEDS CLARIFICATION hasta que una fuente aprobada la resuelva.
- Los planes y las implementaciones MUST NOT rellenar vacíos eligiendo Hilt, Koin, Retrofit, Ktor,
  Room, DataStore o una estrategia de módulos sin evaluación y aprobación técnica.
- Los contratos, endpoints, credenciales, parámetros de identidad y reglas de negocio MUST NOT
  crearse por inferencia desde pantallas visuales o código placeholder.
- El código de la aplicación, la configuración Gradle, los artefactos de diseño y las dependencias
  MAY cambiar únicamente dentro de un flujo de feature aprobado y fuera de la operación de
  actualización de esta constitución.

## Flujo de desarrollo y puertas de calidad

1. Antes de planificar, la especificación MUST contener historias testeables, criterios de
   aceptación, casos límite, exclusiones y toda dependencia no resuelta marcada como NEEDS
   CLARIFICATION.
2. Cada plan MUST incluir una comprobación de constitución antes de iniciar implementación y MUST
   repetirla después del diseño y antes de cerrar la feature.
3. Research y plan MUST registrar la justificación de dependencias, decisiones arquitectónicas,
   seguridad, uso de contratos y toda excepción justificada a esta constitución.
4. Las tareas MUST ordenarse por dependencias, ser trazables a historias y criterios de aceptación
   y limitarse de modo que la implementación no absorba trabajo ajeno silenciosamente.
5. El análisis MUST identificar inconsistencias entre especificación, plan, tareas, Figma aprobado,
   contratos del backend y código existente antes de implementar.
6. La implementación MUST procesar solo el alcance aprobado. La convergencia MUST comparar el código
   con la especificación, el plan y las tareas, y MUST agregar el trabajo restante en vez de declarar
   una finalización falsa.
7. Antes de considerar listo un Pull Request, la revisión MUST verificar la evidencia aplicable de
   compilación, pruebas unitarias, lint, accesibilidad, seguridad, estados de UI, documentación y
   trazabilidad.

## Governance

1. Esta constitución MUST prevalecer sobre prompts, especificaciones, planes y tareas que entren en
   conflicto con ella.
2. Cada plan MUST incluir una comprobación de constitución antes de iniciar implementación y otra
   antes de cerrar la feature.
3. Toda excepción MUST documentar su motivo, riesgo, responsable y condición o fecha de eliminación.
4. Las decisiones arquitectónicas importantes MUST registrarse mediante ADR cuando afecten a
   múltiples features o sean difíciles de revertir.
5. Toda enmienda MUST proponer el texto, justificación, impacto de compatibilidad, artefactos
   afectados y Sync Impact Report. MUST recibir revisión y aprobación mediante el proceso de revisión
   del repositorio antes de entrar en vigor.
6. Las versiones de la constitución MUST seguir versionado semántico:
   - MAJOR: eliminación o redefinición incompatible de principios o gobernanza.
   - MINOR: principio nuevo o ampliación material de requisitos.
   - PATCH: aclaración que no cambia la intención.
7. Toda enmienda MUST actualizar la versión, la fecha Last Amended y el Sync Impact Report. La fecha
   Ratified original MUST permanecer sin cambios.
8. Los templates relacionados de Spec Kit MUST actualizarse únicamente cuando sea necesario para
   mantener verificables los controles de constitución en especificaciones, planes y tareas. Los
   templates compatibles en tiempo de ejecución MUST NOT cambiarse sin una necesidad concreta de
   sincronización.
9. La constitución MUST NOT contener tokens placeholder sin resolver. Las decisiones legítimas del
   equipo MAY permanecer como entradas explícitas NEEDS CLARIFICATION con su límite de decisión.
10. La revisión de cumplimiento MUST verificar cada regla MUST aplicable. Toda desviación de una
    regla marcada SHOULD MUST justificarse en el plan o Pull Request. Las reglas marcadas MAY son
    opcionales, pero MUST respetar todas las demás restricciones.

**Version**: 1.0.0 | **Ratified**: 2026-08-29 | **Last Amended**: 2026-08-29
