/* Mutual exclusion with a busy-wait semaphore. */
byte sem = 1;

active proctype P1 () {	
	do  ::  atomic { sem > 0; sem-- }
		    sem++
	od
}

active proctype P2 () {	
	do  ::  atomic { sem > 0; sem-- }
		    sem++
	od
}

active proctype P3 () {	
	do  ::  atomic { sem > 0; sem-- }
		    sem++
	od
}
