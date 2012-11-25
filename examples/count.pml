byte	n = 0;
byte	finished = 0;

active proctype P() {
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

active proctype Q() {
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

active proctype Finish() {
	finished == 2; 
    printf("The result is %d\n", n);
	assert (n > 2);
}
