package facade;

public class Main {
    public static void main(String[] args) {
        AgenciaViajesFacade buscador = new AgenciaViajesFacade();

        buscador.buscarViajeCompleto("10/05", "15/05", "Cordoba", "Madrid");

   
        buscador.buscarViajeCompleto("12/05", "18/05", "Sevilla", "Barcelona");
    }
}