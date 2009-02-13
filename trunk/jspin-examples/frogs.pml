	/*
  Frogs puzzle:
  	Seven stones
	Three male frogs at right facing left
	Three female frogs at left facing right
	F-> F-> F-> [EMPTY] <-M <-M <-M

  Frogs can move in the direction it is facing to an empty stone:
	That is adjacent
	That is reached by jumping over a frog on an adjacent stone

  Is there a sequence of moves that will exchange the positions
	of the male and female frogs?
  Solution: try to Verify/Safety []!success; 
    when it fails the trail gives the set of moves.
  Local variables ":init:" and "at" can be excluded.
*/

#define STONES 7

/* Verify acceptance of []!success */
#define success (\
	(stones[0]==female) && \
	(stones[1]==female) && \
	(stones[2]==female) && \
	(stones[4]==male)   && \
	(stones[5]==male)   && \
	(stones[6]==male)      \
	)

mtype = { none, male, female }
mtype stones[STONES];

proctype mF(byte at) {
end:do
	:: 	atomic {
			(at < STONES-1) && 
			(stones[at+1] == none) -> 
			stones[at] = none; 
			stones[at+1] = male;
			at = at + 1;
		}
	:: atomic {
			(at < STONES-2) && 
	   		(stones[at+1] != none) && 
			(stones[at+2] == none) -> 
			stones[at] = none; 
			stones[at+2] = male;
			at = at + 2;
		}
	od
}

proctype fF(byte at) {
end:do
	:: atomic {
			(at > 0) && 
			(stones[at-1] == none) -> 
			stones[at] = none; 
			stones[at-1] = female;
			at = at - 1;
		}
	:: atomic {
			(at > 1) && 
	   		(stones[at-1] != none) && 
			(stones[at-2] == none) -> 
			stones[at] = none; 
			stones[at-2] = female;
			at = at - 2;
		}
	od
}

init {
	atomic {
		stones[STONES/2] = none;
		byte I = 0;
        do
        :: I == STONES/2 -> break;
   		:: else -> 
             stones[I] = male;
             run mF(I);
    		 stones[STONES-I-1] = female;
			 run fF(STONES-I-1);
             I++
        od
	}
}
