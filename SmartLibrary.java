import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SmartLibrary {
    public interface Notificable {
        void notificar(String mensaje);
    }

    // ============================================================
    // Clase abstracta Usuario
    // ============================================================
    public abstract static class Usuario {
        private final String identificacion;
        private final String nombre;
        private final String correo;

        public Usuario(String identificacion, String nombre, String correo) {
            this.identificacion = identificacion;
            this.nombre = nombre;
            this.correo = correo;
        }

        public String getIdentificacion() {
            return identificacion;
        }

        public String getNombre() {
            return nombre;
        }

        public String getCorreo() {
            return correo;
        }
    }

    // ============================================================
    // Estudiante (hereda de Usuario e implementa Notificable)
    // ============================================================
    public static class Estudiante extends Usuario implements Notificable {
        private final String codigoEstudiantil;
        private final String programaAcademico;

        public Estudiante(String identificacion, String nombre, String correo,
                          String codigoEstudiantil, String programaAcademico) {
            super(identificacion, nombre, correo);
            this.codigoEstudiantil = codigoEstudiantil;
            this.programaAcademico = programaAcademico;
        }

        public String getCodigoEstudiantil() {
            return codigoEstudiantil;
        }

        public String getProgramaAcademico() {
            return programaAcademico;
        }

        @Override
        public void notificar(String mensaje) {
            System.out.println("[NOTIFICACIÓN a Estudiante " + getNombre() +
                               " (" + codigoEstudiantil + ")] → " + mensaje);
        }
    }

    // ============================================================
    // Bibliotecario (hereda de Usuario e implementa Notificable)
    // ============================================================
    public static class Bibliotecario extends Usuario implements Notificable {
        private final String codigoEmpleado;
        private final String turno;

        public Bibliotecario(String identificacion, String nombre, String correo,
                             String codigoEmpleado, String turno) {
            super(identificacion, nombre, correo);
            this.codigoEmpleado = codigoEmpleado;
            this.turno = turno;
        }

        public String getCodigoEmpleado() {
            return codigoEmpleado;
        }

        public String getTurno() {
            return turno;
        }

        @Override
        public void notificar(String mensaje) {
            System.out.println("[NOTIFICACIÓN a Bibliotecario " + getNombre() +
                               " (" + codigoEmpleado + ")] → " + mensaje);
        }
    }

    // ============================================================
    // Clase Ejemplar
    // ============================================================
    public static class Ejemplar {
        private final String codigo;
        private final String titulo;

        public Ejemplar(String codigo, String titulo) {
            this.codigo = codigo;
            this.titulo = titulo;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getTitulo() {
            return titulo;
        }

        @Override
        public String toString() {
            return "Ejemplar{codigo=" + codigo + ", titulo=" + titulo + "}";
        }
    }

    // ============================================================
    // Clase Renovacion (parte de la composición con Prestamo)
    // ============================================================
    public static class Renovacion {
        private final LocalDate fechaRenovacion;
        private final LocalDate fechaAnterior;
        private final LocalDate nuevaFecha;

        public Renovacion(LocalDate fechaRenovacion, LocalDate fechaAnterior, LocalDate nuevaFecha) {
            this.fechaRenovacion = fechaRenovacion;
            this.fechaAnterior = fechaAnterior;
            this.nuevaFecha = nuevaFecha;
        }

        public LocalDate getFechaRenovacion() {
            return fechaRenovacion;
        }

        public LocalDate getFechaAnterior() {
            return fechaAnterior;
        }

        public LocalDate getNuevaFecha() {
            return nuevaFecha;
        }

        @Override
        public String toString() {
            return "Renovacion{realizada=" + fechaRenovacion +
                   ", anterior=" + fechaAnterior +
                   ", nueva=" + nuevaFecha + "}";
        }
    }

    // ============================================================
    // Clase Prestamo
    // ============================================================
    public static class Prestamo {
        private final Estudiante estudiante;
        private final Ejemplar ejemplar;
        private LocalDate fechaPrevistaDevolucion;
        private final List<Renovacion> renovaciones = new ArrayList<>();

        public Prestamo(Estudiante estudiante, Ejemplar ejemplar, LocalDate fechaPrevistaDevolucion) {
            this.estudiante = estudiante;
            this.ejemplar = ejemplar;
            this.fechaPrevistaDevolucion = fechaPrevistaDevolucion;
        }

        /**
         * Renueva el préstamo.
         * Regla: la nueva fecha DEBE ser estrictamente posterior a la fecha prevista vigente.
         */
        public void renovar(LocalDate nuevaFecha) {
            if (nuevaFecha == null || !nuevaFecha.isAfter(fechaPrevistaDevolucion)) {
                throw new IllegalArgumentException(
                    "Renovación inválida: la nueva fecha (" + nuevaFecha +
                    ") debe ser posterior a la fecha prevista actual (" + fechaPrevistaDevolucion + ")"
                );
            }

            Renovacion renovacion = new Renovacion(
                LocalDate.now(),
                this.fechaPrevistaDevolucion,
                nuevaFecha
            );

            renovaciones.add(renovacion);
            this.fechaPrevistaDevolucion = nuevaFecha;
        }

        public LocalDate getFechaPrevistaDevolucion() {
            return fechaPrevistaDevolucion;
        }

        public List<Renovacion> getRenovaciones() {
            return renovaciones;
        }

        public int getCantidadRenovaciones() {
            return renovaciones.size();
        }

        public Estudiante getEstudiante() {
            return estudiante;
        }

        public Ejemplar getEjemplar() {
            return ejemplar;
        }
    }

    // 5.3 Prueba mínima
    public static class PruebaSmartLibrary {
        public static void main(String[] args) {
            Estudiante estudiante = new Estudiante(
                "1087654321",
                "Ana María López",
                "ana.lopez@universidad.edu.co",
                "202310450",
                "Ingeniería de Sistemas"
            );

            Ejemplar ejemplar = new Ejemplar("EJ-001", "Clean Code");

            LocalDate fechaInicial = LocalDate.of(2026, 10, 1);
            Prestamo prestamo = new Prestamo(estudiante, ejemplar, fechaInicial);

            System.out.println("=== PRUEBA 1: Renovación válida ===");
            System.out.println("Ejemplar del préstamo: " + prestamo.getEjemplar());
            System.out.println("Fecha prevista inicial: " + prestamo.getFechaPrevistaDevolucion());

            prestamo.renovar(LocalDate.of(2026, 10, 15));

            estudiante.notificar("Su préstamo fue renovado. Nueva fecha de devolución: "
                                 + prestamo.getFechaPrevistaDevolucion());

            System.out.println("Nueva fecha prevista: " + prestamo.getFechaPrevistaDevolucion());
            System.out.println("Cantidad de renovaciones: " + prestamo.getCantidadRenovaciones());
            System.out.println("Detalle de renovaciones: " + prestamo.getRenovaciones());

            System.out.println("\n=== PRUEBA 2: Renovación inválida ===");
            try {
                prestamo.renovar(LocalDate.of(2026, 10, 10));
                System.out.println("ERROR: se permitió una renovación inválida");
            } catch (IllegalArgumentException e) {
                System.out.println("Excepción capturada correctamente:");
                System.out.println("→ " + e.getMessage());
            }

            System.out.println("Fecha prevista sigue siendo: " + prestamo.getFechaPrevistaDevolucion());
            System.out.println("Cantidad de renovaciones sigue siendo: " + prestamo.getCantidadRenovaciones());
        }
    }
}

