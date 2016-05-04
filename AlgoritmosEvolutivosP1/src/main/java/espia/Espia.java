/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espia;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.simple.SimpleStatistics;
import ec.util.Parameter;
import ec.vector.IntegerVectorIndividual;
import java.io.File;

import java.lang.System;
import java.util.Scanner;

/**
 *
 * @author ggutierrez
 */
public class Espia extends Problem implements SimpleProblemForm{
    
    public static final String P_IN = "in";
    public static final String P_CANT_DESTINOS = "cantDestinos";
    public static final String P_INICIO_MISION = "inicioMision";
    
    public static final String P_DIA_INICIO_MEDIA_TEMPORADA = "diaInicioMediaTemporada";
    public static final String P_AUMENTO_MEDIA_TEMPORADA = "aumentoMediaTemporada";
    public static final String P_DIA_INICIO_ALTA_TEMPORADA = "diaInicioAltaTemporada";
    public static final String P_AUMENTO_ALTA_TEMPORADA = "aumentoAltaTemporada";
    public static final String P_DIAS_ESTADIA = "diasEstadia";
    
    private int [][] mCostos; 
    private File in;
    
    private int cantDestinos;
    private int diaInicioMediaTemporada;
    private double aumentoMediaTemporada;
    private int diaInicioAltaTemporada;
    private double aumentoAltaTemporada;
    private int diasEstadia;

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base); //To change body of generated methods, choose Tools | Templates.
        
        //cargo parametros del problema
        this.diaInicioMediaTemporada = state.parameters.getInt(base.push(P_DIA_INICIO_MEDIA_TEMPORADA),null);
        this.aumentoMediaTemporada = 1 +  state.parameters.getDouble(base.push(P_AUMENTO_MEDIA_TEMPORADA),null)/100;
        this.diaInicioAltaTemporada = state.parameters.getInt(base.push(P_DIA_INICIO_ALTA_TEMPORADA),null);
        this.aumentoAltaTemporada = 1 + state.parameters.getDouble(base.push(P_AUMENTO_ALTA_TEMPORADA),null)/100;
        this.diasEstadia = state.parameters.getInt(base.push(P_DIAS_ESTADIA),null);
        
        //Cargo cantidad de destinos
        this.cantDestinos = state.parameters.getInt(base.push(P_CANT_DESTINOS),null);
        
        //Cargo matriz de costos desde archivo
        this.mCostos = new int[cantDestinos][cantDestinos];
        this.in = state.parameters.getFile(base.push(P_IN), null);
        
        Scanner s = null;

        try {
                // Leemos el contenido del fichero
                System.out.println("... Leemos el contenido del fichero ...");
                s = new Scanner(in);
                // Leemos linea a linea el fichero
                int fila=0;
                while (s.hasNextLine()) {
                        String linea = s.nextLine(); 	// Guardamos la linea en un String
                        String [] arrayStr=linea.split(" ");
                        for (int columna=0;columna<arrayStr.length;columna++){
                        	int costo = Integer.parseInt(arrayStr[columna]); 
                            this.mCostos[fila][columna]= costo == -1 ? Integer.MAX_VALUE : costo;
                        }
                        fila++;
                        System.out.println(linea);      // Imprimimos la linea
                }

        } catch (Exception ex) {
                System.out.println("Mensaje: " + ex.getMessage());
        } finally {
                // Cerramos el fichero tanto si la lectura ha sido correcta o no
                try {
                        if (s != null)
                                s.close();
                } catch (Exception ex2) {
                        System.out.println("Mensaje 2: " + ex2.getMessage());
                }
        }
    }
    
    

    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int thread) {
            
    	if (ind.evaluated) return;

    	IntegerVectorIndividual ind2 = (IntegerVectorIndividual)ind;

    	double valor = 0; //Calcular el fitness del individuo
    	for (int i=1; i < ind2.genome.length; i++){
    			valor+=((mCostos[ind2.genome[i-1]][ind2.genome[i]]) * aumentoPorTemporada(i));
    	}
    	
    	((SimpleFitness) ind2.fitness).setFitness(state, valor*(-1), valor==0);

    	ind2.evaluated=true;
    
    }

	public double aumentoPorTemporada(int j){
    	int  diaMision = j * this.diasEstadia;
    	
        if (diaMision >= this.diaInicioAltaTemporada)
            return aumentoAltaTemporada;
        else if (diaMision >= this.diaInicioMediaTemporada)
            return aumentoMediaTemporada;
        else
            return 1;
    }
    
    @Override
    public void describe(EvolutionState state, Individual ind,
    		int subpopulation, int threadnum, int log) {
    	// TODO Auto-generated method stub
    	state.output.println( ind.genotypeToStringForHumans(), log );
    }
}