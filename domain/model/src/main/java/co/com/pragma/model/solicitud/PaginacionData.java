package co.com.pragma.model.solicitud;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PaginacionData<T> {
    private List<T> datos;
    private long totalElementos;
    private long totalPaginas;
}
