package com.kaadas.lock.publiclibrary.ota.ti;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by ole on 10/11/2017.
 */

public class TIOADEoadImageReader {

  private final String TAG = TIOADEoadImageReader.class.getSimpleName();

  private byte[] rawImageData;
  public TIOADEoadHeader imageHeader;
  private ArrayList <TIOADEoadHeader.TIOADEoadSegmentInformation> imageSegments;
  private Context context;

  public TIOADEoadImageReader(Uri filename, Context context) {
    this.imageSegments = new ArrayList<>();
    this.context = context;
    this.TIOADToadLoadImageFromDevice(filename);
  }

  public TIOADEoadImageReader(String filename, Context context) {

    this.imageSegments = new ArrayList<>();
    this.context = context;
    if (filename.indexOf("/binFile/")>=0){  //如果文件路径包含有binFile   那么认为此文件时本地下载的   不是assest里面的文件
      this.TIOADToadLoadImageFromDownLock(filename);
    }else {
      this.TIOADToadLoadImage(filename);
    }

  }

  public void TIOADToadLoadImageFromDownLock(String filename) {
    try {
      InputStream inputStream = new FileInputStream(new File(filename));
      rawImageData = new byte[inputStream.available()];
      int len = inputStream.read(rawImageData);
      Log.d(TAG,"Read " + len + " bytes from file");
      this.imageHeader = new TIOADEoadHeader(rawImageData);
      this.imageHeader.validateImage();
    }
    catch (IOException e) {
      Log.d(TAG,"Could not read input  file   "  + e.getMessage() +"   " + e.getLocalizedMessage());
    }
  }

  public void TIOADToadLoadImage(String assetFilename) {
    AssetManager aMan = this.context.getAssets();
    try {
      InputStream inputStream = aMan.open(assetFilename);
      rawImageData = new byte[inputStream.available()];
      int len = inputStream.read(rawImageData);
      Log.d(TAG,"Read " + len + " bytes from asset file");
      this.imageHeader = new TIOADEoadHeader(rawImageData);
      this.imageHeader.validateImage();
    }
    catch (IOException e) {
      Log.d(TAG,"Could not read input file" + e.getMessage() +"   " + e.getLocalizedMessage());
    }
  }
  public void TIOADToadLoadImageFromDevice(Uri filename) {
    try {
      InputStream inputStream = context.getContentResolver().openInputStream(filename);
      rawImageData = new byte[inputStream.available()];
      int len = inputStream.read(rawImageData);
      Log.d(TAG,"Read " + len + " bytes from file");
      this.imageHeader = new TIOADEoadHeader(rawImageData);
      this.imageHeader.validateImage();
    }
    catch (IOException e) {
      Log.d(TAG,"Could not read input  file   "  + e.getMessage() +"   " + e.getLocalizedMessage());
    }
  }

  public byte[] getRawImageData() {
    return rawImageData;
  }

  public byte[] getHeaderForImageNotify() {
    byte[] imageNotifyHeader = new byte[22];
    int position = 0;
    //0
    System.arraycopy(imageHeader.TIOADEoadImageIdentificationValue,0,imageNotifyHeader,position,imageHeader.TIOADEoadImageIdentificationValue.length);
    position += imageHeader.TIOADEoadImageIdentificationValue.length;
    //7
    imageNotifyHeader[position++] = imageHeader.TIOADEoadBIMVersion;
    //8
    imageNotifyHeader[position++] = imageHeader.TIOADEoadImageHeaderVersion;
    //9
    System.arraycopy(imageHeader.TIOADEoadImageInformation,0,imageNotifyHeader,position,imageHeader.TIOADEoadImageInformation.length);
    position += imageHeader.TIOADEoadImageInformation.length;
    //13
    for (int ii = 0; ii < 4; ii++) {
      imageNotifyHeader[position++] = TIOADEoadDefinitions.GET_BYTE_FROM_UINT32(imageHeader.TIOADEoadImageLength, ii);
    }
    //17
    System.arraycopy(imageHeader.TIOADEoadImageSoftwareVersion,0,imageNotifyHeader,position,imageHeader.TIOADEoadImageSoftwareVersion.length);
    position += imageHeader.TIOADEoadImageSoftwareVersion.length;
    //21
    return imageNotifyHeader;
  }


}

