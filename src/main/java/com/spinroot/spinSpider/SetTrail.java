/* Copyright 2006 by Mordechai (Moti) Ben-Ari. See copyright.txt */
/*
 * Use the trail file to give attributes to states and transitions
*/

package com.spinroot.spinSpider;

class SetTrail {
    SpinSpider spd;

    SetTrail(SpinSpider s) {
        spd = s;
    }

    // Traverse the state diagram according to the trail file
    void setTrail() {
        int i = 0;
        if (spd.states.size() == 0) return;

        // Start from first state
        spd.states.get(i).inTrail = true;
        // For each line in the trail, find a statement with matching id
        for (Trail tr : spd.trails) {
            Statement st = null;
            for (Statement stmt : spd.statements)
                if (stmt.id == tr.id) {
                    st = stmt;
                    break;
                }
            // Set trail flag for the state obtained by taking
            //   this transition in the trail
            i = findNewState(i, st);
            spd.states.get(i).inTrail = true;
        }
    }

    // Starting from state "index", find where the state where
    //   statement "stmt" goes to
    private int findNewState(int index, Statement stmt) {
        boolean ok;
        State currentState = spd.states.get(index);
        for (State s : spd.states) {
            ok = true;
            // For each process, the location is the same,
            //   except for the process executed by this statement,
            //   in which case the new state is reached by the statement
            for (int i = 0; i < s.s.length; i++) {
                if (i == stmt.process) ok = ok && (s.s[i] == stmt.targetState);
                if (i != stmt.process) ok = ok && (s.s[i] == currentState.s[i]);
            }

            // Flag the transition
            if (ok) {
                boolean foundTransition = false;
                for (Transition tr : spd.transitions)
                    if ((tr.head == index) && (tr.tail == spd.states.indexOf(s))) {
                        tr.inTrail = true;
                        foundTransition = true;
                    }
                if (foundTransition)
                    return spd.states.indexOf(s);
                else continue;
            }
        }
        return 0;
    }
}
