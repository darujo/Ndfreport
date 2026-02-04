package ru.daru_jo.specifications;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class Specifications {

    public static <T> Specification<@NonNull T> ge(Specification<@NonNull T> specification, String field, Integer value) {
        if (value != null) {
            if (specification == null) {
                specification = ge(field, value);
            } else {
                specification = specification.and(ge(field, value));
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> ge(String field, Integer value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(field), value));

    }

    public static <T> Specification<@NonNull T> le(Specification<@NonNull T> specification, String field, Integer value) {
        if (value != null) {
            if (specification == null) {
                specification = le(field, value);
            } else {
                specification = specification.and(le(field, value));
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> le(String field, Integer value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(field), value));
    }

    public static <T> Specification<@NonNull T> ge(Specification<@NonNull T> specification, String field, Date value) {
        if (value != null) {
            if (specification == null) {
                specification = ge(field, value);
            } else {
                specification = specification.and(ge(field, value));
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> ge(String field, Date value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(field), value));

    }

    public static <T> Specification<@NonNull T> le(Specification<@NonNull T> specification, String field, Date value) {
        if (value != null) {
            if (specification == null) {
                specification = le(field, value);
            } else {
                specification = specification.and(le(field, value));
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> le(String field, Date value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(field), value));
    }

    public static <T> Specification<@NonNull T> gt(Specification<@NonNull T> specification, String field, Date value) {
        if (value != null) {
            if (specification == null) {
                specification = gt(field, value);
            } else {
                specification = specification.and(gt(field, value));
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> gt(String field, Date value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(field), value));

    }

    public static <T> Specification<@NonNull T> lt(Specification<@NonNull T> specification, String field, Date value) {
        if (value != null) {
            if (specification == null) {
                specification = lt(field, value);
            } else {
                specification = specification.and(lt(field, value));
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> lt(String field, Date value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(field), value));
    }

    public static <T> Specification<@NonNull T> eq(Specification<@NonNull T> specification, String field, Integer value) {
        if (value != null) {
            if (specification == null) {
                specification = eq(field, value);
            } else {
                specification = specification.and(eq(field, value));
            }
        }
        return specification;
    }

    public static <T> Specification<@NonNull T> eq(Specification<@NonNull T> specification, String field, Boolean value) {
        if (value != null) {
            if (specification == null) {
                specification = eq(field, value);
            } else {
                specification = specification.and(eq(field, value));
            }
        }
        return specification;
    }

    public static <T> Specification<@NonNull T> eq(Specification<@NonNull T> specification, String field, Date value) {
        if (value != null) {
            if (specification == null) {
                specification = eq(field, value);
            } else {
                specification = specification.and(eq(field, value));
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> eq(String field, Object value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(field), value));

    }

    public static <T> Specification<@NonNull T> isNotNull(Specification<@NonNull T> specification, String field, Boolean value) {
        if (value != null && value) {
            if (specification == null) {
                specification = isNotNull(field);
            } else {
                specification = specification.and(isNotNull(field));
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> isNotNull(String field) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(field)));

    }

    public static <T> Specification<@NonNull T> eq(Specification<@NonNull T> specification, String field, Long value) {
        if (value != null) {
            if (specification == null) {
                specification = eq(field, value);
            } else {
                specification = specification.and(eq(field, value));
            }
        }
        return specification;
    }

    //нужно для того чтобы записи на разных страницах не повторялись
    public static <T> Specification<@NonNull T> queryDistinctTrue() {
        return ((root, query, criteriaBuilder) -> {
            query.distinct(false);
            return null;
        });
    }

    public static <T> Specification<@NonNull T> like(Specification<@NonNull T> specification, String field, String value) {
        if (value != null && !value.isBlank()) {
            if (specification == null) {
                specification = like(field, value);
            } else {
                specification = specification.and(like(field, value));
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> like(String field, String value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(field)), String.format("%%%s%%", value).toUpperCase()));
    }

    public static <T> Specification<@NonNull T> in(Specification<@NonNull T> specification, String field, Long[] value) {
        if (value != null && value.length > 0) {
            if (value.length == 1) {
                if (specification == null) {
                    specification = eq(field, value[0]);
                } else {
                    specification = eq(specification, field, value[0]);
                }
            } else {
                if (specification == null) {
                    specification = in(field, Arrays.asList(value));
                } else {
                    specification = specification.and(in(field, Arrays.asList(value)));
                }
            }
        }
        return specification;
    }

    public static <T> Specification<@NonNull T> in(Specification<@NonNull T> specification, String field, List<?> value) {
        if (value != null && !value.isEmpty()) {
            if (value.size() == 1) {
                specification = eq(specification, field, value.get(0));
            } else {
                if (specification == null) {
                    specification = in(field, value);
                } else {
                    specification = specification.and(in(field, value));
                }
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> in(String field, List<?> value) {
        return ((root, query, criteriaBuilder) ->
                root.get(field).in(value));
    }

    public static <T> Specification<@NonNull T> eq(Specification<@NonNull T> specification, String field, String value) {
        if (value != null && !value.isEmpty()) {
            if (specification == null) {
                specification = eq(field, value);
            } else {
                specification = specification.and(eq(field, value));
            }
        }
        return specification;
    }

    public static <T> Specification<@NonNull T> notEqual(Specification<@NonNull T> specification, String field, Long value) {
        if (value != null) {
            if (specification == null) {
                specification = notEqual(field, value);
            } else {
                specification = specification.and(notEqual(field, value));
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> notEqual(String field, Object value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(field), value)
        );
    }

    public static <T> Specification<@NonNull T> ne(Specification<@NonNull T> specification, String field, Long value) {
        if (value != null) {
            if (specification == null) {
                specification = ne(field, value);
            } else {
                specification = specification.and(ne(field, value));
            }
        }
        return specification;
    }

    public static <T> Specification<@NonNull T> ne(String field, Object value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(field), value));
    }

    public static <T> Specification<@NonNull T> ne(Specification<@NonNull T> specification, String field, Boolean value) {
        if (value != null) {
            if (specification == null) {
                specification = ne(field, value);
            } else {
                specification = specification.and(ne(field, value));
            }
        }
        return specification;
    }

    public static <T> Specification<@NonNull T> ne(String field, Boolean value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(field), value));
    }

    public static <T> Specification<@NonNull T> eqIgnoreCase(Specification<@NonNull T> specification, String field, String value) {
        if (value != null) {
            if (specification == null) {
                specification = eqIgnoreCase(field, value);
            } else {
                specification = specification.and(eqIgnoreCase(field, value));
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> eqIgnoreCase(String field, String value) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get(field)), value.toLowerCase()));
    }

    public static <T> Specification<@NonNull T> eq(Specification<@NonNull T> specification, String field, Object value) {
        if (value != null) {
            if (specification == null) {
                specification = eq(field, value);
            } else {
                specification = specification.and(eq(field, value));
            }
        }
        return specification;
    }

    public static <T> Specification<@NonNull T> inO(Specification<@NonNull T> specification, String field, List<Object> value) {
        if (value != null && !value.isEmpty()) {
            if (value.size() == 1) {
                specification = eq(specification, field, value.get(0));
            } else {
                if (specification == null) {
                    specification = inObject(field, value);
                } else {
                    specification = specification.and(inObject(field, value));
                }
            }
        }
        return specification;
    }

    private static <T> Specification<@NonNull T> inObject(String field, List<Object> value) {
        return ((root, query, criteriaBuilder) ->
                root.get(field).in(value));
    }

}
