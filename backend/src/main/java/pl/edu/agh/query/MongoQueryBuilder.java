package pl.edu.agh.query;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MongoQueryBuilder {

    private final List<QueryFilter> filters = new ArrayList<>();

    public MongoQueryBuilder addFilter(QueryFilter filter) {
        filters.add(filter);
        return this;
    }

    public MongoQueryBuilder addFilter(String fieldName, Operation operation, Object value) {
        if (fieldName != null && operation != null && value != null) {
            filters.add(new QueryFilter(fieldName, operation, value));
        }
        return this;
    }

    public Query build() {
        Query query = new Query();
        Criteria criteria = new Criteria();

        for (QueryFilter filter : filters) {
            criteria = switch (filter.operation()) {
                case EQ -> criteria.and(filter.fieldName()).is(filter.value());
                case GT -> criteria.and(filter.fieldName()).gt(filter.value());
                case LT -> criteria.and(filter.fieldName()).lt(filter.value());
                case GTE -> criteria.and(filter.fieldName()).gte(filter.value());
                case LTE -> criteria.and(filter.fieldName()).lte(filter.value());
                case NE -> criteria.and(filter.fieldName()).ne(filter.value());
                case IN -> criteria.and(filter.fieldName()).in((Collection<?>) filter.value());
            };
        }
        query.addCriteria(criteria);

        return query;
    }
}

