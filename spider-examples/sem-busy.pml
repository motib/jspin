/* Mutual exclusion with a busy-wait semaphore. */

byte sem = 1;
#define notmutex (p@cs && q@cs && r@cs)

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

