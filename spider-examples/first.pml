/* First attempt */
byte    turn = 1;

active proctype p() {
    do :: turn == 1;
		  turn = 2;
    od
}

active proctype q() {
    do :: turn == 2;
          turn = 1;
    od
}

