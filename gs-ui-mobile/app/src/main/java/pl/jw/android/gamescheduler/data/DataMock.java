package pl.jw.android.gamescheduler.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jacek on 2016-09-09.
 */
public class DataMock {

    public static List<String> getAccountsYes() {
        return Arrays.asList("ja");
    }

    public static List<String> getAccountsNo() {
        return new ArrayList<>();
    }

    public static List<String> getAccountsMissing() {
        return Arrays.asList("maciek", "walek", "chudy");
    }
}
