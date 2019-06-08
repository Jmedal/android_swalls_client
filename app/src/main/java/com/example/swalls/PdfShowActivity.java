package com.example.swalls;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.swalls.core.util.FileRequestUtils;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;


public class PdfShowActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener, OnDrawListener{

    private final String url = ItemListActivity.URL+ "/downLoadFile";//定义网络图片渎地址

    private Thread mThread;

    private String fileName;

    private String reportName;

    private PDFView pdfView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_show_activity);
        pdfView = (PDFView) findViewById( R.id.pdfView);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            fileName = bundle.getString("fileName");
            reportName = bundle.getString("reportName");
        }

        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);

        if(!suffix.equals("pdf")){
            Toast.makeText(PdfShowActivity.this , "文件类型有误，打开失败！", Toast.LENGTH_SHORT).show();
            this.finish();
        }else {
            String path = Environment.getExternalStorageDirectory()
                    + "/download/share/" + fileName;
            File file = new File(path);
            if (!file.exists() || file.length() == 0)
                requestPDF(fileName);     //文件不存在或损坏，下载文件
            else
               displayFromFile(file);    //文件存在，从sd卡中读取pdf文件
        }
    }

    /**
     * 显示pdf文件
     * @param file
     */
    private void displayFromFile( File file ) {
        pdfView.fromFile(file)   //设置pdf文件地址
                .defaultPage(1)         //设置默认显示第1页
                .onPageChange(this)     //设置翻页监听
                .onLoad(this)           //设置加载监听
                .onDraw(this)            //绘图监听
                .showMinimap(false)     //pdf放大的时候，是否在屏幕的右上角生成小地图
                .swipeVertical( false )  //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
                .enableSwipe(true)   //是否允许翻页，默认是允许翻
                // .pages( 2 , 3 , 4 , 5  )  //把2 , 3 , 4 , 5 过滤掉
                .load();
    }

    /**
     * 翻页回调
     * @param page
     * @param pageCount
     */
    @Override
    public void onPageChanged(int page, int pageCount) {
        Toast.makeText(PdfShowActivity.this , "第" + page + "页，总共有 " + pageCount + "页", Toast.LENGTH_SHORT).show();
    }

    /**
     * 加载完成回调
     * @param nbPages  总共的页数
     */
    @Override
    public void loadComplete(int nbPages) {
        Toast.makeText(PdfShowActivity.this , reportName + " 加载完成，" + "总共有 " + nbPages + "页" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
        // Toast.makeText( MainActivity.this ,  "pageWidth= " + pageWidth + "
        // pageHeight= " + pageHeight + " displayedPage="  + displayedPage , Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 1:
                    displayFromFile((File) msg.obj);
                    break;
                default:
                    break;
            }

        }
    };

    /**
     * 获取详细数据
     */
    private void requestPDF(final String fileName){
        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    String URL = url + "/" + fileName;
                    File file = FileRequestUtils.downFile(URL, fileName, "/share"); //下载pdf
                    Message message = new Message();
                    message.what = 1;
                    message.obj = file;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
    }
}


//    private void displayFromAssets(String assetFileName ) {
//        pdfView.fromAsset(assetFileName)   //设置pdf文件地址
//                .defaultPage(6)         //设置默认显示第1页
//                .onPageChange(this)     //设置翻页监听
//                .onLoad(this)           //设置加载监听
//                .onDraw(this)            //绘图监听
//                .showMinimap(false)     //pdf放大的时候，是否在屏幕的右上角生成小地图
//                .swipeVertical( false )  //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
//                .enableSwipe(true)   //是否允许翻页，默认是允许翻页
//                // .pages( 2 , 3 , 4 , 5  )  //把2 , 3 , 4 , 5 过滤掉
//                .load();
//    }


//从assets目录读取pdf
//        displayFromAssets("bao.pdf");

