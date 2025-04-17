package fr.eirb.lemondedenemo.periscope.api.utils;

import java.io.File;

public interface Fish {
  String getName();

  double getLength();

  void setLength(int length);

  double getHeight();

  void setHeight(int height);

  File getImageFile();
}
