/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BackEnd;
//Librerias
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Max
 */
public class App {

    // Semaphores
    private Semaphore mutexConsolas = new Semaphore(1);
    private Semaphore mutexControles = new Semaphore(1);
    private Semaphore mutexPaquetes = new Semaphore(1);
    private Semaphore almacenarConsolas = new Semaphore(10); //esto no se esta manejando bien aparentemente
    private Semaphore almacenarControles = new Semaphore(10);
    private Semaphore almacenarPaquetes = new Semaphore(10);
    private Semaphore retirarConsolas = new Semaphore(0);
    private Semaphore retirarControles = new Semaphore(0);
    private Semaphore retirarPaquetes = new Semaphore(0);
    private Semaphore mutexCronometro = new Semaphore(1);
    // Productores
    private Productor[] listaProductoresConsola;
    private Productor[] listaProductoresControles;
    private Productor[] listaProductoresPaquetes;
    // Ensambladores
    private Ensamblador[] listaEnsambladores;
    // Almacenes
    private Almacen almacenConsolas;
    private Almacen almacenControles;
    private Almacen almacenPaquetes;
    private int[] almacenEnsamblados = new int[1];
    // Gerente y Cronometrador
    private Gerente gerente;
    private Cronometro cronometrador;
    // Variables de configuracion
    private int duracionDia;
    private int[] diasDespacho = new int[1];
    private int productoresConsolas;
    private int productoresControles;
    private int productoresPaquetes;
    private int maxProductoresConsolas;
    private int maxProductoresControles;
    private int maxProductoresPaquetes;
    private int ensambladores;
    private int maxEnsambladores;
    private int tamanoAlmacenConsolas;
    private int tamanoAlmacenControles;
    private int tamanoAlmacenPaquetes;
    // Condiciones
    private final boolean tipoConsola = false;
    private final boolean tipoControl = true;
    private final boolean tipoPaquete = true;
    
    // Inicializa la aplicación. se crean las listas de productos, los almacenes gerente, cronometrador.
    public void init() {
        leerConfig();
        listaProductoresConsola = new Productor[maxProductoresConsolas];
        listaProductoresControles = new Productor[maxProductoresControles];
        listaProductoresPaquetes = new Productor[maxProductoresPaquetes];
        listaEnsambladores = new Ensamblador[maxEnsambladores];
        almacenConsolas = new Almacen(tamanoAlmacenConsolas);
        almacenControles = new Almacen(tamanoAlmacenControles);
        almacenPaquetes = new Almacen(tamanoAlmacenPaquetes);
        gerente = new Gerente(mutexCronometro, diasDespacho, almacenEnsamblados, duracionDia);
        cronometrador = new Cronometro(mutexCronometro, diasDespacho, duracionDia);
        gerente.start();
        cronometrador.start();
        for (int i = 0; i < maxProductoresConsolas; i++) {
            listaProductoresConsola[i] = new Productor(tipoConsola, almacenarConsolas, mutexConsolas, retirarConsolas, almacenConsolas, duracionDia);
        }
        for (int i = 0; i < maxProductoresControles; i++) {
            listaProductoresControles[i] = new Productor(tipoControl, almacenarControles, mutexControles, retirarControles, almacenControles, duracionDia);
        }
        for (int i = 0; i < maxProductoresPaquetes; i++) {
            listaProductoresPaquetes[i] = new Productor(tipoPaquete, almacenarPaquetes, mutexPaquetes, retirarPaquetes, almacenPaquetes, duracionDia);
        }
        for (int i = 0; i < maxEnsambladores; i++) {
            listaEnsambladores[i] = new Ensamblador(almacenarControles,
                    almacenarConsolas,
                    almacenarPaquetes,
                    retirarConsolas,
                    retirarControles,
                    retirarPaquetes,
                    mutexControles,
                    mutexConsolas,
                    mutexPaquetes,
                    almacenConsolas,
                    almacenControles,
                    almacenPaquetes,
                    duracionDia,
                    almacenEnsamblados);
        }
        for (int i = 0; i < productoresConsolas; i++) {
            listaProductoresConsola[i].start();
            almacenarConsolas.release(1);
        }
        for (int i = 0; i < productoresControles; i++) {
            listaProductoresControles[i].start();
            almacenarControles.release(1);
        }
        for (int i = 0; i < productoresPaquetes; i++) {
            listaProductoresPaquetes[i].start();
            almacenarPaquetes.release(1);
        }
        for (int i = 0; i < ensambladores; i++) {
            listaEnsambladores[i].start();
        }
    }
    
