package com.jogging.tracker.util.rsql;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.util.CommonUtils;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericRsqlSpecification<T> implements Specification<T> {

    private String property;
    private ComparisonOperator operator;
    private String argument;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Object argument = castArguments(root);

        switch (RsqlSearchOperation.getSimpleOperator(operator)) {

            case EQUAL: {
                if ("null".equals(argument) || "NULL".equals(argument)) {
                    return builder.isNull(root.get(property));
                } else {
                    return builder.equal(root.get(property), argument);
                }
            }
            case NOT_EQUAL: {
                if ("null".equals(argument) || "NULL".equals(argument)) {
                    return builder.isNotNull(root.get(property));
                } else {
                    return builder.notEqual(root.get(property), argument);
                }
            }
            case GREATER_THAN: {
                if (argument instanceof LocalDate) {
                    return builder.greaterThan(root.get(property), (LocalDate) argument);
                } else {
                    return builder.greaterThan(root.get(property), argument.toString());
                }
            }
            case GREATER_THAN_OR_EQUAL: {
                if (argument instanceof LocalDate) {
                    return builder.greaterThanOrEqualTo(root.get(property), (LocalDate) argument);
                } else {
                    return builder.greaterThanOrEqualTo(root.get(property), argument.toString());
                }
            }
            case LESS_THAN: {
                if (argument instanceof LocalDate) {
                    return builder.lessThan(root.get(property), (LocalDate) argument);
                } else {
                    return builder.lessThan(root.get(property), argument.toString());
                }
            }
            case LESS_THAN_OR_EQUAL: {
                if (argument instanceof LocalDate) {
                    return builder.lessThanOrEqualTo(root.get(property), (LocalDate) argument);
                } else {
                    return builder.lessThanOrEqualTo(root.get(property), argument.toString());
                }
            }
        }

        return null;
    }

    private Object castArguments(final Root<T> root) {

        Class<?> type = null;

        try {
            type = root.get(property).getJavaType();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown field: " + property);
        }


        if ("null".equalsIgnoreCase(argument)) {
            return argument;
        }


        try {
            if (type.equals(Integer.class)) {
                return Integer.parseInt(argument);

            } else if (type.equals(Long.class)) {
                return Long.parseLong(argument);

            } else if (type.equals(User.Role.class)) {
                return User.Role.valueOf(argument);

            } else if (type.equals(User.Status.class)) {
                return User.Status.valueOf(argument);

            } else if (type.equals(LocalDate.class)) {
                return CommonUtils.fromDateString(argument);

            } else {
                return argument;
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Bad argument: " + argument, e);
        } catch (AppException e) {
            throw new IllegalArgumentException(AppException.ErrorCodeMsg.DATE_PARSE_ERROR.getMessage());
        }

    }

}