package com.qualitypaper.fluentfusion.service.vocabulary.interfaces;

import java.io.IOException;

public interface ImageInterface {


  String sendUnSplashHttpRequest(String url) throws IOException, InterruptedException;
}
