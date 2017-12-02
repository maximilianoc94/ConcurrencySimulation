
package BackEnd;

/**
 *
 * @author Maximiliano Casale
 */
public class Almacen {
    private int tamanoMaximo;
    private int espacioOcupado;
    
    public Almacen(int tamanoMaximo){
        this.tamanoMaximo = tamanoMaximo;
        espacioOcupado = 0;
            
    }

    public int getTamanoMaximo() {
        return tamanoMaximo;
    }

    public int getEspacioOcupado() {
        return espacioOcupado;
    }

    public void setTamanoMaximo(int tamanoMaximo) {
        this.tamanoMaximo = tamanoMaximo;
    }

    public void setEspacioOcupado(int espacioOcupado) {
        this.espacioOcupado = espacioOcupado;
    }
}
