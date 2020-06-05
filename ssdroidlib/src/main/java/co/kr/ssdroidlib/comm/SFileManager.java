package co.kr.ssdroidlib.comm;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class SFileManager {

	static public boolean DeleteFile(String sFilePath)
	{
		try
		{
			File file = new File(sFilePath); 
			if( file.exists() ){ 
				if(file.delete())
				{ 
					return true; 
				}
			}
		}
		catch(Exception e)
		{
			Log.e("JavaSong","DeleteFile " + e.getLocalizedMessage());
			return false;
		}
		return false;
	}
	
	static public boolean DeleteFolder(String sFilePath)
	{
		try
		{
			File file = new File(sFilePath); 
			if( file.exists() ){ 
				File[] deleteFolderList = file.listFiles();
				for (int j = 0; j < deleteFolderList.length; j++) {
					if(deleteFolderList[j].isDirectory())
						DeleteFolder(deleteFolderList[j].getPath());
					else
						deleteFolderList[j].delete();
				}
				file.delete();
			}
		}
		catch(Exception e)
		{
			Log.e("JavaSong","DeleteFolder " + e.getLocalizedMessage());
			return false;
		}
		
		return true;
	}
	
	static public boolean MakeDir(String sFolder)
	{
		File f = new File(sFolder);
		if(!f.exists())
		{
			if(f.mkdirs() == false)
			{
				return false;
			}
		}
		return true;
	}
	
	static public boolean WriteFile(String FilePath,String sContents)
	{
		File file = new File(FilePath);
        FileWriter writer = null;
        
        try {
            // 기존 파일의 내용에 이어서 쓰려면 true를, 기존 내용을 없애고 새로 쓰려면 false를 지정한다.
            writer = new FileWriter(file, false);
            writer.write(sContents);
            writer.flush();
        } catch(IOException e) {
            e.printStackTrace();
			Log.e("JavaSong","WriteFile " + e.getMessage());
            return false;
        } finally {
            try {
                if(writer != null) writer.close();
            } catch(IOException e) {
                e.printStackTrace();
				Log.e("JavaSong","WriteFile " + e.getMessage());
                return false;
            }
            
        }
        return true;
	}

	static public boolean Refilename(String sPath,String sRefileName)
	{
		boolean success = false;
		try
		{
			String sToFileName = "";
			int index = sPath.lastIndexOf('/');
			if(index != -1)
				sToFileName = sPath.substring(0,index) + "/" + sRefileName;
			
			File file = new File(sPath);
			File tofile = new File(sToFileName);
			success = file.renameTo(tofile);
		}
		catch(Exception e)
		{
			Log.e("JavaSong","Refilename " + e.getLocalizedMessage());
			success = false;
		}
		return success;
	}
}