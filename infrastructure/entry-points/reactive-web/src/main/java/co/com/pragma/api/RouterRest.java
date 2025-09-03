package co.com.pragma.api;

import co.com.pragma.api.dto.PaginacionDataDto;
import co.com.pragma.api.dto.PrestamoRespuestaDto;
import co.com.pragma.api.dto.PrestamoSolicitudDto;
import co.com.pragma.api.exception.Error;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/v1/solicitudes",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "listenPOSTUseCase",
                    operation = @Operation(
                            operationId = "crearSolicitud",
                            summary = "Crear una nueva solicitud de préstamo",
                            description = "Este endpoint permite registrar una solicitud de préstamo en el sistema. "
                                    + "Requiere un tipo de préstamo válido, un plazo (meses) y un monto.",
                            tags = {"Solicitud"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = PrestamoSolicitudDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Solicitud creada exitosamente",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = PrestamoRespuestaDto.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    name = "Respuesta de solicitud de préstamo",
                                                                    value = "{ \"monto\": 2000000.00, \"plazo\": 12, \"email\": \"laura.mendez@gmail.com\", \"estadoId\": 1, \"estadoDescripcion\": \"PENDIENTE REVISION\", \"tipoPrestamoId\": 2 }"
                                                            )
                                                    }
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Datos inválidos en la solicitud",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = Error.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    name = "Datos inválidos",
                                                                    value = "{ \"codigo\": \"400\", \"mensaje\": \"El tipo de préstamo (24) no existe\" }"
                                                            )
                                                    }
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "Conflicto: existe solicitud de préstamo del mismo tipo activa",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = Error.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    name = "Existe solicitud activa",
                                                                    value = "{ \"codigo\": \"409\", \"mensaje\": \"Actualmente tiene una solicitud activa por el mismo tipo de préstamo\" }"
                                                            )
                                                    }
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/solicitudes",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "listenGETUseCase",
                    operation = @Operation(
                            operationId = "listarSolicitudes",
                            summary = "Listar solicitudes de préstamo",
                            description = "Este endpoint permite listar solicitudes de préstamo con filtros opcionales "
                                    + "como correo y tipo de préstamo. El resultado es paginado.",
                            tags = {"Solicitud"},
                            parameters = {
                                    @Parameter(name = "pagina", description = "Número de página (>=1)", required = true, example = "1"),
                                    @Parameter(name = "tamano", description = "Tamaño de página (>=1)", required = true, example = "10"),
                                    @Parameter(name = "correo", description = "Correo electrónico asociado a la solicitud", example = "laura.mendez@gmail.com"),
                                    @Parameter(name = "tipoPrestamoId", description = "ID del tipo de préstamo", example = "2")
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Listado de solicitudes",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = PaginacionDataDto.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    name = "Lista de solicitudes",
                                                                    value = """
                                                                            {
                                                                              "datos": [
                                                                                {
                                                                                  "monto": 2000000.00,
                                                                                  "plazo": 12,
                                                                                  "email": "laura.mendez@gmail.com",
                                                                                  "estadoId": 1,
                                                                                  "estadoDescripcion": "PENDIENTE REVISION",
                                                                                  "tipoPrestamoId": 2,
                                                                                  "tipoPrestamoDescripcion": "Libre inversión",
                                                                                  "nombreSolicitante": "Laura Mendez",
                                                                                  "salarioBaseSolicitante": 2500000.00,
                                                                                  "tasaInteres": 1.2,
                                                                                  "deudaTotalMensualSolicitudesAprobadas": 800000.00
                                                                                }
                                                                              ],
                                                                              "totalElementos": 1,
                                                                              "totalPaginas": 1
                                                                            }
                                                                            """
                                                            )
                                                    }
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Parámetros inválidos",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = Error.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    name = "Parámetro inválido",
                                                                    value = "{ \"codigo\": \"400\", \"mensaje\": \"El parámetro 'pagina' debe ser mayor a 0\" }"
                                                            )
                                                    }
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(SolicitudHandler solicitudHandler) {
        return route(POST("/v1/solicitudes"), solicitudHandler::listenPOSTUseCase)
                .andRoute(GET("/v1/solicitudes"), solicitudHandler::listenGETUseCase);
    }
}

