package co.com.pragma.api.common;

public class Constantes {
    public static final String PATRON_CORREO = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String PATRON_MONTO = "^(?!0+(\\.0{1,2})?$)\\d+(\\.\\d{1,2})?$";
    public static final String PATRON_ENTERO_POSITIVO = "^[1-9]\\d*$";
}
