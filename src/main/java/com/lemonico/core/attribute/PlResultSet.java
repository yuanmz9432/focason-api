/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.attribute;



import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;

public final class PlResultSet<T> implements Serializable
{
    private final List<T> data;
    private final long count;

    public PlResultSet(Page<T> page) {
        this.data = page.getContent();
        this.count = page.getTotalElements();
    }

    public PlResultSet(final List<T> data, final long count) {
        this.data = data;
        this.count = count;
    }

    public static <T> PlResultSet<T> emptyResultSet() {
        return new PlResultSet<>(Collections.emptyList(), 0L);
    }

    public static <T> LcResultSetBuilder<T> builder() {
        return new LcResultSetBuilder();
    }

    @JsonIgnore
    public Stream<T> stream() {
        return this.data.stream();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public List<T> getData() {
        return this.data;
    }

    public long getCount() {
        return this.count;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PlResultSet)) {
            return false;
        } else {
            PlResultSet<?> other = (PlResultSet) o;
            Object this$data = this.getData();
            Object other$data = other.getData();
            if (this$data == null) {
                if (other$data == null) {
                    return this.getCount() == other.getCount();
                }
            } else if (this$data.equals(other$data)) {
                return this.getCount() == other.getCount();
            }

            return false;
        }
    }

    public int hashCode() {
        Object $data = this.getData();
        int result = 59 + ($data == null ? 43 : $data.hashCode());
        long $count = this.getCount();
        result = result * 59 + (int) ($count >>> 32 ^ $count);
        return result;
    }

    public String toString() {
        List<T> var10000 = this.getData();
        return "LcResultSet(data=" + var10000 + ", count=" + this.getCount() + ")";
    }

    public PlResultSet<T> withData(final List<T> data) {
        return this.data == data ? this : new PlResultSet(data, this.count);
    }

    public PlResultSet<T> withCount(final long count) {
        return this.count == count ? this : new PlResultSet(this.data, count);
    }

    public static class LcResultSetBuilder<T>
    {
        private List<T> data;
        private long count;

        LcResultSetBuilder() {}

        public LcResultSetBuilder<T> data(final List<T> data) {
            this.data = data;
            return this;
        }

        public LcResultSetBuilder<T> count(final long count) {
            this.count = count;
            return this;
        }

        public PlResultSet<T> build() {
            return new PlResultSet<>(this.data, this.count);
        }

        public String toString() {
            return "LcResultSet.LcResultSetBuilder(data=" + this.data + ", count=" + this.count + ")";
        }
    }
}
