import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dawn
 */
class Network {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    int layers;
    int[] sizes;
    double[][] biases;
    double[][][] weights;

    Network(int[] sizes) {
        layers = sizes.length;
        this.sizes = sizes;
        biases = new double[layers - 1][];
        for (int i = 0; i < layers - 1; i++) {
            biases[i] = new double[sizes[i + 1]];
            for (int j = 0; j < biases[i].length; j++) {
                biases[i][j] = RANDOM.nextGaussian();
            }
        }
        weights = new double[layers - 1][][];
        for (int i = 0; i < layers - 1; i++) {
            weights[i] = new double[sizes[i + 1]][];
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = new double[sizes[i]];
                for (int k = 0; k < weights[i][j].length; k++) {
                    weights[i][j][k] = RANDOM.nextGaussian();
                }
            }
        }
    }

    double[] feedForward(double[] a) {
        double[] n;
        for (int i = 0; i < biases.length; i++) {
            n = new double[biases[i].length];
            for (int j = 0; j < biases[i].length; j++) {
                double b = biases[i][j];
                double[] w = weights[i][j];
                for (int k = 0; k < w.length; k++) {
                    b += w[k] * a[k];
                }
                n[j] = sigmoid(b);
            }
            a = n;
        }
        return a;
    }

    void sgd(List<NumberImage> trainingData, int epochs, int minBatchSize, double eta, List<NumberImage> testData) {
        if (null == trainingData) { return; }
        int testSize = null == testData ? 0 : testData.size();
        int n = trainingData.size();
        for (int e = 0; e < epochs; e++) {
            Collections.shuffle(trainingData);
            List<List<NumberImage>> miniBatches = Stream.iterate(0, i -> i + 1).limit(n/minBatchSize).map(i -> trainingData.subList(i * minBatchSize, (i + 1) * minBatchSize)).collect(Collectors.toList());
            miniBatches.forEach(mb -> updateMiniBatch(mb, eta));
            if (0 < testSize) {
                System.out.println(String.format("Epoch %s: %s / %s", e, evaluate(testData), testSize));
            } else {
                System.out.println(String.format("Epoch %s complete", e));
            }
        }
    }



    void updateMiniBatch(List<NumberImage> miniBatch, double eta) {
        double len = miniBatch.size();
        double[][] nablaB = zeroB();
        double[][][] nablaW = zeroW();

        miniBatch.forEach(m -> {
            WB r = backprop(m.data, m.numbers);
            for (int i = 0; i < nablaB.length; i++) {
                for (int j = 0; j < nablaB[i].length; j++) {
                    nablaB[i][j] += r.b[i][j];
                }
            }
            for (int i = 0; i < nablaW.length; i++) {
                for (int j = 0; j < nablaW[i].length; j++) {
                    for (int k = 0; k < nablaW[i][j].length; k++) {
                        nablaW[i][j][k] += r.w[i][j][k];
                    }
                }
            }
        });
        for (int i = 0; i < nablaB.length; i++) {
            for (int j = 0; j < nablaB[i].length; j++) {
                biases[i][j] = biases[i][j] - (eta/len) * nablaB[i][j];
            }
        }
        for (int i = 0; i < nablaW.length; i++) {
            for (int j = 0; j < nablaW[i].length; j++) {
                for (int k = 0; k < nablaW[i][j].length; k++) {
                    weights[i][j][k] = weights[i][j][k] - (eta/len) * nablaW[i][j][k];
                }
            }
        }
    }

    private double[][] zeroB() {
        double[][] nablaB = new double[layers - 1][];
        for (int i = 0; i < layers - 1; i++) {
            nablaB[i] = new double[sizes[i + 1]];
            for (int j = 0; j < nablaB[i].length; j++) {
                nablaB[i][j] = 0.0;
            }
        }
        return nablaB;
    }
    private double[][][] zeroW() {
        double[][][] nablaW = new double[layers - 1][][];
        for (int i = 0; i < layers - 1; i++) {
            nablaW[i] = new double[sizes[i + 1]][];
            for (int j = 0; j < nablaW[i].length; j++) {
                nablaW[i][j] = new double[sizes[i]];
                for (int k = 0; k < nablaW[i][j].length; k++) {
                    nablaW[i][j][k] = 0.0;
                }
            }
        }
        return nablaW;
    }

    private WB backprop(double[] x, double[] y) {
        double[][] nablaB = zeroB();
        double[][][] nablaW = zeroW();
        double[] activation = x;
        double[][] activations = new double[layers][];
        activations[0] = x;
        double[][] zs = new double[layers - 1][];
        for (int i = 0; i < biases.length; i++) {
            double[] a = new double[biases[i].length];
            double[] aa = new double[biases[i].length];
            for (int j = 0; j < biases[i].length; j++) {
                double b = biases[i][j];
                for (int k = 0; k < weights[i][j].length; k++) {
                    b += weights[i][j][k] * activation[k];
                }
                a[j] = b;
                aa[j] = sigmoid(b);
            }
            zs[i] = a;
            activation = aa;
            activations[i + 1] = activation;
        }

        double[] delta = dot(costDerivative(activations[activations.length - 1], y), sigmoidPrime(zs[zs.length - 1]));
        nablaB[nablaB.length - 1] = delta;
        nablaW[nablaW.length - 1] = dot2(delta, activations[activations.length - 2]);

        for (int i = activations.length - 3; 0 <= i; i--) {
            double[] z = zs[i];
            double[] sp = sigmoidPrime(z);
            delta = dot(dot3(weights[i + 1], delta), sp);
            nablaB[i] = delta;
            nablaW[i] = dot2(delta, activations[i]);
        }

        return new WB(nablaB, nablaW);
    }

    private static class WB {
        private double[][] b;
        private double[][][] w;

        public WB(double[][] b, double[][][] w) {
            this.b = b;
            this.w = w;
        }
    }

    long evaluate(List<NumberImage> testData) {
        return testData.stream().filter(d -> d.success(feedForward(d.data))).count();
    }

    double[] costDerivative(double[] outputActivations, double[] y) {
         double[] r = new double[outputActivations.length];
         for (int i = 0; i < outputActivations.length; i++) {
             r[i] = outputActivations[i] - y[i];
         }
         return r;
    }

    private static double sigmoid(double z) {
        return 1.0/(1.0+Math.exp(-z));
    }

    private static double sigmoidPrime(double z) {
        return sigmoid(z)*(1-sigmoid(z));
    }

    private static double[] sigmoidPrime(double[] z) {
        double[] zz = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            zz[i] = sigmoidPrime(z[i]);
        }
        return zz;
    }

    private static double[] dot(double[] x, double[] y) {
        double[] r = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            r[i] = x[i] * y[i];
        }
        return r;
    }

    private static double[][] dot2(double[] x, double[] y) {
        double[][] r = new double[x.length][y.length];
        for (int i = 0; i < x.length; i++) {
            r[i] = new double[y.length];
            for (int j = 0; j < y.length; j++) {
                r[i][j] = x[i] * y[j];
            }
        }
        return r;
    }

    private static double[] dot3(double[][] x, double[] y) {
        double[] r = new double[x[0].length];
        for (int i = 0; i < r.length; i++) {
            double t = 0.0;
            for (int j = 0; j < y.length; j++) {
                t += x[j][i] * y[j];
            }
            r[i] = t;
        }
        return r;
    }

}
