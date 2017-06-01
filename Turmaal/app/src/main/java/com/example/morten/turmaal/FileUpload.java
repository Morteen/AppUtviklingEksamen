package com.example.morten.turmaal;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by morten on 01.06.2017.
 */

public class FileUpload extends AsyncTask<Void,Void,Void> {

    public FileUpload(Context context,File file) {
        this.context = context;
        this.file=file;
    }

    /**
         * Upload a file to a FTP server. A FTP URL is generated with the
         * following syntax:
         * ftp://user:password@host:port/filePath;type=i.
         *
         * @param ftpServer , FTP server address (optional port ':portNumber').
         * @param user , Optional user name to login.
         * @param password , Optional password for user.
         * @param fileName , Destination file name on FTP server (with optional
         *            preceding relative path, e.g. "myDir/myFile.txt").
         * @param source , Source file to upload.
         * @throws MalformedURLException, IOException on error.
         *
         *
         */
        Context context;
    File file;





    public static void upload( String ftpServer, String user, String password,String fileName, File source ) throws MalformedURLException,
            IOException {
        int sjekk=0;
        if (ftpServer != null &&fileName!= null&&source != null){
            StringBuffer sb = new StringBuffer("ftp://");
            // check for authentication else assume its anonymous access.
            if ( user != null && password != null)
            {
                sb.append(user);
                sb.append(':');
                sb.append(password);
                sb.append('@');
            }
            sb.append(ftpServer);
            sb.append('/');
            sb.append(fileName);
         /*
          * type ==&gt; a=ASCII mode, i=image (binary) mode, d= file directory
          * listing
          */
            sb.append(";type=i");

            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                URL url = new URL(sb.toString());
                URLConnection urlc = url.openConnection();

                bos = new BufferedOutputStream(urlc.getOutputStream());
                bis = new BufferedInputStream(new FileInputStream(source));

                int i;
                // read byte by byte until end of stream
                while ((i = bis.read()) != -1) {
                    bos.write(i);
                    sjekk++;
                }
            } finally {
                if (bis != null)
                    try {
                        bis.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                if (bos != null)
                    try {
                        bos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
            }
        }else{


        }

    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            upload("itfag.usn.no:22", "210144","#071362Morten","/home/210144/public_html/images", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}




