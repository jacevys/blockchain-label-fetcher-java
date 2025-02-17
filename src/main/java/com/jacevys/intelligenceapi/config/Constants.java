package com.jacevys.intelligenceapi.config;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import com.jacevys.intelligenceapi.common.Cycle;

public class Constants {
    List<String> list = Arrays.asList("test");
    Cycle<String> misttrackApiKey = new Cycle<>(list);
}