/* Copyright (C) 2006-10 M. Ben-Ari. See copyright.txt */
/* 
   Barz's implementation of general semaphores
      by binary semaphores
*/

#define NPROCS 3
#define K      2
#define PID
#include "critical.h"

byte gate = ((K == 0) -> 0 : 1);
int count = K;

bool test[NPROCS] = false;
#define notInTest ((test[0]==false) && (test[1]==false) && (test[2]==false))

/*   Verify Safety with the following properties */

ltl p0 { [](gate <= 1) } /* gate is a binary semaphore */
ltl p1 { []((count == 0) -> (gate == 0)) }
ltl p2 { [](((gate == 0) && notInTest) -> (count == 0)) }

active [NPROCS] proctype P () {	
	do :: 
		/* Wait */
		atomic { gate > 0; gate--; test[_pid] = true; }
		assert(gate == 0);
		d_step {
			count--;
			if
			:: count > 0 -> gate++;
			:: else
			fi;
			test[_pid] = false;
		}

    	critical_section();

		/* Signal */
		d_step {
			count++;
			if
			:: count == 1 -> gate++;
			:: else
			fi;
		}
	od
}

