package com.example.jacek.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jacek.myapplication.data.DataMock;
import com.example.jacek.myapplication.data.Event;

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
    //    @Bind(R.id.eventTextViewLabelAnswerMissing)
//    TextView textViewLabelAnswerMissing;
//    @Bind(R.id.eventListViewAnswerMissing)
//    ListView listViewViewLabelAnswerMissing;
    @Bind(R.id.eventButtonConfirm)
    Button buttonConfirm;
    @Bind(R.id.eventButtonReject)
    Button buttonReject;


    public EventItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(Event data) {
        listViewConfirmed.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_accounts_row, R.id.accountName, data.accountsConfirmed));
        listViewRejected.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_accounts_row, R.id.accountName, data.accountsRejected));
        //listViewViewLabelAnswerMissing.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_accounts_row, R.id.accountName, DataMock.getAccountsMissing()));

        boolean actionTaken = data.accountsRejected.contains(GameSchedulerApplication.getInstance().getUserName()) || data.accountsConfirmed.contains(GameSchedulerApplication.getInstance().getUserName());
        int buttonVisibility = actionTaken ? View.INVISIBLE : View.VISIBLE;
        buttonConfirm.setVisibility(buttonVisibility);
        buttonReject.setVisibility(buttonVisibility);
    }


    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);

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
