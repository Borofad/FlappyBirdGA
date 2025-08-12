package com.neuralNetwork;

public class NetworkTools {
    public static double[][][] xavierInitialization(int[] NETWORK_LAYER_SIZES){
        int NETWORK_SIZE = NETWORK_LAYER_SIZES.length;
        double[][][] weights = new double[NETWORK_SIZE][][];
        for(int i = 1; i < NETWORK_SIZE; i++){
            double a = Math.sqrt(6.0 / (NETWORK_LAYER_SIZES[i - 1] + NETWORK_LAYER_SIZES[i]));
            weights[i] = createRandom2DArray(   NETWORK_LAYER_SIZES[i - 1],
                    NETWORK_LAYER_SIZES[i],
                    -4 * a,
                    4 * a);
        }

        return weights;
    }

    public static double[][] createRandom2DArray(int rows, int cols, double lowerBound, double upperBound){
        double[][] res = new double[rows][];
        for(int i = 0; i < rows; i++){
            res[i] = createRandomArray(cols, lowerBound, upperBound);
        }

        return res;
    }

    public static double[] createRandomArray(int length, double lowerBound, double upperBound){
        double[] res = new double[length];
        for(int i = 0; i < length; i++){
            res[i] = Math.random() * (upperBound - lowerBound) + lowerBound;
        }

        return res;
    }

    public static double[] transform2D(double[][] arr){
        int rows = arr.length;
        int cols = arr[0].length;
        double[] res = new double[rows * cols];
        for(int row = 0; row < rows; row++){
            for(int col = 0; col < cols; col++){
                res[row * cols + col] = arr[row][col];
            }
        }

        return res;
    }
}