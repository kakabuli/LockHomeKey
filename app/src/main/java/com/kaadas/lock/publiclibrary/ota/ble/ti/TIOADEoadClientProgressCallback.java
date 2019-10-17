package com.kaadas.lock.publiclibrary.ota.ble.ti;

/**
 * Created by ole on 01/12/2017.
 */

public interface TIOADEoadClientProgressCallback {

  void oadProgressUpdate(float percent, int currentBlock);
  void oadStatusUpdate(TIOADEoadDefinitions.oadStatusEnumeration status);

}
