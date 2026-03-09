import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;


public class MaquetadorBasico {



public void AnadirTexto(File fichero,String texto){
    boolean vacio = false;
    
    if(fichero.length() == 0){
        vacio = true;
    }

    try(FileWriter fich = new FileWriter(fichero,true)){
        
        if( vacio == false){
        fich.write("\n"+texto.trim());
        fich.close();
        
        }else{ fich.write(texto.trim());}

    }catch (IOException e){
        System.out.println("Error en la funcion Añadir\n");
        
    }
}

public String Extraer(File fichero,int inicio, int fin){
    
    String parrafo = "";

    try(BufferedReader linea = new BufferedReader(new FileReader(fichero))) {
        String leido;
        int cont = 1;
        while((leido = linea.readLine()) != null){
          
          
          if(cont >= inicio && cont <= fin ){
            parrafo = parrafo +  leido + "\n";

          }
          cont++;
        }

    }catch(IOException e){
        System.out.println("Error en la fucnion Extraer\n");
    }
    return parrafo;
}



public void Dividir(File fichero, int division){

    try(BufferedReader lineas = new BufferedReader(new FileReader(fichero));
        FileWriter fich1 = new FileWriter("pruebas/parte1_"+fichero.getName(),false );
        FileWriter fich2 = new FileWriter("pruebas/parte2_"+fichero.getName(),false)){

        String leido;
        int cont = 1;

        while ((leido = lineas.readLine() ) != null){
            
            if(cont >= division){
                
                fich2.write(leido + "\n");
        
            }else{
             
                fich1.write(leido + "\n");
            
            }
            cont ++;
        }

    }catch(IOException e){
        System.out.println("Error en la funcion division\n");
    }
    

}
}


