package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.SolicitudPrestamoMapper;
import co.com.pragma.r2dbc.model.entities.SolicitudPrestamoData;
import co.com.pragma.r2dbc.repository.SolicitudPrestamoRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class SolicitudPrestamoRepositoryAdapter extends ReactiveAdapterOperations<SolicitudPrestamo, SolicitudPrestamoData, Long, SolicitudPrestamoRepository>
        implements SolicitudPrestamoGateway {

    private final TransactionalOperator transactionalOperator;
    private final SolicitudPrestamoMapper solicitudPrestamoMapper;

    public SolicitudPrestamoRepositoryAdapter(SolicitudPrestamoRepository repository, ObjectMapper mapper,
                                              TransactionalOperator transactionalOperator,
                                              SolicitudPrestamoMapper solicitudPrestamoMapper) {
        super(repository, mapper, d -> mapper.map(d, SolicitudPrestamo.class));
        this.transactionalOperator = transactionalOperator;
        this.solicitudPrestamoMapper = solicitudPrestamoMapper;
    }

    @Override
    public Mono<SolicitudPrestamo> guardar(SolicitudPrestamo solicitudPrestamo) {
        return this.save(solicitudPrestamo)
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Boolean> existePorEmailYTipoPrestamoIdSinFinalizar(String correo, Long tipoSolicitudId, List<Long> estados) {
        return repository.existsByCorreoAndTipoPrestamoIdAndEstadoIdNotIn(correo, tipoSolicitudId, estados);
    }

    @Override
    public Flux<SolicitudPrestamo> obtenerPorEstados(List<Long> estados, int pagina, int tamano) {
        return repository.findByEstadoIdIn(estados, pagina, tamano)
                .map(solicitudPrestamoMapper::convertirDesde);
    }

    @Override
    public Flux<SolicitudPrestamo> obtenerPorEstadosYCorreoYTipoPrestamoId(List<Long> estados, String correo, Long tipoPrestamoId, int pagina, int tamano) {
        return repository.findByEstadoIdInAndCorreoAndTipoPrestamoId(estados, correo, tipoPrestamoId, pagina, tamano)
                .map(solicitudPrestamoMapper::convertirDesde);
    }

    @Override
    public Flux<SolicitudPrestamo> obtenerPorEstadosYCorreo(List<Long> estados, String correo, int pagina, int tamano) {
        return repository.findByEstadoIdInAndCorreo(estados, correo, pagina, tamano)
                .map(solicitudPrestamoMapper::convertirDesde);
    }

    @Override
    public Flux<SolicitudPrestamo> obtenerPorEstadosYTipoPrestamoId(List<Long> estados, Long tipoPrestamoId, int pagina, int tamano) {
        return repository.findByEstadoIdInAndTipoPrestamoId(estados, tipoPrestamoId, pagina, tamano)
                .map(solicitudPrestamoMapper::convertirDesde);
    }

    @Override
    public Mono<Long> contarPorEstados(List<Long> estados) {
        return repository.countByEstadoIdIn(estados);
    }

    @Override
    public Mono<Long> contarPorEstadosYCorreoYTipoPrestamoId(List<Long> estados, String correo, Long tipoPrestamoId) {
        return repository.countByEstadoIdInAndCorreoAndTipoPrestamoId(estados, correo, tipoPrestamoId);
    }

    @Override
    public Mono<Long> contarPorEstadosYCorreo(List<Long> estados, String correo) {
        return repository.countByEstadoIdInAndCorreo(estados, correo);
    }

    @Override
    public Mono<Long> contarPorEstadosYTipoPrestamoId(List<Long> estados, Long tipoPrestamoId) {
        return repository.countByEstadoIdInAndTipoPrestamoId(estados, tipoPrestamoId);
    }

    @Override
    public Mono<BigDecimal> obtenerDeudaTotalMensualSolicitudesAprobadas(String correo) {
        return repository.obtenerDeudaTotalMensualSolicitudesAprobadas(correo);
    }

}
