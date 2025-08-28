package co.com.pragma.api.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PaginacionDataDto<T> {
    private List<T> datos;
    private long totalElementos;
    private long totalPaginas;
}
