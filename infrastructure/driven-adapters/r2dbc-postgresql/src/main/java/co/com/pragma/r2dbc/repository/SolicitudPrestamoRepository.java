package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.model.dto.SolicitudPrestamoDto;
import co.com.pragma.r2dbc.model.entities.SolicitudPrestamoData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface SolicitudPrestamoRepository extends ReactiveCrudRepository<SolicitudPrestamoData, Long>, ReactiveQueryByExampleExecutor<SolicitudPrestamoData> {

    Mono<Boolean> existsByCorreoAndTipoPrestamoIdAndEstadoIdNotIn(String correo, long tipoPrestamoId, List<Long> estados);

    Mono<Long> countByEstadoIdIn(List<Long> estados);

    Mono<Long> countByEstadoIdInAndCorreoAndTipoPrestamoId(List<Long> estados, String correo, Long tipoPrestamoId);

    Mono<Long> countByEstadoIdInAndCorreo(List<Long> estados, String correo);

    Mono<Long> countByEstadoIdInAndTipoPrestamoId(List<Long> estados, Long tipoPrestamoId);

    @Query("""
            UPDATE solicitudes.solicitud
             SET id_estado = :idEstado
            WHERE id_solicitud = :idSolicitud
            """)
    Mono<Void> actualizarEstado(@Param("idEstado") Long idEstado, @Param("idSolicitud") Long idSolicitud);

    @Query("""
            SELECT
                COALESCE(SUM(monto / plazo), 0) AS deuda_total_mensual_solicitudes_aprobadas
            FROM solicitudes.solicitud
            WHERE id_estado = 4
                AND email = :correo
            """)
    Mono<BigDecimal> obtenerDeudaTotalMensualSolicitudesAprobadas(@Param("correo") String correo);

    @Query("""
            SELECT s.id_solicitud as idsolicitud,
                s.monto as monto,
                s.plazo as plazo,
                s.email as email,
                s.id_estado as idestado,
                e.nombre as estado,
                s.id_tipo_prestamo as idtipoprestamo,
                tp.nombre as tipoprestamo,
                tp.tasa_interes as tasainteres
            FROM solicitudes.solicitud AS s
            INNER JOIN solicitudes.estado as e ON e.id_estado = s.id_estado
            INNER JOIN solicitudes.tipo_prestamo as tp ON tp.id_tipo_prestamo = s.id_tipo_prestamo
            WHERE s.id_estado IN (:estados)
            LIMIT :tamano OFFSET :pagina
            """)
    Flux<SolicitudPrestamoDto> findByEstadoIdIn(@Param("estados") List<Long> estados, @Param("pagina") int pagina,
                                                @Param("tamano")int tamano);

    @Query("""
            SELECT s.id_solicitud as idsolicitud,
                s.monto as monto,
                s.plazo as plazo,
                s.email as email,
                s.id_estado as idestado,
                e.nombre as estado,
                s.id_tipo_prestamo as idtipoprestamo,
                tp.nombre as tipoprestamo,
                tp.tasa_interes as tasainteres
            FROM solicitudes.solicitud AS s
            INNER JOIN solicitudes.estado as e ON e.id_estado = s.id_estado
            INNER JOIN solicitudes.tipo_prestamo as tp ON tp.id_tipo_prestamo = s.id_tipo_prestamo
            WHERE s.id_estado IN (:estados)
                AND s.email = :correo
                AND s.id_tipo_prestamo = :prestamoId
            LIMIT :tamano OFFSET :pagina
            """)
    Flux<SolicitudPrestamoDto> findByEstadoIdInAndCorreoAndTipoPrestamoId(@Param("estados") List<Long> estados, @Param("correo") String correo,
                                                                          @Param("prestamoId") long prestamoId, @Param("pagina") int pagina,
                                                                          @Param("tamano") int tamano);

    @Query("""
            SELECT s.id_solicitud as idsolicitud,
                s.monto as monto,
                s.plazo as plazo,
                s.email as email,
                s.id_estado as idestado,
                e.nombre as estado,
                s.id_tipo_prestamo as idtipoprestamo,
                tp.nombre as tipoprestamo,
                tp.tasa_interes as tasainteres
            FROM solicitudes.solicitud AS s
            INNER JOIN solicitudes.estado as e ON e.id_estado = s.id_estado
            INNER JOIN solicitudes.tipo_prestamo as tp ON tp.id_tipo_prestamo = s.id_tipo_prestamo
            WHERE s.id_estado IN (:estados)
                AND s.email = :correo
            LIMIT :tamano OFFSET :pagina
            """)
    Flux<SolicitudPrestamoDto> findByEstadoIdInAndCorreo(@Param("estados") List<Long> estados, @Param("correo") String correo,
                                                         @Param("pagina") int pagina, @Param("tamano") int tamano);

    @Query("""
            SELECT s.id_solicitud as idsolicitud,
                s.monto as monto,
                s.plazo as plazo,
                s.email as email,
                s.id_estado as idestado,
                e.nombre as estado,
                s.id_tipo_prestamo as idtipoprestamo,
                tp.nombre as tipoprestamo,
                tp.tasa_interes as tasainteres
            FROM solicitudes.solicitud AS s
            INNER JOIN solicitudes.estado as e ON e.id_estado = s.id_estado
            INNER JOIN solicitudes.tipo_prestamo as tp ON tp.id_tipo_prestamo = s.id_tipo_prestamo
            WHERE s.id_estado IN (:estados)
                AND s.id_tipo_prestamo = :prestamoId
            LIMIT :tamano OFFSET :pagina
            """)
    Flux<SolicitudPrestamoDto> findByEstadoIdInAndTipoPrestamoId(@Param("estados") List<Long> estados, @Param("prestamoId") long prestamoId,
                                                                 @Param("pagina") int pagina, @Param("tamano") int tamano);

}
