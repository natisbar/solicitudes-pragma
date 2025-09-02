package co.com.pragma.consumer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder(toBuilder = true)
@Setter
@Getter
public class LoginDto {
    String correoElectronico;
    String contrasena;
}
