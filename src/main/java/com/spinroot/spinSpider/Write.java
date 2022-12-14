/* Copyright 2005-7 by Mordechai (Moti) Ben-Ari. See copyright.txt */
/*
 * Write the output in different formats
*/

package com.spinroot.spinSpider;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Write {
    SpinSpider spd;

    Write(SpinSpider s, String f) {
        spd = s;
    }

    // Write the never claim from the number of processes and
    //   the names of the variables
    boolean writeNeverClaim(int numProcs, String[] vars) {
        PrintWriter neverWriter = null;
        spd.progress("Writing never claim");
        if (vars.length == 0) {
            spd.progress("No variables for never claim");
            return false;
        }
        try {
            neverWriter = new PrintWriter(new FileWriter(spd.fileName + ".nvr"));
        } catch (IOException e) {
            spd.fileError(".nvr");
        }

        neverWriter.print("never {\n\tdo\n\t:: printf(\"*spd* " + numProcs);
        for (int i = 0; i < numProcs; i++)
            neverWriter.print(" %d");
        neverWriter.print(" " + vars.length);
        for (int i = 0; i < vars.length; i++)
            neverWriter.print(" " + vars[i] + " %d");
        neverWriter.print("\\n\",\n\t\t");
        for (int i = 0; i < numProcs; i++)
            neverWriter.print("pc_value(" + i + "), ");
        for (int i = 0; i < vars.length; i++)
            neverWriter.print((i == 0 ? "" : ", ") + vars[i]);
        neverWriter.println(")\n\tod\n}");
        neverWriter.close();
        return true;
    }

    // Write the graph in dot format - nodes and edges
    // trailCode = 0 (no trail), 1 (emphasize trail), 2 (just trail)
    void writeDotGraph() {
        PrintWriter graphWriter = null;
        String fn = spd.fileName + Config.names[spd.trailCode];
        String label;       // Building the label in this string
        int numStates = 0;  // For printing a node number
        String[] dotPrologue =
                spd.dotSize == 0 ? Config.smallDotPrologue : Config.largeDotPrologue;
        String dotTrail =
                spd.trailStyle == 0 ? Config.dotTrailColor : Config.dotTrailBold;

        spd.progress("Writing dot file");
        try {
            graphWriter = new PrintWriter(new FileWriter(fn + ".dot"));
        } catch (IOException e) {
            spd.fileError(".dot");
        }

        // Write dot prologue
        graphWriter.println("digraph \"" + fn + "\"" + " {");
        for (int i = 0; i < dotPrologue.length; i++)
            graphWriter.println(dotPrologue[i]);

        // Write states
        for (State st : spd.states) {
            label = (numStates++) + " [label=\"";
            for (int proc = 0; proc < State.numProcs; proc++)
                for (Statement t : spd.statements)
                    if ((t.process == proc) &&
                            (t.sourceState == st.s[proc]))
                        label = label + t.line + ". " + t.source + "\\n";
            for (int var = 0; var < State.numVars; var++)
                label = label + " " + st.v[var];
            graphWriter.print(label + "\"");
            if ((spd.trailCode == 1) && st.inTrail)
                graphWriter.print(dotTrail);
            if ((spd.trailCode == 2) && !st.inTrail)
                graphWriter.print(" style = invis ");
            graphWriter.println("];");
        }

        // Write transitions
        for (Transition t : spd.transitions) {
            graphWriter.print(t.head + " -> " + t.tail);
            if ((spd.trailCode == 1) && t.inTrail)
                graphWriter.print(" [" + dotTrail + "]");
            if ((spd.trailCode == 2) && !t.inTrail)
                graphWriter.print(" [ style = invis ]");
            graphWriter.println(";");
        }
        graphWriter.println("}");
        graphWriter.close();
        if ((spd.trailCode != 3) && !spd.format.equals("dot"))
            spd.runProcess(spd.properties.getProperty("DOT") +
                    " -T" + spd.format +
                    " -o " + fn + "." + spd.format + " " +
                    fn + ".dot", null);
    }

    // Write the graph in fsm format
    // This format is used by tools developed at the 
    //    Eindhoven University of Technology
    void writeFSMGraph() {
        int offset;
        PrintWriter graphWriter = null;
        spd.progress("Writing fsm file");
        try {
            graphWriter = new PrintWriter(new FileWriter(spd.fileName + ".fsm"));
        } catch (IOException e) {
            spd.fileError(".fsm");
        }

        // If the program terminates there is an end statement
        // that is not a "source" in the -d file
        // Add dummy statements at the end of the "statements" data structure
        // spp1 is the original statementsPerProcess
        // spp2 is statementsPerProcess with added dummy statements
        int[] spp1 = new int[spd.statementsPerProcess.size()];
        int[] spp2 = new int[spd.statementsPerProcess.size()];
        for (int i = 0; i < spp1.length; i++) {
            spp1[i] = spd.statementsPerProcess.get(i).intValue();
            spp2[i] = spp1[i];
        }
        boolean[] endStates = new boolean[State.numProcs];
        for (int i = 0; i < spd.statements.size(); i++) {
            Statement st = spd.statements.get(i);
            if ((st.targetState == 0) && (!endStates[st.process])) {
                spd.statements.add(new Statement(st.process, 0, 0, 0, false, 0, ""));
                spp2[st.process]++;
                endStates[st.process] = true;
            }
        }

        // Compute fan in and fan out of each node
        int[] fanIn = new int[spd.states.size()];
        int[] fanOut = new int[spd.states.size()];
        for (int i = 0; i < spd.transitions.size(); i++) {
            fanIn[spd.transitions.get(i).tail]++;
            fanOut[spd.transitions.get(i).head]++;
        }
        // Compute the maximum fan in and fan out in the diagram
        int maxFanIn = 0, maxFanOut = 0;
        for (int i = 0; i < spd.states.size(); i++) {
            if (fanIn[i] > maxFanIn) maxFanIn = fanIn[i];
            if (fanOut[i] > maxFanOut) maxFanOut = fanOut[i];
        }
        maxFanIn++;
        maxFanOut++;

        // Processes: 
        //   "p" + number of process + "(" + statements per process + ")"
        //       + "String" + list of statements
        offset = 0;
        for (int proc = 0; proc < State.numProcs; proc++) {
            graphWriter.print("p" + (proc + 1) +
                    "(" + spp2[proc] + ") String");
            for (int stmt = 0; stmt < spp1[proc]; stmt++) {
                Statement stm = spd.statements.get(offset + stmt);
                graphWriter.print(" \"" + stm.line + "," + stm.source + "\"");
            }
            offset = offset + spp1[proc];
            if (spp2[proc] > spp1[proc])
                graphWriter.print(" \" end state \"");
            graphWriter.println();
        }

        // Variables:
        //   name of variable + "(" + number of values + ")"
        //       + "Nat" + list of values
        for (int var = 0; var < State.numVars; var++) {
            int max = 0;
            for (State st : spd.states) {
                int val = Integer.parseInt(st.v[var]);
                if (val > max) max = val;
            }
            graphWriter.print(spd.states.get(0).vname[var] + "(" + (max + 1) + ") Nat");
            for (int i = 0; i <= max; i++)
                graphWriter.print(" \"" + i + "\"");
            graphWriter.println();
        }

        // Fan in, fan out, node nr
        graphWriter.print("fan_in(" + maxFanIn + ") Nat");
        for (int i = 0; i < maxFanIn; i++)
            graphWriter.print(" \"" + i + "\"");
        graphWriter.println();
        graphWriter.print("fan_out(" + maxFanOut + ") Nat");
        for (int i = 0; i < maxFanOut; i++)
            graphWriter.print(" \"" + i + "\"");
        graphWriter.println();
        graphWriter.println("node_nr(0)");
        graphWriter.println("---");

        // States:
        //   Process location counters (relative to lists above!)
        //   Values of variables
        //   Fan-in and fan-out
        int stateCount = 0;
        for (State st : spd.states) {
            offset = 0;
            for (int proc = 0; proc < State.numProcs; proc++) {
                boolean found = false;
                for (int i = 0; i < spp1[proc]; i++)
                    if (spd.statements.get(offset + i).sourceState == st.s[proc]) {
                        graphWriter.print(i + " ");
                        found = true;
                        break;
                    }
                if (!found) {
                    graphWriter.print((spp2[proc] - 1) + " ");
                }
                offset = offset + spp1[proc];
            }
            for (int var = 0; var < State.numVars; var++)
                graphWriter.print(st.v[var] + " ");
            graphWriter.print(fanIn[stateCount] + " " +
                    fanOut[stateCount] + " " + (stateCount + 1));
            stateCount++;
            graphWriter.println();
        }
        graphWriter.println("---");

        // Transitions
        for (Transition t : spd.transitions)
            graphWriter.println((t.head + 1) + " " + (t.tail + 1));
        graphWriter.close();
    }

    // Write debug file using toString defined in each class
    void writeDebug() {
        PrintWriter debugWriter = null;
        spd.progress("Writing debug file");
        try {
            debugWriter = new PrintWriter(
                    new FileWriter(spd.fileName + ".dbg"));
        } catch (IOException e) {
            spd.fileError(".dbg");
        }

        debugWriter.println("States");
        int n = 0;
        for (State s : spd.states)
            debugWriter.println((n++) + ". " + s);

        debugWriter.println("\nTransitions");
        for (Transition t : spd.transitions)
            debugWriter.println(t);

        debugWriter.println("\nStatements from -d file");
        for (Statement st : spd.statements)
            debugWriter.println(st);

        if (spd.trailCode > 0) {
            debugWriter.println("\nTrail");
            for (Trail tr : spd.trails)
                debugWriter.println(tr);
        }
        debugWriter.close();
    }
}
