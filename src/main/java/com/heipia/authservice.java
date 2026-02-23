package com.heipia;

import java.util.Scanner;

public class authservice {
    private SupabaseClient supabase = new SupabaseClient();
    private String verde = "\u001B[32m";
    private String rojo = "\u001B[31m";
    private String reset = "\u001B[0m";
    private String amarillo = "\u001B[33m";
    private String azul = "\u001B[34m";

    public void crearCuenta(Scanner sc) {
        String signupUrl = "https://lxrwpuzxeywjjklspxox.supabase.co/auth/v1/signup";
        String loginUrl = "https://lxrwpuzxeywjjklspxox.supabase.co/auth/v1/login";

        try {
            System.out.println("\n" + azul + "═══════════════════════════════════════" + reset);
            System.out.println(amarillo + "📝 CREAR NUEVA CUENTA" + reset);
            System.out.println(azul + "═══════════════════════════════════════" + reset);

            // Bucle para nombre
            String name = "";
            while (true) {
                System.out.print("Nombre: ");
                name = sc.nextLine().trim();

                if (name.isEmpty()) {
                    System.out.println(rojo + "❌ El nombre no puede estar vacío" + reset);
                } else if (name.length() < 3) {
                    System.out.println(rojo + "❌ El nombre debe tener al menos 3 caracteres" + reset);
                } else {
                    break;
                }
            }

            // Bucle para email
            String email = "";
            while (true) {
                System.out.print("Email: ");
                email = sc.nextLine().trim().toLowerCase();

                if (email.isEmpty()) {
                    System.out.println(rojo + "❌ El email no puede estar vacío" + reset);
                } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                    System.out.println(rojo + "❌ Formato de email inválido" + reset);
                } else {
                    break;
                }
            }

            // Bucle para contraseña
            String password = "";
            while (true) {
                System.out.print("Contraseña (mínimo 6 caracteres): ");
                password = sc.nextLine();

                if (password.isEmpty()) {
                    System.out.println(rojo + "❌ La contraseña no puede estar vacía" + reset);
                } else if (password.length() < 6) {
                    System.out.println(rojo + "❌ La contraseña debe tener al menos 6 caracteres" + reset);
                } else if (password.contains(" ")) {
                    System.out.println(rojo + "❌ La contraseña no puede contener espacios" + reset);
                } else {
                    break;
                }
            }

            // Verificar conexión antes de consultar
            System.out.print(amarillo + "Verificando disponibilidad..." + reset);

            // Verificar si existe
            String checkEndpoint = "/rest/v1/users?email=eq." + email;
            SupabaseClient.RespuestaGet checkResponse = supabase.get(checkEndpoint);

            if (!checkResponse.exitosa) {
                if (checkResponse.esErrorRed()) {
                    System.out.println("\n" + rojo + "❌ " + checkResponse.mensajeError + reset);
                    System.out.println(amarillo + "¿Deseas continuar en modo local? (s/n): " + reset);
                    String respuesta = sc.nextLine().trim().toLowerCase();
                    if (respuesta.equals("s") || respuesta.equals("si")) {
                        // Volver al main para modo local
                        return;
                    }
                } else {
                    System.out
                            .println("\n" + rojo + "❌ Error al verificar email: " + checkResponse.mensajeError + reset);
                }
                return;
            }

            if (!checkResponse.cuerpo.equals("[]")) {
                System.out.println("\n" + rojo + "❌ Ese email ya está registrado." + reset);
                return;
            }

            System.out.println(verde + " ✓ Disponible" + reset);

            // Crear usuario
            String json = "{"
                    + "\"name\":\"" + name + "\","
                    + "\"email\":\"" + email + "\","
                    + "\"password\":\"" + password + "\""
                    + "}";

            SupabaseClient.RespuestaPost response = supabase.post("/rest/v1/users", json);

            if (response.exitosa && response.codigoHttp == 201) {
                mostrarExitoCreacion(signupUrl, loginUrl);

                // Preguntar si desea iniciar sesión ahora
                if (preguntarIniciarSesion(sc)) {
                    iniciarSesion(sc);
                }
            } else {
                manejarErrorCreacion(response);
            }

        } catch (Exception e) {
            System.out.println(rojo + "\n❌ Error inesperado al crear cuenta" + reset);
            System.out.println(amarillo + "Presiona ENTER para continuar..." + reset);
            sc.nextLine();
        }
    }

    private void mostrarExitoCreacion(String signupUrl, String loginUrl) {
        System.out.println(verde + "═══════════════════════════════════════" + reset);
        System.out.println(verde + "✅ ¡CUENTA CREADA EXITOSAMENTE!" + reset);
        System.out.println(verde + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "\n🌐 REGÍSTRATE EN LA WEB:" + reset);
        System.out.println(azul + signupUrl + reset);
        System.out.println(amarillo + "\n🔑 INICIA SESIÓN EN LA WEB:" + reset);
        System.out.println(azul + loginUrl + reset);
        System.out.println(verde + "\n✨ AHORA PUEDES INICIAR SESIÓN EN LA APLICACIÓN" + reset);
        System.out.println(verde + "═══════════════════════════════════════" + reset);
    }

    private boolean preguntarIniciarSesion(Scanner sc) {
        while (true) {
            System.out.print(amarillo + "\n¿Deseas iniciar sesión ahora? (s/n): " + reset);
            String respuesta = sc.nextLine().trim().toLowerCase();

            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                return true;
            } else if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            } else {
                System.out.println(rojo + "❌ Responde 's' o 'n'" + reset);
            }
        }
    }

    private void manejarErrorCreacion(SupabaseClient.RespuestaPost response) {
        if (response.esErrorRed()) {
            System.out.println(rojo + "❌ " + response.mensajeError + reset);
            System.out.println(amarillo + "💡 Puedes usar el modo local para calcular subredes" + reset);
        } else {
            System.out.println(rojo + "❌ No se pudo crear la cuenta." + reset);
            if (response.codigoHttp > 0) {
                System.out.println("Código HTTP: " + response.codigoHttp);
            }
        }
    }

    public void iniciarSesion(Scanner sc) {
        String loginUrl = "https://lxrwpuzxeywjjklspxox.supabase.co/auth/v1/login";
        int intentos = 0;
        final int MAX_INTENTOS = 3;

        while (intentos < MAX_INTENTOS) {
            try {
                System.out.println("\n" + azul + "═══════════════════════════════════════" + reset);
                System.out.println(
                        amarillo + "🔐 INICIAR SESIÓN (Intento " + (intentos + 1) + "/" + MAX_INTENTOS + ")" + reset);
                System.out.println(azul + "═══════════════════════════════════════" + reset);

                // Bucle para email en login
                String email = "";
                while (true) {
                    System.out.print("Email: ");
                    email = sc.nextLine().trim().toLowerCase();

                    if (email.isEmpty()) {
                        System.out.println(rojo + "❌ El email no puede estar vacío" + reset);
                    } else {
                        break;
                    }
                }

                // Bucle para contraseña en login
                String password = "";
                while (true) {
                    System.out.print("Contraseña: ");
                    password = sc.nextLine();

                    if (password.isEmpty()) {
                        System.out.println(rojo + "❌ La contraseña no puede estar vacía" + reset);
                    } else {
                        break;
                    }
                }

                String endpoint = "/rest/v1/users?email=eq." + email + "&password=eq." + password;
                SupabaseClient.RespuestaGet respuesta = supabase.get(endpoint);

                if (!respuesta.exitosa) {
                    if (respuesta.esErrorRed()) {
                        System.out.println(rojo + "\n❌ " + respuesta.mensajeError + reset);
                        System.out.println(amarillo + "¿Deseas usar el modo local? (s/n): " + reset);
                        String opcion = sc.nextLine().trim().toLowerCase();
                        if (opcion.equals("s") || opcion.equals("si")) {
                            return; // Volver al main para modo local
                        }
                    } else {
                        System.out.println(rojo + "\n❌ Error en la consulta: " + respuesta.mensajeError + reset);
                    }
                    intentos++;
                    continue;
                }

                if (!respuesta.cuerpo.equals("[]")) {
                    // Extraer nombre del usuario de la respuesta JSON
                    String nombre = extraerNombre(respuesta.cuerpo);
                    mostrarLoginExitoso(nombre, loginUrl);
                    menuPrincipal(sc, nombre);
                    return;
                } else {
                    System.out.println(rojo + "❌ Email o contraseña incorrectos" + reset);
                    intentos++;

                    if (intentos < MAX_INTENTOS) {
                        if (!preguntarReintentar(sc)) {
                            return;
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println(rojo + "\n❌ Error inesperado en login" + reset);
                intentos++;
            }
        }

        System.out.println(rojo + "\n❌ Demasiados intentos fallidos. Volviendo al menú principal." + reset);
    }

    private String extraerNombre(String respuestaJson) {
        try {
            // Búsqueda simple del nombre (mejorable con una librería JSON)
            String[] partes = respuestaJson.split("\"name\":\"");
            if (partes.length > 1) {
                return partes[1].split("\"")[0];
            }
        } catch (Exception e) {
            // Ignorar error, devolver nombre genérico
        }
        return "Usuario";
    }

    private void mostrarLoginExitoso(String nombre, String loginUrl) {
        System.out.println(verde + "═══════════════════════════════════════" + reset);
        System.out.println(verde + "✅ ¡LOGIN EXITOSO!" + reset);
        System.out.println(verde + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "👋 ¡Bienvenido, " + nombre + "!" + reset);
        System.out.println(amarillo + "\n🌐 TAMBIÉN PUEDES INICIAR SESIÓN EN LA WEB:" + reset);
        System.out.println(azul + loginUrl + reset);
        System.out.println(verde + "\n✨ ACCEDIENDO AL MENÚ PRINCIPAL..." + reset);
        System.out.println(verde + "═══════════════════════════════════════" + reset);
    }

    private boolean preguntarReintentar(Scanner sc) {
        while (true) {
            System.out.print(amarillo + "¿Deseas intentar nuevamente? (s/n): " + reset);
            String reintentar = sc.nextLine().trim().toLowerCase();
            if (reintentar.equals("s") || reintentar.equals("si")) {
                return true;
            } else if (reintentar.equals("n") || reintentar.equals("no")) {
                return false;
            } else {
                System.out.println(rojo + "❌ Responde 's' o 'n'" + reset);
            }
        }
    }

    private void menuPrincipal(Scanner sc, String nombre) {
        while (true) {
            try {
                System.out.println("\n" + azul + "═══════════════════════════════════════" + reset);
                System.out.println(amarillo + "📋 MENÚ PRINCIPAL - " + nombre + reset);
                System.out.println(azul + "═══════════════════════════════════════" + reset);

                System.out.println(verde + "(+)" + reset + "  Crear nuevo proyecto");
                System.out.println(verde + "(/)" + reset + "  Proyectos existentes");
                System.out.println(verde + "(i)" + reset + "  Información de cuenta");
                System.out.println(verde + "(w)" + reset + "  Ver enlaces web");
                System.out.println(rojo + "(<-)" + reset + " Cerrar sesión");

                System.out.print(amarillo + "\n➤ " + reset + "Selecciona una opción: ");

                String opcion = sc.nextLine().trim().toLowerCase();

                switch (opcion) {
                    case "+":
                    case "crear":
                    case "nuevo":
                        System.out.println(verde + "📁 Creando nuevo proyecto..." + reset);
                        System.out
                                .println(amarillo + "🔧 Función en desarrollo. Presiona ENTER para continuar." + reset);
                        sc.nextLine();
                        break;

                    case "/":
                    case "listar":
                    case "proyectos":
                        System.out.println(verde + "📂 Mostrando proyectos existentes..." + reset);
                        System.out
                                .println(amarillo + "🔧 Función en desarrollo. Presiona ENTER para continuar." + reset);
                        sc.nextLine();
                        break;

                    case "i":
                    case "info":
                    case "informacion":
                        mostrarInfoCuenta(nombre);
                        pausa(sc);
                        break;

                    case "w":
                    case "web":
                    case "enlaces":
                        mostrarEnlacesWeb();
                        pausa(sc);
                        break;

                    case "<-":
                    case "salir":
                    case "exit":
                    case "cerrar":
                        if (confirmarCerrarSesion(sc, nombre)) {
                            System.out.println(amarillo + "👋 Cerrando sesión..." + reset);
                            return;
                        }
                        break;

                    default:
                        System.out.println(rojo + "❌ Opción no válida" + reset);
                        System.out.println(amarillo + "   Usa: +, /, i, w, o <-" + reset);
                        pausa(sc);
                }
            } catch (Exception e) {
                System.out.println(rojo + "\n❌ Error en el menú. Volviendo..." + reset);
            }
        }
    }

    private void pausa(Scanner sc) {
        System.out.println(amarillo + "\nPresiona ENTER para continuar..." + reset);
        sc.nextLine();
    }

    private boolean confirmarCerrarSesion(Scanner sc, String nombre) {
        while (true) {
            System.out.print(amarillo + "¿Estás seguro de cerrar sesión, " + nombre + "? (s/n): " + reset);
            String respuesta = sc.nextLine().trim().toLowerCase();
            if (respuesta.equals("s") || respuesta.equals("si")) {
                return true;
            } else if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            } else {
                System.out.println(rojo + "❌ Responde 's' o 'n'" + reset);
            }
        }
    }

    private void mostrarInfoCuenta(String nombre) {
        System.out.println("\n" + azul + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "👤 INFORMACIÓN DE LA CUENTA" + reset);
        System.out.println(azul + "═══════════════════════════════════════" + reset);
        System.out.println("Usuario: " + verde + nombre + reset);
        System.out.println("Estado: " + verde + "Activo" + reset);
        System.out.println("Plan: " + amarillo + "Gratuito" + reset);
        System.out.println(azul + "═══════════════════════════════════════" + reset);
    }

    private void mostrarEnlacesWeb() {
        String signupUrl = "https://lxrwpuzxeywjjklspxox.supabase.co/auth/v1/signup";
        String loginUrl = "https://lxrwpuzxeywjjklspxox.supabase.co/auth/v1/login";

        System.out.println("\n" + azul + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "🌐 ENLACES WEB" + reset);
        System.out.println(azul + "═══════════════════════════════════════" + reset);

        System.out.println(verde + "📝 Registro:" + reset);
        System.out.println("   " + signupUrl);

        System.out.println(verde + "\n🔑 Inicio de sesión:" + reset);
        System.out.println("   " + loginUrl);

        System.out.println(azul + "\n═══════════════════════════════════════" + reset);
    }
}