package com.github.euler.dl4j;

public abstract class AbstractMatrixSerializer<T> implements MatrixSerializer<T> {

    private final float limitValue;
    private final Operator operator;

    public AbstractMatrixSerializer(float limitValue, Operator operator) {
        super();
        this.limitValue = limitValue;
        this.operator = operator;
    }

    protected Boolean isTrue(float value) {
        switch (this.operator) {
        case GT:
            return value > limitValue;
        case GTE:
            return value >= limitValue;
        case LT:
            return value < limitValue;
        case LTE:
            return value <= limitValue;
        default:
            return false;
        }
    }

    public static enum Operator {
        GT, GTE, LT, LTE
    }

}
