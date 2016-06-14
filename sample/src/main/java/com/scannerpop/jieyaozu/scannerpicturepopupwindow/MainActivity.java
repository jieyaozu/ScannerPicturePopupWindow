package com.scannerpop.jieyaozu.scannerpicturepopupwindow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    private ScannerPicturePopupWindow scannerPicturePopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridview);
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.sample1);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.sample2);
        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.mipmap.sample3);
        Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(), R.mipmap.sample4);
        Bitmap bitmap5 = BitmapFactory.decodeResource(getResources(), R.mipmap.sample5);
        Bitmap bitmap6 = BitmapFactory.decodeResource(getResources(), R.mipmap.sample6);
        Bitmap bitmap7 = BitmapFactory.decodeResource(getResources(), R.mipmap.sample7);
        Bitmap bitmap8 = BitmapFactory.decodeResource(getResources(), R.mipmap.sample8);
        Bitmap bitmap9 = BitmapFactory.decodeResource(getResources(), R.mipmap.sample9);
        bitmapList.add(bitmap1);
        bitmapList.add(bitmap2);
        bitmapList.add(bitmap3);
        bitmapList.add(bitmap4);
        bitmapList.add(bitmap5);
        bitmapList.add(bitmap6);
        bitmapList.add(bitmap7);
        bitmapList.add(bitmap8);
        bitmapList.add(bitmap9);
        GridViewAdapter gridViewAdapter = new GridViewAdapter();
        gridView.setAdapter(gridViewAdapter);

        scannerPicturePopupWindow = new ScannerPicturePopupWindow(this, gridView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class GridViewAdapter extends BaseAdapter {
        private Map<Integer, ImageView> imageViewList = new HashMap<Integer, ImageView>();

        @Override
        public int getCount() {
            return bitmapList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(MainActivity.this, R.layout.activity_gridview_item, null);
            }
            final ImageView imageView = (ImageView) view.findViewById(R.id.plan_unit_album_item_image);
            if (!imageViewList.containsKey(position)) {
                imageViewList.put(position, imageView);
            }
            imageView.setImageBitmap(bitmapList.get(position));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scannerPicturePopupWindow.showScannerPictureWindow(imageViewList, bitmapList.get(position), bitmapList, position);
                }
            });
            return view;
        }
    }
}
