package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.TipoPrestamo;
import co.com.pragma.model.solicitud.gateways.TipoPrestamoGateway;
import co.com.pragma.r2dbc.model.entities.TipoPrestamoData;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.repository.TipoPrestamoRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class TipoPrestamoRepositoryAdapter extends ReactiveAdapterOperations<TipoPrestamo, TipoPrestamoData, Long, TipoPrestamoRepository>
        implements TipoPrestamoGateway {

    public TipoPrestamoRepositoryAdapter(TipoPrestamoRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, TipoPrestamo.class));
    }


    @Override
    public Mono<TipoPrestamo> encontrarPorId(Long id) {
        return findById(id);
    }

    @Override
    public Mono<Boolean> existePorId(Long id) {
        return repository.existsById(id);
    }
}
