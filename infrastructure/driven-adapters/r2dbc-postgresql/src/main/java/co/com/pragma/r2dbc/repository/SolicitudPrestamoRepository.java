package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.entities.SolicitudPrestamoData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SolicitudPrestamoRepository extends ReactiveCrudRepository<SolicitudPrestamoData, Long>, ReactiveQueryByExampleExecutor<SolicitudPrestamoData> {

    Mono<Boolean> existsByEmailAndTipoPrestamoIdAndEstadoIdNotIn(String correo, long tipoPrestamoId, List<Long> estados);

}
