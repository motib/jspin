                   Examples for Erigone

Example              File             Mode     Result

Concurrent counting  count        -s -ll80 -ls40 -lt80
                                           assert false
Finite automaton     fa           -s       assert false
Satisfiability       sat          -s       success
Second attempt       second       -s       assert false
Third attempt        third        -s       invalid end state
Dekker's algorithm   dekker       -s       success
Weak semaphore       sem          -s       success
Second attempt       second-ltl   -s -t    never claim terminated
Second attempt with
  remote reference   second-rr    -s -t    never claim terminated
Fourth attempt       fouth-live   -a -t    acceptance cycle
Fourth attempt       fouth-live   -f -t    acceptance cycle
Dekker's algorithm   dekker-live  -a -t    acceptance cycle
Dekker's algorithm   dekker-live  -f -t    success
Fairness             fair1        -a -t    acceptance cycle
Fairness             fair1        -f -t    success
Fairness             fair2        -a -t    acceptance cycle
Fairness             fair2        -f -t    acceptance cycle
Channels: FIFO       ch1          -r       1,2,3/4,5,6/7,8,9/10,11,12
Channels: Sorted     ch2          -r       4,5,6/7,8,8/7,8,9/1,2,3/20,21,22
Channels: Random     ch3          -r       5,6/10,11/8
