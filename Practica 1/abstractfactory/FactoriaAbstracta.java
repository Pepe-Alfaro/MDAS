package abstractfactory;
import java.util.List;
public abstract class FactoriaAbstracta {

    public abstract Menu crearMenuSemanal(List<Plato> platos,Acompanamiento side);
    public abstract Menu crearMenuTemporada(List<Plato> platos,Acompanamiento side);
}