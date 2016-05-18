/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espia;

import ec.EvolutionState;
import ec.Species;
import ec.vector.IntegerVectorIndividual;
import ec.vector.IntegerVectorSpecies;
import ec.vector.VectorIndividual;
import ec.vector.VectorSpecies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author ggutierrez
 */
public class IntegerVectorIndividualEspia extends IntegerVectorIndividual{

    
    
   @Override
    public void reset(EvolutionState state, int thread) {
	   	
	   	ArrayList<Integer> numeros= new ArrayList<Integer>();
	    int res;
	    // agrego a lista los numeros que contendra el genome - siempre empieza en 0
	    // numeros = 1..genome.length-1
        for(int i=1;i < genome.length;i++){
                numeros.add(i);
        }
        
        genome[0]=0;
        // crea genoma de forma que no haya repetidos
        int k=numeros.size();
        int n=k;
        for(int i=1;i<=k;i++){
            res = state.random[thread].nextInt(n);            
            genome[i]=(Integer)numeros.get(res);
            numeros.remove(res);
            n--;
        }
        
    }

    @Override
    public void defaultMutate(EvolutionState state, int thread) {
        
        // Aplico EM - Exchange Mutation
        IntegerVectorSpecies s = (IntegerVectorSpecies) species;

        int aux, pos;
       
        for(int x = 1; x < genome.length; x++){
        	//sorteo una posicion con la que mutar
            if (state.random[thread].nextBoolean(s.mutationProbability(x)))
                { 	
            			pos = randomValueFromClosedInterval(1, genome.length-1, state.random[thread]);
            			aux = genome[x]; 
            			genome[x] = genome[pos];
            			genome[pos]= aux ;
                }
        }
    
    }

    @Override
    public void defaultCrossover(EvolutionState state, int thread, VectorIndividual ind) {

    	// Aplico cruzamiento Partially Mapped Crossover (PMX)
        IntegerVectorSpecies s = (IntegerVectorSpecies) species;
        IntegerVectorIndividual ind2=(IntegerVectorIndividual) ind;
        int tmp;
        int point;
        
        
        int len = Math.min(genome.length, ind2.genome.length);
        if (len != genome.length || len != ind2.genome.length)
            state.output.warnOnce("Genome lengths are not the same.  Vector crossover will only be done in overlapping region.");
        
        //Elijo puntos de corte
        point = state.random[thread].nextInt((len / s.chunksize));
        while (point==0){
            point = state.random[thread].nextInt((len / s.chunksize));
        }
        
        int point0 = state.random[thread].nextInt((len / s.chunksize));
        while ((point0==point) || (point0==0)){
            point0 = state.random[thread].nextInt((len / s.chunksize));
        }
        if (point0 > point) { int p = point0; point0 = point; point = p; }

        int inicio=point0*s.chunksize;
        int fin=point*s.chunksize;
        int cantMapeo=fin-inicio;
        
        //creo hashmap para guardar mapeos del largo de la seccion de corte
        HashMap<Integer,Integer> mapeoGen= new HashMap<Integer,Integer>(cantMapeo);
        HashMap<Integer,Integer> mapeoInd=new HashMap<Integer,Integer>(cantMapeo);
        
        //hago el crossover para la seccion y guardo los mapeos para aplicar despues
        for(int x=point0*s.chunksize;x<point*s.chunksize;x++){
            mapeoGen.put(ind2.genome[x],genome[x]);
            mapeoInd.put(genome[x], ind2.genome[x]);
            
            tmp = ind2.genome[x];
            ind2.genome[x] = genome[x];
            genome[x] = tmp;
        }
        
        int cantGen=genome.length;
        int i=1;
        //aplico mapeo a this desde 1 a inicio seccion
        while (i<inicio) {
            while (mapeoGen.containsKey(genome[i])){
                genome[i]=(int) mapeoGen.get(genome[i]);
            }
            i++;
        }

        //aplico mapeo a this desde fin seccion al fin del genoma
        int contador=fin;
        while (contador<cantGen) {
            while (mapeoGen.containsKey(genome[contador])){
                genome[contador]=(int) mapeoGen.get(genome[contador]);
            }
            contador++;
        }
        //aplico mapeo a ind desde 1 a inicio seccion
        i=1;
        while (i<inicio) {
            while (mapeoInd.containsKey(ind2.genome[i])){
                ind2.genome[i]=(int) mapeoInd.get(ind2.genome[i]);
            }
            i++;
        }
        //aplico mapeo a ind desde fin seccion al fin del genoma
        contador=fin;
        cantGen=ind2.genome.length;
        while (contador<cantGen) {
            while (mapeoInd.containsKey(ind2.genome[contador])){
                ind2.genome[contador]=(int) mapeoInd.get(ind2.genome[contador]);
            }
            contador++;
        }
            
    }
    
}
