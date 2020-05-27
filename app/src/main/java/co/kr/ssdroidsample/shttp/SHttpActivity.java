package co.kr.ssdroidsample.shttp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import co.kr.ssdroidlib.http.SHttpRequest;
import co.kr.ssdroidsample.R;

public class SHttpActivity extends AppCompatActivity {

    final int Request_Get = 1;
    final int Request_FileDownload = 2;
    final int Request_FileUpload = 3;
    TextView mText;

    SHttpRequest mRequest = new SHttpRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shttp);



        mText = findViewById(R.id.txtResult);
        Button Request = findViewById(R.id.btnRequest);
        Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRequest.RequestHttp(Request_Get,"https://www.pushnara.co.kr",null,null);
            }
        });

        Button Download = findViewById(R.id.btnDownload);
        Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRequest.DownloadFile(Request_FileDownload,"https://www.pushnara.co.kr",null,null,null);
            }
        });

        Button Upload = findViewById(R.id.btnUpload);
        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRequest.UploadFile(Request_FileUpload,"https://www.pushnara.co.kr","",null,null);
            }
        });
        mRequest.SetDataListener(new SHttpRequest.IDataListener() {
            @Override
            public void OnCompleted(long ID, long Code, byte[] Data, Map<String, List<String>> Header, String sError) {
                if(ID == Request_Get)
                {
                    if(Code == 200)
                    {
                        String sData = new String(Data);
                        mText.setText(sData);
                    }
                    else
                        mText.setText(sError);
                }
                else if(ID == Request_FileDownload)
                {
                }
                else if(ID == Request_FileUpload)
                {
                }
            }
        });
    }
}
