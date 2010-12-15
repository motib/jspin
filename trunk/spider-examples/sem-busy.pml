/* Mutual exclusion with a busy-wait semaphore. */

byte sem = 1;

active proctype p() {	
	do :: 	atomic { sem > 0; sem--; }
cs:			sem++;
	od
}

active proctype q() {	
	do :: 	atomic { sem > 0; sem--; }
cs:			sem++;
	od
}

active proctype r() {	
	do :: 	atomic { sem > 0; sem--; }
cs:			sem++;
	od
}

ltl { !<>(p@cs && q@cs && r@cs) }
