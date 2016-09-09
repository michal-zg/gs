package com.example.jacek.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jacek.myapplication.data.DataMock;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jacek on 2016-04-10.
 */
public class EventItemLayout extends LinearLayout {

    @Bind(R.id.eventEditTextLabelDateTime)
    TextView editTextLabelDateTime;
    @Bind(R.id.eventTextViewLabelConfirmed)
    TextView textViewLabelConfirmed;
    @Bind(R.id.eventListViewConfirmed)
    ListView listViewConfirmed;
    @Bind(R.id.eventTextViewLabelRejected)
    TextView textViewLabelRejected;
    @Bind(R.id.eventListViewRejected)
    ListView listViewRejected;
    @Bind(R.id.eventTextViewLabelAnswerMissing)
    TextView textViewLabelAnswerMissing;
    @Bind(R.id.eventListViewAnswerMissing)
    ListView listViewViewLabelAnswerMissing;
    @Bind(R.id.eventButtonConfirm)
    Button buttonConfirm;
    @Bind(R.id.eventButtonReject)
    Button buttonReject;


    public EventItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);

        listViewConfirmed.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_accounts_row, R.id.accountName, DataMock.getAccountsYes()));
        listViewRejected.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_accounts_row, R.id.accountName, DataMock.getAccountsNo()));
        listViewViewLabelAnswerMissing.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_accounts_row, R.id.accountName, DataMock.getAccountsMissing()));
    }

    @OnClick(R.id.eventButtonConfirm)
    void confirm() {
        // TODO
    }

    @OnClick(R.id.eventButtonReject)
    void reject() {
        // TODO
    }
}
