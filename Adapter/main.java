import java.io.File;

public class main {

    public static void main(String[] args){
    MaquetadorBasico miMaquetador = new MaquetadorBasico();

    //1ª Prueba
    File fichero = new File("pruebas/prueba.txt");
    miMaquetador.AnadirTexto(fichero, "Si funciona menos mal");

    //2ª Prueba
    String result = miMaquetador.Extraer(fichero,3,5);
    System.out.println("2ºTest --- Resultado \n"+ result);

    //3ª Prueba
    miMaquetador.Dividir(fichero,4);
        
    }
}


