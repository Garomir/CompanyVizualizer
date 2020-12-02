package ru.ramich.companyvizualizer.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.ramich.companyvizualizer.R;
import ru.ramich.companyvizualizer.models.Company;

public class CustomAdapter extends BaseAdapter {

    private List<Company> companies = null;

    public CustomAdapter(List<Company> companies) {
        super();
        this.companies = companies;
    }

    @Override
    public int getCount() {
        return companies.size();
    }

    @Override
    public Object getItem(int position) {
        return companies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return companies.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) v = View.inflate(parent.getContext(), R.layout.list_item, null);
        TextView txt1 = v.findViewById(R.id.tvItem);
        txt1.setText(companies.get(position).getName());
        return v;
    }
}
