/* Mutual exclusion with a semaphore, 
	implemented by channels. 
*/

mtype { request, release };
chan S = [1] of { mtype };
#define notmutex (p@csp && q@csq)

active proctype p() {	
	do ::	S?request;
csp:		S!release;
	od 
}

active proctype q() {	
	do ::	S?request;
csq:		S!release;
	od 
}

active proctype sem() {
	do ::	S!request; 
   			S?release;
	od
}

