
 /* 
  Increment a variable in two processes.
  Check that final value can be two!!
*/
#define TIMES 10
byte	n = 0;
byte	finished = 0;

active [2] proctype P() {
	byte 	i = 1;
	byte	temp;
	do :: ( i > TIMES ) -> break  
	   :: else ->
		temp = n;
		n = temp + 1;
		i++
	od;
	finished++;  /* Process terminates */
}

active proctype Finish() {
	finished == 2;  /* Wait for termination */ 
	printf("n = %d\n", n);
	assert (n > 2);
 /* Assert can't be 2 */
}
