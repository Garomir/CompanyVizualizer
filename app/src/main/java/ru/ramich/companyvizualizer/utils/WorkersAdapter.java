package ru.ramich.companyvizualizer.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.ramich.companyvizualizer.R;
import ru.ramich.companyvizualizer.models.Worker;

public class WorkersAdapter extends BaseAdapter {

    private List<Worker> workers;

    public WorkersAdapter(List<Worker> workers) {
        super();
        this.workers = workers;
    }

    @Override
    public int getCount() {
        return workers.size();
    }

    @Override
    public Object getItem(int position) {
        return workers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return workers.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) v = View.inflate(parent.getContext(), R.layout.list_item_worker, null);
        TextView txt1 = v.findViewById(R.id.tvFirstname);
        TextView txt2 = v.findViewById(R.id.tvLastname);
        TextView txt3 = v.findViewById(R.id.tvPosition);
        TextView txt4 = v.findViewById(R.id.tvSalary);
        txt1.setText(workers.get(position).getFirstname());
        txt2.setText(workers.get(position).getLastname());
        txt3.setText(workers.get(position).getPosition());
        txt4.setText(String.valueOf(workers.get(position).getSalary()));
        return v;
    }
}
