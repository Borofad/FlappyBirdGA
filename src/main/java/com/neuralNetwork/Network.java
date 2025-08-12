package com.neuralNetwork;

import java.util.Arrays;

public class Network {
    private final int[] NETWORK_LAYER_SIZES;
    private final int NETWORK_SIZE;
    private final int INPUT_SIZE;
    private double[][][] weights;
    private double[][] biases;
    private double[][] outputs;
    public Network(int... NETWORK_LAYER_SIZES) {
        this.NETWORK_LAYER_SIZES = NETWORK_LAYER_SIZES;
        this.NETWORK_SIZE = NETWORK_LAYER_SIZES.length;
        this.INPUT_SIZE = NETWORK_LAYER_SIZES[0];

        this.weights = NetworkTools.xavierInitialization(NETWORK_LAYER_SIZES);

        this.biases = new double[NETWORK_SIZE][];
        this.outputs = new double[NETWORK_SIZE][];
        for(int i = 0; i < NETWORK_SIZE; i++){
            outputs[i] = new double[NETWORK_LAYER_SIZES[i]];

            if(i > 0){
                biases[i] = new double[NETWORK_LAYER_SIZES[i]];
            }
        }
    }

    public double[] calculate(double[] input) throws NetworkException {
        if(input.length != INPUT_SIZE) throw new NetworkException("Expected input size " + INPUT_SIZE + ", but found " + input.length);

        outputs[0] = Arrays.copyOf(input, INPUT_SIZE);
        for(int layer = 1; layer < NETWORK_SIZE; layer++){
            int neurons = NETWORK_LAYER_SIZES[layer];

            for(int neuron = 0; neuron < neurons; neuron++){
                int prevLayerNeurons = NETWORK_LAYER_SIZES[layer - 1];

                double sum = biases[layer][neuron];
                for(int prevNeuron = 0; prevNeuron < prevLayerNeurons; prevNeuron++){
                    sum += outputs[layer - 1][prevNeuron] * weights[layer][prevNeuron][neuron];
                }
                outputs[layer][neuron] = sigmoid(sum);
            }
        }

        return outputs[NETWORK_SIZE - 1];
    }

    private double sigmoid(double sum) {
        return 1d / (1 + Math.exp(-sum));
    }

    public int[] getNETWORK_LAYER_SIZES() {
        return NETWORK_LAYER_SIZES;
    }

    public double[][][] getWeights() {
        return weights;
    }

    public double[][] getBiases() {
        return biases;
    }
}