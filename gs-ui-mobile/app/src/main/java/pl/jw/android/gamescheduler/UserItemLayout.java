package pl.jw.android.gamescheduler;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.jw.android.gamescheduler.data.User;

/**
 * Created by jacek on 2016-04-10.
 */
public class UserItemLayout extends LinearLayout {

    private static final String TAG = UserItemLayout.class.getSimpleName();

    @Bind(R.id.accountName)
    TextView accountName;

    private User data;

    public UserItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(User item) {
        this.data = item;

        accountName.setText(data.getAlias());
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);

    }
}
