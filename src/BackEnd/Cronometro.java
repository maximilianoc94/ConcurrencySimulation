package BackEnd;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maximiliano Casale
 */
public class Cronometro extends Thread {

    private Semaphore mutexCronometro;
    private final int diasDespacho;
    private int[] diasFaltantes = new int[1];
    private final int duracionDia;
    private boolean dormido;

    public Cronometro(Semaphore mutexCronometro, int[] diasFaltantes, int duracionDia) {

        this.mutexCronometro = mutexCronometro;
        this.diasFaltantes = diasFaltantes;
        this.duracionDia = duracionDia;
        this.dormido = true;
        this.diasDespacho = diasFaltantes[0];
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(duracionDia * 1000);
                mutexCronometro.acquire(1);
                dormido = false;
                if (diasFaltantes[0] == 0) {
                    diasFaltantes[0] = diasDespacho;
                }
                diasFaltantes[0]--;
                System.out.println("Dias Faltantes para Despacho:" + diasFaltantes[0]);
                sleep((duracionDia / 20) * 1000);
                mutexCronometro.release(1);
                dormido = true;
            } catch (InterruptedException ex) {
                Logger.getLogger(Cronometro.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public boolean isDormido() {
        return dormido;
    }

    public int getDiasFaltantes() {
        return diasFaltantes[0];
    }


}
