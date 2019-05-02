package com.example.android.products;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.products.data.ProductContract.ProductEntry;


public class ProductCursorAdapter extends android.widget.CursorAdapter {
    private LayoutInflater inflater;
    Context mContext;


    public ProductCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvname = (TextView) view.findViewById(R.id.Product_Name);
        TextView tvprice = (TextView) view.findViewById(R.id.Product_Price);
        TextView tvquantity = (TextView) view.findViewById(R.id.Product_quantity);
        ImageView productimageView = (ImageView) view.findViewById(R.id.Producimageall);
        int quantitycheck = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_Quantity));


        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_Price));
        String quantity = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_Quantity));
        String image = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_Image));


        tvname.setText(name);
        tvprice.setText(price + "$");
        tvquantity.setText(quantity);
        if (TextUtils.isEmpty(image)) {
            productimageView.setImageResource(R.drawable.addproduct);
        } else {
            productimageView.setImageURI(Uri.parse(image));


        }

        if (quantitycheck <= 10) {
            tvquantity.setTextColor(Color.RED);
        }


    }
}
