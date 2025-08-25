package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.TipoPrestamo;
import co.com.pragma.r2dbc.entities.TipoPrestamoData;
import co.com.pragma.r2dbc.repository.TipoPrestamoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoPrestamoRepositoryAdapterTest {

    @InjectMocks
    TipoPrestamoRepositoryAdapter repositoryAdapter;

    @Mock
    TipoPrestamoRepository repository;

    @Mock
    ObjectMapper mapper;


    @Test
    void debeObtenerSiExistePorId() {
        TipoPrestamoData entity = TipoPrestamoData.builder().id(1L).build();
        TipoPrestamo model = TipoPrestamo.builder().id(1L).build();

        when(repository.findById(anyLong())).thenReturn(Mono.just(entity));
        when(mapper.map(any(TipoPrestamoData.class), eq(TipoPrestamo.class))).thenReturn(model);

        Mono<TipoPrestamo> result = repositoryAdapter.encontrarPorId(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(entity.getId()))
                .verifyComplete();
    }

    @Test
    void debeVerSiExistePorId() {
        when(repository.existsById(anyLong())).thenReturn(Mono.just(true));

        Mono<Boolean> result = repositoryAdapter.existePorId(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(Boolean.TRUE))
                .verifyComplete();
    }

    @Test
    void mustFindValueById() {
        TipoPrestamoData entity = TipoPrestamoData.builder().id(1L).build();
        TipoPrestamo model = TipoPrestamo.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Mono.just(entity));
        when(mapper.map(any(TipoPrestamoData.class), eq(TipoPrestamo.class))).thenReturn(model);

        Mono<TipoPrestamo> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(entity.getId()))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        TipoPrestamoData entity = TipoPrestamoData.builder().id(1L).build();
        TipoPrestamo model = TipoPrestamo.builder().id(1L).build();

        when(repository.findAll()).thenReturn(Flux.fromIterable(List.of(entity)));
        when(mapper.map(any(TipoPrestamoData.class), eq(TipoPrestamo.class))).thenReturn(model);

        Flux<TipoPrestamo> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(entity.getId()))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        TipoPrestamoData entity = TipoPrestamoData.builder().id(1L).build();
        TipoPrestamo model = TipoPrestamo.builder().id(1L).build();

        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(entity));
        when(mapper.map(any(TipoPrestamoData.class), eq(TipoPrestamo.class))).thenReturn(model);
        when(mapper.map(any(TipoPrestamo.class), eq(TipoPrestamoData.class))).thenReturn(entity);

        Flux<TipoPrestamo> result = repositoryAdapter.findByExample(model);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(entity.getId()))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        TipoPrestamoData entity = TipoPrestamoData.builder().id(1L).build();
        TipoPrestamo model = TipoPrestamo.builder().id(1L).build();

        when(repository.save(any(TipoPrestamoData.class))).thenReturn(Mono.just(entity));
        when(mapper.map(any(TipoPrestamoData.class), eq(TipoPrestamo.class))).thenReturn(model);
        when(mapper.map(any(TipoPrestamo.class), eq(TipoPrestamoData.class))).thenReturn(entity);

        Mono<TipoPrestamo> result = repositoryAdapter.save(model);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(entity.getId()))
                .verifyComplete();
    }
}
