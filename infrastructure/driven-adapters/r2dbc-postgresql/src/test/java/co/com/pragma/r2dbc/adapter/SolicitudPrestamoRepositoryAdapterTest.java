package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.r2dbc.entities.SolicitudPrestamoData;
import co.com.pragma.r2dbc.repository.SolicitudPrestamoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudPrestamoRepositoryAdapterTest {

    @InjectMocks
    SolicitudPrestamoRepositoryAdapter repositoryAdapter;

    @Mock
    SolicitudPrestamoRepository repository;

    @Mock
    TransactionalOperator transactionalOperator;

    @Mock
    ObjectMapper mapper;

    @Test
    void debeGuardarUsuario() {
        SolicitudPrestamoData entity = SolicitudPrestamoData.builder().id(1L).build();
        SolicitudPrestamo model = SolicitudPrestamo.builder().id(1L).build();

        when(repository.save(any(SolicitudPrestamoData.class))).thenReturn(Mono.just(entity));
        when(mapper.map(any(SolicitudPrestamoData.class), eq(SolicitudPrestamo.class))).thenReturn(model);
        when(mapper.map(any(SolicitudPrestamo.class), eq(SolicitudPrestamoData.class))).thenReturn(entity);
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mono<SolicitudPrestamo> result = repositoryAdapter.guardar(model);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(entity.getId()))
                .verifyComplete();
    }

    @Test
    void debeVerSiExistePorEmailYTipoPrestamoIdSinFinalizar() {
        when(repository.existsByEmailAndTipoPrestamoIdAndEstadoIdNotIn(anyString(), anyLong(), anyList())).thenReturn(Mono.just(true));

        Mono<Boolean> result = repositoryAdapter.existePorEmailYTipoPrestamoIdSinFinalizar("correo@corre.com", 1L, List.of(1L));

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(Boolean.TRUE))
                .verifyComplete();
    }

    @Test
    void mustFindValueById() {
        SolicitudPrestamoData entity = SolicitudPrestamoData.builder().id(1L).build();
        SolicitudPrestamo model = SolicitudPrestamo.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Mono.just(entity));
        when(mapper.map(any(SolicitudPrestamoData.class), eq(SolicitudPrestamo.class))).thenReturn(model);

        Mono<SolicitudPrestamo> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(entity.getId()))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        SolicitudPrestamoData entity = SolicitudPrestamoData.builder().id(1L).build();
        SolicitudPrestamo model = SolicitudPrestamo.builder().id(1L).build();

        when(repository.findAll()).thenReturn(Flux.fromIterable(List.of(entity)));
        when(mapper.map(any(SolicitudPrestamoData.class), eq(SolicitudPrestamo.class))).thenReturn(model);

        Flux<SolicitudPrestamo> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(entity.getId()))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        SolicitudPrestamoData entity = SolicitudPrestamoData.builder().id(1L).build();
        SolicitudPrestamo model = SolicitudPrestamo.builder().id(1L).build();

        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(entity));
        when(mapper.map(any(SolicitudPrestamoData.class), eq(SolicitudPrestamo.class))).thenReturn(model);
        when(mapper.map(any(SolicitudPrestamo.class), eq(SolicitudPrestamoData.class))).thenReturn(entity);

        Flux<SolicitudPrestamo> result = repositoryAdapter.findByExample(model);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(entity.getId()))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        SolicitudPrestamoData entity = SolicitudPrestamoData.builder().id(1L).build();
        SolicitudPrestamo model = SolicitudPrestamo.builder().id(1L).build();

        when(repository.save(any(SolicitudPrestamoData.class))).thenReturn(Mono.just(entity));
        when(mapper.map(any(SolicitudPrestamoData.class), eq(SolicitudPrestamo.class))).thenReturn(model);
        when(mapper.map(any(SolicitudPrestamo.class), eq(SolicitudPrestamoData.class))).thenReturn(entity);

        Mono<SolicitudPrestamo> result = repositoryAdapter.save(model);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(entity.getId()))
                .verifyComplete();
    }
}
