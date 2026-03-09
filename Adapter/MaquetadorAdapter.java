import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;


public class MaquetadorAdapter implements IMaquetadorAvanzado{

    private MaquetadorBasico maquetador;
    
    private int ContarLineas(File fichero){
        int cont = 0;

        try(BufferedReader lineas = new BufferedReader(new FileReader(fichero))){
            String leido;
            
            while((leido = lineas.readLine()) != null){
                cont++;
            }


        }catch(IOException e){
            System.out.println("Error en la funcion ContarLineas");
        }

    return cont;
        
    }

    public MaquetadorAdapter(){
        this.maquetador = new MaquetadorBasico();
    }

    @Override
    public File UnirFicheros(File fichero1, File fichero2){
       
        File fich = new File("pruebas/Union_"+ fichero1.getName().replace(".txt","_") + fichero2.getName() );
        String texto1,texto2;
        int fin1,fin2;

       try{
            fin1 = ContarLineas(fichero1);
            fin2 = ContarLineas(fichero2);

            texto1 = maquetador.Extraer(fichero1,1,fin1);
            texto2 = maquetador.Extraer(fichero2,1,fin2);

            maquetador.AnadirTexto(fich,texto1);
            maquetador.AnadirTexto(fich,texto2);
        
         }catch(Exception e){

            System.out.println("Error en la funcion UnirFicheros");
            return null;
       }
       
       return fich;

    }

    @Override
    public File CombinarFicheros(File fichero1, File fichero2, int inicio, int fin){
        
        boolean extraer = true;
        
        File res = new File("pruebas/Combinar_" + fichero1.getName().replace(".txt", "_") + fichero2.getName() ); 

        try{
        int fin1 = ContarLineas(fichero1);
        int fin2 = ContarLineas(fichero2);

        String texto = "";
        int salto = (fin-inicio)+1;
        
            if(fin > fin1 && fin > fin2){

                System.out.println("Error: El valor de los indices es mayor al de los ficheros\n");
                return null;

            }


            while( inicio <= fin1 || inicio <= fin2){

                if(extraer == true){
               
                    if(inicio <= fin1){
                
                        texto += maquetador.Extraer(fichero1, inicio, fin);
                    
                
                     }
                extraer = false;

                }else {

                    if(inicio <= fin2){
                        texto += maquetador.Extraer(fichero2, inicio, fin);
                        maquetador.AnadirTexto(res, texto);
                
                     }

                extraer = true;
                }

                 if(extraer == true){
                    inicio = fin+1;
                    fin += salto;
                    }

            }
            
            maquetador.AnadirTexto(res,texto);    

        }catch(Exception e){
        System.out.println("Error en la funcion CombinarFicheros");
        }        
        
        return res;
    }

    @Override
    public void SepararFicheros(File fichero1, File fichero2, int division){

    }

}
