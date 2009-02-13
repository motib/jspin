/* First attempt */
byte    turn = 1;
byte critical = 0; 

active proctype p() {
    do 
	:: turn == 1;
       critical++;
       assert (critical == 1);
       critical--;
	   turn = 2;
    od
}

active proctype q() {
    do 
    :: turn == 2;
       critical++;
       assert (critical == 1);
       critical--;
       turn = 1;
    od
}
