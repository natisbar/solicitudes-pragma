package co.com.pragma.model.solicitud;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(toBuilder = true)
public class FiltroData {
    private String correo;
    private Long tipoPrestamoId;
    private int pagina;
    private int tamano;
    private String dataUsuario;

    public boolean todoNull(){
        return tipoPrestamoId == null && esVacio(correo);
    }

    public boolean todoNoNull(){
        return tipoPrestamoId != null && !esVacio(correo);
    }

    public boolean existeSoloCorreo(){
        return !esVacio(correo) && tipoPrestamoId == null;
    }

    public boolean esVacio(String correo){
        return correo == null || correo.isEmpty();
    }
}

