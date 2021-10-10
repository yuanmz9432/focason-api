package api.lemonico.attribute;


import org.seasar.doma.jdbc.SelectOptions;

import java.io.Serializable;

public final class LcPagination implements Serializable {
    public static final LcPagination DEFAULT = of(20, 1);
    private final int limit;
    private final int page;

    public LcPagination(final int limit, final int page) {
        this.limit = limit;
        this.page = page;
    }

    public static LcPagination of(int limit, int page) {
        return of(limit, page, (LcSort) null);
    }

    public static LcPagination of(int limit, int page, LcSort sort) {
        if (limit < 1) {
            throw new IllegalArgumentException("Page size (limit) must not be less than one!");
        } else if (page < 1) {
            throw new IllegalArgumentException("Page index (page) must not be less than one!");
        } else {
            return builder().limit(limit).page(page).build();
        }
    }

    public static LcPagination.LcPaginationBuilder builder() {
        return new LcPagination.LcPaginationBuilder();
    }

    public SelectOptions toSelectOptions() {
        return SelectOptions.get().limit(this.limit).offset(this.limit * (this.page - 1));
    }

    public LcPagination next() {
        return this.withPage(this.page + 1);
    }

    public LcPagination first() {
        return this.withPage(1);
    }

    public LcPagination previousOrFirst() {
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
        } else if (!(o instanceof LcPagination)) {
            return false;
        } else {
            LcPagination other = (LcPagination) o;
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

    public LcPagination withLimit(final int limit) {
        return this.limit == limit ? this : new LcPagination(limit, this.page);
    }

    public LcPagination withPage(final int page) {
        return this.page == page ? this : new LcPagination(this.limit, page);
    }

    public static class LcPaginationBuilder {
        private int limit;
        private int page;

        LcPaginationBuilder() {
        }

        public LcPagination.LcPaginationBuilder limit(final int limit) {
            this.limit = limit;
            return this;
        }

        public LcPagination.LcPaginationBuilder page(final int page) {
            this.page = page;
            return this;
        }

        public LcPagination build() {
            return new LcPagination(this.limit, this.page);
        }

        public String toString() {
            return "LcPagination.LcPaginationBuilder(limit=" + this.limit + ", page=" + this.page + ")";
        }
    }
}
