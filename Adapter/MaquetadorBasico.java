import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;


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
        System.out.println("Error en la funcion Extraer\n");
    }
    return parrafo;
}



public List<File> Dividir(File fichero, int division){
    List<File> lista = new ArrayList<>();
    File fich1 = new File("pruebas/division1"+fichero.getName());
    File fich2 = new File("pruebas/division2"+fichero.getName());


    try(BufferedReader lineas = new BufferedReader(new FileReader(fichero));
        FileWriter fw1 = new FileWriter(fich1,false);
        FileWriter fw2 = new FileWriter(fich2,false)){

        String leido;
        int cont = 1;

        while ((leido = lineas.readLine() ) != null){
            
            if(cont >= division){
                
                fw2.write(leido + "\n");
        
            }else{
             
                fw1.write(leido + "\n");
            
            }
            cont ++;
        }
        lista.add(fich1);
        lista.add(fich2);
        
    }catch(IOException e){
        System.out.println("Error en la funcion division\n");
    }
    
 
    return lista;
}
}


