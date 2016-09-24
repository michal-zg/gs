package pl.jw.android.gamescheduler.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacek on 2016-09-22.
 */

public class ArrayAdapterItemWrapper<D> {

    private D data;

    private ArrayAdapterItemWrapper(D data) {
        this.data = data;
    }

    public void update(D data) {
        this.data = data;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayAdapterItemWrapper<?> that = (ArrayAdapterItemWrapper<?>) o;

        return data != null ? data.equals(that.data) : that.data == null;

    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }


    public static <D> D unwrap(Object item) {
        return ((ArrayAdapterItemWrapper<D>) item).data;
    }

    public static <D> List<ArrayAdapterItemWrapper<D>> wrap(List<D> items) {
        ArrayList<ArrayAdapterItemWrapper<D>> list = new ArrayList<>();
        for (D item : items) {
            list.add(wrap(item));
        }

        return list;
    }

    public static <D> ArrayAdapterItemWrapper<D> wrap(D item) {
        return new ArrayAdapterItemWrapper<D>(item);
    }
}
