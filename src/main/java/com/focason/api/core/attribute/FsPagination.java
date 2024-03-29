/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.attribute;



import java.io.Serializable;
import org.seasar.doma.jdbc.SelectOptions;

public final class FsPagination implements Serializable
{
    public static final FsPagination DEFAULT = of(20, 1);
    private final int limit;
    private final int page;

    public FsPagination(final int limit, final int page) {
        this.limit = limit;
        this.page = page;
    }

    public static FsPagination of(int limit, int page) {
        return of(limit, page, (FsSort) null);
    }

    public static FsPagination of(int limit, int page, FsSort sort) {
        if (limit < 1) {
            throw new IllegalArgumentException("Page size (limit) must not be less than one!");
        } else if (page < 1) {
            throw new IllegalArgumentException("Page index (page) must not be less than one!");
        } else {
            return builder().limit(limit).page(page).build();
        }
    }

    public static FsPagination.LcPaginationBuilder builder() {
        return new FsPagination.LcPaginationBuilder();
    }

    public SelectOptions toSelectOptions() {
        return SelectOptions.get().limit(this.limit).offset(this.limit * (this.page - 1));
    }

    public FsPagination next() {
        return this.withPage(this.page + 1);
    }

    public FsPagination first() {
        return this.withPage(1);
    }

    public FsPagination previousOrFirst() {
        return this.hasPrevious() ? this.withPage(this.page - 1) : this.first();
    }

    public boolean hasPrevious() {
        return this.page > 1;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getPage() {
        return this.page;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof FsPagination)) {
            return false;
        } else {
            FsPagination other = (FsPagination) o;
            if (this.getLimit() != other.getLimit()) {
                return false;
            } else {
                return this.getPage() == other.getPage();
            }
        }
    }

    public int hashCode() {
        int result = 59 + this.getLimit();
        result = result * 59 + this.getPage();
        return result;
    }

    public String toString() {
        int var10000 = this.getLimit();
        return "LcPagination(limit=" + var10000 + ", page=" + this.getPage() + ")";
    }

    public FsPagination withLimit(final int limit) {
        return this.limit == limit ? this : new FsPagination(limit, this.page);
    }

    public FsPagination withPage(final int page) {
        return this.page == page ? this : new FsPagination(this.limit, page);
    }

    public static class LcPaginationBuilder
    {
        private int limit;
        private int page;

        LcPaginationBuilder() {}

        public FsPagination.LcPaginationBuilder limit(final int limit) {
            this.limit = limit;
            return this;
        }

        public FsPagination.LcPaginationBuilder page(final int page) {
            this.page = page;
            return this;
        }

        public FsPagination build() {
            return new FsPagination(this.limit, this.page);
        }

        public String toString() {
            return "LcPagination.LcPaginationBuilder(limit=" + this.limit + ", page=" + this.page + ")";
        }
    }
}
