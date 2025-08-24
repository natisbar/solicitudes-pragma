package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.entities.SolicitudPrestamoData;
import co.com.pragma.r2dbc.repository.SolicitudPrestamoRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class SolicitudPrestamoRepositoryAdapter extends ReactiveAdapterOperations<SolicitudPrestamo, SolicitudPrestamoData, Long, SolicitudPrestamoRepository>
        implements SolicitudPrestamoGateway {

    private final TransactionalOperator transactionalOperator;

    public SolicitudPrestamoRepositoryAdapter(SolicitudPrestamoRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, SolicitudPrestamo.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<SolicitudPrestamo> guardar(SolicitudPrestamo solicitudPrestamo) {
        return this.save(solicitudPrestamo)
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Boolean> encontrarPorEmailYTipoPrestamoIdSinFinalizar(String correo, Long tipoSolicitudId, List<Long> estados) {
        return repository.existsByEmailAndTipoPrestamoIdAndEstadoIdNotIn(correo, tipoSolicitudId, estados);
    }
}
