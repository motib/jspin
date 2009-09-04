/* Mutual exclusion with a busy-wait semaphore. */

byte critical = 0;

active [3] proctype P () {	
	do 
  	:: 
 			sem > 0; sem--
		}
		critical++;
        assert(critical == 1);
        critical--;
		sem++
	od
}
