package co.com.pragma.model.solicitud.enums;

import co.com.pragma.model.solicitud.common.ex.NegocioException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Estado {
    PENDIENTE("PENDIENTE REVISION", 1, false),
    RECHAZADA("RECHAZADA", 2, true),
    REVISION("REVISION MANUAL", 3, false),
    APROBADO("APROBADO", 4, true);

    private final String valor;
    private final long id;
    private final boolean esFinalizado;

    Estado(String valor, long id, boolean esFinalizado) {
        this.valor = valor;
        this.id = id;
        this.esFinalizado = esFinalizado;
    }

    public static Estado obtenerPorId(long id){
        return Arrays.stream(Estado.values())
                .filter(estado -> estado.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static Estado obtenerPorDescripcion(String descripcion){
        return Arrays.stream(Estado.values())
                .filter(estado -> estado.getValor().equals(descripcion.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new NegocioException("El estado recibido no existe"));
    }


    public static List<Estado> obtenerEstadosFinalizados(){
        return Arrays.stream(Estado.values())
                .filter(estado -> estado.esFinalizado)
                .toList();
    }

    public static List<Estado> obtenerEstadosSinFinalizar(){
        return Arrays.stream(Estado.values())
                .filter(estado -> !estado.esFinalizado)
                .toList();
    }
}


