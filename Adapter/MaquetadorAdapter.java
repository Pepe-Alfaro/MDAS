import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

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
    public void SepararFicheros(File fichero1, int... divisiones){
        MaquetadorAdapter maqueta = new MaquetadorAdapter();
        int inicio;
        int fin;
        Arrays.sort(divisiones);
        int n = divisiones.length;

        String texto = maquetador.Extraer(fichero1, 1, divisiones[0]-1);
        File fichero = new File("pruebas/Separar-1_"+fichero1.getName());
        maquetador.AnadirTexto(fichero, texto);


        for(int i = 0 ; i < n-1 ; i++){
        inicio = divisiones[i];
        fin = divisiones[i+1]-1;
        
        texto = maquetador.Extraer(fichero1, inicio, fin);
        fichero = new File("pruebas/Separar-"+(i+2)+"_"+fichero1.getName());
        maquetador.AnadirTexto(fichero, texto);

        }

        if(divisiones[n-1] <= maqueta.ContarLineas(fichero1)){
        texto = maquetador.Extraer(fichero1, divisiones[n-1],Integer.MAX_VALUE );
        fichero = new File("pruebas/Seperar-"+(divisiones.length+1)+"_"+fichero1.getName());
        maquetador.AnadirTexto(fichero, texto);
        
        }else{
            System.out.println("El último fichero no se creará ya que el corte "+ divisiones[n-1] +" es mayor al numero de lineas del fichero\n");
        }

    }

}

