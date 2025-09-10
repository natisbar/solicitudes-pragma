package co.com.pragma.usecase.generarsolicitud;

import co.com.pragma.model.solicitud.CuotaPago;
import co.com.pragma.model.solicitud.SolicitudPrestamo;
import co.com.pragma.model.solicitud.common.ex.NegocioException;
import co.com.pragma.model.solicitud.enums.Estado;
import co.com.pragma.model.solicitud.gateways.NotificacionGateway;
import co.com.pragma.model.solicitud.gateways.SolicitudPrestamoGateway;
import co.com.pragma.model.solicitud.gateways.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class ActualizarEstadoSolicitudUseCase {
    private final SolicitudPrestamoGateway solicitudPrestamoGateway;
    private final UsuarioGateway usuarioGateway;
    private final NotificacionGateway<SolicitudPrestamo> notificacionGateway;
    private static final Logger logger = Logger.getLogger(ActualizarEstadoSolicitudUseCase.class.getName());
    public static final String ESTADO_NO_ES_PARA_FINALIZAR = "El estado no es válido, debe ser RECHAZADO o APROBADO";
    public static final String SOLICITUD_FINALIZADA = "La solicitud ya fue finalizada.";
    public static final String SOLICITUD_NO_EXISTE = "La solicitud a actualizar no existe";
    public static final String SOLICITUD_ACTUALIZADA = "Solicitud actualizada correctamente";
    private static final BigDecimal CANTIDAD_SALARIOS_MAXIMOS = new BigDecimal(5);

    public Mono<String> ejecutar(SolicitudPrestamo solicitudPrestamo) {
        return Mono.just(solicitudPrestamo)
                .flatMap(solicitud -> {
                    List<Estado> estadosFinalizados = Estado.obtenerEstadosFinalizados();
                    if (estadosFinalizados.contains(solicitud.getEstado())){
                        return solicitudPrestamoGateway.obtenerPorId(solicitud.getId())
                                .flatMap(solicitudEncontrada ->
                                                usuarioGateway.obtenerPorListaCorreos(List.of(solicitudEncontrada.getCorreo()), solicitud.getDataUsuario())
                                                        .next()
                                                        .flatMap(usuario -> validarEstadoPrevioYActualizar(solicitud.toBuilder()
                                                                .tipoPrestamoId(solicitudEncontrada.getTipoPrestamoId())
                                                                .tipoPrestamo(solicitudEncontrada.getTipoPrestamo())
                                                                .monto(solicitudEncontrada.getMonto())
                                                                .plazo(solicitudEncontrada.getPlazo())
                                                                .correo(solicitudEncontrada.getCorreo()).solicitante(usuario)
                                                                .build(), solicitudEncontrada.getEstado()))
                                                        .doOnSuccess(solicitudFinal -> {
                                                            if (Estado.APROBADO.equals(solicitudFinal.getEstado())){
                                                                solicitudFinal.setPlanDePago(generarPlanDePago(solicitudFinal));
                                                            }
                                                            if (Estado.obtenerEstadosFinalizados().contains(solicitudFinal.getEstado())){
                                                                notificacionGateway.responder(solicitudFinal)
                                                                        .subscribeOn(Schedulers.boundedElastic())
                                                                        .subscribe(
                                                                                messageId -> logger.info("Mensaje enviado correctamente. ID: " + messageId),
                                                                                error -> logger.log(Level.SEVERE, "Error enviando notificación", error)
                                                                        );
                                                            }
                                                        })
                                                        .thenReturn(SOLICITUD_ACTUALIZADA)
                                )
                                .switchIfEmpty(Mono.error(new NegocioException(SOLICITUD_NO_EXISTE)));
                    }
                    return Mono.error(new NegocioException(ESTADO_NO_ES_PARA_FINALIZAR));
                })
                .doOnError(error -> logger.log(Level.SEVERE, "Error enviando notificación", error));
    }

    private Mono<SolicitudPrestamo> validarEstadoPrevioYActualizar(SolicitudPrestamo solicitudActualizar, Estado estadoPrevio){
        return Mono.just(estadoPrevio)
                .flatMap(estado -> {
                    if (Estado.PENDIENTE.equals(estadoPrevio)){
                        if (Estado.APROBADO.equals(solicitudActualizar.getEstado()) &&
                                solicitudActualizar.getMonto().compareTo(solicitudActualizar.getSolicitante().getSalarioBase().multiply(CANTIDAD_SALARIOS_MAXIMOS)) > 0){
                            return Mono.just(solicitudActualizar.toBuilder().estadoId(Estado.REVISION.getId()).estado(Estado.REVISION).build());
                        }
                        return Mono.just(solicitudActualizar);
                    }
                    else if (Estado.REVISION.equals(estadoPrevio)){
                        return Mono.just(solicitudActualizar);
                    }
                    else return Mono.error(new NegocioException(SOLICITUD_FINALIZADA));
                })
                .flatMap(solicitudFinal -> solicitudPrestamoGateway.actualizarEstado(solicitudFinal)
                        .thenReturn(solicitudFinal));
    }

    private List<CuotaPago> generarPlanDePago(SolicitudPrestamo solicitud) {
        BigDecimal monto = solicitud.getMonto(); // Monto total
        int plazo = solicitud.getPlazo();        // Número de cuotas (meses)
        BigDecimal tasaInteresAnual = solicitud.getTipoPrestamo().getTasaInteres();

        // tasa mensual = anual / 12 / 100
        BigDecimal tasaMensual = tasaInteresAnual
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        // Fórmula de cuota mensual (sistema francés)
        BigDecimal unoMasTasa = tasaMensual.add(BigDecimal.ONE);
        BigDecimal factorPotencia = unoMasTasa.pow(plazo);
        BigDecimal cuotaMensual = monto
                .multiply(tasaMensual.multiply(factorPotencia))
                .divide(factorPotencia.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        BigDecimal saldoPendiente = monto;
        List<CuotaPago> planDePago = new ArrayList<>();

        for (int i = 1; i <= plazo; i++) {
            BigDecimal interes = saldoPendiente.multiply(tasaMensual).setScale(2, RoundingMode.HALF_UP);
            BigDecimal capital = cuotaMensual.subtract(interes).setScale(2, RoundingMode.HALF_UP);
            saldoPendiente = saldoPendiente.subtract(capital).setScale(2, RoundingMode.HALF_UP);

            CuotaPago cuota = new CuotaPago();
            cuota.setNumeroCuota(i);
            cuota.setCuota(cuotaMensual);
            cuota.setCapital(capital);
            cuota.setInteres(interes);
            cuota.setSaldoRestante(saldoPendiente.max(BigDecimal.ZERO)); // Evita que baje de cero por redondeo

            planDePago.add(cuota);
        }

        return planDePago;
    }

}
