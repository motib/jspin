/* Copyright 2005-6 by Mordechai (Moti) Ben-Ari. See copyright.txt */
/*
 * Definine a state of the state transition diagram
*/

package com.spinroot.spinSpider;

class State {
    // Static variables
    // The number of processes and variables is
    //   the same for all *spd* lines printed out
    static int numProcs;
    static int numVars;

    // Variables for each state
    int[] s;        // pc_value for each process
    String[] vname;    // Name for each variable
    String[] v;        // Value for each variable
    boolean inTrail;   // Is this state in the trail?

    public State(int[] st, String[] vn, String[] vr) {
        s = st;
        vname = vn;
        v = vr;
        inTrail = false;
    }

    public String toString() {
        String str = "(";
        for (int proc = 0; proc < numProcs; proc++)
            str = str + ((proc == 0) ? "" : ",") + s[proc];
        for (int var = 0; var < numVars; var++)
            str = str + "," + vname[var] + "=" + v[var];
        return str + ")" + (inTrail ? " *" : "");
    }
}
