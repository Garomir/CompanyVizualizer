package ru.ramich.companyvizualizer.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.ramich.companyvizualizer.R;
import ru.ramich.companyvizualizer.models.Department;

public class DepartmentAdapter extends BaseAdapter {

    private List<Department> departments = null;

    public DepartmentAdapter(List<Department> departments) {
        super();
        this.departments = departments;
    }

    @Override
    public int getCount() {
        return departments.size();
    }

    @Override
    public Object getItem(int position) {
        return departments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return departments.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) v = View.inflate(parent.getContext(), R.layout.list_item, null);
        TextView txt1 = v.findViewById(R.id.tvItem);
        txt1.setText(departments.get(position).getName());
        return v;
    }
}
