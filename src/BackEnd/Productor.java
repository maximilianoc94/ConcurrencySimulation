package BackEnd;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maximiliano Casale
 */
public class Productor extends Thread {

    private Semaphore almacenar;
    private Semaphore mutex;
    private Semaphore retirar;
    private Almacen almacen;
    private boolean tipo;
    private int duracionDia;

    public Productor(boolean tipo, Semaphore almacenar, Semaphore mutex, Semaphore retirar, Almacen almacen, int duracionDia) {
        this.tipo = tipo;
        this.almacenar = almacenar;
        this.mutex = mutex;
        this.retirar = retirar;
        this.almacen = almacen;
        this.duracionDia = duracionDia;
    }

    @Override
    public void run() {
        while (true) {
            try {
                
                
                if (almacen.getEspacioOcupado() < almacen.getTamanoMaximo()) {
                    almacenar.acquire(1);
                    mutex.acquire(1);
                    almacen.setEspacioOcupado(almacen.getEspacioOcupado() + 1);
                    System.out.println("Almacene!:" + almacen.getEspacioOcupado());
                    mutex.release(1);
                    retirar.release(1);
                }
                                
                if (tipo) { //paquete y control true, consola false
                    Thread.sleep(duracionDia * 1000);
                } else {
                    Thread.sleep(3*duracionDia*1000);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Productor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    
}
