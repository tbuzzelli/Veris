package com.verisjudge.checker;
import com.verisjudge.Verdict;
import com.verisjudge.utils.FastScanner;

public abstract class Checker {
    public abstract Verdict check(FastScanner input, FastScanner pScanner, FastScanner ansScanner);
}
