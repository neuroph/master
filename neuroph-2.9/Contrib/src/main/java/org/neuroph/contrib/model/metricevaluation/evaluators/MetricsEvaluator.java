package org.neuroph.contrib.model.metricevaluation.evaluators;

import org.neuroph.contrib.model.metricevaluation.domain.ClassificationOutput;
import org.neuroph.contrib.model.metricevaluation.domain.ConfusionMatrix;
import org.neuroph.contrib.model.metricevaluation.domain.MetricResult;
import org.neuroph.core.data.DataSet;

public abstract class MetricsEvaluator implements NeurophEvaluator<MetricResult> {

    ConfusionMatrix confusionMatrix;

    private MetricsEvaluator(String[] labels, int classNumber) {
        confusionMatrix = new ConfusionMatrix.ConfusionMatrixBuilder()
                .withLabels(labels)
                .withClassNumber(classNumber)
                .createConfusionMatrix();
    }

    @Override
    public MetricResult getEvaluationResult() {
        return MetricResult.fromConfusionMatrix(confusionMatrix);
    }

    public static MetricsEvaluator createEvaluator(final DataSet dataSet) {
        if (dataSet.getOutputSize() == 1) {
            //TODO how can we handle different thresholds???
            return new BinaryClassEvaluator(0.5);
        } else {
            return new MultiClassEvaluator(dataSet);
        }
    }


    /**
     * Binary evaluator used for computation of metrics in case when data has only one output result (one output neuron)
     */
    private static class BinaryClassEvaluator extends MetricsEvaluator {

        public static final String[] BINARY_CLASS_LABELS = new String[]{"No", "Yes"};
        public static final int BINARY_CLASSIFICATION = 2;
        public static final int TRUE = 1;
        public static final int FALSE = 0;

        private double threshold;

        private BinaryClassEvaluator(double threshold) {
            super(BINARY_CLASS_LABELS, BINARY_CLASSIFICATION);
            this.threshold = threshold;
        }

        @Override
        public void processResult(double[] predictedOutput, double[] actualOutput) {
            int actualClass = calculateClass(actualOutput[0]);
            int predictedClass = calculateClass(predictedOutput[0]);

            confusionMatrix.incrementElement(actualClass, predictedClass);
        }

        private int calculateClass(double classResult) {
            int classValue = FALSE;
            if (classResult >= threshold) {
                classValue = TRUE;
            }
            return classValue;
        }

    }

    /**
     * Evaluator used for computation of metrics in case when data has
     * multiple classes - one vs many classification
     */
    private static class MultiClassEvaluator extends MetricsEvaluator {

        private MultiClassEvaluator(DataSet dataSet) {
            super(dataSet.getColumnNames(), dataSet.getOutputSize());
        }

        @Override
        public void processResult(double[] predictedOutput, double[] actualOutput) {
            int actualClass = ClassificationOutput.getMaxOutput(actualOutput).getActualClass();
            int predictedClass = ClassificationOutput.getMaxOutput(predictedOutput).getActualClass();

            confusionMatrix.incrementElement(actualClass, predictedClass);
        }
    }

}
