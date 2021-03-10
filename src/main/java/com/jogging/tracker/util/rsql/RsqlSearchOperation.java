package com.jogging.tracker.util.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

public enum RsqlSearchOperation {
    EQUAL(new ComparisonOperator(" eq ")),
    NOT_EQUAL(new ComparisonOperator(" ne ")),
    GREATER_THAN(new ComparisonOperator(" gt ")),
    GREATER_THAN_OR_EQUAL(new ComparisonOperator(" gte ")),
    LESS_THAN(new ComparisonOperator(" lt ")),
    LESS_THAN_OR_EQUAL(new ComparisonOperator(" lte "));

    private ComparisonOperator operator;

    RsqlSearchOperation(ComparisonOperator operator) {
        this.operator = operator;
    }

    public static RsqlSearchOperation getSimpleOperator(ComparisonOperator operator) {
        for (RsqlSearchOperation operation : values()) {
            if (operation.getOperator().getSymbol().equals(operator.getSymbol())) {
                return operation;
            }
        }
        throw new IllegalArgumentException(String.format("Unknown operator %s", operator.getSymbol()));
    }

    public ComparisonOperator getOperator() {
        return operator;
    }
}