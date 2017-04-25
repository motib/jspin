/* Copyright 2005-6 by Mordechai (Moti) Ben-Ari. See copyright.txt */
/*
 * Defining a transition of the state transition diagram
*/

package com.spinroot.spinSpider;

class Transition {
    int head;         // head state
    int tail;         // tail state
    boolean inTrail;  // Is this transition in the trail?

    public Transition(int h, int t) {
        head = h;
        tail = t;
        inTrail = false;
    }

    public String toString() {
        return head + " -> " + tail + (inTrail ? " *" : "");
    }
}
