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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author ggutierrez
 */
public class IntegerVectorIndividualEspia extends IntegerVectorIndividual{

    ArrayList<Integer> numeros= new ArrayList<Integer>();
    int res;
    Random rnd=new Random();
    //int[] genome1=new int[5];
    
   @Override
    public void reset(EvolutionState state, int thread) {
        //super.reset(state, thread); //To change body of generated methods, choose Tools | Templates.
        
        for(int i=0;i<(genome.length-1);i++){
                numeros.add(i, i+1);
        }
        
        //int[] genome1=new int[genome.length];
        genome[0]=0;
        int k=numeros.size();
        int n=k;
        for(int i=1;i<=k;i++){
            res=rnd.nextInt(n);            
            genome[i]=(Integer)numeros.get(res);
            numeros.remove(res);
            n--;
        }
        //genome=genome1;
    
        //System.out.println("PROBANDO-"+genome[0]+"-"+genome[1]+"-"+genome[2]+"-"+genome[3]+"-"+genome[4]);
        
    }

    @Override
    public void defaultMutate(EvolutionState state, int thread) {
        //super.defaultMutate(state, thread); //To change body of generated methods, choose Tools | Templates.
        
        // Aplico EM - Exchange Mutation
        int n=(genome.length);
        IntegerVectorSpecies s = (IntegerVectorSpecies) species;

        int pos1=rnd.nextInt(n);
        while (pos1==0){
            pos1=rnd.nextInt(n);
        }
        int pos2=rnd.nextInt(n);
        while ((pos2==pos1) || (pos2==0)){
            pos2=rnd.nextInt(n);
        }
        int aux;
        for(int x = 1; x < genome.length; x++){
            if (state.random[thread].nextBoolean(s.mutationProbability(x)))
                {
                    aux=genome[pos2];
                    genome[pos2]=genome[pos1];
                    genome[pos1]=aux;
                }
        }
        //System.out.println("*************GENOME MUTATION-"+genome[0]+"-"+genome[1]+"-"+genome[2]+"-"+genome[3]+"-"+genome[4]);
    
    }

    @Override
    public void defaultCrossover(EvolutionState state, int thread, VectorIndividual ind) {
        //super.defaultCrossover(state, thread, ind); //To change body of generated methods, choose Tools | Templates.
        
        // Aplico cruzamiento Partially Mapped Crossover (PMX)
        IntegerVectorSpecies s = (IntegerVectorSpecies) species;
        IntegerVectorIndividual ind2=(IntegerVectorIndividual) ind;
        int tmp;
        int point;
        
        
        int len = Math.min(genome.length, ind2.genome.length);
        if (len != genome.length || len != ind2.genome.length)
            state.output.warnOnce("Genome lengths are not the same.  Vector crossover will only be done in overlapping region.");
        
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
        HashMap mapeoGen=new HashMap(cantMapeo);
        HashMap mapeoInd=new HashMap(cantMapeo);

	//        System.out.println("*************IND INICIAL-"+inicio+"-"+fin+"-pIni-pFin"+ind2.genome[0]+"-"+ind2.genome[1]+"-"+ind2.genome[2]+"-"+ind2.genome[3]+"-"+ind2.genome[4]);
	//
	//        System.out.println("*************GENOME INICIAL-"+genome[0]+"-"+genome[1]+"-"+genome[2]+"-"+genome[3]+"-"+genome[4]);
        //System.out.println(this.genotypeToStringForHumans());
//        System.out.println(this.genotypeToStringForHumans());
        
        for(int x=point0*s.chunksize;x<point*s.chunksize;x++)
            {
            mapeoGen.put(ind2.genome[x],genome[x]);
            mapeoInd.put(genome[x], ind2.genome[x]);
            
            tmp = ind2.genome[x];
            ind2.genome[x] = genome[x];
            genome[x] = tmp;
            }
        int cantGen=genome.length;
        int i=1;
        while (i<inicio) {
            while (mapeoGen.containsKey(genome[i])){
                genome[i]=(int) mapeoGen.get(genome[i]);
            }
            i++;
        }
        int contador=fin;
        while (contador<cantGen) {
            while (mapeoGen.containsKey(genome[contador])){
                genome[contador]=(int) mapeoGen.get(genome[contador]);
            }
            contador++;
        }

        i=1;
        while (i<inicio) {
            while (mapeoInd.containsKey(ind2.genome[i])){
                ind2.genome[i]=(int) mapeoInd.get(ind2.genome[i]);
            }
            i++;
        }
        contador=fin;
        cantGen=ind2.genome.length;
        while (contador<cantGen) {
            while (mapeoInd.containsKey(ind2.genome[contador])){
                ind2.genome[contador]=(int) mapeoInd.get(ind2.genome[contador]);
            }
            contador++;
        }
            
//            System.out.println("*************IND-FINAL"+ind2.genome[0]+"-"+ind2.genome[1]+"-"+ind2.genome[2]+"-"+ind2.genome[3]+"-"+ind2.genome[4]);
//            
//            System.out.println("*************GENOME-FINAL"+genome[0]+"-"+genome[1]+"-"+genome[2]+"-"+genome[3]+"-"+genome[4]);
       // System.out.println(ind2.genotypeToStringForHumans());
//        System.out.println(ind2.genotypeToStringForHumans());
    }
    


    
    
}
