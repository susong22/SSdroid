package co.kr.ssdroidlib.socket;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SSocket {
    int ConnectTimeout = 60*1000; //1분
    int ReadTimeout = 120*1000; //2분

    private Socket mSocket;
    public boolean Connect(String sDomain,int nPort,int ConnectTimeout,int ReadTimeout)
    {
        this.ConnectTimeout = ConnectTimeout;
        this.ReadTimeout = ReadTimeout;
        return Connect(sDomain,nPort);
    }

    public boolean Connect(String sDomain,int nPort)
    {
        try {
            SocketAddress socketAddress = new InetSocketAddress(sDomain, nPort);
            mSocket = new Socket();
            mSocket.setSoTimeout(ConnectTimeout); /* InputStream에서 데이터읽을때의 timeout */
            mSocket.connect(socketAddress, ReadTimeout); /* socket연결 자체에대한 timeout */
        } catch (Exception e) {
            Log.e("JavaSong","Connect " + e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public OutputStream GetOutputStream() {
        OutputStream out = null;
        try {
            out = mSocket.getOutputStream();
        }
        catch (IOException e)
        {
            Log.e("JavaSong","GetOutputStream " + e.getLocalizedMessage());
        }
        return out;
    }

    public boolean SendData(OutputStream out,byte[] sData)
    {
        try
        {
            out.write(sData);
            /*
            OutputStream out = mSocket.getOutputStream();
            byte[] btData = sData.getBytes();
            byte[] btHeader = new byte[11];
            btHeader[0] = (byte)'R';
            String sHeader = Long.toString(btData.length);
            byte[] btTempHeader = sHeader.getBytes();
            System.arraycopy(btTempHeader, 0, btHeader, 1, btTempHeader.length);
            out.write(btHeader);
            out.write(btData);
            out.flush();
            */
        }
        catch (IOException e)
        {
            Log.e("JavaSong","SendData " + e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public InputStream GetInputStream() {
        InputStream in = null;
        try
        {
            in = mSocket.getInputStream();
        }
        catch (IOException e)
        {
            Log.e("JavaSong","GetInputStream " + e.getLocalizedMessage());
        }
        return in;
    }

    public ByteArrayOutputStream RcvdData(InputStream input)
    {
        ByteArrayOutputStream ouput = null;
        try {
            ouput = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            for (int nChunk = input.read(buf); nChunk!=-1; nChunk = input.read(buf))
            {
                ouput.write(buf, 0, nChunk);
            }
        }
        catch (IOException e)
        {
            Log.e("JavaSong","RcvdData " + e.getLocalizedMessage());
        }
        return ouput;
    }

    public void Disconnect()
    {
        if(mSocket != null)
        {
            try
            {
                mSocket.close();
            } catch (IOException e) {
                Log.e("JavaSong","Disconnect " + e.getLocalizedMessage());
            }
        }

    }
}
