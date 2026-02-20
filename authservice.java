package HeIPIA;

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
                } else {
                    break;
                }
            }

            // Bucle para email
            String email = "";
            while (true) {
                System.out.print("Email: ");
                email = sc.nextLine().trim();
                
                if (email.isEmpty()) {
                    System.out.println(rojo + "❌ El email no puede estar vacío" + reset);
                } else if (!email.contains("@") || !email.contains(".")) {
                    System.out.println(rojo + "❌ Formato de email inválido (debe contener @ y .)" + reset);
                } else {
                    break;
                }
            }

            // Bucle para contraseña
            String password = "";
            while (true) {
                System.out.print("Contraseña (mínimo 6 caracteres): ");
                password = sc.nextLine().trim();
                
                if (password.isEmpty()) {
                    System.out.println(rojo + "❌ La contraseña no puede estar vacía" + reset);
                } else if (password.length() < 6) {
                    System.out.println(rojo + "❌ La contraseña debe tener al menos 6 caracteres" + reset);
                } else {
                    break;
                }
            }

            // Verificar si existe
            String checkEndpoint = "/rest/v1/users?email=eq." + email;
            String checkResponse = supabase.get(checkEndpoint);

            if (!checkResponse.equals("[]")) {
                System.out.println(rojo + "❌ Ese email ya está registrado." + reset);
                return;
            }

            // Crear usuario
            String json = "{"
                    + "\"name\":\"" + name + "\","
                    + "\"email\":\"" + email + "\","
                    + "\"password\":\"" + password + "\""
                    + "}";

            int responseCode = supabase.post("/rest/v1/users", json);

            if (responseCode == 201) {
                System.out.println(verde + "═══════════════════════════════════════" + reset);
                System.out.println(verde + "✅ ¡CUENTA CREADA EXITOSAMENTE!" + reset);
                System.out.println(verde + "═══════════════════════════════════════" + reset);

                System.out.println(amarillo + "\n🌐 REGÍSTRATE EN LA WEB:" + reset);
                System.out.println(azul + signupUrl + reset);

                System.out.println(amarillo + "\n🔑 INICIA SESIÓN EN LA WEB:" + reset);
                System.out.println(azul + loginUrl + reset);

                System.out.println(verde + "\n✨ AHORA PUEDES INICIAR SESIÓN EN LA APLICACIÓN" + reset);
                System.out.println(verde + "═══════════════════════════════════════" + reset);

                // Preguntar si desea iniciar sesión ahora
                System.out.print(amarillo + "\n¿Deseas iniciar sesión ahora? (s/n): " + reset);
                String respuesta = sc.nextLine().trim().toLowerCase();

                if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                    iniciarSesion(sc);
                }

            } else {
                System.out.println(rojo + "❌ No se pudo crear la cuenta. Código: " + responseCode + reset);
            }

        } catch (Exception e) {
            System.out.println(rojo + "Error al crear cuenta:" + reset);
            e.printStackTrace();
        }
    }

    public void iniciarSesion(Scanner sc) {
        String loginUrl = "https://lxrwpuzxeywjjklspxox.supabase.co/auth/v1/login";

        try {
            System.out.println("\n" + azul + "═══════════════════════════════════════" + reset);
            System.out.println(amarillo + "🔐 INICIAR SESIÓN" + reset);
            System.out.println(azul + "═══════════════════════════════════════" + reset);

            // Bucle para email en login
            String email = "";
            while (true) {
                System.out.print("Email: ");
                email = sc.nextLine().trim();
                
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
                password = sc.nextLine().trim();
                
                if (password.isEmpty()) {
                    System.out.println(rojo + "❌ La contraseña no puede estar vacía" + reset);
                } else {
                    break;
                }
            }

            String endpoint = "/rest/v1/users?email=eq."
                    + email + "&password=eq." + password;

            String respuesta = supabase.get(endpoint);

            if (!respuesta.equals("[]")) {

                // Extraer nombre del usuario de la respuesta JSON
                String nombre = "";
                try {
                    nombre = respuesta.split("\"name\":\"")[1].split("\"")[0];
                } catch (Exception e) {
                    nombre = "Usuario";
                }

                System.out.println(verde + "═══════════════════════════════════════" + reset);
                System.out.println(verde + "✅ ¡LOGIN EXITOSO!" + reset);
                System.out.println(verde + "═══════════════════════════════════════" + reset);

                System.out.println(amarillo + "👋 ¡Bienvenido, " + nombre + "!" + reset);

                System.out.println(amarillo + "\n🌐 TAMBIÉN PUEDES INICIAR SESIÓN EN LA WEB:" + reset);
                System.out.println(azul + loginUrl + reset);

                System.out.println(verde + "\n✨ ACCEDIENDO AL MENÚ PRINCIPAL..." + reset);
                System.out.println(verde + "═══════════════════════════════════════" + reset);

                menuPrincipal(sc, nombre);

            } else {
                System.out.println(rojo + "❌ Email o contraseña incorrectos" + reset);

                // Opción para reintentar
                System.out.print(amarillo + "¿Deseas intentar nuevamente? (s/n): " + reset);
                String reintentar = sc.nextLine().trim().toLowerCase();
                if (reintentar.equals("s") || reintentar.equals("si")) {
                    iniciarSesion(sc);
                }
            }

        } catch (Exception e) {
            System.out.println(rojo + "Error en login:" + reset);
            e.printStackTrace();
        }
    }

    private void menuPrincipal(Scanner sc, String nombre) {
        while (true) {
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
                    // Aquí puedes llamar a un método crearProyecto();
                    break;

                case "/":
                case "listar":
                case "proyectos":
                    System.out.println(verde + "📂 Mostrando proyectos existentes..." + reset);
                    // Aquí puedes llamar a listarProyectos();
                    break;

                case "i":
                case "info":
                case "informacion":
                    mostrarInfoCuenta(nombre);
                    break;

                case "w":
                case "web":
                case "enlaces":
                    mostrarEnlacesWeb();
                    break;

                case "<-":
                case "salir":
                case "exit":
                case "cerrar":
                    System.out.println(amarillo + "👋 Cerrando sesión, " + nombre + "..." + reset);
                    System.out.println(verde + "Volviendo al menú principal...\n" + reset);
                    return;

                default:
                    System.out.println(rojo + "❌ Opción no válida" + reset);
                    System.out.println(amarillo + "   Usa: +, /, i, w, o <-" + reset);
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