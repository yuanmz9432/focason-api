package api.lemonico.attribute;



import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;

public final class LcResultSet<T> implements Serializable
{
    private final List<T> data;
    private final long count;

    public LcResultSet(Page<T> page) {
        this.data = page.getContent();
        this.count = page.getTotalElements();
    }

    public LcResultSet(final List<T> data, final long count) {
        this.data = data;
        this.count = count;
    }

    public static <T> LcResultSet<T> emptyResultSet() {
        return new LcResultSet<>(Collections.emptyList(), 0L);
    }

    public static <T> LcResultSet.LcResultSetBuilder<T> builder() {
        return new LcResultSet.LcResultSetBuilder();
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
        } else if (!(o instanceof LcResultSet)) {
            return false;
        } else {
            LcResultSet<?> other = (LcResultSet) o;
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

    public LcResultSet<T> withData(final List<T> data) {
        return this.data == data ? this : new LcResultSet(data, this.count);
    }

    public LcResultSet<T> withCount(final long count) {
        return this.count == count ? this : new LcResultSet(this.data, count);
    }

    public static class LcResultSetBuilder<T>
    {
        private List<T> data;
        private long count;

        LcResultSetBuilder() {}

        public LcResultSet.LcResultSetBuilder<T> data(final List<T> data) {
            this.data = data;
            return this;
        }

        public LcResultSet.LcResultSetBuilder<T> count(final long count) {
            this.count = count;
            return this;
        }

        public LcResultSet<T> build() {
            return new LcResultSet<>(this.data, this.count);
        }

        public String toString() {
            return "LcResultSet.LcResultSetBuilder(data=" + this.data + ", count=" + this.count + ")";
        }
    }
}
