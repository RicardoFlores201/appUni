# Nutrideli UPIICSA

Aplicación Android nativa enfocada en pedidos de comida saludable, con dos perfiles de uso:

- **Cliente**: explora platillos, filtra por categoría/etiquetas, agrega al carrito y realiza pedidos.
- **Restaurante**: registra su negocio, administra su catálogo de platillos y visualiza su panel.

El proyecto está construido con **Kotlin + Jetpack Compose** y usa **Firebase** como backend (Autenticación, Firestore, Realtime Database y Storage).

## Tabla de contenido

- [Tecnologías y stack](#tecnologías-y-stack)
- [Arquitectura del proyecto](#arquitectura-del-proyecto)
- [Módulos funcionales](#módulos-funcionales)
- [Navegación de pantallas](#navegación-de-pantallas)
- [Integración con Firebase](#integración-con-firebase)
- [Configuración de autenticación (Google y GitHub)](#configuración-de-autenticación-google-y-github)
- [Estructura de datos en Firebase](#estructura-de-datos-en-firebase)
- [Requisitos y ejecución local](#requisitos-y-ejecución-local)
- [Estructura de carpetas](#estructura-de-carpetas)
- [Pruebas](#pruebas)
- [Mejoras sugeridas](#mejoras-sugeridas)

## Tecnologías y stack

### Android

- Kotlin
- Android SDK `minSdk 31`, `targetSdk 36`, `compileSdk 36`
- Gradle Kotlin DSL (`build.gradle.kts`)

### UI

- Jetpack Compose
- Material 3
- Material Icons (`core` y `extended`)
- Coil (`io.coil-kt:coil-compose`) para carga de imágenes remotas
- Navigation Compose (`androidx.navigation:navigation-compose`)

### Backend y servicios

- Firebase Authentication
  - Email/Password
  - Google Sign-In (Credential Manager + Google Identity)
  - GitHub OAuth (`OAuthProvider`)
- Cloud Firestore
  - Usuarios, restaurantes y platillos
- Firebase Realtime Database
  - Pedidos y cambios de estado en tiempo real
- Firebase Storage
  - Imágenes de platillos

### Librerías relevantes

- `com.google.firebase:firebase-bom:34.7.0`
- `com.google.firebase:firebase-auth`
- `com.google.firebase:firebase-firestore`
- `com.google.firebase:firebase-database-ktx:21.0.0`
- `com.google.firebase:firebase-storage`
- `androidx.credentials:credentials:1.5.0`
- `androidx.credentials:credentials-play-services-auth:1.5.0`
- `com.google.android.gms:play-services-auth:21.4.0`
- `com.google.android.libraries.identity.googleid:googleid:1.1.1`
- `androidx.browser:browser:1.9.0`

## Arquitectura del proyecto

El proyecto sigue un enfoque **MVVM orientado a estado de UI**, con Compose:

- **Views (`views/`)**: pantallas Compose para cliente, restaurante, login y registro.
- **ViewModels (`viewModel/`)**:
  - `LoginViewModel`: login cliente (email, Google, GitHub), registro de usuario y validaciones.
  - `RestaurantAuthViewModel`: registro/login de restaurante y verificación de tipo de cuenta.
  - `DishViewModel`: CRUD de platillos y subida de imágenes a Storage.
  - `CartViewModel`: estado local del carrito y validación de restaurante único por compra.
  - `OrderViewModel`: creación de pedidos y listeners en Realtime Database.
- **Model (`model/`)**: `UserModel`, `RestaurantModel`, `DishModel`, `OrderModel`.
- **Navigation (`navigation/`)**:
  - `AppScreen`: definición centralizada de rutas.
  - `AppNavigation`: grafo de navegación con `NavHost`.

## Módulos funcionales

### 1) Flujo de cliente

- Inicio en pantalla de selección de perfil.
- Login por Email/Password, Google o GitHub.
- Registro de usuario.
- Home con listado de platillos y filtros.
- Detalle de platillo.
- Carrito y checkout.
- Historial de pedidos y detalle de pedido.
- Gestión de direcciones (pantalla dedicada).

### 2) Flujo de restaurante

- Registro de restaurante.
- Login de restaurante.
- Dashboard con listado de sus platillos.
- Alta, edición y eliminación de platillos.
- Pantallas placeholder para pedidos/perfil de restaurante (pendientes de implementación completa).

### 3) Carrito y pedidos

- El carrito restringe productos a **un solo restaurante por pedido**.
- Cálculo de subtotal, cargo de envío y total.
- Pedido almacenado en Realtime Database.
- Soporte de estados de pedido: `pending`, `confirmed`, `preparing`, `on_delivery`, `delivered`, `cancelled`.

## Navegación de pantallas

La app usa `NavHostController` con rutas declaradas en `AppScreen`.

- `MainActivity` crea el `NavController` y monta `AppNavigation`.
- `AppNavigation` define `startDestination = "blank"` y registra rutas de cliente y restaurante.
- Rutas con parámetros:
  - `dish_detail/{dishId}`
  - `order_detail/{orderId}`
  - `edit_dish/{dishId}`
- Se utiliza `popUpTo` para limpiar el stack en flujos de login/salida y evitar regreso a pantallas no deseadas.

## Integración con Firebase

El proyecto ya incluye:

- Plugin `com.google.gms.google-services`
- Archivo `app/google-services.json`

Servicios empleados:

1. **Auth**: sesión y proveedores de identidad.
2. **Firestore**: entidades de negocio (`users`, `restaurants`, `dishes`).
3. **Realtime Database**: nodo `orders` para pedidos y actualizaciones de estado.
4. **Storage**: bucket para imágenes de platillos (`dishes/{restaurantId}/{dishId}.jpg`).

## Configuración de autenticación (Google y GitHub)

> Esta sección es clave para que los inicios de sesión federados funcionen correctamente en cualquier entorno.

### A) Google Sign-In con Firebase Auth

1. En **Firebase Console** > Authentication > Sign-in method, habilita **Google**.
2. Verifica que el package name de Android coincida con la app: `com.ejercicio.app`.
3. Registra huellas **SHA-1/SHA-256** del certificado de debug/release en Firebase Project Settings.
4. Descarga/reemplaza `google-services.json` en `app/`.
5. Asegura que `default_web_client_id` exista en recursos (lo inyecta `google-services`).
6. El flujo en la app:
   - `CredentialManager` obtiene credencial Google.
   - Se extrae el ID token (`GoogleIdTokenCredential`).
   - Firebase autentica con `GoogleAuthProvider.getCredential(...)`.

### B) GitHub OAuth con Firebase Auth

1. En **GitHub Developer Settings** crea una OAuth App.
2. En Firebase > Authentication > Sign-in method, habilita **GitHub**.
3. Configura `Client ID` y `Client Secret` de tu OAuth App en Firebase.
4. Usa como callback URL la que te provee Firebase Auth para GitHub (la muestra el panel al habilitar el proveedor).
5. En la app, el login usa:
   - `OAuthProvider.newBuilder("github.com")`
   - Scope: `user:email`
   - `startActivityForSignInWithProvider(...)`

## Estructura de datos en Firebase

### Firestore

Colecciones utilizadas:

- `users`: datos de cliente (`userId`, `email`, `username`)
- `restaurants`: datos de restaurante (`restaurantId`, `restaurantName`, `ownerName`, etc.)
- `dishes`: catálogo de platillos con metadatos nutricionales y URL de imagen

### Realtime Database

- Nodo `orders/{orderId}` con:
  - datos de usuario
  - datos de restaurante
  - items del pedido
  - totales
  - dirección/instrucciones
  - estado y timestamps

### Storage

- Imágenes de platillos por restaurante:
  - `dishes/{restaurantId}/{uuid}.jpg`

## Requisitos y ejecución local

### Requisitos

- Android Studio (versión reciente con soporte Compose)
- JDK 11
- Dispositivo/emulador con Android 12+ (API 31+)
- Proyecto Firebase configurado

### Pasos

1. Clona el repositorio.
2. Abre el proyecto en Android Studio.
3. Verifica `google-services.json` en `app/`.
4. Sincroniza Gradle.
5. Ejecuta en emulador/dispositivo.

### Comandos útiles

```bash
./gradlew assembleDebug
./gradlew test
./gradlew connectedAndroidTest
```

> En Windows PowerShell puedes usar `.\gradlew.bat ...`.

## Estructura de carpetas

```text
app/src/main/java/com/ejercicio/app/
  ├─ navigation/      # Rutas y NavHost Compose
  ├─ model/           # Data classes de dominio
  ├─ viewModel/       # Lógica de estado y acceso a Firebase
  ├─ views/           # Pantallas Compose (cliente/restaurante/login)
  ├─ components/      # Componentes reutilizables de UI
  └─ utils/           # Constantes y utilidades (ingredientes/tags/categorías)
```

## Pruebas

Actualmente existen plantillas base de pruebas:

- Unit tests: `app/src/test`
- Instrumented tests: `app/src/androidTest`

Recomendado para evolución:

- pruebas unitarias de ViewModels (validaciones y estados),
- pruebas de navegación Compose,
- pruebas de integración contra Firebase Emulator Suite.

## Mejoras sugeridas

- Añadir una capa Repository para desacoplar Firebase de ViewModels.
- Estandarizar nombres de colecciones (`users` vs `Users`) para evitar inconsistencias.
- Implementar por completo pantallas de pedidos/perfil para restaurante.
- Agregar manejo robusto de errores de red y reintentos.
- Incorporar CI (lint, test, build) y cobertura mínima de pruebas.

---

### Créditos

Proyecto académico/práctico de Android + Firebase orientado a una experiencia de pedidos saludables con enfoque de doble rol (cliente/restaurante).
