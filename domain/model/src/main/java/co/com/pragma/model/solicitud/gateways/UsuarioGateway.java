package co.com.pragma.model.solicitud.gateways;

import co.com.pragma.model.solicitud.Usuario;
import reactor.core.publisher.Flux;

import java.util.List;

public interface UsuarioGateway {
    Flux<Usuario> obtenerPorListaCorreos(List<String> correos, String dataUsuario);
}
