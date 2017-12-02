package BackEnd;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maximiliano Casale
 */
public class Gerente extends Thread {


    private Semaphore mutexCronometro;

    private int[] diasFaltantes = new int[1];
    private boolean dormido;
    private int[] almacenEnsamblados = new int[1];
    private final int duracionDia;
    public Gerente(Semaphore mutexCronometro, int[] diasFaltantes, int[] almacenEnsamblados, int duracionDia) {

        this.mutexCronometro = mutexCronometro;
        this.duracionDia = duracionDia;
        this.diasFaltantes = diasFaltantes;
        this.dormido = true;
        this.almacenEnsamblados = almacenEnsamblados;
    }

    @Override
    public void run() {
        while (true) {
            int tiempoADormir = new Random().nextInt(duracionDia*(3000/4))+ (duracionDia*250);
            try {
                Thread.sleep(tiempoADormir);
                mutexCronometro.acquire(1);
                dormido = false;
                if (diasFaltantes[0] == 0) {
                    almacenEnsamblados[0] = 0;
                    System.out.println("Gerente: Despachado!");
                }
                mutexCronometro.release(1);
                dormido = true;
            } catch (InterruptedException ex) {
                Logger.getLogger(Gerente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public Semaphore getMutexCronometro() {
        return mutexCronometro;
    }

    public int[] getDiasFaltantes() {
        return diasFaltantes;
    }

    public boolean isDormido() {
        return dormido;
    }

    public int[] getAlmacenEnsamblados() {
        return almacenEnsamblados;
    }

    public int getDuracionDia() {
        return duracionDia;
    }

    
    
}
