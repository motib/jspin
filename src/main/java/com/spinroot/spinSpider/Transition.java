/* Copyright 2005-6 by Mordechai (Moti) Ben-Ari. See copyright.txt */
/*
 * Defining a transition of the state transition diagram
*/

package com.spinroot.spinSpider;

class Transition {
    final int head;         // head state
    final int tail;         // tail state
    boolean inTrail;  // Is this transition in the trail?

    public Transition(int head, int tail) {
        this.head = head;
        this.tail = tail;
        inTrail = false;
    }

    public String toString() {
        return head + " -> " + tail + (inTrail ? " *" : "");
    }
}
