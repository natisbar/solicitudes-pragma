package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.r2dbc.mapper.SolicitudPrestamoMapper;
import co.com.pragma.r2dbc.model.dto.SolicitudPrestamoDto;
import co.com.pragma.r2dbc.model.entities.SolicitudPrestamoData;
import co.com.pragma.r2dbc.repository.SolicitudPrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
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
    SolicitudPrestamoMapper solicitudPrestamoMapper;

    @Mock
    ObjectMapper mapper;

    private SolicitudPrestamoDto dto;
    private SolicitudPrestamo domain;

    @BeforeEach
    void setUp() {
        dto = new SolicitudPrestamoDto();
        dto.setIdsolicitud(1L);
        dto.setEmail("test@test.com");

        domain = new SolicitudPrestamo();
        domain.setId(1L);
        domain.setCorreo("test@test.com");
    }


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
        when(repository.existsByCorreoAndTipoPrestamoIdAndEstadoIdNotIn(anyString(), anyLong(), anyList())).thenReturn(Mono.just(true));

        Mono<Boolean> result = repositoryAdapter.existePorEmailYTipoPrestamoIdSinFinalizar("correo@corre.com", 1L, List.of(1L));

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(Boolean.TRUE))
                .verifyComplete();
    }

    @Test
    void debeActualizarEstado() {
        SolicitudPrestamo model = SolicitudPrestamo.builder().id(1L).estadoId(4L).build();
        when(repository.actualizarEstado(anyLong(), anyLong())).thenReturn(Mono.empty());

        Mono<Void> result = repositoryAdapter.actualizarEstado(model);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void obtenerPorId() {
        SolicitudPrestamoDto dto = SolicitudPrestamoDto.builder().idsolicitud(1L).build();
        SolicitudPrestamo model = SolicitudPrestamo.builder().id(1L).build();

        when(repository.findByIdWithJoin(1L)).thenReturn(Mono.just(dto));
        when(solicitudPrestamoMapper.convertirDesde(any(SolicitudPrestamoDto.class))).thenReturn(model);

        Mono<SolicitudPrestamo> result = repositoryAdapter.obtenerPorId(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(dto.getIdsolicitud()))
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

    @Test
    void obtenerPorEstados_debeRetornarFlux() {
        List<Long> estados = List.of(1L, 2L);
        when(repository.findByEstadoIdIn(estados, 0, 10))
                .thenReturn(Flux.just(dto));
        when(solicitudPrestamoMapper.convertirDesde(any(SolicitudPrestamoDto.class))).thenReturn(domain);

        Flux<SolicitudPrestamo> resultado = repositoryAdapter.obtenerPorEstados(estados, 0, 10);

        StepVerifier.create(resultado)
                .expectNext(domain)
                .verifyComplete();

        verify(repository).findByEstadoIdIn(estados, 0, 10);
    }

    @Test
    void obtenerPorEstadosYCorreoYTipoPrestamoId_debeRetornarFlux() {
        List<Long> estados = List.of(1L);
        when(repository.findByEstadoIdInAndCorreoAndTipoPrestamoId(estados, "test@test.com", 99L, 0, 5))
                .thenReturn(Flux.just(dto));
        when(solicitudPrestamoMapper.convertirDesde(any(SolicitudPrestamoDto.class))).thenReturn(domain);

        Flux<SolicitudPrestamo> resultado = repositoryAdapter.obtenerPorEstadosYCorreoYTipoPrestamoId(estados, "test@test.com", 99L, 0, 5);

        StepVerifier.create(resultado)
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void obtenerPorEstadosYCorreo_debeRetornarFlux() {
        List<Long> estados = List.of(2L);
        when(repository.findByEstadoIdInAndCorreo(estados, "test@test.com", 1, 20))
                .thenReturn(Flux.just(dto));
        when(solicitudPrestamoMapper.convertirDesde(any(SolicitudPrestamoDto.class))).thenReturn(domain);

        Flux<SolicitudPrestamo> resultado = repositoryAdapter.obtenerPorEstadosYCorreo(estados, "test@test.com", 1, 20);

        StepVerifier.create(resultado)
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void obtenerPorEstadosYTipoPrestamoId_debeRetornarFlux() {
        List<Long> estados = List.of(3L);
        when(repository.findByEstadoIdInAndTipoPrestamoId(estados, 88L, 2, 15))
                .thenReturn(Flux.just(dto));
        when(solicitudPrestamoMapper.convertirDesde(any(SolicitudPrestamoDto.class))).thenReturn(domain);

        Flux<SolicitudPrestamo> resultado = repositoryAdapter.obtenerPorEstadosYTipoPrestamoId(estados, 88L, 2, 15);

        StepVerifier.create(resultado)
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void contarPorEstados_debeRetornarMono() {
        List<Long> estados = List.of(1L, 2L);
        when(repository.countByEstadoIdIn(estados)).thenReturn(Mono.just(5L));

        StepVerifier.create(repositoryAdapter.contarPorEstados(estados))
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    void contarPorEstadosYCorreoYTipoPrestamoId_debeRetornarMono() {
        List<Long> estados = List.of(1L);
        when(repository.countByEstadoIdInAndCorreoAndTipoPrestamoId(estados, "test@test.com", 77L))
                .thenReturn(Mono.just(3L));

        StepVerifier.create(repositoryAdapter.contarPorEstadosYCorreoYTipoPrestamoId(estados, "test@test.com", 77L))
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void contarPorEstadosYCorreo_debeRetornarMono() {
        List<Long> estados = List.of(2L);
        when(repository.countByEstadoIdInAndCorreo(estados, "test@test.com"))
                .thenReturn(Mono.just(2L));

        StepVerifier.create(repositoryAdapter.contarPorEstadosYCorreo(estados, "test@test.com"))
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void contarPorEstadosYTipoPrestamoId_debeRetornarMono() {
        List<Long> estados = List.of(3L);
        when(repository.countByEstadoIdInAndTipoPrestamoId(estados, 55L))
                .thenReturn(Mono.just(1L));

        StepVerifier.create(repositoryAdapter.contarPorEstadosYTipoPrestamoId(estados, 55L))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void obtenerDeudaTotalMensualSolicitudesAprobadas_debeRetornarMono() {
        when(repository.obtenerDeudaTotalMensualSolicitudesAprobadas("test@test.com"))
                .thenReturn(Mono.just(BigDecimal.TEN));

        StepVerifier.create(repositoryAdapter.obtenerDeudaTotalMensualSolicitudesAprobadas("test@test.com"))
                .expectNext(BigDecimal.TEN)
                .verifyComplete();
    }
}
