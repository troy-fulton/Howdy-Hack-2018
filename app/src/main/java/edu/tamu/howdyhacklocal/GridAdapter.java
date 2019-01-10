package edu.tamu.howdyhacklocal;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


// Adapted from https://www.youtube.com/watch?v=2RypDFzkIyg
public class GridAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private Bitmap[] bitmapArray;

    public GridAdapter(Context context, Bitmap[] bmps) {
        this.context = context;
        this.bitmapArray = bmps;
    }


    // Inherited methods:
    @Override
    public int getCount() {
        return bitmapArray.length;
    }

    @Override
    public Object getItem(int i) {
        return bitmapArray[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View gridView  = view;

        if (gridView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            gridView = inflater.inflate(R.layout.custom_layout, null);
        }

        ImageView imageView = (ImageView) gridView.findViewById(R.id.imageView);

        /*
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        //lp.height = bitmapArray[i].getHeight();
        //lp.width = bitmapArray[i].getWidth();
        lp.width = viewGroup.getWidth() / 3;
        lp.height = viewGroup.getWidth() / 3;
        imageView.requestLayout();
        */

        //imageView.setLayoutParams(new ViewGroup.LayoutParams(80, 80));

        imageView.setImageBitmap(bitmapArray[i]);

        return gridView;
    }
}
