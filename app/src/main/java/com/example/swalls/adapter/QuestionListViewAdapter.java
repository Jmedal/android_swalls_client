package com.example.swalls.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.swalls.R;
import com.example.swalls.WallDetailActivity;
import com.example.swalls.adapter.holder.QListViewHolder;
import com.example.swalls.core.util.VolleySingleton;
import com.example.swalls.modal.Wall;

import java.util.ArrayList;
import java.util.List;

public class QuestionListViewAdapter extends BaseAdapter {

    private Context context;
    private Activity activity;

    private LayoutInflater mInflater;

    private List data;

    public QuestionListViewAdapter(Context context){
        this.context = context;
        this.activity = activity;
        this.mInflater = LayoutInflater.from(context);
        this.data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final QListViewHolder holder;
        if(convertView == null){
            holder = new QListViewHolder();
            convertView = mInflater.inflate(R.layout.qlist_layout,null);
            holder.qlist_picture = (ImageView)convertView.findViewById(R.id.qlist_picture);
            holder.qlist_time = (TextView)convertView.findViewById(R.id.qlist_time);
            holder.qlist_title = (TextView)convertView.findViewById(R.id.qlist_title);
            holder.qlist_name = (TextView)convertView.findViewById(R.id.qlist_name);
            holder.qlist_layout = (LinearLayout)convertView.findViewById(R.id.qlist_layout);
            convertView.setTag(holder);
        }else {
            holder = (QListViewHolder) convertView.getTag();
        }

        holder.qlist_name.setText((String)((Wall)data.get(position)).getWriterName());
        holder.qlist_title.setText((String)((Wall)data.get(position)).getAbstracts());
        holder.qlist_time.setText((String)((Wall)data.get(position)).getWriterTime());
//        holder.qlist_picture.setImageIcon(Icon.createWithContentUri((String)((Wall)data.get(position)).getPicture()));
//        System.out.println((String)((Wall)data.get(position)).getPicture());
        String Imageulr = (String)((Wall)data.get(position)).getPicture();


        ImageRequest request = new ImageRequest(
//                "https://wx.qlogo.cn/mmopen/vi_32/yqiciclJJuEQNm3iadNQmFxjwiax3lRo1Ipc1cjD6zDWIov6hcic2NqnibxZ1zdMicoObkhulut26OicjOeXLG6SdwIAcA/132",
                Imageulr,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        holder.qlist_picture.setImageBitmap(bitmap);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                holder.qlist_picture.setImageResource(R.mipmap.ic_launcher);  //异常时加载的图片
            }
        });
//        System.out.println(this);
        VolleySingleton.getVolleySingleton(context.getApplicationContext())
                .addToRequestQueue(request);

        holder.qlist_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInflater.getContext(), WallDetailActivity.class);
                intent.putExtra("id",((Wall)data.get(position)).getId());
                mInflater.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    public void setData(List<Wall> data){
        if(data != null){
            this.data = data;
        }
    }
}
