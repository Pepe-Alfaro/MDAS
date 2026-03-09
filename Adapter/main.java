import java.io.File;

public class main {

    public static void main(String[] args){
    MaquetadorBasico MaquetadorBa = new MaquetadorBasico();
    MaquetadorAdapter MaquetadorAd = new MaquetadorAdapter();

    /*//PRUEBAS MAQUETADOR_BASICO
    //1ª Prueba -- AñadirTexto
    File fichero = new File("pruebas/prueba.txt");
    MaquetadorBa.AnadirTexto(fichero, "Si funciona menos mal");

    //2ª Prueba -- ExtraerTexto
    String result = MaquetadorBa.Extraer(fichero,3,5);
    System.out.println("2ºTest --- Resultado \n"+ result);

    //3ª Prueba -- Dividir
    MaquetadorBa.Dividir(fichero,4);
        
    //PRUEBAS MAQUETADOR_ADAPTER
    File fichero2 = new File("pruebas/prueba2.txt");
    
    //1ª Prueba -- Unirficheros
    MaquetadorAd.UnirFicheros(fichero, fichero2);
    */
    //2ª Prueba -- CombinarFicheros
    File fichero = new File("pruebas/fich1.txt");
    File fichero2 = new File("pruebas/fich2.txt");
    MaquetadorAd.CombinarFicheros(fichero, fichero2, 1,5 );
    



    }


}


