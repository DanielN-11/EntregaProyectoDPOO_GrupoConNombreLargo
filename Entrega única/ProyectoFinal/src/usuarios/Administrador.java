package usuarios;
import Cafeteria.SolicitudCambioTurno;

public class Administrador extends Usuario {

    public Administrador(String idUsuario, String login, String password, String nombre) {
        super(idUsuario, login, password, nombre);
    }
    
    public void aprobarSolicitud(SolicitudCambioTurno solicitud) {
        solicitud.aprobar();
    }

    public void rechazarSolicitud(SolicitudCambioTurno solicitud) {
        solicitud.rechazar();
    }
}