package org.neuroph.samples.evaluation.evaluators;

import org.neuroph.samples.evaluation.evaluators.NeurophEvaluator;
import org.neuroph.samples.evaluation.domain.ErrorMeasureResult;


public class CrossEntropyEvaluator implements NeurophEvaluator<ErrorMeasureResult> {

    private double cumulativeError;
    private double n;


    @Override
    public void processResult(double[] predictedOutputs, double[] actualOutputs) {

        for (int i = 0; i < actualOutputs.length; i++) {
            cumulativeError += actualOutputs[i] * Math.log(predictedOutputs[i]);
        }
        n++;
    }

    @Override
    public ErrorMeasureResult getEvaluationResult() {
        double averageError = -cumulativeError / n;
        return new ErrorMeasureResult.ErrorMeasureResultBuilder()
                .withError(averageError)
                .createErrorMeasureResult();
    }
}
