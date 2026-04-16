package facade;
import java.util.List;

public class AgenciaViajesFacade {
    private ServicioTransporte transporte = new ServicioTransporte();
    private ServicioAlojamiento alojamiento = new ServicioAlojamiento();
    private IOficinaTurismo oficinaMadrid = new OficinaTurismoFecha();
    private IOficinaTurismo oficinaBarcelona = new OficinaTurismoEvento();

    public void buscarViajeCompleto(String fechaInicio, String fechaFin, String origen, String destino) {
        System.out.println("=== BUSCANDO PAQUETE GRUPAL PARA " + destino.toUpperCase() + " ===");
        
       
        List<Transporte> tDisp = transporte.buscar(origen, destino, fechaInicio);
        System.out.println("Transportes disponibles:");
        for (Transporte t : tDisp) System.out.println(" - " + t.medio + " (" + t.precio + "€)");

        
        List<Alojamiento> aDisp = alojamiento.buscar(destino, fechaInicio);
        System.out.println("Alojamientos disponibles:");
        for (Alojamiento a : aDisp) System.out.println(" - " + a.hotel + " (" + a.precioNoche + "€/noche)");

 
        System.out.println("Actividades culturales sugeridas:");
        if (destino.equalsIgnoreCase("Madrid")) {
            for (String act : oficinaMadrid.buscarActividades(destino, fechaInicio)) System.out.println(" * " + act);
        } else {
            for (String act : oficinaBarcelona.buscarActividades(destino, "Cultura")) System.out.println(" * " + act);
        }
        System.out.println("============================================\n");
    }
}