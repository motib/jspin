byte	n = 0;
byte	finished = 0;

proctype P() {
	byte 	i = 1;
	byte	temp;
	do :: ( i > 10 ) -> break  
	   :: else ->
		temp = n;
		n = temp + 1;
		i++
	od;
	finished++;
}

init {
  atomic {
    run P();
    run P()
  }
	finished == 2; 
	assert (n > 2);
}
