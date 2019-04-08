package com.payment.snappaytable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.payment.snappaytable.model.Product;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {

    private final Context mContext;

    private LayoutInflater mInflater;

    private ArrayList<Product> products;

    private ProductAdapter(Context context, ArrayList<Product> products) {
        this.mContext = context;
        this.products = products;
        mInflater = LayoutInflater.from(context);
    }

    public static ProductAdapter build(Context context, ArrayList<Product> products) {
        return new ProductAdapter(context, products);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.product_details, null);

            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.product_text_view);
            holder.price = convertView.findViewById(R.id.price_text_view);
            holder.image = convertView.findViewById(R.id.product_image);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(products.get(position).getName());
        holder.price.setText(String.format("$ %s", Float.toString(products.get(position).getPrice())));
        holder.image.setImageResource(products.get(position).getResource());

        return convertView;
    }

    private static class ViewHolder {

        TextView name;

        TextView price;

        ImageView image;
    }
}
