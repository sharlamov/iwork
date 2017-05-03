package com.dao.model;

import java.util.LinkedHashSet;

public class QueryBuilder extends LinkedHashSet<String> {

    private String sql;
    private StringBuilder query;
    private int len;

    public QueryBuilder(String sql) {
        this.sql = sql;
        len = sql.length();
        query = new StringBuilder(len);
        parse();
    }

    private void parse() {
        StringBuilder sb = new StringBuilder();

        boolean isParam = false;
        boolean isQuote = false;

        for (int i = 0; i <= len; i++) {
            char c = i == len ? '\0' : sql.charAt(i);

            if (!isParam && !isQuote && c == ':') {
                query.append('?');
                isParam = true;
                continue;
            }

            if (c == '\'')
                isQuote = !isQuote;

            if (!isQuote && c == '?')
                add(sb.toString());

            if (isParam) {
                if (Character.isLetterOrDigit(c) || c == '$' || c == '_') {
                    sb.append(c);
                    continue;
                } else {
                    add(sb.toString());
                    sb.setLength(0);
                    isParam = false;
                }
            }
            query.append(c);
        }
    }

    public String getQuery() {
        return query.toString();
    }
}
