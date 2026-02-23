package com.heipia;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    private static Scanner sc;
    private static String verde = "\u001B[32m";
    private static String rojo = "\u001B[31m";
    private static String reset = "\u001B[0m";
    private static String amarillo = "\u001B[33m";
    private static String azul = "\u001B[34m";
    private static String cian = "\u001B[36m";

    public static void main(String[] args) {
        sc = new Scanner(System.in);

        try {
            // Verificar conexión a internet al inicio
            boolean hayInternet = verificarConexion();

            if (hayInternet) {
                mostrarModoOnline();
            } else {
                System.out.println(rojo + "📴 Modo: Sin conexión a internet" + reset);
                mostrarBanner();
                System.out.println(amarillo + "\n⚠️  TRABAJANDO EN MODO LOCAL" + reset);
                modoLocal();
            }
        } catch (Exception e) {
            System.out.println(rojo + "❌ Error crítico en la aplicación" + reset);
            System.out.println(amarillo + "Reinicia el programa" + reset);
        }
    }

    private static void mostrarModoOnline() {
        while (true) {
            try {
                mostrarBanner();
                System.out.println(verde + "🌐 Modo: Con conexión a internet" + reset);
                System.out.println();
                System.out.println("1. Crear cuenta");
                System.out.println("2. Iniciar sesión");
                System.out.println("3. Modo sin conexión (local)");
                System.out.println("4. Salir");

                System.out.print("\nSelecciona una opción: ");

                if (!sc.hasNextInt()) {
                    String entrada = sc.nextLine();
                    System.out.println(rojo + "❌ Por favor, ingresa un número (1-4)" + reset);
                    continue;
                }

                int opcion = sc.nextInt();
                sc.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1:
                        authservice auth1 = new authservice();
                        auth1.crearCuenta(sc);
                        pausa();
                        break;
                    case 2:
                        authservice auth2 = new authservice();
                        auth2.iniciarSesion(sc);
                        pausa();
                        break;
                    case 3:
                        modoLocal();
                        break;
                    case 4:
                        salirAplicacion();
                        return;
                    default:
                        System.out.println(rojo + "❌ Opción inválida. Elige 1-4" + reset);
                        pausa();
                }
            } catch (Exception e) {
                System.out.println(rojo + "❌ Error en el menú principal" + reset);
                sc.nextLine();
                pausa();
            }
        }
    }

    private static void modoLocal() {
        while (true) {
            try {
                System.out.println("\n" + verde + "═══════════════════════════════════════" + reset);
                System.out.println(amarillo + "📋 MENÚ LOCAL - CALCULADORA DE SUBREDES" + reset);
                System.out.println(verde + "═══════════════════════════════════════" + reset);
                System.out.println(verde + "(+)" + reset + " Crear nuevo proyecto");
                System.out.println(verde + "(g)" + reset + " Ver proyectos guardados");
                System.out.println(verde + "(i)" + reset + " Información");
                System.out.println(rojo + "(v)" + reset + " Volver al menú principal");
                System.out.println(rojo + "(x)" + reset + " Salir de la aplicación");
                System.out.print(amarillo + "➤ " + reset + "Selecciona una opción: ");

                String opcion = sc.nextLine().trim().toLowerCase();

                switch (opcion) {
                    case "+":
                    case "crear":
                    case "nuevo":
                        crearProyecto();
                        pausa();
                        break;

                    case "g":
                    case "guardados":
                    case "ver":
                        verProyectosGuardados();
                        pausa();
                        break;

                    case "i":
                    case "info":
                    case "informacion":
                        mostrarInfoLocal();
                        pausa();
                        break;

                    case "v":
                    case "volver":
                        System.out.println(amarillo + "Volviendo al menú principal..." + reset);
                        return;

                    case "x":
                    case "salir":
                    case "exit":
                        confirmarSalida();
                        break;

                    default:
                        System.out.println(rojo + "❌ Opción no válida. Usa +, g, i, v, o x" + reset);
                        pausa();
                }
            } catch (Exception e) {
                System.out.println(rojo + "❌ Error en el menú local" + reset);
                pausa();
            }
        }
    }

    // MÉTODO MODIFICADO: Ver proyectos guardados en C:/Downloads/HeIPIA_Proyectos/
    private static void verProyectosGuardados() {
        System.out.println("\n" + azul + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "📂 PROYECTOS GUARDADOS EN DOWNLOADS" + reset);
        System.out.println(azul + "═══════════════════════════════════════" + reset);

        String userHome = System.getProperty("user.home");
        String rutaBase = userHome + "/Downloads/HeIPIA_Proyectos/";
        java.io.File carpeta = new java.io.File(rutaBase);

        if (!carpeta.exists() || !carpeta.isDirectory()) {
            System.out.println(amarillo + "No hay proyectos guardados en Downloads aún." + reset);
            System.out.println(amarillo + "📁 La carpeta se creará cuando guardes tu primer proyecto." + reset);
            return;
        }

        java.io.File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".txt"));

        if (archivos == null || archivos.length == 0) {
            System.out.println(amarillo + "No hay proyectos guardados en Downloads." + reset);
            return;
        }

        System.out.println(verde + "Proyectos disponibles en Downloads:" + reset);
        for (int i = 0; i < archivos.length; i++) {
            System.out.println("   " + (i + 1) + ". " + archivos[i].getName());
        }

        System.out.print(amarillo + "\n¿Quieres ver el contenido de algún proyecto? (número o 0 para salir): " + reset);
        try {
            int seleccion = Integer.parseInt(sc.nextLine().trim());
            if (seleccion > 0 && seleccion <= archivos.length) {
                mostrarContenidoArchivo(archivos[seleccion - 1].getPath());
            }
        } catch (NumberFormatException e) {
            // Ignorar
        }
    }

    private static void mostrarContenidoArchivo(String ruta) {
        System.out.println("\n" + cian + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "📄 CONTENIDO DEL PROYECTO" + reset);
        System.out.println(cian + "═══════════════════════════════════════" + reset);

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
            }
        } catch (IOException e) {
            System.out.println(rojo + "❌ Error al leer el archivo: " + e.getMessage() + reset);
        }
    }

    private static void confirmarSalida() {
        while (true) {
            System.out.print(amarillo + "\n¿Estás seguro de que quieres salir? (s/n): " + reset);
            String respuesta = sc.nextLine().trim().toLowerCase();

            if (respuesta.equals("s") || respuesta.equals("si")) {
                salirAplicacion();
                return;
            } else if (respuesta.equals("n") || respuesta.equals("no")) {
                System.out.println(amarillo + "Continuando en modo local..." + reset);
                return;
            } else {
                System.out.println(rojo + "❌ Responde 's' o 'n'" + reset);
            }
        }
    }

    private static void salirAplicacion() {
        System.out.println(verde + "\n═══════════════════════════════════════" + reset);
        System.out.println(verde + "👋 ¡Gracias por usar Heipia!" + reset);
        System.out.println(verde + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "Desarrollado por: @cesaralbiter" + reset);
        System.out.println(verde + "═══════════════════════════════════════" + reset);
        sc.close();
        System.exit(0);
    }

    private static void pausa() {
        System.out.println(amarillo + "\nPresiona ENTER para continuar..." + reset);
        sc.nextLine();
    }

    private static void crearProyecto() {
        try {
            System.out.println("\n" + azul + "═══════════════════════════════════════" + reset);
            System.out.println(amarillo + "📋 NUEVO PROYECTO DE SUBNETEO" + reset);
            System.out.println(azul + "═══════════════════════════════════════" + reset);
            System.out.println(amarillo + "💡 Para cancelar, escribe 'cancelar' en cualquier campo" + reset);

            String ip = "";
            int prefijoBase = 0;

            // Ingresar IP
            while (true) {
                System.out.print("\nIngrese dirección IP: ");
                ip = sc.nextLine().trim();

                if (ip.equalsIgnoreCase("cancelar")) {
                    System.out.println(amarillo + "Operación cancelada." + reset);
                    return;
                }

                if (!validarIP(ip)) {
                    System.out.println(rojo + "❌ IP inválida. Formato: XXX.XXX.XXX.XXX (0-255)" + reset);
                    continue;
                }
                break;
            }

            // Ingresar prefijo
            while (true) {
                System.out.print("Ingrese prefijo de red (0-32): ");
                String entrada = sc.nextLine().trim();

                if (entrada.equalsIgnoreCase("cancelar")) {
                    System.out.println(amarillo + "Operación cancelada." + reset);
                    return;
                }

                try {
                    prefijoBase = Integer.parseInt(entrada);
                    if (prefijoBase < 0 || prefijoBase > 32) {
                        System.out.println(rojo + "❌ El prefijo debe estar entre 0 y 32" + reset);
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println(rojo + "❌ Ingrese un número válido" + reset);
                }
            }

            // Validar dirección de red
            if (!esDireccionRed(ip, prefijoBase)) {
                String ipRedCorrecta = obtenerIpRed(ip, prefijoBase);
                System.out.println(rojo + "\n❌ La IP ingresada NO es una dirección de red válida" + reset);
                System.out.println(
                        amarillo + "   Dirección de red correcta: " + ipRedCorrecta + "/" + prefijoBase + reset);

                while (true) {
                    System.out.print(amarillo + "   ¿Usar esta IP corregida? (s/n): " + reset);
                    String respuesta = sc.nextLine().trim().toLowerCase();

                    if (respuesta.equals("s") || respuesta.equals("si")) {
                        ip = ipRedCorrecta;
                        System.out.println(verde + "✓ Usando IP de red: " + ip + "/" + prefijoBase + reset);
                        break;
                    } else if (respuesta.equals("n") || respuesta.equals("no")) {
                        System.out.println(amarillo + "Operación cancelada." + reset);
                        return;
                    } else {
                        System.out.println(rojo + "❌ Responde 's' o 'n'" + reset);
                    }
                }
            }

            // Calcular información del bloque
            long ipBaseLong = ipToLong(ip);
            long broadcastBaseLong = calcularBroadcastBase(ipBaseLong, prefijoBase);
            long totalIPsBase = 1L << (32 - prefijoBase);

            int totalHostsBase = calcularHostsUtiles(prefijoBase, totalIPsBase);
            String tipoRed = obtenerTipoRed(prefijoBase);

            // Mostrar información del bloque
            mostrarInfoBloque(ip, prefijoBase, tipoRed, broadcastBaseLong, ipBaseLong, totalIPsBase, totalHostsBase);

            // Ingresar hosts
            ArrayList<Integer> hosts = ingresarHosts(sc, totalHostsBase);
            if (hosts.isEmpty()) {
                System.out.println(rojo + "❌ No se ingresaron hosts. Cancelando..." + reset);
                return;
            }

            // Calcular y validar espacio
            if (!calcularYValidarEspacio(hosts, prefijoBase, totalIPsBase, ip, sc)) {
                return;
            }

        } catch (Exception e) {
            System.out.println(rojo + "❌ Error al crear proyecto: " + e.getMessage() + reset);
        }
    }

    private static ArrayList<Integer> ingresarHosts(Scanner sc, int maxHosts) {
        ArrayList<Integer> hosts = new ArrayList<>();

        System.out.println("\n" + amarillo + "📊 INGRESE HOSTS (// para terminar):" + reset);
        System.out.println(amarillo + "   Mínimo: 1, Máximo por subred: " + maxHosts + reset);

        int totalRequerido = 0;

        while (true) {
            System.out.print("Hosts para subred " + (hosts.size() + 1) + ": ");
            String entrada = sc.nextLine().trim();

            if (entrada.equals("//")) {
                if (hosts.isEmpty()) {
                    System.out.println(rojo + "⚠️  Debes ingresar al menos un host" + reset);
                    continue;
                }
                break;
            }

            try {
                int host = Integer.parseInt(entrada);
                if (host <= 0) {
                    System.out.println(rojo + "❌ El número debe ser positivo" + reset);
                } else if (host > maxHosts) {
                    System.out.println(rojo + "❌ Máximo " + maxHosts + " hosts por subred" + reset);
                } else {
                    hosts.add(host);
                    totalRequerido += host;
                    System.out.println(verde + "✔ Host agregado. Total requerido: " + totalRequerido + reset);
                }
            } catch (NumberFormatException e) {
                System.out.println(rojo + "❌ Ingresa un número válido" + reset);
            }
        }

        return hosts;
    }

    private static int calcularHostsUtiles(int prefijoBase, long totalIPsBase) {
        if (prefijoBase == 31) {
            return 2;
        } else if (prefijoBase == 32) {
            return 1;
        } else {
            return (int) (totalIPsBase - 2);
        }
    }

    private static String obtenerTipoRed(int prefijoBase) {
        if (prefijoBase == 31)
            return "Punto a punto (/31)";
        if (prefijoBase == 32)
            return "Host único (/32)";
        return "Normal";
    }

    private static void mostrarInfoBloque(String ip, int prefijoBase, String tipoRed,
            long broadcastBaseLong, long ipBaseLong,
            long totalIPsBase, int totalHostsBase) {
        System.out.println("\n" + verde + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "📊 INFORMACIÓN DEL BLOQUE BASE:" + reset);
        System.out.println(verde + "═══════════════════════════════════════" + reset);
        System.out.println("IP de red:     " + ip + "/" + prefijoBase);
        System.out.println("Tipo de red:   " + cian + tipoRed + reset);
        System.out.println("Broadcast:     " + (prefijoBase >= 31 ? "N/A" : longToIP(broadcastBaseLong)));

        if (prefijoBase == 32) {
            System.out.println("Rango útil:    " + "Solo esta IP");
        } else if (prefijoBase == 31) {
            System.out.println("Rango útil:    " + longToIP(ipBaseLong) + " - " + longToIP(ipBaseLong + 1));
        } else {
            System.out.println("Rango útil:    " + longToIP(ipBaseLong + 1) + " - " + longToIP(broadcastBaseLong - 1));
        }

        System.out.println("Total IPs:     " + totalIPsBase);
        System.out.println("Hosts útiles:  " + totalHostsBase);
        System.out.println(verde + "═══════════════════════════════════════" + reset);
    }

    private static boolean calcularYValidarEspacio(ArrayList<Integer> hosts, int prefijoBase,
            long totalIPsBase, String ip, Scanner sc) {
        ArrayList<Integer> tamanosBloques = new ArrayList<>();
        ArrayList<Integer> prefijosCalculados = new ArrayList<>();
        long espacioRequerido = 0;

        for (int h : hosts) {
            int bits = calcularBitsNecesarios(h, prefijoBase);
            int blockSize = 1 << bits;
            int prefijoSubred = 32 - bits;

            tamanosBloques.add(blockSize);
            prefijosCalculados.add(prefijoSubred);
            espacioRequerido += blockSize;
        }

        if (espacioRequerido > totalIPsBase) {
            mostrarErrorEspacio(totalIPsBase, espacioRequerido, ip);

            while (true) {
                System.out.print(amarillo + "¿Deseas intentar con otros valores? (s/n): " + reset);
                String respuesta = sc.nextLine().trim().toLowerCase();
                if (respuesta.equals("s") || respuesta.equals("si")) {
                    return false;
                } else if (respuesta.equals("n") || respuesta.equals("no")) {
                    return true;
                } else {
                    System.out.println(rojo + "❌ Responde 's' o 'n'" + reset);
                }
            }
        }

        mostrarResumen(hosts, prefijosCalculados, ip, prefijoBase);

        long ipBaseLong = ipToLong(ip);
        long broadcastBaseLong = calcularBroadcastBase(ipBaseLong, prefijoBase);

        String resultado = calcularSubredes(hosts, tamanosBloques, prefijosCalculados, ipBaseLong,
                broadcastBaseLong, totalIPsBase, prefijoBase, ip);

        // Preguntar si quiere guardar
        System.out.print(amarillo + "\n¿Deseas guardar este proyecto en un archivo? (s/n): " + reset);
        String guardar = sc.nextLine().trim().toLowerCase();
        if (guardar.equals("s") || guardar.equals("si")) {
            guardarProyectoEnArchivo(ip, prefijoBase, hosts, prefijosCalculados, resultado);
        }

        return true;
    }

    // MÉTODO MEJORADO: Guardar proyecto en C:/Downloads/HeIPIA_Proyectos/
    // AHORA CON LA LÓGICA COMPLETA: si existe la carpeta, la usa; si no, la crea
    private static void guardarProyectoEnArchivo(String ip, int prefijoBase, ArrayList<Integer> hosts,
            ArrayList<Integer> prefijos, String tablaResultado) {
        try {
            // Usar la carpeta Downloads del usuario
            String userHome = System.getProperty("user.home");
            String rutaBase = userHome + "/Downloads/HeIPIA_Proyectos/";

            // Crear objeto File para la carpeta
            java.io.File carpeta = new java.io.File(rutaBase);

            // Verificar si la carpeta existe
            if (carpeta.exists() && carpeta.isDirectory()) {
                // CASO 1: LA CARPETA YA EXISTE
                System.out.println(verde + "📁 Usando carpeta existente: " + rutaBase + reset);
            } else {
                // CASO 2: LA CARPETA NO EXISTE - LA CREAMOS
                System.out.println(amarillo + "📁 Creando carpeta: " + rutaBase + reset);
                if (carpeta.mkdirs()) {
                    System.out.println(verde + "✅ Carpeta creada exitosamente" + reset);
                } else {
                    // Si no se puede crear en Downloads, usar carpeta local como fallback
                    System.out.println(rojo + "❌ No se pudo crear la carpeta en Downloads" + reset);
                    System.out.println(amarillo + "💡 Usando carpeta local como respaldo..." + reset);
                    rutaBase = "proyectos_subredes/";
                    carpeta = new java.io.File(rutaBase);
                    carpeta.mkdirs();
                }
            }

            // Generar nombre de archivo con timestamp
            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = ahora.format(formatter);

            String nombreArchivo = rutaBase + "subneteo_" + ip.replace(".", "_") + "_" + timestamp + ".txt";

            try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
                writer.println("╔══════════════════════════════════════════════════════════════╗");
                writer.println("║                    PROYECTO DE SUBNETEO                      ║");
                writer.println("╚══════════════════════════════════════════════════════════════╝");
                writer.println();
                writer.println("📅 Fecha: " + java.time.LocalDate.now());
                writer.println("🕐 Hora: " + java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                writer.println();
                writer.println("════════════════════════════════════════════════════════════════");
                writer.println("📌 DATOS DEL PROYECTO");
                writer.println("════════════════════════════════════════════════════════════════");
                writer.println("IP de red base: " + ip + "/" + prefijoBase);
                writer.println();
                writer.println("📊 HOSTS INGRESADOS:");
                for (int i = 0; i < hosts.size(); i++) {
                    writer.println("   Subred " + (i + 1) + ": " + hosts.get(i) + " hosts → /" + prefijos.get(i));
                }
                writer.println();
                writer.println("════════════════════════════════════════════════════════════════");
                writer.println("📋 TABLA DE SUBNETEO");
                writer.println("════════════════════════════════════════════════════════════════");

                // Escribir la tabla línea por línea
                String[] lineasTabla = tablaResultado.split("\n");
                for (String linea : lineasTabla) {
                    // Limpiar códigos de color para el archivo
                    linea = linea.replace(verde, "").replace(rojo, "").replace(amarillo, "")
                            .replace(azul, "").replace(cian, "").replace(reset, "");
                    writer.println(linea);
                }

                writer.println();
                writer.println("════════════════════════════════════════════════════════════════");
                writer.println("✅ Generado por HeIPIA v1.1.0");
                writer.println("👤 @cesaralbiter");
                writer.println("════════════════════════════════════════════════════════════════");
            }

            System.out.println(verde + "\n✅ Proyecto guardado exitosamente!");
            System.out.println("📁 Archivo: " + nombreArchivo.replace(userHome, "~") + reset);
            System.out.println(amarillo + "📂 Carpeta: " + rutaBase + reset);

        } catch (IOException e) {
            System.out.println(rojo + "\n❌ Error al guardar el archivo: " + e.getMessage() + reset);

            // Fallback a carpeta local si hay error
            System.out.println(amarillo + "💡 Intentando guardar en carpeta local como respaldo..." + reset);
            try {
                java.io.File carpetaLocal = new java.io.File("proyectos_subredes");
                carpetaLocal.mkdirs();

                LocalDateTime ahora = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                String timestamp = ahora.format(formatter);

                String nombreArchivo = "proyectos_subredes/subneteo_" + ip.replace(".", "_") + "_" + timestamp + ".txt";

                try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
                    writer.println("PROYECTO DE SUBNETEO (RESPALDO LOCAL)");
                    writer.println("IP: " + ip + "/" + prefijoBase);
                    writer.println("Fecha: " + java.time.LocalDate.now());
                    writer.println("\n" + tablaResultado);
                }

                System.out.println(verde + "✅ Guardado en: " + nombreArchivo + reset);
            } catch (IOException ex) {
                System.out.println(rojo + "❌ Error en respaldo: " + ex.getMessage() + reset);
            }
        }
    }

    private static void mostrarErrorEspacio(long disponible, long requerido, String ip) {
        System.out.println("\n" + rojo + "═══════════════════════════════════════" + reset);
        System.out.println(rojo + "❌ ERROR: ESPACIO INSUFICIENTE" + reset);
        System.out.println(rojo + "═══════════════════════════════════════" + reset);
        System.out.println("Red base:       " + ip);
        System.out.println("Espacio disponible: " + disponible + " IPs");
        System.out.println("Espacio requerido:  " + requerido + " IPs");
        System.out.println("Faltan:             " + (requerido - disponible) + " IPs");
        System.out.println(amarillo + "\n💡 Sugerencia: Usa un prefijo más grande (número menor)" + reset);
    }

    private static void mostrarResumen(ArrayList<Integer> hosts, ArrayList<Integer> prefijos,
            String ip, int prefijoBase) {
        System.out.println("\n" + verde + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "📋 RESUMEN DEL PROYECTO:" + reset);
        System.out.println(verde + "═══════════════════════════════════════" + reset);
        System.out.println("IP de red: " + ip + "/" + prefijoBase);
        System.out.println("\nHosts ingresados:");
        for (int i = 0; i < hosts.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + hosts.get(i) + " hosts → /" + prefijos.get(i));
        }
    }

    private static String calcularSubredes(ArrayList<Integer> hosts, ArrayList<Integer> tamanosBloques,
            ArrayList<Integer> prefijosCalculados, long ipBaseLong,
            long broadcastBaseLong, long totalIPsBase, int prefijoBase, String ipOriginal) {

        StringBuilder resultado = new StringBuilder();

        ArrayList<HostBlock> listaCombinada = new ArrayList<>();
        for (int i = 0; i < hosts.size(); i++) {
            listaCombinada.add(new HostBlock(hosts.get(i), tamanosBloques.get(i), prefijosCalculados.get(i)));
        }
        Collections.sort(listaCombinada, (a, b) -> b.blockSize - a.blockSize);

        String lineaSeparador = verde
                + "══════════════════════════════════════════════════════════════════════════════════════════════"
                + reset;

        System.out.println("\n" + lineaSeparador);
        resultado.append("\n").append(lineaSeparador).append("\n");

        System.out.println(amarillo + "📊 TABLA DE SUBNETEO" + reset);
        resultado.append(amarillo).append("📊 TABLA DE SUBNETEO").append(reset).append("\n");

        System.out.println(lineaSeparador);
        resultado.append(lineaSeparador).append("\n");

        String formato = "%-4s %-8s %-15s %-15s %-15s %-15s %-18s %-8s\n";
        System.out.printf(formato, "#", "Hosts", "IP Red", "Primera IP", "Última IP", "Broadcast", "Máscara",
                "Prefijo");
        resultado.append(String.format(formato, "#", "Hosts", "IP Red", "Primera IP", "Última IP", "Broadcast",
                "Máscara", "Prefijo"));

        System.out.println(verde
                + "────────────────────────────────────────────────────────────────────────────────────────────────"
                + reset);
        resultado.append(verde
                + "────────────────────────────────────────────────────────────────────────────────────────────────"
                + reset).append("\n");

        long currentIP = ipBaseLong;
        long totalIPsUsadas = 0;
        boolean hayDesbordamiento = false;

        for (int i = 0; i < listaCombinada.size(); i++) {
            HostBlock hb = listaCombinada.get(i);

            if (currentIP % hb.blockSize != 0) {
                currentIP = ((currentIP / hb.blockSize) + 1) * hb.blockSize;
            }

            long network = currentIP;
            String estado = "";

            if (hb.prefijo == 31) {
                if (network + 1 > broadcastBaseLong) {
                    estado = rojo + " (DESBORDADO)" + reset;
                    hayDesbordamiento = true;
                }
            } else if (hb.prefijo == 32) {
                if (network > broadcastBaseLong) {
                    estado = rojo + " (DESBORDADO)" + reset;
                    hayDesbordamiento = true;
                }
            } else {
                if (network + hb.blockSize - 1 > broadcastBaseLong) {
                    estado = rojo + " (DESBORDADO)" + reset;
                    hayDesbordamiento = true;
                }
            }

            long[] ips = calcularRangoIPs(network, hb.prefijo, hb.blockSize);

            String linea = String.format("%-4d %-8d %-15s %-15s %-15s %-15s %-18s /%-8d%s\n",
                    i + 1,
                    hb.hosts,
                    longToIP(network),
                    longToIP(ips[0]),
                    longToIP(ips[1]),
                    longToIP(ips[2]),
                    longToIP(calcularMascara(hb.prefijo)),
                    hb.prefijo,
                    estado);

            System.out.print(linea);
            resultado.append(linea);

            currentIP += hb.blockSize;
            totalIPsUsadas += hb.blockSize;
        }

        System.out.println(lineaSeparador);
        resultado.append(lineaSeparador).append("\n");

        if (hayDesbordamiento) {
            String alerta = rojo + "\n⚠️  ALERTA: Algunas subredes exceden el rango del bloque base" + reset;
            System.out.println(alerta);
            resultado.append(alerta).append("\n");
        }

        // Añadir reporte final al resultado
        StringBuilder reporte = new StringBuilder();
        reporte.append(amarillo).append("\n📊 REPORTE FINAL DE IPs").append(reset).append("\n");
        reporte.append(verde).append("────────────────────────────────").append(reset).append("\n");
        reporte.append("IPs disponibles:   ").append(totalIPsBase).append("\n");
        reporte.append("IPs utilizadas:    ").append(totalIPsUsadas).append("\n");

        long ipsSobrantes = totalIPsBase - totalIPsUsadas;
        if (ipsSobrantes > 0) {
            reporte.append(verde).append("IPs sobrantes:     ").append(ipsSobrantes).append(reset).append("\n");
            if (currentIP < ipBaseLong + totalIPsBase) {
                reporte.append(amarillo).append("Rango sobrante:    ").append(longToIP(currentIP)).append(" - ")
                        .append(longToIP(ipBaseLong + totalIPsBase - 1)).append(reset).append("\n");
            }
        } else if (ipsSobrantes < 0) {
            reporte.append(rojo).append("IPs faltantes:     ").append(Math.abs(ipsSobrantes)).append(reset)
                    .append("\n");
        } else {
            reporte.append("IPs sobrantes:     0 (uso exacto)\n");
        }
        reporte.append(verde).append("═══════════════════════════════════════").append(reset).append("\n");

        System.out.print(reporte.toString());
        resultado.append(reporte.toString());

        return resultado.toString();
    }

    private static long[] calcularRangoIPs(long network, int prefijo, int blockSize) {
        long firstIP, lastIP, broadcast;

        if (prefijo == 31) {
            firstIP = network;
            lastIP = network + 1;
            broadcast = network + 1;
        } else if (prefijo == 32) {
            firstIP = network;
            lastIP = network;
            broadcast = network;
        } else {
            firstIP = network + 1;
            lastIP = network + blockSize - 2;
            broadcast = network + blockSize - 1;
        }

        return new long[] { firstIP, lastIP, broadcast };
    }

    private static long calcularMascara(int prefijo) {
        return 0xFFFFFFFFL << (32 - prefijo);
    }

    private static long calcularBroadcastBase(long ipRed, int prefijo) {
        if (prefijo == 32)
            return ipRed;
        if (prefijo == 31)
            return ipRed + 1;
        long mascara = 0xFFFFFFFFL << (32 - prefijo);
        long wildcard = ~mascara & 0xFFFFFFFFL;
        return ipRed | wildcard;
    }

    private static int calcularBitsNecesarios(int hosts, int prefijoBase) {
        if (prefijoBase == 32)
            return 0;
        int bits = 0;
        int maxHosts;
        do {
            bits++;
            maxHosts = (32 - bits == 31) ? (1 << bits) : (1 << bits) - 2;
        } while (maxHosts < hosts && bits < 32);
        return bits;
    }

    private static boolean validarIP(String ip) {
        if (ip == null || ip.isEmpty())
            return false;
        String[] partes = ip.split("\\.");
        if (partes.length != 4)
            return false;
        try {
            for (String parte : partes) {
                int valor = Integer.parseInt(parte);
                if (valor < 0 || valor > 255)
                    return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static long ipToLong(String ip) {
        String[] partes = ip.split("\\.");
        return ((long) Integer.parseInt(partes[0]) << 24) |
                ((long) Integer.parseInt(partes[1]) << 16) |
                ((long) Integer.parseInt(partes[2]) << 8) |
                (long) Integer.parseInt(partes[3]);
    }

    public static String longToIP(long ip) {
        return ((ip >> 24) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                (ip & 0xFF);
    }

    private static long aplicarMascara(long ip, int prefijo) {
        long mascara = 0xFFFFFFFFL << (32 - prefijo);
        return ip & mascara;
    }

    private static boolean esDireccionRed(String ip, int prefijo) {
        long ipLong = ipToLong(ip);
        long ipRed = aplicarMascara(ipLong, prefijo);
        return ipLong == ipRed;
    }

    private static String obtenerIpRed(String ip, int prefijo) {
        long ipLong = ipToLong(ip);
        long ipRed = aplicarMascara(ipLong, prefijo);
        return longToIP(ipRed);
    }

    private static void mostrarBanner() {
        System.out.println(verde + "██╗  ██╗███████╗██╗██████╗ ██╗ █████╗ ");
        System.out.println("██║  ██║██╔════╝██║██╔══██╗██║██╔══██╗");
        System.out.println("███████║█████╗  ██║██████╔╝██║███████║");
        System.out.println("██╔══██║██╔══╝  ██║██╔═══╝ ██║██╔══██║");
        System.out.println("██║  ██║███████╗██║██║     ██║██║  ██║");
        System.out.println("╚═╝  ╚═╝╚══════╝╚═╝╚═╝     ╚═╝╚═╝  ╚═╝");
        System.out.println("author: @cesaralbiter" + "\u001B[0m");
    }

    private static boolean verificarConexion() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setConnectTimeout(3000);
            conexion.setReadTimeout(3000);
            conexion.connect();
            return conexion.getResponseCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    private static void mostrarInfoLocal() {
        System.out.println("\n" + azul + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "ℹ️  INFORMACIÓN DEL MODO LOCAL" + reset);
        System.out.println(azul + "═══════════════════════════════════════" + reset);
        System.out.println("📌 Calculadora de subredes VLSM");
        System.out.println("📌 Soporta prefijos /0 a /32");
        System.out.println("📌 Manejo especial para /31 y /32");
        System.out.println("📌 Detección de desbordamiento");
        System.out.println("📌 Guardado en C:/Downloads/HeIPIA_Proyectos/");
        System.out.println("📌 Visualización de proyectos guardados");
        System.out.println("\n" + verde + "✨ Funciona sin conexión a internet" + reset);
        System.out.println(azul + "═══════════════════════════════════════" + reset);
    }

    static class HostBlock {
        int hosts;
        int blockSize;
        int prefijo;

        HostBlock(int hosts, int blockSize, int prefijo) {
            this.hosts = hosts;
            this.blockSize = blockSize;
            this.prefijo = prefijo;
        }
    }
}