    // En LeerConfig se lee el archivo de texto para determinar tamaño de almacenes, maximo de productores y duracción del dia. En caso de que se
    // presente algun inconveniente con el archivo el programa procedera a tomar los valores predeterminados
    public void leerConfig() {
        try (FileReader fr = new FileReader("Configuracion.txt")) {
            Scanner scan = new Scanner(fr);
            scan.nextLine();
            duracionDia = Integer.parseInt(scan.nextLine());
            scan.nextLine();
            diasDespacho[0] = Integer.parseInt(scan.nextLine());
            scan.nextLine();
            tamanoAlmacenConsolas = Integer.parseInt(scan.nextLine());
            scan.nextLine();
            tamanoAlmacenControles = Integer.parseInt(scan.nextLine());
            scan.nextLine();
            tamanoAlmacenPaquetes = Integer.parseInt(scan.nextLine());
            scan.nextLine();
            productoresConsolas = Integer.parseInt(scan.nextLine());
            scan.nextLine();
            maxProductoresConsolas = Integer.parseInt(scan.nextLine());
            scan.nextLine();
            productoresControles = Integer.parseInt(scan.nextLine());
            scan.nextLine();
            maxProductoresControles = Integer.parseInt(scan.nextLine());
            scan.nextLine();
            productoresPaquetes = Integer.parseInt(scan.nextLine());
            scan.nextLine();
            maxProductoresPaquetes = Integer.parseInt(scan.nextLine());
            scan.nextLine();
            ensambladores = Integer.parseInt(scan.nextLine());
            scan.nextLine();
            maxEnsambladores = Integer.parseInt(scan.nextLine());
        } catch (IOException | NumberFormatException ex) {
            System.out.println("Error de lectura, asignados valores por default");
            tamanoAlmacenConsolas = 20;
            tamanoAlmacenControles = 30;
            tamanoAlmacenPaquetes = 40;
            maxProductoresConsolas = 10;
            maxProductoresControles = 5;
            maxProductoresPaquetes = 3;
            maxEnsambladores = 4;
            productoresConsolas = 2;
            productoresControles = 3;
            productoresPaquetes = 1;
            ensambladores = 1;
            duracionDia = 24;
            diasDespacho[0] = 30;
        }
        validateInput();
    }
    
    // validateInput verifica que el usuario no introduzca números negativos, en caso de presentarse algun inconveniente de este tipo el programa
    // procede a asignar valores por default al archivo de texto.
    public void validateInput() {
        if (tamanoAlmacenConsolas <= 0
                || tamanoAlmacenControles <= 0
                || tamanoAlmacenPaquetes <= 0
                || maxProductoresConsolas <= 0
                || maxProductoresControles <= 0
                || maxProductoresPaquetes <= 0
                || maxEnsambladores <= 0
                || productoresConsolas <= 0
                || productoresControles <= 0
                || productoresPaquetes <= 0
                || ensambladores <= 0
                || duracionDia <= 0
                || diasDespacho[0] <= 0) {
            System.out.println("Solo se puede ingresar numeros positivos!");
            System.out.println("Error de lectura, asignados valores por default");
            tamanoAlmacenConsolas = 20;
            tamanoAlmacenControles = 30;
            tamanoAlmacenPaquetes = 40;
            maxProductoresConsolas = 10;
            maxProductoresControles = 5;
            maxProductoresPaquetes = 3;
            maxEnsambladores = 4;
            productoresConsolas = 2;
            productoresControles = 3;
            productoresPaquetes = 1;
            ensambladores = 1;
            duracionDia = 24;
            diasDespacho[0] = 30;
        }
    }
    
    // agregarProductorConsolas verifica si hay menos productores de consolas que el número guardado en el contador, de ser asi, se procede a crear un productor,
    // se guarda en la lista de productores de consolas y se inicializa con .start();
    public void agregarProductorConsolas() {
        if (productoresConsolas < maxProductoresConsolas) {
            listaProductoresConsola[productoresConsolas] = new Productor(tipoConsola, almacenarConsolas, mutexConsolas, retirarConsolas, almacenConsolas, duracionDia);
            listaProductoresConsola[productoresConsolas].start();
            productoresConsolas++;
        }
    }

