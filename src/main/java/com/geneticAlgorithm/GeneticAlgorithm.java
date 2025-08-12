package com.geneticAlgorithm;

import java.util.*;

public class GeneticAlgorithm {
    private static final int SURVIVORS = 20;
    private static final int TOURNAMENT_SIZE = 3;
    private static double mutationRate = 0.05;
    private static double mutationStrength = 0.2;
    public static <T extends GeneticClient> void evolve(List<T> clients){
        Queue<T> pq = new PriorityQueue<>(Comparator.comparingInt(GeneticClient::getScore));
        for(T client : clients){
            pq.offer(client);

            if(pq.size() > SURVIVORS) pq.poll();
        }

        List<T> survivors = new ArrayList<>(pq);
        clients.removeAll(survivors);

        crossover(clients, survivors);
        mutate(clients);

        clients.addAll(survivors);
    }

    private static <T extends GeneticClient> void mutate(List<T> clients) {
        Random random = new Random();
        for(T client : clients){
            int[] NETWORK_LAYER_SIZES = clients.get(0).getNetwork().getNETWORK_LAYER_SIZES();
            for(int layer = 1; layer < NETWORK_LAYER_SIZES.length; layer++){
                for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++){
                    client.getNetwork().getBiases()[layer][neuron] += Math.random() <= mutationRate ? random.nextGaussian() * mutationStrength : 0;

                    for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevNeuron++){
                        client.getNetwork().getWeights()[layer][prevNeuron][neuron] += Math.random() <= mutationRate ? random.nextGaussian() * mutationStrength : 0;
                    }
                }
            }
        }
    }

    public static <T extends GeneticClient> void crossover(List<T> newborns, List<T> survivors){
        for(T newborn : newborns){
            T parent1 = pickParent(survivors);
            T parent2 = pickParent(survivors);

            double alpha = Math.random();

            int[] NETWORK_LAYER_SIZES = survivors.get(0).getNetwork().getNETWORK_LAYER_SIZES();
            for(int layer = 1; layer < NETWORK_LAYER_SIZES.length; layer++){
                for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++){
                    newborn.getNetwork().getBiases()[layer][neuron] = alpha * parent1.getNetwork().getBiases()[layer][neuron] + (1 - alpha) * parent2.getNetwork().getBiases()[layer][neuron];

                    for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevNeuron++){
                        newborn.getNetwork().getWeights()[layer][prevNeuron][neuron] = alpha * parent1.getNetwork().getWeights()[layer][prevNeuron][neuron] + (1 - alpha) * parent2.getNetwork().getWeights()[layer][prevNeuron][neuron];
                    }
                }
            }
        }
    }

    public static <T extends GeneticClient> T pickParent(List<T> parents){
        T parent = parents.get((int) (Math.random() * parents.size()));
        for(int i = 1; i < TOURNAMENT_SIZE; i++){
            int randomIdx = (int) (Math.random() * parents.size());

            if(parents.get(randomIdx).getScore() > parent.getScore()) parent = parents.get(randomIdx);
        }

        return parent;
    }

    public static double getMutationRate() {
        return mutationRate;
    }

    public static void setMutationRate(double mutationRate) {
        GeneticAlgorithm.mutationRate = mutationRate;
    }

    public static double getMutationStrength() {
        return mutationStrength;
    }

    public static void setMutationStrength(double mutationStrength) {
        GeneticAlgorithm.mutationStrength = mutationStrength;
    }
}
