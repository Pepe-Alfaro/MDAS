import java.io.File;


public interface IMaquetadorAvanzado{
    public File UnirFicheros(File fichero1, File fichero2);
    public File CombinarFicheros(File fichero1, File fichero2, int inicio , int fin);
    public void SepararFicheros(File fichero1, File fichero2, int division);

}