/* Mutual exclusion with a semaphore, implemented by channels. */

#define K 2  /* Number of processes in critical section */
mtype { request, release };
chan S = [0] of { mtype };
byte critical = 0;

active[4] proctype P () {	
	do
	::  S?request;
		critical++;
        assert(critical <= K);
        critical--;
		S!release
	od 
}

active proctype sem() {
	byte count = K;
	do
	::  (count >= 1) -> 
			S!request; 
			count--
	::  S?release; 
		count++;
	od
}