    // agregarProductorControles verifica si hay menos productores de controles que el número guardado en el contador, de ser asi, se procede a crear un productor,
    // se guarda en la lista de productores de controles y se inicializa con .start();
    public void agregarProductorControles() {
        if(productoresControles < maxProductoresControles){
            listaProductoresControles[productoresControles] = new Productor(tipoControl, almacenarControles, mutexControles, retirarControles, almacenControles, duracionDia);
            listaProductoresControles[productoresControles].start();
            productoresControles++;
        }
    }

    
    // agregarProductorPauetes verifica si hay menos productores de paquetes que el número guardado en el contador, de ser asi, se procede a crear un productor,
    // se guarda en la lista de productores de paquetes y se inicializa con .start();
    public void agregarProductorPaquetes() {
        if(productoresPaquetes < maxProductoresPaquetes){
            listaProductoresPaquetes[productoresPaquetes] = new Productor(tipoPaquete, almacenarPaquetes, mutexPaquetes, retirarPaquetes, almacenPaquetes, duracionDia);
            listaProductoresPaquetes[productoresPaquetes].start();
            productoresPaquetes++;
        }
    }
    
    // agregarEnsamblador verifica si hay menos productores de controles que el número guardado en el contador, de ser asi, se procede a crear un ensamblador,
    // se guarda en la lista de productores de ensambladores y se inicializa con .start();    
    public void agregarEnsamblador(){
        if(ensambladores< maxEnsambladores){
            listaEnsambladores[ensambladores] = new Ensamblador(almacenarControles,
                    almacenarConsolas,
                    almacenarPaquetes,
                    retirarConsolas,
                    retirarControles,
                    retirarPaquetes,
                    mutexControles,
                    mutexConsolas,
                    mutexPaquetes,
                    almacenConsolas,
                    almacenControles,
                    almacenPaquetes,
                    duracionDia,
                    almacenEnsamblados);
            listaEnsambladores[ensambladores].start();
            ensambladores++;
        }
    }
    
    // Despedir al ensamblador
    public void despedirEnsamblador(){
      
    }
    
    //Despedir al productor de consolas
    public void despedirProductorConsolas() {

    }
    
    //Despedir al productor de controles
    public void despedirProductorControles() {

    }
    
    //Despedir al productor de paquetes
    public void despedirProductorPaquetes() {

    }

    //Getters  y Setters ----------------------------------
    public void setMutexConsolas(Semaphore mutexConsolas) {
        this.mutexConsolas = mutexConsolas;
    }

    public void setMutexControles(Semaphore mutexControles) {
        this.mutexControles = mutexControles;
    }

    public void setMutexPaquetes(Semaphore mutexPaquetes) {
        this.mutexPaquetes = mutexPaquetes;
    }

    public void setAlmacenarConsolas(Semaphore almacenarConsolas) {
        this.almacenarConsolas = almacenarConsolas;
    }

    public void setAlmacenarControles(Semaphore almacenarControles) {
        this.almacenarControles = almacenarControles;
    }

    public void setAlmacenarPaquetes(Semaphore almacenarPaquetes) {
        this.almacenarPaquetes = almacenarPaquetes;
    }

    public void setRetirarConsolas(Semaphore retirarConsolas) {
        this.retirarConsolas = retirarConsolas;
    }

    public void setRetirarControles(Semaphore retirarControles) {
        this.retirarControles = retirarControles;
    }

    public void setRetirarPaquetes(Semaphore retirarPaquetes) {
        this.retirarPaquetes = retirarPaquetes;
    }

    public void setListaProductoresConsola(Productor[] listaProductoresConsola) {
        this.listaProductoresConsola = listaProductoresConsola;
    }

    public void setListaProductoresControles(Productor[] listaProductoresControles) {
        this.listaProductoresControles = listaProductoresControles;
    }

    public void setListaProductoresPaquetes(Productor[] listaProductoresPaquetes) {
        this.listaProductoresPaquetes = listaProductoresPaquetes;
    }

    public void setListaEnsambladores(Ensamblador[] listaEnsambladores) {
        this.listaEnsambladores = listaEnsambladores;
    }

    public void setAlmacenConsolas(Almacen almacenConsolas) {
        this.almacenConsolas = almacenConsolas;
    }

    public void setAlmacenControles(Almacen almacenControles) {
        this.almacenControles = almacenControles;
    }

    public void setAlmacenPaquetes(Almacen almacenPaquetes) {
        this.almacenPaquetes = almacenPaquetes;
    }

    public void setAlmacenEnsamblados(int[] almacenEnsamblados) {
        this.almacenEnsamblados = almacenEnsamblados;
    }

    public void setDuracionDia(int duracionDia) {
        this.duracionDia = duracionDia;
    }

