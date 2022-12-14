/* Copyright 2005-6 by Mordechai (Moti) Ben-Ari. See copyright.txt */
/* Transitions obtained by processing New/Old/Up/Down in -DCHECK file
 */

package com.spinroot.spinSpider;

class Trail {
    int id;

    public Trail(int i) {
        id = i;
    }

    public String toString() {
        return "id " + id;
    }
}
