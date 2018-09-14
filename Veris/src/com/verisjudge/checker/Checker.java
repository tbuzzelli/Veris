package com.verisjudge.checker;
import com.verisjudge.utils.FastScanner;

public abstract class Checker {
    public abstract CheckerVerdict check(FastScanner input, FastScanner pScanner, FastScanner ansScanner);
}
