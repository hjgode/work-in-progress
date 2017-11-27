package com.mobile.app.assist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

    /* return the bitmap for page 1 of a pdf file
     * returns null if error!
    */
public class PDFprint {
    static String TAG="PDFprint";
/*
    File pdfFile = null;
    Bitmap pageImage=null;
    String pageText;
*/
    Context context;

    public PDFprint(Context _context){
        context=_context;
        //init PDFBox class
        PDFBoxResourceLoader.init(_context);
    }

    public boolean stripText(String sFilePDF, String sFileTextOut) {
        boolean bRet = false;
        File pdfFile=new File(sFilePDF);
        if (pdfFile == null || !pdfFile.exists()) {
            updateStatus("Mising PDF File. Stopped");
            return bRet;
        }
        String parsedText = null;
        PDDocument document = null;
        try {
            //document = PDDocument.load(assetManager.open("Hello.pdf"));
            document = PDDocument.load(pdfFile);
            bRet = true;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
            updateStatus("Exception loading PDF File " + e.getMessage());
        }

        try {
            if (document!=null) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                pdfStripper.setStartPage(0);
                pdfStripper.setEndPage(1);
                parsedText=pdfStripper.getText(document);
                //write
                FileWriter fileWriter=new FileWriter(sFileTextOut);
                fileWriter.write(parsedText);
                fileWriter.flush();
                fileWriter.close();
                updateStatus("READY");
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
            updateStatus("Exception processing PDF File " + e.getMessage());
            bRet = false;
        } finally {
            try {
                if (document != null)
                    document.close();
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
                updateStatus("Exception closing PDF File " + e.getMessage());
            }
            //enableButtons();
            return  bRet;
        }
    }

    /**
     * Loads an existing PDF and renders it to a Bitmap
     * returns null if error!
     */
    public Bitmap renderFile(String sFile, float fScale, String sFileBmpOut) {
        //disableButtons();
        File theFile=new File(sFile);
        // Render the page and save it to an image file
        float myScale=fScale; //609 (printer) / 226 (pdf)
        PDDocument document=null;
        Bitmap pageImage=null;
        try {
            updateStatus("Loading PDF File: "+sFile);
            // Load in an already created PDF
            document = PDDocument.load(theFile);
            // Create a renderer for the document
            PDFRenderer renderer = new PDFRenderer(document);

            updateStatus("Rendering PDF File...");
            // Render the image to an RGB Bitmap
            pageImage = renderer.renderImage(0, myScale, Bitmap.Config.RGB_565);
            updateStatus("Rendering PDF File DONE");
/*
			if(pageImage.getWidth() > printerWidthPx) //pageImage=226, printerWidthPx=609
                myScale=(float)(pageImage.getWidth() / printerWidthPx);
			else
                myScale=(float)(printerWidthPx / pageImage.getWidth());
			if (myScale!=1){ //create new scaled image for printer
			    pageImage=renderer.renderImage(0, 1, Bitmap.Config.RGB_565);
            }
*/

            // Save the render result to an image
            File renderFile = new File(sFileBmpOut);
            FileOutputStream fileOut = new FileOutputStream(renderFile);
            pageImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);
            fileOut.flush();
            fileOut.close();

            updateStatus("Successfully rendered image");

            // Optional: display the render result on screen
        } catch(Exception e) {
            e.printStackTrace();
            pageImage=null;
        }
        finally {
            try {
                if (document != null)
                    document.close();
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
                Log.d(TAG,"Exception closing PDF File " + e.getMessage());
            }
            return pageImage;
        }
        //enableButtons();
    }

    void updateStatus(String s){
        Log.d(TAG,s);
    }

    Bitmap getBitmap(String sFile, float fScale) {
        File bitmapfile=null;
        String bitmapFilename="";
        //assign files
        try {
            bitmapfile = File.createTempFile("bitmap", ".bmp", this.context.getCacheDir());
            bitmapFilename=bitmapfile.getPath();
            Log.d(TAG, "using bitmapfile: "+bitmapFilename);
        }catch (IOException ex){
            Log.d(TAG,"Bitmap createTempFile failed: "+ex.getMessage());
            bitmapfile=null;
        }
        /*
        try{
            textfile=File.createTempFile("text",".txt",this.context.getCacheDir());
            textFilename=textfile.getPath();
            Log.d(TAG, "using textfile: "+textFilename);
        }catch (IOException ex){
            Log.d(TAG,"Text createTempFile failed: "+ex.getMessage());
            textfile=null;
        }*/
        //test bitmap file is ready
        if (bitmapfile == null) {
            //return error
            return null;
        }

        //convert file to bitmap
        try {
            PDFprint pdFprint = new PDFprint(this.context);
            Bitmap bmp = pdFprint.renderFile(sFile, fScale, bitmapFilename);
            if (bmp != null) {
                ; //Convert OK
                return bmp;
            } else {
                //return ERROR
                return null;
            }
        } catch (Exception ex) {

        }
        return null;
    }
}
