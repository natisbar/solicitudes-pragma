package co.com.pragma.api;

import co.com.pragma.api.dto.PrestamoRespuestaDto;
import co.com.pragma.api.dto.PrestamoSolicitudDto;
import co.com.pragma.api.exception.Error;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperation(
            operation = @Operation(
                    operationId = "crearSolicitud",
                    summary = "Crear una nueva solicitud de prestamo",
                    description = "Este endpoint permite registrar una solicitud de prestamo en el sistema. "
                            + "Requiere un tipo de prestamo válido, un plazo (meses) y un monto",
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
                                                            name = "Respuesta de solicitud prestamo",
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
                                                            name = "Datos inválidos en la solicitud",
                                                            value = "{ \"codigo\": \"400\", \"mensaje\": \"El tipo de prestamo (24) no existe\" }"
                                                    )
                                            }
                                    )
                            ),
                            @ApiResponse(
                                    responseCode = "409",
                                    description = "Conflicto: existe solicitud de prestamo del mismo tipo activa",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = Error.class),
                                            examples = {
                                                    @ExampleObject(
                                                            name = "Existe solicitud de prestamo activa por el mismo tipo",
                                                            value = "{ \"codigo\": \"409\", \"mensaje\": \"Actualmente tiene una solicitud activa por el mismo tipo de prestamo\" }"
                                                    )
                                            }
                                    )
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/v1/solicitudes"), handler::listenPOSTUseCase);
    }
}
