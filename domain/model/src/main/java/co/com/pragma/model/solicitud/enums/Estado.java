package co.com.pragma.model.solicitud.enums;

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

    public static List<Estado> obtenerEstadosFinalizados(){
        return Arrays.stream(Estado.values())
                .filter(estado -> estado.esFinalizado)
                .toList();
    }
}