    public void setDiasDespacho(int diasDespacho) {
        this.diasDespacho[0] = diasDespacho;
    }

    public void setProductoresConsolas(int productoresConsolas) {
        this.productoresConsolas = productoresConsolas;
    }

    public void setProductoresControles(int productoresControles) {
        this.productoresControles = productoresControles;
    }

    public void setProductoresPaquetes(int productoresPaquetes) {
        this.productoresPaquetes = productoresPaquetes;
    }

    public void setMaxProductoresConsolas(int maxProductoresConsolas) {
        this.maxProductoresConsolas = maxProductoresConsolas;
    }

    public void setMaxProductoresControles(int maxProductoresControles) {
        this.maxProductoresControles = maxProductoresControles;
    }

    public void setMaxProductoresPaquetes(int maxProductoresPaquetes) {
        this.maxProductoresPaquetes = maxProductoresPaquetes;
    }

    public void setEnsambladores(int ensambladores) {
        this.ensambladores = ensambladores;
    }

    public void setMaxEnsambladores(int maxEnsambladores) {
        this.maxEnsambladores = maxEnsambladores;
    }

    public void setTamanoAlmacenConsolas(int tamanoAlmacenConsolas) {
        this.tamanoAlmacenConsolas = tamanoAlmacenConsolas;
    }

    public void setTamanoAlmacenControles(int tamanoAlmacenControles) {
        this.tamanoAlmacenControles = tamanoAlmacenControles;
    }

    public void setTamanoAlmacenPaquetes(int tamanoAlmacenPaquetes) {
        this.tamanoAlmacenPaquetes = tamanoAlmacenPaquetes;
    }

    public Semaphore getMutexConsolas() {
        return mutexConsolas;
    }

    public Semaphore getMutexControles() {
        return mutexControles;
    }

    public Semaphore getMutexPaquetes() {
        return mutexPaquetes;
    }

    public Semaphore getAlmacenarConsolas() {
        return almacenarConsolas;
    }

    public Semaphore getAlmacenarControles() {
        return almacenarControles;
    }

    public Semaphore getAlmacenarPaquetes() {
        return almacenarPaquetes;
    }

    public Semaphore getRetirarConsolas() {
        return retirarConsolas;
    }

    public Semaphore getRetirarControles() {
        return retirarControles;
    }

    public Semaphore getRetirarPaquetes() {
        return retirarPaquetes;
    }

    public Productor[] getListaProductoresConsola() {
        return listaProductoresConsola;
    }

    public Productor[] getListaProductoresControles() {
        return listaProductoresControles;
    }

    public Productor[] getListaProductoresPaquetes() {
        return listaProductoresPaquetes;
    }

    public Ensamblador[] getListaEnsambladores() {
        return listaEnsambladores;
    }

    public Almacen getAlmacenConsolas() {
        return almacenConsolas;
    }

    public Almacen getAlmacenControles() {
        return almacenControles;
    }

    public Almacen getAlmacenPaquetes() {
        return almacenPaquetes;
    }

    public int[] getAlmacenEnsamblados() {
        return almacenEnsamblados;
    }

    public int getDuracionDia() {
        return duracionDia;
    }

    public int getDiasDespacho() {
        return diasDespacho[0];
    }

    public int getProductoresConsolas() {
        return productoresConsolas;
    }

    public int getProductoresControles() {
        return productoresControles;
    }

    public int getProductoresPaquetes() {
        return productoresPaquetes;
    }

    public int getMaxProductoresConsolas() {
        return maxProductoresConsolas;
    }

    public int getMaxProductoresControles() {
        return maxProductoresControles;
    }

    public int getMaxProductoresPaquetes() {
        return maxProductoresPaquetes;
    }

    public int getEnsambladores() {
        return ensambladores;
    }

    public int getMaxEnsambladores() {
        return maxEnsambladores;
    }

    public int getTamanoAlmacenConsolas() {
        return tamanoAlmacenConsolas;
    }

    public int getTamanoAlmacenControles() {
        return tamanoAlmacenControles;
    }

    public int getTamanoAlmacenPaquetes() {
        return tamanoAlmacenPaquetes;
    }

    public boolean isTipoConsola() {
        return tipoConsola;
    }

    public boolean isTipoControl() {
        return tipoControl;
    }

    public boolean isTipoPaquete() {
        return tipoPaquete;
    }

    public Gerente getGerente() {
        return gerente;
    }

    public Cronometro getCronometrador() {
        return cronometrador;
    }

}
