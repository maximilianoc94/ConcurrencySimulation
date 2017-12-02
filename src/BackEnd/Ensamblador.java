package BackEnd;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maximiliano Casale
 */
public class Ensamblador extends Thread {

    private Semaphore almacenarControles;
    private Semaphore almacenarConsolas;
    private Semaphore almacenarPaquetes;
    private Semaphore retirarConsolas;
    private Semaphore retirarControles;
    private Semaphore retirarPaquetes;
    private Semaphore mutexControles;
    private Semaphore mutexConsolas;
    private Semaphore mutexPaquetes;
    private Almacen almacenConsolas;
    private Almacen almacenControles;
    private Almacen almacenPaquetes;
    private int duracionDia;
    private int[] almacenEnsamblados;

    public Ensamblador(Semaphore almacenarControles, Semaphore almacenarConsolas, Semaphore almacenarPaquetes, Semaphore retirarConsolas, Semaphore retirarControles, Semaphore retirarPaquetes, Semaphore mutexControles, Semaphore mutexConsolas, Semaphore mutexPaquetes, Almacen almacenConsolas, Almacen almacenControles, Almacen almacenPaquetes, int duracionDia, int[] almacenEnsamblados) {
        this.almacenarControles = almacenarControles;
        this.almacenarConsolas = almacenarConsolas;
        this.almacenarPaquetes = almacenarPaquetes;
        this.retirarConsolas = retirarConsolas;
        this.retirarControles = retirarControles;
        this.retirarPaquetes = retirarPaquetes;
        this.mutexControles = mutexControles;
        this.mutexConsolas = mutexConsolas;
        this.mutexPaquetes = mutexPaquetes;
        this.almacenConsolas = almacenConsolas;
        this.almacenControles = almacenControles;
        this.almacenPaquetes = almacenPaquetes;
        this.duracionDia = duracionDia;
        this.almacenEnsamblados = almacenEnsamblados;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(2*duracionDia*1000);
                    
                if (almacenConsolas.getEspacioOcupado() >= 1
                        && almacenControles.getEspacioOcupado() >= 2
                        && almacenPaquetes.getEspacioOcupado() >= 1) {
                    retirarControles.acquire(1);
                    retirarConsolas.acquire(1);
                    retirarPaquetes.acquire(1);
                    mutexControles.acquire(1);
                    mutexConsolas.acquire(1);
                    mutexPaquetes.acquire(1);
                    almacenControles.setEspacioOcupado(almacenControles.getEspacioOcupado() - 2);
                    almacenConsolas.setEspacioOcupado(almacenConsolas.getEspacioOcupado() - 1);
                    almacenPaquetes.setEspacioOcupado(almacenPaquetes.getEspacioOcupado() - 1);
                    almacenEnsamblados[0]++;
                    System.out.println("Ensamblados:" + almacenEnsamblados[0]);
                    mutexControles.release(1);
                    mutexConsolas.release(1);
                    mutexPaquetes.release(1);
                almacenarControles.release(2);
                almacenarConsolas.release(1);  
                almacenarPaquetes.release(1);  
                }
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Productor.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
