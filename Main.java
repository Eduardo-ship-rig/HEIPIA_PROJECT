package HeIPIA;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        authservice auth = new authservice();
        String verde = "\u001B[32m";
        String rojo = "\u001B[31m";
        String reset = "\u001B[0m";
        String amarillo = "\u001B[33m";
        String azul = "\u001B[34m";
        String cian = "\u001B[36m";

        // Verificar conexión a internet al inicio
        boolean hayInternet = verificarConexion();

        if (hayInternet) {
            // Modo con internet - autenticación
            mostrarBanner(verde);
            System.out.println(verde + "🌐 Modo: Con conexión a internet" + reset);
            System.out.println();
            System.out.println("1. Crear cuenta");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Modo sin conexión (local)");

            System.out.print("\nSelecciona una opción: ");
            int opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    auth.crearCuenta(sc);
                    break;
                case 2:
                    auth.iniciarSesion(sc);
                    break;
                case 3:
                    modoLocal(sc, rojo, verde, reset, amarillo, azul, cian);
                    break;
                default:
                    System.out.println(rojo + "❌ Opción inválida." + reset);
                    return;
            }
        } else {
            modoLocal(sc, rojo, verde, reset, amarillo, azul, cian);
        }
        sc.close();
    }

    private static void modoLocal(Scanner sc, String rojo, String verde, String reset, String amarillo, String azul,
            String cian) {
        // Modo sin internet
        System.out.println(rojo + "📴 Modo: Sin conexión a internet" + reset);
        mostrarBanner(verde);
        System.out.println(amarillo + "\n⚠️  TRABAJANDO EN MODO LOCAL" + reset);

        while (true) {
            System.out.println("\n" + verde + "===== MENÚ LOCAL =====" + reset);
            System.out.println(verde + "(+)" + reset + " Crear nuevo proyecto");
            System.out.println(rojo + "(<-)" + reset + " Salir");
            System.out.print(amarillo + "➤ " + reset + "Selecciona una opción: ");

            String opcion = sc.nextLine().trim().toLowerCase();

            switch (opcion) {
                case "+":
                case "crear":
                case "nuevo":
                    crearProyecto(sc, verde, rojo, reset, amarillo, azul, cian);
                    break;

                case "<-":
                case "salir":
                case "exit":
                    System.out.println(verde + "👋 ¡Hasta luego!" + reset);
                    return;

                default:
                    System.out.println(rojo + "❌ Opción no válida. Usa '+' para crear o '<-' para salir." + reset);
            }
        }
    }

    private static void crearProyecto(Scanner sc, String verde, String rojo, String reset, String amarillo, String azul,
            String cian) {
        System.out.println("\n" + azul + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "📋 NUEVO PROYECTO DE SUBNETEO" + reset);
        System.out.println(azul + "═══════════════════════════════════════" + reset);

        // Validar entrada de IP y prefijo
        String ip = "";
        int prefijoBase = 0;

        while (true) {
            System.out.print("Ingrese dirección IP: ");
            ip = sc.nextLine().trim();

            if (!validarIP(ip)) {
                System.out.println(rojo + "❌ IP inválida. Formato correcto: XXX.XXX.XXX.XXX" + reset);
                continue;
            }

            System.out.print("Ingrese prefijo de red (0-32): ");
            try {
                prefijoBase = Integer.parseInt(sc.nextLine().trim());
                if (prefijoBase < 0 || prefijoBase > 32) {
                    System.out.println(rojo + "❌ El prefijo debe estar entre 0 y 32" + reset);
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println(rojo + "❌ Ingrese un número válido" + reset);
                continue;
            }

            // VALIDACIÓN CRÍTICA: Verificar que la IP sea realmente dirección de red
            if (!esDireccionRed(ip, prefijoBase)) {
                String ipRedCorrecta = obtenerIpRed(ip, prefijoBase);
                System.out.println(
                        rojo + "❌ La IP ingresada NO es una dirección de red válida para /" + prefijoBase + reset);
                System.out.println(amarillo + "   La dirección de red correcta sería: " + ipRedCorrecta + "/"
                        + prefijoBase + reset);
                System.out.print(amarillo + "   ¿Usar esta IP corregida? (s/n): " + reset);

                String respuesta = sc.nextLine().trim().toLowerCase();
                if (respuesta.equals("s") || respuesta.equals("si")) {
                    ip = ipRedCorrecta;
                    System.out.println(verde + "✓ Usando IP de red: " + ip + "/" + prefijoBase + reset);
                    break;
                }
                // Si no, sigue en el bucle
            } else {
                System.out.println(verde + "✓ IP de red válida" + reset);
                break;
            }
        }

        String[] ptr = ip.split("\\.");

        // Calcular límites del bloque base usando long
        long ipBaseLong = ipToLong(ip);

        // CORRECCIÓN: Calcular broadcast correctamente para cualquier prefijo
        long broadcastBaseLong = calcularBroadcastBase(ipBaseLong, prefijoBase);
        long totalIPsBase = 1L << (32 - prefijoBase);

        // DEBUG: Mostrar valores calculados
        System.out.println("\n" + cian + "🔍 DEBUG - Valores calculados:" + reset);
        System.out.println("   IP base: " + ip + "/" + prefijoBase);
        System.out.println("   IP base (long): " + ipBaseLong);
        System.out.println("   Broadcast calculado: " + longToIP(broadcastBaseLong));
        System.out.println("   Total IPs: " + totalIPsBase);

        // MEJORA 3: Manejo correcto de hosts útiles para /31 y /32
        int totalHostsBase;
        String tipoRed = "Normal";

        if (prefijoBase == 31) {
            totalHostsBase = 2; // /31 permite 2 hosts (sin broadcast)
            tipoRed = "Punto a punto (/31)";
        } else if (prefijoBase == 32) {
            totalHostsBase = 1; // /32 es una sola IP
            tipoRed = "Host único (/32)";
        } else {
            totalHostsBase = (int) (totalIPsBase - 2); // Caso normal
        }

        System.out.println("\n" + verde + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "📊 INFORMACIÓN DEL BLOQUE BASE:" + reset);
        System.out.println(verde + "═══════════════════════════════════════" + reset);
        System.out.println("IP de red:     " + ip + "/" + prefijoBase);
        System.out.println("Tipo de red:   " + cian + tipoRed + reset);
        System.out.println("Broadcast:     " + (prefijoBase >= 31 ? "N/A (no aplica)" : longToIP(broadcastBaseLong)));

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

        ArrayList<Integer> hosts = new ArrayList<>();

        System.out.println("\n" + amarillo + "📊 INGRESE HOSTS (// para terminar):" + reset);

        while (true) {
            System.out.print("Host: ");
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
                if (host > 0) {
                    // VALIDACIÓN: Verificar que el host no sea mayor al total de hosts disponibles
                    if (host > totalHostsBase) {
                        System.out.println(
                                rojo + "❌ No puedes solicitar " + host + " hosts en un bloque /" + prefijoBase);
                        System.out.println(amarillo + "   Máximo posible: " + totalHostsBase + " hosts" + reset);
                        continue;
                    }
                    hosts.add(host);
                    System.out.println(verde + "✔ Host agregado" + reset);
                } else {
                    System.out.println(rojo + "❌ El host debe ser mayor a 0" + reset);
                }
            } catch (NumberFormatException e) {
                System.out.println(rojo + "❌ Ingresa un número válido o // para terminar" + reset);
            }
        }

        // VALIDACIÓN: Verificar espacio total antes de calcular
        long espacioRequerido = 0;
        ArrayList<Integer> tamanosBloques = new ArrayList<>();
        ArrayList<Integer> prefijosCalculados = new ArrayList<>();

        for (int h : hosts) {
            int bits = calcularBitsNecesarios(h, prefijoBase);
            int blockSize = 1 << bits;
            int prefijoSubred = 32 - bits;

            tamanosBloques.add(blockSize);
            prefijosCalculados.add(prefijoSubred);
            espacioRequerido += blockSize;
        }

        if (espacioRequerido > totalIPsBase) {
            System.out.println("\n" + rojo + "═══════════════════════════════════════" + reset);
            System.out.println(rojo + "❌ ERROR: ESPACIO INSUFICIENTE" + reset);
            System.out.println(rojo + "═══════════════════════════════════════" + reset);
            System.out.println("Espacio disponible: " + totalIPsBase + " IPs");
            System.out.println("Espacio requerido:  " + espacioRequerido + " IPs");
            System.out.println("Faltan:             " + (espacioRequerido - totalIPsBase) + " IPs");
            System.out.println(amarillo + "\nSugerencia: Use un prefijo más grande (menor número)" + reset);
            return;
        }

        // Resumen del proyecto
        System.out.println("\n" + verde + "═══════════════════════════════════════" + reset);
        System.out.println(amarillo + "📋 RESUMEN DEL PROYECTO:" + reset);
        System.out.println(verde + "═══════════════════════════════════════" + reset);
        System.out.println("IP de red: " + ip + "/" + prefijoBase);
        System.out.println("\nHosts ingresados:");
        for (int i = 0; i < hosts.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + hosts.get(i) + " hosts → /" + prefijosCalculados.get(i));
        }

        // Calcular y mostrar tabla de subredes
        calcularSubredes(sc, hosts, tamanosBloques, prefijosCalculados, ipBaseLong, broadcastBaseLong,
                totalIPsBase, prefijoBase, verde, amarillo, reset, rojo, cian);
    }

    // CORRECCIÓN: Nuevo método para calcular broadcast base correctamente
    private static long calcularBroadcastBase(long ipRed, int prefijo) {
        if (prefijo == 32) {
            return ipRed; // /32 es solo una IP
        }
        if (prefijo == 31) {
            return ipRed + 1; // /31 tiene 2 IPs, la última es el broadcast implícito
        }

        // Para prefijos normales: máscara invertida OR con IP de red
        long mascara = 0xFFFFFFFFL << (32 - prefijo);
        long wildcard = ~mascara & 0xFFFFFFFFL; // Aseguramos que esté en 32 bits
        return ipRed | wildcard;
    }

    private static int calcularBitsNecesarios(int hosts, int prefijoBase) {
        if (prefijoBase == 32) {
            return 0;
        }

        int bits = 0;
        int maxHosts;

        do {
            bits++;
            if (32 - bits == 31) {
                maxHosts = 1 << bits;
            } else {
                maxHosts = (1 << bits) - 2;
            }
        } while (maxHosts < hosts && bits < 32);

        return bits;
    }

    private static void calcularSubredes(Scanner sc, ArrayList<Integer> hosts, ArrayList<Integer> tamanosBloques,
            ArrayList<Integer> prefijosCalculados, long ipBaseLong,
            long broadcastBaseLong, long totalIPsBase, int prefijoBase,
            String verde, String amarillo, String reset, String rojo, String cian) {

        // Crear lista combinada
        ArrayList<HostBlock> listaCombinada = new ArrayList<>();
        for (int i = 0; i < hosts.size(); i++) {
            listaCombinada.add(new HostBlock(hosts.get(i), tamanosBloques.get(i), prefijosCalculados.get(i)));
        }

        // Ordenar por tamaño de bloque (mayor a menor)
        Collections.sort(listaCombinada, (a, b) -> b.blockSize - a.blockSize);

        long currentIP = ipBaseLong;
        boolean desbordado = false;
        int primeraSubredDesbordada = -1;

        // Mostrar tabla COMPLETA
        System.out.println("\n" + verde
                + "══════════════════════════════════════════════════════════════════════════════════════════════"
                + reset);
        System.out.println(amarillo + "📊 TABLA DE SUBNETEO COMPLETA" + reset);
        System.out.println(
                verde + "══════════════════════════════════════════════════════════════════════════════════════════════"
                        + reset);

        // DEBUG: Mostrar broadcast base para verificación
        System.out.println(cian + "🔍 Broadcast base de referencia: " + longToIP(broadcastBaseLong) + reset);
        System.out.println(verde
                + "────────────────────────────────────────────────────────────────────────────────────────────────────────"
                + reset);

        System.out.printf("%-4s %-8s %-15s %-15s %-15s %-15s %-18s %-8s %-12s\n",
                "#", "Hosts", "IP Red", "Primera IP", "Última IP", "Broadcast", "Máscara", "Prefijo", "Estado");
        System.out.println(verde
                + "────────────────────────────────────────────────────────────────────────────────────────────────────────"
                + reset);

        long totalIPsUsadas = 0;
        int index = 1;

        for (HostBlock hb : listaCombinada) {
            int h = hb.hosts;
            int blockSize = hb.blockSize;
            int newPrefix = hb.prefijo;

            // Si la red actual no está alineada con el blockSize, ajustar
            if (currentIP % blockSize != 0) {
                currentIP = ((currentIP / blockSize) + 1) * blockSize;
            }

            long network = currentIP;
            long firstIP, lastIP, broadcast;

            if (newPrefix == 31) {
                firstIP = network;
                lastIP = network + 1;
                broadcast = network + 1;
            } else if (newPrefix == 32) {
                firstIP = network;
                lastIP = network;
                broadcast = network;
            } else {
                firstIP = network + 1;
                lastIP = network + blockSize - 2;
                broadcast = network + blockSize - 1;
            }

            // VALIDACIÓN CORREGIDA: Verificar que no exceda el broadcast base
            boolean estaDesbordado = false;

            // Para /31, comparamos la última IP
            if (newPrefix == 31) {
                if (lastIP > broadcastBaseLong) {
                    estaDesbordado = true;
                }
            }
            // Para /32, comparamos la única IP
            else if (newPrefix == 32) {
                if (network > broadcastBaseLong) {
                    estaDesbordado = true;
                }
            }
            // Para redes normales, comparamos el broadcast
            else {
                if (broadcast > broadcastBaseLong) {
                    estaDesbordado = true;
                }
            }

            if (estaDesbordado && !desbordado) {
                primeraSubredDesbordada = index;
                desbordado = true;
            }

            String estado = estaDesbordado ? rojo + "✗ DESBORDADO" + reset : verde + "✓ OK" + reset;

            long mask = 0xFFFFFFFFL << (32 - newPrefix);
            String maskStr = longToIP(mask);

            System.out.printf("%-4d %-8d %-15s %-15s %-15s %-15s %-18s /%-7d %s\n",
                    index,
                    h,
                    longToIP(network),
                    longToIP(firstIP),
                    longToIP(lastIP),
                    longToIP(broadcast),
                    maskStr,
                    newPrefix,
                    estado);

            // DEBUG: Mostrar comparación para la primera subred
            if (index == 1) {
                System.out.println(cian + "   └─ Comparación: broadcast subred=" + longToIP(broadcast) +
                        " vs broadcast base=" + longToIP(broadcastBaseLong) + reset);
            }

            currentIP += blockSize;
            totalIPsUsadas += blockSize;
            index++;
        }

        System.out.println(
                verde + "══════════════════════════════════════════════════════════════════════════════════════════════"
                        + reset);

        if (desbordado) {
            System.out.println("\n" + rojo + "⚠️  ALERTA: Se detectó desbordamiento en la subred #"
                    + primeraSubredDesbordada + reset);
            System.out.println(amarillo + "   Las subredes a partir de esta exceden el rango del bloque base" + reset);

            System.out.print(amarillo + "\n¿Desea continuar mostrando todas las subredes? (s/n): " + reset);
            String respuesta = sc.nextLine().trim().toLowerCase();

            if (!respuesta.equals("s") && !respuesta.equals("si")) {
                System.out.println(verde + "✅ Cálculo detenido por desbordamiento." + reset);
                return;
            }
        }

        // REPORTE FINAL
        System.out.println(amarillo + "\n📊 REPORTE FINAL DE IPs" + reset);
        System.out.println(verde + "────────────────────────────────" + reset);
        System.out.println("IPs disponibles:   " + totalIPsBase);
        System.out.println("IPs utilizadas:    " + totalIPsUsadas);

        long ipsSobrantes = totalIPsBase - totalIPsUsadas;
        if (ipsSobrantes > 0) {
            System.out.println(verde + "IPs sobrantes:     " + ipsSobrantes + reset);
            System.out.println(amarillo + "Última IP disponible: " + longToIP(ipBaseLong + totalIPsBase - 1) + reset);
            if (currentIP < ipBaseLong + totalIPsBase) {
                System.out.println(amarillo + "Rango sobrante:   " + longToIP(currentIP) + " - " +
                        longToIP(ipBaseLong + totalIPsBase - 1) + reset);
            }
        } else if (ipsSobrantes < 0) {
            System.out.println(rojo + "IPs faltantes:     " + Math.abs(ipsSobrantes) + reset);
        } else {
            System.out.println(amarillo + "IPs sobrantes:     0 (uso exacto)" + reset);
        }

        System.out.println(
                verde + "══════════════════════════════════════════════════════════════════════════════════════════════"
                        + reset);
    }

    // Clase auxiliar
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

    // Métodos de utilidad
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

    public static String intToIP(int ip) {
        return longToIP(ip & 0xFFFFFFFFL);
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

    private static void mostrarBanner(String verde) {
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
}