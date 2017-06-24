package com.treecio.meetpoint.algorithm;

import java.util.ArrayList;
import java.util.List;

public class CantProcessException extends Exception {

    List<String> problems;

    public CantProcessException(List<String> problems) {
        this.problems = problems;
    }

    public List<String> getProblems() {
        return problems;
    }
}
