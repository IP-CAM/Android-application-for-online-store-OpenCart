package com.example.store.oc;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Rost on 02.11.2018.
 */

public class Attribute extends DialogFragment {
    AttributeGroups[] data;
    LinearLayout head = null;
    LinearLayout body = null;
    TextView head_name = null;
    TextView name = null;
    TextView text = null;
    Context context;
    public Attribute(AttributeGroups[] d, Context context) {
        this.data = d;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Характеристики");
        View v = inflater.inflate(R.layout.attribute, null);
        LinearLayout wrapper = (LinearLayout) v.findViewById(R.id.wrapper);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
        LinearLayout.LayoutParams layoutParams_for_head = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        layoutParams_for_head.setMargins(15,15,0,0);
        LinearLayout.LayoutParams layoutParams_for_liner = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < data.length; i++){
            head = new LinearLayout(context);
            head.setOrientation(LinearLayout.HORIZONTAL);
            head_name = new TextView(context);
            head_name.setText(data[i].getName());
            head_name.setTextSize(20);
            head_name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            head.addView(head_name, layoutParams_for_head);
            wrapper.addView(head, layoutParams_for_liner);

            for (int j = 0; j < data[i].getAttribute().length; j++){
                body = new LinearLayout(context);
                body.setOrientation(LinearLayout.HORIZONTAL);
                name = new TextView(context);
                name.setText(data[i].getAttribute()[j].getName());
                text = new TextView(context);
                text.setText(data[i].getAttribute()[j].getText());
                body.addView(name, layoutParams);
                body.addView(text, layoutParams);
                wrapper.addView(body, layoutParams_for_liner);
            }
        }

        return v;
    }
}
